package org.prismus.scrambler.builder;

import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.Constant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ValueDefinitionParser {
    private Properties configurationProperties;
    private GroovyShell shell;

    public ValueDefinition parseText(String definitionText) {
        if (shell == null) {
            shell = createGroovyShell();
        }
        return (ValueDefinition) shell.evaluate(
                definitionText
                + "\n    build()"
                + "\n    return this"
        );
    }

    public ValueDefinition parse(String resource) throws IOException {
        final URL url = this.getClass().getResource(resource);
        if (url == null) {
            throw new IllegalArgumentException(String.format("Not found resource for: %s", resource));
        }
        return parseText(ResourceGroovyMethods.getText(url));
    }

    public ValueDefinition parse(URL resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("Null resource provided");
        }
        return parseText(ResourceGroovyMethods.getText(resource));
    }

    public ValueDefinition parse(File file) throws IOException {
        return parseText(ResourceGroovyMethods.getText(file));
    }

    public ValueDefinition parse(InputStream inputStream) throws IOException {
        return parseText(IOGroovyMethods.getText(inputStream));
    }

    public ValueDefinition parse(Reader reader) throws IOException {
        return parseText(IOGroovyMethods.getText(reader));
    }

    GroovyShell createGroovyShell() {
        final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass(ValueDefinition.class.getName());

        final ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(Value.class.getPackage().getName());
        importCustomizer.addStarImports(Constant.class.getPackage().getName());
        importCustomizer.addStarImports(getClass().getPackage().getName());
        compilerConfiguration.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(compilerConfiguration);
    }

    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public void setConfigurationProperties(String configurationResource) throws IOException {
        setConfigurationProperties(getClass().getResourceAsStream(configurationResource));
    }

    void setConfigurationProperties(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            final Properties properties = new Properties();
            try {
                properties.load(inputStream);
                this.configurationProperties = properties;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

}
