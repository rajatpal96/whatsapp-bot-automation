# script to download and install agent in shell

rm -rf /home/thehobbit/Desktop/agent
mkdir -p /home/thehobbit/Desktop/agent
cd /home/thehobbit/Desktop
rm scrappy-1.0-bundle.zip
wget "https://s3-ap-southeast-1.amazonaws.com/walkinapp.public/scrappy-1.0-bundle.zip"
unzip scrappy-1.0-bundle.zip
cd agent
java -jar -Dlog4j.configuration=file:"/home/thehobbit/Desktop/agent/log4j.properties" scrappy-1.0.jar /home/thehobbit/Desktop/agent/config.properties
