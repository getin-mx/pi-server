package mobi.allshoppings.dao.spi;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

import org.springframework.util.StringUtils;

import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.MultiLang;

public class GenericModelKeyComparator implements Comparator<ModelKey> {

	private boolean asc;
	private Method method;
	private String lang;
	
	public GenericModelKeyComparator(Class<?> clazz, String field, boolean asc, String lang) throws NoSuchMethodException, SecurityException {
		this.asc = asc;
		this.lang = lang;
		if( !StringUtils.hasText(this.lang)) lang = "es";
		String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
		method = clazz.getMethod(methodName);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compare(ModelKey o1, ModelKey o2) {
		try {
			Object r1 = method.invoke(o1);
			Object r2 = method.invoke(o2);
			if(!(r1 instanceof Number) && !(r1 instanceof Date) && !(r1 instanceof Collection)) {
				if( r1 != null ) r1 = r1.toString().toLowerCase();
				if( r2 != null ) r2 = r2.toString().toLowerCase();
			}

			if( r1 instanceof MultiLang )
				r1 = ((MultiLang)r1).get(lang);

			if( r2 instanceof MultiLang )
				r2 = ((MultiLang)r2).get(lang);
			
			if( asc ) {
				if( r1 == null ) return -1;
				if( r2 == null ) return 1;
				return ((Comparable)r1).compareTo((Comparable)r2);
			} else {
				if( r1 == null ) return 1;
				if( r2 == null ) return -1;
				return ((Comparable)r2).compareTo((Comparable)r1);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return 0;
		}
	}
}
