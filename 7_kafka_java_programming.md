# Section 7 - Kafka Java Programming

## Quiz 3: Kafka Java Programming

1. I should block the producer.send() by adding .get() at the end
   - [x] No!
   - [] Yes!

Do not block your .send(). Instead, make sure to .close() your producer before shutting down your application

2. Producing with a key allows to...
   - [] add more information to my message
   - [x] ensure the messages that have the same keys go to the same partition (will be read by same consumer)

3. If I don't use a key (or set key=null)
   - [] the messages will be sent always to the same partition
   - [x] the messages will be sent to all partitions in round-robin fashion

4. To allows consumers in a group to resume at the right offset, I need to set
   - [] auto.offset.resets
   - [x] group.id

5. When my consumers have the same group.id
   - [x] they will read from mutually-exclusive partitions
   - [] they will all read all the partitions 

6. If a consumer joins or leaves a group
   - [] nothing happens 
   - [x] a rebalance happens to assign parittions to all the consumers

7. We usually use .assign() and .seek()  for..
   - [] normal consumer operations
   - [x] replay capability at a certain offset

8. Can a Kafka client version 0.11 send and read data from a 2.0 broker?
   - [x] Yes
   - [] No

   Kafka brokers have backward compatibility enabled

9. Can a Kafka Client v2.0 talk to a Kafka Broker v1.0?
   - [x] Yes 
   - [] No

   Kafka brokers have forward compatibility enabled
