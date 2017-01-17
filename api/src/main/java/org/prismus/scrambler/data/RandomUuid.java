package org.prismus.scrambler.data;

import java.util.UUID;

/**
 * Generates random string identifier using {@link UUID#randomUUID()} method
 *
 * @author Serge Pruteanu
 */
public class RandomUuid extends ConstantData<String> {

    @Override
    protected String doNext() {
        return UUID.randomUUID().toString();
    }

}
