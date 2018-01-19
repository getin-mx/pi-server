package mobi.allshoppings.search.test;

import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import junit.framework.TestCase;

public class SolrTest extends TestCase {

	@Test
	public void test0001() {
		try {
			String urlString = "http://nemo.allshoppings.mobi:8983/solr/allshoppingstest";
			SolrClient solr = new HttpSolrClient(urlString);

			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", "552199");
			document.addField("entity", "ent1");
			document.addField("name", "Gouda cheese wheel");
			document.addField("price", "49.99");
			UpdateResponse response = solr.add(document);
			solr.commit();
			System.out.println(response);
			
			SolrQuery query = new SolrQuery();
			query.set("q", "entity:ent1");			
			QueryResponse qresponse = solr.query(query);
			SolrDocumentList list = qresponse.getResults();
			Iterator<SolrDocument> i = list.iterator();
			while( i.hasNext() ) {
				SolrDocument doc = i.next();
				Map<String, Object> map = doc.getFieldValueMap();
				Iterator<String> k = map.keySet().iterator();
				while(k.hasNext()) {
					String key = k.next();
					Object val = map.get(key);
					System.out.println("key: " + key + ", val: " + val + " of type " + val.getClass().getName());
				}
			}
			solr.close();
			
		} catch(Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
