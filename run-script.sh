#!/bin/bash
echo "Run generator"
cd test-generator
mvn clean compile exec:java -Dexec.args="offices.txt 90 ops1.txt ops2.txt ops3.txt"
cd ..
echo "Run analyzer"
cd test-analyzer
mvn clean compile exec:java -Dexec.args="stats-dates.txt stats-offices.txt ops1.txt ops2.txt ops3.txt"
