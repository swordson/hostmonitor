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

import javax.xml.ws.ServiceMode;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

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
	HashMap<String, String> resultmap2 = null, resultmap=null;
	private static ArrayList<String> requestOsInfoList;	
	private static final Byte ONLINE = 1;
	private static final Byte OFFLINE = 0;
	private int timeout;   
	//调度工厂实例化后，经过timeout时间开始执行调度  
	private IpmiServiceIpml ipmiServiceIpml=new IpmiServiceIpml();
	//初始化SNMP信息查询字段
	static {
		requestOsInfoList = new ArrayList<String>();
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
		requestOsInfoList.add("netFlowIn");
		requestOsInfoList.add("netFlowOut");
		requestOsInfoList.add("ssCpuRawUser");
		requestOsInfoList.add("ssCpuRawNice");
		requestOsInfoList.add("ssCpuRawSystem");
		requestOsInfoList.add("ssCpuRawIdle");
		requestOsInfoList.add("ssCpuRawWait");
		requestOsInfoList.add("ssCpuRawInterrupt");
		requestOsInfoList.add("ssCpuRawSoftIRQ");
		
	}
	
	public void init() {
		
	}
	
	public void destroy() {
		
	}
	
	public void startListener(final String interval, final SnmpMgtOsServiceImpl snmpMgtOsService2) {
	   	logger.info("Os status monitoring process is started, Time interval is "+ interval + " seconds");  
		timer.schedule(new TimerTask() {
	   		public void run() {
	   			
					
						try {
							updateOsInfo(snmpMgtOsService2);
							
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						this.cancel();
						timer.purge();
						System.gc();
						startListener(interval,snmpMgtOsService2);
	   			
						
	   				
	   		}
	   	},1000, Integer.parseInt(interval)*1000);	

	
	}
	
	
	public void updateOsInfo(SnmpMgtOsServiceImpl snmpMgtOsService) throws IOException, InterruptedException {
		System.gc();
		long startTime=System.currentTimeMillis();
		SnmpOp snmpop = new SnmpOp();
		List osList = snmpMgtOsService.findAllOsByHql();
		for(int i = 0; i < osList.size(); i++){
			OsInfo os = (OsInfo)osList.get(i);
//			HashMap<String, String>resultmap = snmpop.getInfo(os.getSnmpUser(),
//					os.getSnmpPasswd(), os.getIpAddress1(),
//					requestOsInfoList);
			//TODO:目前验证服务器是否在线的机制不够科学
			
				
				if (Ssh.validateSsh2(os.getIpAddress1())
						|| Ssh.validateSsh2(os.getIpAddress2())) {
					os.setOsStatu(ONLINE);
				
					try {
						resultmap = snmpop.getInfo(os.getSnmpUser(),
								os.getSnmpPasswd(), os.getIpAddress1(),
								requestOsInfoList);
						if (os.getIpAddress2() != null) {

							resultmap2 = snmpop.getInfo(os.getSnmpUser(),
									os.getSnmpPasswd(), os.getIpAddress2(),
									requestOsInfoList);

						}
						
						
						String ssCpuRawUser=resultmap.get("ssCpuRawUser");
						String ssCpuRawSystem=resultmap.get("ssCpuRawSystem");
						String ssCpuRawNice=resultmap.get("ssCpuRawNice");
						String ssCpuRawIdle=resultmap.get("ssCpuRawIdle");
						String ssCpuRawWait=resultmap.get("ssCpuRawWait");
						String ssCpuRawInterrupt=resultmap.get("ssCpuRawInterrupt");
						String ssCpuRawSoftIRQ=resultmap.get("ssCpuRawSoftIRQ");
						float cpuUserate=getCpuUserate(ssCpuRawUser,ssCpuRawSystem,ssCpuRawIdle,ssCpuRawWait,ssCpuRawInterrupt,ssCpuRawSoftIRQ,ssCpuRawNice);
						os.setCupUserate(cpuUserate);
						String memAvail=resultmap.get("memAvailReal");
						String memTotal=resultmap.get("memTotalReal");
						float memUserate=getMemUserate(memAvail,memTotal);
						os.setMemUserate(memUserate);
						String diskAvail=resultmap.get("diskAvail");
						String diskTotal=resultmap.get("diskTotal");
						float hdUserate=getDiskUserate(diskAvail,diskTotal);
						os.setHdUserate(hdUserate);
						if (resultmap != null && resultmap.size() > 0) {
							// TODO:获取更多的关于OS的性能监控信息
							String netFlowIn;
							String netFlowOut;
							os.setSysUptime(resultmap.get("sysUpTime"));
							
							if (resultmap2 != null && resultmap2.size() > 0) {
								netFlowIn = resultmap2.get("netFlowIn")
										+ resultmap.get("netFlowIn");
								netFlowOut = resultmap2.get("netFlowOut")
										+ resultmap.get("netFlowOut");

							} else {
								netFlowIn = resultmap.get("netFlowIn");
								netFlowOut = resultmap.get("netFlowOut");
								logger.info("There was a problem while connecting to "
										+ os.getIpAddress2()
										+ " with SNMP Protocal");
							}
							
							long time=System.currentTimeMillis()-os.getCurentTime();
							
							float flowInRate=(float) ((Double.parseDouble(netFlowIn)-Double.parseDouble(os.getNetFlowIn()))/time/1000/1024);
							float flowOutRate=(float) ((Double.parseDouble(netFlowIn)-Double.parseDouble(os.getNetFlowIn()))/time/1000/1024);
							os.setFlowInRate(flowInRate);
							os.setFlowOutRate(flowOutRate);
							os.setNetFlowIn(netFlowIn);
							os.setNetFlowOut(netFlowOut);
							
							os.setCurentTime(System.currentTimeMillis());
							
						} else {
							String netFlowIn;
							String netFlowOut;
							if (resultmap2 != null && resultmap2.size() > 0) {
								netFlowIn = resultmap2.get("netFlowIn");
								netFlowOut = resultmap2.get("netFlowOut");
								logger.info("There was a problem while connecting to "
										+ os.getIpAddress1()
										+ " with SNMP Protocal");
								
								long time=System.currentTimeMillis()-os.getCurentTime();
								os.setCurentTime(System.currentTimeMillis());
								float flowInRate=(float) ((Double.parseDouble(netFlowIn)-Double.parseDouble(os.getNetFlowIn()))/time/1000/1024);
								float flowOutRate=(float) ((Double.parseDouble(netFlowIn)-Double.parseDouble(os.getNetFlowIn()))/time/1000/1024);
								os.setFlowInRate(flowInRate);
								os.setFlowOutRate(flowOutRate);
								os.setNetFlowIn(netFlowIn);
								os.setNetFlowOut(netFlowOut);
							} else {
								logger.info("There was a problem while connecting to "
										+ os.getIpAddress1()
										+ "and "
										+ os.getIpAddress2()
										+ " with SNMP Protocal");
								os.setCurentTime(System.currentTimeMillis());
							}

						}
					
						try {
							List<SensorEntity> list = ipmiServiceIpml.doRun(
									os.getIpmiIp(), os.getIpmiUserName(),
									os.getIpmiPassword());
							ipmiServiceIpml.print_list(list);
						} catch (Exception e) {
							
						}
					} catch (Exception e) {
	                 logger.info(e);
					}

				} else {
					os.setOsStatu(OFFLINE);
				}
				os.setSysProcesses(resultmap.get("sysProcesses"));
				os.setCurentTime(System.currentTimeMillis());
				snmpMgtOsService.updateOsInfo(os);
	
		}
		long endTime=System.currentTimeMillis();
		logger.info("The monitor takes:"+(endTime-startTime)+"ms");
	}
	
	

	

	private float getCpuUserate(String ssCpuRawUser, String ssCpuRawSystem,
			String ssCpuRawIdle, String ssCpuRawWait, String ssCpuRawInterrupt,
			String ssCpuRawSoftIRQ,String ssCpuRawNice) {
		   
        double ssCpuRawUser1 =  Double.parseDouble(ssCpuRawUser);
        double ssCpuRawNice1 =  Double.parseDouble(ssCpuRawNice);
        double ssCpuRawSystem1 = Double.parseDouble(ssCpuRawSystem);
        double ssCpuRawIdle1 =  Double.parseDouble(ssCpuRawIdle);
        double ssCpuRawWait1 =  Double.parseDouble(ssCpuRawWait);
        double ssCpuRawInterrupt1 =  Double.parseDouble(ssCpuRawInterrupt);
        double ssCpuRawSoftIRQ1 = Double.parseDouble(ssCpuRawSoftIRQ);
        
        double cpuRatio = 100*(ssCpuRawUser1+ssCpuRawNice1+ssCpuRawSystem1+ssCpuRawWait1+ssCpuRawInterrupt1+ssCpuRawSoftIRQ1)/(ssCpuRawUser1+ssCpuRawNice1
                +ssCpuRawSystem1+ssCpuRawIdle1+ssCpuRawWait1+ssCpuRawInterrupt1+ssCpuRawSoftIRQ1);
        
		return (float) cpuRatio;
	}

	private float getDiskUserate(String diskAvail, String diskTotal) {
		double diskTotal1=Double.parseDouble(diskTotal);
	    double diskAvail1=Double.parseDouble(diskAvail);
	    return (float) ((diskTotal1-diskAvail1)/diskTotal1);
	}

	private float getMemUserate(String memAvail, String memTotal) {
	double memTotal1=Double.parseDouble(memTotal);
    double memAvail1=Double.parseDouble(memAvail);
    return (float) ((memTotal1-memAvail1)/memTotal1);	
	}



	public void setTimeout(int timeout) {  
		this.timeout = timeout;  
		}

	public IpmiServiceIpml getIpmiServiceIpml() {
		return ipmiServiceIpml;
	}

	public void setIpmiServiceIpml(IpmiServiceIpml ipmiServiceIpml) {
		this.ipmiServiceIpml = ipmiServiceIpml;
	}  

}
	
