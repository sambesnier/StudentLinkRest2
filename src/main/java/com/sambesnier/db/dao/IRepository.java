package com.sambesnier.db.dao;

import java.util.List;
import java.util.Map;


public interface IRepository {

	public Object create(Object obj); 
	public Object read(String id, Class<?> cls);  
	
	public void update(Object obj);
	public void delete(Object obj);
	
	public List<?> readAll(Class<?> cls);
	
	public Object  queryObject(String query, Map<String, Object> prop);
	public List<?> queryList(String query, Map<String, Object> prop);
	
 
}
