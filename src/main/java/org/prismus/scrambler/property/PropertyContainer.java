package org.prismus.scrambler.property;

import org.prismus.scrambler.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class PropertyContainer extends Constant<Collection> {
    private Collection<? extends Property> propertyCollection;

    public PropertyContainer() {
        this(null, new ArrayList<Property>());
    }

    public PropertyContainer(String name) {
        this(name, new ArrayList<Property>());
    }

    public PropertyContainer(String name, Collection<? extends Property> propertyCollection) {
        super(name, new ArrayList(propertyCollection.size()));
        this.propertyCollection = propertyCollection;
    }

    public void setPropertyCollection(Property... propertyCollection) {
        this.propertyCollection = new ArrayList<Property>(Arrays.asList(propertyCollection));
    }

    public void setPropertyCollection(Collection<? extends Property> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    @SuppressWarnings({"unchecked"})
    public PropertyContainer addProperties(Property... properties) {
        propertyCollection.addAll(new ArrayList(Arrays.asList(properties)));
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public PropertyContainer addProperties(Collection<? extends Property> propertyCollection) {
        this.propertyCollection.addAll(new ArrayList(Arrays.asList(propertyCollection)));
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Collection value() {
        final Collection value = super.value();
        for (final Property property : propertyCollection) {
            value.add(property.value());
        }
        return value;
    }
}
