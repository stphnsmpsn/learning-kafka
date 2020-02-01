mkdir -p /opt/kafka_2.13-2.4.0/data/zookeeper
mkdir -p /opt/kafka_2.13-2.4.0/data/kafka
mkdir -p /opt/kafka_2.13-2.4.0/data/logs
nohup zookeeper-server-start.sh /opt/kafka_2.13-2.4.0/config/zookeeper.properties > /opt/kafka_2.13-2.4.0/data/logs/zookeeper.log 2>&1 &
nohup kafka-server-start.sh /opt/kafka_2.13-2.4.0/config/server.properties > /opt/kafka_2.13-2.4.0/data/logs/kafka.log 2>&1 &