import org.prismus.scrambler.log.*

log4j {
    path 'c:/work/temp/Case122498_BCBSNC/*.log*'
    pattern '%d %5p %37c [%t] - %m%n'
    message {
        if (get(Log4jConsumer.EVENT_CATEGORY).startsWith('com.edifecs.shared.filestore')) {
            if (get('Message').contains('FileID')) {
                match 'Message', ~/(?<Action>.*)FileID[: =\)]{1,}\s*(?<FileID>\d+)(?<Execution>.+)\s+(?<ExecutionTime>\d+)\s+ms/, {
                    if (!get('Action')) {
                        put 'Action', get('Execution')
                    }
                    remove 'Execution'
                }
            } else if (get('Message').endsWith('ms')) {
                match 'Message', ~/(?<Action>.*);\s+(?:\w+\s*)*:\s(?<ExecutionTime>\d+)\s+ms/, {
                    if (!get('Action')) {
                        put 'Action', get('Execution')
                    }
                    remove 'Execution'
                }
            }
        }
    }
}

output 'c:/work/temp/Case122498_BCBSNC/traces.csv', {
    columns 'Date', 'FileID', 'ExecutionTime', 'Action', 'Thread', 'Source', 'Message'
    fieldSeparator '"'
}
