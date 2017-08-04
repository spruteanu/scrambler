/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

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
