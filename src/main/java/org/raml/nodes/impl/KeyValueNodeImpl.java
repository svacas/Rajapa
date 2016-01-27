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
package org.raml.nodes.impl;

import org.raml.nodes.BaseNode;
import org.raml.nodes.Position;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;

import javax.annotation.Nonnull;

public class KeyValueNodeImpl extends BaseNode implements KeyValueNode
{

    public KeyValueNodeImpl(@Nonnull Node keyNode, @Nonnull Node valueNode)
    {
        addChild(keyNode);
        addChild(valueNode);
    }

    public KeyValueNodeImpl()
    {

    }

    @Override
    public Position getStartPosition()
    {
        return getKey().getStartPosition();
    }

    @Override
    public Position getEndPosition()
    {
        return getValue().getEndPosition();
    }

    @Override
    public void addChild(Node node)
    {
        if (getChildren().size() >= 2)
        {
            throw new IllegalStateException();
        }
        super.addChild(node);
    }

    @Override
    public Node getKey()
    {
        if (getChildren().isEmpty())
        {
            throw new IllegalStateException("Key value pair with no key " + getClass().getSimpleName());
        }
        return getChildren().get(0);
    }

    @Override
    public Node getValue()
    {
        if (getChildren().size() < 2)
        {
            throw new IllegalStateException("Key value pair with no value " + getClass().getSimpleName());
        }
        return getChildren().get(1);
    }
}
