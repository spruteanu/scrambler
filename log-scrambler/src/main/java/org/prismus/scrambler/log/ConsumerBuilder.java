package org.prismus.scrambler.log;

import java.util.Map;

/**
 * @author Serge Pruteanu
 */
public class ConsumerBuilder<T extends LogConsumer> {
    protected LogCrawler.Builder contextBuilder;
    private T consumer;
    private Object consumerObj;
    private Object[] args;

    private Map<String, Object> consumerProperties;

    ConsumerBuilder() {
    }

    ConsumerBuilder(LogCrawler.Builder contextBuilder, Object consumer, Object... args) {
        this.contextBuilder = contextBuilder;
        this.consumerObj = consumer;
        this.args = args;
    }

    ConsumerBuilder withConsumer(T consumer) {
        this.consumer = consumer;
        return this;
    }

    ConsumerBuilder withProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties;
        return this;
    }

    LogCrawler.Builder recurContext() {
        return contextBuilder;
    }

    @SuppressWarnings("unchecked")
    protected T newConsumer(Object object, Object... objArgs) {
        T result;
        if (object instanceof LogConsumer) {
            result = (T)object;
        } else if (object instanceof ConsumerBuilder) {
            result = (T) ((ConsumerBuilder) object).build();
        } else {
            result = (T) contextBuilder.getConsumer(object, objArgs);
        }
        if (consumerProperties != null) {
            DefaultObjectProvider.setInstanceProperties(result, consumerProperties);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected T getConsumer() {
        if (consumer == null) {
            consumer = (T) newConsumer(consumerObj, args);
        }
        return consumer;
    }

    public T build() {
        return newConsumer(getConsumer());
    }

}
