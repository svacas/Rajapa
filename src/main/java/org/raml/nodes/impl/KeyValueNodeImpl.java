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
