package ttt;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Drone implements MqttCallback, IMqttActionListener {
public static final String COMMAND_KEY = "COMMAND";
public static final String COMMAND_SEPARATOR = ":";
public static final String GET_ALTITUDE_COMMAND_KEY = "GET_ALTITUDE";
// Replace with your own topic name
public static final String TOPIC = "java-magazine-mqtt/drones/altitude";

public static final String ENCODING = "UTF-8";

// Quality of Service = Exactly once
// I want to receive all messages exactly once
public static final int QUALITY_OF_SERVICE = 2;
protected String name;
protected String clientId;
protected MqttAsyncClient client;
protected MemoryPersistence memoryPersistence;
protected IMqttToken connectToken;
protected IMqttToken subscribeToken;

public Drone(String name) { this.name = name; }

public String getName() { return name; }

public void onFailure(IMqttToken arg0, Throwable arg1) {
	// The method will run if an operation failed
    arg1.printStackTrace();
	
}

public void onSuccess(IMqttToken asyncActionToken) {
    if (asyncActionToken.equals(connectToken)) {
        System.out.println( String.format(
            "%s successfully connected",name));
        try {
            subscribeToken = client.subscribe(
                TOPIC, QUALITY_OF_SERVICE, 
                null, this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    } 
    else if (asyncActionToken.equals( 
                   subscribeToken)) 
    {
        System.out.println( String.format(
            "%s subscribed to the %s topic",
            name, TOPIC));
        publishTextMessage( String.format(
            "%s is listening.", name));
    }
}

public void connectionLost(Throwable arg0) {
	// The MQTT client lost the connection
	arg0.printStackTrace();
}

public void deliveryComplete(
                IMqttDeliveryToken token) {
    // Delivery for a message has been completed
    // and all acknowledgments have been received
}

public void messageArrived(String topic, MqttMessage message) throws Exception {
// A message has arrived from the MQTT broker
// The MQTT broker doesn't send back 
// an acknowledgment to the server until 
// this method returns cleanly
if (!topic.equals(TOPIC)) {
 return;
}

String messageText = new String(message.getPayload(), ENCODING);
System.out.println( String.format("%s received %s: %s", name, topic,messageText));
String[] keyValue = messageText.split(COMMAND_SEPARATOR);
if (keyValue.length != 3) {
 return;
}
if (keyValue[0].equals(COMMAND_KEY) && keyValue[1].equals(GET_ALTITUDE_COMMAND_KEY) &&
     keyValue[2].equals(name)) {
     // Process the "get altitude" command
     int altitudeInFeet = ThreadLocalRandom
         .current().nextInt(1, 6001);
     System.out.println( String.format(
         "%s altitude: %d feet",
         name, altitudeInFeet));
 }
}

public void connect() {
    try {
        MqttConnectOptions options = 
            new MqttConnectOptions();
        // options.setUserName(
        //    "replace with your username");
        // options.setPassword(
        //    "replace with your password"
        //    .toCharArray());
        // Replace with ssl:// and work with TLS/SSL
        // best practices in a 
        // production environment
        memoryPersistence = 
            new MemoryPersistence();
        String serverURI = 
            "tcp://iot.eclipse.org:1883";
        clientId = MqttAsyncClient.generateClientId();
        client = new MqttAsyncClient(
                       serverURI, clientId, 
                       memoryPersistence);
        // I want to use this instance as the callback
        client.setCallback(this);
        connectToken = client.connect(
            options, null, this);
    } catch (MqttException e) {
        e.printStackTrace();
    }
}

public boolean isConnected() {
    return (client != null) && 
           (client.isConnected());
}

public MessageActionListener publishTextMessage(
        String messageText) 
 {
     byte[] bytesMessage;
     try {
         bytesMessage = 
             messageText.getBytes(ENCODING);
         MqttMessage message;
         message = new MqttMessage(bytesMessage);
         String userContext = "ListeningMessage";
         MessageActionListener actionListener = 
             new MessageActionListener(
                 TOPIC, messageText, userContext);
         client.publish(TOPIC, message,
                 userContext,actionListener);
         return actionListener;
 } catch (UnsupportedEncodingException e) {
     e.printStackTrace();
     return null;
 } catch (MqttException e) {
     e.printStackTrace();
     return null;
 }
}
public MessageActionListener publishCommand(
        String commandName, String destinationName) 
{
  String command = String.format("%s%s%s%s%s",
      COMMAND_KEY, COMMAND_SEPARATOR,
      commandName, COMMAND_SEPARATOR,
      destinationName);
  return publishTextMessage(command);
}

}