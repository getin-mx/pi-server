package mobi.allshoppings.tools;

import java.util.Map;

import javax.jdo.Query;

public interface CustomDatatableFilter {

	public void delegateFilter(Query query, Map<String, Object> parameters);
	
}
