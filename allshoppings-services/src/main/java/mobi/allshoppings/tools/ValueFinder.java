package mobi.allshoppings.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;

public class ValueFinder {

	
	public static String DEFAULT_VALUE = "name";
	public static String DEFAULT_SPRING_DAO_BEAN = ".dao.ref";
	
	public static Object findValueFor(String fieldName, Object fieldValue) {
		return findValueFor(fieldName, fieldValue, DEFAULT_VALUE);
	}

	public static Object findValueFor(String fieldName, Object fieldValue, String valueName) {
		if( fieldName == null || fieldName.equals("") || fieldValue.equals(null) ) {
			return null;
		}
		
		if( fieldName.equals("shoppings") || fieldName.equals("brands") || fieldName.equals("stores") || fieldName.equals("availableFinancialEntities")) {
			try {
				String originalClassName = fieldName.substring(0, fieldName.length() - 1).toLowerCase();
				if( fieldName.equals("availableFinancialEntities")) {
					originalClassName = "financialentity";
				}
				ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
				@SuppressWarnings("unchecked")
				GenericDAO<ModelKey> dao = (GenericDAO<ModelKey>) ctx.getBean(originalClassName + DEFAULT_SPRING_DAO_BEAN);
				ModelKey o = dao.get(fieldValue.toString(), true);
				Object value = ReflectionUtil.getAttributeValue("name", o);
				return value;
			} catch (ASException e) {
				return fieldValue;
			} catch (IllegalArgumentException e) {
				return fieldValue;
			} catch (IllegalAccessException e) {
				return fieldValue;
			}
		}
		
		if( !fieldName.toUpperCase().endsWith("ID") || fieldName.length() <= 2 ) {
			return fieldValue;
		}
		
		String originalClassName = fieldName.substring(0, fieldName.length() - 2);
		try {
			ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
			@SuppressWarnings("unchecked")
			GenericDAO<ModelKey> dao = (GenericDAO<ModelKey>) ctx.getBean(originalClassName + DEFAULT_SPRING_DAO_BEAN);
			ModelKey o = dao.get(fieldValue.toString());
			Object value = ReflectionUtil.getAttributeValue(valueName, o);
			return value;
		} catch (ASException e) {
			return fieldValue;
		} catch (IllegalArgumentException e) {
			return fieldValue;
		} catch (IllegalAccessException e) {
			return fieldValue;
		}
	}
}
