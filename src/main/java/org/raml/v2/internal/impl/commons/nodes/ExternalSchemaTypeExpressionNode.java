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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYIncludeNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaTypeFacets;
import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeFacets;
import org.raml.v2.internal.utils.NodeUtils;
import org.raml.v2.internal.utils.SchemaGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExternalSchemaTypeExpressionNode extends StringNodeImpl implements TypeExpressionNode
{
    public ExternalSchemaTypeExpressionNode(String value)
    {
        super(value);
    }

    private ExternalSchemaTypeExpressionNode(ExternalSchemaTypeExpressionNode node)
    {
        super(node);
    }

    public String getSchemaValue()
    {
        return getValue();
    }

    public String getSchemaPath()
    {
        return this.getStartPosition().getPath();
    }

    @Nullable
    public String getInternalFragment()
    {
        final Node source = NodeUtils.getSource(this, SYIncludeNode.class);
        if (source != null)
        {
            final String value = ((SYIncludeNode) source).getValue();
            if (value.contains("#"))
            {
                return value.substring(value.indexOf("#") + 1);
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ExternalSchemaTypeExpressionNode(this);
    }

    public boolean isJsonSchema()
    {
        return SchemaGenerator.isJsonSchema(getSchemaPath());
    }

    public boolean isXmlSchema()
    {
        return SchemaGenerator.isXmlSchema(getSchemaValue());
    }

    @Nullable
    @Override
    public TypeFacets generateDefinition()
    {
        if (isXmlSchema())
        {
            return new XmlSchemaTypeFacets(getSchemaValue(), getSchemaPath(), getInternalFragment());
        }
        else
        {
            return new JsonSchemaTypeFacets(getSchemaValue(), getSchemaPath(), getInternalFragment());
        }

    }
}
