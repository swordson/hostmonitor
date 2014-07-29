package cn.com.powerleader.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.com.powerleader.model.OsInfo;

public class propertiesReader {
	List<OsInfo> osList=new ArrayList<OsInfo>();
	
	public List<OsInfo> readXml(){
	    try{
		DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=builderFactory.newDocumentBuilder();
		Document document = db.parse(new File("properties.xml")); 
		NodeList list = document.getElementsByTagName("Client");
		for (int i = 0; i < list.getLength(); i++) {
		     Element element=(Element) list.item(i);
		     OsInfo info=new OsInfo();
	         info.setIpAddress1(element.getElementsByTagName("result").item(0).getFirstChild().getNodeValue());
	         Node ipAddress2=element.getElementsByTagName("result").item(1).getFirstChild();
	         if(ipAddress2!=null){
	         info.setIpAddress2(ipAddress2.getNodeValue());
	         }else{
	        	 info.setIpAddress2(null);
	         }
	         info.setIpmiIp(element.getElementsByTagName("result").item(2).getFirstChild().getNodeValue());
	         info.setIpmiUserName(element.getElementsByTagName("result").item(3).getFirstChild().getNodeValue());
	         info.setIpmiPassword(element.getElementsByTagName("result").item(4).getFirstChild().getNodeValue());
	         info.setSnmpUser(element.getElementsByTagName("result").item(5).getFirstChild().getNodeValue());
	         info.setSnmpPasswd(element.getElementsByTagName("result").item(6).getFirstChild().getNodeValue());
         osList.add(info); 	
		}
	    } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return osList;
	}

	public List<OsInfo> getOsList() {
		return osList;
	}

	public void setOsList(List<OsInfo> osList) {
		this.osList = osList;
	}
	
	
	

}
