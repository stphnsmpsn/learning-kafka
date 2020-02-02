FROM ubuntu:latest

RUN apt-get update && apt-get install -q -y \
    vim \
    openjdk-8-jdk \
    openjdk-8-jre \
    maven \
    wget

RUN cd opt \
    && wget http://apache.forsale.plus/kafka/2.4.0/kafka_2.13-2.4.0.tgz \
    && tar -xvf kafka_2.13-2.4.0.tgz

RUN echo "export PATH=/opt/kafka_2.13-2.4.0/bin:$PATH" >> /root/.bashrc
RUN echo "JAVA_HOME= /usr/lib/jvm/java-8-openjdk-amd64" >> /etc/environment
RUN echo "JRE_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre" >> /etc/environment

RUN sed -i 's/dataDir=\/tmp\/zookeeper/dataDir=\/opt\/kafka_2.13-2.4.0\/data\/zookeeper/g' /opt/kafka_2.13-2.4.0/config/zookeeper.properties
RUN sed -i 's/log.dirs=\/tmp\/kafka-logs/log.dirs=\/opt\/kafka_2.13-2.4.0\/data\/kafka/g' /opt/kafka_2.13-2.4.0/config/server.properties
RUN sed -i 's/export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"/export KAFKA_HEAP_OPTS="-Xmx256M -Xms128M"/g' /opt/kafka_2.13-2.4.0/bin/kafka-server-start.sh

# Copy Maven project 
COPY src/ /opt/learning-kafka/src
COPY pom.xml /opt/learning-kafka/

# Expose Kafka and Zookeeper Default Ports
EXPOSE 9092 
EXPOSE 2181 
EXPOSE 2888 
EXPOSE 3888 

# copy command and entrypoint
COPY docker-command.sh /
#COPY docker-entrypoint.sh /
RUN chmod +x /docker-command.sh
#RUN chmod +x /docker-entrypoint.sh

# set cmd and entrypoint
#CMD ["/docker-command.sh"]
#ENTRYPOINT ["/docker-entrypoint.sh"]