package org.prismus.scrambler.value;

import java.util.UUID;

/**
 * Generates random string identifier using {@link UUID#randomUUID()} method
 *
 * @author Serge Pruteanu
 */
public class RandomUuid extends Constant<String> {

    @Override
    protected String doNext() {
        return UUID.randomUUID().toString();
    }

}
