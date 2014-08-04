package cn.com.powerleader.test;  
  
import java.io.IOException;
import java.util.concurrent.Executors;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;  

import cn.com.powerleader.service.impl.SnmpMgtOsServiceImpl;
import cn.com.powerleader.snmpif.OsServer2;
import cn.com.powerleader.util.OsBeanFactory;
  
public class ScheduledExecutorTest {  
 
    public ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(1); 
    OsServer2 osServer2=new OsServer2();
    SnmpMgtOsServiceImpl snmpMgtOsService = (SnmpMgtOsServiceImpl) OsBeanFactory
			.getBean("snmpMgtOsService");
  
    public void lanuchTimer(){  
        Runnable task = new Runnable() {  
            public void run() {  
                throw new RuntimeException();  
            }  
        };  
        scheduExec.scheduleWithFixedDelay(task, 1000*5, 1000*5, TimeUnit.MILLISECONDS);  
    }  
     
    public void addOneTask(){  
        Runnable task = new Runnable() {  
            public void run() {  
                 try {
                	 System.out.println("scheduleExecutor");
					osServer2.updateOsInfo(snmpMgtOsService);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
				}
            }  
        };  
        scheduExec.scheduleWithFixedDelay(task, 1000*1, 1000*5, TimeUnit.MILLISECONDS);  
    }  
      
    public static void main(String[] args) throws Exception {  
        ScheduledExecutorTest test = new ScheduledExecutorTest();  
        test.lanuchTimer();    
        test.addOneTask();  
    }  
}