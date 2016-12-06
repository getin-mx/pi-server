package mobi.allshoppings.model.tools;

import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;

public interface IndexHelper {

	public static final String GENERAL_SEARCH_INDEX = "GeneralSearchIndex";

	public void indexObject(ModelKey obj) throws ASException;
	public void indexObject(List<ModelKey> list) throws ASException;
	public void unIndexObject(ModelKey obj) throws ASException;
	public void unIndexObject(String indexName, String key) throws ASException;
	public void clearIndex(final Class<? extends ModelKey> clazz);
	public void clearIndex(final String indexName);
	public void clearAll();
	public List<ASScoredDocument> query(String indexName, String queryString, Map<String, String> additionalFields) throws ASException;
	
}
