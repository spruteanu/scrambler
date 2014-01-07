package org.prismus.scrambler.builder;

import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.meta.EntityMeta;
import org.prismus.scrambler.property.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ValueDefinitionParser {
    private Properties configurationProperties;
    private GroovyShell shell;

    public Object parse(String definitionText) {
        if (shell == null) {
            shell = createGroovyShell();
        }
        return shell.evaluate(definitionText);
    }

    GroovyShell createGroovyShell() {
        final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass(ValueDefinition.class.getName());

        final ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(Value.class.getPackage().getName());
        importCustomizer.addStarImports(Constant.class.getPackage().getName());
        importCustomizer.addStarImports(getClass().getPackage().getName());
        importCustomizer.addStarImports(EntityMeta.class.getPackage().getName());
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
