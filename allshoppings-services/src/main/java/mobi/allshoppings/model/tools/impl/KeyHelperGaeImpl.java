package mobi.allshoppings.model.tools.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.KeyHelper;

import org.apache.commons.lang3.StringUtils;

import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.KeyFactory;

public class KeyHelperGaeImpl implements KeyHelper, Serializable {

	private static final long serialVersionUID = 1L;
	private static Long lastTime = new Long(0L);
	
	@Override
	public <T> String obtainEncodedKey(T key) {
		if (!(key instanceof Key)) {
			return null;
		}
		
		String encodedKey = KeyFactory.keyToString((Key)key);	
		return encodedKey;
	}
	
	@Override
	public String obtainEncodedKey(Class<?> clazz, String identifier) {
		Key key = this.<Key>obtainKey(clazz, identifier);
		String encodedKey = this.<Key>obtainEncodedKey(key);
		
		return encodedKey;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T obtainKey(Class<?> clazz, String identifier) {
		Key key = KeyFactory.createKey(clazz.getSimpleName(), identifier);
		
		return (T)key;
	}

	@Override
	public String obtainIdentifierFromKey(String encodedKey) {
		String decodedKey = KeyFactory.stringToKey(encodedKey).getName();	
		return decodedKey;
	}
	
	@Override
	public <T> String obtainIdentifierFromKey(T key) {
		if (!(key instanceof Key)) {
			return null;
		}

		String decodedKey = ((Key)key).getName();	
		return decodedKey;
	}
	
	@Override
	public void setKeyWithIdentifier(ModelKey object, String identifier) {
		Class<?> clazz = object.getClass();
		Key key = this.<Key>obtainKey(clazz, identifier);
		object.setKey(key);
	}

	@Override
	public String resolveKey(String seed) {
		String val = seed.toLowerCase()
				.replaceAll("<", "_")
				.replaceAll(">", "_")
				.replaceAll("&", "_")
				.replaceAll("\\+", " ")
				.replaceAll(",", "")
				.replaceAll("%", "")
				.replaceAll("#", "")
				.replaceAll("´", "")
				.replaceAll("`", "")
				.replaceAll("'", "")
				.replaceAll("\\.", "")
				.replaceAll("\"", "")
				.replace("à", "a")
				.replace("è", "e")
				.replace("ì", "i")
				.replace("ò", "o")
				.replace("ù", "u")
				.replace("ñ", "n")
				.replace("À", "a")
				.replace("È", "e")
				.replace("Ì", "i")
				.replace("Ò", "o")
				.replace("Ù", "u")
				.replace("Ñ", "n")
				.trim()
				.replaceAll(" ", "");
		val = StringUtils.stripAccents(val);
		return val;
	}

	@Override
	public synchronized <T> T createNumericUniqueKey(Class<?> clazz) {
		synchronized (lastTime) {
			Long dat = new Date().getTime();
			if( dat == lastTime ) {
				try { Thread.sleep(10); } catch (InterruptedException e) {}
				dat = new Date().getTime();
			}
			String identifier = String.valueOf(dat);
			T key = this.obtainKey(clazz, identifier);
			lastTime = dat;
			return key;
		}
	}
	
	@Override
	public synchronized <T> T createStringUniqueKey(Class<?> clazz, String seed) {
		String identifier = seed;
		T key = this.obtainKey(clazz, identifier);
		return key;
	}

	@Override
	public synchronized <T> T createStringUniqueKey(Class<?> clazz) {
		String identifier = UUID.randomUUID().toString();
		T key = this.obtainKey(clazz, identifier);
		return key;
	}
}
