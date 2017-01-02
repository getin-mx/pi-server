package mobi.allshoppings.cli;

import java.util.List;








import joptsimple.OptionParser;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.model.AddressComponentsCache;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Key;
import com.inodes.util.CollectionFactory;


public class GetinDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(GetinDump.class.getName());

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
//			GeoCodingHelper geocoder = (GeoCodingHelper)getApplicationContext().getBean("geocoding.helper");

			log.log(Level.INFO, "Dumping Getin Data....");
			// Shoppings

			// centrosantafe
			// toreoparquecentral
			// parquelindavista
			// parquetezontle
			// antara
			// galeriascoapa
			// paseoacoxpa
			// --> lacuspideskymall
			// interlomas
			// perisur
			// --> multiplazaaragon
			// --> plazasatelite
			// --> aeropuertobenitojuarez
			// --> centrocoyoacan
			// --> parquedelta


			// Brands

			// salvatoreferragamo_mx
			// --> invicta_mx
			// fincasantaveracruz_mx
			// vickyform_mx
			// mobo_mx
			// fullsand_mx
			// chilimbalam_mx
			// sportium_mx
			// --> blulagoon_mx
			// --> colombia
			
			// Shoppings ----------------------------------------------------------------------------------------------------
			Shopping shopping = null;
			try {
				shopping = shoppingDao.get("mundoe", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Mundo E");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("mundoe"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("lacuspideskymall", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("La Cuspide Sky Mall");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("lacuspideskymall"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("multiplazaaragon", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Multiplaza Aragon");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("multiplazaaragon"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("plazasatelite", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Plaza Satelite");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("plazasatelite"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("aeropuertobenitojuarez", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Aeropuerto Benito Juarez");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("aeropuertobenitojuarez"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("parquedelta", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Parque Delta");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("parquedelta"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("centrocoyoacan", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Centro Coyoacan");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("centrocoyoacan"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("mercadoroma", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Mercado Roma");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("mercadoroma"));
				shoppingDao.create(shopping);
			}

			try {
				shopping = shoppingDao.get("metropolipatriotismo", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Metropoli Patriotismo");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("metropolipatriotismo"));
				shoppingDao.create(shopping);
			}


			try {
				shopping = shoppingDao.get("angelopolis", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Angelopolis");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("angelopolis"));
				shoppingDao.create(shopping);
			}
			try {
				shopping = shoppingDao.get("squalo", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("squalo");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("squalo"));
				shoppingDao.create(shopping);
			}
			try {
				shopping = shoppingDao.get("forumbuenavista", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Fórum Buenavista");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("forumbuenavista"));
				shoppingDao.create(shopping);
			}

			// Brands ----------------------------------------------------------------------------------------------------
			Brand brand;
			try {
				brand = brandDao.get("bestbuy_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Best Buy");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "bestbuy_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("invicta_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Invicta");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "invicta_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("blulagoon_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Blu Lagoon");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "blulagoon_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("flormar_pa", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Flormar");
				brand.setCountry("Panama");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "flormar_pa"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("bathandbodyworks_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Bath and Body Works");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "bathandbodyworks_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("delicafe_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Deli Cafe");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "delicafe_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("modatelas_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Modatelas");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "modatelas_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("botanicus_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Botanicus");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "botanicus_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("saavedra_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Saavedra");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "saavedra_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("campobaja_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Campobaja");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "campobaja_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("agasys_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Agasys");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "agasys_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("squalo_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Squalo");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "squalo_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("sunglasshut_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Sunglass Hut");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "sunglasshut_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("sbarro_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Sbarro");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "sbarro_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("prada_mx", true);
				brand.setStatus(StatusAware.STATUS_ENABLED);
				brandDao.update(brand);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Prada");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "prada_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("areasmexico_mx", true);
				brand.setName("Areas México");
				brandDao.update(brand);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Areas México");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "areasmexico_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("montedepiedad_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Monte De Piedad");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "montedepiedad_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("chomarc_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Chomarc");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "chomarc_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("outletdeportes_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Outlet Deportes");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "outletdeportes_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("hrconsulting_ar", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("HR Consulting");
				brand.setCountry("Argentina");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "hrconsulting_ar"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("sallybeauty_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Sally Beauty");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "sallybeauty_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("roku_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Roku");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "roku_mx"));
				brandDao.create(brand);
			}

				try {
					brand = brandDao.get("saboreateycafe_mx", true);
				} catch( Exception e ) {
					brand = new Brand();
					brand.setName("Saboreaté Y Café");
					brand.setCountry("Mexico");
					brand.setKey((Key)keyHelper.obtainKey(Brand.class, "saboreateycafe_mx"));
					brandDao.create(brand);
				}
			
			
			// Stores ----------------------------------------------------------------------------------------------------
			List<StoreAdapter> stores = CollectionFactory.createList();
			stores.add(new StoreAdapter("56", "Sportium Lomas Verdes", "sportium_mx", null));
			stores.add(new StoreAdapter("175", "Sportium Satelite", "sportium_mx", "plazasatelite"));
			stores.add(new StoreAdapter("176", "Sportium Cuautitlan", "sportium_mx", null));
			stores.add(new StoreAdapter("177", "Sportium Arboledas", "sportium_mx", null));
			stores.add(new StoreAdapter("178", "Sportium Coyoacan", "sportium_mx", null));
			stores.add(new StoreAdapter("179", "Sportium Del Valle", "sportium_mx", null));
			stores.add(new StoreAdapter("180", "Sportium San Angel", "sportium_mx", null));
			stores.add(new StoreAdapter("181", "Sportium Desierto", "sportium_mx", null));
			stores.add(new StoreAdapter("182", "Sportium Santa Fe", "sportium_mx", "centrosantafe"));
			stores.add(new StoreAdapter("134", "Chilim Balam Toreo", "chilimbalam_mx", "toreoparquecentral"));
			stores.add(new StoreAdapter("143", "Chilim Balam Satelite", "chilimbalam_mx", "plazasatelite"));
			stores.add(new StoreAdapter("167", "Chilim Balam Santa Fe", "chilimbalam_mx", "centrosantafe"));
			stores.add(new StoreAdapter("146", "Vicky Form Toreo", "vickyform_mx", "toreoparquecentral"));
			stores.add(new StoreAdapter("148", "Vicky Form Lindavista", "vickyform_mx", "parquelindavista"));
			stores.add(new StoreAdapter("199", "Vicky Form Parque Tezontle", "vickyform_mx", "parquetezontle"));
			stores.add(new StoreAdapter("170", "Finca SantaVeracruz Reforma", "fincasantaveracruz_mx", null));
			stores.add(new StoreAdapter("171", "Finca SantaVeracruz Sevilla", "fincasantaveracruz_mx", null));
			stores.add(new StoreAdapter("173", "Fullsand Perisur", "fullsand_mx", "perisur"));
//			stores.add(new StoreAdapter("174", "Fullsand Aragon", "fullsand_mx", "multiplazaaragon"));
			stores.add(new StoreAdapter("185", "Invicta Aeropuerto", "invicta_mx", "aeropuertobenitojuarez"));
			stores.add(new StoreAdapter("192", "Mobo Antara", "mobo_mx", "antara"));
			stores.add(new StoreAdapter("193", "Mobo Galerias Coapa", "mobo_mx", "galeriascoapa"));
			stores.add(new StoreAdapter("194", "Mobo Paseo Acoxpa", "mobo_mx", "paseoacoxpa"));
			stores.add(new StoreAdapter("195", "Mobo La Cuspide", "mobo_mx", "lacuspideskymall"));
			stores.add(new StoreAdapter("196", "Mobo Interlomas", "mobo_mx", "interlomas"));
			stores.add(new StoreAdapter("200", "Mobo El Salvador", "mobo_mx", null));
			stores.add(new StoreAdapter("198", "Ferragamo Masaryk", "salvatoreferragamo_mx", null));

			stores.add(new StoreAdapter("206", "Watch My Watch Plaza La Isla", "watchmywatch_mx", null));
			stores.add(new StoreAdapter("207", "Watch My Watch Playa del Carmen", "watchmywatch_mx", null));

			stores.add(new StoreAdapter("208", "Adolfo Dominguez Andares", "adolfodominguez_mx", "centrocomercialandares"));
			stores.add(new StoreAdapter("209", "Adolfo Dominguez Interlomas", "adolfodominguez_mx", "interlomas"));

			stores.add(new StoreAdapter("211", "Flormar Multiplaza", "flormar_pa", null));
			stores.add(new StoreAdapter("264", "Flormar Metromall", "flormar_pa", null));
			stores.add(new StoreAdapter("265", "Flormar Altaplaza", "flormar_pa", null));
			stores.add(new StoreAdapter("266", "Flormar Multicentro", "flormar_pa", null));

			stores.add(new StoreAdapter("213", "Blu Lagoon Centro Coyoacan", "blulagoon_mx", "centrocoyoacan"));

			stores.add(new StoreAdapter("218", "+Kota Parque Delta", "maskota_mx", "parquedelta"));

			stores.add(new StoreAdapter("221", "Deli Cafe Copr Chedraui", "delicafe_mx", null));
			stores.add(new StoreAdapter("222", "Deli Cafe Dos Vistas", "delicafe_mx", null));

			stores.add(new StoreAdapter("223", "Bath and Body Works Santa Fe", "bathandbodyworks_mx", "centrosantafe"));
			stores.add(new StoreAdapter("224", "Bath and Body Works Satelite", "bathandbodyworks_mx", "plazasatelite"));

			stores.add(new StoreAdapter("226", "Modatelas Tacubaya", "modatelas_mx", null));
			stores.add(new StoreAdapter("260", "Modatelas Coapa", "modatelas_mx", null));
			stores.add(new StoreAdapter("261", "Modatelas Chalco", "modatelas_mx", null));

			stores.add(new StoreAdapter("228", "Botanicus Polanco", "botanicus_mx", null));
			stores.add(new StoreAdapter("229", "Botanicus Condesa", "botanicus_mx", null));
			stores.add(new StoreAdapter("230", "Botanicus Mercado Roma", "botanicus_mx", "mercadoroma"));
			stores.add(new StoreAdapter("231", "Botanicus Metropoli Patriotismo", "botanicus_mx", "metropolipatriotismo"));
			stores.add(new StoreAdapter("253", "Botanicus La Cuspide", "botanicus_mx", "lacuspideskymall"));
			stores.add(new StoreAdapter("254", "Botanicus Interlomas", "botanicus_mx", "interlomas"));
			stores.add(new StoreAdapter("255", "Botanicus Galerias Cuernavaca", "botanicus_mx", "galeriascuernavaca"));
			stores.add(new StoreAdapter("256", "Botanicus Plaza Cuernavaca", "botanicus_mx", "plazacuernavaca"));
			stores.add(new StoreAdapter("257", "Botanicus Tepoztlan", "botanicus_mx", null));
			stores.add(new StoreAdapter("258", "Botanicus Tepozteco", "botanicus_mx", null));
			stores.add(new StoreAdapter("263", "Botanicus Malinalco", "botanicus_mx", null));
			stores.add(new StoreAdapter("267", "Botanicus San Cristobal", "botanicus_mx", null));
			stores.add(new StoreAdapter("268", "Botanicus Angelopolis", "botanicus_mx", "angelopolis"));
			stores.add(new StoreAdapter("277", "Botanicus Tuxtla", "botanicus_mx", "angelopolis"));
			
			
			stores.add(new StoreAdapter("215", "Agasys Escobedo", "agasys_mx", null));
			stores.add(new StoreAdapter("216", "Agasys Juarez", "agasys_mx", null));
			stores.add(new StoreAdapter("232", "Agasys Juarez 2", "agasys_mx", null));
			stores.add(new StoreAdapter("233", "Agasys Benito Juarez 1", "agasys_mx", null));
			stores.add(new StoreAdapter("234", "Agasys Guadalupe 3", "agasys_mx", null));
			stores.add(new StoreAdapter("235", "Agasys Apodaca 2", "agasys_mx", null));
			stores.add(new StoreAdapter("236", "Agasys Monterrey 5", "agasys_mx", null));
			stores.add(new StoreAdapter("237", "Agasys Guadalupe 2", "agasys_mx", null));
			stores.add(new StoreAdapter("238", "Agasys Monterrey 2", "agasys_mx", null));
			stores.add(new StoreAdapter("239", "Agasys Monterrey 4", "agasys_mx", null));
			stores.add(new StoreAdapter("240", "Agasys Apodaca", "agasys_mx", null));
			stores.add(new StoreAdapter("241", "Agasys Zuazua", "agasys_mx", null));
			stores.add(new StoreAdapter("242", "Agasys Guadalupe 1", "agasys_mx", null));
			stores.add(new StoreAdapter("243", "Agasys El Centro o Cris", "agasys_mx", null));
			stores.add(new StoreAdapter("244", "Agasys San Marcos VDM", "agasys_mx", null));
			stores.add(new StoreAdapter("245", "Agasys El Greco VDM", "agasys_mx", null));
			stores.add(new StoreAdapter("246", "Agasys Monterrey 3", "agasys_mx", null));
			stores.add(new StoreAdapter("247", "Agasys Xochimart VDM", "agasys_mx", null));
			stores.add(new StoreAdapter("259", "Agasys El Centro VDM", "agasys_mx", null));
			
			stores.add(new StoreAdapter("251", "Saavedra Roma Sur", "saavedra_mx", null));
			stores.add(new StoreAdapter("252", "Saavedra Insurgentes Sur", "saavedra_mx", null));

			stores.add(new StoreAdapter("270", "Campobaja Roma", "campobaja_mx", null));
			
			stores.add(new StoreAdapter("272", "Squalo Patria", "squalo_mx", "plazapatria"));
			stores.add(new StoreAdapter("273", "Squalo Vallarta", "squalo_mx", "galeriasvallarta"));
			stores.add(new StoreAdapter("274", "Squalo Plaza del Sol", "squalo_mx", "plazadelsol"));
			stores.add(new StoreAdapter("275", "Squalo Playa 1", "squalo_mx", null));
			stores.add(new StoreAdapter("276", "Squalo Monterrey", "squalo_mx", "galeriasvalleoriente"));
			stores.add(new StoreAdapter("281", "Sunglass Hut Lerma Outlet", "sunglasshut_mx", null));
			stores.add(new StoreAdapter("282", "Sbarro Buenavista", "sbarro_mx", "forumbuenavista"));
			stores.add(new StoreAdapter("283", "Prada Perisur", "prada_mx", "perisur"));
			stores.add(new StoreAdapter("284", "Prada Satelite", "prada_mx", "plazasatelite"));
			stores.add(new StoreAdapter("290", "Prada Santa Fe", "prada_mx", "centrosantafe"));

			stores.add(new StoreAdapter("286", "Areas México A59", "areasmexico_mx",null));
			stores.add(new StoreAdapter("287", "Areas México G55", "areasmexico_mx",null));
			stores.add(new StoreAdapter("289", "Areas México A70", "areasmexico_mx",null));
			stores.add(new StoreAdapter("288", "Areas México G58", "areasmexico_mx",null));

			//300 start key hardcode
			stores.add(new StoreAdapter("300", "Adolfo Dominguez Anatole France", "chomarc_mx",null));
			stores.add(new StoreAdapter("301", "La Martina Antara", "chomarc_mx","antara"));
			stores.add(new StoreAdapter("302", "Adolfo Dominguez Galerías Atizapan", "chomarc_mx",null));
			stores.add(new StoreAdapter("303", "U by AD	Galerías Atizapan", "chomarc_mx",null));
			stores.add(new StoreAdapter("304", "La Martina Santa Fe", "chomarc_mx","centrosantafe"));
			stores.add(new StoreAdapter("305", "Fullsand AICM", "fullsand_mx", null));
			
			stores.add(new StoreAdapter("306", "Adolfo Dominguez Duraznos", "chomarc_mx",null));
			stores.add(new StoreAdapter("307", "Adolfo Dominguez Aguascalientes", "chomarc_mx",null));
			stores.add(new StoreAdapter("308", "La Martina Antea Queretaro", "chomarc_mx",null));
			stores.add(new StoreAdapter("309", "Adolfo Dominguez Lomas Verdes", "chomarc_mx",null));
			stores.add(new StoreAdapter("310", "Adolfo Dominguez Emilio Castelar", "chomarc_mx",null));
			stores.add(new StoreAdapter("311", "La Martina Punta Norte", "chomarc_mx",null));
			stores.add(new StoreAdapter("312", "Outlet Deportes	Correo Mayor 48", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("313", "Outlet Deportes	Correo Mayor 71", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("314", "Outlet Deportes	Correo Mayor 81", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("315", "HR Consulting Helguera", "hrconsulting_ar",null));
			stores.add(new StoreAdapter("316","Sally Beauty	Perisur", "sallybeauty_mx","perisur"));
			stores.add(new StoreAdapter("317","Sally Beauty	Centro Coyoacan", "sallybeauty_mx","centrocoyoacan")); 
			stores.add(new StoreAdapter("318","Sally Beauty	Mega Coyoacan", "sallybeauty_mx",null)); 
			stores.add(new StoreAdapter("319","Sally Beauty	Delta", "sallybeauty_mx","parquedelta"));
			stores.add(new StoreAdapter("320","Sally Beauty	Metepec", "sallybeauty_mx",null));
			stores.add(new StoreAdapter("321","Roku Condesa", "roku_mx",null));
			stores.add(new StoreAdapter("322", "Fullsand Galerías Guadalajara", "fullsand_mx", null));
			stores.add(new StoreAdapter("323", "Saavedra La nueva orizaba ", "saavedra_mx", null));
			stores.add(new StoreAdapter("324", "Saavedra La Parroquia", "saavedra_mx", null));

			stores.add(new StoreAdapter("325", "La Martina Cancún", "chomarc_mx",null));
			stores.add(new StoreAdapter("326", "Adolfo Dominguez Los Cabos", "chomarc_mx",null)); 
			stores.add(new StoreAdapter("327", "Adolfo Dominguez Mérida", "chomarc_mx",null));
			stores.add(new StoreAdapter("328", "Adolfo Dominguez Outlet Veracruz", "chomarc_mx",null)); 
			stores.add(new StoreAdapter("329", "Adolfo Dominguez Xalapa", "chomarc_mx",null));
			stores.add(new StoreAdapter("330", "Adolfo Dominguez Outlet Xalapa", "chomarc_mx",null));
			stores.add(new StoreAdapter("331", "Adolfo Dominguez Coaztacoalcos", "chomarc_mx",null));
			stores.add(new StoreAdapter("332", "Adolfo Dominguez Monterrey", "chomarc_mx",null)); 
			stores.add(new StoreAdapter("333", "UNO de 50 Monterrey", "chomarc_mx",null)); 
			stores.add(new StoreAdapter("334", "Adolfo Dominguez Tabasco", "chomarc_mx",null));
			stores.add(new StoreAdapter("335", "Adolfo Dominguez Veracruz", "chomarc_mx",null));

			stores.add(new StoreAdapter("336", "Saboreaté Y Café Alameda", "saboreateycafe_mx",null));
			stores.add(new StoreAdapter("337", "Getin lab - Piso 4", "getin_mx",null));

			stores.add(new StoreAdapter("338", "Outlet Deportes San Je 1", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("339", "Outlet Deportes	Tepeyac", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("340", "Outlet Deportes	Gran Sur", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("341", "Outlet Deportes	San Je 3", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("342", "Outlet Deportes	San Cosme", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("343", "Outlet Deportes	Portal", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("344", "Outlet Deportes	Zapamundi", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("345", "Outlet Deportes	Tacuba", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("346", "Outlet Deportes	Eje Central", "outletdeportes_mx",null));
			stores.add(new StoreAdapter("347", "Outlet Deportes	Isabel la Catolica", "outletdeportes_mx",null));
			
			stores.add(new StoreAdapter("348", "Botanicus San Miguel Umarán", "botanicus_mx", null));
			stores.add(new StoreAdapter("349", "Botanicus San Miguel Insurgentes", "botanicus_mx", null));			
			Store store;
			for(StoreAdapter obj : stores ) {
				try {
					store = storeDao.getUsingExternalId(obj.getExternalKey());
					store.setName(obj.getName());
					storeDao.update(store);
				} catch( Exception e ) {
					shopping = obj.getShoppingId() == null ? null : shoppingDao.get(obj.getShoppingId(), true);
					brand = brandDao.get(obj.getBrandId(), true);

					store = new Store();
					store.setExternalId(obj.getExternalKey());
					store.setName(obj.getName());
					store.setBrand(brand);
					store.setShopping(shopping);
					store.setAvatarId(brand.getAvatarId());
					store.setKey(obj.getShoppingId() == null ? storeDao.createKey()
							: storeDao.createKey(obj.getShoppingId(), obj.getBrandId()));
					storeDao.create(store);
				}
			}

			// Set Sportium stores lat and lon
//			setLatLon(geocoder, storeDao, "56", 19.4952773, -99.2490495);
//			setLatLon(geocoder, storeDao, "175", 19.5079268, -99.2222704);
//			setLatLon(geocoder, storeDao, "176", 19.6391546, -99.2251587);
//			setLatLon(geocoder, storeDao, "177", 19.5471315, -99.2024959);
//			setLatLon(geocoder, storeDao, "178", 19.3278346, -99.1494134);
//			setLatLon(geocoder, storeDao, "179", 19.3737203, -99.1655293);
//			setLatLon(geocoder, storeDao, "180", 19.3451124, -99.1900544);
//			setLatLon(geocoder, storeDao, "181", 19.3427192, -99.2265813);
//			setLatLon(geocoder, storeDao, "182", 19.3772541, -99.2575977);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	public static void setLatLon(GeoCodingHelper geocoder, StoreDAO dao, String externalId, double lat, double lon ) throws ASException {
		Store store = dao.getUsingExternalId(externalId);
		store.getAddress().setLatitude(lat);
		store.getAddress().setLongitude(lon);

		try {
			AddressComponentsCache acc = geocoder.getAddressHLComponents(lat, lon);
			store.getAddress().setCity(acc.getCity());
			store.getAddress().setCountry(acc.getCountry());
		} catch( ASException e ) {
			if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				throw e;
			}
		}
		
		dao.update(store);
	}
	
	static class StoreAdapter {
		String externalKey;
		String name;
		String brandId;
		String shoppingId;

		public StoreAdapter(String externalKey, String name, String brandId, String shoppingId) {
			super();
			this.externalKey = externalKey;
			this.name = name;
			this.brandId = brandId;
			this.shoppingId = shoppingId;
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
		 * @return the shoppingId
		 */
		public String getShoppingId() {
			return shoppingId;
		}
		/**
		 * @param shoppingId the shoppingId to set
		 */
		public void setShoppingId(String shoppingId) {
			this.shoppingId = shoppingId;
		}
	}
}
