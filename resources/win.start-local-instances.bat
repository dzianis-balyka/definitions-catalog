REM
START "cassandra-jg" /D C:\javasoft\apache-cassandra-3.11.4 .\bin\cassandra -f
TIMEOUT /T 30
REM
START "cqlsh-jg" /D C:\javasoft\apache-cassandra-3.11.4 .\bin\cqlsh.bat
REM START "es-jg" /D G:\javasoft\elasticsearch-6.2.4\bin elasticsearch.bat