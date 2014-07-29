package cn.com.powerleader.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import cn.com.powerleader.model.SensorEntity;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.chassis.ChassisControl;
import com.veraxsystems.vxipmi.coding.commands.chassis.ChassisControlResponseData;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatus;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatusResponseData;
import com.veraxsystems.vxipmi.coding.commands.chassis.PowerCommand;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSdr;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSdrResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSensorReading;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSensorReadingResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.ReserveSdrRepository;
import com.veraxsystems.vxipmi.coding.commands.sdr.ReserveSdrRepositoryResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.CompactSensorRecord;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.FullSensorRecord;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.RateUnit;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.ReadingType;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.SensorRecord;
import com.veraxsystems.vxipmi.coding.commands.session.SetSessionPrivilegeLevel;
import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.common.TypeConverter;

public class IpmiServiceIpml {
	private String hostname = "";
	private String username = "";
	private String password = "";
	private int INITIAL_CHUNK_SIZE = 8;
	private int MAX_REPO_RECORD_ID = 65535;
	private int CHUNK_SIZE = 64;

	private int HEADER_SIZE = 5;
	private IpmiConnector connector = null;
	private ConnectionHandle handle = null;
	private int nextRecId;

	public List<SensorEntity> doRun(String ip, String username, String password)
			throws Exception {

		List<SensorEntity> list = new ArrayList<SensorEntity>();
		nextRecId = 0;

		int reservationId = 0;
		int lastReservationId = -1;

		// Create the connector

		try {
			connector = new IpmiConnector(6000);

			// start the session to the remote host. We assume, that two-key
			// authentication isn't enabled, so BMC key is left null (see
			// #startSession for details).
			handle = startSession(connector, InetAddress.getByName(ip),
					username, password, "", PrivilegeLevel.User);

			// We get sensor data until we encounter ID = 65535 which means that
			// this record is the last one.
			while (nextRecId < MAX_REPO_RECORD_ID) {

				SensorEntity sensorEntity = new SensorEntity();

				SensorRecord record = null;
				try {
					// Populate the sensor record and get ID of the next record
					// in
					// repository (see #getSensorData for details).
					record = getSensorData(connector, handle, reservationId);
					int recordReadingId = -1;

					// We check if the received record is either
					// FullSensorRecord or
					// CompactSensorRecord, since these types have readings
					// associated with them (see IPMI specification for
					// details).
					if (record instanceof FullSensorRecord) {
						FullSensorRecord fsr = (FullSensorRecord) record;
						recordReadingId = TypeConverter.byteToInt(fsr
								.getSensorNumber());
						// System.out.print(fsr.getName()
						// +'\t'+"------------------"
						// + fsr.getEntityId() +'\t'+"--------------------"
						// + fsr.getSensorNumber()+'\t'+"--------------------"
						// // +'\t'+fsr.getId()
						// // +'\t'+fsr.getRecordType()
						// // +'\t'+fsr.getEntityInstanceNumber()
						// + '\t' + fsr.getSensorType() + '\t');
						sensorEntity.setEntityID(fsr.getEntityId());
						sensorEntity.setSensorName(fsr.getName());
						sensorEntity.setSensorType(fsr.getSensorType());
					} else if (record instanceof CompactSensorRecord) {
						CompactSensorRecord csr = (CompactSensorRecord) record;
						recordReadingId = TypeConverter.byteToInt(csr
								.getSensorNumber());
						System.out.println(csr.getName());
					}

					// If our record has got a reading associated, we get
					// request
					// for it
					GetSensorReadingResponseData data2 = null;
					try {
						if (recordReadingId >= 0) {
							data2 = (GetSensorReadingResponseData) connector
									.sendMessage(
											handle,
											new GetSensorReading(
													IpmiVersion.V20,
													handle.getCipherSuite(),
													AuthenticationType.RMCPPlus,
													recordReadingId));
							if (record instanceof FullSensorRecord) {
								FullSensorRecord rec = (FullSensorRecord) record;
								// Parse sensor reading using information
								// retrieved
								// from sensor record. See
								// FullSensorRecord#calcFormula for details.
								if (data2.getSensorReading(rec) > 0) {

									sensorEntity.setIp(ip);
									sensorEntity.setSensorID(recordReadingId);
									sensorEntity.setReading(data2
											.getSensorReading(rec));
									list.add(sensorEntity);
									sensorEntity = null;
								}
								// System.out
								// .println(data2.getSensorReading(rec)
								// + "\t"
								// + rec.getSensorBaseUnit()
								// .toString()
								// + "\t"
								// + (rec.getRateUnit() != RateUnit.None ?
								// " per "
								// + "\t"
								// + rec.getRateUnit()
								// : ""));
							}
							if (record instanceof CompactSensorRecord) {
								CompactSensorRecord rec = (CompactSensorRecord) record;
								// Get states asserted by the sensor
								List<ReadingType> events = data2
										.getStatesAsserted(rec.getSensorType(),
												rec.getEventReadingType());
								String s = "";
								for (int i = 0; i < events.size(); ++i) {
									s += events.get(i) + ", ";
								}
								// System.out.println(s);

							}

						}
					} catch (IPMIException e) {
						if (e.getCompletionCode() == CompletionCode.DataNotPresent) {
							// e.printStackTrace();
							// System.out.println("data not present!");
						} else {
							throw e;
						}
					}
				} catch (IPMIException e) {
					// If getting sensor data failed, we check if it already
					// failed
					// with this reservation ID.
					if (lastReservationId == reservationId)
						throw e;
					lastReservationId = reservationId;

					// If the cause of the failure was cancelling of the
					// reservation, we get new reservationId and retry. This can
					// happen many times during getting all sensors, since BMC
					// can't
					// manage parallel sessions and invalidates old one if new
					// one
					// appeares.
					reservationId = ((ReserveSdrRepositoryResponseData) connector
							.sendMessage(handle, new ReserveSdrRepository(
									IpmiVersion.V20, handle.getCipherSuite(),
									AuthenticationType.RMCPPlus)))
							.getReservationId();
				}
			}
		} catch (Exception e) {
			System.out
					.println("wrong info:get all sensor reading connect teardown()!");
			list = null;
		} finally {
			if (handle != null) {
				connector.closeSession(handle);
				connector.closeConnection(handle);
			}

			connector.tearDown();
		}

		System.out.println("get all sensor reading DORUN() over!");
		return list;
	}

	public void print_list(List<SensorEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println("ip:" + list.get(i).getIp() + '\t' + "sensorID:"
					+ list.get(i).getSensorID() + '\t' + "reading:"
					+ list.get(i).getReading() + '\t' + "name:"
					+ list.get(i).getSensorName());
		}
	}

	public ConnectionHandle startSession(IpmiConnector connector,
			InetAddress address, String username, String password,
			String bmcKey, PrivilegeLevel privilegeLevel) throws Exception {

		// Create the handle to the connection which will be it's identifier
		ConnectionHandle handle = connector.createConnection(address);

		CipherSuite cs;

		try {
			// Get cipher suites supported by the remote host
			List<CipherSuite> suites = connector
					.getAvailableCipherSuites(handle);

			if (suites.size() > 3) {
				cs = suites.get(3);
			} else if (suites.size() > 2) {
				cs = suites.get(2);
			} else if (suites.size() > 1) {
				cs = suites.get(1);
			} else {
				cs = suites.get(0);
			}
			// Pick the cipher suite and requested privilege level for the
			// session
			connector.getChannelAuthenticationCapabilities(handle, cs,
					privilegeLevel);

			// Open the session and authenticate
			connector
					.openSession(handle, username, password, bmcKey.getBytes());
		} catch (Exception e) {
			connector.closeConnection(handle);
			throw e;
		}

		return handle;
	}

	public SensorRecord getSensorData(IpmiConnector connector,
			ConnectionHandle handle, int reservationId) throws Exception {
		try {
			// BMC capabilities are limited - that means that sometimes the
			// record size exceeds maximum size of the message. Since we don't
			// know what is the size of the record, we try to get
			// whole one first
			GetSdrResponseData data = (GetSdrResponseData) connector
					.sendMessage(handle,
							new GetSdr(IpmiVersion.V20,
									handle.getCipherSuite(),
									AuthenticationType.RMCPPlus, reservationId,
									nextRecId));

			// If getting whole record succeeded we create SensorRecord from
			// received data...
			SensorRecord sensorDataToPopulate = SensorRecord
					.populateSensorRecord(data.getSensorRecordData());
			// ... and update the ID of the next record
			nextRecId = data.getNextRecordId();

			return sensorDataToPopulate;
		} catch (IPMIException e) {
			// The following error codes mean that record is too large to be
			// sent in one chunk. This means we need to split the data in
			// smaller parts.
			if (e.getCompletionCode() == CompletionCode.CannotRespond
					|| e.getCompletionCode() == CompletionCode.UnspecifiedError) {
				// First we get the header of the record to find out its size.
				GetSdrResponseData data = (GetSdrResponseData) connector
						.sendMessage(
								handle,
								new GetSdr(IpmiVersion.V20, handle
										.getCipherSuite(),
										AuthenticationType.RMCPPlus,
										reservationId, nextRecId, 0,
										INITIAL_CHUNK_SIZE));
				// The record size is 5th byte of the record. It does not take
				// into accoount the size of the header, so we need to add it.
				int recSize = TypeConverter.byteToInt(data
						.getSensorRecordData()[4]) + HEADER_SIZE;
				int read = INITIAL_CHUNK_SIZE;

				byte[] result = new byte[recSize];

				System.arraycopy(data.getSensorRecordData(), 0, result, 0,
						data.getSensorRecordData().length);

				// We get the rest of the record in chunks (watch out for
				// exceeding the record size, since this will result in BMC's
				// error.
				while (read < recSize) {
					int bytesToRead = CHUNK_SIZE;
					if (recSize - read < bytesToRead) {
						bytesToRead = recSize - read;
					}
					GetSdrResponseData part = (GetSdrResponseData) connector
							.sendMessage(handle, new GetSdr(IpmiVersion.V20,
									handle.getCipherSuite(),
									AuthenticationType.RMCPPlus, reservationId,
									nextRecId, read, bytesToRead));

					System.arraycopy(part.getSensorRecordData(), 0, result,
							read, bytesToRead);

					read += bytesToRead;
				}

				// Finally we populate the sensor record with the gathered
				// data...
				SensorRecord sensorDataToPopulate = SensorRecord
						.populateSensorRecord(result);
				// ... and update the ID of the next record
				nextRecId = data.getNextRecordId();
				return sensorDataToPopulate;
			} else {
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public String PowerOff(int port, String ipAddress, String username,
			String password) {
		IpmiConnector connector;

		try {
			connector = new IpmiConnector(6001);
			ConnectionHandle handle = connector.createConnection(InetAddress
					.getByName(ipAddress));
			CipherSuite cs = connector.getAvailableCipherSuites(handle).get(3);
			connector.getChannelAuthenticationCapabilities(handle, cs,
					PrivilegeLevel.Administrator);
			connector.openSession(handle, username, password, null);

			GetChassisStatusResponseData rd = (GetChassisStatusResponseData) connector
					.sendMessage(handle, new GetChassisStatus(IpmiVersion.V20,
							cs, AuthenticationType.RMCPPlus));

			connector.sendMessage(handle, new SetSessionPrivilegeLevel(
					IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
					PrivilegeLevel.Administrator));

			ChassisControl chassisControl = null;
			if (!rd.isPowerOn()) {
				return "{flag:0,msg:'系统尚未开启'}";
//				chassisControl = new ChassisControl(IpmiVersion.V20, cs,
//						AuthenticationType.RMCPPlus, PowerCommand.PowerUp);
			} else {
				chassisControl = new ChassisControl(IpmiVersion.V20, cs,
						AuthenticationType.RMCPPlus, PowerCommand.PowerDown);
				
			}
			ChassisControlResponseData data = (ChassisControlResponseData) connector
					.sendMessage(handle, chassisControl);
			return "{flag:1,msg:'success'}";

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "{flag:0,msg:'系统出错'}";
		
	}

	public String PowerOn(int port, String ipAddress, String username,
			String password) {
		try {
			connector = new IpmiConnector(6002);
			ConnectionHandle handle = connector.createConnection(InetAddress
					.getByName(ipAddress));
			CipherSuite cs = connector.getAvailableCipherSuites(handle).get(3);
			connector.getChannelAuthenticationCapabilities(handle, cs,
					PrivilegeLevel.Administrator);
			connector.openSession(handle, username, password, null);

			GetChassisStatusResponseData rd = (GetChassisStatusResponseData) connector
					.sendMessage(handle, new GetChassisStatus(IpmiVersion.V20,
							cs, AuthenticationType.RMCPPlus));

			connector.sendMessage(handle, new SetSessionPrivilegeLevel(
					IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
					PrivilegeLevel.Administrator));

			ChassisControl chassisControl = null;
			if (!rd.isPowerOn()) {
				chassisControl = new ChassisControl(IpmiVersion.V20, cs,
						AuthenticationType.RMCPPlus, PowerCommand.PowerUp);
			} else {
				return "{flag:0,msg:'系统尚未开启'}";
				
			}
			ChassisControlResponseData data = (ChassisControlResponseData) connector
					.sendMessage(handle, chassisControl);
			return "{flag:1,msg:'success'}";

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "{flag:0,msg:'系统出错'}";
	}

}
