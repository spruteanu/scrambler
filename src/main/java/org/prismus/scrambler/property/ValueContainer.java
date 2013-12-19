package org.prismus.scrambler.property;

import org.prismus.scrambler.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueContainer extends Constant<Collection> {
    private Collection<? extends Value> propertyCollection;

    public ValueContainer() {
        this(new ArrayList<Value>());
    }

    public ValueContainer(Collection<? extends Value> propertyCollection) {
        super(new ArrayList(propertyCollection.size()));
        this.propertyCollection = propertyCollection;
    }

    public void setPropertyCollection(Value... valueCollection) {
        this.propertyCollection = new ArrayList<Value>(Arrays.asList(valueCollection));
    }

    public void setPropertyCollection(Collection<? extends Value> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    @SuppressWarnings({"unchecked"})
    public ValueContainer addProperties(Value... properties) {
        propertyCollection.addAll(new ArrayList(Arrays.asList(properties)));
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public ValueContainer addProperties(Collection<? extends Value> propertyCollection) {
        this.propertyCollection.addAll(new ArrayList(Arrays.asList(propertyCollection)));
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Collection value() {
        final Collection value = super.value();
        for (final Value property : propertyCollection) {
            value.add(property.value());
        }
        return value;
    }
}
