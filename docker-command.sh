mkdir -p /opt/kafka_2.13-2.4.0/data/zookeeper
mkdir -p /opt/kafka_2.13-2.4.0/data/kafka
mkdir -p /opt/kafka_2.13-2.4.0/data/logs
nohup zookeeper-server-start.sh /opt/kafka_2.13-2.4.0/config/zookeeper.properties > /opt/kafka_2.13-2.4.0/data/logs/zookeeper.log 2>&1 &
nohup kafka-server-start.sh /opt/kafka_2.13-2.4.0/config/server.properties > /opt/kafka_2.13-2.4.0/data/logs/kafka.log 2>&1 &
sleep 5
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1
cd /opt/learning-kafka
mvn clean package
java -cp target/learning-kafka-jar-with-dependencies.jar dev.stephensampson.kafka.Main 2>&1 | tee /opt/kafka_2.13-2.4.0/data/logs/java_app.log