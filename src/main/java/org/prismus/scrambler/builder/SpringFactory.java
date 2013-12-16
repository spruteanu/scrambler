package org.prismus.scrambler.builder;

import org.prismus.scrambler.Property;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class SpringFactory implements Property {
    private static final String NOT_SUPPORTED_FILE_TYPE_MSG = "Not supported spring file: %s for provided list: %s, Supported extensions: %s";
    private static final String NO_CONTEXT_FILES_MSG = "At least one spring context file should be specified for files: %s";

    private static final String XML_FILE_MATCH = ".xml";
    private static final String CONTEXT_PROPERTIES_MATCH = "context.properties";
    private static final String OVERRIDE_PROPERTIES_MATCH = "override.properties";
    private static final String PROPERTIES_MATCH = ".properties";
    private static final String PLACEHOLDER_PROPERTIES_MATCH = "placeholder.properties";

    private String name = "instanceBuilder";
    private List<String> contextFiles;
    private boolean fileResource;
    private Property property;

    public SpringFactory() {
    }

    public SpringFactory(List<String> contextFiles, boolean fileResource) {
        this.contextFiles = contextFiles;
        this.fileResource = fileResource;
    }

    public void setFileResource(boolean fileResource) {
        this.fileResource = fileResource;
    }

    public void setContextFiles(List<String> contextFiles) {
        this.contextFiles = contextFiles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SpringFactory usingContextFiles(List<String> contextFiles) {
        return usingContextFiles(contextFiles, false);
    }

    public SpringFactory usingContextFiles(List<String> contextFiles, boolean fileResource) {
        this.contextFiles = contextFiles;
        this.fileResource = fileResource;
        return this;
    }

    public Object value() {
        return getProperty().value();
    }

    public Property getProperty() {
        if (property == null) {
            property = createProperty();
        }
        return property;
    }

    Property createProperty() {
        final Set<String> contextXmls = new LinkedHashSet<String>();
        final Set<String> contextProperties = new LinkedHashSet<String>();
        final Set<String> overrideProperties = new LinkedHashSet<String>();
        final Set<String> placeHolderProperties = new LinkedHashSet<String>();

        matchContextResources(contextXmls, contextProperties, overrideProperties, placeHolderProperties);

        final GenericApplicationContext context = new GenericApplicationContext();
        loadDefinitions(new XmlBeanDefinitionReader(context), contextXmls);
        loadDefinitions(new PropertiesBeanDefinitionReader(context), contextProperties);
        if (!representsSpringContext(contextXmls, contextProperties)) {
//            buildRuntimeInstance(context);
            throw new UnsupportedOperationException(String.format(NO_CONTEXT_FILES_MSG, contextFiles));
        }
        addPlaceHolders(placeHolderProperties, context);
        addPropertyOverrides(overrideProperties, context);

        context.refresh();
        return context.getBean(getName(), Property.class);
    }

    boolean representsSpringContext(Set<String> contextXmls, Set<String> contextProperties) {
        return contextXmls.size() > 0 || contextProperties.size() > 0;
    }

    void loadDefinitions(BeanDefinitionReader definitionReader, Set<String> contextResources) {
        for (final String resource : contextResources) {
            definitionReader.loadBeanDefinitions(getResourceObject(resource));
        }
    }

    AbstractResource getResourceObject(String resource) {
        return fileResource ? new FileSystemResource(resource) : new ClassPathResource(resource);
    }

    void addPropertyOverrides(Set<String> overrideProperties, GenericApplicationContext context) {
        for (final String overrideResource : overrideProperties) {
            final PropertyOverrideConfigurer configurer = new PropertyOverrideConfigurer();
            configurer.setIgnoreInvalidKeys(true);
            configurer.setIgnoreResourceNotFound(true);
            configurer.setLocation(getResourceObject(overrideResource));
            context.addBeanFactoryPostProcessor(configurer);
        }
    }

    void addPlaceHolders(Set<String> placeHolderProperties, GenericApplicationContext context) {
        for (final String placeHolderResource : placeHolderProperties) {
            final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            configurer.setIgnoreUnresolvablePlaceholders(true);
            configurer.setIgnoreResourceNotFound(true);
            configurer.setLocation(getResourceObject(placeHolderResource));
            context.addBeanFactoryPostProcessor(configurer);
        }
    }

    void matchContextResources(Set<String> contextXmls, Set<String> contextProperties, Set<String> overrideProperties, Set<String> placeHolderProperties) {
        for (final String contextFile : contextFiles) {
            if (contextFile.endsWith(XML_FILE_MATCH)) {
                contextXmls.add(contextFile);
            } else if (contextFile.endsWith(CONTEXT_PROPERTIES_MATCH)) {
                contextProperties.add(contextFile);
            } else if (contextFile.endsWith(OVERRIDE_PROPERTIES_MATCH)) {
                overrideProperties.add(contextFile);
            } else if (contextFile.endsWith(PROPERTIES_MATCH)) {
                overrideProperties.add(contextFile);
            } else if (contextFile.endsWith(PLACEHOLDER_PROPERTIES_MATCH)) {
                placeHolderProperties.add(contextFile);
            } else {
                throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_FILE_TYPE_MSG, contextFile, contextFiles,
                        Arrays.asList(XML_FILE_MATCH, CONTEXT_PROPERTIES_MATCH, OVERRIDE_PROPERTIES_MATCH, PLACEHOLDER_PROPERTIES_MATCH)));
            }
        }
    }
}
