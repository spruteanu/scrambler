package org.prismus.scrambler.test;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.ValuePredicate;
import org.prismus.scrambler.value.ValueDefinition;

import java.util.List;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class BoxTestSuite {
    private Object inspected;
    private ValueDefinition valueDefinition;

    public BoxTestSuite of(String method, Class... args) {
        return this;
    }

    public BoxTestSuite of(String method, Value... args) {
        return this;
    }

    public void execute() {

    }

    public class VoidMethodSuite {
        private String method;
        private List<Value> args;
        private ValueDefinition valueDefinition;

        public VoidMethodSuite scanDefinitions(String... definitions) {
            return this;
        }

        public VoidMethodSuite usingDefinitions(String definition, String... definitions) {
            return this;
        }

        public VoidMethodSuite thrown(Exception expected) {
            return this;
        }

        public VoidMethodSuite thrown(ValuePredicate expected) {
            return this;
        }

        public VoidMethodSuite expect(String field, ValuePredicate valuePredicate) {
            return this;
        }

        public BoxTestSuite end() {
            return BoxTestSuite.this;
        }

        void execute() {

        }

    }

    public class MethodSuite extends VoidMethodSuite {
        private String method;
        private List<Value> args;
        private ValueDefinition valueDefinition;

        public MethodSuite scanDefinitions(String... definitions) {
            return this;
        }

        public MethodSuite usingDefinitions(String definition, String... definitions) {
            return this;
        }

        public MethodSuite thrown(Exception expected) {
            return this;
        }

        public MethodSuite thrown(ValuePredicate expected) {
            return this;
        }

        public MethodSuite expect(String field, ValuePredicate valuePredicate) {
            return this;
        }

        public MethodSuite expect(ValuePredicate valuePredicate) {
            return this;
        }

        void execute() {

        }

    }

}
