/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.powerleader.service;
import java.util.List;

import cn.com.powerleader.model.OsInfo;
/**
 *
 * @author joe
 */
public interface SnmpMgtOsService {
	public List findAllOsByHql();
	public void updateOsInfo(OsInfo os);


}
