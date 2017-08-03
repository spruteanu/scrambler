#!/bin/sh
CP=.:./lib/*

if [ -f $JAVA_HOME/bin/java ] ; then
  JAVA_EXE=$JAVA_HOME/bin/java
else
  JAVA_EXE=java
fi

JAVA_OPTS=""

$JAVA_EXE $JAVA_OPTS -classpath $CP org.prismus.scrambler.log.LogCrawler $*
