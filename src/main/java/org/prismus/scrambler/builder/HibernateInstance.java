package org.prismus.scrambler.builder;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.EntityType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class HibernateInstance extends DecoratorInstance<Map> {
    private static final String IDENTIFIER_NOT_DEFINED_MSG = "Identifier property is not defined, identifier %s property should be set";

    private final SessionFactory sessionFactory;
    private final String entityName;

    public HibernateInstance(SessionFactory sessionFactory, String entityName) {
        this(sessionFactory, entityName, new HashMap<String, Object>());
    }

    public HibernateInstance(SessionFactory sessionFactory,
                             String entityName,
                             Map<String, Object> instance) {
        super();
        this.instance.setValue(instance);
        this.sessionFactory = sessionFactory;
        this.entityName = entityName;
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, Object> next() {
        randomRequired();
        return (Map<String, Object>) super.next();
    }

    public HibernateInstance random(String... properties) {
        final ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        for (final String propertyName : properties) {
            final Type type = classMetadata.getPropertyType(propertyName);
//            instance.random(type.getReturnedClass()); // todo Serge: fix me
        }
        return this;
    }

    public HibernateInstance incremental(String... properties) {
        final ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        for (final String propertyName : properties) {
            final Type type = classMetadata.getPropertyType(propertyName);
//            instance.incremental(type.getReturnedClass()); // todo Serge: fix me
        }
        return this;
    }

    public HibernateInstance randomAll() {
        random(true);
        return this;
    }

    public List<String> getPropertyNames() {
        return Arrays.asList(sessionFactory.getClassMetadata(entityName).getPropertyNames());
    }

    void randomRequired() {
        random(false);
        checkIdentifierSet();
    }

    void random(boolean includeNullable) {
        setMissingIdentifier();

        final ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        final boolean[] propertyNullability = classMetadata.getPropertyNullability();
        final String[] propertyNames = classMetadata.getPropertyNames();
        for (int i = 0, propertyNamesLength = propertyNames.length; i < propertyNamesLength; i++) {
            final String propertyName = propertyNames[i];
            final Type type = classMetadata.getPropertyType(propertyName);
            if (includeNullable || !propertyNullability[i]) {
                if (type instanceof EntityType) {
                    randomProperty(((EntityType) type));
                } else {
                    if (type instanceof PrimitiveType) {
//                        instance.random(type.getReturnedClass(), ((PrimitiveType) type).getDefaultValue()); // todo Serge: fix me
                    } else {
//                        instance.random(type.getReturnedClass()); // todo Serge: fix me
                    }
                }
            }
        }
    }

    void checkIdentifierSet() {
        final ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        if (!instance.propertyValueMap.containsKey(classMetadata.getIdentifierPropertyName())) {
            throw new IllegalStateException(String.format(IDENTIFIER_NOT_DEFINED_MSG, classMetadata.getIdentifierPropertyName()));
        }
    }

    @SuppressWarnings({"unchecked"})
    void setMissingIdentifier() {
        final ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        final String identifierPropertyName = classMetadata.getIdentifierPropertyName();
        if (!instance.propertyValueMap.containsKey(identifierPropertyName)) {
//            instance.addProperty(Incremental.of(classMetadata.getPropertyType(identifierPropertyName).getReturnedClass())); // todo Serge: fix me
        }
    }

    public void randomProperty(EntityType type) {
        // todo sergep: associations are not implemented yet
    }

    public void nextInstance() {
        usingValue(new HashMap<String, Object>());
    }

    public static HibernateInstance of(SessionFactory sessionFactory, String entityName) {
        return new HibernateInstance(sessionFactory, entityName);
    }

    public static HibernateInstance of(SessionFactory sessionFactory,
                                       String entityName,
                                       Map<String, Object> instance) {
        return new HibernateInstance(sessionFactory, entityName, instance);
    }
}
