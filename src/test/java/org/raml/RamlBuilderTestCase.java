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
package org.raml;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.nodes.Node;
import org.raml.utils.TreeDumper;

@RunWith(Parameterized.class)
public class RamlBuilderTestCase
{

    private File input;
    private File expected;
    private String name;

    public RamlBuilderTestCase(File input, File expected, String name)
    {
        this.input = input;
        this.expected = expected;
        this.name = name;
    }

    @Test
    public void runTest() throws IOException
    {
        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(input);
        assertThat(raml, notNullValue());
        String dump = new TreeDumper().dump(raml);
        String expected = IOUtils.toString(new FileInputStream(this.expected));
        System.out.println("dump = \n" + dump);
        Assert.assertThat(dump, IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(expected));

    }


    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> data() throws URISyntaxException
    {
        final URI baseFolder = RamlBuilderTestCase.class.getResource("").toURI();
        final File testFolder = new File(baseFolder);
        final File[] scenarios = testFolder.listFiles();
        List<Object[]> result = new ArrayList<>();
        for (File scenario : scenarios)
        {
            if (scenario.isDirectory())
            {
                File input = new File(scenario, "input.raml");
                File output = new File(scenario, "output.txt");
                if (input.isFile() && output.isFile())
                {
                    result.add(new Object[] {input, output, scenario.getName()});
                }
            }
        }
        return result;
    }


}
