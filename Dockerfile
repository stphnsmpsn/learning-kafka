FROM ubuntu:latest

RUN apt-get update && apt-get install -q -y \
    vim \
    openjdk-8-jdk \
    wget

RUN cd opt \
    && wget http://apache.forsale.plus/kafka/2.4.0/kafka_2.13-2.4.0.tgz \
    && tar -xvf kafka_2.13-2.4.0.tgz

RUN echo "export PATH=/opt/kafka_2.13-2.4.0/bin:$PATH" >> /root/.bashrc

RUN sed -i 's/dataDir=\/tmp\/zookeeper/dataDir=\/opt\/kafka_2.13-2.4.0\/data\/zookeeper/g' /opt/kafka_2.13-2.4.0/config/zookeeper.properties
RUN sed -i 's/log.dirs=\/tmp\/kafka-logs/log.dirs=\/opt\/kafka_2.13-2.4.0\/data\/kafka/g' /opt/kafka_2.13-2.4.0/config/server.properties
RUN sed -i 's/export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"/export KAFKA_HEAP_OPTS="-Xmx256M -Xms128M"/g' /opt/kafka_2.13-2.4.0/bin/kafka-server-start.sh

# Kafka Default Ports:
EXPOSE 9092 

# Zookeeper Default Ports:
# for client connections;
EXPOSE 2181 
# for follower(other zookeeper nodes) connections;
EXPOSE 2888 
# for inter nodes connections;
EXPOSE 3888 

# copy command and entrypoint
COPY docker-command.sh /
RUN chmod +x /docker-command.sh

# set cmd and entrypoint
#CMD ["/docker-command.sh"]