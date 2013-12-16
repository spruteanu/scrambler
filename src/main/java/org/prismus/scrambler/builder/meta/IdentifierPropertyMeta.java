package org.prismus.scrambler.builder.meta;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class IdentifierPropertyMeta extends PropertyMeta {
    private String identifierKeyName;
    private short keySequence;

    void setIdentifierKeyName(String identifierKeyName) {
        this.identifierKeyName = identifierKeyName;
    }

    void setKeySequence(short keySequence) {
        this.keySequence = keySequence;
    }

    public String getIdentifierKeyName() {
        return identifierKeyName;
    }

    public short getKeySequence() {
        return keySequence;
    }
}
