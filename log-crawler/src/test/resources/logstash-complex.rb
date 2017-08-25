input {
    file {
        path => "c:/work/temp/1/**etl.log*"
        #type => "ETLLogger"
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
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} - ([\w ]+) - \[(?<Thread>.+)\] (?<Message>.+)' }
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

output {
#if [type] == "some-file-name" {
    #some output here
#}
    # elasticsearch {
	# 	hosts => ["localhost:9200"]
	# 	index => "logs-etl-logger-%{+YYYY.MM.dd}"
	# 	template => "c:/work/temp/1/EtlLogger-es-template.json"
	# 	template_overwrite => true
	# 	#document_id => "document_id_if_needed"
	# }
    # Next lines are only for debugging.
    stdout { codec => rubydebug }
    file {path => "c:/work/temp/1/etlLogger.result" codec => rubydebug}
}
