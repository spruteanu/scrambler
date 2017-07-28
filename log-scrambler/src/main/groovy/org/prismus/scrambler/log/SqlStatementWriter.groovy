package org.prismus.scrambler.log

import groovy.sql.Sql

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class SqlStatementWriter {
    Sql sql
    String separator = ';'
    String mssqlSeparator = '\\s+go\\s+'
//
//    protected def executeStatement(String statement, Map<String, ?> propertyValues = null) {
//        return sql.execute(statement)
//    }
//
//    protected def executeStatement(String statement, Map<String, ?> propertyValues = null) {
//        final boolean executeStatement = checkExecuteStatement(statement)
//        if (executeStatement) {
//            log.debug("Executing statement: ${statement}")
//            try {
//                executeStatement = super.executeStatement(statement, propertyValues)
//            } catch (Exception e) {
//                if (statement.toLowerCase().contains('drop constraint')) {
//                    log.warn("Ignoring exception occurred on executing statement: ${statement}, exception message: ${e.getMessage()}")
//                } else {
//                    throw e
//                }
//            }
//        }
//        executeStatement
//    }

    final def write(def entity) {
        return doWrite(entity.text)
    }

    final def write(String entity) {
        String sqlStatements = entity
        final file = new File(entity)
        if (file.exists()) {
            sqlStatements = file.text
        }
        return doWrite(sqlStatements)
    }

    protected int doWrite(String sqlStatements, Map<String, ?> propertyValues = null) {
        assert sql: 'Sql object is not defined'
        int results = 0
        for (statement in splitStatements(sqlStatements)) {
            statement = statement.trim()
            if (statement) {
                try {
                    executeStatement(statement)
                } catch (Exception e) {
                    throw new RuntimeException("Failed execute statement: ${statement}, of: ${sqlStatements}", e)
                }
                results++
            }
        }
        return results
    }

    protected String[] splitStatements(String sqlStatements) {
        final sqlSplitPattern = Pattern.compile(mssqlSeparator, Pattern.CASE_INSENSITIVE)
        final splitStatements
        if (sqlSplitPattern.matcher(sqlStatements).find()) {
            splitStatements = sqlSplitPattern.split(sqlStatements)
        } else {
            splitStatements = sqlStatements.split(separator)
        }
        return splitStatements
    }

    final def write(Collection entities) {
        def int results = 0
        for (entity in entities) {
            results += write(entity)
        }
        return results
    }

    final def flush() {
    }

    final def close() {
    }

    private Set<String> existingTables

    def schemaModificationPatterns = [
            Pattern.compile("alter\\s+table\\s+(\\w+).*", (int) (Pattern.CASE_INSENSITIVE) | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("drop\\s+table\\s+(\\w+).*", (int) (Pattern.CASE_INSENSITIVE) | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("create\\s+table\\s+(\\w+).*", (int) (Pattern.CASE_INSENSITIVE) | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("create\\s+view\\s+(\\w+).*", (int) (Pattern.CASE_INSENSITIVE) | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("create\\s+index\\s+(?:\\w+)\\s+on\\s+(\\w+).*", (int) (Pattern.CASE_INSENSITIVE) | Pattern.MULTILINE | Pattern.DOTALL),
    ]

    boolean checkExecuteStatement(String statement) {
        boolean executeStatement = true
        for (pattern in schemaModificationPatterns) {
            try {
                final Matcher matcher = pattern.matcher(statement)
                if (matcher.matches() && matcher.groupCount() > 0) {
                    final String tableName = matcher.group(1).trim()
                    final boolean tableExists = checkTableExists(tableName)
                    if (statement.toLowerCase().contains('drop')) {
                        executeStatement = tableExists
                    }
                    break
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed matching table from statement: ${statement}", e)
            }
        }
        executeStatement
    }

    def checkTableExists(String tableName) {
        if (existingTables == null) {
            existingTables = getExistingTables()
        }
        existingTables.contains(tableName.toUpperCase())
    }

//    Set<String> getExistingTables() {
//        DbTableBatchConsumer.getExistingTables(sql)
//    }

}
