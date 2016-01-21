/*
 *
 */
package org.raml.nodes.snakeyaml;

import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;
import static org.yaml.snakeyaml.nodes.NodeId.sequence;

import org.raml.nodes.impl.KeyValueNodeImpl;
import org.raml.nodes.Node;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SYModelWrapper
{

    public static final Tag INCLUDE_TAG = new Tag("!include");

    public Node wrap(org.yaml.snakeyaml.nodes.Node node)
    {
        if (node.getNodeId() == mapping)
        {
            return wrap((MappingNode) node);
        }
        if (node.getNodeId() == sequence)
        {
            return wrap((SequenceNode) node);
        }
        if (node.getNodeId() == scalar)
        {
            return wrap((ScalarNode) node);
        }
        else
        {
            throw new IllegalStateException("Invalid node type");
        }
    }

    private SYObjectNode wrap(MappingNode mappingNode)
    {
        SYObjectNode mapping = new SYObjectNode(mappingNode);
        for (NodeTuple nodeTuple : mappingNode.getValue())
        {
            Node key = wrap(nodeTuple.getKeyNode());
            Node value = wrap(nodeTuple.getValueNode());
            KeyValueNodeImpl keyValue = new KeyValueNodeImpl(key, value);
            mapping.addChild(keyValue);
        }
        return mapping;
    }

    private Node wrap(ScalarNode scalarNode)
    {
        final Tag tag = scalarNode.getTag();
        if (INCLUDE_TAG.equals(tag))
        {
            return new SYIncludeNode(scalarNode);
        }
        else if (Tag.NULL.equals(tag))
        {
            return new SYNullNode(scalarNode);
        }
        else if (Tag.FLOAT.equals(tag))
        {
            return new SYFloatingNode(scalarNode);
        }
        else if (Tag.INT.equals(tag))
        {
            return new SYIntegerNode(scalarNode);
        }
        else
        {
            return new SYStringNode(scalarNode);
        }
    }

    private SYArrayNode wrap(SequenceNode sequenceNode)
    {
        SYArrayNode sequence = new SYArrayNode(sequenceNode);
        for (org.yaml.snakeyaml.nodes.Node node : sequenceNode.getValue())
        {
            sequence.addChild(wrap(node));
        }
        return sequence;
    }
}
