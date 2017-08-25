input {
    file {
        path => "c:/work/temp/1/**/TM.log*"
        #type => "fileStoreLog"
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

    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\w ]{5,}) %{JAVACLASS:EventCategory{37,} - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }

    date {
        match => ['Date', 'yyyy-MM-dd HH:mm:ss,SSS']
    }

    mutate {
        strip => "Message"
        remove_field => [ 'message', '@version', 'Date', 'host', 'path']
    }

    if 'multiline' in [tags] {
        mutate {
            remove_field => [ 'tags' ]
        }
        ruby {
            code => "event.set('Exceptions', event.get('Message').scan(/(?:[a-zA-Z$_][a-zA-Z$_0-9]*\.)*[a-zA-Z$_][a-zA-Z$_0-9]*Exception/))"
        }
    }

    grok {
        match => [ 'Message', '(?<Action>.*)FileID[: =\)]{1,}\s*(?<FileID>\d+)(?<Execution>.+)\s+(?<ExecutionTime>\d+)\s+ms' ]
    }

    mutate {
        if ![Action] {
            copy => { 'Execution' => 'Action' }
        }
        remove_field => [ 'Execution' ]
        if [ExecutionTime] {
            convert => { 'ExecutionTime' => 'integer' }
        }
    }
}

output {
#if [type] == "some-file-name" {
    #some output here
#}
    # elasticsearch {
    #     hosts => ["localhost:9200"]
    #     index => "logs-etl-logger-%{+YYYY.MM.dd}"
    #     template => "c:/work/temp/1/EtlLogger-es-template.json"
    #     template_overwrite => true
    #     #document_id => "document_id_if_needed"
    # }
    # Next lines are only for debugging.
    stdout { codec => rubydebug }
    file {path => "c:/work/temp/1/etlLogger.result" codec => rubydebug}
}
