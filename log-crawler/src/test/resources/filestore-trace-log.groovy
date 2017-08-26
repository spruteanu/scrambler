log4j {
    path 'D:/work/tm/bugs/Case122498_BCBSNC'
    pattern '%d %5p %37c [%t] - %m%n'
    fileFilter 'TM*.log*'
    message match(~/(?<Action>.*)FileID[: =\)]{1,}\s*(?<FileID>\d+)(?<Execution>.+)\s+(?<ExecutionTime>\d+)\s+ms/, {
        group 'Execution', {
            if (!get('Action')) {
                put 'Action', get('Execution')
            }
            remove 'Execution'
        }
    })
}

output 'D:/work/tm/bugs/Case122498_BCBSNC/trace-log.csv', {
    columns 'Date', 'FileID', 'ExecutionTime', 'Action', 'Thread', 'Source', 'Message'
    fieldSeparator '"'
}
