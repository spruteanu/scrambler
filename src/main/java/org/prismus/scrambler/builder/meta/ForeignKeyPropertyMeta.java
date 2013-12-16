package org.prismus.scrambler.builder.meta;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ForeignKeyPropertyMeta extends PropertyMeta {
    private String primaryEntity;
    private String primaryIdentifier;
    private short keySequence;

    public String getPrimaryEntity() {
        return primaryEntity;
    }

    void setPrimaryEntity(String primaryEntity) {
        this.primaryEntity = primaryEntity;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    void setPrimaryIdentifier(String primaryIdentifier) {
        this.primaryIdentifier = primaryIdentifier;
    }

    public short getKeySequence() {
        return keySequence;
    }

    void setKeySequence(short keySequence) {
        this.keySequence = keySequence;
    }
}
