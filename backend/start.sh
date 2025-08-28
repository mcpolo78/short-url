#!/bin/sh
echo "---"
echo "🚀 Starting Container..."
echo "---"
echo "WHOAMI: $(whoami)"
echo "PWD: $(pwd)"
echo "PORT: $PORT"
echo "JAVA_HOME: $JAVA_HOME"
echo "---"
echo "Listing files in current directory:"
ls -la
echo "---"
echo "Listing files in /app/target:"
ls -la /app/target
echo "---"
echo "Final Java command:"
echo "java -Dserver.port=$PORT -Dspring.profiles.active=railway -jar /app/target/*.jar"
echo "---"

# Execute the Java application
# The 'exec' command replaces the shell process with the Java process
exec java -Dserver.port=$PORT -Dspring.profiles.active=railway -jar /app/target/*.jar
