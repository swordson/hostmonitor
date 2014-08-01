package cn.com.powerleader.snmpif.osif;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.com.powerleader.service.impl.SnmpMgtOsServiceImpl;
import cn.com.powerleader.util.OsBeanFactory;
  


public class OsMonitoringRunner {
	
	public static void main(String[] args) {
		  OsMonitoringServer server = (OsMonitoringServer) OsBeanFactory
				.getBean("osMonitoringServer");
		  SnmpMgtOsServiceImpl snmpMgtOsService = (SnmpMgtOsServiceImpl) OsBeanFactory
					.getBean("snmpMgtOsService");
		  server.startListener("5",snmpMgtOsService);
		  
	} 

}
