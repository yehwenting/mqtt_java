//package ttt;
//
//import java.util.concurrent.ThreadLocalRandom;
//
//import org.eclipse.paho.client.mqttv3.MqttException;
//
//public class MqttSample01 {
//	 public static void main(String[] args) {
//		    Drone drone1 = new Drone("[Drone #1]");
//		    drone1.connect();
////		    Drone drone2 = new Drone("[Drone #2]");
////		    drone2.connect();
//		    Drone masterDrone = new Drone("*Master Drone*");
//		    masterDrone.connect();
//
//		    try {
//		        while (true) {
//		            try {
//		                Thread.sleep(5000);
//		                int r = 
//		                    ThreadLocalRandom.current()
//		                        .nextInt(1, 11);
//		                if ((r < 5) && drone1.isConnected()) {
//		                    masterDrone.publishCommand(               
//		                        Drone.GET_ALTITUDE_COMMAND_KEY,
//		                        drone1.getName());
//		                }  
////		                if (drone2.isConnected()) {
////		                    masterDrone.publishCommand(
////		                        Drone.GET_ALTITUDE_COMMAND_KEY,
////		                        drone2.getName());
////		               }
//		            } catch (InterruptedException e) {
//		                 e.printStackTrace();
//		         }}
//		     } catch(Exception e) {
//		        e.printStackTrace();
//		     } finally {
//		        if (drone1.isConnected()) {
//		            try {
//		                drone1.client.disconnect();
//		            } catch (MqttException e) {
//		                e.printStackTrace();
//		            }
//		        }
////		            ...similarly for drone2 and masterDrone… 
//		    }
//		     }
//	 }
//		    
//	
//	 
