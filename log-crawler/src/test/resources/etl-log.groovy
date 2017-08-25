import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.prismus.scrambler.log.Log4jConsumer
import org.prismus.scrambler.log.LogConsumer
import org.prismus.scrambler.log.LogEntry
import org.prismus.scrambler.log.RegexConsumer

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
//log4j {
//    path 'D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/'
//    pattern 'D:/work/tm/bugs/Case124586_BCBSNC/RTETLLogs/TMLogger.cfg'
//    message new EtlMessageConsumer()
//}

log4j {
    path 'D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/'
    pattern '%d %5p %37c - %m%n'
    fileFilter 'TM*.log*'
    message RegexConsumer.of(~/(.*)FileID[: =\)]{1,}\s*(\d+)(.+)\s+(\d+)\s+ms/)
            .groups('Action', 'FileID', 'Execution', 'ExecutionTime')
            .group('Execution', { LogEntry e ->
        if (!e.get('Action')) {
            e.put('Action', e.get('Execution'))
        }
        e.remove('Execution')
    })
}

//parallel()

output('D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs-log.csv',
            Log4jConsumer.DATE, 'Action', 'TransmissionSID', 'PackageID', 'FileID', 'ExecutionTime', Log4jConsumer.THREAD_NAME, LogEntry.SOURCE_INFO, Log4jConsumer.MESSAGE
)

@CompileStatic
class EtlMessageConsumer implements LogConsumer {
    final Pattern executionTimePattern = ~/.+\s+(\w+\s+to process the file)\s+(.+)\s+\((\d+)\).+(\d+) hour\(s\)\s+(\d+) minute\(s\)\s+(.+) second\(s\)./
    final Pattern failedMovePackagePattern = ~/.+ZIP_(.+) to Failed Queue./
    final Pattern failedProcessingPattern = ~/.+\s+(.+)\s+\((\d+)\)/
    final Pattern createdTransmissionPattern = ~/.+:\s+(\d+)\s+for ETL package: (?:ID:|ZIP_)(.*)/
    final Pattern startedProcessingPattern = ~/.+started to process the transmission (.+)\s+\((\d+)\)/

    @Override
    void consume(LogEntry entry) {
        final logValueMap = entry.logValueMap
        final message = logValueMap.get('Message').toString()
        if (message.endsWith('second(s).')) {
            logValueMap.putAll RegexConsumer.toMap(executionTimePattern, message, 'Action', 'PackageID', 'TransmissionSID', 'Hours', 'Minutes', 'Seconds')
            addExecutionTime(entry)
        } else if (message.startsWith('Move failed package')) {
            entry.put 'Action', 'Failed move package'
            logValueMap.putAll RegexConsumer.toMap(failedMovePackagePattern, message, 'PackageID')
        } else if (message.startsWith('Subcomponent')) {
            entry.put 'Action', 'Failed processing'
            logValueMap.putAll RegexConsumer.toMap(failedProcessingPattern, message, 'PackageID', 'TransmissionSID')
        } else if (message.startsWith('Created Transmission')) {
            entry.put 'Action', 'Created Transmission'
            logValueMap.putAll RegexConsumer.toMap(createdTransmissionPattern, message, 'TransmissionSID', 'PackageID')
        } else {
            final map = RegexConsumer.toMap(startedProcessingPattern, message, 'PackageID', 'TransmissionSID')
            if (map) {
                entry.put 'Action', 'Started transmission processing'
                logValueMap.putAll map
            }
        }
    }

    void addExecutionTime(LogEntry entry) {
        long executionTime = Long.parseLong(entry.get('Hours').toString()) * 3600L * 1000L
        executionTime += Long.parseLong(entry.get('Minutes').toString()) * 60L * 1000L
        executionTime += Long.parseLong(StringUtils.remove(entry.get('Seconds').toString(), '.'))
        entry.put('ExecutionTime', executionTime)
    }
}
