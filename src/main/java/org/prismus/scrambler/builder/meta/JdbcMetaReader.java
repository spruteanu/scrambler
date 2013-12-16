package org.prismus.scrambler.builder.meta;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class JdbcMetaReader {
    static Map<Integer, Class> jdbcTypeClassMap = ImmutableMap.<Integer, Class>builder()
            .put(java.sql.Types.CHAR, String.class)
            .put(java.sql.Types.VARCHAR, String.class)
            .put(java.sql.Types.LONGVARCHAR, String.class)
            .put(java.sql.Types.NUMERIC, BigDecimal.class)
            .put(java.sql.Types.DECIMAL, BigDecimal.class)
            .put(java.sql.Types.BIT, BigDecimal.class)
            .put(java.sql.Types.BOOLEAN, Boolean.class)
            .put(java.sql.Types.TINYINT, Byte.class)
            .put(java.sql.Types.SMALLINT, Short.class)
            .put(java.sql.Types.INTEGER, Integer.class)
            .put(java.sql.Types.BIGINT, Long.class)
            .put(java.sql.Types.REAL, Float.class)
            .put(java.sql.Types.FLOAT, Double.class)
            .put(java.sql.Types.DOUBLE, Double.class)
            .put(java.sql.Types.BINARY, byte[].class)
            .put(java.sql.Types.VARBINARY, byte[].class)
            .put(java.sql.Types.LONGVARBINARY, byte[].class)
            .put(java.sql.Types.DATE, Date.class)
            .put(java.sql.Types.TIME, Date.class)
            .put(java.sql.Types.TIMESTAMP, Date.class)
            .put(java.sql.Types.CLOB, Clob.class)
            .put(java.sql.Types.BLOB, Blob.class)
            .put(java.sql.Types.ARRAY, ArrayList.class)
            .put(java.sql.Types.DATALINK, URL.class)
            .build();

    private DataSource dataSource;
    private String entity;
    private Boolean upperCaseMeta;
    private Boolean detailedMeta;

    public JdbcMetaReader() {
    }

    public JdbcMetaReader(DataSource dataSource, String entity) {
        this(dataSource, entity, null);
    }

    public JdbcMetaReader(DataSource dataSource, String entity, Boolean upperCaseMeta) {
        this.dataSource = dataSource;
        this.upperCaseMeta = upperCaseMeta;
        this.entity = entity;
    }

    public JdbcMetaReader retrieveDetailedMeta(Boolean detailedMeta) {
        this.detailedMeta = detailedMeta;
        return this;
    }

    public void setDetailedMeta(Boolean detailedMeta) {
        this.detailedMeta = detailedMeta;
    }

    public JdbcMetaReader retrieveUpperCaseMeta(Boolean upperCaseMeta) {
        setUpperCaseMeta(upperCaseMeta);
        return this;
    }

    public void setUpperCaseMeta(Boolean upperCaseMeta) {
        this.upperCaseMeta = upperCaseMeta;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public EntityMeta read() throws Exception {
        try {
            final EntityMeta entityMeta = new EntityMeta();
            entityMeta.setName(entity);

            final Map<String, PropertyMeta> propertyMetaMap = readPropertyMeta(dataSource, entity);
            entityMeta.setPropertyList(Collections.unmodifiableList(new ArrayList<PropertyMeta>(propertyMetaMap.values())));
            return entityMeta;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed get table: %s columns and types", entity), e);
        }
    }

    public static EntityMeta readEntityMeta(DataSource dataSource, String entity) throws Exception {
        return readEntityMeta(dataSource, entity, null, null);
    }

    public static EntityMeta readDetailedEntityMeta(DataSource dataSource, String entity) throws Exception {
        return readEntityMeta(dataSource, entity, null, true);
    }

    public static EntityMeta readEntityMeta(DataSource dataSource, String entity,
                                            Boolean upperCaseMeta, Boolean detailedMeta) throws Exception {
        return new JdbcMetaReader(dataSource, entity, upperCaseMeta)
                .retrieveDetailedMeta(detailedMeta)
                .read();
    }

    PropertyMeta readCommonPropertyMeta(ResultSet resultSet, Map<String, PropertyMeta> propertyMetaMap) throws SQLException {
        final String name = resultSet.getString("COLUMN_NAME");
        PropertyMeta propertyMeta = propertyMetaMap.get(name);
        if (propertyMeta == null) {
            propertyMeta = new PropertyMeta();
            propertyMetaMap.put(name, propertyMeta);
            propertyMeta.setName(name);
        }

        final int sqlType = resultSet.getInt("DATA_TYPE");
        propertyMeta.setSqlType(sqlType);

        final Class propertyTypeClass = jdbcTypeClassMap.get(sqlType);
        propertyMeta.setType(propertyTypeClass);

        propertyMeta.setRemarks(resultSet.getString("REMARKS"));
        final short nullable = resultSet.getShort("NULLABLE");
        if (nullable != DatabaseMetaData.procedureNullableUnknown) {
            propertyMeta.setNullable(DatabaseMetaData.procedureNullable == nullable);
        }

        final int columnSize = resultSet.getInt("COLUMN_SIZE");
        if (Arrays.asList(String.class, Date.class).contains(propertyTypeClass)) {
            propertyMeta.setLength(columnSize);
        } else if (Number.class.isAssignableFrom(propertyTypeClass)) {
            propertyMeta.setPrecision(columnSize);
        }
        return propertyMeta;
    }

    Map<String, PropertyMeta> readIdentifierMeta(DatabaseMetaData metadata,
                                                 String entityName,
                                                 Map<String, PropertyMeta> propertyMetaMap) throws SQLException {
        final ResultSet resultSet = metadata.getPrimaryKeys(null, null, entityName);
        try {
            while (resultSet.next()) {
                final IdentifierPropertyMeta propertyMeta = new IdentifierPropertyMeta();
                propertyMeta.setName(resultSet.getString("COLUMN_NAME"));
                propertyMeta.setIdentifierKeyName(resultSet.getString("PK_NAME"));
                propertyMeta.setKeySequence(resultSet.getShort("KEY_SEQ"));
                propertyMetaMap.put(propertyMeta.getName(), propertyMeta);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return propertyMetaMap;
    }

    Map<String, PropertyMeta> readForeignKeyPropertyMap(DatabaseMetaData metadata,
                                                        String entityName,
                                                        Map<String, PropertyMeta> propertyMetaMap) throws SQLException {
        final ResultSet resultSet = metadata.getImportedKeys(null, null, entityName);
        try {
            while (resultSet.next()) {
                final ForeignKeyPropertyMeta propertyMeta = new ForeignKeyPropertyMeta();
                propertyMeta.setPrimaryEntity(resultSet.getString("PKTABLE_NAME"));
                propertyMeta.setPrimaryIdentifier(resultSet.getString("PKCOLUMN_NAME"));
                propertyMeta.setName(resultSet.getString("FKCOLUMN_NAME"));
                propertyMeta.setKeySequence(resultSet.getShort("KEY_SEQ"));
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }
        return propertyMetaMap;
    }

    public boolean isUpperCaseMeta() {
        return Boolean.TRUE.equals(upperCaseMeta);
    }

    @SuppressWarnings({"unchecked"})
    Map<String, PropertyMeta> readPropertyMeta(DataSource dataSource, final String table) throws Exception {
        return (Map<String, PropertyMeta>) JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {
            public Map<String, PropertyMeta> processMetaData(DatabaseMetaData metadata) throws SQLException, MetaDataAccessException {
                final String entityName = getEntityName(metadata, table);
                final Map<String, PropertyMeta> propertyMetaMap = new LinkedHashMap<String, PropertyMeta>();
                if (detailedMeta != null && detailedMeta) {
                    readIdentifierMeta(metadata, entityName, propertyMetaMap);
                    readForeignKeyPropertyMap(metadata, entityName, propertyMetaMap);
                }
                readPropertyMeta(metadata, entityName, propertyMetaMap);
                return propertyMetaMap;
            }
        });
    }

    void readPropertyMeta(DatabaseMetaData metadata,
                          String entityName,
                          Map<String, PropertyMeta> propertyMetaMap) throws SQLException {
        final ResultSet resultSet = metadata.getColumns(null, null, entityName, null);
        try {
            while (resultSet.next()) {
                final PropertyMeta propertyMeta = readCommonPropertyMeta(resultSet, propertyMetaMap);
                propertyMetaMap.put(propertyMeta.getName(), propertyMeta);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }
    }

    String getEntityName(DatabaseMetaData metadata, String table) throws SQLException {
        String entityName = table;
        if (upperCaseMeta != null) {
            if (isUpperCaseMeta()) {
                entityName = table.toUpperCase();
            }
        } else {
            if (metadata.getDriverName().toUpperCase().contains("H2")) {
                entityName = table.toUpperCase();
            }
        }
        return entityName;
    }
}
