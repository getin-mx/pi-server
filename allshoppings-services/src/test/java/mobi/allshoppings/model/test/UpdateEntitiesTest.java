package mobi.allshoppings.model.test;

import java.util.List;

import org.junit.Test;
import org.springframework.util.StringUtils;

import junit.framework.TestCase;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.spi.BrandDAOJDOImpl;
import mobi.allshoppings.dao.spi.OfferDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShoppingDAOJDOImpl;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;

public class UpdateEntitiesTest extends TestCase {

	ShoppingDAO shoppingDao = new ShoppingDAOJDOImpl();
	BrandDAO brandDao = new BrandDAOJDOImpl();
	OfferDAO offerDao = new OfferDAOJDOImpl();

	@Test
	public void test0001() {

		try {
			
			List<Shopping> list = shoppingDao.getAll(true);
			for(Shopping obj : list) {
				shoppingDao.update(obj);
			}
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test0002() {

		try {
			
			List<Brand> list = brandDao.getAll(true);
			for(Brand obj : list) {
				if(!StringUtils.hasText(obj.getCountry())) {
					if( obj.getIdentifier().toLowerCase().endsWith("_mx")) {
						obj.setCountry("Mexico");
					} else if( obj.getIdentifier().toLowerCase().endsWith("_pe")) {
						obj.setCountry("Peru");
					} else if( obj.getIdentifier().toLowerCase().endsWith("_uy")) {
						obj.setCountry("Uruguay");
					} else if( obj.getIdentifier().toLowerCase().endsWith("_pa")) {
						obj.setCountry("Panama");
					} else {
						obj.setCountry("Argentina");
					}
				}
				brandDao.update(obj);
			}
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test0003() {

		try {
			
			List<Offer> list = offerDao.getAll(true);
			for(Offer obj : list) {
				offerDao.update(obj);
			}
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
