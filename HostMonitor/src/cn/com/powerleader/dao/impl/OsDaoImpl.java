/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.powerleader.dao.impl;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.com.powerleader.dao.OsDao;
import cn.com.powerleader.model.OsInfo;

import java.util.List;
/**
 *
 * @author joe
 */
public class OsDaoImpl extends HibernateDaoSupport implements OsDao {
	
	public OsDaoImpl(){
		
	}
	
	@SuppressWarnings("rawtypes")
	public List findAllOsByHql() {
		String hql= "from cn.com.powerleader.model.OsInfo os ";
		List result = this.getHibernateTemplate().find(hql);
		return result;
	}
	
	public void updateOsInfo(OsInfo os) {
		this.getHibernateTemplate().update(os);
	}
}
