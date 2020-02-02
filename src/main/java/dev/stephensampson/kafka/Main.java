package dev.stephensampson.kafka;

import dev.stephensampson.kafka.Producer;
import dev.stephensampson.kafka.config.AutoOffsetResetConfig;

public class Main {

    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String GROUP_ID = "java-kafka-application";
    private static final String[] TOPICS = new String[] {"first_topic"};
    private static final int NUM_CONSUMERS = 3;

    public static void main(String[] args) {

        for(String topic : TOPICS){
            (new Thread(new Producer(BOOTSTRAP_SERVER, topic))).start();
        }

        for(int id = 0; id < NUM_CONSUMERS; id++){
            (new Thread(new Consumer(id, BOOTSTRAP_SERVER, GROUP_ID, AutoOffsetResetConfig.EARLIEST, TOPICS))).start();
        }

    }   

}
