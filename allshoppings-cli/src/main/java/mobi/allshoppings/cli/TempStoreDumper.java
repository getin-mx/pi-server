package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Key;
import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.KeyHelper;

public class TempStoreDumper extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TempStoreDumper.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	//storeKind's
	private static final int FOOT_STREET = 1;
	private static final int MALL = 2;
	private static final int CETRAM =3;
	private static final int KIOSK = 4;
	private static final int DEPARTAMENTAL_STORE = 5;
	private static final int AUTO_SERVICE = 6;
	private static final int AIRPORT = 7;

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");
			ExternalAPHotspotDAO eaphDao = (ExternalAPHotspotDAO)getApplicationContext().getBean("externalaphotspot.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");


			log.log(Level.INFO, "Creating stores...");
			
			// Brands ----------------------------------------------------------------------------------------------------
			Brand brand;

			
			try {
				brand = brandDao.get("trender_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Trender");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "trender_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("asiapacifico_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Asia Pacífico Shoes");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "asiapacifico_mx"));
				brandDao.create(brand);
			    log.log(Level.INFO, "created Asia P...");

			}
			
			
			try {
				brand = brandDao.get("tiendatec_mx", true);
				brandDao.delete(brand);
				throw new Exception();
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Tienda Tec");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "tiendatec_mx"));
				brandDao.create(brand);
			    log.log(Level.INFO, "created tienda tec...");

			}
			
			try {
				brand = brandDao.get("cloe_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Cloe");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "cloe_mx"));
				brandDao.create(brand);
			    log.log(Level.INFO, "created cloe...");

			}


			// Stores ----------------------------------------------------------------------------------------------------
			List<StoreAdapter> stores = CollectionFactory.createList();
			
			stores.add(new StoreAdapter("833", "MT Sport S2 San Martín", "mt_sport_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("834", "MT Sport S4 Tuxtla", "mt_sport_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("835", "MT Sport S10 Tuxtla", "mt_sport_mx", null, TempStoreDumper.MALL));

			stores.add(new StoreAdapter("836", "Trender Parque Tezontle", "trender_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("837", "Trender Parque Delta", "trender_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("838", "Trender Forum Buenavista", "trender_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("839", "Trender Madero", "trender_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("840", "Trender Cosmopol", "trender_mx", null, TempStoreDumper.MALL));
			
			stores.add(new StoreAdapter("841", "Sunglass Hut Vision Ar", "sunglasshut_ar", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("842", "Asia Pacifico Shoes LN001 LI-NING Galerías Serdán", "asiapacifico_mx", null, TempStoreDumper.MALL)); 
			stores.add(new StoreAdapter("843", "Tienda Tec", "tiendatec_mx", null, TempStoreDumper.MALL)); 
			stores.add(new StoreAdapter("844", "Cloe Andares", "cloe_mx", null, TempStoreDumper.MALL)); 
			stores.add(new StoreAdapter("845", "Aditivo Gran Patio Ecatepec", "aditivo_mx", null, TempStoreDumper.MALL)); 
			stores.add(new StoreAdapter("846", "Sally Beauty Oasis Coyoacan", "sallybeauty_mx", null, TempStoreDumper.MALL));


			
			Store store;
			for(StoreAdapter obj : stores ) {
				log.log(Level.INFO, "Processing store" + obj.getName());
				try {
					store = storeDao.getUsingExternalId(obj.getExternalKey());
					store.setName(obj.getName());
					store.setStoreKind(obj.getStoreKind());
					storeDao.update(store);
					log.log(Level.INFO, "Se ha modificado la tienda "+obj.getName());
				} catch( Exception e ) {
					brand = brandDao.get(obj.getBrandId(), true);
					store = new Store();
					store.setTimezone("America/Mexico_City");
					store.setExternalId(obj.getExternalKey());
					store.setName(obj.getName());
					store.setStoreKind(obj.getStoreKind());
					store.setBrand(brand);
					store.setAvatarId(brand.getAvatarId());
					store.setKey(storeDao.createKey());
					storeDao.create(store);
					log.log(Level.INFO, "Se ha creado la tienda "+obj.getName());
				}
			}

		} catch( Exception e ) {
			e.printStackTrace();
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	static class StoreAdapter {
		String externalKey;
		String name;
		String brandId;
		String shoppingId;
		int storeKind;

		public StoreAdapter(String externalKey, String name, String brandId, String shoppingId, int storeKind) {
			super();
			this.externalKey = externalKey;
			this.name = name;
			this.brandId = brandId;
			this.shoppingId = shoppingId;
			if(storeKind > 0 && storeKind < 8){
				this.storeKind = storeKind;
			}else {
				this.storeKind = FOOT_STREET;
			}

		}
		/**
		 * @return the externalKey
		 */
		public String getExternalKey() {
			return externalKey;
		}
		/**
		 * @param externalKey the externalKey to set
		 */
		public void setExternalKey(String externalKey) {
			this.externalKey = externalKey;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the brandId
		 */
		public String getBrandId() {
			return brandId;
		}
		/**
		 * @param brandId the brandId to set
		 */
		public void setBrandId(String brandId) {
			this.brandId = brandId;
		}

		/**
		 *
		 * @return the integer value corresponding to the storeKind type
		 */
		public int getStoreKind() {
			return storeKind;
		}

		/**
		 *
		 * @param storeKind the integer value corresponding to the storeKind type
		 */
		public void setStoreKind(int storeKind) {
			this.storeKind = storeKind;
		}


	}
}