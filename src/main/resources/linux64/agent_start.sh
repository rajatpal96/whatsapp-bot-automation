# script to download and install agent in shell
cd /home/thehobbit/Desktop/agent
java -jar -Dlog4j.configuration=file:"/home/thehobbit/Desktop/agent/log4j.properties" scrappy-1.0.jar /home/thehobbit/Desktop/agent/config.properties
