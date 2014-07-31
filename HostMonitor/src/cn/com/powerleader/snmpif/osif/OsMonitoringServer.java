package cn.com.powerleader.snmpif.osif;
  
import java.io.IOException;
import java.util.Map;

import cn.com.powerleader.snmpif.OsServer;
import cn.com.powerleader.snmpif.OsServer2;


public class OsMonitoringServer extends OsServer2 {
	private Map<String, String> hostMap;
	public void setHostMap(Map<String, String> hostMap) {
		this.hostMap = hostMap;
	}
	public OsMonitoringServer() throws IOException {
		super();
	}
	public void startListener() {
		super.startListener(hostMap.get("interval"));
	}

}
