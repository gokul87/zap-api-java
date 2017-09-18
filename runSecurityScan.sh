cd ZAP_2.6.0
java -jar zap-2.6.0.jar -daemon -host localhost -port 8091 &
sleep 15
cd ..
mvn test
pkill -9 -f zap-2.6.0.jar
