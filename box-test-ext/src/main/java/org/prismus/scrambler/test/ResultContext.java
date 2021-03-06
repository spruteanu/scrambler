package org.prismus.scrambler.test;

import java.util.ArrayList;
import java.util.List;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ResultContext extends TestContext {
    private final List<TestContext> contexts = new ArrayList<TestContext>();

    public ResultContext add(TestContext testContext) {
        contexts.add(testContext);
        return this;
    }

    public List<TestContext> getContexts() {
        return contexts;
    }

}
