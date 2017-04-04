package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.EntityKind;


public class MacDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(MacDump.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
			APDMAEmployeeDAO apdmaeDao = (APDMAEmployeeDAO)getApplicationContext().getBean("apdmaemployee.dao.ref");

			log.log(Level.INFO, "Dumping MAC Address Data....");
			create("Alejandro Saucedo","40:88:05:34:55:c6",apdmaeDao);
			create("Hector Rodriguez","f4:f5:24:e0:0a:ca",apdmaeDao);
			create("Manuel Hernandez","f4:f5:24:df:92:d3",apdmaeDao);
			create("Jorge Velazquez","f4:f5:24:df:ca:95",apdmaeDao);
			create("Daniel Perez","1c:56:fe:c0:19:ac",apdmaeDao);
			create("Efren Santiago","f4:f5:24:df:ba:84",apdmaeDao);
			create("Gustabo Sandoval","f4:f5:24:df:93:cc",apdmaeDao);
			create("Hugo Rodriguez","5c:a8:6a:28:56:18",apdmaeDao);
			create("Jose Cruz","1c:56:fe:bf:c5:d9",apdmaeDao);
			create("Jose Pacheco","1c:56:fe:bf:c8:bd",apdmaeDao);
			create("Marco Martinez","f4:f5:24:df:cc:ed",apdmaeDao);
			create("Reyes Diaz","1c:56:fe:bf:fd:38",apdmaeDao);
			create("Virgilio Fernandez","1c:56:fe:c0:1e:e1",apdmaeDao);
			create("Adan Hernandez","5c:51:887f:27:fd",apdmaeDao);
			create("Andres Cruz","24:da:9b:61:95:ff",apdmaeDao);
			create("David Gaspar","1c:56:fe:bf:e5:78",apdmaeDao);
			create("Hugo Chavira","40:88:05:5a:6a:88",apdmaeDao);
			create("Aaron Sanchez","f4:f5:24:e1:6f:70",apdmaeDao);
			create("Felipe Banda","f4:f5:24:df:94:5f",apdmaeDao);
			create("Francisco Saldaña","1c:56:fe:bf:ec:c8",apdmaeDao);
			create("Gerardo Soria","5c:51:88:e0:70:a3",apdmaeDao);
			create("Jose Antonio Jimenez","1c:56:fe:bf:b7:7e",apdmaeDao);
			create("Luis Contreras","f4:f5:24:df:99:3c",apdmaeDao);
			create("Manuel Fidel Caracheo","40:88:05:50:94:ce",apdmaeDao);
			create("Mario Chavez","1c:56:fe:c0:18:76",apdmaeDao);
			create("Moises Cabrera","24:da:9b:61:7f:46",apdmaeDao);
			create("Noe Villagomez","b0:45:19:4d:98:bc",apdmaeDao);
			create("Raul Ramirez","1c:56:fe:c0:33:21",apdmaeDao);
			create("Rodolfo Olmedo","1c:56:fe:c0:15:ef",apdmaeDao);
			create("Carlos Isel","1c:56:fe:bf:af:2c",apdmaeDao);
			create("Daniel Gonzalez","1c:56:fe:bf:8e:9d",apdmaeDao);
			create("Jorge Quero","f4:f5:24:e1:5c:d4",apdmaeDao);
			create("Manuel Flores Salazar","f4:f5:24:e0:1a:54",apdmaeDao);
			create("Moises Lerma","f4:f5:24:df:bc:97",apdmaeDao);
			create("Christan Herrera","f4:f5:24:df:9d:17",apdmaeDao);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	public static APDMAEmployee create(String name, String mac, APDMAEmployeeDAO dao) throws ASException {
		try {

			APDMAEmployee obj;

			List<APDMAEmployee> l = dao.getUsingEntityIdandMac("modatelas_mx", EntityKind.KIND_BRAND, mac);
			if( l.size() > 0 ) {
				return l.get(0);
			} else {
				Date fromDate = sdf.parse("2017-01-01");

				obj = new APDMAEmployee();
				obj.setEntityId("modatelas_mx");
				obj.setEntityKind(EntityKind.KIND_BRAND);
				obj.setFromDate(fromDate);
				obj.setMac(mac);
				obj.setDescription(name);
				obj.setKey(dao.createKey());
				dao.create(obj);
			}

			return obj;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
}
