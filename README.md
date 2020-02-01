# learning-kafka
My journey through the Apache Kafka Series on Udemy

## Installing Docker: 

On my Ubuntu 18.04 LTS EC2 instance, I use the following: 

```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
apt-cache policy docker-ce
sudo apt-get install -y docker-ce
sudo systemctl status docker
sudo usermod -aG docker ${USER}

```

After installing, test the installation by issuing the command:

```
docker run hello-world
```

If you get the message:
```
...permission denied while trying to connect to the Docker daemon socket...
```

You may need to restart docker, or your machine.

## Building the Image

To build your 'learning-kafka' image, run:
```
docker build . -t learning-kafka
```

There are currently a few 'bugs' in my Docker container, namely I have not figured out how to use the docker-command.sh to successfully launch Zookeeper and Kafka so, for now, I am simply execing into the container and running the script manually. 

Also, I currently share the host network with the container. This is just becuase I am currently new to Kafka and do not have a solid Docker strategy for using it. Eventually, I will make this better. 

## Running The Container

```
sudo chmod +x start.sh
./start.sh
```

Now you are inside the container. Start Kafka by running:
```
/docker-command.sh
```

## A Few Notes

1. I am unsure why I can not get this entrypoint to execute from the RUN directive in the Dockerfile.
2. I will be completing the course inside of my container as to not mess up anything on my host OS. 
3. I will modify the Dockerfile (if needed) as the course progresses so that I can easily reproduce my entire environment and share it with others.
4. We want our data to be persistent so the start script creates a folder on our host machine which is mounted into our container when we run it. You can see in the Dockerfile, we modify the `dataDir` property inside of `config/zookeeper.properties` and the `logDirs` property inside of `config/server.properties` to use the directory we mounted rather than the default `/tmp/zookeeper`, and `/tmp/kafka-logs` respectively. 
