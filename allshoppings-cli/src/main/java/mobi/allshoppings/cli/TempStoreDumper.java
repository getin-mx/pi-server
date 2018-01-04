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
				brand = brandDao.get("devlyn_mx", true);
			} catch( Exception e ) {
				log.log(Level.INFO, "Brand not found");
			}
			
			try {
				brand = brandDao.get("demo_devlyn_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Opticas Devlyn Demo");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "demo_devlyn_mx"));
				brandDao.create(brand);
			}

			// Stores ----------------------------------------------------------------------------------------------------
			List<StoreAdapter> stores = CollectionFactory.createList();
			
			stores.add(new StoreAdapter("871", "Ópticas Devlyn Multiplaza Arboledas", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("872", "Ópticas Devlyn Perinorte", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("873", "Ópticas Devlyn Atizapan Alamedas", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("874", "Ópticas Devlyn Mundo E", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("875", "Ópticas Devlyn Satelite I", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("876", "Ópticas Devlyn Plaza Tlalnepantla", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("877", "Ópticas Devlyn Satelite Ii", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("878", "Ópticas Devlyn Valle Dorado", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("879", "Ópticas Devlyn Sams Lomas Verdes", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("880", "Ópticas Devlyn Oah Inm Esmeralda J. Cantu", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("881", "Ópticas Devlyn Chedraui Tenayuca", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("882", "Ópticas Devlyn San Mateo", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("883", "Ópticas Devlyn Outlet Naucalpan", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("884", "Ópticas Devlyn La Cuspide", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("885", "Ópticas Devlyn Oah Che Nicolas Romero Atizapan", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("886", "Ópticas Devlyn Oah Sor Mex Perinorte Mex. - Qro.", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("887", "Ópticas Devlyn Echegaray", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("888", "Ópticas Devlyn Town Center Nicolas Romero", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("889", "Ópticas Devlyn Oah Che Atizapan San Mateo", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("890", "Ópticas Devlyn Copilco", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("891", "Ópticas Devlyn Via Acoxpa", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("892", "Ópticas Devlyn Parque Jardin", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("893", "Ópticas Devlyn Portal San Angel", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("894", "Ópticas Devlyn Coppel Perinorte", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("895", "Ópticas Devlyn Salamanca", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("896", "Ópticas Devlyn Plaza Universidad", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("897", "Ópticas Devlyn Delta I", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("898", "Ópticas Devlyn Solare Coyoacan", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("899", "Ópticas Devlyn Oah Che Anfora", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("900", "Ópticas Devlyn Chedraui El Anfora", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("901", "Ópticas Devlyn Forum Buenavista", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("902", "Ópticas Devlyn Galerias Insurgentes", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("903", "Ópticas Devlyn Delta II", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("904", "Ópticas Devlyn Centro Comercial Perisur", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("905", "Ópticas Devlyn Satelite Ii", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("906", "Ópticas Devlyn Perinorte", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("907", "Ópticas Devlyn Coppel Perinorte", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("908", "Ópticas Devlyn Mundo E", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("909", "Ópticas Devlyn Atizapan Alamedas", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("910", "Ópticas Devlyn Oah Che Atizapan San Mateo", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("911", "Ópticas Devlyn Oah Sor Mex Perinorte Mex. - Qro.", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("912", "Ópticas Devlyn Chedraui Tenayuca", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("913", "Ópticas Devlyn Valle Dorado", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("914", "Ópticas Devlyn Echegaray", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("915", "Ópticas Devlyn Satelite I", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("916", "Ópticas Devlyn Via Vallejo", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("917", "Ópticas Devlyn Patio La Raza", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("918", "Ópticas Devlyn Plaza Tepeyac", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("919", "Ópticas Devlyn Gran Patio Santa Fe", "devlyn_mx", null, TempStoreDumper.MALL));
			stores.add(new StoreAdapter("920", "Ópticas Devlyn Plaza Azcapotzalco", "devlyn_mx", null, TempStoreDumper.MALL));
			
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