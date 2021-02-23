package sendtosink;  
//Kafka 
import java.util.Properties; 
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09; 
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;  
//Flink 
import org.apache.flink.api.common.functions.RuntimeContext; 
import org.apache.flink.streaming.api.datastream.DataStream; 
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment; 
import org.apache.flink.api.java.tuple.Tuple6;//6 elements 
import org.apache.flink.api.common.functions.MapFunction; 
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor; 
import org.apache.flink.streaming.api.TimeCharacteristic; 
import java.util.Date;  
//packages for cassandra connector 
import org.apache.flink.streaming.connectors.cassandra.CassandraSink; 
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder; 
import com.datastax.driver.core.Cluster;   
//elasticsearch 
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction; 
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer; 
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink; 
import org.apache.http.HttpHost; 
import org.elasticsearch.action.index.IndexRequest; 
import org.elasticsearch.client.Requests; 
import org.elasticsearch.action.ActionRequest; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map;

 public class sendtosink 
{ 	 	
	public static void main(String[] args) throws Exception  	
	{ 		
	//for Kafka 		
	Properties properties = new Properties(); 
                          properties.setProperty("bootstrap.servers", "localhost:9092");     	
                          properties.setProperty("group.id", "flink_consumer"); 		
                          
    	//Kafka with topic name='iotdata'  		
  	FlinkKafkaConsumer09<String> flinkKafkaConsumer09 = new FlinkKafkaConsumer09<>("iotdata", new SimpleStringSchema(), properties); 

	// set up the streaming execution environment     		
  	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();   		
	
	//Read data from kafka  				
	DataStream<Tuple6<Integer, Integer, Integer, Integer, Integer, 	String>> stream = env
                                                                           	.addSource(flinkKafkaConsumer09) 			
		                                                                .rebalance() 			
		                                                                .map(new MapFunction<String, Tuple6<Integer,Integer,Integer,Integer,Integer,String>>()   	{  			

		@Override 			
		public Tuple6<Integer, Integer, Integer, Integer, Integer,String> map(String input) throws Exception 			
		{ 				
			String[] inputSplits = input.split(","); 			
			return Tuple6.of(Integer.parseInt(inputSplits[0]), 
                       Integer.parseInt(inputSplits[1]), 
                       Integer.parseInt(inputSplits[2]), 
                       Integer.parseInt(inputSplits[3]),
                       Integer.parseInt(inputSplits[4]), 
                       inputSplits[5]); 			
		} 		
	} 		
	); 
 			    				
	// Cassansdra KEYSPACE pollution Table sensor 		
    	CassandraSink
                  .addSink(stream) 				
		  .setQuery("INSERT INTO pollution.sensor6 (O3, PM, CO, SO2, NO2, time) values (?, ?, ?, ?, ?, ?);") 			
                  .setClusterBuilder(new ClusterBuilder() {
                      private static final long serialVersionUID = 1L;  				
                     
                      @Override 					
			                public Cluster buildCluster(Cluster.Builder builder) 
			                { 						
			                  return builder.addContactPoint("127.0.0.1").build(); 				
                      } 
                   }).build();  
     		 	 		
	/* Elasticsearch Configuration*/ 		
	List<HttpHost> httpHosts = new ArrayList<>();
  	httpHosts.add(new HttpHost("127.0.0.1", 9200, "http")); 

	ElasticsearchSink.Builder<Tuple6<Integer, Integer, Integer, Integer, Integer, String>> esSinkBuilder = new ElasticsearchSink.Builder<>( 
		    httpHosts,
    
        new ElasticsearchSinkFunction<Tuple6<Integer, Integer, Integer, Integer, Integer, String>>()  				
		    {        				
			    public IndexRequest createIndexRequest(Tuple6<Integer, Integer, Integer, Integer, Integer, String> element)  					
          {             				
				    // construct JSON document to index 
            Map<String, String> json = new HashMap<>();
				    
     		    json.put("O3", element.f0.toString());  //ozone
				    json.put("PM", element.f1.toString());	//particullate_matter 		
				    json.put("CO", element.f2.toString());	//carbon_monoxide 
				    json.put("S02", element.f3.toString()); //sulfure_dioxide
				    json.put("NO2", element.f4.toString());	//nitrogen_dioxide
				    json.put("date", element.f5);  //date 		
            
				    return Requests.indexRequest()
                   .index("iotdata") // index name                   				
                   .type("sensor6") // mapping name                   				
                   .source(json);         			
			}              			
			
			@Override             					
			public void process(Tuple6<Integer, Integer, Integer, Integer, Integer,String> element, RuntimeContext ctx, RequestIndexer indexer)
  			{             										
        	indexer.add(createIndexRequest(element));         				
        		} 			
		} 		
	);  		

	// set number of events to be seen before writing to Elasticsearch 
	esSinkBuilder.setBulkFlushMaxActions(1); 	 		

	// finally, build and add the sink to the job's pipeline 
	stream.addSink(esSinkBuilder.build());   		

	//visualize 
	System.out.println(env.getExecutionPlan());  

	env.execute("Cassandra and Elasticsearch sinks");  	
  } 		 

} 

