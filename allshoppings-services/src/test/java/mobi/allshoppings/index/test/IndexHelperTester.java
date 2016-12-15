package mobi.allshoppings.index.test;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import mobi.allshoppings.dao.AreaDAO;
import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.dao.ServiceDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.model.Area;
import mobi.allshoppings.model.OfferType;
import mobi.allshoppings.model.Service;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

@ContextConfiguration(locations = {"../../test/testApplicationContext.xml"})
public class IndexHelperTester extends AbstractJUnit4SpringContextTests {

	@Autowired
	IndexHelper indexHelper;
	@Autowired
	UserDAO userDao;
	@Autowired
	ShoppingDAO shoppingDao;
	@Autowired
	AreaDAO areaDao;
	@Autowired
	ServiceDAO serviceDao;
	@Autowired
	OfferTypeDAO offerTypeDao;
	
	@Test
	public void test0001() throws Exception {
		try {

			Range range = new Range(0,50);
			List<User> list = userDao.getUsingStatusAndRange(null, range, "key");
			List<ModelKey> ind = CollectionFactory.createList();
			ind.addAll(list);
			
			indexHelper.clearAll();
			indexHelper.indexObject(ind);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0002() throws Exception {
		try {

			List<User> list = userDao.getUsingIndex("*", null, null, null, null, null, null);
			for( User obj : list ) {
				System.out.println(obj);
			}
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0003() throws Exception {
		try {

			Range range = new Range(0,500);
			List<Shopping> list = shoppingDao.getUsingStatusAndRange(null, range, "key");
			List<ModelKey> ind = CollectionFactory.createList();
			ind.addAll(list);
			
			indexHelper.clearIndex(Shopping.class);
			indexHelper.indexObject(ind);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0004() throws Exception {
		try {

			List<Shopping> list = shoppingDao.getUsingIndex("paque lindavitsa~", null, null, null, null, null, null);
			for( Shopping obj : list ) {
				System.out.println(obj);
			}
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0005() throws Exception {
		try {

			Range range = new Range(0,500);
			List<Area> list = areaDao.getUsingStatusAndRange(null, range, "key");
			List<ModelKey> ind = CollectionFactory.createList();
			ind.addAll(list);

			List<Service> list2 = serviceDao.getUsingStatusAndRange(null, range, "key");
			ind.addAll(list2);
			
			List<OfferType> list3 = offerTypeDao.getUsingStatusAndRange(null, range, "key");
			ind.addAll(list3);
			
			indexHelper.clearIndex(Area.class);
			indexHelper.clearIndex(Service.class);
			indexHelper.clearIndex(OfferType.class);
			indexHelper.indexObject(ind);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}
}
