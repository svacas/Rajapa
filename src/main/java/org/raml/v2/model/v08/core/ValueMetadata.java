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
package org.raml.v2.model.v08.core;

public interface ValueMetadata
{

    /**
     * Returns 'true', if the actual value is missing, and returned value has
     * been obtained from the RAML document by means of some rule.  
     */
    boolean calculated();

    /**
     * Returns 'true', if the actual value is missing, and returned value is
     * stated in the RAML spec as default for the property
     */
    boolean insertedAsDefault();

    /**
     * Returns 'true' for optional siblings of traits and resource types
     */
    boolean optional();

    /**
     * Returns 'true', if the instance contains no positive information about the value.
     */
    boolean emptyMeta();

}
