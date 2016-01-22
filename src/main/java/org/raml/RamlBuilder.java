/*
 *
 */
package org.raml;

import org.raml.grammar.GrammarPhase;
import org.raml.grammar.Raml10Grammar;
import org.raml.loader.*;
import org.raml.nodes.Node;
import org.raml.nodes.snakeyaml.RamlNodeParser;
import org.raml.transformer.StringTemplateExpressionTransformer;
import org.raml.transformer.TransformationPhase;
import org.raml.transformer.IncludeResolver;
import org.raml.utils.StreamUtils;

import java.io.*;

public class RamlBuilder
{

    private ResourceLoader resourceLoader;

    public RamlBuilder()
    {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public Node build(File ramlFile) throws FileNotFoundException
    {
        resourceLoader = new CompositeResourceLoader(new UrlResourceLoader(),
                new ClassPathResourceLoader(),
                new FileResourceLoader("."),
                new FileResourceLoader(ramlFile.getParent()));
        return build(new FileReader(ramlFile), "");
    }

    public Node build(String resourceLocation)
    {
        InputStream inputStream = resourceLoader.fetchResource(resourceLocation);
        return build(StreamUtils.reader(inputStream), resourceLocation);
    }

    public Node build(Reader content, String resourceLocation)
    {
        Node rootNode = RamlNodeParser.parse(content);
        // The first phase expands the includes.
        final TransformationPhase firstPhase = new TransformationPhase(new IncludeResolver(resourceLoader, resourceLocation), new StringTemplateExpressionTransformer());
        rootNode = firstPhase.apply(rootNode);
        // Runs Schema. Applies the Raml rules and changes each node for a more specific.
        final GrammarPhase secondPhase = new GrammarPhase(new Raml10Grammar().raml());
        rootNode = secondPhase.apply(rootNode);
        // Detect references and mark invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // Normalize resources and detects duplicated ones and more than one use of url parameters.

        // Applies resourceTypes and Traits Library

        return rootNode;
    }


}
