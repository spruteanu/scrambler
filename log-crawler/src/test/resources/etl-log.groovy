/**
 * @author Serge Pruteanu
 */
log4j {
    path 'D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/'
    pattern 'D:/work/tm/bugs/Case124586_BCBSNC/RTETLLogs/TMLogger.cfg'
    message {
        final message = get('Message') as String
        if (message.endsWith('second(s).')) {
            match 'Message', ~/.+\s+(?<Action>\w+\s+to process the file)\s+(?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\).+(?<Hours>\d+) hour\(s\)\s+(?<Minutes>\d+) minute\(s\)\s+(?<Seconds>.+) second\(s\)./
            put 'ExecutionTime', putFloat('Hours') * 3600 + putFloat('Minutes') * 60 + putFloat('Seconds')
        } else if (message.startsWith('Move failed package')) {
            put 'Action', 'Failed move package'
            match 'Message', ~/.+ZIP_(?<PackageID>.+) to Failed Queue./
        } else if (message.startsWith('Subcomponent')) {
            put 'Action', 'Failed processing'
            match 'Message', ~/.+\s+(?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\)/
        } else if (message.startsWith('Created Transmission')) {
            put 'Action', 'Created Transmission'
            match 'Message', ~/.+:\s+(?<TransmissionSID>\d+)\s+for ETL package: (?:ID:|ZIP_)(?<PackageID>.*)/
        } else {
            if (match('Message', ~/.+started to process the transmission (?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\)/)) {
                put 'Action', 'Started transmission processing'
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
