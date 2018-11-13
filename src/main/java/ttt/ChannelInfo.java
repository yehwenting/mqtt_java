package ttt;

//import ChannelInfo.NetInfo;

public class ChannelInfo {
	
	public class NetInfo {
		boolean isRemote;
		boolean serverDiscovery;
		String hostname;
		int port;
		String password;
		
		public NetInfo() {
			isRemote = false;
			serverDiscovery = false;
			hostname = "";
			port = 0;
			password = "";
		}
	};	
	
	int deviceSerialNumber;
	int hubPort;
	int channel;
	boolean isHubPortDevice;
	boolean isVINT;
	NetInfo netInfo;
	
	public ChannelInfo() {
		deviceSerialNumber = 0;
		hubPort = -1;
		channel = -1;
		isHubPortDevice = false;
		isVINT = false;
		netInfo = new NetInfo();
	}
};