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
package org.raml.v2.internal.impl.commons.model;

import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.utils.NodeSelector;

public class Api extends LibraryBase
{

    private RamlDocumentNode node;

    public Api(RamlDocumentNode delegateNode)
    {
        node = delegateNode;
    }

    @Override
    protected Node getNode()
    {
        return node;
    }

    public String title()
    {
        return getStringValue("title");
    }

    public String version()
    {
        return getStringValue("version");
    }

    public StringType baseUri()
    {
        return getStringTypeValue("baseUri");
    }

    public List<StringType> mediaType()
    {
        return getList("mediaType", StringType.class);
    }

    public List<DocumentationItem> documentation()
    {
        return getList("documentation", DocumentationItem.class);
    }

    public List<Resource> resources()
    {
        ArrayList<Resource> resultList = new ArrayList<>();
        for (Node item : node.getChildren())
        {
            if (item instanceof ResourceNode)
            {
                resultList.add(new Resource((ResourceNode) item));
            }
        }
        return resultList;
    }

    public List<String> protocols()
    {
        ArrayList<String> resultList = new ArrayList<>();
        Node parent = NodeSelector.selectFrom("protocols", node);
        for (Node child : parent.getChildren())
        {
            resultList.add(child.toString());
        }
        return resultList;
    }

    public List<SecuritySchemeRef> securedBy()
    {
        return getList("securedBy", SecuritySchemeRef.class);
    }

    public List<TypeDeclaration> types()
    {
        return getTypeDeclarations("types");
    }

    public List<TypeDeclaration> schemas()
    {
        return getTypeDeclarations("schemas");
    }

    private List<TypeDeclaration> getTypeDeclarations(String rootTypesName)
    {
        List<TypeDeclaration> result = new ArrayList<>();
        Node typesNode = NodeSelector.selectFrom(rootTypesName, node);
        if (typesNode != null)
        {
            for (Node child : typesNode.getChildren())
            {
                result.add(new TypeDeclaration((KeyValueNode) child));
            }
        }
        return result;
    }

    public List<GlobalSchema> schemasV08()
    {
        List<GlobalSchema> result = new ArrayList<>();
        Node typesNode = NodeSelector.selectFrom("schemas", node);
        if (typesNode != null)
        {
            for (Node child : typesNode.getChildren())
            {
                result.add(new GlobalSchema((KeyValueNode) child));
            }
        }
        return result;
    }

}
