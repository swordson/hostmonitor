package cn.com.powerleader.snmpif;
   
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.com.powerleader.model.OsInfo;
import cn.com.powerleader.model.SensorEntity;
import cn.com.powerleader.service.SnmpMgtOsService;
import cn.com.powerleader.service.impl.IpmiServiceIpml;
import cn.com.powerleader.service.impl.SnmpMgtOsServiceImpl;
import cn.com.powerleader.service.impl.UserateComputerServiceImpl;
import cn.com.powerleader.util.OsBeanFactory;
import cn.com.powerleader.util.SnmpOp;
import cn.com.powerleader.util.Ssh;
import cn.com.powerleader.util.propertiesReader;


public class OsServer2 {
	
	private Logger logger = Logger.getLogger(OsServer2.class);
	private final Timer timer = new Timer();
	private SnmpMgtOsService snmpMgtOsService;
	private static ArrayList<String> requestOsInfoList;	
	private static final Byte ONLINE = 1;
	private static final Byte OFFLINE = 0;
	int j=0;
	
	public SnmpMgtOsService getSnmpMgtOsService() {
		return snmpMgtOsService;
	}

	public void setSnmpMgtOsService(SnmpMgtOsService snmpMgtOsService) {
		this.snmpMgtOsService = snmpMgtOsService;
	}	
	
	//初始化SNMP信息查询字段
	static {
		requestOsInfoList = new ArrayList<String>();
		requestOsInfoList.add("sysDescr");
		requestOsInfoList.add("sysUpTime");
		requestOsInfoList.add("sysUpName");
		requestOsInfoList.add("sysUserNums");
		requestOsInfoList.add("sysProcesses");
		requestOsInfoList.add("memTotalReal");
		requestOsInfoList.add("memAvailReal");
		requestOsInfoList.add("memTotalSwap");
		requestOsInfoList.add("memAvailSwap");
		requestOsInfoList.add("memBuffer");
		requestOsInfoList.add("memCached");
		requestOsInfoList.add("diskTotal");
		requestOsInfoList.add("diskAvail");
		requestOsInfoList.add("cpuUsage");
		requestOsInfoList.add("netFlowIn");
		requestOsInfoList.add("netFlowOut");
		
	}
	
	public void init() {
		
	}
	
	public void destroy() {
		
	}
	
	public void startListener(final String interval) {
	   	//logger.info("Os status monitoring process is started, Time interval is "+ interval + " seconds");  
		timer.schedule(new TimerTask() {
	   		public void run() {
	   			
					
						try {
							updateOsInfo();
							System.out.println(j++);
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						this.cancel();
						timer.purge();
						System.gc();
						startListener(interval);
						
	   				
	   		}
	   	},1000, Integer.parseInt(interval)*1000);	
	
		//清除已经取消的TimerTask
		
	}
	
	public void updateOsInfo() throws IOException, InterruptedException {
		long startTime=System.currentTimeMillis();
		SnmpMgtOsServiceImpl snmpMgtOsService = (SnmpMgtOsServiceImpl) OsBeanFactory
				.getBean("snmpMgtOsService");
		List osList = snmpMgtOsService.findAllOsByHql();
		SnmpOp snmpop = new SnmpOp();
		for(int i = 0; i < osList.size(); i++){
			OsInfo os = (OsInfo)osList.get(i);
			//TODO:目前验证服务器是否在线的机制不够科学
			if(Ssh.validateSsh(os.getIpAddress1())) {
				os.setOsStatu(ONLINE);
				HashMap<String, String> resultmap = snmpop.getInfo(os.getSnmpUser(), os.getSnmpPasswd(),
					os.getIpAddress1(), requestOsInfoList);
				UserateComputerServiceImpl computerServiceImpl=new UserateComputerServiceImpl(resultmap, os);
				IpmiServiceIpml ipmiServiceIpml=new IpmiServiceIpml();
				try {
					List<SensorEntity> list = ipmiServiceIpml.doRun(
							os.getIpmiIp(), os.getIpmiUserName(),
							os.getIpmiPassword());
					ipmiServiceIpml.print_list(list);
				} catch (Exception e) {
					
				}
				if(resultmap != null && resultmap.size()>0) {
					System.out.println("desc:"+resultmap.get("sysDescr"));
					System.out.println("process:"+resultmap.get("sysProcesses"));
					System.out.println("cpuUserate:"+computerServiceImpl.getCpuUserate());
					System.out.println("diskUserate:"+computerServiceImpl.getDiskUserate());
					System.out.println("memUserate:"+computerServiceImpl.getMemUserate());
//					long[] netFlow=computerServiceImpl.getNetWorkFlow();
//					System.out.println(netFlow[0]+"---"+netFlow[1]);
	                System.out.println("=====================================");
					//TODO:获取更多的关于OS的性能监控信息
				}else{
					//logger.info("There was a problem while connecting to "+os.getIpAddress1()+" with SNMP Protocal");
				}
			} else {
				os.setOsStatu(OFFLINE);	
			}
	
		}
		long endTime=System.currentTimeMillis();
		logger.info("The monitor takes:"+(endTime-startTime)+"ms");
	}

	
}
