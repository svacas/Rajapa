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
package org.raml.grammar.rule;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.nodes.NodeType;
import org.raml.nodes.StringNode;

import java.io.IOException;
import java.util.Collection;

public class ErrorNodeFactory
{

    public static ErrorNode createUnexpectedKey(Node key, Collection<String> options)
    {
        return new ErrorNode("Unexpected key '" + key + "'. Options are : " + StringUtils.join(options, " or "));
    }

    public static ErrorNode createInvalidElement(Node child)
    {
        return new ErrorNode("Invalid array element " + child + ".");
    }

    public static ErrorNode createInvalidRootElement(Node rootNode, String expected)
    {
        return new ErrorNode("Invalid root node " + rootNode + ". Expected : " + expected + ".");
    }

    public static ErrorNode createInvalidTemplateFunctionExpression(Node node, String token)
    {
        return new ErrorNode("Invalid template function expression " + token);
    }

    public static ErrorNode createInvalidTemplateParameterExpression(Node node, String token)
    {
        return new ErrorNode("Cannot resolve parameter " + token);
    }

    public static Node createRequiredValueNotFound(Node node, Rule keyRule)
    {
        final ErrorNode errorNode = new ErrorNode("Missing required field " + keyRule);
        errorNode.setSource(node);
        return errorNode;
    }

    public static Node createInvalidType(Node node, NodeType type)
    {
        return new ErrorNode("Invalid type " + node.getType() + ", expected " + type);
    }

    public static Node createInvalidFragmentName(String fragmentText)
    {
        return new ErrorNode("Invalid fragment name '" + fragmentText + "'");
    }

    public static Node createEmptyDocument()
    {
        return new ErrorNode("Empty document.");
    }

    public static Node createUnsupportedVersion(String version)
    {
        return new ErrorNode("Unsupported version " + version + "");
    }

    public static Node createInvalidHeader(String header)
    {
        return new ErrorNode("Invalid header declaration " + header);
    }

    public static Node createInvalidInput(IOException ioe)
    {
        return new ErrorNode("Error while reading the input. Reason " + ioe.getMessage());
    }
}
