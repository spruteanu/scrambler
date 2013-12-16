package org.prismus.scrambler.property

import groovy.transform.CompileStatic
import org.prismus.scrambler.Property

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class PropertyContainer extends Generic<Collection> {
    private Collection<? extends Property> propertyCollection

    PropertyContainer() {
        this(null, new ArrayList<Property>())
    }

    PropertyContainer(String name) {
        this(name, new ArrayList<Property>())
    }

    PropertyContainer(String name, Collection<? extends Property> propertyCollection) {
        super(name, new ArrayList(propertyCollection.size()))
        this.propertyCollection = propertyCollection
    }

    void setPropertyCollection(Property... propertyCollection) {
        this.propertyCollection = new ArrayList<Property>(Arrays.asList(propertyCollection))
    }

    void setPropertyCollection(Collection<? extends Property> propertyCollection) {
        this.propertyCollection = propertyCollection
    }

    PropertyContainer addProperties(Property... properties) {
        propertyCollection.addAll(new ArrayList(Arrays.asList(properties)))
        return this
    }

    PropertyContainer addProperties(Collection<? extends Property> propertyCollection) {
        this.propertyCollection.addAll(new ArrayList(Arrays.asList(propertyCollection)))
        return this
    }

    Collection value() {
        final Collection value = super.value() as Collection
        for (final Property property : propertyCollection) {
            value.add(property.value())
        }
        return value
    }
}
