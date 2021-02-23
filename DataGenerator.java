package producer;
import java.util.Properties; 
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random; 
import org.apache.kafka.clients.producer.KafkaProducer; 
import org.apache.kafka.clients.producer.ProducerRecord; 
import java.util.Date;
import java.text.SimpleDateFormat;

public class DataGenerator {
	public static void main(String args[]) { 	
	// for Kafka
	Properties properties = new Properties(); 	
	properties.put("bootstrap.servers", "localhost:9092"); 
	properties.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer"); 	
	properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); 
	properties.put("acks", "1"); 		
	KafkaProducer<Integer,String> producer= new KafkaProducer<Integer, 	String>(properties); 		
	//for loop 		
	int counter =0; 		
	int check =1; 
	while (counter<check) {
		StringBuffer stream = new StringBuffer();
		int ozone = ThreadLocalRandom.current().nextInt(15,215);
    int particullate_matter = ThreadLocalRandom.current().nextInt(15,215); 					
    int carbon_monoxide = ThreadLocalRandom.current().nextInt(15, 215); 					
    int sulfure_dioxide = ThreadLocalRandom.current().nextInt(15, 215); 					
    int nitrogen_dioxide = ThreadLocalRandom.current().nextInt(15, 215); 				
		// create timestamp --> format: yyyy-M-dd HH:mm:ss  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss"); 				
		String date = sdf.format(new Date());  
		stream.append(ozone); 			
		stream.append(","); 									
    stream.append(particullate_matter); 			
		stream.append(","); 									
    stream.append(carbon_monoxide); 			
		stream.append(","); 			
		stream.append(sulfure_dioxide); 			
		stream.append(","); 			
		stream.append(nitrogen_dioxide); 			
		stream.append(","); 			
		stream.append(date);  			
		 
		System.out.println(stream.toString()); 
    
		ProducerRecord<Integer, String> data = newProducerRecord<Integer, String>("iotdata", stream.toString()); 
		producer.send(data);  
		
    //send every 5 seconds
			try {     				
				Thread.sleep(5000); 			
			} 			
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); 			
			} 
	} 		 		
	producer.close(); 	
   } 
}
