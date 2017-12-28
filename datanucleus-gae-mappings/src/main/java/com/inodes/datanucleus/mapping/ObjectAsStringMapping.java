package com.inodes.datanucleus.mapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;

public abstract class ObjectAsStringMapping extends SingleFieldMapping {

	@SuppressWarnings("rawtypes")
	public abstract Class getJavaType();
	
	@Override
	public String getJavaTypeForDatastoreMapping(int index) {
		return ClassNameConstants.JAVA_LANG_STRING;
	}
	
	@Override
	public void setObject(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, Object value) {
		getDatastoreMapping(0).setObject(ps, exprIndex[0], objectToString(value));
	}
	
	@Override
	public Object getObject(ExecutionContext ec, ResultSet resultSet, int[] exprIndex) {
		if(exprIndex == null) return null;
		Object datastoreValue = getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
		Object value = null;
		if(datastoreValue != null) {
			value = stringToObject(datastoreValue.toString());
		}
		return value;
	}
	
	protected abstract String objectToString(Object o);
	
	protected abstract Object stringToObject(String datastoreValue);
	
}//ObjectAsStringMapping SingleFieldMapping
