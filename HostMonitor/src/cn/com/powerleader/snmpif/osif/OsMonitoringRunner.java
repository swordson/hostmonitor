package cn.com.powerleader.snmpif.osif;

import cn.com.powerleader.util.OsBeanFactory;
  


public class OsMonitoringRunner {
	
	public static void main(String[] args) {
		OsMonitoringServer server = (OsMonitoringServer) OsBeanFactory
				.getBean("osMonitoringServer");
		server.startListener("5");
	} 

}
