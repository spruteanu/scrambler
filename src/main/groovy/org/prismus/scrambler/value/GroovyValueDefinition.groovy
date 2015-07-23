package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings(["GroovyAssignabilityCheck", "UnnecessaryQualifiedReference"])
class GroovyValueDefinition {
    private Properties configurationProperties
    private GroovyShell shell

    private ValueDefinition definition = new ValueDefinition()

    GroovyValueDefinition() {
    }

    @CompileStatic
    ValueDefinition parseText(String definitionText) {
        if (!shell) {
            shell = createGroovyShell()
        }
        final script = (DelegatingScript)shell.parse(definitionText)
        script.setDelegate(definition)
        script.run()
        return definition
    }

    @CompileStatic
    ValueDefinition parse(String resource) throws IOException {
        final URL url = this.getClass().getResource(resource)
        if (url == null) {
            throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
        }
        return parseText(ResourceGroovyMethods.getText(url))
    }

    ValueDefinition parse(def resource) throws IOException {
        return parseText(resource.text)
    }

    @CompileStatic
    protected GroovyShell createGroovyShell() {
        final compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.setScriptBaseClass(DelegatingScript.name)

        final importCustomizer = new ImportCustomizer()
        importCustomizer.addStarImports(Value.package.name)
        importCustomizer.addStarImports(Constant.package.name)
        importCustomizer.addStarImports(getClass().package.name)
        compilerConfiguration.addCompilationCustomizers(importCustomizer)

        return new GroovyShell(compilerConfiguration)
    }

    @CompileStatic
    Properties getConfigurationProperties() {
        return configurationProperties
    }

    @CompileStatic
    void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties
    }

    @CompileStatic
    void setConfigurationProperties(String configurationResource) throws IOException {
        setConfigurationProperties(getClass().getResourceAsStream(configurationResource))
    }

    @CompileStatic
    protected void setConfigurationProperties(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            final Properties properties = new Properties()
            try {
                properties.load(inputStream)
                this.configurationProperties = properties
            } finally {
                try {
                    inputStream.close()
                } catch (IOException ignore) { }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    static void register() {
        Object.metaClass.mixin ObjectCategory
        Number.metaClass.mixin NumberCategory
        Date.metaClass.mixin DateCategory
        String.metaClass.mixin StringCategory
        Collection.metaClass.mixin CollectionCategory
        Map.metaClass.mixin MapCategory

        Class.metaClass.mixin ClassCategory
    }

    static {
        register()
    }

}
