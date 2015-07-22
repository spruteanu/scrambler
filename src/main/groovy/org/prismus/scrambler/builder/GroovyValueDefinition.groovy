package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.*

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
        Object.metaClass {
            constant { ->
                return new Constant(delegate)
            }
        }

        Number.metaClass {
            incremental { Number step = null ->
                return NumberValue.increment((Number) delegate, step)
            }

            random { Number minimum = null, Number maximum = null ->
                return NumberValue.random((Number) delegate, minimum, maximum)
            }
        }

        Date.metaClass {
            incremental { Integer step = null, Integer calendarField = null ->
                return DateValue.increment((Date) delegate, step, calendarField)
            }

            random { Date minimum = null, Date maximum = null ->
                return DateValue.random((Date) delegate, minimum, maximum)
            }
        }

        String.metaClass {
            incremental { String pattern = null, Integer index = null ->
                return StringValue.increment((String) delegate, pattern, index)
            }

            random { Integer count = null ->
                return StringValue.random((String) delegate, count)
            }
        }

        Collection.metaClass {
            of { Value val, Integer count = null ->
                Util.checkNullValue(val)
                return new CollectionValue((Collection) delegate, val, count)
            }

            randomOf { ->
                final collection = (Collection) delegate
                Util.checkEmptyCollection(collection)
                return CollectionValue.randomOf(collection)
            }
        }

        Map.metaClass {
            of { Map keyValueMap = null ->
                return new MapValue((Map) delegate, keyValueMap)
            }
        }

        Class.metaClass {
            of { Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: new GroovyDefinitionCallable(defCl)
                )
            }

            of { Map<Object, Object> propertyValueMap, Closure defCl = null ->
                GroovyDefinitionCallable definitionCallable = null
                if (defCl) {
                    definitionCallable = new GroovyDefinitionCallable(defCl)
                }
                return new InstanceValue(
                        type: (Class) delegate,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: definitionCallable
                ).usingDefinitions(propertyValueMap)
            }

            of { String propertyName, Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        predicate: Util.createPropertyPredicate(propertyName),
                        definitionClosure: new GroovyDefinitionCallable(defCl)
                )
            }

            of { Collection constructorArgs, Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        constructorArguments: constructorArgs,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: new GroovyDefinitionCallable(defCl)
                )
            }

            of { String propertyName, Collection constructorArgs, Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        constructorArguments: constructorArgs,
                        predicate: Util.createPropertyPredicate(propertyName),
                        definitionClosure: new GroovyDefinitionCallable(defCl)
                )
            }

            array { Value val, Integer count = null ->
                return ClassValue.of((Class) delegate, val, count)
            }

            array { Number defaultValue, Number step, Integer count = null ->
                return ClassValue.incrementArray((Class) delegate, defaultValue, step, count)
            }

            reference { String propertyPredicate = null ->
                return new ReferenceValue(new TypePredicate((Class) delegate), propertyPredicate != null ? Util.createPropertyPredicate(propertyPredicate) : null)
            }
        }
    }

    static {
        register()
    }

}
