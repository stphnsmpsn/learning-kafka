# Section 6 - Kafka CLI

## Topics (`kafka-topics.sh`)

### Creating Topics 

The following command will create a Kafka Topic with a specific name, number of partitions, and replication factor. 

Note: you can not have a ${REPLICATION_FACTOR} > ${NUM_BROKERS}

```
kafka-topics.sh --bootstrap-server localhost:9092 --topic ${TOPIC_NAME} --create --partitions ${NUM_PARTITIONS} --replication-factor ${REPLICATION_FACTOR}
```

Until we learn how to create a cluster, we must use replication factor of `1`. 

Example Command: 
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1
```

### Listing Topics

How do we know the topic was created? Execute the following command:
```
kafka-topics.sh --bootstrap-server localhost:9092 --list
```
The output should be:

> first_topic

### Learning More About a Topic
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --describe
```
Should produce the output:
>        Topic: first_topic      PartitionCount: 3       ReplicationFactor: 1    Configs: segment.bytes=1073741824
>        Topic: first_topic      Partition: 0    Leader: 0       Replicas: 0     Isr: 0
>        Topic: first_topic      Partition: 1    Leader: 0       Replicas: 0     Isr: 0
>        Topic: first_topic      Partition: 2    Leader: 0       Replicas: 0     Isr: 0

### Deleting a Topic
This will have no impact if delete.topic.enable is not set to true (default is `true`)
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --delete
```

## Producers (`kafka-console-producer.sh`)

First, let's recreate our deleted topic:
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1
```

### Using Default Properties

And now, we can use the kafka console to produce. First:
```
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
```
If the command is sucessful, we should see the carat: 
> `>`

Let's produce some data:

>       >hello Stephane
>       >awesome course!
>       >learning Kafka
>       >just another meesage :)
Press CTRL+C to break form the console. 

### Specifying Properties
```
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all
```

>       >some message that is acked
>       >just for fun
>       >fun learning!

### Sending Messages to Inexistent Topics
```
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic new_topic
```
This produces a warning because ther leader is not available, but the producer is able to recover from errors. It waits until the leader is available and then produces the message. 
>       >hey this topic does not exist!
>       [2020-02-01 22:31:03,832] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
>       >
Now, sending messages to the topic no longer produces a warning
>       >another message

Listing the topics will now show our newly created topic:
```
kafka-topics.sh --bootstrap-server localhost:9092 --list
```
The output should be:

>        first_topic
>        new_topic

Getting more information about this new topic:
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic new_topic --describe
```
>           Topic: new_topic        PartitionCount: 1       ReplicationFactor: 1    Configs: segment.bytes=1073741824
>           Topic: new_topic        Partition: 0    Leader: 0       Replicas: 0     Isr: 0
Shows us that the topic was created but with 1 partition and a replications factor of 1. This is generally not desirable  so **always create topic before producing to it**. 

Note: **Default properties can be changed in server.properties**

## Consumers (`kafka-console.consumer.sh`)

Runnin the following command will not yield any results: 
```
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic
```
This is because the kafka-console-consumer does not read all of the topics. It only reads from the point when you launch it, intercepting new messages... So, in a new terminal, lets produce to the topic.

As topics are produced, they appear in the consumer console!

If we do need to consume all messages in a topic, we can do so as follows:
```
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
```