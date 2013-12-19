package org.prismus.scrambler.builder.meta;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class PropertyMeta {

    private String name;

    /**
     * DATA_TYPE int => SQL type from java.sql.Types
     */
    private int sqlType;

    /**
     * length in bytes of data
     */
    private Integer length;

    /**
     * NULLABLE short => can it contain NULL.
     * procedureNoNulls - does not allow NULL values
     * procedureNullable - allows NULL values
     * procedureNullableUnknown - nullability unknown
     */
    private Boolean nullable;

    /**
     * REMARKS String => comment describing parameter/column
     */
    private String remarks;

    /**
     * Java class type
     */
    private Class type;

    private Integer precision;

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public int getSqlType() {
        return sqlType;
    }

    void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public Integer getLength() {
        return length;
    }

    void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getNullable() {
        return nullable;
    }

    void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public String getRemarks() {
        return remarks;
    }

    void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Class getType() {
        return type;
    }

    void setType(Class type) {
        this.type = type;
    }

    public Integer getPrecision() {
        return precision;
    }

    void setPrecision(Integer precision) {
        this.precision = precision;
    }

}
