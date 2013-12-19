package org.prismus.scrambler.builder;

import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class DefinitionParser {
    private Properties configurationProperties;
    private GroovyShell shell;

    public Definition parse(String definitionText) {
        if (shell == null) {
            shell = createGroovyShell();
        }
        return (Definition) shell.evaluate(definitionText);
    }

    GroovyShell createGroovyShell() {
        final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass(Definition.class.getName());
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
                } catch (IOException ignore) { }
            }
        }
    }

}
