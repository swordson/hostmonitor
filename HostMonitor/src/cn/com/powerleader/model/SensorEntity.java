package cn.com.powerleader.model;

import com.veraxsystems.vxipmi.coding.commands.sdr.record.EntityId;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.SensorType;

public class SensorEntity {

	OsInfo info;
	String name;
	String ip;
	String sensorName;
	SensorType sensorType;
	EntityId entityID;
	int sensorID;
	Double reading;
	Long id;
	public OsInfo getInfo() {
		return info;
	}
	public void setInfo(OsInfo info) {
		this.info = info;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public SensorType getSensorType() {
		return sensorType;
	}
	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}
	public EntityId getEntityID() {
		return entityID;
	}
	public void setEntityID(EntityId entityID) {
		this.entityID = entityID;
	}
	public int getSensorID() {
		return sensorID;
	}
	public void setSensorID(int sensorID) {
		this.sensorID = sensorID;
	}
	public Double getReading() {
		return reading;
	}
	public void setReading(Double reading) {
		this.reading = reading;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	
	

	
	

	
	
	
	
	
}
