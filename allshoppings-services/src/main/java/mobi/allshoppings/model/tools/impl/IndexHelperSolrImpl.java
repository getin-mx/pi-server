package mobi.allshoppings.model.tools.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.ASScoredDocument;
import mobi.allshoppings.model.tools.ASSearchField;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.model.tools.MultiLang;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.ValueFinder;

public class IndexHelperSolrImpl implements IndexHelper {

	protected static final Logger logger = Logger.getLogger(IndexHelperSolrImpl.class.getName()); 
	protected static final String[] EMPTY_STRING_ARRAY = new String[0];
	protected static final List<String> INVALID_READ_FIELDS = Arrays.asList(new String[] {"key"});

	protected SolrClient solrSingleton;

	@Autowired
	private SystemConfiguration systemConfiguration;

	/**
	 * Keeps the SolrClient object as a singleton
	 * 
	 * @return The SolrClient singleton
	 */
	public SolrClient getSolrClient() {
		if( solrSingleton == null )
			solrSingleton = new HttpSolrClient.Builder(systemConfiguration.getSolrUrl()).build();
		return solrSingleton;
	}

	/**
	 * Builds a search document with a given ModelKey object
	 * 
	 * @param obj
	 *            The object to build the document from
	 * @param fields
	 *            A list of fields to be included in the document
	 * @return The created document to be added to a search index
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private SolrInputDocument buildDocument(ModelKey obj, String[] fields) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("type", obj.getClass().getName());

		// Append fields to the document builder and build the document itself
		doc = appendFields(doc, obj, fields);

		return doc;
	}

	/**
	 * Appends fields to a document builder
	 * 
	 * @param pp
	 *            The used persistence provider
	 * @param doc
	 *            The document builder to work with
	 * @param obj
	 *            The object to extract the fields from
	 * @param fields
	 *            a list of all the fields to be extracted
	 * @return The same document builder, but with the appended fields
	 * 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private SolrInputDocument appendFields(SolrInputDocument doc, Object obj, String[] fields) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		Map<String, Object> properties = PropertyUtils.describe(obj);

		for (int idx = 0; idx < fields.length; idx++) {
			try {
				String fieldName = fields[idx];
				String fieldParts[] = fieldName.split("\\.");

				// If field is part of a subField
				if( fieldParts.length > 1 ) {
					Object fieldValue = properties.get(fieldParts[0]);
					StringBuffer subValue = new StringBuffer();
					for( int i = 1; i < fieldParts.length; i++ ) {
						subValue.append(fieldParts[i]);
						if( i < (fieldParts.length - 1)) subValue.append(".");
					}
					doc = appendFields(doc, fieldValue, new String[] {subValue.toString()});
				} else {
					Object fieldValue = properties.get(fieldName);
					String idxName = fieldName + "_txt";
					// Process different types for the right output
					if (fieldValue != null) {
						if (fieldValue instanceof Date) {
						} else if (fieldName.equals("status")) {
							doc.addField(idxName, String.valueOf(fieldValue));
						} else if (fieldName.equals("role")) {
							doc.addField(idxName, String.valueOf(fieldValue));
						} else if( fieldValue instanceof Integer || fieldValue instanceof Long || fieldValue instanceof Float || fieldValue instanceof Double ) {
						} else if ("password".equals(fieldName)) {
						} else if ("authToken".equals(fieldName)) {
						} else if (fieldValue instanceof MultiLang ) {
							Iterator<String> i = ((MultiLang) fieldValue).getValues().keySet().iterator();
							while(i.hasNext()) {
								String k = i.next();
								String v = ((MultiLang) fieldValue).get(k);
								doc.addField(idxName, v);
							}
						} else if (fieldValue instanceof Email ) {
							doc.addField(idxName, ((Email)fieldValue).getEmail());
						} else if (fieldValue instanceof Text ) {
							doc.addField(idxName, ((Text)fieldValue).getValue());
						} else if (fieldValue instanceof List ) {
							Iterator<?> i = ((List<?>) fieldValue).iterator();
							while(i.hasNext()) {
								Object o = i.next();
								doc.addField(idxName, ValueFinder.findValueFor(fieldName, o).toString());
							}
						} else if (fieldValue instanceof Set ) {
							Iterator<?> i = ((Set<?>) fieldValue).iterator();
							while(i.hasNext()) {
								Object o = i.next();
								doc.addField(idxName, ValueFinder.findValueFor(fieldName, o).toString());
							}
						} else if ("identifier".equals(fieldName)) {
							doc.addField("id", fieldValue.toString());
						} else if (fieldValue instanceof String) {
							if(!doc.containsKey(idxName)) {
								doc.addField(idxName, fieldValue.toString());
							}
						}
					}
				}
			} catch (Exception e) {
				// ignore property
			}
		}

		return doc;
	}

	/**
	 * Adds an object to the solr index
	 * 
	 * @param obj
	 *            The object to add
	 */
	@Override
	public void indexObject(ModelKey obj) throws ASException {
		List<ModelKey> list = CollectionFactory.createList();
		list.add(obj);
		indexObject(list);
	}

	/**
	 * Adds a list of objects to the solr index
	 * 
	 * @param list
	 *            The list of objects to add
	 */
	@Override
	public void indexObject(List<ModelKey> list) throws ASException {

		try {
			SolrClient solr = getSolrClient();
			List<SolrInputDocument> docList = CollectionFactory.createList();
			for( ModelKey obj : list ) {
				if( obj instanceof Indexable ) {
					String[] fields = guessGenericFields(obj, "all");
					docList.add(buildDocument(obj, fields));
				}
			}
			solr.add(docList);
			solr.commit();
		} catch( Exception e ) {
			logger.log(Level.SEVERE, "Failed to index documents", e);
		}

	}

	/**
	 * Removes an object from its specific Model Search Index and from the
	 * General Search Index
	 * 
	 * @param obj
	 *            The object to be removed
	 */
	public void unIndexObject(String indexName, String key) {
		String[] documentId = new String[] {key};
		unIndexDocument(indexName, documentId);
	}

	/**
	 * Removes an object from its specific Model Search Index and from the
	 * General Search Index
	 * 
	 * @param obj
	 *            The object to be removed
	 */
	public void unIndexObject(ModelKey obj) {
		String docId = obj.getIdentifier();
		unIndexDocument(obj.getClass().getName(), new String[] {docId});
	}

	/**
	 * Removes a document from an Index
	 * 
	 * @param indexName
	 *            The index to remove the document from
	 * @param documentId
	 *            The document ID to remove
	 */
	private void unIndexDocument(String indexName, String[] documentId) {
		try {
			SolrClient solr = getSolrClient();
			for( String identifier : documentId ) {
				solr.deleteByQuery( "type:" + indexName + " AND id:" +identifier);
			}
			solr.commit();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to delete documents", e);
		}
	}

	/**
	 * Clears an entire model Index
	 * 
	 * @param clazz
	 *            The Model class used to identify the Index to clear
	 */
	public void clearIndex(final Class<? extends ModelKey> clazz) {
		clearIndex(clazz.getName());
	}

	/**
	 * Clears an index, removing all its elements
	 * @param indexName The index to clear
	 */
	public void clearIndex(final String indexName) {
		try {
			SolrClient solr = getSolrClient();
			solr.deleteByQuery( "type:" + indexName );
			solr.commit();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to delete documents", e);
		}
	}

	/**
	 * Clears an index, removing all its elements
	 * 
	 * @param indexName
	 *            The index to clear
	 */
	public void clearAll() {
		try {
			SolrClient solr = getSolrClient();
			solr.deleteByQuery( "*:*" );
			solr.commit();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to delete documents", e);
		}
	}

	/**
	 * Makes a query in Solr and return a list of scored documents (search
	 * results)
	 * 
	 * @param indexName
	 *            The index to search
	 * @param queryString
	 *            the query to search for
	 * @return A list of scored documents with the search results
	 */
	public List<ASScoredDocument> query(String indexName, String queryString, Map<String, String> additionalFields) throws ASException {
		List<ASScoredDocument> ret2 = new ArrayList<ASScoredDocument>();
		SolrClient solr = getSolrClient();

		try {
			SolrQuery query = new SolrQuery();
			String qstr = queryString + " AND type:" + indexName;
			if( additionalFields != null ) {
				Iterator<String> it = additionalFields.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					String val = additionalFields.get(key);
					if( val.contains(",")) {
						String[] parts = val.split(",");
						qstr += " AND (";
						for( int i = 0; i < parts.length; i++ ) {
							if( i > 0 ) qstr += " OR ";
							qstr += " " + key + "_txt:" + parts[i];
						}
						qstr += ")";
					} else {
						qstr += " AND " + key + "_txt:" + val;
					}
				}
			}
			query.set("q", qstr);
			query.setRows(100);
			QueryResponse qresponse = solr.query(query);
			SolrDocumentList list = qresponse.getResults();

			Iterator<SolrDocument> i = list.iterator();
			while(i.hasNext()) {
				SolrDocument d = i.next();
				ASScoredDocument asd = new ASScoredDocument();
				Iterator<String> x = d.getFieldValueMap().keySet().iterator();
				while(x.hasNext()) {
					String f = x.next();
					ASSearchField asf = null;
					if( d.getFieldValue(f) instanceof Collection ) {
						Iterator<?> i2 = ((Collection<?>)d.getFieldValues(f)).iterator();
						while( i2.hasNext() ) {
							Object val = i2.next();
							asf = new ASSearchField(f, val.toString());
							asd.addField(asf);
						}
					} else { 
						asf = new ASSearchField(f, d.getFieldValue(f).toString());
						asd.addField(asf);
					}
				}
				ret2.add(asd);
			}

		} catch( NullPointerException npe ) {
			logger.log(Level.INFO, "NullPointerException in search service", npe);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return ret2;
	}

	/**
	 * Obtains an object's field list to index
	 * 
	 * @param obj
	 *            The object to analyze
	 * @param level
	 *            Unused
	 * @return The String list of all the fields to index for that object
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private String[] guessGenericFields(final Object obj, final String level) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> fields = PropertyUtils.describe(obj);
		List<String> outputFields = new ArrayList<String>(fields.keySet());
		outputFields.remove("class");
		Iterator<String> i = fields.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			Object o = fields.get(key);
			if (o != null 
					&& !(o instanceof String) && !(o instanceof Number)
					&& !(o instanceof Date) && !(o instanceof MultiLang)
					&& !(o instanceof Collection) && !(o instanceof Text)
					&& !(o instanceof Key) & !(o instanceof Class)) {
				Map<String, Object> subFields = PropertyUtils.describe(o);
				outputFields.remove(key);
				Iterator<String> i2 = subFields.keySet().iterator();
				while(i2.hasNext()) {
					String subKey = i2.next();
					if(!subKey.equals("class"))
						outputFields.add(key + "." + subKey);
				}
			}
		}

		List<String> ret = CollectionFactory.createList();
		for( String f : outputFields ) {
			if(!isInvalidField(f))
				ret.add(f);
		}

		return ret.toArray(EMPTY_STRING_ARRAY);
	}

	/**
	 * Returns if a field is valid to index or not, checking against the
	 * INVALID_READ_FIELDS lsit
	 * 
	 * @param key
	 *            The field to check for
	 * @return True if the field is valid, False if not
	 */
	public boolean isInvalidField(String key) {
		if( INVALID_READ_FIELDS.contains(key))
			return true;
		else
			return false;
	}
}
