# Section 4 - Kafka Theory

## Topics, Partitions, and Offsets

### Topics

A particular **stream** of data.
* Similar to table in a databse (without all the constraints)
* You can have as many topics as you want
* A topic is identified by its **name**

### Partitions

Topics are split in **partitions**
* Each partition is ordered
* Each message witin a partition gets an incremental id, called **offset**

 Must specify when creating a topic how many partitions it will have.
* Can change later

Paritions can have different number of messages

### Offsets

Only has a meaning for a specific parition
* P0O0 != P1O1
* Order is guaranteed only within a partition (not across partition)

### Other

* Data is kept only for a limied time (default is one week)
* Offsets continue incrementing (never go back to 0)
* Once data is written to a partition **it can never be changed** (immutable)
* Data is sent randomly to a parititon unless a key is provided

## Brokers and Topics

### Brokers

What holds the topics/partitions? - The broker!
* A kafka **cluster** is composed of multiple brokers (servers)
* Each broker is identified with its ID (integer)
* Each broker container certain topic partitions (some but not all of the data - Kafka is distributed)
* After connected to any broker (called a bootstrap broker), you will be connected to the entire cluster
* A good number to get started is 3 brokers, but some big clusters have over 100

### Brokers and Topics

Topics are spread across brokers. 

## Topic Replication

Any distributed system in **big data** world needs to have replication. If a machine goes down, the 'thing' still has to work!

* Topics should have a replication factor (usually between 2 and 3), with 3 being the gold standard. 
* This way, if a broker is down, another broker can serve the data.

### Leader for a partition

At any one time, only ONE broker can be a leader for a given partition
* Only that leader can receive and serve data for a partition (other brokers will synchronize the data)
* Each partition has one leader and multiple ISRs (in-sync replicas)

**Zookeeper** decides replicas / ISR. If leader broker goes down, election happens
* ISR becomes leader
* If down broker comes back, it will try to become the leader again (after replicating the data)

## Producers and Message Keys

### Producers

Producers write data to topics (which are made of partitions)
* Load is balanced to many brokers thanks to the number of partitions
* Producers automatically know which broker/partition to write to (do not need to specify)
* If data is sent wihtout a key, it will be sent round-robin to different brokers (producer does load-balancing)
* In case of broker failures, producers automatically recover

Producer can choose to receive acknowledgement of data writes
* **acks=0:** Producer won't wait for ack (possible data loss)
* **acks=1:** Producer will wait for leader ack (limited data loss)
* **acks=all:** Leader + (all) replicas acknowledgement (no data loss)

### Message Keys

Producers can choose to send a key with the message (string, number, etc...)
* If key=null, data is sent round-robin across brokers
* If a key is sent, all messages for that key will always go to the same partition
* A key is sent if you need message ordering for a specific field (ex: truck_id)
* Mechanism of key->partition is determined using **hashing** which depends on number of partitions. 
* You do not specifiy which partition a key goes to, but only that a key always goes to the same partition

## Consumers and Consumer Groups

### Consumers

Consumers read data from a topic (identified by its name)
* Know which broker to read from (automaticall)
* Know how to recover in event of broker failures
* Data is read in order **within each partition**

Consumers can read from multiple partitions
* Remember, no guarantee of order across partitions

### Consumer Groups

Consumers read data in consumer groups
* Consumer could be a Java application, etc.. that reads data in groups
* Each consumer within a group reads from exclusive partitions
* If you have more consumers than partitions, some will be inactive

Consumers will automatically use a **GroupCoordinator** and a **ConsumerCoordinator** to assign consumers to a partition


## Consumer Offsets and Delivery Semantics

### Consumer Offsets

Kafka stores the offsets at which a consumer group has been reading (checkmarking / bookmarking)
* The offsets are commited live in a Kafka **topic** named **__consumer_offsets**
* When a consumer in a group has processed data received from Kafka, it should be committing the offsets
* If a consumer dies, it will be able to read back from where it left off thanks to the committed consuimer offsets

### Delivery Semantics 

Consumers choose when to commit offsets.

There are three delivery semantics:
1. At most oncce
   * Offsets are committed as soon as the message is received
   * If the processing goes wrong, the message will be lost (it won't be read again)

2. At least once (usually preferred)
   * Offsets are committed after the message is processed
   * If the processing goes wrong, the message will be read again 
   * This can result in duplicate processing of messages. Need to make sure processing is **indempotent** (i.e. processing messages more than once won't impact your systems)

3. Exactly Once (holy grail)
   * Can only be achieved for Kafka <=> Kafka worlflows using Kafka Streams API (maybe also using Spark, etc...)
   * For Kafka => Externel Systems workflows, use an **idempotent** consumer

```
Idempotent: 
denoting an element of a set which is unchanged in value when multiplied or otherwise operated on by itself.
```

## Kafka Broker Discovery

Every Kafka broker is called a **bootstrap server**
* Means you can connect to one broker and have it tell you how to connect to all other servers in the cluster
* Each broker knows about all brokers, topics, and partitions (partitions)
   * Does not hold all the data but knows about it

## Zookeeper

When we start Kafka, we first have to start **Zookeeper**

* Zookeeper manages brokers (keeps a list of them) 
* Helps in performing leader election for partitions. 
Sends notifications to Kafka in the event of changes
   * new topic
   * broker dies
   * broker comes up
   * deletes topics
   * etc...

Zookeeper by design, in production, operates with an odd number of servers (3,5,7 zookeepers)
* Zookeper has a leader (handles writes)
* Rest of the servers are followers (handle reads)

Kafka > v0.10 does NOTE store consumer offsets. It is completely isolated. Offsets are stored in Kafka topic. 

## Kafka Guarantees

1. Messages are appended to a topic partition in the order they are sent
2. Consumers read messags in the order stored in a topic partition
3. With a replication factor of N, producers and consumers can tolderate up to N-1 brokers being down. This is why a replication factor of 3 is a good idea:
   * Allows for one broker to be taken down for maintenance
   * Allows for another broker to be taken down unexpectedly
4. As long as the number of partitions remains constant for a topic (no new partitions), the same key will always go to the same partition.

## Theory Roundup



## Quiz 1: Quiz on Theory

1. Kafka Topics...
   - [ ] always have one partition
   - [x] can have as many partitions as desired

2. Offsets are only relevant at the level of
   - [ ] the topic
   - [x] the topic partition

3. Once sent to a topic, a message can be modified
   - [ ] true
   - [x] false

4. Brokers are identified by:
   - [ ] a name (string)
   - [x] an ID (number)

5. Every broker
   - [ ] contains all the topics and all the partitions
   - [x] contains only a subset of the topics and partitions

6. If a topic has a replication factor of 3
   - [x] each partition will live on three different brokers
   - [ ] each partition will live on two different brokers
   - [ ] each partition will live on four different brokers
   
7. If a topic has a replication factor of 3, what maximum number of brokers can be stopped without impacting the topic availability?
   - [ ] 1
   - [x] 2
   - [ ] 3

8. Each partition can have only 1 leader and multiple replicas
   - [x] true
   - [ ] false

9. To produce data to a topic, a producer must provice the kafka client with...
   - [x] any broker from the cluster and the topic name 
   - [ ] any broker from the cluster and the topic name and the partitions list
   - [ ] all the brokers from the cluster and the topic name
   - [ ] the list of brokers that have the data, the topic name, and the parititions list

   Very important: you only need to connect to one broker (any broker) and just provide the topic name you want to write to. Kafka Clients will route your data to the appropriate brokers and partitions for you!

10. To get acknowledgement of writes to only the leader, we need to use the conifg...
   - [x] acks=1
   - [ ] acks-0
   - [ ] acks=all

11. To read data from a topic, the following configuration is needed for the consumers:
   - [x] any brokers to connect to, and the topic name
   - [ ] all brokers of the cluster, and the topic name
   - [ ] any brokers, and the list of topic partitions

12. Two consumers that have the same group.id (consumer group id) will read from mutually exclusive partitions
   - [x] true
   - [ ] false

13. Kafka Consumer Offsets are stored in...
   - [ ] Zookeeper
   - [x] Kafka

   This is the case since Kafka 0.9, in the topic __consumer_offsets
