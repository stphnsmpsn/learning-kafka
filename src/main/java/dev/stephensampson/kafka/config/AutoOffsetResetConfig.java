package dev.stephensampson.kafka.config;

public enum AutoOffsetResetConfig {
        EARLIEST("earliest"), LATEST("latest"), NONE("none");

        private final String value;

        AutoOffsetResetConfig(String value){
            this.value = value;
        }

        public String value(){
            return this.value;
        }

    }