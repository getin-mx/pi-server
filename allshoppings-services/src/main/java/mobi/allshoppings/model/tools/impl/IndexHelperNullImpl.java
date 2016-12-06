package mobi.allshoppings.model.tools.impl;

import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.ASScoredDocument;
import mobi.allshoppings.model.tools.IndexHelper;

public class IndexHelperNullImpl implements IndexHelper {

	@Override
	public void indexObject(ModelKey obj) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unIndexObject(ModelKey obj) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unIndexObject(String indexName, String key) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearIndex(Class<? extends ModelKey> clazz) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearIndex(String indexName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ASScoredDocument> query(String indexName, String queryString, Map<String, String> additionalFields)
			throws ASException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void indexObject(List<ModelKey> list) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAll() {
		// TODO Auto-generated method stub
		
	}

}
