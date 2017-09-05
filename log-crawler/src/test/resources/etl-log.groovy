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
log4j {
    path 'D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/'
    pattern 'D:/work/tm/bugs/Case124586_BCBSNC/RTETLLogs/TMLogger.cfg'
    message {
        final message = get('Message') as String
        if (message.endsWith('second(s).')) {
            putAll match(~/.+\s+(?<Action>\w+\s+to process the file)\s+(?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\).+(?<Hours>\d+) hour\(s\)\s+(?<Minutes>\d+) minute\(s\)\s+(?<Seconds>.+) second\(s\)./, message)
            put 'ExecutionTime', toFloat('Hours') * 3600 + toFloat('Minutes') * 60 + toFloat('Seconds')
        } else if (message.startsWith('Move failed package')) {
            put 'Action', 'Failed move package'
            putAll match(~/.+ZIP_(?<PackageID>.+) to Failed Queue./, message)
        } else if (message.startsWith('Subcomponent')) {
            put 'Action', 'Failed processing'
            putAll match(~/.+\s+(?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\)/, message)
        } else if (message.startsWith('Created Transmission')) {
            put 'Action', 'Created Transmission'
            putAll match(~/.+:\s+(?<TransmissionSID>\d+)\s+for ETL package: (?:ID:|ZIP_)(?<PackageID>.*)/, message)
        } else {
            final map = match(~/.+started to process the transmission (?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\)/, message)
            if (map) {
                put 'Action', 'Started transmission processing'
                putAll map
            }
        }
    }
}

log4j {
    path 'D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/TM*.log*'
    pattern '%d %5p %37c - %m%n'
    message match(~/(?<Action>.*)FileID[: =\)]{1,}\s*(?<FileID>\d+)(?<Execution>.+)\s+(?<ExecutionTime>\d+)\s+ms/, {
        group 'Execution', {
            if (!get('Action')) {
                put 'Action', get('Execution')
            }
            remove 'Execution'
        }
    })
}

//parallel()

output'D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs-log.csv',
            Log4jConsumer.DATE, 'Action', 'TransmissionSID', 'PackageID', 'FileID', 'ExecutionTime', Log4jConsumer.THREAD_NAME, LogEntry.SOURCE_INFO, Log4jConsumer.MESSAGE

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
