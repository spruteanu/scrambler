import org.prismus.scrambler.log.Log4jConsumer

log4j {
    path 'D:/work/tm/bugs/Case122498_BCBSNC/TM*.log*'
    pattern '%d %5p %37c [%t] - %m%n'
    message {
        if (get(Log4jConsumer.EVENT_CATEGORY).startsWith('com.edifecs.shared.filestore')) {
            match 'Message', ~/(?<Action>.*)FileID[: =\)]{1,}\s*(?<FileID>\d+)(?<Execution>.+)\s+(?<ExecutionTime>\d+)\s+ms/, {
                if (!get('Action')) {
                    put 'Action', get('Execution')
                }
                remove 'Execution'
            }
        }
    }
}

output 'D:/work/tm/bugs/Case122498_BCBSNC/trace-log.csv', {
    columns 'Date', 'FileID', 'ExecutionTime', 'Action', 'Thread', 'Source', 'Message'
    fieldSeparator '"'
}
