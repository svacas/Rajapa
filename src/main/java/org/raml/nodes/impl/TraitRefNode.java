/*
 *
 */
package org.raml.nodes.impl;

import org.raml.grammar.Raml10Grammar;
import org.raml.nodes.Node;
import org.raml.utils.NodeSelector;

public class TraitRefNode extends AbstractReferenceNode
{

    private String name;

    public TraitRefNode(String name)
    {
        this.name = name;
    }

    @Override
    public String getRefName()
    {
        return name;
    }

    @Override
    public TraitNode getRefNode()
    {
        // We add the .. as the node selector selects the value and we want the key value pair
        final Node resolve = NodeSelector.selectFrom(Raml10Grammar.TRAITS_KEY_NAME + "/*/" + getRefName() + "/..", getRelativeNode());
        if (resolve instanceof TraitNode)
        {
            return (TraitNode) resolve;
        }
        else
        {
            return null;
        }
    }
}
