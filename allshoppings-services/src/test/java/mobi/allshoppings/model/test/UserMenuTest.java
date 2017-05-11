package mobi.allshoppings.model.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.inodes.datanucleus.model.Key;

import junit.framework.TestCase;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.dao.UserMenuDAO;
import mobi.allshoppings.dao.spi.UserDAOJDOImpl;
import mobi.allshoppings.dao.spi.UserMenuDAOJDOImpl;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserMenu;
import mobi.allshoppings.model.UserMenuEntry;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;

public class UserMenuTest extends TestCase {

	UserMenuDAO userMenuDao = new UserMenuDAOJDOImpl();
	UserDAO userDao = new UserDAOJDOImpl();
	KeyHelper keyHelper = new KeyHelperGaeImpl();

	@Test
	public void test0001() {

		try {
			UserMenu um = null;
			try {
				um = userMenuDao.get("admin", true);
				userMenuDao.delete("admin");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.main", "fa-laptop", "Main Page"));
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-laptop", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.applications", "fa-laptop", "Aplicaciones"));
				um.getEntries().add(new UserMenuEntry("index.brands", "fa-laptop", "Cadenas"));
				um.getEntries().add(new UserMenuEntry("index.shoppings", "fa-laptop", "Centros Comerciales"));
				um.getEntries().add(new UserMenuEntry("index.storeemployees", "fa-users", "Employees"));
				um.setKey(userMenuDao.createKey("admin"));
				userMenuDao.create(um);
			}

			User erick = null;
			try {
				erick = userDao.get("erick@yogome.com", true);
			} catch( Exception e ) {
				erick = new User();
				erick.setFirstname("erick");
				erick.setLastname("");
				erick.setEmail("erick@yogome.com");
				erick.getSecuritySettings().setRole(Role.ADMIN);
				erick.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				erick.setKey((Key)keyHelper.obtainKey(User.class, "erick@yogome.com"));
				userDao.create(erick);
			}

			try {
				um = userMenuDao.get("erick@yogome.com", true);
				userMenuDao.delete("erick@yogome.com");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.devicemessage", "fa-laptop", "Envio de Mensajes"));
				um.setKey(userMenuDao.createKey("erick@yogome.com"));
				userMenuDao.create(um);
			}

			User astrid = null;
			try {
				astrid = userDao.get("astrid@getin.mx", true);
			} catch( Exception e ) {
				astrid = new User();
				astrid.setFirstname("Astrid");
				astrid.setLastname("");
				astrid.setEmail("astrid@getin.mx");
				astrid.getSecuritySettings().setRole(Role.ADMIN);
				astrid.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				astrid.setKey((Key)keyHelper.obtainKey(User.class, "astrid@getin.mx"));
				userDao.create(astrid);
			}

			try {
				um = userMenuDao.get("astrid@getin.mx", true);
				userMenuDao.delete("astrid@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("astrid@getin.mx"));
				userMenuDao.create(um);
			}

			User daniel = null;
			try {
				daniel = userDao.get("daniel@getin.mx", true);
			} catch( Exception e ) {
				daniel = new User();
				daniel.setFirstname("Daniel");
				daniel.setLastname("Palacios");
				daniel.setEmail("daniel@getin.mx");
				daniel.getSecuritySettings().setRole(Role.ADMIN);
				daniel.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				daniel.setKey((Key)keyHelper.obtainKey(User.class, "daniel@getin.mx"));
				userDao.create(daniel);
			}

			try {
				um = userMenuDao.get("daniel@getin.mx", true);
				userMenuDao.delete("daniel@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("daniel@getin.mx"));
				userMenuDao.create(um);
			}

			User francisco = null;
			try {
				francisco = userDao.get("francisco@getin.mx", true);
			} catch( Exception e ) {
				francisco = new User();
				francisco.setFirstname("Francisco");
				francisco.setLastname("");
				francisco.setEmail("francisco@getin.mx");
				francisco.getSecuritySettings().setRole(Role.ADMIN);
				francisco.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				francisco.setKey((Key)keyHelper.obtainKey(User.class, "francisco@getin.mx"));
				userDao.create(francisco);
			}

			try {
				um = userMenuDao.get("francisco@getin.mx", true);
				userMenuDao.delete("francisco@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("francisco@getin.mx"));
				userMenuDao.create(um);
			}

			User anabell = null;
			try {
				anabell = userDao.get("anabell@getin.mx", true);
			} catch( Exception e ) {
				anabell = new User();
				anabell.setFirstname("Anabell");
				anabell.setLastname("");
				anabell.setEmail("anabell@getin.mx");
				anabell.getSecuritySettings().setRole(Role.ADMIN);
				anabell.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				anabell.setKey((Key)keyHelper.obtainKey(User.class, "anabell@getin.mx"));
				userDao.create(anabell);
			}

			try {
				um = userMenuDao.get("anabell@getin.mx", true);
				userMenuDao.delete("anabell@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("anabell@getin.mx"));
				userMenuDao.create(um);
			}

			User mariajose = null;
			try {
				mariajose = userDao.get("mariajose@getin.mx", true);
			} catch( Exception e ) {
				mariajose = new User();
				mariajose.setFirstname("Maria Jose");
				mariajose.setLastname("");
				mariajose.setEmail("mariajose@getin.mx");
				mariajose.getSecuritySettings().setRole(Role.ADMIN);
				mariajose.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				mariajose.setKey((Key)keyHelper.obtainKey(User.class, "mariajose@getin.mx"));
				userDao.create(mariajose);
			}

			try {
				um = userMenuDao.get("mariajose@getin.mx", true);
				userMenuDao.delete("mariajose@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("mariajose@getin.mx"));
				userMenuDao.create(um);
			}

			User adrian = null;
			try {
				adrian = userDao.get("adrian@getin.mx", true);
				userDao.delete(adrian);
			} catch( Exception e ) {
			}

			try {
				um = userMenuDao.get("adrian@getin.mx", true);
				userMenuDao.delete("adrian@getin.mx");
			} catch( Exception e ) {
			}
			
			
			User matias = null;
			try {
				matias = userDao.get("matias@getin.mx", true);
			} catch( Exception e ) {
				matias = new User();
				matias.setFirstname("Matias");
				matias.setLastname("Hapanowicz");
				matias.setEmail("matias@getin.mx");
				matias.getSecuritySettings().setRole(Role.ADMIN);
				matias.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				matias.setKey((Key)keyHelper.obtainKey(User.class, "matias@getin.mx"));
				userDao.create(matias);
			}

			try {
				um = userMenuDao.get("matias@getin.mx", true);
				userMenuDao.delete("matias@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("matias@getin.mx"));
				userMenuDao.create(um);
			}

			User eduardo = null;
			try {
				eduardo = userDao.get("eduardo@getin.mx", true);
			} catch( Exception e ) {
				eduardo = new User();
				eduardo.setFirstname("Eduardo");
				eduardo.setLastname("Cardenas");
				eduardo.setEmail("eduardo@getin.mx");
				eduardo.getSecuritySettings().setRole(Role.ADMIN);
				eduardo.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				eduardo.setKey((Key)keyHelper.obtainKey(User.class, "eduardo@getin.mx"));
				userDao.create(eduardo);
			}

			try {
				um = userMenuDao.get("eduardo@getin.mx", true);
				userMenuDao.delete("eduardo@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("eduardo@getin.mx"));
				userMenuDao.create(um);
			}

			User ingrid = null;
			try {
				ingrid = userDao.get("ingrid@getin.mx", true);
			} catch( Exception e ) {
				ingrid = new User();
				ingrid.setFirstname("Ingrid");
				ingrid.setLastname("");
				ingrid.setEmail("ingrid@getin.mx");
				ingrid.getSecuritySettings().setRole(Role.ADMIN);
				ingrid.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				ingrid.setKey((Key)keyHelper.obtainKey(User.class, "ingrid@getin.mx"));
				userDao.create(ingrid);
			}

			try {
				um = userMenuDao.get("ingrid@getin.mx", true);
				userMenuDao.delete("ingrid@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.apdvanalysis", "fa-thermometer-full", "Analisis de Visitas"));
				um.getEntries().add(new UserMenuEntry("index.apdmaemployees", "fa-address-card-o", "Empleados"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-user-o", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("ingrid@getin.mx"));
				userMenuDao.create(um);
			}

			User liverpool = null;
			try {
				liverpool = userDao.get("liverpool_mx", true);
			} catch( Exception e ) {
				liverpool = new User();
				liverpool.setFirstname("Liverpool");
				liverpool.setLastname("Mexico");
				liverpool.setEmail("liverpool@allshoppings.mobi");
				liverpool.getSecuritySettings().setRole(Role.APPLICATION);
				liverpool.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				liverpool.setKey((Key)keyHelper.obtainKey(User.class, "liverpool_mx"));
				userDao.create(liverpool);
			}

			User yogome = null;
			try {
				yogome = userDao.get("yogome_mx", true);
			} catch( Exception e ) {
				yogome = new User();
				yogome.setFirstname("Yogome");
				yogome.setLastname("Mexico");
				yogome.setEmail("yogome@allshoppings.mobi");
				yogome.getSecuritySettings().setRole(Role.APPLICATION);
				yogome.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				yogome.setKey((Key)keyHelper.obtainKey(User.class, "yogome_mx"));
				userDao.create(yogome);
			}

			User amazing = null;
			try {
				amazing = userDao.get("amazing_mx", true);
			} catch( Exception e ) {
				amazing = new User();
				amazing.setFirstname("Amazing App");
				amazing.setLastname("Mexico");
				amazing.setEmail("amazing@allshoppings.mobi");
				amazing.getSecuritySettings().setRole(Role.APPLICATION);
				amazing.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				amazing.setKey((Key)keyHelper.obtainKey(User.class, "amazing_mx"));
				userDao.create(amazing);
			}

			User sportium = null;
			try {
				sportium = userDao.get("sportium_mx", true);
			} catch( Exception e ) {
				sportium = new User();
				sportium.setFirstname("Sportium");
				sportium.setLastname("Mexico");
				sportium.setEmail("sportium@allshoppings.mobi");
				sportium.getSecuritySettings().setRole(Role.BRAND);
				sportium.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				sportium.setKey((Key)keyHelper.obtainKey(User.class, "sportium_mx"));
				userDao.create(sportium);
			}
			User volaris = null;
			try {
				volaris = userDao.get("volaris_mx", true);
				userDao.delete(volaris);
				throw new Exception();
			} catch( Exception e ) {
				volaris = new User();
				volaris.setFirstname("Volaris");
				volaris.setLastname("Mexico");
				volaris.setEmail("volaris@allshoppings.mobi");
				volaris.getSecuritySettings().setRole(Role.BRAND);
				volaris.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				volaris.getSecuritySettings().setShoppings(Arrays.asList("a9f9d78e-d5f6-42b5-97be-2a84aca5165d"));
				volaris.setKey((Key)keyHelper.obtainKey(User.class, "volaris_mx"));
				userDao.create(volaris);
			}
			try {
				um = userMenuDao.get("volaris_mx", true);
				userMenuDao.delete("volaris_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.patternheatmap", "fa-building", "Patrones"));
				um.setKey(userMenuDao.createKey("volaris_mx"));
				userMenuDao.create(um);
			}
			User bestbuy = null;
			try {
				bestbuy = userDao.get("bestbuy_mx", true);
				userDao.delete(bestbuy);
				throw new Exception();
			} catch( Exception e ) {
				bestbuy = new User();
				bestbuy.setFirstname("Best Buy");
				bestbuy.setLastname("Mexico");
				bestbuy.setEmail("bestbuy@allshoppings.mobi");
				bestbuy.getSecuritySettings().setRole(Role.BRAND);
				bestbuy.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				bestbuy.getSecuritySettings().setShoppings(Arrays.asList("1491370940990"));
				bestbuy.setKey((Key)keyHelper.obtainKey(User.class, "bestbuy_mx"));
				userDao.create(bestbuy);
			}
			try {
				um = userMenuDao.get("bestbuy_mx", true);
				userMenuDao.delete("bestbuy_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.patternheatmap", "fa-building", "Patrones"));
				um.setKey(userMenuDao.createKey("bestbuy_mx"));
				userMenuDao.create(um);
			}

			User clubcasablanca = null;
			try {
				clubcasablanca = userDao.get("clubcasablanca_mx", true);
				userDao.delete(clubcasablanca);
				throw new Exception();
			} catch( Exception e ) {
				clubcasablanca = new User();
				clubcasablanca.setFirstname("Club Casablanca");
				clubcasablanca.setLastname("Mexico");
				clubcasablanca.setEmail("clubcasablanca@allshoppings.mobi");
				clubcasablanca.getSecuritySettings().setRole(Role.BRAND);
				clubcasablanca.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				clubcasablanca.setKey((Key)keyHelper.obtainKey(User.class, "clubcasablanca_mx"));
				userDao.create(clubcasablanca);
			}
			try {
				um = userMenuDao.get("clubcasablanca_mx", true);
				userMenuDao.delete("clubcasablanca_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisitsonly", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("clubcasablanca_mx"));
				userMenuDao.create(um);
			}
			
			try {
				um = userMenuDao.get("sportium_mx", true);
				userMenuDao.delete("sportium_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("sportium_mx"));
				userMenuDao.create(um);
			}

			User bathandbodyworks = null;
			try {
				bathandbodyworks = userDao.get("bathandbodyworks_mx", true);
			} catch( Exception e ) {
				bathandbodyworks = new User();
				bathandbodyworks.setFirstname("Bath And Body Works");
				bathandbodyworks.setLastname("Mexico");
				bathandbodyworks.setEmail("bathandbodyworks@allshoppings.mobi");
				bathandbodyworks.getSecuritySettings().setRole(Role.BRAND);
				bathandbodyworks.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				bathandbodyworks.setKey((Key)keyHelper.obtainKey(User.class, "bathandbodyworks_mx"));
				userDao.create(bathandbodyworks);
			}

			try {
				um = userMenuDao.get("bathandbodyworks_mx", true);
				userMenuDao.delete("bathandbodyworks_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("bathandbodyworks_mx"));
				userMenuDao.create(um);
			}

			User universodefragancias = null;
			try {
				universodefragancias = userDao.get("universodefragancias_mx", true);
			} catch( Exception e ) {
				universodefragancias = new User();
				universodefragancias.setFirstname("Universo de Fragancias");
				universodefragancias.setLastname("Mexico");
				universodefragancias.setEmail("universodefragancias@allshoppings.mobi");
				universodefragancias.getSecuritySettings().setRole(Role.BRAND);
				universodefragancias.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				universodefragancias.setKey((Key)keyHelper.obtainKey(User.class, "universodefragancias_mx"));
				userDao.create(universodefragancias);
			}

			try {
				um = userMenuDao.get("universodefragancias_mx", true);
				userMenuDao.delete("universodefragancias_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("universodefragancias_mx"));
				userMenuDao.create(um);
			}

			User delicafe = null;
			try {
				delicafe = userDao.get("delicafe_mx", true);
			} catch( Exception e ) {
				delicafe = new User();
				delicafe.setFirstname("Deli Cafe");
				delicafe.setLastname("Mexico");
				delicafe.setEmail("delicafe@allshoppings.mobi");
				delicafe.getSecuritySettings().setRole(Role.BRAND);
				delicafe.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				delicafe.setKey((Key)keyHelper.obtainKey(User.class, "delicafe_mx"));
				userDao.create(delicafe);
			}

			try {
				um = userMenuDao.get("delicafe_mx", true);
				userMenuDao.delete("delicafe_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("delicafe_mx"));
				userMenuDao.create(um);
			}
			
			User montedepiedad = null;
			try {
				montedepiedad = userDao.get("montedepiedad_mx", true);
			} catch( Exception e ) {
				montedepiedad = new User();
				montedepiedad.setFirstname("Monte De Piedad");
				montedepiedad.setLastname("Mexico");
				montedepiedad.setEmail("montedepiedad@allshoppings.mobi");
				montedepiedad.getSecuritySettings().setRole(Role.BRAND);
				montedepiedad.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				montedepiedad.setKey((Key)keyHelper.obtainKey(User.class, "montedepiedad_mx"));
				userDao.create(montedepiedad);
			}

			try {
				um = userMenuDao.get("montedepiedad_mx", true);
				userMenuDao.delete("montedepiedad_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("montedepiedad_mx"));
				userMenuDao.create(um);
			}

//			User campobaja = null;
//			try {
//				campobaja = userDao.get("campobaja_mx", true);
//			} catch( Exception e ) {
//				campobaja = new User();
//				campobaja.setFirstname("Campobaja");
//				campobaja.setLastname("Mexico");
//				campobaja.setEmail("campobaja@allshoppings.mobi");
//				campobaja.getSecuritySettings().setRole(Role.BRAND);
//				campobaja.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
//				campobaja.setKey((Key)keyHelper.obtainKey(User.class, "campobaja_mx"));
//				userDao.create(campobaja);
//			}

			try {
				um = userMenuDao.get("campobaja_mx", true);
				userMenuDao.delete("campobaja_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("campobaja_mx"));
				userMenuDao.create(um);
			}

			User modatelas = null;
			try {
				modatelas = userDao.get("modatelas_mx", true);
				userDao.delete(modatelas);
				throw new Exception();
			} catch( Exception e ) {
//				modatelas = new User();
//				modatelas.setFirstname("Modatelas");
//				modatelas.setLastname("Mexico");
//				modatelas.setEmail("modatelas@allshoppings.mobi");
//				modatelas.getSecuritySettings().setRole(Role.BRAND);
//				modatelas.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
//				modatelas.setKey((Key)keyHelper.obtainKey(User.class, "modatelas_mx"));
//				modatelas.getSecuritySettings().setShoppings(Arrays.asList("740547b3-5c3a-492c-a2f8-bc88345fcc5d"));
//				userDao.create(modatelas);
			}

			try {
				um = userMenuDao.get("modatelas_mx", true);
				userMenuDao.delete("modatelas_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.patternheatmap", "fa-building", "Patrones"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.setKey(userMenuDao.createKey("modatelas_mx"));
				userMenuDao.create(um);
			}

			User botanicus = null;
			try {
				botanicus = userDao.get("botanicus_mx", true);
			} catch( Exception e ) {
				botanicus = new User();
				botanicus.setFirstname("Botanicus");
				botanicus.setLastname("Mexico");
				botanicus.setEmail("botanicus@allshoppings.mobi");
				botanicus.getSecuritySettings().setRole(Role.BRAND);
				botanicus.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				botanicus.setKey((Key)keyHelper.obtainKey(User.class, "botanicus_mx"));
				userDao.create(botanicus);
			}

			try {
				um = userMenuDao.get("botanicus_mx", true);
				userMenuDao.delete("botanicus_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("botanicus_mx"));
				userMenuDao.create(um);
			}
			
			User prada = null;
			try {
				prada = userDao.get("prada_mx", true);
			} catch( Exception e ) {
				prada = new User();
				prada.setFirstname("Prada");
				prada.setLastname("Mexico");
				prada.setEmail("prada@allshoppings.mobi");
				prada.getSecuritySettings().setRole(Role.BRAND);
				prada.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				prada.setKey((Key)keyHelper.obtainKey(User.class, "prada_mx"));
				userDao.create(prada);
			}

			try {
				um = userMenuDao.get("prada_mx", true);
				userMenuDao.delete("prada_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("prada_mx"));
				userMenuDao.create(um);
			}
			
			User sbarro = null;
			try {
				sbarro = userDao.get("sbarro_mx", true);
				userDao.delete("sbarro_mx");
				throw new Exception();
			} catch( Exception e ) {
//				sbarro = new User();
//				sbarro.setFirstname("Sbarro");
//				sbarro.setLastname("Mexico");
//				sbarro.setEmail("sbarro@allshoppings.mobi");
//				sbarro.getSecuritySettings().setRole(Role.BRAND);
//				sbarro.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
//				sbarro.setKey((Key)keyHelper.obtainKey(User.class, "sbarro_mx"));
//				userDao.create(sbarro);
			}

			try {
				um = userMenuDao.get("sbarro_mx", true);
				userMenuDao.delete("sbarro_mx");
				throw new Exception();
			} catch( Exception e ) {
//				um = new UserMenu();
//				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
//				um.setKey(userMenuDao.createKey("sbarro_mx"));
//				userMenuDao.create(um);
			}
			
			User sunglasshut = null;
			try {
				sunglasshut = userDao.get("sunglasshut_mx", true);
			} catch( Exception e ) {
				sunglasshut = new User();
				sunglasshut.setFirstname("Sun Glass Hut");
				sunglasshut.setLastname("Mexico");
				sunglasshut.setEmail("sunglasshut@allshoppings.mobi");
				sunglasshut.getSecuritySettings().setRole(Role.BRAND);
				sunglasshut.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				sunglasshut.setKey((Key)keyHelper.obtainKey(User.class, "sunglasshut_mx"));
				userDao.create(sunglasshut);
			}

			try {
				um = userMenuDao.get("sunglasshut_mx", true);
				userMenuDao.delete("sunglasshut_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("sunglasshut_mx"));
				userMenuDao.create(um);
			}
			
			User squalo = null;
			try {
				squalo = userDao.get("squalo_mx", true);
			} catch( Exception e ) {
				squalo = new User();
				squalo.setFirstname("Squalo");
				squalo.setLastname("Mexico");
				squalo.setEmail("squalo@allshoppings.mobi");
				squalo.getSecuritySettings().setRole(Role.BRAND);
				squalo.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				squalo.setKey((Key)keyHelper.obtainKey(User.class, "squalo_mx"));
				userDao.create(squalo);
			}

			try {
				um = userMenuDao.get("squalo_mx", true);
				userMenuDao.delete("squalo_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("squalo_mx"));
				userMenuDao.create(um);
			}
			
			User areasmexico = null;
			try {
				areasmexico = userDao.get("areasmexico_mx", true);
			} catch( Exception e ) {
				areasmexico = new User();
				areasmexico.setFirstname("Areas Mexico");
				areasmexico.setLastname("Mexico");
				areasmexico.setEmail("areasmexico@allshoppings.mobi");
				areasmexico.getSecuritySettings().setRole(Role.BRAND);
				areasmexico.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				areasmexico.setKey((Key)keyHelper.obtainKey(User.class, "areasmexico_mx"));
				userDao.create(areasmexico);
			}

			try {
				um = userMenuDao.get("areasmexico_mx", true);
				userMenuDao.delete("areasmexico_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("areasmexico_mx"));
				userMenuDao.create(um);
			}

			User saavedra = null;
			try {
				saavedra = userDao.get("saavedra_mx", true);
			} catch( Exception e ) {
				saavedra = new User();
				saavedra.setFirstname("Saavedra");
				saavedra.setLastname("Mexico");
				saavedra.setEmail("saavedra@allshoppings.mobi");
				saavedra.getSecuritySettings().setRole(Role.BRAND);
				saavedra.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				saavedra.setKey((Key)keyHelper.obtainKey(User.class, "saavedra_mx"));
				userDao.create(saavedra);
			}

			try {
				um = userMenuDao.get("saavedra_mx", true);
				userMenuDao.delete("saavedra_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("saavedra_mx"));
				userMenuDao.create(um);
			}

//			User demo = null;
//			try {
//				demo = userDao.get("demo_mx", true);
//			} catch( Exception e ) {
//				demo = new User();
//				demo.setFirstname("Demo");
//				demo.setLastname("Mexico");
//				demo.setEmail("demo@allshoppings.mobi");
//				demo.getSecuritySettings().setRole(Role.BRAND);
//				demo.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
//				demo.setKey((Key)keyHelper.obtainKey(User.class, "demo_mx"));
//				userDao.create(demo);
//			}

			try {
				um = userMenuDao.get("demo_mx", true);
				userMenuDao.delete("demo_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.demovisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.demomap", "fa-map-marker", "Mapas"));
				um.getEntries().add(new UserMenuEntry("index.demoareas", "fa-area-chart", "Areas de Afluencia"));
				um.setKey(userMenuDao.createKey("demo_mx"));
				userMenuDao.create(um);
			}

			User devlyn = null;
			try {
				devlyn = userDao.get("devlyn_mx", true);
			} catch( Exception e ) {
				devlyn = new User();
				devlyn.setFirstname("devlyn");
				devlyn.setLastname("Mexico");
				devlyn.setEmail("devlyn@allshoppings.mobi");
				devlyn.getSecuritySettings().setRole(Role.BRAND);
				devlyn.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				devlyn.setKey((Key)keyHelper.obtainKey(User.class, "devlyn_mx"));
				userDao.create(devlyn);
			}

			try {
				um = userMenuDao.get("devlyn_mx", true);
				userMenuDao.delete("devlyn_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.demovisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.demomap", "fa-map-marker", "Mapas"));
				um.setKey(userMenuDao.createKey("devlyn_mx"));
				userMenuDao.create(um);
			}

			User demo2 = null;
			try {
				demo2 = userDao.get("demo2_mx", true);
			} catch( Exception e ) {
				demo2 = new User();
				demo2.setFirstname("Demo");
				demo2.setLastname("Mexico");
				demo2.setEmail("demo2@allshoppings.mobi");
				demo2.getSecuritySettings().setRole(Role.BRAND);
				demo2.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				demo2.setKey((Key)keyHelper.obtainKey(User.class, "demo2_mx"));
				userDao.create(demo2);
			}

			try {
				um = userMenuDao.get("demo2_mx", true);
				userMenuDao.delete("demo2_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.demovisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.demomap", "fa-map-marker", "Mapas"));
				um.setKey(userMenuDao.createKey("demo2_mx"));
				userMenuDao.create(um);
			}

			User demo3 = null;
			try {
				demo3 = userDao.get("demo3_mx", true);
			} catch( Exception e ) {
				demo3 = new User();
				demo3.setFirstname("Demo");
				demo3.setLastname("Mexico");
				demo3.setEmail("demo3@allshoppings.mobi");
				demo3.getSecuritySettings().setRole(Role.BRAND);
				demo3.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				demo3.setKey((Key)keyHelper.obtainKey(User.class, "demo3_mx"));
				userDao.create(demo3);
			}

			try {
				um = userMenuDao.get("demo3_mx", true);
				userMenuDao.delete("demo3_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.demovisits3", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("demo3_mx"));
				userMenuDao.create(um);
			}

			User demoar = null;
			try {
				demoar = userDao.get("demo_ar", true);
			} catch( Exception e ) {
				demoar = new User();
				demoar.setFirstname("Demo");
				demoar.setLastname("Argentina");
				demoar.setEmail("demoar@allshoppings.mobi");
				demoar.getSecuritySettings().setRole(Role.STORE);
				demoar.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				demoar.setKey((Key)keyHelper.obtainKey(User.class, "demo_ar"));
				demoar.getSecuritySettings()
					.setStores(Arrays.asList("2036683d-9340-4f2b-a610-864727158baf","badcd7d9-706d-44d7-9ce1-a22455421f56","4aab445c-a80e-4ff2-8ecc-c565aff660d2","11b52fd5-44c7-460b-bdb2-dc9dc45f4a03"));
				userDao.create(demoar);
			}

			try {
				um = userMenuDao.get("demo_ar", true);
				userMenuDao.delete("demo_ar");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.setKey(userMenuDao.createKey("demo_ar"));
				userMenuDao.create(um);
			}

			User demo4 = null;
			try {
				demo4 = userDao.get("demo4_mx", true);
			} catch( Exception e ) {
				demo4 = new User();
				demo4.setFirstname("Demo 4");
				demo4.setLastname("Mexico");
				demo4.setEmail("demo4@allshoppings.mobi");
				demo4.getSecuritySettings().setRole(Role.BRAND);
				demo4.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				demo4.setKey((Key)keyHelper.obtainKey(User.class, "demo4_mx"));
				userDao.create(demo4);
			}

			try {
				um = userMenuDao.get("demo4_mx", true);
				userMenuDao.delete("demo4_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("demo4_mx"));
				userMenuDao.create(um);
			}
			
			User droc = null;
			try {
				droc = userDao.get("droc_mx", true);
			} catch( Exception e ) {
				droc = new User();
				droc.setFirstname("DRoc");
				droc.setLastname("Mexico");
				droc.setEmail("droc@allshoppings.mobi");
				droc.getSecuritySettings().setRole(Role.BRAND);
				droc.getSecuritySettings().setShoppings(new ArrayList<String>());
				droc.getSecuritySettings().getShoppings().add("mundoe");
				droc.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				droc.setKey((Key)keyHelper.obtainKey(User.class, "droc_mx"));
				userDao.create(droc);
			}

			try {
				um = userMenuDao.get("droc_mx", true);
				userMenuDao.delete("droc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.setKey(userMenuDao.createKey("droc_mx"));
				userMenuDao.create(um);
			}

			User walmart = null;
			try {
				walmart = userDao.get("walmart_mx", true);
			} catch( Exception e ) {
				walmart = new User();
				walmart.setFirstname("Walmart");
				walmart.setLastname("Mexico");
				walmart.setEmail("walmart@allshoppings.mobi");
				walmart.getSecuritySettings().setRole(Role.BRAND);
				walmart.getSecuritySettings().setShoppings(new ArrayList<String>());
				walmart.getSecuritySettings().getShoppings().add("wallmartdemo");
				walmart.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				walmart.setKey((Key)keyHelper.obtainKey(User.class, "walmart_mx"));
				userDao.create(walmart);
			}

			try {
				um = userMenuDao.get("walmart_mx", true);
				userMenuDao.delete("walmart_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en Tienda"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("walmart_mx"));
				userMenuDao.create(um);
			}

			User invicta = null;
			try {
				invicta = userDao.get("invicta_mx", true);
			} catch( Exception e ) {
				invicta = new User();
				invicta.setFirstname("Invicta");
				invicta.setLastname("Mexico");
				invicta.setEmail("invicta@allshoppings.mobi");
				invicta.getSecuritySettings().setRole(Role.BRAND);
				invicta.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				invicta.setKey((Key)keyHelper.obtainKey(User.class, "invicta_mx"));
				userDao.create(invicta);
			}

			try {
				um = userMenuDao.get("invicta_mx", true);
				userMenuDao.delete("invicta_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("invicta_mx"));
				userMenuDao.create(um);
			}

			User chilimbalam = null;
			try {
				chilimbalam = userDao.get("chilimbalam_mx", true);
				userDao.delete(chilimbalam);
				throw new Exception();
			} catch( Exception e ) {
				chilimbalam = new User();
				chilimbalam.setFirstname("Chilim Balam");
				chilimbalam.setLastname("Mexico");
				chilimbalam.setEmail("chilimbalam@allshoppings.mobi");
				chilimbalam.getSecuritySettings().setRole(Role.BRAND);
				chilimbalam.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				chilimbalam.setKey((Key)keyHelper.obtainKey(User.class, "chilimbalam_mx"));
				userDao.create(chilimbalam);
			}

			try {
				um = userMenuDao.get("chilimbalam_mx", true);
				userMenuDao.delete("chilimbalam_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("chilimbalam_mx"));
				userMenuDao.create(um);
			}

			User maskota = null;
			try {
				maskota = userDao.get("maskota_mx", true);
				userDao.delete(maskota);
				throw new Exception();
			} catch( Exception e ) {
				maskota = new User();
				maskota.setFirstname("+Kota");
				maskota.setLastname("Mexico");
				maskota.setEmail("maskota@allshoppings.mobi");
				maskota.getSecuritySettings().setRole(Role.BRAND);
				maskota.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				maskota.setKey((Key)keyHelper.obtainKey(User.class, "maskota_mx"));
				userDao.create(maskota);
			}

			try {
				um = userMenuDao.get("maskota_mx", true);
				userMenuDao.delete("maskota_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("maskota_mx"));
				userMenuDao.create(um);
			}

			User blulagoon = null;
			try {
				blulagoon = userDao.get("blulagoon_mx", true);
				userDao.delete(blulagoon);
				throw new Exception();
			} catch( Exception e ) {
//				blulagoon = new User();
//				blulagoon.setFirstname("Blu Lagoon");
//				blulagoon.setLastname("Mexico");
//				blulagoon.setEmail("blulagoon@allshoppings.mobi");
//				blulagoon.getSecuritySettings().setRole(Role.BRAND);
//				blulagoon.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
//				blulagoon.setKey((Key)keyHelper.obtainKey(User.class, "blulagoon_mx"));
//				userDao.create(blulagoon);
			}

			try {
				um = userMenuDao.get("blulagoon_mx", true);
				userMenuDao.delete("blulagoon_mx");
				throw new Exception();
			} catch( Exception e ) {
//				um = new UserMenu();
//				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
//				um.setKey(userMenuDao.createKey("blulagoon_mx"));
//				userMenuDao.create(um);
			}
			
			User sallybeauty = null;
			try {
				sallybeauty = userDao.get("sallybeauty_mx", true);
				userDao.delete(sallybeauty);
				throw new Exception();
			} catch( Exception e ) {
				sallybeauty = new User();
				sallybeauty.setFirstname("Sally Beauty");
				sallybeauty.setLastname("Mexico");
				sallybeauty.setEmail("sallybeauty@allshoppings.mobi");
				sallybeauty.getSecuritySettings().setRole(Role.BRAND);
				sallybeauty.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				sallybeauty.setKey((Key)keyHelper.obtainKey(User.class, "sallybeauty_mx"));
				userDao.create(sallybeauty);
			}

			try {
				um = userMenuDao.get("sallybeauty_mx", true);
				userMenuDao.delete("sallybeauty_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("sallybeauty_mx"));
				userMenuDao.create(um);
			}
			User roku = null;
			try {
				roku = userDao.get("roku_mx", true);
				userDao.delete(roku);
				throw new Exception();
			} catch( Exception e ) {
				roku = new User();
				roku.setFirstname("Roku");
				roku.setLastname("Mexico");
				roku.setEmail("roku@allshoppings.mobi");
				roku.getSecuritySettings().setRole(Role.BRAND);
				roku.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				roku.setKey((Key)keyHelper.obtainKey(User.class, "roku_mx"));
				userDao.create(roku);
			}

			try {
				um = userMenuDao.get("roku_mx", true);
				userMenuDao.delete("roku_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.setKey(userMenuDao.createKey("roku_mx"));
				userMenuDao.create(um);
			}
			User grupopavel = null;
			try {
				grupopavel = userDao.get("grupopavel_mx", true);
				userDao.delete(grupopavel);
				throw new Exception();
			} catch( Exception e ) {
				grupopavel = new User();
				grupopavel.setFirstname("Grupo Pavel");
				grupopavel.setLastname("Mexico");
				grupopavel.setEmail("grupopavel@allshoppings.mobi");
				grupopavel.getSecuritySettings().setRole(Role.BRAND);
				grupopavel.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				grupopavel.setKey((Key)keyHelper.obtainKey(User.class, "grupopavel_mx"));
				userDao.create(grupopavel);
			}

			try {
				um = userMenuDao.get("grupopavel_mx", true);
				userMenuDao.delete("grupopavel_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("grupopavel_mx"));
				userMenuDao.create(um);
			}
			User latabernadelleon = null;
			try {
				latabernadelleon = userDao.get("latabernadelleon_mx", true);
				userDao.delete(latabernadelleon);
				throw new Exception();
			} catch( Exception e ) {
				latabernadelleon = new User();
				latabernadelleon.setFirstname("La Taberna del León");
				latabernadelleon.setLastname("Mexico");
				latabernadelleon.setEmail("latabernadelleon@allshoppings.mobi");
				latabernadelleon.getSecuritySettings().setRole(Role.BRAND);
				latabernadelleon.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				latabernadelleon.setKey((Key)keyHelper.obtainKey(User.class, "latabernadelleon_mx"));
				userDao.create(latabernadelleon);
			}

			try {
				um = userMenuDao.get("latabernadelleon_mx", true);
				userMenuDao.delete("latabernadelleon_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("latabernadelleon_mx"));
				userMenuDao.create(um);
			}
			
			User marketintelligence = null;
			try {
				marketintelligence = userDao.get("marketintelligence_mx", true);
				userDao.delete(marketintelligence);
				throw new Exception();
			} catch( Exception e ) {
				marketintelligence = new User();
				marketintelligence.setFirstname("Market Intelligence");
				marketintelligence.setLastname("Mexico");
				marketintelligence.setAvatarId("d1ba036f-5f6c-4c85-a17a-3b03323d0f8c.png");
				marketintelligence.setEmail("marketintelligence@allshoppings.mobi");
				marketintelligence.getSecuritySettings().setRole(Role.BRAND);
				marketintelligence.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				marketintelligence.setKey((Key)keyHelper.obtainKey(User.class, "marketintelligence_mx"));
				userDao.create(marketintelligence);
			}

			try {
				um = userMenuDao.get("marketintelligence_mx", true);
				userMenuDao.delete("marketintelligence_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("marketintelligence_mx"));
				userMenuDao.create(um);
			}
			User alansolorio = null;
			try {
				alansolorio = userDao.get("alansolorio_mx", true);
				userDao.delete(alansolorio);
				throw new Exception();
			} catch( Exception e ) {
				alansolorio = new User();
				alansolorio.setFirstname("Alan Solorio");
				alansolorio.setLastname("Mexico");
				alansolorio.setEmail("alansolorio@allshoppings.mobi");
				alansolorio.getSecuritySettings().setRole(Role.BRAND);
				alansolorio.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				alansolorio.setKey((Key)keyHelper.obtainKey(User.class, "alansolorio_mx"));
				userDao.create(alansolorio);
			}

			try {
				um = userMenuDao.get("alansolorio_mx", true);
				userMenuDao.delete("alansolorio_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("alansolorio_mx"));
				userMenuDao.create(um);
			}
			User saboreateycafe = null;
			try {
				saboreateycafe = userDao.get("saboreateycafe_mx", true);
				userDao.delete(saboreateycafe);
				throw new Exception();
			} catch( Exception e ) {
				saboreateycafe = new User();
				saboreateycafe.setFirstname("Saboreaté y Café");
				saboreateycafe.setLastname("Mexico");
				saboreateycafe.setEmail("saboreateycafe@allshoppings.mobi");
				saboreateycafe.getSecuritySettings().setRole(Role.BRAND);
				saboreateycafe.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				saboreateycafe.setKey((Key)keyHelper.obtainKey(User.class, "saboreateycafe_mx"));
				userDao.create(saboreateycafe);
			}

			try {
				um = userMenuDao.get("saboreateycafe_mx", true);
				userMenuDao.delete("saboreateycafe_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("saboreateycafe_mx"));
				userMenuDao.create(um);
			}
			
			User getin = null;
			try {
				getin = userDao.get("getin_mx", true);
				userDao.delete(getin);
				throw new Exception();
			} catch( Exception e ) {
				getin = new User();
				getin.setFirstname("getin");
				getin.setLastname("Mexico");
				getin.setEmail("getin@allshoppings.mobi");
				getin.getSecuritySettings().setRole(Role.BRAND);
				getin.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				getin.setKey((Key)keyHelper.obtainKey(User.class, "getin_mx"));
				userDao.create(getin);
			}

			try {
				um = userMenuDao.get("getin_mx", true);
				userMenuDao.delete("getin_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("getin_mx"));
				userMenuDao.create(um);
			}
			
			User annik = null;
			try {
				annik = userDao.get("annik_mx", true);
				userDao.delete(annik);
				throw new Exception();
			} catch( Exception e ) {
				annik = new User();
				annik.setFirstname("Annik");
				annik.setLastname("Mexico");
				annik.setEmail("annik@allshoppings.mobi");
				annik.getSecuritySettings().setRole(Role.BRAND);
				annik.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				annik.setKey((Key)keyHelper.obtainKey(User.class, "annik_mx"));
				userDao.create(annik);
			}

			try {
				um = userMenuDao.get("annik_mx", true);
				userMenuDao.delete("annik_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("annik_mx"));
				userMenuDao.create(um);
			}

			User aditivo = null;
			try {
				aditivo = userDao.get("aditivo_mx", true);
				userDao.delete(aditivo);
				throw new Exception();
			} catch( Exception e ) {
				aditivo = new User();
				aditivo.setFirstname("Aditivo");
				aditivo.setLastname("Mexico");
				aditivo.setEmail("aditivo@allshoppings.mobi");
				aditivo.getSecuritySettings().setRole(Role.BRAND);
				aditivo.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				aditivo.setKey((Key)keyHelper.obtainKey(User.class, "aditivo_mx"));
				userDao.create(aditivo);
			}

			try {
				um = userMenuDao.get("aditivo_mx", true);
				userMenuDao.delete("aditivo_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("aditivo_mx"));
				userMenuDao.create(um);
			}

			try {
				aditivo = userDao.get("sistemas@aditivo_mx", true);
				userDao.delete(aditivo);
				throw new Exception();
			} catch( Exception e ) {
				aditivo = new User();
				aditivo.setFirstname("Aditivo");
				aditivo.setLastname("Mexico");
				aditivo.setEmail("sistemas@aditivo_mx");
				aditivo.getSecuritySettings().setRole(Role.STORE);
				aditivo.getSecuritySettings().setStores(Arrays.asList("61374a58-a679-4532-811a-aa3340bcc47e",
								"ce91457a-f7dc-49d0-93ff-79259e553769", "78416e3d-2274-4a24-9186-3616588f6197",
								"aff0af5d-45b8-46b6-81ee-12c79990653b", "98abde27-4dcc-4d5b-ac16-d43eac63b94b",
								"9a14f70c-52eb-4756-8fb3-b48ee8b86094", "77bfffb3-48a9-43b6-b1ec-51d526e96da8",
								"349b85b9-a083-4e65-9740-f3d59278f635"));
				aditivo.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				aditivo.setKey((Key)keyHelper.obtainKey(User.class, "sistemas@aditivo_mx"));
				userDao.create(aditivo);
			}

			try {
				um = userMenuDao.get("sistemas@aditivo_mx", true);
				userMenuDao.delete("sistemas@aditivo_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("sistemas@aditivo_mx"));
				userMenuDao.create(um);
			}


			User d98coastav = null;
			try {
				d98coastav = userDao.get("98coastav_mx", true);
				userDao.delete(d98coastav);
				throw new Exception();
			} catch( Exception e ) {
				d98coastav = new User();
				d98coastav.setFirstname("98 Coast Av.");
				d98coastav.setLastname("Mexico");
				d98coastav.setEmail("98coastav@allshoppings.mobi");
				d98coastav.getSecuritySettings().setRole(Role.BRAND);
				d98coastav.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				d98coastav.setKey((Key)keyHelper.obtainKey(User.class, "98coastav_mx"));
				userDao.create(d98coastav);
			}

			try {
				um = userMenuDao.get("98coastav_mx", true);
				userMenuDao.delete("98coastav_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("98coastav_mx"));
				userMenuDao.create(um);
			}

			User outletdeportes = null;
			try {
				outletdeportes = userDao.get("outletdeportes_mx", true);
				userDao.delete(outletdeportes);
				throw new Exception();
			} catch( Exception e ) {
				outletdeportes = new User();
				outletdeportes.setFirstname("Outlet Deportes");
				outletdeportes.setLastname("Mexico");
				outletdeportes.setEmail("outletdeportes@allshoppings.mobi");
				outletdeportes.getSecuritySettings().setRole(Role.BRAND);
				outletdeportes.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				outletdeportes.setKey((Key)keyHelper.obtainKey(User.class, "outletdeportes_mx"));
				userDao.create(outletdeportes);
			}

			try {
				um = userMenuDao.get("outletdeportes_mx", true);
				userMenuDao.delete("outletdeportes_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("outletdeportes_mx"));
				userMenuDao.create(um);
			}

			try {
				User colombia = null;
				colombia = userDao.get("colombia_mx", true);
				userDao.delete(colombia);
			} catch( Exception e ) {
			}

			try {
				um = userMenuDao.get("colombia_mx", true);
				userMenuDao.delete("colombia_mx");
			} catch( Exception e ) {
			}

			User colombia = null;
			try {
				colombia = userDao.get("colombia_co", true);
				userDao.delete(colombia);
			} catch( Exception e ) {
			}

			try {
				um = userMenuDao.get("colombia_co", true);
				userMenuDao.delete("colombia_co");
			} catch( Exception e ) {
			}

			User flormar = null;
			try {
				flormar = userDao.get("flormar_pa", true);
			} catch( Exception e ) {
				flormar = new User();
				flormar.setFirstname("Flormar");
				flormar.setLastname("Panama");
				flormar.setEmail("flormar@allshoppings.mobi");
				flormar.getSecuritySettings().setRole(Role.BRAND);
				flormar.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				flormar.setKey((Key)keyHelper.obtainKey(User.class, "flormar_pa"));
				userDao.create(flormar);
			}

			try {
				um = userMenuDao.get("flormar_pa", true);
				userMenuDao.delete("flormar_pa");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("flormar_pa"));
				userMenuDao.create(um);
			}

			User us1 = null;
			try {
				us1 = userDao.get("sunglasshut_pa", true);
			} catch( Exception e ) {
				us1 = new User();
				us1.setFirstname("Sunglass Hut");
				us1.setLastname("Panama");
				us1.setEmail("sungallshutpa@allshoppings.mobi");
				us1.getSecuritySettings().setRole(Role.BRAND);
				us1.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				us1.setKey((Key)keyHelper.obtainKey(User.class, "sunglasshut_pa"));
				userDao.create(us1);
			}

			try {
				um = userMenuDao.get("sunglasshut_pa", true);
				userMenuDao.delete("sunglasshut_pa");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("sunglasshut_pa"));
				userMenuDao.create(um);
			}

			try {
				us1 = userDao.get("lamartina_pa", true);
			} catch( Exception e ) {
				us1 = new User();
				us1.setFirstname("La Martina");
				us1.setLastname("Panama");
				us1.setEmail("lamartinapa@allshoppings.mobi");
				us1.getSecuritySettings().setRole(Role.BRAND);
				us1.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				us1.setKey((Key)keyHelper.obtainKey(User.class, "lamartina_pa"));
				userDao.create(us1);
			}

			try {
				um = userMenuDao.get("lamartina_pa", true);
				userMenuDao.delete("lamartina_pa");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("lamartina_pa"));
				userMenuDao.create(um);
			}

			try {
				us1 = userDao.get("grandstore_pa", true);
			} catch( Exception e ) {
				us1 = new User();
				us1.setFirstname("Grand Store");
				us1.setLastname("Panama");
				us1.setEmail("grandstore_pa@allshoppings.mobi");
				us1.getSecuritySettings().setRole(Role.BRAND);
				us1.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				us1.setKey((Key)keyHelper.obtainKey(User.class, "grandstore_pa"));
				userDao.create(us1);
			}

			try {
				um = userMenuDao.get("grandstore_pa", true);
				userMenuDao.delete("grandstore_pa");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("grandstore_pa"));
				userMenuDao.create(um);
			}

			try {
				us1 = userDao.get("flormar_cr", true);
			} catch( Exception e ) {
				us1 = new User();
				us1.setFirstname("Flormar");
				us1.setLastname("Costa Rica");
				us1.setEmail("flormar_cr@allshoppings.mobi");
				us1.getSecuritySettings().setRole(Role.BRAND);
				us1.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				us1.setKey((Key)keyHelper.obtainKey(User.class, "flormar_cr"));
				userDao.create(us1);
			}

			try {
				um = userMenuDao.get("flormar_cr", true);
				userMenuDao.delete("flormar_cr");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("flormar_cr"));
				userMenuDao.create(um);
			}

			try {
				us1 = userDao.get("nezrin.saker@demodazl.com", true);
				userDao.delete(us1);
				throw new Exception();
			} catch( Exception e ) {
				us1 = new User();
				us1.setFirstname("Nezrin");
				us1.setLastname("Saker");
				us1.setEmail("nezrin.saker@demodazl.com");
				us1.getSecuritySettings().setRole(Role.STORE);
				us1.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				us1.getSecuritySettings()
				.setStores(Arrays.asList("a07e8329-9e82-421e-98e6-72b7fb6f04f3",
						"22855128-ff48-4953-95d4-2b049f529d03", "39931956-66d1-46c3-8695-3c5b119258b2",
						"18ec3350-8c25-4f12-9498-a4350915b5c5", "938637cb-4bb9-4452-8439-077d8ceb5c21",
						"707c8c11-4aff-4ca3-bc0d-60bf2f8ef282", "ab42aa2b-c1f6-438f-875b-32794eecdf02",
						"3612e18d-7ae2-4d59-9b0a-181149559941", "b36443b2-932c-4344-ada4-0fe254ce626b",
						"8a4d093b-4ef5-42af-8e71-d4c9746ab751", "32350a8b-eea0-4881-965a-d142d27df4a1",
						"50506876-8e69-4b91-9084-c3dd63c95b68", "3731dfc8-3308-4c0f-b462-5100a29e25cc",
						"6c6a6156-38c3-4167-be09-42d9890510ed", "5d9789e3-f7e5-41e4-8fb3-9d028c485772"));
				us1.setKey((Key)keyHelper.obtainKey(User.class, "nezrin.saker@demodazl.com"));
				userDao.create(us1);
			}

			try {
				um = userMenuDao.get("nezrin.saker@demodazl.com", true);
				userMenuDao.delete("nezrin.saker@demodazl.com");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("nezrin.saker@demodazl.com"));
				userMenuDao.create(um);
			}

			try {
				us1 = userDao.get("flormar_co", true);
			} catch( Exception e ) {
				us1 = new User();
				us1.setFirstname("Flormar");
				us1.setLastname("Colombia");
				us1.setEmail("flormar_co@allshoppings.mobi");
				us1.getSecuritySettings().setRole(Role.BRAND);
				us1.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				us1.setKey((Key)keyHelper.obtainKey(User.class, "flormar_co"));
				userDao.create(us1);
			}

			try {
				um = userMenuDao.get("flormar_co", true);
				userMenuDao.delete("flormar_co");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("flormar_co"));
				userMenuDao.create(um);
			}

			User adolfodominguez = null;
			try {
				adolfodominguez = userDao.get("adolfodominguez_mx", true);
			} catch( Exception e ) {
				adolfodominguez = new User();
				adolfodominguez.setFirstname("Adolfo Dominguez");
				adolfodominguez.setLastname("Mexico");
				adolfodominguez.setEmail("adolfodominguez@allshoppings.mobi");
				adolfodominguez.getSecuritySettings().setRole(Role.BRAND);
				adolfodominguez.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				adolfodominguez.setKey((Key)keyHelper.obtainKey(User.class, "adolfodominguez_mx"));
				userDao.create(adolfodominguez);
			}

			try {
				um = userMenuDao.get("adolfodominguez_mx", true);
				userMenuDao.delete("adolfodominguez_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("adolfodominguez_mx"));
				userMenuDao.create(um);
			}

			User watchmywatch = null;
			try {
				watchmywatch = userDao.get("watchmywatch_mx", true);
			} catch( Exception e ) {
				watchmywatch = new User();
				watchmywatch.setFirstname("Watch My Watch");
				watchmywatch.setLastname("Mexico");
				watchmywatch.setEmail("watchmywatch@allshoppings.mobi");
				watchmywatch.getSecuritySettings().setRole(Role.BRAND);
				watchmywatch.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				watchmywatch.setKey((Key)keyHelper.obtainKey(User.class, "watchmywatch_mx"));
				userDao.create(watchmywatch);
			}

			try {
				um = userMenuDao.get("watchmywatch_mx", true);
				userMenuDao.delete("watchmywatch_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("watchmywatch_mx"));
				userMenuDao.create(um);
			}

			User fincasantaveracruz = null;
			try {
				fincasantaveracruz = userDao.get("fincasantaveracruz_mx", true);
			} catch( Exception e ) {
				fincasantaveracruz = new User();
				fincasantaveracruz.setFirstname("Finca Santa VeraCruz");
				fincasantaveracruz.setLastname("Mexico");
				fincasantaveracruz.setEmail("fincasantaveracruz@allshoppings.mobi");
				fincasantaveracruz.getSecuritySettings().setRole(Role.BRAND);
				fincasantaveracruz.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				fincasantaveracruz.setKey((Key)keyHelper.obtainKey(User.class, "fincasantaveracruz_mx"));
				userDao.create(fincasantaveracruz);
			}

			try {
				um = userMenuDao.get("fincasantaveracruz_mx", true);
				userMenuDao.delete("fincasantaveracruz_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("fincasantaveracruz_mx"));
				userMenuDao.create(um);
			}

			User fullsand = null;
			try {
				fullsand = userDao.get("fullsand_mx", true);
				userDao.delete("fullsand_mx");
				throw new Exception();
			} catch( Exception e ) {
				fullsand = new User();
				fullsand.setFirstname("Fullsand");
				fullsand.setLastname("Mexico");
				fullsand.setEmail("fullsand@allshoppings.mobi");
				fullsand.getSecuritySettings().setRole(Role.BRAND);
				fullsand.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				fullsand.setKey((Key)keyHelper.obtainKey(User.class, "fullsand_mx"));
				userDao.create(fullsand);
			}

			try {
				um = userMenuDao.get("fullsand_mx", true);
				userMenuDao.delete("fullsand_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("fullsand_mx"));
				userMenuDao.create(um);
			}

			try {
				um = userMenuDao.get("tanyamoss_mx", true);
				userMenuDao.delete("tanyamoss_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("tanyamoss_mx"));
				userMenuDao.create(um);
			}

			User tanyamoss = null;
			try {
				tanyamoss = userDao.get("tanyamoss_mx", true);
				userDao.delete("tanyamoss_mx");
				throw new Exception();
			} catch( Exception e ) {
				tanyamoss = new User();
				tanyamoss.setFirstname("Tanya Moss");
				tanyamoss.setLastname("Mexico");
				tanyamoss.setEmail("tanyamoss@allshoppings.mobi");
				tanyamoss.getSecuritySettings().setRole(Role.BRAND);
				tanyamoss.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				tanyamoss.setKey((Key)keyHelper.obtainKey(User.class, "tanyamoss_mx"));
				userDao.create(tanyamoss);
			}

			try {
				um = userMenuDao.get("pakmail_mx", true);
				userMenuDao.delete("pakmail_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("pakmail_mx"));
				userMenuDao.create(um);
			}

			User pakmail = null;
			try {
				pakmail = userDao.get("pakmail_mx", true);
				userDao.delete("pakmail_mx");
				throw new Exception();
			} catch( Exception e ) {
				pakmail = new User();
				pakmail.setFirstname("Pakmail");
				pakmail.setLastname("Mexico");
				pakmail.setEmail("pakmail@allshoppings.mobi");
				pakmail.getSecuritySettings().setRole(Role.BRAND);
				pakmail.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				pakmail.setKey((Key)keyHelper.obtainKey(User.class, "pakmail_mx"));
				userDao.create(pakmail);
			}

			User pameladeharo = null;
			try {
				pameladeharo = userDao.get("pameladeharo_mx", true);
				userDao.delete("pameladeharo_mx");
				throw new Exception();
			} catch( Exception e ) {
				pameladeharo = new User();
				pameladeharo.setFirstname("Pamela de Haro");
				pameladeharo.setLastname("Mexico");
				pameladeharo.setEmail("pameladeharo@allshoppings.mobi");
				pameladeharo.getSecuritySettings().setRole(Role.BRAND);
				pameladeharo.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				pameladeharo.setKey((Key)keyHelper.obtainKey(User.class, "pameladeharo_mx"));
				userDao.create(pameladeharo);
			}

			try {
				um = userMenuDao.get("pameladeharo_mx", true);
				userMenuDao.delete("pameladeharo_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("pameladeharo_mx"));
				userMenuDao.create(um);
			}

			User tonymoly = null;
			try {
				tonymoly = userDao.get("tonymoly_mx", true);
				userDao.delete("tonymoly_mx");
				throw new Exception();
			} catch( Exception e ) {
				tonymoly = new User();
				tonymoly.setFirstname("Tony Moly");
				tonymoly.setLastname("Mexico");
				tonymoly.setEmail("tonymoly@allshoppings.mobi");
				tonymoly.getSecuritySettings().setRole(Role.BRAND);
				tonymoly.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				tonymoly.setKey((Key)keyHelper.obtainKey(User.class, "tonymoly_mx"));
				userDao.create(tonymoly);
			}

			try {
				um = userMenuDao.get("tonymoly_mx", true);
				userMenuDao.delete("tonymoly_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("tonymoly_mx"));
				userMenuDao.create(um);
			}

			User farmaciasyza = null;
			try {
				farmaciasyza = userDao.get("farmaciasyza_mx", true);
				userDao.delete("farmaciasyza_mx");
				throw new Exception();
			} catch( Exception e ) {
				farmaciasyza = new User();
				farmaciasyza.setFirstname("Farmacias YZA");
				farmaciasyza.setLastname("Mexico");
				farmaciasyza.setEmail("farmaciasyza@allshoppings.mobi");
				farmaciasyza.getSecuritySettings().setRole(Role.BRAND);
				farmaciasyza.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				farmaciasyza.setKey((Key)keyHelper.obtainKey(User.class, "farmaciasyza_mx"));
				userDao.create(farmaciasyza);
			}

			try {
				um = userMenuDao.get("farmaciasyza_mx", true);
				userMenuDao.delete("farmaciasyza_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("farmaciasyza_mx"));
				userMenuDao.create(um);
			}

			User vickyform = null;
			try {
				vickyform = userDao.get("vickyform_mx", true);
			} catch( Exception e ) {
				vickyform = new User();
				vickyform.setFirstname("Vicky Form");
				vickyform.setLastname("Mexico");
				vickyform.setEmail("vickyform@allshoppings.mobi");
				vickyform.getSecuritySettings().setRole(Role.BRAND);
				vickyform.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				vickyform.setKey((Key)keyHelper.obtainKey(User.class, "vickyform_mx"));
				userDao.create(vickyform);
			}

			try {
				um = userMenuDao.get("vickyform_mx", true);
				userMenuDao.delete("vickyform_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.setKey(userMenuDao.createKey("vickyform_mx"));
				userMenuDao.create(um);
			}

			User agasys = null;
			try {
				agasys = userDao.get("agasys_mx", true);
			} catch( Exception e ) {
				agasys = new User();
				agasys.setFirstname("Agasys");
				agasys.setLastname("Mexico");
				agasys.setEmail("agasys@allshoppings.mobi");
				agasys.getSecuritySettings().setRole(Role.BRAND);
				agasys.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				agasys.setKey((Key)keyHelper.obtainKey(User.class, "agasys_mx"));
				userDao.create(agasys);
			}

			try {
				um = userMenuDao.get("agasys_mx", true);
				userMenuDao.delete("agasys_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
//				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.apddetails", "fa-file-excel-o", "Detalles"));
				um.setKey(userMenuDao.createKey("agasys_mx"));
				userMenuDao.create(um);
			}

			User descifra = null;
			try {
				descifra = userDao.get("descifra_mx", true);
			} catch( Exception e ) {
				descifra = new User();
				descifra.setFirstname("Descifra");
				descifra.setLastname("Mexico");
				descifra.setEmail("descifra@allshoppings.mobi");
				descifra.getSecuritySettings().setRole(Role.APPLICATION);
				descifra.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				descifra.setKey((Key)keyHelper.obtainKey(User.class, "descifra_mx"));
				userDao.create(descifra);
			}

			try {
				um = userMenuDao.get("descifra_mx", true);
				userMenuDao.delete("descifra_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.setKey(userMenuDao.createKey("descifra_mx"));
				userMenuDao.create(um);
			}

			
			
			User beepquest = null;
			try {
				beepquest = userDao.get("beepquest_mx", true);
			} catch( Exception e ) {
				beepquest = new User();
				beepquest.setFirstname("Beep Quest");
				beepquest.setLastname("Mexico");
				beepquest.setEmail("beepquest@allshoppings.mobi");
				beepquest.getSecuritySettings().setRole(Role.APPLICATION);
				beepquest.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				beepquest.setKey((Key)keyHelper.obtainKey(User.class, "beepquest_mx"));
				userDao.create(beepquest);
			}

			User skyalert = null;
			try {
				skyalert = userDao.get("skyalert_mx", true);
			} catch( Exception e ) {
				skyalert = new User();
				skyalert.setFirstname("Sky Alert");
				skyalert.setLastname("Mexico");
				skyalert.setEmail("skyalert@allshoppings.mobi");
				skyalert.getSecuritySettings().setRole(Role.APPLICATION);
				skyalert.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				skyalert.setKey((Key)keyHelper.obtainKey(User.class, "skyalert_mx"));
				userDao.create(skyalert);
			}

			User calibrator = null;
			try {
				calibrator = userDao.get("getin-apdevice-calibrator", true);
			} catch( Exception e ) {
				calibrator = new User();
				calibrator.setFirstname("APDevice Calibrator");
				calibrator.setLastname("GetIn");
				calibrator.setEmail("calibrator@allshoppings.mobi");
				calibrator.getSecuritySettings().setRole(Role.APPLICATION);
				calibrator.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				calibrator.setKey((Key)keyHelper.obtainKey(User.class, "getin-apdevice-calibrator"));
				userDao.create(calibrator);
			}

			// Chomarc --------------------------------------------------------------------
			User user = null;
			
			try {
				um = userMenuDao.get("anallerena@chomarc_mx", true);
				userMenuDao.delete("anallerena@chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("anallerena@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("anallerena@chomarc_mx", true);
			} catch( Exception e ) {
				user = new User();
				user.setFirstname("Ana");
				user.setLastname("Llerena");
				user.setEmail("anallerena@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4df8d43b-b8d6-41a2-a342-082884a5e897",
								"8b0d5f06-f5f6-4d70-aab5-b3ef1e807d97", "b9e3dee9-fcb8-4c36-9620-2c897b03566f",
								"b327518a-28a8-4ca0-b82a-bd1e646307ce", "6e39b7d5-dcd5-458b-9088-a2c97be409e3",
								"743003df-cb85-4f8f-98eb-d41ff31f3e36", "e0bb9d40-7639-47d0-ab38-135b280ac769"));
				user.setKey((Key)keyHelper.obtainKey(User.class, "anallerena@chomarc_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("beatrizcors@chomarc_mx", true);
				userMenuDao.delete("beatrizcors@chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("beatrizcors@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("beatrizcors@chomarc_mx", true);
				userDao.delete(user);
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Beatriz");
				user.setLastname("Cors");
				user.setEmail("beatrizcors@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("93ec0988-c44a-402e-bbe4-83d1e377a559",
								"d22be9e5-74a7-4671-aa7e-1a464bb748b7", "2ed6fea4-efb3-4aef-bc5c-af1e3d712b4b",
								"cc13c199-5969-4010-aedb-bf01a4428786", "ba26aea6-dda1-4bfe-a270-23350be7105e",
								"95d98d90-ba0b-42e1-843b-4a0e5c09db4b", "62c734bd-15fa-4bc5-a542-d38dd30e4546",
								"94b9e9fc-3f73-4926-9890-fe0d924952fc"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "beatrizcors@chomarc_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("evyreiter@chomarc_mx", true);
				userMenuDao.delete("evyreiter@chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("evyreiter@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("evyreiter@chomarc_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Evy");
				user.setLastname("Reiter");
				user.setEmail("evyreiter@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("b5e35d13-3abc-4629-993a-c742dbd81f0e",
								"5dce55ee-8506-4b70-b7a7-aee2a7e6cbb4"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "evyreiter@chomarc_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("rogersilva@chomarc_mx", true);
				userMenuDao.delete("rogersilva@chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("rogersilva@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("rogersilva@chomarc_mx", true);
				userDao.delete(user);
				throw(new Exception());
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Roger");
				user.setLastname("Silva");
				user.setEmail("rogersilva@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1480464171515","28b335d2-9bf2-48c3-a335-d2ae4314247a","4beba5a8-3987-489e-9ef7-8be65d3c1b27","81b52571-3bd3-44e3-bed9-9e592d568f04","06a99c76-c4ca-4952-a99f-3576c7e4dce0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "rogersilva@chomarc_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("ginatena@chomarc_mx", true);
				userMenuDao.delete("ginatena@chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ginatena@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ginatena@chomarc_mx", true);
				userDao.delete(user);
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Gina");
				user.setLastname("Tena");
				user.setEmail("ginatena@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("93ec0988-c44a-402e-bbe4-83d1e377a559",
								"d22be9e5-74a7-4671-aa7e-1a464bb748b7", "2ed6fea4-efb3-4aef-bc5c-af1e3d712b4b",
								"cc13c199-5969-4010-aedb-bf01a4428786", "ba26aea6-dda1-4bfe-a270-23350be7105e",
								"95d98d90-ba0b-42e1-843b-4a0e5c09db4b", "62c734bd-15fa-4bc5-a542-d38dd30e4546",
								"94b9e9fc-3f73-4926-9890-fe0d924952fc"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ginatena@chomarc_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("leonardooneto@chomarc_mx", true);
				userMenuDao.delete("leonardooneto@chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("leonardooneto@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("leonardooneto@chomarc_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Leonardo");
				user.setLastname("Oneto");
				user.setEmail("leonardooneto@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5dce55ee-8506-4b70-b7a7-aee2a7e6cbb4"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "leonardooneto@chomarc_mx"));
				userDao.create(user);
			}
			// Dakab_mx -------------------------------------------------------------------
			try {
				um = userMenuDao.get("dakab_mx", true);
				userMenuDao.delete("dakab_mx");
				throw new Exception();
			} catch( Exception e ) {
//				um = new UserMenu();
//				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
//				um.setKey(userMenuDao.createKey("dakab_mx"));
//				userMenuDao.create(um);
			}

			try {
				user = userDao.get("dakab_mx", true);
				userDao.delete(user);
				throw new Exception();
			} catch (Exception e) {
//				user = new User();
//				user.setFirstname("Dakab");
//				user.setLastname("");
//				user.setEmail("dakab_mx");
//				user.getSecuritySettings().setRole(Role.STORE);
//				user.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");	user.getSecuritySettings().setStores(Arrays.asList("1474331820269","1484249039313","71143dcf-9a92-49f9-901d-47cd23e4ce12","9e8793b4-750d-464a-b0cb-c4bfcd7f4e4b","d83ddb08-0f8a-4926-a09f-9b4ea1c86bc1"));
//				user.setKey((Key) keyHelper.obtainKey(User.class, "dakab_mx"));
//				userDao.create(user);
			}
			// Sportium --------------------------------------------------------------------
			
			//Lomas Verdes
			try {
				um = userMenuDao.get("lomasverdes@sportium", true);
				userMenuDao.delete("lomasverdes@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("lomasverdes@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("lomasverdes@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Lomas Verdes");
				user.setLastname("");
				user.setEmail("lomasverdes@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f2a79040-fefe-48f2-bd5d-e2db54ef5f23"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "lomasverdes@sportium"));
				userDao.create(user);
			}
			//satelite
			try {
				um = userMenuDao.get("satelite@sportium", true);
				userMenuDao.delete("satelite@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("satelite@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("satelite@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Satelite");
				user.setLastname("");
				user.setEmail("satelite@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1471039822461"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "satelite@sportium"));
				userDao.create(user);
			}
			//Cuautitlan
			try {
				um = userMenuDao.get("cuautitlan@sportium", true);
				userMenuDao.delete("cuautitlan@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("cuautitlan@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("cuautitlan@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Cuautitlan");
				user.setLastname("");
				user.setEmail("cuautitlan@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("d0f83d5d-e451-45ee-ad41-e2b4da086f2f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "cuautitlan@sportium"));
				userDao.create(user);
			}
			//Arboledas
			try {
				um = userMenuDao.get("arboledas@sportium", true);
				userMenuDao.delete("arboledas@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("arboledas@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("arboledas@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Arboledas");
				user.setLastname("");
				user.setEmail("arboledas@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("8cd52856-7e34-4f19-8c45-e25e325d4ff9"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "arboledas@sportium"));
				userDao.create(user);
			}
			//Coyoacan
			try {
				um = userMenuDao.get("coyoacan@sportium", true);
				userMenuDao.delete("coyoacan@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("coyoacan@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("coyoacan@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Coyoacan");
				user.setLastname("");
				user.setEmail("coyoacan@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("67af6e6e-9f11-4948-9887-65679bfd3d69"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "coyoacan@sportium"));
				userDao.create(user);
			}
			//Del valle
			try {
				um = userMenuDao.get("delvalle@sportium", true);
				userMenuDao.delete("delvalle@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("delvalle@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("delvalle@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Del valle");
				user.setLastname("");
				user.setEmail("delvalle@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("970b5795-ad0a-49ac-a7eb-110d826c7b8f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "delvalle@sportium"));
				userDao.create(user);
			}
			//San Angel
			try {
				um = userMenuDao.get("sanangel@sportium", true);
				userMenuDao.delete("sanangel@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("sanangel@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("sanangel@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("San Angel");
				user.setLastname("");
				user.setEmail("sanangel@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("109ec028-6749-4332-9427-a39ccbfe7244"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "sanangel@sportium"));
				userDao.create(user);
			}
			//Desierto
			try {
				um = userMenuDao.get("desierto@sportium", true);
				userMenuDao.delete("desierto@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("desierto@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("desierto@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Desierto");
				user.setLastname("");
				user.setEmail("desierto@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("c726776f-0a96-43d1-ae97-4169e595e5c6"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "desierto@sportium"));
				userDao.create(user);
			}
			//Santa Fe
			try {
				um = userMenuDao.get("santafe@sportium", true);
				userMenuDao.delete("santafe@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("santafe@sportium"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("santafe@sportium", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Santa Fe");
				user.setLastname("");
				user.setEmail("santafe@sportium");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1471039822614"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "santafe@sportium"));
				userDao.create(user);
			}
			// End Sportium --------------------------------------------------------------------
			
			// Custom Sportium --------------------------------------------------------------------

			//Coyoacan
			try {
				um = userMenuDao.get("jcambray@sportium.com.mx", true);
				userMenuDao.delete("jcambray@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("jcambray@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("jcambray@sportium.com.mx", true);
				userDao.delete(user.getIdentifier());;
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Coyoacan");
				user.setLastname("");
				user.setEmail("jcambray@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("67af6e6e-9f11-4948-9887-65679bfd3d69"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "jcambray@sportium.com.mx"));
				userDao.create(user);
			}

			// San Angel
			try {
				um = userMenuDao.get("rdelpozo@sportium.com.mx", true);
				userMenuDao.delete("rdelpozo@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("rdelpozo@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("rdelpozo@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium San Angel");
				user.setLastname("");
				user.setEmail("rdelpozo@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("109ec028-6749-4332-9427-a39ccbfe7244"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "rdelpozo@sportium.com.mx"));
				userDao.create(user);
			}

			// Desierto
			try {
				um = userMenuDao.get("mmonroy@sportium.com.mx", true);
				userMenuDao.delete("mmonroy@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("mmonroy@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("mmonroy@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Desierto");
				user.setLastname("");
				user.setEmail("mmonroy@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("c726776f-0a96-43d1-ae97-4169e595e5c6"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "mmonroy@sportium.com.mx"));
				userDao.create(user);
			}

			// Santa Fe
			try {
				um = userMenuDao.get("asosa@sportium.com.mx", true);
				userMenuDao.delete("asosa@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("asosa@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("asosa@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Santa Fe");
				user.setLastname("");
				user.setEmail("asosa@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1471039822614"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "asosa@sportium.com.mx"));
				userDao.create(user);
			}

			// Arboledas
			try {
				um = userMenuDao.get("gesquivel@sportium.com.mx", true);
				userMenuDao.delete("gesquivel@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("gesquivel@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("gesquivel@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Arboledas");
				user.setLastname("");
				user.setEmail("gesquivel@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("8cd52856-7e34-4f19-8c45-e25e325d4ff9"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "gesquivel@sportium.com.mx"));
				userDao.create(user);
			}

			// Lomas Verdes
			try {
				um = userMenuDao.get("icordova@sportium.com.mx", true);
				userMenuDao.delete("icordova@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("icordova@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("icordova@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Lomas Verdes");
				user.setLastname("");
				user.setEmail("icordova@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f2a79040-fefe-48f2-bd5d-e2db54ef5f23"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "icordova@sportium.com.mx"));
				userDao.create(user);
			}

			// Cuautitlan
			try {
				um = userMenuDao.get("rcontreras@sportium.com.mx", true);
				userMenuDao.delete("rcontreras@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("rcontreras@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("rcontreras@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Cuautitlan");
				user.setLastname("");
				user.setEmail("rcontreras@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("d0f83d5d-e451-45ee-ad41-e2b4da086f2f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "rcontreras@sportium.com.mx"));
				userDao.create(user);
			}

			// Satelite
			try {
				um = userMenuDao.get("lcepeda@sportium.com.mx", true);
				userMenuDao.delete("lcepeda@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("lcepeda@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("lcepeda@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Satelite");
				user.setLastname("");
				user.setEmail("lcepeda@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1471039822461"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "lcepeda@sportium.com.mx"));
				userDao.create(user);
			}

			// Del Valle
			try {
				um = userMenuDao.get("lrivera@sportium.com.mx", true);
				userMenuDao.delete("lrivera@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("lrivera@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("lrivera@sportium.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium Del Valle");
				user.setLastname("");
				user.setEmail("lrivera@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("970b5795-ad0a-49ac-a7eb-110d826c7b8f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "lrivera@sportium.com.mx"));
				userDao.create(user);
			}

			// Corporativo
			try {
				um = userMenuDao.get("gperez@sportium.com.mx", true);
				userMenuDao.delete("gperez@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("gperez@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("gperez@sportium.com.mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium");
				user.setLastname("");
				user.setEmail("gperez@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f2a79040-fefe-48f2-bd5d-e2db54ef5f23",
								"8cd52856-7e34-4f19-8c45-e25e325d4ff9", "1471039822461",
								"d0f83d5d-e451-45ee-ad41-e2b4da086f2f", "67af6e6e-9f11-4948-9887-65679bfd3d69",
								"970b5795-ad0a-49ac-a7eb-110d826c7b8f", "109ec028-6749-4332-9427-a39ccbfe7244",
								"c726776f-0a96-43d1-ae97-4169e595e5c6", "1471039822614"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "gperez@sportium.com.mx"));
				userDao.create(user);
			}

			// Corporativo
			try {
				um = userMenuDao.get("dgomez@sportium.com.mx", true);
				userMenuDao.delete("dgomez@sportium.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				um.setKey(userMenuDao.createKey("dgomez@sportium.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("dgomez@sportium.com.mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Sportium");
				user.setLastname("");
				user.setEmail("dgomez@sportium.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
					.setStores(Arrays.asList("f2a79040-fefe-48f2-bd5d-e2db54ef5f23",
							"8cd52856-7e34-4f19-8c45-e25e325d4ff9", "1471039822461",
							"d0f83d5d-e451-45ee-ad41-e2b4da086f2f", "67af6e6e-9f11-4948-9887-65679bfd3d69",
							"970b5795-ad0a-49ac-a7eb-110d826c7b8f", "109ec028-6749-4332-9427-a39ccbfe7244",
							"c726776f-0a96-43d1-ae97-4169e595e5c6", "1471039822614"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "dgomez@sportium.com.mx"));
				userDao.create(user);
			}

			// End Custom Sportium --------------------------------------------------------------------

			// Custom Outlet Deportes
			// Zona 1
			try {
				um = userMenuDao.get("zona1@outletdeportes_mx", true);
				userMenuDao.delete("zona1@outletdeportes_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("zona1@outletdeportes_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("zona1@outletdeportes_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Outlet Deportes");
				user.setLastname("");
				user.setEmail("zona1@outletdeportes_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f718f26a-c6de-4149-9605-d737450b7bd9",
								"5cad0cb3-196a-4f3f-b4f4-224f610f6467", "c0d2ccb9-0ed4-45f8-8f4c-770ddb4495b4",
								"791d303b-3f45-4037-a6f4-2ba9c1e15c75", "958d7395-fb37-4b8b-a716-b2c1a9ffdb9f",
								"40b7b23d-f2b4-4f70-be05-a0ad51ce44ba", "440dfccd-73b3-48a5-98c8-d893a01a085f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "zona1@outletdeportes_mx"));
				userDao.create(user);
			}

			// Zona 2
			try {
				um = userMenuDao.get("zona2@outletdeportes_mx", true);
				userMenuDao.delete("zona2@outletdeportes_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("zona2@outletdeportes_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("zona2@outletdeportes_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Outlet Deportes");
				user.setLastname("");
				user.setEmail("zona2@outletdeportes_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("3a361ad5-9748-4bfa-9a69-460fd8214e6e",
								"23b16093-fbbf-4d9a-811b-6c82bd0eb940", "0804ce51-a635-40be-8952-28cc25c946dd",
								"247b4ead-822f-4713-9465-177666b2e31c", "e43a9f92-7db7-46d1-8a74-3f629eea2b47",
								"dcb748f6-a060-43da-9a13-aea9ca02245a", "fb111c5b-db32-4129-a09a-f15269f57285"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "zona2@outletdeportes_mx"));
				userDao.create(user);
			}

			// Zona 3
			try {
				um = userMenuDao.get("zona3@outletdeportes_mx", true);
				userMenuDao.delete("zona3@outletdeportes_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("zona3@outletdeportes_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("zona3@outletdeportes_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Outlet Deportes");
				user.setLastname("");
				user.setEmail("zona3@outletdeportes_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("41e68dc9-254d-4803-82b7-c083eeaf28df",
								"9263926c-88e3-435b-ad7e-1920abfb73a6", "649c6de9-05aa-40de-bd39-7b1d37921658",
								"b6b1a93f-0116-4d9a-ad40-9c6842eaa8c0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "zona3@outletdeportes_mx"));
				userDao.create(user);
			}

			// Zona Puebla
			try {
				um = userMenuDao.get("zonapuebla@outletdeportes_mx", true);
				userMenuDao.delete("zonapuebla@outletdeportes_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("zonapuebla@outletdeportes_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("zonapuebla@outletdeportes_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Outlet Deportes");
				user.setLastname("");
				user.setEmail("zonapuebla@outletdeportes_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("01bf63e5-6b31-4bf9-beb6-2a9dcbb8a304",
						"9bbf47a2-5a32-4ae3-b217-858c7c1e2703", "115e5c3c-7850-4e3e-82d1-16b15b5256a5"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "zonapuebla@outletdeportes_mx"));
				userDao.create(user);
			}

			// End Custom Outlet --------------------------------------------------------------------
			
			// Custom Fullsand  --------------------------------------------------------------------
			// rgalaz
			try {
				um = userMenuDao.get("rgalaz@fullsand.com", true);
				userMenuDao.delete("rgalaz@fullsand.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("rgalaz@fullsand.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("rgalaz@fullsand.com", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Fullsand");
				user.setLastname("");
				user.setEmail("rgalaz@fullsand.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("1471039822790",
						"c0919283-ef3b-4a62-b5e6-22a3ce9d5271"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "rgalaz@fullsand.com"));
				userDao.create(user);
			}

			// cgomez@fullsand.com
			try {
				um = userMenuDao.get("cgomez@fullsand.com", true);
				userMenuDao.delete("cgomez@fullsand.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("cgomez@fullsand.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("cgomez@fullsand.com", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Fullsand");
				user.setLastname("");
				user.setEmail("cgomez@fullsand.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("d40631ed-c981-4be9-8585-b74fe552832e",
						"39132498-241e-4efb-a416-06f37f2f5b10"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "cgomez@fullsand.com"));
				userDao.create(user);
			}

			// mabrajan@fullsand.com
			try {
				um = userMenuDao.get("mabrajan@fullsand.com", true);
				userMenuDao.delete("mabrajan@fullsand.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("mabrajan@fullsand.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("mabrajan@fullsand.com", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Fullsand");
				user.setLastname("");
				user.setEmail("mabrajan@fullsand.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("8aeea969-57d3-4bd5-bddb-dd98caf1fc00"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "mabrajan@fullsand.com"));
				userDao.create(user);
			}

			// llopez@fullsand.com
			try {
				um = userMenuDao.get("llopez@fullsand.com", true);
				userMenuDao.delete("llopez@fullsand.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("llopez@fullsand.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("llopez@fullsand.com", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Fullsand");
				user.setLastname("");
				user.setEmail("llopez@fullsand.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("d2cfebcb-2cfd-4b63-90a1-8235b5d51e1d"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "llopez@fullsand.com"));
				userDao.create(user);
			}

			// End Custom Fullsand  --------------------------------------------------------------------

			// Custom 98 Coast av --------------------------------------------------------------------

			try {
				um = userMenuDao.get("oasis@98coastav_mx", true);
				userMenuDao.delete("oasis@98coastav_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("oasis@98coastav_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("oasis@98coastav_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("98 Coast Av. oasis");
				user.setLastname("");
				user.setEmail("oasis@98coastav_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("f03d2386-3c1e-40c7-9005-d402f565f107"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "oasis@98coastav_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("portal@98coastav_mx", true);
				userMenuDao.delete("portal@98coastav_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("portal@98coastav_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("portal@98coastav_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("98 Coast Av. Portal");
				user.setLastname("");
				user.setEmail("portal@98coastav_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("2edff7a5-6000-45b1-9425-a7712e133d80"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "portal@98coastav_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("playa1@98coastav_mx", true);
				userMenuDao.delete("playa1@98coastav_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("playa1@98coastav_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("playa1@98coastav_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("98 Coast Av. Playa 1");
				user.setLastname("");
				user.setEmail("playa1@98coastav_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("510b13bc-c316-42e1-853a-38dcf8855746"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "playa1@98coastav_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("playa2@98coastav_mx", true);
				userMenuDao.delete("playa2@98coastav_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("playa2@98coastav_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("playa2@98coastav_mx", true);
				userDao.delete(user.getIdentifier());
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("98 Coast Av. Playa 2");
				user.setLastname("");
				user.setEmail("playa2@98coastav_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("83d06e8f-4ca8-4a92-bf48-b796bc24ac50"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "playa2@98coastav_mx"));
				userDao.create(user);
			}

			// End 98 Coast av  --------------------------------------------------------------------

		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
