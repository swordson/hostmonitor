package cn.com.powerleader.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.core.Logger;
import org.snmp4j.Snmp;
import org.snmp4j.smi.IpAddress;

import cn.com.powerleader.model.OsInfo;
import cn.com.powerleader.util.SnmpGetAsyn;
import cn.com.powerleader.util.SnmpOp;

public class UserateComputerServiceImpl {
	Logger logger=new Logger();
	String community = "public";
	HashMap<String, String> resultmap = new HashMap<String, String>();
	OsInfo osInfo = new OsInfo();

	public UserateComputerServiceImpl(HashMap<String, String> resultmap,
			OsInfo info) {
		super();
		this.resultmap = resultmap;
		this.osInfo = info;
	}

	public double getMemUserate() {
		double memTotal = Long.parseLong(resultmap.get("memTotalReal"));
		double memAvail = Long.parseLong(resultmap.get("memAvailReal"));
		return (memTotal - memAvail) / memTotal;

	}

	public double getCpuUserate() {
		List<String> oidList = new ArrayList<String>();

		// ssCpuRawUser
		oidList.add(".1.3.6.1.4.1.2021.11.50.0");
		// ssCpuRawNice
		oidList.add(".1.3.6.1.4.1.2021.11.51.0");
		// ssCpuRawSystem
		oidList.add(".1.3.6.1.4.1.2021.11.52.0");
		// ssCpuRawIdle
		oidList.add(".1.3.6.1.4.1.2021.11.53.0");
		// ssCpuRawWait
		oidList.add(".1.3.6.1.4.1.2021.11.54.0");
		// ssCpuRawKernel
		// oidList.add(".1.3.6.1.4.1.2021.11.55.0");
		// ssCpuRawInterrupt
		oidList.add(".1.3.6.1.4.1.2021.11.56.0");
		// ssCpuRawSoftIRQ
		oidList.add("1.3.6.1.4.1.2021.11.61.0");
		// 异步采集数据
		String addr = osInfo.getIpAddress1();
		List cpuData = SnmpGetAsyn.snmpAsynGetList(addr, community, oidList);
		double ssCpuRawUser = Double.parseDouble(cpuData.get(0).toString());
		double ssCpuRawNice = Double.parseDouble(cpuData.get(1).toString());
		double ssCpuRawSystem = Double.parseDouble(cpuData.get(2).toString());
		double ssCpuRawIdle = Double.parseDouble(cpuData.get(3).toString());
		double ssCpuRawWait = Double.parseDouble(cpuData.get(4).toString());
		double ssCpuRawInterrupt = Double
				.parseDouble(cpuData.get(5).toString());
		double ssCpuRawSoftIRQ = Double.parseDouble(cpuData.get(6).toString());
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map

		double cpuRatio = 100
				* (ssCpuRawUser + ssCpuRawNice + ssCpuRawSystem + ssCpuRawWait
						+ ssCpuRawInterrupt + ssCpuRawSoftIRQ)
				/ (ssCpuRawUser + ssCpuRawNice + ssCpuRawSystem + ssCpuRawIdle
						+ ssCpuRawWait + ssCpuRawInterrupt + ssCpuRawSoftIRQ);

		return cpuRatio;

	}

	public double getDiskUserate() {
		double diskUsed = Double.parseDouble(resultmap.get("diskAvail"));
		double diskTotal = Double.parseDouble(resultmap.get("diskTotal"));
		return (diskTotal - diskUsed) / diskTotal;
	}

	public long[] getNetWorkFlow() {
		SnmpOp op=new SnmpOp();
		long[] netFlow ;
		netFlow=new long[2];
		ArrayList<String> oidList = new ArrayList<String>();
		oidList.add("netFlowIn");
		oidList.add("netFlowOut");
		String name=osInfo.getSnmpUser();
		String pass=osInfo.getSnmpPasswd();
		String addr1=osInfo.getIpAddress1();
		String addr2=osInfo.getIpAddress2();
		HashMap<String, String> map1 = null;
		HashMap<String, String> map2 = null;
		try {
		
			map1 = op.getInfo(name, pass, addr1, oidList);
			map2 = op.getInfo(name, pass, addr2, oidList);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("网址配置有误");
		}
		if(map1!=null&&map2!=null){
			long netFlowIn1=Long.parseLong(map1.get("netFlowIn"));
		    long netFlowOut1=Long.parseLong(map1.get("netFlowOut"));
		    long netFlowIn2=Long.parseLong(map2.get("netFlowIn"));
		    long netFlowOut2=Long.parseLong(map2.get("netFlowOut"));
		    try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    HashMap<String, String> map3=op.getInfo(name, pass, addr1, oidList);
		    HashMap<String, String> map4=op.getInfo(name, pass, addr2, oidList);
		    long netFlowIn3=Long.parseLong(map3.get("netFlowIn"));
		    long netFlowOut3=Long.parseLong(map3.get("netFlowOut"));
		    long netFlowIn4=Long.parseLong(map4.get("netFlowIn"));
		    long netFlowOut4=Long.parseLong(map4.get("netFlowOut"));
		    netFlow[0]=(netFlowIn3+netFlowIn4-netFlowIn2-netFlowIn1)/1024/2;
		    netFlow[1]=(netFlowOut4+netFlowOut3-netFlowOut1-netFlowOut2)/1024/2;
			return netFlow;
		}else if(map2==null&map1!=null){
			long netFlowIn1=Long.parseLong(map1.get("netFlowIn"));
		    long netFlowOut1=Long.parseLong(map1.get("netFlowOut"));
		    try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    HashMap<String, String> map3=op.getInfo(name, pass, addr1, oidList);
		    long netFlowIn3=Long.parseLong(map3.get("netFlowIn"));
		    long netFlowOut3=Long.parseLong(map3.get("netFlowOut"));
		    netFlow[0]=(netFlowIn3-netFlowIn1)/1024/2;
		    netFlow[1]=(netFlowOut3-netFlowOut1)/1024/2;
		    return netFlow;
		}else if(map1==null&&map2!=null){
			long netFlowIn2=Long.parseLong(map2.get("netFlowIn"));
		    long netFlowOut2=Long.parseLong(map2.get("netFlowOut"));
		    try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    HashMap<String, String> map4=op.getInfo(name, pass, addr2, oidList);
		    long netFlowIn4=Long.parseLong(map4.get("netFlowIn"));
		    long netFlowOut4=Long.parseLong(map4.get("netFlowOut"));
		    netFlow[0]=(netFlowIn4-netFlowIn2)/1024/2;
		    netFlow[1]=(netFlowOut4-netFlowOut2)/1024/2;
		    return netFlow;
		}else{
			logger.info("网址有误，两个网卡均无法访问");
			return null;
		}
		
	    
	    
	    
	    
	   

	}

	public HashMap<String, String> getResultmap() {
		return resultmap;
	}

	public void setResultmap(HashMap<String, String> resultmap) {
		this.resultmap = resultmap;
	}

	public OsInfo getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(OsInfo osInfo) {
		this.osInfo = osInfo;
	}
	// public double netFlowCom(List data1,List data2){
	//
	// double flowIn1=Double.parseDouble(data2.get(0));
	// double flowIn2=Double.parseDouble(data1.get(0));
	// double flowOut1=Double.parseDouble(data2.get(1));
	// double flowOut2=Double.parseDouble(data1.get(1));
	// return (flowIn1+flowOut1-flowIn2-flowOut2)/1024/3;
	//
	// }

}
