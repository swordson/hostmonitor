package cn.com.powerleader.snmpif;
   
import java.io.IOException;

import org.apache.log4j.Logger;

import cn.com.powerleader.model.OsInfo;
import cn.com.powerleader.service.SnmpMgtOsService;
import cn.com.powerleader.service.impl.SnmpMgtOsServiceImpl;
import cn.com.powerleader.util.OsBeanFactory;
import cn.com.powerleader.util.SnmpOp;
import cn.com.powerleader.util.Ssh;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


public class OsServer {
	
	private Logger logger = Logger.getLogger(OsServer.class);
	private final Timer timer = new Timer();
	private static ArrayList<String> requestOsInfoList;	
	private static final Byte ONLINE = 1;
	private static final Byte OFFLINE = 0;	
	
	
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
		
	}
	
	public void init() {
		
	}
	
	public void destroy() {
		
	}
	
	public void startListener(final String interval) {
	   	logger.info("Os status monitoring process is started, Time interval is "+ interval + " seconds");  
		timer.schedule(new TimerTask() {
	   		public void run() {
	   			
					
						try {
							updateOsInfo();
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				
	   			this.cancel();
				//清除已经取消的TimerTask
				timer.purge();
	   			startListener(interval);	
	   		}
	   	},Integer.parseInt(interval)*1000);	
	}
	
	public void updateOsInfo() throws IOException, InterruptedException {
		long startTime=System.currentTimeMillis();
		
		List osList = snmpMgtOsService.findAllOsByHql();
	
		SnmpOp snmpop = new SnmpOp();
		for(int i = 0; i < osList.size(); i++){
			OsInfo os = (OsInfo)osList.get(i);
			//TODO:目前验证服务器是否在线的机制不够科学
			if(Ssh.validateSsh(os.getIpAddress1())) {
				os.setOsStatu(ONLINE);
				HashMap<String, String> resultmap = snmpop.getInfo(os.getSnmpUser(), os.getSnmpPasswd(),
					os.getIpAddress1(), requestOsInfoList);
				if(resultmap != null && resultmap.size()>0) {
					//TODO:获取更多的关于OS的性能监控信息
			
					os.setSysUptime(resultmap.get("sysUpTime"));
					os.setSysProcesses(resultmap.get("sysProcesses"));
				}else{
					logger.info("There was a problem while connecting to "+os.getIpAddress1()+" with SNMP Protocal");
				}
			} else {
				os.setOsStatu(OFFLINE);	
			}
			snmpMgtOsService.updateOsInfo(os);		
		}
		long endTime=System.currentTimeMillis();
		logger.info("The monitor takes:"+(endTime-startTime)+"ms");
	}
}
