# realtime-pipeline-kafka-flink
The goal of this project is to build a real-time air pollution data pipeline on a single node cluster, using only opensources frameworks, that:
- [x] collects data with a sensor, 
- [x] utilizes a message queue to receive and deliver the data through Apache Kafka,
- [x] using the Apache Flink to move the data into different sinks (Apache Cassandra and Elasticsearch), as an Extract - Load data flow, 
- [x] create a dashboard that displays the information using Kibana

## **Tools - Frameworks** ##
- Virtual Machine: VirtualBox v5.2.26, 
- Hardware settings: 2 cores, 6GB RAM, 12GB HDD, 
- OS: Ubuntu 18.04 (Debian Linux) 
- Oracle Java v1.80,
- Apache Maven v3.6.0,
- Apache Kafka v2.1-2.1.0,
- Apache Flink v1.7.2,
- Apache Cassandra v3.11.4,
- Elasticsearch v6.6.1,
- Kibana v6.6.1.


## Dataset Information ##
- Simulate an IoT sensor using Java and creation of a 6 features instances (ozone, particullate_matter, carbon_monoxide, sulfure_dioxide, nitrogen_dioxide, time)   every 5 sec.

![IoT_simulation](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/IoT_simul.png)


## Pipeline Architecture ##

![architect](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/architect_system.png)



## **Data Flow** ##
- __IoT sensor simulator:__ data from the sensor, Apache Kafka's Producer

- __Data Flow 1:__ send to Apache Kafka

- __Data Flow 2:__ send to Apache Flink, Apache Kafka's Consumer

- __Data Flow 3:__ send to Apache Cassandra

- __Data Flow 4:__ send to Elasticsearch

- __Visualize__: using Kibana


## **Build, Provision and Deploy the Project** ##
1. Starting Zookeeper(Kafka) in terminal 1: 
	```
	cd /opt/kafka
	bin/zookeeper-server-start.sh config/zookeeper.properties
	```

2. Starting Kafka server in terminal 2: 
	```
	cd /opt/kafka
	bin/kafka-server-start.sh config/server.properties 
	```

3. Creation of topic "iotdata" in terminal 3: 
	```
	cd /opt/kafka
	bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic iotdata 
	```

4. In the same terminal(terminal 3), starting Cassandra: 
	```
	cassandra -f
	```
	
5. Staring Elasticsearch in terminal 4:
	```
	elasticsearch
	```
	
6. Check the Elasticsearch status in terminal 5:
	```
	curl -X GET "localhost:9200" 
	```
	
7. Starting Apache Flink in terminal 6: 
	```
	cd /opt/flink
	bin/start-cluster.sh
	```
   You can check the status of Apache Flink using the web interface: `http://localhos:8081/#/overview`. 
   
8. In the same terminal(terminal 6) starting the  Apache Maven project:
	```
	bin/flink run -c sendtosink.sendtosink /opt/maven-projects/0304pollution/target/pollution-0.1.jar 
	```
	
9. Starting Kafka's Producer in terminal 7, for sending the data to Kafka:
	```
	cd /opt/maven-projects/01pollution
	mvn exec:java -Dexec.mainClass="producer.DataGenerator" 
	```
	
10. Viewing the Flink Plan Visualizer using the `https://flink.apache.org/visualize`:

	![web_flink2](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/web_flink2.png)


11. Viewing the data transfering status of Apache Flink using the web interface: `http://localhos:8081/#/overview`. 

   ![data_cassandra](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/data_cassandra.png)
   

12. Check data status in Apache Cassandra in terminal 8: 
	```
	use pollution;
	select * from sensor6; 
	```
	![data_cassandra](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/data_cassandra.png)
	
13. Check data status in Elasticsearch in terminal 6: 
	```
	curl -XGET 'localhost:9200/iotdata/sensor6/_search?pretty'
	```
	
	![elastic_data](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/elastic_data.png)
	
	
13. Starting Kibana in terminal 9: 
	```
	kibana
	```
	
14. Viewing the data in Kibana using the web interface: `http://localhos:5601`:

	![kibana](https://github.com/ioantsep/realtime-pipeline-kafka-flink/blob/main/images/kibana.png)
	
	
	
	
	
	
