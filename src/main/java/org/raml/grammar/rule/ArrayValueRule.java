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
package org.raml.grammar.rule;

import org.raml.nodes.ArrayNode;
import org.raml.nodes.Node;

import java.util.List;

public class ArrayValueRule extends Rule
{

    private Rule of;

    public ArrayValueRule(Rule of)
    {
        this.of = of;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof ArrayNode;
    }

    @Override
    public Node transform(Node node)
    {
        Node result = node;
        if (getFactory() != null)
        {
            result = getFactory().create();
        }
        final List<Node> children = node.getChildren();
        for (Node child : children)
        {
            if (of.matches(child))
            {
                final Node transform = of.transform(child);
                child.replaceWith(transform);
            }
            else
            {
                child.replaceWith(ErrorNodeFactory.createInvalidElement(child));
            }
        }
        return result;
    }

    @Override
    public String getDescription()
    {
        return "Array[" + of.getDescription() + "]";
    }
}
