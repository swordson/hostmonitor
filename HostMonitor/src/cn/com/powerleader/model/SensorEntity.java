package cn.com.powerleader.model;

import com.veraxsystems.vxipmi.coding.commands.sdr.record.EntityId;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.SensorType;

public class SensorEntity {

	
	String name;
	String ip;
	
	String SensorName;
	SensorType sensorType;
	EntityId entityID;
	int sensorID;
	double reading;
	
	
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
		return SensorName;
	}
	public void setSensorName(String sensorName) {
		SensorName = sensorName;
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
	
	public double getReading() {
		return reading;
	}
	public void setReading(double reading) {
		this.reading = reading;
	}
	
	
	
	
	

	
	

	
	
	
	
	
}
