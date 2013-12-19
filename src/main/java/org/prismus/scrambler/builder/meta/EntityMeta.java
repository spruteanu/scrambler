package org.prismus.scrambler.builder.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class EntityMeta {
    private String name;
    private Class type;
    private List<PropertyMeta> propertyList;

    // todo Serge: implement foreign keys

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    void setType(Class type) {
        this.type = type;
    }

    public List<PropertyMeta> getPropertyList() {
        return propertyList;
    }

    void setPropertyList(List<PropertyMeta> propertyList) {
        this.propertyList = propertyList;
    }

    public List<PropertyMeta> getPropertyList(List<String> propertyNames) {
        List<PropertyMeta> results = propertyList;
        if (propertyNames != null) {
            results = new ArrayList<PropertyMeta>(propertyNames.size());
            for (final String propertyName : propertyNames) {
                for (final PropertyMeta propertyMeta : propertyList) {
                    if (propertyMeta.getName().equalsIgnoreCase(propertyName)) {
                        results.add(propertyMeta);
                    }
                }
            }
        }
        return results;
    }

    public List<IdentifierPropertyMeta> getIdentifiers() {
        final List<IdentifierPropertyMeta> identifiers = new ArrayList<IdentifierPropertyMeta>(3);
        for (final PropertyMeta propertyMeta : propertyList) {
            if (propertyMeta instanceof IdentifierPropertyMeta) {
                identifiers.add((IdentifierPropertyMeta) propertyMeta);
            }
        }
        return identifiers;
    }

    public static String[] getPropertyNames(List<PropertyMeta> propertyMetaList) {
        final String[] propertyNames = new String[propertyMetaList.size()];
        for (int i = 0, propertyMetaListSize = propertyMetaList.size(); i < propertyMetaListSize; i++) {
            final PropertyMeta propertyMeta = propertyMetaList.get(i);
            propertyNames[i] = propertyMeta.getName();
        }
        return propertyNames;
    }

    public static int[] getSqlTypes(List<PropertyMeta> propertyMetaList) {
        final int[] sqlTypes = new int[propertyMetaList.size()];
        for (int i = 0, propertyMetaListSize = propertyMetaList.size(); i < propertyMetaListSize; i++) {
            final PropertyMeta propertyMeta = propertyMetaList.get(i);
            sqlTypes[i] = propertyMeta.getSqlType();
        }
        return sqlTypes;
    }

}
