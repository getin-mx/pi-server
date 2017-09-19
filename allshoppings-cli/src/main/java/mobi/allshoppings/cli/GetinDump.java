package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;


public class GetinDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(GetinDump.class.getName());
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
				shopping = shoppingDao.get("bazarcoyoacan", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Bazar Coyoacann");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("bazarcoyoacan"));
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
			try {
				shopping = shoppingDao.get("plazacarso", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Plaza Carso");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("plazacarso"));
				shoppingDao.create(shopping);
			}
			try {
				shopping = shoppingDao.get("plazaloreto", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Plaza Loreto");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("plazaloreto"));
				shoppingDao.create(shopping);
			}
			try {
				shopping = shoppingDao.get("grandsanfrancisco", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Grand San Francisco");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("grandsanfrancisco"));
				shoppingDao.create(shopping);
			}
			
			try {
				shopping = shoppingDao.get("wallmartdemo", true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName("Walmart Demo");
				shopping.setCheckinAreaSize(200);
				shopping.setFenceSize(200);
				shopping.getAddress().setCountry("Mexico");
				shopping.setKey(shoppingDao.createKey("wallmartdemo"));
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
				brand.setName("Flormar Panama");
				brand.setCountry("Panama");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "flormar_pa"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("flormar_co", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Flormar Colombia");
				brand.setCountry("Colombia");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "flormar_co"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("flormar_cr", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Flormar Costa Rica");
				brand.setCountry("Costa Rica");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "flormar_cr"));
				brandDao.create(brand);
			}


			try {
				brand = brandDao.get("lamartina_pa", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("La Martina Panama");
				brand.setCountry("Panama");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "lamartina_pa"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("grandstore_pa", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Grand Store Panama");
				brand.setCountry("Panama");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "grandstore_pa"));
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
				brand = brandDao.get("sunglasshut_pa", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Sunglass Hut Panama");
				brand.setCountry("Panama");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "sunglasshut_pa"));
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
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Prada");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "prada_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("areasmexico_mx", true);
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
				brand.setName("Saboreaté y Café ");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "saboreateycafe_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("volaris_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Volaris");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "volaris_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("latabernadelleon_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("La Taberna Del León");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "latabernadelleon_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("demo4_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Demo 4");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "demo4_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("marketintelligence_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Market Intelligence");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "marketintelligence_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("grupopavel_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Grupo Pavel");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "grupopavel_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("alansolorio_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Alan Solorio");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "alansolorio_mx"));
				brandDao.create(brand);
			}
			try {
				brand = brandDao.get("annik_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("AnniK");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "annik_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("pameladeharo_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Pamela de Haro");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "pameladeharo_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("clubcasablanca_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Club Casablanca");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "clubcasablanca_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("universodefragancias_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Universo de Fragancias");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "universodefragancias_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("aditivo_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Aditivo");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "aditivo_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("98coastav_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("98 Coast Av.");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "98coastav_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("tanyamoss_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Tanya Moss");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "tanyamoss_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("pakmail_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Pakmail");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "pakmail_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("walmart_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Walmart");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "walmart_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("farmaciasyza_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Farmacias YZA");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "farmaciasyza_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("tonymoly_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Tony Moly");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "tonymoly_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("devlyn_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Opticas Devlyn");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "devlyn_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("clarins_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Clarins");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "clarins_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("liverpoolboutiques_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Liverpool Boutiques");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "liverpoolboutiques_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("gameplanet_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Gameplanet");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "gameplanet_mx"));
				brandDao.create(brand);
			}

			try {
				brand = brandDao.get("converse_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Converse");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "converse_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("ecobutik_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Ecobutik");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "ecobutik_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("canalla_bistro_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Canalla Bistro");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "canalla_bistro_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("cafe_balcarce_ar", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Cafe Balcarce");
				brand.setCountry("Argentina");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "cafe_balcarce_ar"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("carolina_herrera_il", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Carolina Herrera");
				brand.setCountry("Israel");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "carolina_herrera_il"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("dentalia_mx", true);
				brand.setStatus(StatusAware.STATUS_DISABLED);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Dentalia");
				brand.setCountry("Mexico");
				brand.setStatus(StatusAware.STATUS_DISABLED);
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "dentalia_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("farmacias_similares_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Farmacias Similares");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "farmacias_similares_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("moda_holding_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Moda Holding");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "moda_holding_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("capadeozono_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Capa de Ozono");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "capadeozono_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("aditivo_franquicias_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Aditivo Franquicias");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "aditivo_franquicias_mx"));
				brandDao.create(brand);
			}
			// error to aditivo franquicias. franquicias_edmond_bcprint_mx == aditivo_franquicias_mx	
			try {
				brand = brandDao.get("franquicias_edmond_bcprint_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Franquicias Edmond BCPrint");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "franquicias_edmond_bcprint_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("aditivo_franquicias_michan_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Aditivo Franquicias Michan");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "aditivo_franquicias_michan_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("mt_sport_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("MT Sport");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "mt_sport_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("elgalpontacuara_ar", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("El galpón de Tacuara");
				brand.setCountry("Argentina");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "elgalpontacuara_ar"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("aditivofranquicias2_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Aditivo Franquicias 2");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "aditivofranquicias2_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("atelier_mx", true);
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Atelier");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "atelier_mx"));
				brandDao.create(brand);
			}
			
			try {
				brand = brandDao.get("fraiche_mx", true);
				brand.setStatus(StatusAware.STATUS_ENABLED); 
		        brandDao.update(brand);
		        log.log(Level.INFO, "Se ha creado la marca "+brand.getName());
			} catch( Exception e ) {
				brand = new Brand();
				brand.setName("Fraiche");
				brand.setCountry("Mexico");
				brand.setKey((Key)keyHelper.obtainKey(Brand.class, "fraiche_mx"));
				brandDao.create(brand);
			}
			// Stores ----------------------------------------------------------------------------------------------------
			List<StoreAdapter> stores = CollectionFactory.createList();
			stores.add(new StoreAdapter("56", "Sportium Lomas Verdes", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("175", "Sportium Satelite", "sportium_mx", "plazasatelite", 0));
			stores.add(new StoreAdapter("176", "Sportium Cuautitlan", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("177", "Sportium Arboledas", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("178", "Sportium Coyoacan", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("179", "Sportium Del Valle", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("180", "Sportium San Angel", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("181", "Sportium Desierto", "sportium_mx", null, 0));
			stores.add(new StoreAdapter("182", "Sportium Santa Fe", "sportium_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("134", "Chilim Balam Toreo", "chilimbalam_mx", "toreoparquecentral", 0));
			stores.add(new StoreAdapter("143", "Chilim Balam Satelite", "chilimbalam_mx", "plazasatelite", 0));
			stores.add(new StoreAdapter("167", "Chilim Balam Santa Fe", "chilimbalam_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("146", "Vicky Form Toreo", "vickyform_mx", "toreoparquecentral", 0));
			stores.add(new StoreAdapter("148", "Vicky Form Lindavista", "vickyform_mx", "parquelindavista", 0));
			stores.add(new StoreAdapter("199", "Vicky Form Parque Tezontle", "vickyform_mx", "parquetezontle", 0));
			stores.add(new StoreAdapter("170", "Finca SantaVeracruz Reforma", "fincasantaveracruz_mx", null, 0));
			stores.add(new StoreAdapter("171", "Finca SantaVeracruz Sevilla", "fincasantaveracruz_mx", null, 0));
			stores.add(new StoreAdapter("173", "Fullsand Perisur", "fullsand_mx", "perisur", 0));
//			stores.add(new StoreAdapter("174", "Fullsand Aragon", "fullsand_mx", "multiplazaaragon", 0));
			stores.add(new StoreAdapter("185", "Invicta Aeropuerto", "invicta_mx", "aeropuertobenitojuarez", 0));
			stores.add(new StoreAdapter("192", "Mobo Antara Old", "mobo_mx", "antara", 0));
			stores.add(new StoreAdapter("193", "Mobo Galerias Coapa Old", "mobo_mx", "galeriascoapa", 0));
			stores.add(new StoreAdapter("194", "Mobo Paseo Acoxpa Old", "mobo_mx", "paseoacoxpa", 0));
			stores.add(new StoreAdapter("195", "Mobo La Cuspide Old", "mobo_mx", "lacuspideskymall", 0));
			stores.add(new StoreAdapter("196", "Mobo Interlomas Old", "mobo_mx", "interlomas", 0));
			stores.add(new StoreAdapter("200", "Mobo El Salvador Old", "mobo_mx", null, 0));
			stores.add(new StoreAdapter("198", "Ferragamo Masaryk", "salvatoreferragamo_mx", null, 0));
			stores.add(new StoreAdapter("206", "Watch My Watch Plaza La Isla", "watchmywatch_mx", null, 0));
			stores.add(new StoreAdapter("207", "Watch My Watch Playa del Carmen", "watchmywatch_mx", null, 0));
			stores.add(new StoreAdapter("208", "Adolfo Dominguez Andares", "adolfodominguez_mx", "centrocomercialandares", 0));
			stores.add(new StoreAdapter("209", "Adolfo Dominguez Interlomas", "adolfodominguez_mx", "interlomas", 0));
			stores.add(new StoreAdapter("211", "Flormar Multiplaza", "flormar_pa", null, 0));
			stores.add(new StoreAdapter("264", "Flormar Metromall", "flormar_pa", null, 0));
			stores.add(new StoreAdapter("265", "Flormar Altaplaza", "flormar_pa", null, 0));
			stores.add(new StoreAdapter("266", "Flormar Multicentro", "flormar_pa", null, 0));
			stores.add(new StoreAdapter("213", "Blu Lagoon Coyoacan Piloto", "blulagoon_mx", "centrocoyoacan", 0));
			stores.add(new StoreAdapter("218", "+Kota Parque Delta", "maskota_mx", "parquedelta", 0));
			stores.add(new StoreAdapter("221", "Deli Cafe Copr Chedraui", "delicafe_mx", null, 0));
			stores.add(new StoreAdapter("222", "Deli Cafe Dos Vistas", "delicafe_mx", null, 0));
			stores.add(new StoreAdapter("223", "Bath and Body Works Santa Fe", "bathandbodyworks_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("224", "Bath and Body Works Satelite", "bathandbodyworks_mx", "plazasatelite", 0));
			stores.add(new StoreAdapter("226", "Modatelas Tacubaya", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("260", "Modatelas Coapa", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("261", "Modatelas Chalco", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("228", "Botanicus Polanco", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("229", "Botanicus Condesa", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("230", "Botanicus Mercado Roma", "botanicus_mx", "mercadoroma", 0));
			stores.add(new StoreAdapter("231", "Botanicus Metropoli Patriotismo", "botanicus_mx", "metropolipatriotismo", 0));
			stores.add(new StoreAdapter("253", "Botanicus La Cuspide", "botanicus_mx", "lacuspideskymall", 0));
			stores.add(new StoreAdapter("254", "Botanicus Bazar Coyoacan", "botanicus_mx", "bazarcoyoacan", 0));
			stores.add(new StoreAdapter("255", "Botanicus Galerias Cuernavaca", "botanicus_mx", "galeriascuernavaca", 0));
			stores.add(new StoreAdapter("256", "Botanicus Plaza Cuernavaca", "botanicus_mx", "plazacuernavaca", 0));
			stores.add(new StoreAdapter("257", "Botanicus Tepoztlan", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("258", "Botanicus Tepozteco", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("263", "Botanicus Malinalco", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("267", "Botanicus San Cristobal", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("268", "Botanicus Angelopolis", "botanicus_mx", "angelopolis", 0));
			stores.add(new StoreAdapter("277", "Botanicus Tuxtla", "botanicus_mx", "angelopolis", 0));
			stores.add(new StoreAdapter("215", "Agasys Escobedo", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("216", "Agasys Juarez", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("232", "Agasys Juarez 2", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("233", "Agasys Benito Juarez 1", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("234", "Agasys Guadalupe 3", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("235", "Agasys Apodaca 2", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("236", "Agasys Monterrey 5", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("237", "Agasys Guadalupe 2", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("238", "Agasys Monterrey 2", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("239", "Agasys Monterrey 4", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("240", "Agasys Apodaca", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("241", "Agasys Zuazua", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("242", "Agasys Guadalupe 1", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("243", "Agasys El Centro o Cris", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("244", "Agasys San Marcos VDM", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("245", "Agasys El Greco VDM", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("246", "Agasys Monterrey 3", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("247", "Agasys Xochimart VDM", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("259", "Agasys El Centro VDM", "agasys_mx", null, 0));
			stores.add(new StoreAdapter("251", "Saavedra Roma Sur", "saavedra_mx", null, 0));
			stores.add(new StoreAdapter("252", "Saavedra Insurgentes Sur", "saavedra_mx", null, 0));
			stores.add(new StoreAdapter("270", "Campobaja Roma", "campobaja_mx", null, 0));
			stores.add(new StoreAdapter("272", "Squalo Patria", "squalo_mx", "plazapatria", 0));
			stores.add(new StoreAdapter("273", "Squalo Vallarta", "squalo_mx", "galeriasvallarta", 0));
			stores.add(new StoreAdapter("274", "Squalo Plaza del Sol", "squalo_mx", "plazadelsol", 0));
			stores.add(new StoreAdapter("275", "Squalo Playa 1", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("276", "Squalo Monterrey", "squalo_mx", "galeriasvalleoriente", 0));
			stores.add(new StoreAdapter("281", "Sunglass Hut Lerma Outlet", "sunglasshut_mx", null, 0));
			stores.add(new StoreAdapter("282", "Sbarro Oasis", "sbarro_mx", "forumbuenavista", 0));
			stores.add(new StoreAdapter("286", "Areas México A59", "areasmexico_mx",null, 0));
			stores.add(new StoreAdapter("287", "Areas México G55", "areasmexico_mx",null, 0));
			stores.add(new StoreAdapter("289", "Areas México A70", "areasmexico_mx",null, 0));
			stores.add(new StoreAdapter("288", "Areas México G58", "areasmexico_mx",null, 0));

			//300 start key hardcode
			stores.add(new StoreAdapter("300", "Adolfo Dominguez Anatole France", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("301", "La Martina Antara", "chomarc_mx","antara", 0));
			stores.add(new StoreAdapter("302", "Adolfo Dominguez Galerías Atizapan", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("303", "U by AD	Galerías Atizapan", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("304", "La Martina Santa Fe", "chomarc_mx","centrosantafe", 0));
			stores.add(new StoreAdapter("305", "Fullsand AICM", "fullsand_mx", null, 0));
			stores.add(new StoreAdapter("306", "Adolfo Dominguez Duraznos", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("307", "Adolfo Dominguez Aguascalientes", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("308", "La Martina Antea Queretaro", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("309", "Adolfo Dominguez Outlet Lomas Verdes", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("310", "Adolfo Dominguez Outlet Castelar", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("311", "La Martina Punta Norte", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("312", "Outlet Deportes Correo Mayor 3", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("313", "Outlet Deportes Correo Mayor 2", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("314", "Outlet Deportes Correo Mayor 1", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("315", "HR Consulting Helguera", "hrconsulting_ar",null, 0));
			stores.add(new StoreAdapter("316","Sally Beauty Perisur", "sallybeauty_mx","perisur", 0));
			stores.add(new StoreAdapter("317","Sally Beauty Centro Coyoacan", "sallybeauty_mx","centrocoyoacan", 0)); 
			stores.add(new StoreAdapter("318","Sally Beauty Mega Coyoacan", "sallybeauty_mx",null, 0)); 
			stores.add(new StoreAdapter("319","Sally Beauty Delta", "sallybeauty_mx","parquedelta", 0));
//			stores.add(new StoreAdapter("320","Sally Beauty Metepec", "sallybeauty_mx",null, 0));
			stores.add(new StoreAdapter("321","Roku Condesa", "roku_mx",null, 0));
			stores.add(new StoreAdapter("322", "Fullsand Galerías Guadalajara", "fullsand_mx", null, 0));
			stores.add(new StoreAdapter("323", "La Nueva Orizaba Central de Abastos", "saavedra_mx", null, 0));
			stores.add(new StoreAdapter("324", "La Parroquia Central de Abastos", "saavedra_mx", null, 0));
			stores.add(new StoreAdapter("325", "La Martina Cancún", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("326", "Adolfo Dominguez Los Cabos", "chomarc_mx",null, 0)); 
			stores.add(new StoreAdapter("327", "Adolfo Dominguez Mérida", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("328", "Adolfo Dominguez Outlet Veracuz", "chomarc_mx",null, 0)); 
			stores.add(new StoreAdapter("329", "Adolfo Dominguez Xalapa", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("330", "Adolfo Dominguez Outlet Xalapa", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("331", "Adolfo Dominguez Coatzacoalcos", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("332", "Adolfo Dominguez Monterrey", "chomarc_mx",null, 0)); 
			stores.add(new StoreAdapter("333", "UNO de 50 Monterrey", "chomarc_mx",null, 0)); 
			stores.add(new StoreAdapter("334", "Adolfo Dominguez Tabasco", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("335", "Adolfo Dominguez Veracruz", "chomarc_mx",null, 0));
			stores.add(new StoreAdapter("336", "Saboreaté Y Café Alameda", "saboreateycafe_mx",null, 0));
			stores.add(new StoreAdapter("337", "Getin lab - Piso 4", "getin_mx",null, 0));
			stores.add(new StoreAdapter("338", "Outlet Deportes San Je 1", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("339", "Outlet Deportes Tepeyac", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("340", "Outlet Deportes Gran Sur", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("341", "Outlet Deportes San Je 3", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("342", "Outlet Deportes San Cosme", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("343", "Outlet Deportes Portal", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("344", "Outlet Deportes Zapamundi", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("345", "Outlet Deportes Tacuba", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("346", "Outlet Deportes Eje Central", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("347", "Outlet Deportes Isabel la Catolica", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("348", "Botanicus San Miguel Umarán", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("349", "Botanicus San Miguel Insurgentes", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("350", "Outlet Deportes Puebla 3","outletdeportes_mx", null, 0));
			stores.add(new StoreAdapter("351", "Outlet Deportes Puebla 2","outletdeportes_mx", null, 0));
//			stores.add(new StoreAdapter("352", "Outlet Deportes Huinala MTY","outletdeportes_mx", null, 0));
			stores.add(new StoreAdapter("353", "Outlet Deportes Leon 1","outletdeportes_mx", null, 0));
			stores.add(new StoreAdapter("354", "Outlet Deportes Leon 2","outletdeportes_mx", null, 0));
			stores.add(new StoreAdapter("355", "Outlet Deportes Leon 3","outletdeportes_mx", null, 0));
			stores.add(new StoreAdapter("356", "Outlet Deportes Hilvana Queretaro","outletdeportes_mx", null, 0));
			stores.add(new StoreAdapter("357", "Botanicus Valle de Bravo", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("358", "Blu Lagoon Santa Teresa", "blulagoon_mx", null, 0));
			stores.add(new StoreAdapter("359", "Blu Lagoon Plaza Carso", "blulagoon_mx", "plazacarso", 0));
			stores.add(new StoreAdapter("360", "Blu Lagoon Coyoacán", "blulagoon_mx", null, 0));
			stores.add(new StoreAdapter("361", "Melissa Duraznos", "blulagoon_mx", null, 0));
			stores.add(new StoreAdapter("362", "Getin lab - Piso 5", "getin_mx",null, 0));
			stores.add(new StoreAdapter("363", "Modatelas Ermita", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("364", "Modatelas Ermita II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("365", "Modatelas Iztapalapa", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("366", "Modatelas Zaragoza", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("367", "Modatelas Coacalco II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("368", "Modatelas Coacalco III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("369", "La Taberna Del León Loreto", "latabernadelleon_mx", null, 0));
			stores.add(new StoreAdapter("370", "Demo4 Santa Fe", "demo4_mx", null, 0));
			stores.add(new StoreAdapter("371", "Demo4 Coacalco", "demo4_mx", null, 0));
			stores.add(new StoreAdapter("372", "Demo4 Toreo", "demo4_mx", null, 0));
			stores.add(new StoreAdapter("373", "Demo4 Tlalnepantla", "demo4_mx", null, 0));
			stores.add(new StoreAdapter("374", "Modatelas Tlalnepantla I", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("375", "Modatelas Tlalnepantla III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("376", "Modatelas Naucalpan", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("377", "Modatelas Ixtapaluca", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("378", "Modatelas Ixtapaluca II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("379", "Market Intelligence	Corregio 1", "marketintelligence_mx", null, 0));
			stores.add(new StoreAdapter("380", "Grupo Pavel Tumi", "grupopavel_mx", null, 0));
			stores.add(new StoreAdapter("381", "Grupo Pavel Inglot", "grupopavel_mx", null, 0));
			//stores.add(new StoreAdapter("382", "Modatelas Ermita", "modatelas_mx", null, 0));
			//stores.add(new StoreAdapter("383", "Modatelas Coacalco II", "modatelas_mx", null, 0)); 
			//stores.add(new StoreAdapter("384", "Modatelas Coacalco III", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("385", "Modatelas Romero Rubio", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("386", "Modatelas Izcalli", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("387", "Modatelas Express Tepotzotlan", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("388", "Modatelas Lago de Guadalupe", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("389", "Modatelas Atizapán", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("390", "Modatelas Tultitlán", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("391", "Modatelas Patio Ayotla", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("392", "Modatelas Express Visitación", "modatelas_mx", null, 0)); 
			stores.add(new StoreAdapter("393", "Modatelas Casas Alemán", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("394", "Alan Solorio San Rafael", "alansolorio_mx", null, 0));
			stores.add(new StoreAdapter("395", "Modatelas Chimalhuacán", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("396", "Modatelas Chimalhuacan III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("397", "Modatelas Plaza Chimalhuacán", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("398", "Volaris Servicios Especiales", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("399", "Volaris Mesa Registro 1", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("400", "Volaris Mesa Registro 2", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("401", "Volaris Mesa Registro 3", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("402", "Volaris Columna Central", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("403", "Volaris Mesa Registro 4", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("404", "Volaris Mesa Registro 5", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("405", "Volaris Mesa Registro 6", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("406", "Volaris Banda Tansportadora 1", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("407", "Volaris Banda Tansportadora 2", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("408", "Volaris Banda Tansportadora 3", "volaris_mx", null, 0));
			stores.add(new StoreAdapter("409", "Best Buy Caja 1", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("410", "Best Buy Caja 2", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("411", "Best Buy Caja 3", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("412", "Best Buy Caja 4", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("413", "Best Buy Lateral 1", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("414", "Best Buy Lateral 2", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("415", "Best Buy Lateral 3", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("416", "Best Buy Lateral 4", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("417", "Best Buy Lateral 5", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("418", "Best Buy Central", "bestbuy_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("419", "Modatelas La Aurora", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("420", "Modatelas Las Maravillas", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("421", "Fullsand Acapulco Diamante", "fullsand_mx", null, 0));			
			stores.add(new StoreAdapter("433", "Monte de Piedad - 96", "montedepiedad_mx", null, 0));
			stores.add(new StoreAdapter("434", "Monte de Piedad - 61", "montedepiedad_mx", null, 0));
			stores.add(new StoreAdapter("435", "Monte de Piedad - 305", "montedepiedad_mx", null, 0));
			stores.add(new StoreAdapter("436", "Botanicus Tlanepantla", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("437", "Modatelas Los Reyes", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("438", "Modatelas Córdoba", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("439", "Modatelas Cd. Mendoza", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("440", "Modatelas Cd Juarez", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("441", "Modatelas Cd. Juarez II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("442", "Modatelas Coatzacoalcos", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("443", "Modatelas Minatitlán", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("444", "Modatelas Fantasy Oaxaca", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("445", "Modatelas Chihuahua II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("446", "Modatelas Juchitán", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("447", "Modatelas Miahuatlan", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("448", "Modatelas Pinotepa", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("449", "Modatelas Oaxaca III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("450", "Modatelas Chihuahua III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("451", "Modatelas Nicolas Romero II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("452", "Modatelas Oaxaca IV", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("453", "Modatelas Martínez de la Torre", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("454", "Modatelas Cd Camargo", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("455", "Modatelas Veracruz III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("456", "Modatelas Cd Juarez III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("457", "Modatelas Tuxpan", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("458", "Modatelas Puerto Escondido", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("459", "Modatelas Salina Cruz", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("460", "Modatelas Cosamaloapan", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("461", "Modatelas Tierra Blanca", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("462", "Modatelas Parral", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("463", "Modatelas Chihuahua IV", "modatelas_mx", null, 0));
			
			stores.add(new StoreAdapter("464", "Annik Desierto de los Leones", "annik_mx", "grandsanfrancisco", 0));
			stores.add(new StoreAdapter("465", "Annik San Jerónimo", "annik_mx", null, 0));
			stores.add(new StoreAdapter("466", "Fullsand La Quinta Playa del Carmen", "fullsand_mx", null, 0));
			stores.add(new StoreAdapter("467", "Volaris AICM Nacional", "volaris_mx", null, 0));

			stores.add(new StoreAdapter("468", "Pamela de Haro Athos", "pameladeharo_mx", null, 0));
			stores.add(new StoreAdapter("469", "Pamela de Haro Emilio Castelar", "pameladeharo_mx", null, 0));

			stores.add(new StoreAdapter("470", "Auxiliar 1", "clubcasablanca_mx", null, 0));
			stores.add(new StoreAdapter("471", "Auxiliar 2", "clubcasablanca_mx", null, 0));
			stores.add(new StoreAdapter("472", "Auxiliar 3", "clubcasablanca_mx", null, 0));
			stores.add(new StoreAdapter("473", "Auxiliar 4", "clubcasablanca_mx", null, 0));
			stores.add(new StoreAdapter("474", "Fitness Center", "clubcasablanca_mx", null, 0));

			stores.add(new StoreAdapter("475", "Outlet Deportes Isabel la Catolica II", "outletdeportes_mx",null, 0));
			stores.add(new StoreAdapter("476", "Fullsand Américas Cancún", "fullsand_mx", null, 0));			

			stores.add(new StoreAdapter("477", "Universo de Fragancias Naucalpan", "universodefragancias_mx", null, 0));			
			stores.add(new StoreAdapter("478", "Aditivo Vidal Alcocer", "aditivo_mx", null, 0));			
			stores.add(new StoreAdapter("479", "98 Coast Av. Oasis Coyoacán", "98coastav_mx", null, 0));			
			stores.add(new StoreAdapter("480", "98 Coast Av. Portal", "98coastav_mx", null, 0));			
			stores.add(new StoreAdapter("481", "BestBuy Santa Fe", "bestbuy_mx", "centrosantafe", 0));			
			stores.add(new StoreAdapter("482", "98 Coast Av. Playa 1", "98coastav_mx", null, 0));			
			stores.add(new StoreAdapter("483", "98 Coast Av. Playa 2", "98coastav_mx", null, 0));			

			stores.add(new StoreAdapter("484", "Flormar Atlantis", "flormar_co", null, 0));
			stores.add(new StoreAdapter("485", "Flormar Jardin Plaza", "flormar_co", null, 0));
			stores.add(new StoreAdapter("486", "Flormar WTC", "flormar_co", null, 0));
			stores.add(new StoreAdapter("487", "Flormar Cacique", "flormar_co", null, 0));
			stores.add(new StoreAdapter("488", "Flormar Lincoln Plaza", "flormar_cr", null, 0));
			stores.add(new StoreAdapter("489", "Sunglass Hut Altaplaza", "sunglasshut_pa", null, 0));
			stores.add(new StoreAdapter("490", "Sunglass Hut Multiplaza", "sunglasshut_pa", null, 0));
			stores.add(new StoreAdapter("491", "Sunglass Hut Metromall", "sunglasshut_pa", null, 0));
			stores.add(new StoreAdapter("492", "Grand Store Altaplaza", "grandstore_pa", null, 0));
			stores.add(new StoreAdapter("493", "La Martina Multiplaza", "lamartina_pa", null, 0));
			
			stores.add(new StoreAdapter("495", "Tanya Moss Plaza Carso", "tanyamoss_mx", "plazacarso", 0));
			stores.add(new StoreAdapter("496", "Tanya Moss Toreo Parque Central", "tanyamoss_mx", "toreoparquecentral", 0));
			stores.add(new StoreAdapter("497", "Tanya Moss Parque Delta", "tanyamoss_mx", "parquedelta", 0));

			stores.add(new StoreAdapter("498", "Aditivo Venustiano Carranza", "aditivo_mx", null, 0));			
			stores.add(new StoreAdapter("499", "Aditivo Chalco 2", "aditivo_mx", null, 0));			
			stores.add(new StoreAdapter("500", "Aditivo Los Reyes", "aditivo_franquicias_michan_mx", null, 0));			
			stores.add(new StoreAdapter("501", "Aditivo El Rosario", "aditivo_mx", null, 0));			
			stores.add(new StoreAdapter("502", "Aditivo Plaza Aragon", "aditivo_mx", null, 0));			
			stores.add(new StoreAdapter("503", "Aditivo Arco Norte", "aditivo_mx", null, 0));			
			stores.add(new StoreAdapter("504", "Aditivo Neza 1", "aditivo_mx", null, 0));			

//			stores.add(new StoreAdapter("505", "Pakmail Parque Lincoln", "pakmail_mx", null, 0));
//			stores.add(new StoreAdapter("506", "Pakmail Plaza Polanco", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("507", "Pakmail Atenas", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("508", "Pakmail Lomas Estrella", "pakmail_mx", null, 0));
//			stores.add(new StoreAdapter("509", "Pakmail Mixcoac", "pakmail_mx", null, 0));
//			stores.add(new StoreAdapter("510", "Pakmail Obrero Mundial", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("511", "Pakmail Roma", "pakmail_mx", null, 0));
//			stores.add(new StoreAdapter("512", "Pakmail Bosque de la Herradura", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("513", "Pakmail Echegaray", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("514", "Pakmail Palo Solo", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("515", "Pakmail San Mateo Atenco", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("516", "Pakmail Metepec", "pakmail_mx", null, 0));
//			stores.add(new StoreAdapter("517", "Pakmail Valle Dorado", "pakmail_mx", null, 0));

			stores.add(new StoreAdapter("518", "Demo", "walmart_mx", "wallmartdemo", 0));

			stores.add(new StoreAdapter("519","Sally Beauty Multiplaza Arboledas", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("520","Sally Beauty Parque Linda Vista", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("521","Sally Beauty La Cúspide Lomas Verdes", "sallybeauty_mx", null, 0));
//			stores.add(new StoreAdapter("522","Sally Beauty Plaza Satélite", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("523","Sally Beauty City Shop", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("524","Sally Beauty Town Center El Rosario", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("525","Sally Beauty Forum Buena Vista", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("526","Sally Beauty Parque Toreo", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("527","Sally Beauty Pabellón Azcapotzalco", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("528","Sally Beauty Gran Sur", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("529","Sally Beauty Pabellón Polanco", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("530","Sally Beauty CM Coapa", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("531","Sally Beauty Paseo Acoxpa ", "sallybeauty_mx", null, 0));
//			stores.add(new StoreAdapter("532","Sally Beauty Galerías Coapa", "sallybeauty_mx", null, 0));
//			stores.add(new StoreAdapter("533","Sally Beauty Oasis Coyoacán", "sallybeauty_mx", null, 0));
			
			stores.add(new StoreAdapter("534","Farmacias YZA Tolentino", "farmaciasyza_mx", null, 0));

			stores.add(new StoreAdapter("535","Tony Moly Oasis Coyoacan", "tonymoly_mx", null, 0));
			stores.add(new StoreAdapter("536","Tony Moly Paseo Acoxpa", "tonymoly_mx", null, 0));

			stores.add(new StoreAdapter("537","Pakmail Coyuya", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("538","Pakmail Felix Cuevas", "pakmail_mx", null, 0));

			stores.add(new StoreAdapter("539","Opticas Devlyn Perisur", "devlyn_mx", null, 0));
			stores.add(new StoreAdapter("540","Opticas Devlyn Parque Delta", "devlyn_mx", null, 0));
			stores.add(new StoreAdapter("541","Opticas Devlyn Plaza Satelite", "devlyn_mx", null, 0));
			stores.add(new StoreAdapter("542","Opticas Devlyn Salamanca", "devlyn_mx", null, 0));
			stores.add(new StoreAdapter("543","Opticas Devlyn Galerias Marina", "devlyn_mx", null, 0));

			stores.add(new StoreAdapter("544","Sally Beauty Rio Churubusco", "sallybeauty_mx",null, 0));

			stores.add(new StoreAdapter("545","GAP Perisur", "liverpoolboutiques_mx",null, 0));
			stores.add(new StoreAdapter("546","Chico's Santa Fe", "liverpoolboutiques_mx",null, 0));
			stores.add(new StoreAdapter("547","Banana Republic Oasis Coyoacan", "liverpoolboutiques_mx",null, 0));

			stores.add(new StoreAdapter("548","Farmacias YZA Tekal", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("549","Farmacias YZA Iman", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("550","Farmacias YZA Horacio", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("551","Farmacias YZA Reforma Marítima", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("552","Farmacias YZA General Anaya", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("553","Farmacias YZA Lazaro", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("554","Farmacias YZA Cuitlahuac", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("555","Farmacias YZA Minas", "farmaciasyza_mx",null, 0));
			stores.add(new StoreAdapter("556","Farmacias YZA Porfirio", "farmaciasyza_mx",null, 0));

			stores.add(new StoreAdapter("557","Clarins Perisur", "clarins_mx",null, 0));

			stores.add(new StoreAdapter("558", "Botanicus El Rosario", "botanicus_mx", null, 0));
			stores.add(new StoreAdapter("559", "Gameplanet Santa Fe I", "gameplanet_mx", null, 0));
			stores.add(new StoreAdapter("560", "Gameplanet Universidad", "gameplanet_mx", null, 0));
			stores.add(new StoreAdapter("561", "Gameplanet Portal San Angel", "gameplanet_mx", null, 0));

			stores.add(new StoreAdapter("562", "Tanya Moss Parque Duraznos", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("563", "Tanya Moss Pabellón Polanco", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("564", "Tanya Moss Altavista", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("565", "Tanya Moss Santa Teresa", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("566", "Tanya Moss Interlomas", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("567", "Tanya Moss Santa Fe", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("568", "Tanya Moss Isla Coyoacan", "tanyamoss_mx", null, 0));
//			stores.add(new StoreAdapter("569", "Tanya Moss Isla Perisur", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("570", "Tanya Moss Andares", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("571", "Tanya Moss Aeropuerto Guadalajara", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("572", "Tanya Moss Angelopolis", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("573", "Tanya Moss Oasis Coyoacan", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("574", "Tanya Moss Aeropuerto CDMX Terminal 2", "tanyamoss_mx", null, 0));
			stores.add(new StoreAdapter("575", "Tanya Moss Palacio de Hierro Polanco", "tanyamoss_mx", null, 0));

			stores.add(new StoreAdapter("576", "Converse Centro Historico", "converse_mx", null, 0));
			stores.add(new StoreAdapter("577", "Merrell Satelite", "converse_mx", null, 0));
			stores.add(new StoreAdapter("578", "Cole Haan Interlomas", "converse_mx", null, 0));

			stores.add(new StoreAdapter("579", "Pakmail Valladolid", "pakmail_mx", null, 0));
			stores.add(new StoreAdapter("580", "Pakmail San Jeronimo", "pakmail_mx", null, 0));

			
			stores.add(new StoreAdapter("283", "Prada Perisur", "prada_mx", "perisur", 0));
			stores.add(new StoreAdapter("284", "Prada Satelite", "prada_mx", "plazasatelite", 0));
			stores.add(new StoreAdapter("290", "Prada Santa Fe", "prada_mx", "centrosantafe", 0));
			stores.add(new StoreAdapter("422", "Prada Coyoacan", "prada_mx", null, 0));
			stores.add(new StoreAdapter("423", "Prada Xalapa", "prada_mx", null, 0));
			stores.add(new StoreAdapter("424", "Prada Galerias Atizapan", "prada_mx", null, 0));
			stores.add(new StoreAdapter("425", "Prada Galerias Tabasco", "prada_mx", null, 0));
			stores.add(new StoreAdapter("426", "Prada Altabrisa (Tabasco II)", "prada_mx", null, 0));
			stores.add(new StoreAdapter("427", "Prada Galerias Serdan", "prada_mx", null, 0));
			stores.add(new StoreAdapter("428", "Prada Parque Toreo", "prada_mx", null, 0));
			stores.add(new StoreAdapter("429", "Prada Plaza Mayor", "prada_mx", null, 0));
			stores.add(new StoreAdapter("430", "Prada Outlet Punta Norte", "prada_mx", null, 0));  
			stores.add(new StoreAdapter("431", "Prada Outlet Galerias", "prada_mx", null, 0));
			stores.add(new StoreAdapter("432", "Prada Outlet Mulza", "prada_mx", null, 0));
			stores.add(new StoreAdapter("494", "Prada Cancun", "prada_mx", null, 0));

			stores.add(new StoreAdapter("581", "Prada Pabellon Polanco", "prada_mx", null, 0));
			stores.add(new StoreAdapter("582", "Prada Galerias Insurgentes", "prada_mx", null, 0));
//			stores.add(new StoreAdapter("583", "Prada MTY", "prada_mx", null, 0));
			stores.add(new StoreAdapter("584", "Prada Galerias Guadalajara", "prada_mx", null, 0));
			stores.add(new StoreAdapter("585", "Prada Angelópolis", "prada_mx", null, 0));
			stores.add(new StoreAdapter("586", "Prada Miramontes/Outlet Zapamundi", "prada_mx", null, 0));
			stores.add(new StoreAdapter("587", "Prada Gran Plaza", "prada_mx", null, 0));
			stores.add(new StoreAdapter("588", "Prada Merida", "prada_mx", null, 0));
			stores.add(new StoreAdapter("589", "Prada Paseo Interlomas", "prada_mx", null, 0));
			stores.add(new StoreAdapter("590", "Prada Outlet Puebla", "prada_mx", null, 0));
			stores.add(new StoreAdapter("591", "Prada Galerias Coapa", "prada_mx", null, 0));
			stores.add(new StoreAdapter("592", "Prada Galerias Toluca", "prada_mx", null, 0));
			stores.add(new StoreAdapter("593", "Prada Parque Via Vallejo", "prada_mx", null, 0));
			stores.add(new StoreAdapter("594", "Prada Parque Delta", "prada_mx", null, 0));
			stores.add(new StoreAdapter("595", "Prada Fashion Drive", "prada_mx", null, 0));
			
			stores.add(new StoreAdapter("596","Pakmail Mariano Otero", "pakmail_mx", null, 0));
			
			stores.add(new StoreAdapter("597","Sally Beauty World Trade Center", "sallybeauty_mx", null, 0));
//			stores.add(new StoreAdapter("598","Sally Beauty Paseo Interlomas", "sallybeauty_mx", null, 0));
			stores.add(new StoreAdapter("599","Sally Beauty Aragón", "sallybeauty_mx", null, 0));
			
			stores.add(new StoreAdapter("600","Ecobutik Centro Histórico", "ecobutik_mx", null, 0));
			stores.add(new StoreAdapter("601","Ecobutik San Angel", "ecobutik_mx", null, 0));
			
			stores.add(new StoreAdapter("602","Canalla Bistro", "canalla_bistro_mx", null, 0));
			
			stores.add(new StoreAdapter("603","Squalo Pabellon", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("604","Squalo Laureles", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("605","Squalo Oaxaca", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("606","Squalo Mazatlan 2", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("607","Squalo San Luis", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("608","Squalo Tuxtla", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("609","Squalo Morelos", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("610","Squalo Merida", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("611","Squalo Morelia.", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("612","Squalo Torreon", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("613","Squalo Manzanillo", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("614","Squalo Chihuahua", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("615","Squalo Gran Plaza", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("616","Squalo Galerias", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("617","Squalo La Isla", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("618","Squalo Tlajomulco", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("619","Squalo Playa 6", "squalo_mx", null, 0));
			stores.add(new StoreAdapter("620","Squalo Mazatlan 1", "squalo_mx", null, 0));
			
//			stores.add(new StoreAdapter("621","Prada Angelópolis", "prada_mx", null, 0));
			stores.add(new StoreAdapter("622","Prada Galerías Valle Oriente", "prada_mx", null, 0));
			
			stores.add(new StoreAdapter("623","Castelar", "cafe_balcarce_ar", null, 0));
			
			stores.add(new StoreAdapter("624","Carolina Herrera", "carolina_herrera_il", null, 0));
			
			stores.add(new StoreAdapter("625","Botanicus El Rosario", "botanicus_mx", null, 0));
			
			stores.add(new StoreAdapter("626","Aditivo Vallejo", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("627","Aditivo Ecatepec", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("628","Aditivo Iztapalapa", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("629","Aditivo Guadalajara", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("630","Aditivo Puebla", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("631","Aditivo Naucalpan", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("632","Aditivo León", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("633","Aditivo Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("634","Aditivo Chalco", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("635","Aditivo Tlalnepantla", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("636","Aditivo Lechería", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("637","Aditivo Cuautitlán", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("638","Aditivo Azteca", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("639","Aditivo Cuautla", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("640","Aditivo Texcoco", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("641","Aditivo Veracruz", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("642","Aditivo Ixtapaluca", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("643","Aditivo Olivar del conde", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("644","Aditivo Pino Suarez", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("645","Aditivo Puebla 3", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("646","Aditivo Toluca", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("647","Aditivo Tecamac", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("648","Aditivo Lazaro Cardenas", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("649","Aditivo Pachuca", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("650","Aditivo Canal del Norte", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("651","Aditivo Pachuca 2", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("652","Aditivo Ecatepec 2", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("653","Aditivo Queretaro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("654","Aditivo San Luis", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("655","Aditivo San Luis Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("656","Aditivo Tula", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("657","Aditivo Neza 2", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("658","Aditivo Nicolas Romero", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("659","Aditivo Metro Pino Suarez", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("660","Aditivo Zumpango", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("661","Aditivo Queretaro Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("662","Aditivo Tizayuca", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("663","Aditivo Tuyehualco", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("664","Aditivo 4 Caminos", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("665","Aditivo Veracruz Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("666","Aditivo Huehuetoca", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("667","Aditivo San Juan del Río", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("668","Aditivo Guadalajara Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("669","Aditivo Atlixco", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("670","Aditivo Apizaco", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("671","Aditivo Los Reyes", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("672","Aditivo Cuajimalpa", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("673","Aditivo Toluca Centro", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("674","Aditivo Plaza Tulyehualco", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("675","Aditivo Tlalnepantla Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("676","Aditivo Chimalhuacan", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("677","Aditivo Salamanca", "aditivo_mx", null, 0));
//			stores.add(new StoreAdapter("678","Aditivo Alcaraz Circuito", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("679","Aditivo Insurgentes", "aditivo_mx", null, 0));
			
			stores.add(new StoreAdapter("680","Saboreaté Y Café Merida", "saboreateycafe_mx", null, 0));
			
			stores.add(new StoreAdapter("681","Mobo República del Salvador", "mobo_mx", null, 0));
			stores.add(new StoreAdapter("682","Mobo Paseo Interlomas", "mobo_mx", null, 0));
			stores.add(new StoreAdapter("683","Mobo Palmas 330", "mobo_mx", null, 0));
			stores.add(new StoreAdapter("684","Mobo Forum Buenavista", "mobo_mx", "forumbuenavista", 0));
			stores.add(new StoreAdapter("685","Mobo Aragon 2", "mobo_mx", null, 0));
			stores.add(new StoreAdapter("686","Mobo Meave piso 1", "mobo_mx", null, 0));
			stores.add(new StoreAdapter("687","Mobo Meave piso 2", "mobo_mx", null, 0));
			
			stores.add(new StoreAdapter("688","Dentalia Garden", "dentalia_mx", null, 0));
			stores.add(new StoreAdapter("689","Dentalia Pabellón Bosques", "dentalia_mx", null, 0));
			stores.add(new StoreAdapter("690","Dentalia Samara", "dentalia_mx", null, 0));
			
			stores.add(new StoreAdapter("691","Mobo Town Center Nicolás Romero", "mobo_mx", null, 0));
			
			stores.add(new StoreAdapter("692","Farmacias Similares Ermita 4", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("693","Farmacias Similares Zaragoza 1", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("694","Farmacias Similares Eje Central 2", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("695","Farmacias Similares Centro 6", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("696","Farmacias Similares Centro 10", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("697","Farmacias Similares Nezahualcóyotl 20", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("698","Farmacias Similares El Olivo", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("699","Farmacias Similares Polanco 1", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("700","Farmacias Similares Apatlaco 1", "farmacias_similares_mx", null, 0));
			stores.add(new StoreAdapter("701","Farmacias Similares Aragón 2", "farmacias_similares_mx", null, 0));
			
			stores.add(new StoreAdapter("702", "Homero", "getin_mx",null, 0));
			
			stores.add(new StoreAdapter("703", "Mobo Aeropuerto Terminal 1", "mobo_mx",null, 0));
			stores.add(new StoreAdapter("704", "Mobo Aeropuerto T2 Llegadas", "mobo_mx",null, 0));
			
			stores.add(new StoreAdapter("705","Aditivo Puebla Centro", "aditivo_mx", null, 0));
			stores.add(new StoreAdapter("706","Aditivo Tlalnepantla", "aditivo_mx", null, 0));
			
			stores.add(new StoreAdapter("707","Moda Holding Dorothy Gaynor Santa Fe II", "moda_holding_mx", null, 0));
			stores.add(new StoreAdapter("708","*Moda Holding Dorothy Gaynor Santa Fe I", "moda_holding_mx", null, 0));
			stores.add(new StoreAdapter("709","Moda Holding Dorothy Gaynor Madero", "moda_holding_mx", null, 0));
			stores.add(new StoreAdapter("710","*Moda Holding Dorothy Gaynor 16 de Septiembre", "moda_holding_mx", null, 0));
//			stores.add(new StoreAdapter("711","Moda Holding Dorothy Gaynor Santa Fe I", "moda_holding_mx", null, 0));
			stores.add(new StoreAdapter("712","Moda Holding Zingara Santa Fe I", "moda_holding_mx", null, 0));
			stores.add(new StoreAdapter("713","Moda Holding Zingara  Santa Fe II", "moda_holding_mx", null, 0));
			
			stores.add(new StoreAdapter("714", "Outlet Deportes 16 de Septiembre", "outletdeportes_mx",null, 0));
			
			stores.add(new StoreAdapter("715", "Aditivo Cortázar", "aditivo_mx",null, 0));
			
			stores.add(new StoreAdapter("716", "Sbarro Perisur", "sbarro_mx",null, 0));
			stores.add(new StoreAdapter("717", "Sbarro Galerias Coapa", "sbarro_mx",null, 0));
			stores.add(new StoreAdapter("718", "Sbarro Acoxpa", "sbarro_mx",null, 0));
//			stores.add(new StoreAdapter("719", "Sbarro Oasis", "sbarro_mx",null, 0));
			stores.add(new StoreAdapter("720", "Sbarro Coyoacán", "sbarro_mx",null, 0));
			stores.add(new StoreAdapter("721", "Sbarro Universidad", "sbarro_mx",null, 0));
			
			stores.add(new StoreAdapter("722", "Mobo Palmas Puente", "mobo_mx",null, 0));
			
			stores.add(new StoreAdapter("723", "Capa de Ozono Santa Fe 1", "capadeozono_mx",null, 0));
			stores.add(new StoreAdapter("724", "Capa de Ozono Santa Fe 2", "capadeozono_mx",null, 0));
			
//			stores.add(new StoreAdapter("725", "Aditivo Franquicia Xochimilco", "franquicias_edmond_bcprint_mx",null, 0));
//			stores.add(new StoreAdapter("726", "Aditivo Franquicia Tlahuac", "franquicias_edmond_bcprint_mx",null, 0));
//			stores.add(new StoreAdapter("727", "Aditivo Franquicia Plaza Central", "franquicias_edmond_bcprint_mx",null, 0));
//			stores.add(new StoreAdapter("728", "Aditivo Franquicia Cuernavaca", "franquicias_edmond_bcprint_mx",null, 0));
			
			stores.add(new StoreAdapter("729", "Aditivo Franquicia Tlahuac", "aditivo_franquicias_mx",null, 0));
			stores.add(new StoreAdapter("730", "Aditivo Franquicia Xochimilco", "aditivo_franquicias_mx",null, 0));
			stores.add(new StoreAdapter("731", "Aditivo Franquicia Plaza Central", "aditivo_franquicias_mx",null, 0));
			stores.add(new StoreAdapter("732", "Aditivo Franquicia Cuernavaca", "aditivo_franquicias_mx",null, 0));
			
			stores.add(new StoreAdapter("733", "Sally Beauty Plaza Insurgentes", "sallybeauty_mx",null, 0));
			
			stores.add(new StoreAdapter("734", "Sportium Santa Fe II", "getin_mx",null, 0));
//			stores.add(new StoreAdapter("735", "Sportium Patio Santa Fe", "sportium_mx",null, 0));
			
			stores.add(new StoreAdapter("736", "Outlet Deportes Motolinea", "outletdeportes_mx",null, 0));
			
			stores.add(new StoreAdapter("737", "Adolfo Dominguez Cancun", "chomarc_mx",null, 0));
			
			stores.add(new StoreAdapter("738", "MT Sport S5 Centro Sur Puebla", "mt_sport_mx",null, 0));
			stores.add(new StoreAdapter("739", "MT Sport S1 Atlixco", "mt_sport_mx",null, 0));
			
			stores.add(new StoreAdapter("740", "El Galpón de Tacuara", "elgalpontacuara_ar",null, 0));
			
			stores.add(new StoreAdapter("741", "Outlet Deportes Venustiano Carranza", "outletdeportes_mx",null, 0));
			
			stores.add(new StoreAdapter("742","Mobo Meave", "mobo_mx", null, 0));
			
			stores.add(new StoreAdapter("743", "Outlet Deportes Punta Norte", "outletdeportes_mx",null, 0));
			
			stores.add(new StoreAdapter("744", "Homero 2", "getin_mx",null, 0));
			
			stores.add(new StoreAdapter("745", "El Galpón de Tacuara Espacio abierto", "elgalpontacuara_ar",null, 0));
			stores.add(new StoreAdapter("746", "El Galpón de Tacuara Cafetería", "elgalpontacuara_ar",null, 0)); 
			
			stores.add(new StoreAdapter("747", "Chilim Balam Parque Lindavista", "chilimbalam_mx", "parquelindavista", 0));
			stores.add(new StoreAdapter("748", "Chilim Balam Forum Buenavista", "chilimbalam_mx", "forumbuenavista", 0));
			stores.add(new StoreAdapter("749", "Chilim Balam Cosmopol", "chilimbalam_mx", null, 0));
			stores.add(new StoreAdapter("750", "Chilim Balam Plaza Ciudad Jardín", "chilimbalam_mx", null, 0));
			stores.add(new StoreAdapter("751", "Chilim Balam Galerías Metepec", "chilimbalam_mx", null, 0));
			stores.add(new StoreAdapter("752", "Chilim Balam Galerias Serdan", "chilimbalam_mx", null, 0));
			stores.add(new StoreAdapter("753", "Chilim Balam Galerías Cuernavaca", "chilimbalam_mx", null, 0));
			stores.add(new StoreAdapter("754", "Chilim Balam Galerías Pachuca", "chilimbalam_mx", null, 0));
			
			stores.add(new StoreAdapter("756", "Squalo Veracruz", "squalo_mx", null, 0));
			
			stores.add(new StoreAdapter("757", "Aditivo Franquicias 2 Miramontes", "aditivofranquicias2_mx",null, 0));
			stores.add(new StoreAdapter("758", "Aditivo Franquicias 2 Izazaga", "aditivofranquicias2_mx",null, 0));
			stores.add(new StoreAdapter("759", "Aditivo Franquicias 2 Coacalco", "aditivofranquicias2_mx",null, 0));
			stores.add(new StoreAdapter("760", "Aditivo Franquicias 2 La Villa", "aditivofranquicias2_mx",null, 0));
			stores.add(new StoreAdapter("761", "Aditivo Franquicias 2 Tehuacán", "aditivofranquicias2_mx",null, 0));
			
			stores.add(new StoreAdapter("762", "Prada Esfera", "prada_mx", null, 0));
			stores.add(new StoreAdapter("763", "Prada Victoria", "prada_mx", null, 0));
			
			stores.add(new StoreAdapter("764", "Adolfo Dominguez Antara", "chomarc_mx",null, 0));
			
			stores.add(new StoreAdapter("765", "Atelier Polanco", "atelier_mx",null, 0));
			stores.add(new StoreAdapter("766", "Atelier Pedregal", "atelier_mx",null, 0));
			stores.add(new StoreAdapter("767", "Atelier Roma", "atelier_mx",null, 0));
			
			stores.add(new StoreAdapter("768", "Modatelas Ixtlahuaca", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("769", "Modatelas Tonala", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("770", "Modatelas Zumpango III", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("771", "Modatelas Morelia IV", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("772", "Modatelas Huahapan De Leon", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("773", "Modatelas Auatlan De Navarro", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("774", "Modatelas Poza Rica II", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("775", "Modatelas San Juan Del Rio", "modatelas_mx", null, 0));
			stores.add(new StoreAdapter("776", "Modatelas Ixmiquilpan", "modatelas_mx", null, 0));
			
			stores.add(new StoreAdapter("777", "Fraiche Chihuahua", "fraiche_mx", null, 0));
			stores.add(new StoreAdapter("778", "Fraiche Heroes de Tecamac", "fraiche_mx", null, 0));
			stores.add(new StoreAdapter("779", "Fraiche Nezahualcoyotl", "fraiche_mx", null, 0));
			stores.add(new StoreAdapter("780", "Fraiche Guelatao", "fraiche_mx", null, 0));
			stores.add(new StoreAdapter("781", "Fraiche Guanajuato", "fraiche_mx", null, 0));
			stores.add(new StoreAdapter("782", "Fraiche Viaducto Piedad", "fraiche_mx", null, 0));
			stores.add(new StoreAdapter("783", "Fraiche Av. Canal Miramontes", "fraiche_mx", null, 0));
			
			stores.add(new StoreAdapter("784", "Pakmail Mixcoac", "pakmail_mx", null, 0));
			
			stores.add(new StoreAdapter("785","Botanicus Polanco 2", "botanicus_mx", null, 0));
			
			stores.add(new StoreAdapter("786","Aditivo Poza Rica", "aditivo_mx", null, 0));
			
			Store store;
			for(StoreAdapter obj : stores ) {
				log.log(Level.INFO, "Processing store" + obj.getName());
				try {
					store = storeDao.getUsingExternalId(obj.getExternalKey());
					store.setName(obj.getName());
					store.setStoreKind(obj.getStoreKind());
					storeDao.update(store);
					log.log(Level.INFO, "Se ha modificado la tienta "+obj.getName());
				} catch( Exception e ) {
					shopping = obj.getShoppingId() == null ? null : shoppingDao.get(obj.getShoppingId(), true);
					brand = brandDao.get(obj.getBrandId(), true);

					store = new Store();
					store.setExternalId(obj.getExternalKey());
					store.setName(obj.getName());
					store.setStoreKind(obj.getStoreKind());
					store.setBrand(brand);
					store.setShopping(shopping);
					store.setAvatarId(brand.getAvatarId());
					store.setKey(obj.getShoppingId() == null ? storeDao.createKey()
							: storeDao.createKey(obj.getShoppingId(), obj.getBrandId()));
					storeDao.create(store);
					log.log(Level.INFO, "Se ha creado la tienta "+obj.getName());
				}
			} 	

			// Assing antennas for droc
			List<String> externalDevices = eaphDao.getExternalHostnames();
			for(String hostname : externalDevices) {
				List<APDAssignation> li = apdaDao.getUsingHostnameAndDate(hostname, new Date());
				if( li.isEmpty() ) {
					APDAssignation assig = new APDAssignation();
					assig.setEntityId("mundoe");
					assig.setEntityKind(EntityKind.KIND_SHOPPING);
					assig.setHostname(hostname);
					assig.setFromDate(sdf.parse("2016-12-17"));
					assig.setKey(apdaDao.createKey(assig));
					apdaDao.create(assig);
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
