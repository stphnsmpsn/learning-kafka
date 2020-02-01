sudo mkdir -p /opt/learning-kafka/data
docker run -it -v /opt/learning-kafka/data:/opt/kafka_2.13-2.4.0/data --network="host" learning-kafka /bin/bash
