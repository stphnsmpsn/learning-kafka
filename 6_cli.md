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

## Consumers (`kafka-console-consumer.sh`)

Running the following command will not yield any results: 
```
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic
```
This is because the kafka-console-consumer does not read all of the topics. It only reads from the point when you launch it, intercepting new messages... So, in a new terminal, lets produce to the topic.

As topics are produced, they appear in the consumer console!

If we do need to consume all messages in a topic, we can do so as follows:
```
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
```

## Kafka Consumers in Group (`kafka-console-consumer.sh`)

Here, we will create a producer, and two consumers for a topic. Messages send from the producer will be spread across producers. 

Note: a single partition is never shared across consumers within the same group unless a consumer goes offline. The number of partitions a topic has has to equal the maximum number of consumers in a group that can feed from a topic. 
```
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-first-application
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-first-application
```

Also worth noting... the from-beginning option will only work as you might expect if the group has not yet committed its offset to Kafka... That is to say, running:

```
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-second-application --from-beginning
```
Will list all messages in a topic. However, if we stop this consumer and start it again:
```
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-second-application --from-beginning
```
It will only list new messages since we have stopped it. 

## Kafka Consumer Groups (`kafka-consumer-groups.sh`)

### Listing Consumer Groups
To list consumer groups, issue the following command:
```
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --list
```
Notice that any consumers that were run without the `--group` option have created consumer groups with unique identifiers: `console-consumer-#####`. 

### Describing Consumer Groups
```
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group my-second-application
```
Which should give the following output: 
>            GROUP             TOPIC     PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG  CONSUMER-ID  HOST  CLIENT-ID
>     my-first-application  first_topic     0            0               0          0       -         -       -
>     my-first-application  first_topic     1            1               1          0       -         -       -
>     my-first-application  first_topic     2            1               1          0       -         -       -

**LAG** tells us how many messages a consumer is behind processing. If there are no unprocessed messages, lag will be 0 and the **CURRENT-OFFSET** will match the **LOG-END-OFFSET**. 

The **CONSUMER-ID** field will be populated if there are active consumers running. 

### Resetting Offsets

Resets offsets of a defined consumer group. Supports one consumer group at a time and instances should be inactive.
We have several options for resetting offsets:
1. --to-datetime
2. --by-period
3. --to-earliest
4. --to-latest
5. --shift-by
6. --from-file
7. --to-current

We must also specify the topic for which we want to reset the offsets. The command will look like:
```
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-first-application --reset-offsets --to-earliest --execute --topic first_topic
```
And the output:
>       GROUP                   TOPIC          PARTITION  NEW-OFFSET     
>       my-first-application    first_topic    0          0              
>       my-first-application    first_topic    2          0              
>       my-first-application    first_topic    1          0

## Additional CLI Options
The CLI has many options, but here are several others that are commonly used: 

## Producer with Keys
```
kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
```
>       key,value
>       another key,another value

## Consumer with Keys
```
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,
```

## UI Options
1. https://github.com/yahoo/CMAK
2. Kafka Tools 

## CLI Alternative

[KafkaCat](https://github.com/edenhill/kafkacat) is an open-source alternative to using the Kafka CLI, created by Magnus Edenhill.

Read more [here](https://medium.com/@coderunner/debugging-with-kafkacat-df7851d21968)

## Quiz 2: Quiz on CLI

1. The `kafka-topics` CLI needs to connect to...
   - [x] Zookeeper
   - [] Kafka

2. The `kafka-console-producer` CLI needs to connect to...
   - [x] Kafka
   - [ ] Zookeeper

3. If I produce to a topic that does not exist, by default...
   - [ ] I will see an **ERROR** and my producer will not work
   - [x] I will see a **WARNING** and Kafka will auto create the topic

4. When a topic is auto-created, how many partitions and replication factor does it have by default?
   - [ ] Partitions: 3, Replication Factor: 1
   - [x] Partitions: 1, Replication Factor: 1
   - [ ] Partitions: 3, Replication Factor: 3

   Note: These can be controlled by the `settings.num.partitions` and `default.replication.factor` properties. 

5. kafka-console-consumer...
   - [ ] does not use a group ID
   - [ ] always uses the same group ID
   = [x] uses a random group ID

6. I should override the group.id for `kafka-console-consumer` using...
   - [x] --group mygroup
   - [ ] --property group.id=mygroup

7. I perform operations on the consumer offsets using...
   - [ ] kafka-console-consumer
   - [x] kafka-consumer-groups
