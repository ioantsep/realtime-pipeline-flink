# realtime-pipeline-kafka-flink
The goal of this project is to build a real-time air pollution data pipeline on a single node cluster, using only opensources frameworks, that:
- [x] collects data with a sensor, 
- [x] utilizes a message queue to receive and deliver the data through Apache Kafka,
- [x] using the Apache Flink to move the data into different sinks (Apache Cassandra and Elasticsearch), as an Extract - Load data flow, 
- [x] create a dashboard that displays the information using Kibana

## Tools - Frameworks ##
•	Oracle Java v1.80,
•	Apache Maven v3.6.0,
•	Apache Kafka v2.1-2.1.0,
•	Apache Flink v1.7.2,
•	Apache Cassandra v3.11.4,
•	Elasticsearch v6.6.1,
•	Kibana v6.6.1.



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
1. Sign-in to Google Cloud Platform console and create a new project, project_name = "iotpipeline", project_ID = "iotpipeline-243711'".

2. Creation of a table in BigQuery: "BIG DATA" --> "BigQuery" --> click on projectID --> "CREATE DATASET" with DatasetID = "weatherData" -->  click on "CREATE   TABLE" --> "Source Data" --> "Empty table", "Table type" = "Native table", "Table name" = "weatherDataTable", "Schema" --> "Add field" with 9 features. 

3. Creation of a Pub/Sub topic: "BIG DATA" --> "Pub/Sub" --> "Topics" --> "Enable API" --> "Create a topic", name ="weatherdata" --> "CREATE". 

4. Connect Pub/Sub with BigQuery using Cloud Functions: "COMPUTE" --> "Cloud Functions" --> "Enable API" --> "Create function", "name" = "weatherPubSubToBQ", "Trigger" = "Cloud Pub/Sub", "Topic" = "weatherdata", "Source code" = "Inline editor". In tab "index.js", write the JavaScript code (Node.js 6): [index.js](https://github.com/ioantsep/weather-pipeline/blob/main/index.js) and in the tab "package.json" write code : 	[package.json](https://github.com/ioantsep/weather-pipeline/blob/main/package.json). Then in "Function to execute" = "subscribe" --> "Create".	
	
5. Creation of a storage bucket for Dataflow: "STORAGE" --> "Browser" --> "Create bucket", "iotpipeline-bucket" --> "Create".

6. Dataflow API: "API & Services" --> "Enable API and Services" --> "Welcome to the new API Library", search bar = "Dataflow" --> " Google Dataflow API" --> "Enable".

7. Creation of template in Dataflow: "BIG DATA" --> "Dataflow" --> "CREATE JOB FROM TEMPLATE" with:
 - "Job name" = "dataflow-gcs-to-pubsub4", 
 - "Cloud Dataflow template" = "Text Files on Cloud Storage to Cloud Pub/Sub",
 - "Input Cloud Storage File(s)" με "gs://codelab-iot-data-pipeline-sampleweatherdata/*.json" (public dataset), 
 - "Output Pub/Sub Topic" = "projects/iotpipeline-243711/topics/weatherdata", 
 - "Temporary location" = "gs://iotpipeline-bucket/tmp".
 
   Click on "Run job" and Dataflow starts.
   
   	![dataflow](https://github.com/ioantsep/weather-pipeline-on-GCP/blob/main/Images/dataflow.png)
   
   

8. Checking the Data Flow: BigQuery --> "iotpipeline-243711", Dataset = "weatherData", Table = "weatherDataTable" --> "QUERY TABLE", --> Query editor: 		
	```
	SELECT * FROM `iotpipeline-243711.weatherData.weatherDataTable` LIMIT 1000	
	```
	
	![BigQuery](https://github.com/ioantsep/weather-pipeline-on-GCP/blob/main/Images/BigQuery%20table.png)
	
   
	

9. Creation graphs using Data Studio: "Query results" --> "EXPLORE WITH DATA STUDIO".

	![DataStudio](https://github.com/ioantsep/weather-pipeline-on-GCP/blob/main/Images/Data%20Studio.png)
   

