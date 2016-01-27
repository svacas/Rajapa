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
package org.raml.grammar;

import org.raml.grammar.rule.ErrorNodeFactory;
import org.raml.grammar.rule.Rule;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.phase.Phase;

public class GrammarPhase implements Phase
{

    private Rule rootRule;

    public GrammarPhase(Rule rootRule)
    {
        this.rootRule = rootRule;
    }

    @Override
    public Node apply(Node node)
    {
        if (rootRule.matches(node))
        {
            final Node result = rootRule.transform(node);
            node.replaceWith(result);
            return result;
        }
        else
        {
            final ErrorNode errorNode = ErrorNodeFactory.createInvalidRootElement(node, rootRule.getDescription());
            node.replaceWith(errorNode);
            return errorNode;
        }
    }

}
