package mobi.allshoppings.model.tools;

import mobi.allshoppings.model.interfaces.ModelKey;

public interface KeyHelper {

	<T> T obtainKey(Class<?> clazz, String identifier);

	<T> String obtainEncodedKey(T key);
	String obtainEncodedKey(Class<?> clazz, String identifier);
	
	String obtainIdentifierFromKey(String encodedKey);
	<T> String obtainIdentifierFromKey(T key);

	String resolveKey(String seed);
	
	void setKeyWithIdentifier(ModelKey object, String identifier);
	
	<T> T createNumericUniqueKey(Class<?> clazz);
	<T> T createStringUniqueKey(Class<?> clazz);
	<T> T createStringUniqueKey(Class<?> clazz, String seed);
}