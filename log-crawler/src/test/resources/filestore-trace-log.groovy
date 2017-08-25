import org.prismus.scrambler.log.Log4jConsumer
import org.prismus.scrambler.log.LogEntry
import org.prismus.scrambler.log.RegexConsumer

/**
 * @author Serge Pruteanu
 */
log4j {
    path 'D:/work/tm/bugs/Case122498_BCBSNC'
    pattern '%d %5p %37c [%t] - %m%n'
    fileFilter '*.log*'
    message RegexConsumer.of(~/(.*)FileID[: =\)]{1,}\s*(\d+)(.+)\s+(\d+)\s+ms/)
            .groups('Action', 'FileID', 'Execution', 'ExecutionTime')
            .group('Execution', { LogEntry e ->
        if (!e.get('Action')) {
            e.put('Action', e.get('Execution'))
        }
        e.remove('Execution')
    })
}
output 'D:/work/tm/bugs/Case122498_BCBSNC/trace-log.csv', {
    columns Log4jConsumer.DATE, 'FileID', 'ExecutionTime', 'Action', Log4jConsumer.THREAD_NAME, LogEntry.SOURCE_INFO, Log4jConsumer.MESSAGE
    fieldSeparator '"'
}
