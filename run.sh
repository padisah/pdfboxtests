#/bin/sh
mvn package
java -jar pdfboxtest-2029/target/pdfboxtest-2029.jar
java -jar pdfboxtest-3001/target/pdfboxtest-3001.jar
echo "Check the difference between out-2029.pdf and out-3001.pdf!"
