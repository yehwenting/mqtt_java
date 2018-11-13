package ttt;

import com.phidget22.*;
import java.util.Scanner; //Required for Text Input

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class VoltageInputExample {
	
	
	static Scanner s = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception,MqttSecurityException, MqttException {
		
		/***
		* Allocate a new Phidget Channel object
		***/
		VoltageInput ch = new VoltageInput();
		
		final MqttClient client = new MqttClient("tcp://iot.eclipse.org:1883", MqttClient.generateClientId());
    	
		client.connect();
    	System.out.println("Connected");
    	final MqttMessage message = new MqttMessage();


		/**
		* Configures the device's DataInterval and ChangeTrigger.
		* Displays info about the attached Phidget channel.
		* Fired when a Phidget channel with onAttachHandler registered attaches
		*/
		ch.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
			
				try {
					//If you are unsure how to use more than one Phidget channel with this event, we recommend going to
					//www.phidgets.com/docs/Using_Multiple_Phidgets for information

					System.out.print("\nAttach Event:");
					
					VoltageInput ph = (VoltageInput) ae.getSource();
					
					/**
					* Get device information and display it.
					**/
					int serialNumber = ph.getDeviceSerialNumber();
					String channelClass = ph.getChannelClassName();
					int channel = ph.getChannel();
					
					DeviceClass deviceClass = ph.getDeviceClass();
					if (deviceClass != DeviceClass.VINT) {
						System.out.print("\n\t-> Channel Class: " + channelClass + "\n\t-> Serial Number: " + serialNumber +
							  "\n\t-> Channel:  " + channel + "\n");
					} 
					else {			
						int hubPort = ph.getHubPort();
						System.out.print("\n\t-> Channel Class: " + channelClass + "\n\t-> Serial Number: " + serialNumber +
							  "\n\t-> Hub Port: " + hubPort + "\n\t-> Channel:  " + channel + "\n");
					}
					
					
					/*
					*	Set the DataInterval inside of the attach handler to initialize the device with this value.
					*	DataInterval defines the minimum time between VoltageChange events.
					*	DataInterval can be set to any value from MinDataInterval to MaxDataInterval.
					*/
					System.out.print("\tSetting DataInterval to 1000ms\n");
					ph.setDataInterval(500);

					/*
					*	Set the VoltageChangeTrigger inside of the attach handler to initialize the device with this value.
					*	VoltageChangeTrigger will affect the frequency of VoltageChange events, by limiting them to only occur when
					*	the voltage changes by at least the value set.
					*/
					System.out.print("\tSetting Voltage ChangeTrigger to 0.0\n");
					ph.setVoltageChangeTrigger(0.0);
					
					/*
					* Set the SensorType inside of the attach handler to initialize the device with this value.
					* You can find the appropriate SensorType for your sensor in its User Guide and the VoltageInput API
					* SensorType will apply the appropriate calculations to the voltage reported by the device
					* to convert it to the sensor's units.
					* SensorType can only be set for Sensor Port voltage inputs(VINT Ports and Analog Input Ports)
					*/
					if (ph.getChannelSubclass() == ChannelSubclass.VOLTAGE_INPUT_SENSOR_PORT) {
						System.out.print("\tSetting Voltage SensorType\n");
						ph.setSensorType(VoltageSensorType.VOLTAGE);
					}
				} 
				catch (PhidgetException e) {
					PhidgetHelperFunctions.DisplayError(e, "Getting Channel Informaiton");
				}
			
			}
			
		});

		
		/**
		* Displays info about the detached Phidget channel.
		* Fired when a Phidget channel with onDetachHandler registered detaches
		*/
		ch.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				try {
					//If you are unsure how to use more than one Phidget channel with this event, we recommend going to
					//www.phidgets.com/docs/Using_Multiple_Phidgets for information

					System.out.print("\nAttach Event:");
					
					Phidget ph = de.getSource();
					
					/**
					* Get device information and display it.
					**/
					int serialNumber = ph.getDeviceSerialNumber();
					String channelClass = ph.getChannelClassName();
					int channel = ph.getChannel();
					
					DeviceClass deviceClass = ph.getDeviceClass();
					if (deviceClass != DeviceClass.VINT) {
						System.out.print("\n\t-> Channel Class: " + channelClass + "\n\t-> Serial Number: " + serialNumber +
							  "\n\t-> Channel:  " + channel + "\n");
					} 
					else {			
						int hubPort = ph.getHubPort();
						System.out.print("\n\t-> Channel Class: " + channelClass + "\n\t-> Serial Number: " + serialNumber +
							  "\n\t-> Hub Port: " + hubPort + "\n\t-> Channel:  " + channel + "\n");
					}
				} 
				catch (PhidgetException e) {
					PhidgetHelperFunctions.DisplayError(e, "Getting Channel Informaiton");
				}
			}
		});

		/**
		* Writes Phidget error info to stderr.
		* Fired when a Phidget channel with onErrorHandler registered encounters an error in the library
		*/
		ch.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				System.out.println("Error: " + ee.getDescription());
			}
		});

		/**
		* Outputs the VoltageInput's most recently detected voltage
		* Fired when a VoltageInput channel with onVoltageChangeHandler registered meets DataInterval and ChangeTrigger criteria
		*/
		ch.addVoltageChangeListener(new VoltageInputVoltageChangeListener() {
			public void onVoltageChange(VoltageInputVoltageChangeEvent e) {
				//If you are unsure how to use more than one Phidget channel with this event, we recommend going to
				//www.phidgets.com/docs/Using_Multiple_Phidgets for information

				System.out.println("[Voltage Event] -> Voltage1111: " + e.getVoltage());
				double v1=(16.801*Math.log(e.getVoltage())) + 98.889;
				String voltage=String.valueOf(v1);
//				message.setPayload("Hello world from Java".getBytes());
				
				message.setPayload(voltage.getBytes());
		    	
				try {
					client.publish("iot_data/test", message);
				} catch (MqttPersistenceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MqttException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	System.out.println("Message published");
//		    	try {
//					client.disconnect();
//				} catch (MqttException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			}
		});

		/**
		* Outputs the VoltageInput's most recently detected sensor value.
		* Fired when a VoltageInput channel with onSensorChangeHandler registered meets DataInterval and ChangeTrigger criteria
		*/
		ch.addSensorChangeListener(new VoltageInputSensorChangeListener() {
			public void onSensorChange(VoltageInputSensorChangeEvent e) {
				//If you are unsure how to use more than one Phidget channel with this event, we recommend going to
				//www.phidgets.com/docs/Using_Multiple_Phidgets for information

				System.out.println("[Sensor Event] -> Sensor Value: " + e.getSensorValue());
			}
		});
		
		try {
			
			/***
			* Set matching parameters to specify which channel to open
			***/
		
			//You may remove these lines and hard-code the addressing parameters to fit your application
			ChannelInfo channelInfo = new ChannelInfo();  //Information from AskForDeviceParameters(). May be removed when hard-coding parameters.
			PhidgetHelperFunctions.AskForDeviceParameters(channelInfo, ch);
			
			ch.setDeviceSerialNumber(channelInfo.deviceSerialNumber);
			ch.setHubPort(channelInfo.hubPort);
			ch.setIsHubPortDevice(channelInfo.isHubPortDevice);
			ch.setChannel(channelInfo.channel);
			
			if(channelInfo.netInfo.isRemote) {
				ch.setIsRemote(channelInfo.netInfo.isRemote);
				if(channelInfo.netInfo.serverDiscovery) {
					try {
						Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);
					}
					catch (PhidgetException e) {
						PhidgetHelperFunctions.PrintEnableServerDiscoveryErrorMessage(e);
						throw new Exception("Program Terminated: EnableServerDiscovery Failed", e);
					}
				}
				else {
					Net.addServer("Server", channelInfo.netInfo.hostname,
						channelInfo.netInfo.port, channelInfo.netInfo.password, 0);
				}
			}
			
			
			//This call may be harmlessly removed
			PrintEventDescriptions();
			
			/***
			* Open the channel with a timeout
			***/
			System.out.print("\nOpening and Waiting for Attachment...");
			
			try {
				ch.open(5000);
			}
			catch (PhidgetException e) {
				PhidgetHelperFunctions.PrintOpenErrorMessage(e, ch);
				throw new Exception ("Program Terminated: Open Failed", e);
			}
			
			/***
			* To find additional functionality not included in this example,
			* be sure to check the API for your device.
			***/
			
			System.out.println("Sampling data for 10 seconds...");

			Thread.sleep(1000000);
		

			/***
			* Perform clean up and exit
			***/			
			System.out.println("\nDone Sampling...");

			System.out.println("Cleaning up...");
			ch.close();
		
			System.out.print("\nExiting...");
			return;

			
		} catch (PhidgetException ex) {
			System.out.println(ex.getDescription());
		}
	}
	
	/***
	* Prints descriptions of how events related to this class work
	***/
	public static void PrintEventDescriptions()	{
		System.out.print("\n--------------------\n" +
				"\n  | Voltage change events will call their associated function every time the input reports its voltage has changed.\n" +
				"  | Press ENTER once you have read this message.\n");
		s.nextLine();
		
		System.out.println("\n--------------------");
	}
}
