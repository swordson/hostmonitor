package cn.com.powerleader.test;


import cn.com.powerleader.snmpif.OsServer2;

public class testOServer {
	public static void main(String[] args) {
		OsServer2 osServer2=new OsServer2();
		osServer2.startListener("5");
	}

}
