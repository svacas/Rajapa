/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
/*
 *
 */
package org.raml.v2.impl.commons.phase;

import static org.raml.v2.impl.commons.phase.ResourceTypesTraitsMerger.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.v2.impl.commons.grammar.BaseRamlGrammar;
import org.raml.v2.impl.commons.nodes.BaseResourceTypeRefNode;
import org.raml.v2.impl.commons.nodes.BaseTraitRefNode;
import org.raml.v2.impl.commons.nodes.MethodNode;
import org.raml.v2.impl.commons.nodes.ResourceNode;
import org.raml.v2.impl.commons.nodes.ResourceTypeNode;
import org.raml.v2.impl.commons.nodes.StringTemplateNode;
import org.raml.v2.impl.commons.nodes.TraitNode;
import org.raml.v2.nodes.ExecutionContext;
import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.ParametrizedReferenceNode;
import org.raml.v2.nodes.ReferenceNode;
import org.raml.v2.nodes.snakeyaml.SYBaseRamlNode;
import org.raml.v2.nodes.snakeyaml.SYNullNode;
import org.raml.v2.nodes.snakeyaml.SYObjectNode;
import org.raml.v2.internal.framework.phase.GrammarPhase;
import org.raml.v2.internal.framework.phase.Transformer;
import org.raml.v2.utils.NodeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypesTraitsTransformer implements Transformer
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Set<ResourceNode> mergedResources = new HashSet<>();
    private BaseRamlGrammar ramlGrammar;

    public ResourceTypesTraitsTransformer(BaseRamlGrammar ramlGrammar)
    {
        this.ramlGrammar = ramlGrammar;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof BaseTraitRefNode ||
               node instanceof BaseResourceTypeRefNode;
    }

    @Override
    public Node transform(Node node)
    {
        ResourceNode resourceNode = node.findAncestorWith(ResourceNode.class);
        if (mergedResources.contains(resourceNode))
        {
            return node;
        }

        // apply method and resource traits if defined
        checkTraits(resourceNode, resourceNode);

        // apply resource type if defined
        ReferenceNode resourceTypeReference = findResourceTypeReference(resourceNode);
        if (resourceTypeReference != null)
        {
            applyResourceType(resourceNode, resourceTypeReference, resourceNode);
        }

        mergedResources.add(resourceNode);
        return node;
    }

    private void checkTraits(KeyValueNode resourceNode, ResourceNode baseResourceNode)
    {
        List<MethodNode> methodNodes = findMethodNodes(resourceNode);
        List<ReferenceNode> resourceTraitRefs = findTraitReferences(resourceNode);

        for (MethodNode methodNode : methodNodes)
        {
            List<ReferenceNode> traitRefs = findTraitReferences(methodNode);
            traitRefs.addAll(resourceTraitRefs);
            for (ReferenceNode traitRef : traitRefs)
            {
                String traitLevel = resourceTraitRefs.contains(traitRef) ? "resource" : "method";
                logger.debug("applying {} level trait '{}' to '{}.{}'", traitLevel, traitRef.getRefName(), resourceNode.getKey(), methodNode.getName());
                applyTrait(methodNode, traitRef, baseResourceNode);
            }
        }
    }

    private void applyResourceType(KeyValueNode targetNode, ReferenceNode resourceTypeReference, ResourceNode baseResourceNode)
    {
        ResourceTypeNode refNode = (ResourceTypeNode) resourceTypeReference.getRefNode();
        if (refNode == null)
        {
            throw new IllegalStateException("Invalid reference"); // validated by grammar
        }
        ResourceTypeNode templateNode = refNode.copy();
        templateNode.setParent(refNode.getParent());

        // resolve parameters
        Map<String, String> parameters = getBuiltinResourceTypeParameters(baseResourceNode);
        if (resourceTypeReference instanceof ParametrizedReferenceNode)
        {
            parameters.putAll(((ParametrizedReferenceNode) resourceTypeReference).getParameters());
        }
        resolveParameters(templateNode, parameters);

        // apply grammar phase to generate method nodes
        GrammarPhase validatePhase = new GrammarPhase(ramlGrammar.resourceTypeParamsResolved());
        validatePhase.apply(templateNode.getValue());

        // apply traits
        checkTraits(templateNode, baseResourceNode);

        // resolve inheritance
        ReferenceNode parentTypeReference = findResourceTypeReference(templateNode);
        if (parentTypeReference != null)
        {
            applyResourceType(templateNode, parentTypeReference, baseResourceNode);
        }

        merge(targetNode.getValue(), templateNode.getValue());
    }

    private Map<String, String> getBuiltinResourceTypeParameters(ResourceNode resourceNode)
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("resourcePathName", resourceNode.getResourcePathName());
        parameters.put("resourcePath", resourceNode.getResourcePath());
        return parameters;
    }

    private Map<String, String> getBuiltinTraitParameters(MethodNode methodNode, ResourceNode resourceNode)
    {
        Map<String, String> parameters = getBuiltinResourceTypeParameters(resourceNode);
        parameters.put("methodName", methodNode.getName());
        return parameters;
    }

    private void applyTrait(MethodNode methodNode, ReferenceNode traitReference, ResourceNode baseResourceNode)
    {
        TraitNode refNode = (TraitNode) traitReference.getRefNode();
        if (refNode == null)
        {
            throw new IllegalStateException("Invalid reference"); // validated by grammar
        }

        TraitNode copy = refNode.copy();

        // resolve parameters
        Map<String, String> parameters = getBuiltinTraitParameters(methodNode, baseResourceNode);
        if (traitReference instanceof ParametrizedReferenceNode)
        {
            parameters.putAll(((ParametrizedReferenceNode) traitReference).getParameters());
        }
        resolveParameters(copy, parameters);

        replaceNullValueWithObject(methodNode);
        merge(methodNode.getValue(), copy.getValue());
    }

    private void resolveParameters(Node parameterizedNode, Map<String, String> parameters)
    {
        ExecutionContext context = new ExecutionContext(parameters);
        List<StringTemplateNode> templateNodes = parameterizedNode.findDescendantsWith(StringTemplateNode.class);
        for (StringTemplateNode templateNode : templateNodes)
        {
            Node resolvedNode = templateNode.execute(context);
            templateNode.replaceWith(resolvedNode);
        }
    }

    private void replaceNullValueWithObject(KeyValueNode keyValueNode)
    {
        Node valueNode = keyValueNode.getValue();
        if (valueNode instanceof SYNullNode)
        {
            valueNode = new SYObjectNode((SYBaseRamlNode) valueNode);
            keyValueNode.setValue(valueNode);
        }
    }

    private List<ReferenceNode> findTraitReferences(KeyValueNode keyValueNode)
    {
        List<ReferenceNode> result = new ArrayList<>();
        Node isNode = NodeSelector.selectFrom("is", keyValueNode.getValue());
        if (isNode != null)
        {
            List<Node> children = isNode.getChildren();
            for (Node child : children)
            {
                result.add((ReferenceNode) child);
            }
        }
        return result;
    }

    private ReferenceNode findResourceTypeReference(KeyValueNode resourceNode)
    {
        return (ReferenceNode) NodeSelector.selectFrom("type", resourceNode.getValue());
    }

    private List<MethodNode> findMethodNodes(KeyValueNode resourceNode)
    {
        List<MethodNode> methodNodes = new ArrayList<>();
        for (Node node : resourceNode.getValue().getChildren())
        {
            if (node instanceof MethodNode)
            {
                methodNodes.add((MethodNode) node);
            }
        }
        return methodNodes;
    }

}
