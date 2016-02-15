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
package org.raml.suggester;

public class DefaultSuggestion implements Suggestion, Comparable<Suggestion>
{

    private String label;
    private String description;
    private String value;

    public DefaultSuggestion(String value, String description, String label)
    {
        this.value = value;
        this.description = description;
        this.label = label;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public Suggestion withDescription(String description)
    {
        return new DefaultSuggestion(getValue(), description, getLabel());
    }

    @Override
    public Suggestion withValue(String value)
    {
        return new DefaultSuggestion(value, getDescription(), getLabel());
    }

    @Override
    public String toString()
    {
        return "DefaultSuggestion{" +
               "label='" + label + '\'' +
               ", description='" + description + '\'' +
               ", value='" + value + '\'' +
               '}';
    }

    @Override
    public int compareTo(Suggestion other)
    {
        return this.getLabel().compareTo(other.getLabel());
    }
}
