/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.powerleader.dao;

import java.util.List;

import cn.com.powerleader.model.OsInfo;
/**
 *
 * @author joe
 */
public interface OsDao {
	public List findAllOsByHql();
	public void updateOsInfo(OsInfo os);
	public OsInfo findBy(String ipAddress1);
}
