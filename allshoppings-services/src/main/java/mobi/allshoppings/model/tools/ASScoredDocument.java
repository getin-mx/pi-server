package mobi.allshoppings.model.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ASScoredDocument {

	private final Map<String, List<ASSearchField>> fieldMap;

	public ASScoredDocument() {
		super();
		this.fieldMap = new HashMap<String, List<ASSearchField>>();
	}
	
	public void addField(ASSearchField field) {
		List<ASSearchField> fieldsForName = (List<ASSearchField>) fieldMap.get(field.getName());
		if( fieldsForName == null ) {
			fieldsForName = new ArrayList<ASSearchField>();
		}
		
		fieldsForName.add(field);
		fieldMap.put(field.getName(), fieldsForName);
		
	}

	public Iterable<ASSearchField> getFields() {
		List<ASSearchField> fields = new ArrayList<ASSearchField>();
		Iterator<String> i = fieldMap.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			fields.addAll(fieldMap.get(key));
		}
		return Collections.unmodifiableCollection(fields);
	}	
	
	public Iterable<ASSearchField> getFields(String name) {
		List<ASSearchField> fieldsForName = (List<ASSearchField>) fieldMap.get(name);
		if (fieldsForName == null) {
			return null;
		} else {
			return Collections.unmodifiableList(fieldsForName);
		}
	}

}
