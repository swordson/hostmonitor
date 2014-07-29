/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.powerleader.service.impl;
import java.util.List;

import cn.com.powerleader.dao.OsDao;
import cn.com.powerleader.model.OsInfo;
import cn.com.powerleader.service.SnmpMgtOsService;
/**
 *
 * @author joe
 */
public class SnmpMgtOsServiceImpl implements SnmpMgtOsService {
    private OsDao osDao;

	public void setOsDao(OsDao osDao){
		this.osDao = osDao;
	}
	
	public OsDao getOsDao(){
		return this.osDao;
	}
	
	public List findAllOsByHql(){
		return osDao.findAllOsByHql();
	}
	
	public void updateOsInfo(OsInfo os){
		osDao.updateOsInfo(os);
	}





}
