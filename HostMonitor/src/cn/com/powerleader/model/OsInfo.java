package cn.com.powerleader.model;

// default package
// Generated 2011-12-22 9:41:03 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * OsInfo generated by hbm2java
 */
public class OsInfo implements java.io.Serializable {

	private long osId;
	private ServerManageInfo serverManageInfo;
	private String ipAddress1;
	private Byte isVm;
	private String osName;
	private String osVersion;
	private String osBit;
	private Byte osStatu;
	private String osUser;
	private String osPasswd;
	private String ipAddress2;
	private String ipAddress3;
	private String ipAddress4;
	private Date dataCreated;
	private Long createdBy;
	private Date dateModified;
	private Long modifiedBy;
	private String memo;
	private String manageName;
	private String snmpUser;
	private String snmpPasswd;
	private Float cupUserate;
	private Float memUserate;
	private Float hdUserate;
	private String sysUptime;
	private String sysProcesses;
	private String netFlowIn;
	private String netFlowOut;
	private Long curentTime;
	private String ipmiIp;
	private String ipmiUserName;
	private String ipmiPassword;
	private double flowInRate;
	private double flowOutRate;
	
	public OsInfo() {
		super();
		
	}
	public OsInfo(long osId, ServerManageInfo serverManageInfo,
			String ipAddress1, Byte isVm, String osName, String osVersion,
			String osBit, Byte osStatu, String osUser, String osPasswd,
			String ipAddress2, String ipAddress3, String ipAddress4,
			Date dataCreated, Long createdBy, Date dateModified,
			Long modifiedBy, String memo, String manageName, String snmpUser,
			String snmpPasswd) {
		this.osId = osId;
		this.serverManageInfo = serverManageInfo;
		this.ipAddress1 = ipAddress1;
		this.isVm = isVm;
		this.osName = osName;
		this.osVersion = osVersion;
		this.osBit = osBit;
		this.osStatu = osStatu;
		this.osUser = osUser;
		this.osPasswd = osPasswd;
		this.ipAddress2 = ipAddress2;
		this.ipAddress3 = ipAddress3;
		this.ipAddress4 = ipAddress4;
		this.dataCreated = dataCreated;
		this.createdBy = createdBy;
		this.dateModified = dateModified;
		this.modifiedBy = modifiedBy;
		this.memo = memo;
		this.manageName = manageName;
		this.snmpUser = snmpUser;
		this.snmpPasswd = snmpPasswd;
	}
	public long getOsId() {
		return osId;
	}
	public void setOsId(long osId) {
		this.osId = osId;
	}
	public ServerManageInfo getServerManageInfo() {
		return serverManageInfo;
	}
	public void setServerManageInfo(ServerManageInfo serverManageInfo) {
		this.serverManageInfo = serverManageInfo;
	}
	public String getIpAddress1() {
		return ipAddress1;
	}
	public void setIpAddress1(String ipAddress1) {
		this.ipAddress1 = ipAddress1;
	}
	public Byte getIsVm() {
		return isVm;
	}
	public void setIsVm(Byte isVm) {
		this.isVm = isVm;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getOsBit() {
		return osBit;
	}
	public void setOsBit(String osBit) {
		this.osBit = osBit;
	}
	public Byte getOsStatu() {
		return osStatu;
	}
	public void setOsStatu(Byte osStatu) {
		this.osStatu = osStatu;
	}
	public String getOsUser() {
		return osUser;
	}
	public void setOsUser(String osUser) {
		this.osUser = osUser;
	}
	public String getOsPasswd() {
		return osPasswd;
	}
	public void setOsPasswd(String osPasswd) {
		this.osPasswd = osPasswd;
	}
	public String getIpAddress2() {
		return ipAddress2;
	}
	public void setIpAddress2(String ipAddress2) {
		this.ipAddress2 = ipAddress2;
	}
	public String getIpAddress3() {
		return ipAddress3;
	}
	public void setIpAddress3(String ipAddress3) {
		this.ipAddress3 = ipAddress3;
	}
	public String getIpAddress4() {
		return ipAddress4;
	}
	public void setIpAddress4(String ipAddress4) {
		this.ipAddress4 = ipAddress4;
	}
	public Date getDataCreated() {
		return dataCreated;
	}
	public void setDataCreated(Date dataCreated) {
		this.dataCreated = dataCreated;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	public Long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getManageName() {
		return manageName;
	}
	public void setManageName(String manageName) {
		this.manageName = manageName;
	}
	public String getSnmpUser() {
		return snmpUser;
	}
	public void setSnmpUser(String snmpUser) {
		this.snmpUser = snmpUser;
	}
	public String getSnmpPasswd() {
		return snmpPasswd;
	}
	public void setSnmpPasswd(String snmpPasswd) {
		this.snmpPasswd = snmpPasswd;
	}
	public Float getCupUserate() {
		return cupUserate;
	}
	public void setCupUserate(Float cupUserate) {
		this.cupUserate = cupUserate;
	}
	public Float getMemUserate() {
		return memUserate;
	}
	public void setMemUserate(Float memUserate) {
		this.memUserate = memUserate;
	}
	public Float getHdUserate() {
		return hdUserate;
	}
	public void setHdUserate(Float hdUserate) {
		this.hdUserate = hdUserate;
	}
	public String getSysUptime() {
		return sysUptime;
	}
	public void setSysUptime(String sysUptime) {
		this.sysUptime = sysUptime;
	}
	public String getSysProcesses() {
		return sysProcesses;
	}
	public void setSysProcesses(String sysProcesses) {
		this.sysProcesses = sysProcesses;
	}
	public String getNetFlowIn() {
		return netFlowIn;
	}
	public void setNetFlowIn(String netFlowIn) {
		this.netFlowIn = netFlowIn;
	}
	public String getNetFlowOut() {
		return netFlowOut;
	}
	public void setNetFlowOut(String netFlowOut) {
		this.netFlowOut = netFlowOut;
	}
	public Long getCurentTime() {
		return curentTime;
	}
	public void setCurentTime(Long curentTime) {
		this.curentTime = curentTime;
	}
	public String getIpmiIp() {
		return ipmiIp;
	}
	public void setIpmiIp(String ipmiIp) {
		this.ipmiIp = ipmiIp;
	}
	public String getIpmiUserName() {
		return ipmiUserName;
	}
	public void setIpmiUserName(String ipmiUserName) {
		this.ipmiUserName = ipmiUserName;
	}
	public String getIpmiPassword() {
		return ipmiPassword;
	}
	public void setIpmiPassword(String ipmiPassword) {
		this.ipmiPassword = ipmiPassword;
	}
	public double getFlowInRate() {
		return flowInRate;
	}
	public void setFlowInRate(double flowInRate) {
		this.flowInRate = flowInRate;
	}
	public double getFlowOutRate() {
		return flowOutRate;
	}
	public void setFlowOutRate(double flowOutRate) {
		this.flowOutRate = flowOutRate;
	}


}
