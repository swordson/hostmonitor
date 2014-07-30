package cn.com.powerleader.test;  
  
import java.io.IOException;
import java.util.concurrent.Executors;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;  

import cn.com.powerleader.snmpif.OsServer2;
  
public class ScheduledExecutorTest {  
 
    public ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(1); 
    OsServer2 osServer2=new OsServer2();
  
    public void lanuchTimer(){  
        Runnable task = new Runnable() {  
            public void run() {  
                throw new RuntimeException();  
            }  
        };  
        scheduExec.scheduleWithFixedDelay(task, 1000*5, 1000*10, TimeUnit.MILLISECONDS);  
    }  
     
    public void addOneTask(){  
        Runnable task = new Runnable() {  
            public void run() {  
                 try {
					osServer2.updateOsInfo();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
				}
            }  
        };  
        scheduExec.scheduleWithFixedDelay(task, 1000*1, 1000, TimeUnit.MILLISECONDS);  
    }  
      
    public static void main(String[] args) throws Exception {  
        ScheduledExecutorTest test = new ScheduledExecutorTest();  
        test.lanuchTimer();  
        Thread.sleep(1000*5);  
        test.addOneTask();  
    }  
}