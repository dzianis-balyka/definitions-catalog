#!/usr/bin/env bash
gnome-terminal -- /usr/local/etc/java-soft/apache-cassandra-3.11.4/bin/cassandra -f
sleep 20s
gnome-terminal -- /usr/local/etc/java-soft/apache-cassandra-3.11.4/bin/cqlsh
#REM START "es-jg" /D G:\javasoft\elasticsearch-6.2.4\bin elasticsearch.bat