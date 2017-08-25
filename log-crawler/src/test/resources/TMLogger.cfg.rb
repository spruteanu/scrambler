input {
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**TM.log*"
        type => "TMLogger"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**ecdatabase.log*"
        type => "TMSQLLogger"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**etl.log*"
        type => "ETLLogger"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**BusinessItemService.log*"
        type => "BISLogger"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**trackinginfo.log*"
        type => "trackinginfo"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**hibernate.log*"
        type => "HibernateFileAppender"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**sql.log*"
        type => "sql"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**sqltiming.log*"
        type => "sqltiming"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**jdbc.log*"
        type => "jdbc"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**edifecs-security-audit.log*"
        type => "securityAuditAppender"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
    file {
        path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/nc-fs/**workflow.log*"
        type => "workflowAppender"
        start_position => "beginning"
        sincedb_path => "/dev/null"
        codec => multiline {
            pattern => "^\d"
            what => "previous"
			negate => true
        }
    }
}

filter {
if [type] == "TMLogger" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\w ]{5,}) %{JAVACLASS:EventCategory{37,} - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
    mutate {
        strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
    grok {
        match => [ 'Message', '(?<Action>.*)FileID[: =\)]{1,}\s*(?<FileID>\d+)(?<Execution>.+)\s+(?<ExecutionTime>\d+)\s+ms' ]
    }

if [Action] == "" {
    mutate {
        copy => { 'Execution' => 'Action' }
    }
}
    mutate {
        remove_field => [ 'Execution' ]
    }
if [ExecutionTime] {
    mutate {
      convert => { 'ExecutionTime' => 'integer' }
    }
}
}
if [type] == "TMSQLLogger" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\w ]{5,}) %{JAVACLASS:EventCategory{37,} - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "ETLLogger" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} - (?<Priority>[\w ]+) - \[(?<Thread>.+)\] (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
    if 'Created Transmission:' in [Message] {
        grok {
            match => [ "Message", '.+:\s+(?<TransmissionSID>\d+)\s+for ETL package: (?:ID:|ZIP_)(?<PackageID>.*)' ]
        }
        mutate { add_field => ['Action', 'Created Transmission'] }
    } else if 'Move failed package' in [Message] {
        grok {
            match => [ "Message", '.+ZIP_(?<PackageID>.+) to Failed Queue.' ]
        }
        mutate { add_field => ['Action', 'Failed move package'] }
    } else if 'Subcomponent' in [Message] {
        grok {
            match => [ "Message", '.+\s+(?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\)' ]
        }
        mutate { add_field => ['Action', 'Failed processing'] }
    } else if 'second(s).' in [Message] {
        grok {
            match => [ "Message", '.+\s+(?<Action>\w+\s+to process the file)\s+(?<PackageID>.+)\s+\((?<TransmissionSID>\d+)\).+%{NUMBER:Hours:int} hour\(s\)\s+%{NUMBER:Minutes:int} minute\(s\)\s+%{NUMBER:Seconds:float} second\(s\).' ]
        }
        ruby {            
            code => "event.set('ExecutionTime', event.get('Hours')*3600 + event.get('Minutes')*60 + event.get('Seconds'))"
        }
        mutate {
            remove_field => [ 'Hours', 'Minutes', 'Seconds']
        }
    }
}
if [type] == "BISLogger" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\w ]{5,}) %{JAVACLASS:EventCategory{37,} - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
    mutate {
        strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "trackinginfo" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\w ]{5,}) %{JAVACLASS:EventCategory{37,} - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "HibernateFileAppender" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} - (?<Priority>[\w ]+) - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "sql" {
    grok {
        match => { "message" => '-----> (?<Date>\w+-\w+-\w+ \w+:\w+:\w+.\w+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss.SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "sqltiming" {
    grok {
        match => { "message" => '-----> (?<Date>\w+-\w+-\w+ \w+:\w+:\w+.\w+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss.SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "jdbc" {
    grok {
        match => { "message" => '(?<Date>\w+-\w+-\w+ \w+:\w+:\w+.\w+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss.SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "securityAuditAppender" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date}\\t(?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
if [type] == "workflowAppender" {
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\w ]{5,}) %{JAVACLASS:EventCategory{37,} - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
  mutate {
    strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }
}
}

output {
#if [type] == "some-file-name" {
    #some output here
#}
    elasticsearch {
        hosts => ["localhost:9200"]
        index => "logs-nc-fs-%{+YYYY.MM.dd}"
        template => "D:/work/proj/scrambler/log-crawler/src/main/resources/es-logstash-template.json"
        template_overwrite => true
        #document_id => "document_id_if_needed"
    }
    # Next lines are only for debugging.
    stdout { codec => rubydebug }
    # file {path => "D:/work/tm/bugs/Case124586_BCBSNC/NC-archiver-logs/tmLogger.result" codec => rubydebug}
}
