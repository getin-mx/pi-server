package mobi.allshoppings.cli;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Key;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.dao.UserMenuDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserMenu;
import mobi.allshoppings.model.UserMenuEntry;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.tools.KeyHelper;

public class UserMenuDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UserMenuDump.class.getName());

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
			UserMenuDAO userMenuDao = (UserMenuDAO)getApplicationContext().getBean("usermenu.dao.ref");
			UserDAO userDao = (UserDAO)getApplicationContext().getBean("user.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");

			log.log(Level.INFO, "Dumping Getin Users....");

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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("astrid@getin.mx"));
				userMenuDao.create(um);
			}

			User luis = null;
			try {
				luis = userDao.get("luis@getin.mx", true);
			} catch( Exception e ) {
				luis = new User();
				luis.setFirstname("luis");
				luis.setLastname("");
				luis.setEmail("luis@getin.mx");
				luis.getSecuritySettings().setRole(Role.ADMIN);
				luis.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				luis.setKey((Key)keyHelper.obtainKey(User.class, "luis@getin.mx"));
				userDao.create(luis);
			}

			try {
				um = userMenuDao.get("luis@getin.mx", true);
				userMenuDao.delete("luis@getin.mx");
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("luis@getin.mx"));
				userMenuDao.create(um);
			}

			User luis2 = null;
			try {
				luis2 = userDao.get("luis.vazquez@getin.mx", true);
			} catch( Exception e ) {
				luis2 = new User();
				luis2.setFirstname("Luis");
				luis2.setLastname("Vazquez");
				luis2.setEmail("luis.vazquez@getin.mx");
				luis2.getSecuritySettings().setRole(Role.ADMIN);
				luis2.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				luis2.setKey((Key)keyHelper.obtainKey(User.class, "luis.vazquez@getin.mx"));
				userDao.create(luis2);
			}

			try {
				um = userMenuDao.get("luis.vazquez@getin.mx", true);
				userMenuDao.delete("luis.vazquez@getin.mx");
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("luis.vazquez@getin.mx"));
				userMenuDao.create(um);
			}
			
			User anilu = null;
			try {
				anilu = userDao.get("anilu@getin.mx", true);
			} catch( Exception e ) {
				anilu = new User();
				anilu.setFirstname("Anilú");
				anilu.setLastname("Béjar");
				anilu.setEmail("anilu@getin.mx");
				anilu.getSecuritySettings().setRole(Role.ADMIN);
				anilu.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				anilu.setKey((Key)keyHelper.obtainKey(User.class, "anilu@getin.mx"));
				userDao.create(anilu);
			}

			try {
				um = userMenuDao.get("anilu@getin.mx", true);
				userMenuDao.delete("anilu@getin.mx");
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("anilu@getin.mx"));
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.getEntries().add(new UserMenuEntry("index.processes", "fa-fast-backward", "Reprocesos"));
				um.setKey(userMenuDao.createKey("anabell@getin.mx"));
				userMenuDao.create(um);
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
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
				um.getEntries().add(new UserMenuEntry("index.storeitems", "fa-microchip", "Items Vendidos"));
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
			
			User ecobutik = null;
			try {
				ecobutik = userDao.get("ecobutik_mx", true);
			} catch( Exception e ) {
				ecobutik = new User();
				ecobutik.setFirstname("Ecobutik");
				ecobutik.setLastname("Mexico");
				ecobutik.setEmail("ecobutik@allshoppings.mobi");
				ecobutik.getSecuritySettings().setRole(Role.BRAND);
				ecobutik.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				ecobutik.setKey((Key)keyHelper.obtainKey(User.class, "ecobutik_mx"));
				userDao.create(ecobutik);
			}
			try {
				um = userMenuDao.get("ecobutik_mx", true);
				userMenuDao.delete("ecobutik_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ecobutik_mx"));
				userMenuDao.create(um);
			}
			
			User canallaBistro = null;
			try {
				canallaBistro = userDao.get("canalla_bistro_mx", true);
			} catch( Exception e ) {
				canallaBistro = new User();
				canallaBistro.setFirstname("Canalla Bistro");
				canallaBistro.setLastname("Mexico");
				canallaBistro.setEmail("canallabistro@allshoppings.mobi");
				canallaBistro.getSecuritySettings().setRole(Role.BRAND);
				canallaBistro.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				canallaBistro.setKey((Key)keyHelper.obtainKey(User.class, "canalla_bistro_mx"));
				userDao.create(canallaBistro);
			}
			try {
				um = userMenuDao.get("canalla_bistro_mx", true);
				userMenuDao.delete("canalla_bistro_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("canalla_bistro_mx"));
				userMenuDao.create(um);
			}
			
			User bestbuy = null;
			try {
				bestbuy = userDao.get("bestbuy_mx", true);
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

			User test = null;
			try {
				test = userDao.get("test_mx", true);
			} catch( Exception e ) {
				test = new User();
				test.setFirstname("Test");
				test.setLastname("Mexico");
				test.setEmail("test@allshoppings.mobi");
				test.getSecuritySettings().setRole(Role.STORE);
				test.getSecuritySettings()
						.setStores(Arrays.asList("dcddac8b-fea4-4cfe-af7d-0aced11d0900",
								"609437b2-d112-45ac-8dae-4deed022145a", "ac024504-06a0-4536-9b47-93ab5018fdb2",
								"4aed23a4-b42d-426f-9d46-5dc0a98e09ea", "10422a41-4d4b-486d-bb48-ea6793d1fe25",
								"9118af23-134a-42d2-b39a-cc606c35c788", "a188cdc1-d91c-4e0e-80c7-1d3d24053073",
								"ea873e0a-2e4d-4d12-af0a-8929cd695caa", "89a5aa60-59f9-470a-9ca5-b327c9560d03",
								"0e4f93b9-4d2f-4b79-84dc-dec26d5628b3", "7c75f5ce-7777-4e7c-b1f4-9f26ed638740",
								"07f04746-ad54-4afd-8fa4-dd696730d3ff", "caf3b63d-87ba-46dd-b446-23e0027abd50",
								"3967a9dd-1327-4751-bba1-b1b12a5cfbbc", "974bd1c0-dbee-4297-b739-09b7b5ce5bea",
								"f3de08a9-cc03-435c-a821-38e44a9ab504", "dfb70228-4b3b-4a83-929e-5d95f9dabe23",
								"4c645c62-c972-4b0d-aaff-bffc1639ee63", "cc6a44c7-bcfe-44e3-b6cf-e6f06bf48b47"));
				test.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				test.setKey((Key)keyHelper.obtainKey(User.class, "test_mx"));
				userDao.create(test);
			}

			try {
				um = userMenuDao.get("test_mx", true);
				userMenuDao.delete("test_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("test_mx"));
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
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
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
			} catch( Exception e ) {
				modatelas = new User();
				modatelas.setFirstname("Modatelas");
				modatelas.setLastname("Mexico");
				modatelas.setEmail("modatelas@allshoppings.mobi");
				modatelas.getSecuritySettings().setRole(Role.STORE);
				modatelas.getSecuritySettings()
						.setStores(Arrays.asList("6d2955f9-f112-47b3-ae50-6528697c249c",
								"94634900-964f-4092-a0dd-e1dc287144ee", "58131ca7-ae67-4022-bcfa-0af9301bdddc",
								"70e3f099-03ef-4df2-9849-0f4f4a3dd411", "ceade140-174b-464c-9116-70ef2b93317a",
								"7c10f699-b944-4627-9155-22af377da01f", "50b32c68-0998-4323-a404-eee8e316e3eb",
								"08644a45-800d-43ac-b228-78c28d82bad1", "76eccdaf-348d-4540-af6d-145463e0844e",
								"519d19e7-ddf4-47a2-abdc-09e89d1f5c64", "07b5d16f-6b65-4beb-b763-d47f8a089efd",
								"d93f2cf8-de1f-4e5c-884b-adb87cbbcdf2", "3f7497de-95c8-4bb5-a9a5-6d097133bf7c",
								"80175990-43cd-4c27-a5ca-d16179c7f55f", "4da00bc6-ae1a-418a-a784-1f764d281908",
								"7e47d9df-f011-4203-9ac5-5aa7222cccb5", "fe9ad717-4f1e-4b28-b9ea-bfa94ace3141",
								"7b7d1c1e-f6af-40b7-8a55-f3bdbf19cde1", "dbd5021e-4d65-47da-a64e-42006513d998",
								"dd1edbcb-5eeb-467e-af3d-223611492520", "2bad1b20-31c4-44c4-9015-6e9dd5c30b00",
								"3307ec01-1e57-40e2-97a3-d1e02227a4b0", "9202f7a2-4354-47bb-a6c3-dd775af93a17",
								"9ccdd1eb-30fe-4304-9bd5-7a4b0614c842", "740547b3-5c3a-492c-a2f8-bc88345fcc5d",
								"b7cb1719-2f50-45c2-a1fa-66c98cd1a7e4", "865adafe-7df9-46fc-8201-32f260e5ff06",
								"f4a78ecf-07d9-4158-bcd4-159a68a247df", "0125a1b9-0d3a-4383-8377-04674b1fc08f"));
				modatelas.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				modatelas.setKey((Key)keyHelper.obtainKey(User.class, "modatelas_mx"));
				modatelas.getSecuritySettings().setShoppings(Arrays.asList("740547b3-5c3a-492c-a2f8-bc88345fcc5d"));
				userDao.create(modatelas);
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
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("botanicus_mx"));
				userMenuDao.create(um);
			}
			
			User gameplanet = null;
			try {
				gameplanet = userDao.get("gameplanet_mx", true);
			} catch( Exception e ) {
				gameplanet = new User();
				gameplanet.setFirstname("Game Planet");
				gameplanet.setLastname("Mexico");
				gameplanet.setEmail("gameplanet@allshoppings.mobi");
				gameplanet.getSecuritySettings().setRole(Role.BRAND);
				gameplanet.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				gameplanet.setKey((Key)keyHelper.obtainKey(User.class, "gameplanet_mx"));
				userDao.create(gameplanet);
			}

			try {
				um = userMenuDao.get("gameplanet_mx", true);
				userMenuDao.delete("gameplanet_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("gameplanet_mx"));
				userMenuDao.create(um);
			}
			
			User converse = null;
			try {
				converse = userDao.get("converse_mx", true);
			} catch( Exception e ) {
				converse = new User();
				converse.setFirstname("Converse");
				converse.setLastname("Mexico");
				converse.setEmail("converse@allshoppings.mobi");
				converse.getSecuritySettings().setRole(Role.BRAND);
				converse.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				converse.setKey((Key)keyHelper.obtainKey(User.class, "converse_mx"));
				userDao.create(converse);
			}

			try {
				um = userMenuDao.get("converse_mx", true);
				userMenuDao.delete("converse_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("converse_mx"));
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
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("prada_mx"));
				userMenuDao.create(um);
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
				areasmexico.getSecuritySettings().setRole(Role.STORE);
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
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
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
			
			User capadeozono = null;
			try {
				capadeozono = userDao.get("capadeozono_mx", true);
			} catch( Exception e ) {
				capadeozono = new User();
				capadeozono.setFirstname("Capa de Ozono");
				capadeozono.setLastname("Mexico");
				capadeozono.setEmail("capadeozono_mx@allshoppings.mobi");
				capadeozono.getSecuritySettings().setRole(Role.BRAND);
				capadeozono.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				capadeozono.setKey((Key)keyHelper.obtainKey(User.class, "capadeozono_mx"));
				userDao.create(capadeozono);
			}

			try {
				um = userMenuDao.get("capadeozono_mx", true);
				userMenuDao.delete("capadeozono_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("capadeozono_mx"));
				userMenuDao.create(um);
			}

			User maskota = null;
			try {
				maskota = userDao.get("maskota_mx", true);
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

			User sallybeauty = null;
			try {
				sallybeauty = userDao.get("sallybeauty_mx", true);
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
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("sallybeauty_mx"));
				userMenuDao.create(um);
			}
			User roku = null;
			try {
				roku = userDao.get("roku_mx", true);
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
				userDao.delete("latabernadelleon_mx");
			} catch( Exception e ) {
				/*latabernadelleon = new User();
				latabernadelleon.setFirstname("La Taberna del León");
				latabernadelleon.setLastname("Mexico");
				latabernadelleon.setEmail("latabernadelleon@allshoppings.mobi");
				latabernadelleon.getSecuritySettings().setRole(Role.BRAND);
				latabernadelleon.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				latabernadelleon.setKey((Key)keyHelper.obtainKey(User.class, "latabernadelleon_mx"));
				userDao.create(latabernadelleon);*/
			}

			try {
				um = userMenuDao.get("latabernadelleon_mx", true);
				userMenuDao.delete("latabernadelleon_mx");
				//throw new Exception();
			} catch( Exception e ) {
				/*um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("latabernadelleon_mx"));
				userMenuDao.create(um);*/
			}
			
			User marketintelligence = null;
			try {
				marketintelligence = userDao.get("marketintelligence_mx", true);
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
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.setKey(userMenuDao.createKey("aditivo_mx"));
				userMenuDao.create(um);
			}

			try {
				aditivo = userDao.get("sistemas@aditivo_mx", true);
			} catch( Exception e ) {
				aditivo = new User();
				aditivo.setFirstname("Aditivo");
				aditivo.setLastname("Mexico");
				aditivo.setEmail("sistemas@aditivo_mx");
				aditivo.getSecuritySettings().setRole(Role.STORE);
				aditivo.getSecuritySettings().setStores(Arrays.asList("32543b75-32b2-4b41-9e03-f876a9e88d18",
						"92ec9131-dbf2-4a42-a3ef-1d68170de391", "77bfffb3-48a9-43b6-b1ec-51d526e96da8",
						"13b66935-ec48-4fea-acc9-9e99f025a63b", "b6d96f4a-d9f7-4537-87ae-c6b3f4b3c5e5",
						"129f18c1-c531-4488-9125-6d4e4ccf6d4d", "5da4f3c0-fe1f-47cb-9b7b-5fc4242240ce",
						"3338e021-59c4-4482-9603-fac42d656c7b", "78416e3d-2274-4a24-9186-3616588f6197",
						"2810ac0e-480b-4374-8130-134862088a86", "2a90e8ab-fe34-4a3f-8bd4-0480ce4f40f8",
						"1b509e3a-068e-4062-9781-d04c175db304", "95744605-8649-4091-bdfe-5426ad0b6b3e",
						"63644d2e-f052-45cb-94f2-911331f298f2", "4d768d73-b9a9-44a0-bbbe-dc00a04f52ec",
						"98abde27-4dcc-4d5b-ac16-d43eac63b94b", "11a2f4a2-75e3-4ee2-be94-391e02739d28",
						"2fc001ca-b8c4-4a5c-b7e2-c732c9f98ce0", "e41ddf46-b7fe-4d56-b52d-05a6cee7adf0",
						"f1c0b0d9-b2b4-4d63-a553-cec5653a79c3", "4fe543ea-610e-444b-bde3-e0cf12092ae6",
						"1fac0105-51f2-4a5f-ac3f-eaf4b6d311ed", "71352b76-f76e-421b-b114-72f071633b61",
						"d2112ef6-7cfa-49a4-94de-a127e45ff1c1", "da761750-c568-4e8b-9965-ee588c3d1d9a",
						"aff0af5d-45b8-46b6-81ee-12c79990653b", "49264559-dddb-42c5-b1bf-1a52a0eb659f",
						"9beaf247-e674-47a2-9d4c-c550bb1aa7cc", "349b85b9-a083-4e65-9740-f3d59278f635",
						"4dbf03a7-f321-4109-abd0-58780310f09c", "6ad6e636-f5ec-4d8f-a499-c85055e03f4e",
						"f33140b3-3ecd-4d70-bfcc-159f47ac9058", "8cac6f24-fc71-4e4a-b556-5bfe06191f3f",
						"23178716-9ef5-4b57-b88e-ea85d080c0f7", "7adbc141-fbea-4203-8f3e-db3108638c30",
						"9a14f70c-52eb-4756-8fb3-b48ee8b86094", "f9c4de20-c3c1-464c-9d8e-d5158312a9db",
						"fcc7aa01-7b2d-4773-ba65-acc2dd7e592c", "674626e2-4537-44ba-a6a9-58632ec9f5ce",
						"2b06d29f-204e-4a38-ac87-b77cb7d39578", "5d8fa91b-b783-46ef-b615-c6aba8dec1fd",
						"0221582e-c2fd-49d0-98ed-4635cd5e22db", "625f5d03-6726-4bb2-894e-60749397dba6",
						"4d217df4-d2ad-44e6-81eb-ac10a9c040ec", "f04111dd-c59d-419c-bf6f-36ebecbcbd0d",
						"bde9a482-df2e-410e-9a51-06a90d2294d0", "06b26796-bbda-49ad-b0b8-e24bc8cbeef6",
						"b087d73e-ccb5-4457-8b09-e85ba72de7e7", "4d1d6d54-0cc1-4ba5-a40f-ed61284149c9",
						"07c6552d-c3fe-445b-aac4-e1d2c234d2ca", "fbbc3da9-1403-4206-8b22-e1aed2b0ec40",
						"3bd9d22b-65d9-44af-805f-87a77af5f691", "54d1aba3-3e2c-4de4-8065-c55a50109dbc",
						"ffc0b360-00d5-4e8f-8bef-f0472df6cb5f", "ce91457a-f7dc-49d0-93ff-79259e553769",
						"f7b002fd-5c0c-4e2f-9879-0e98bda6cd5d", "b251d67f-b441-42d2-b69d-6a84c036e123",
						"61374a58-a679-4532-811a-aa3340bcc47e", "3928b1d6-2fb7-4a62-a081-9e5a23e78e91"));
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

			User devlyn = null;
			try {
				devlyn = userDao.get("devlyn_mx", true);
			} catch( Exception e ) {
				devlyn = new User();
				devlyn.setFirstname("Opticas Devlyn");
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
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("devlyn_mx"));
				userMenuDao.create(um);
			}

			User outletdeportes = null;
			try {
				outletdeportes = userDao.get("outletdeportes_mx", true);
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
						"6c6a6156-38c3-4167-be09-42d9890510ed", "5d9789e3-f7e5-41e4-8fb3-9d028c485772", 
						"fbf07796-137b-426e-8454-236e18a168c0"));
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
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
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
				tanyamoss = userDao.get("brenda@tanyamoss.com", true);
			} catch( Exception e ) {
				tanyamoss = new User();
				tanyamoss.setFirstname("Brenda");
				tanyamoss.setLastname("Mexico");
				tanyamoss.setEmail("brenda@tanyamoss.com");
				tanyamoss.getSecuritySettings().setRole(Role.STORE);
				tanyamoss.getSecuritySettings().setStores(Arrays.asList("1493049397673","324e7d47-d156-4f77-992a-adb26318b8a8","1493049398128","1493049398625","d7d10b1f-75e4-4b04-b035-24fede6f76eb","22a028be-3ac2-4cb9-bc09-7beeeca4f024","4e139439-c74f-47b8-a41a-030756322a84","0cbaca45-1045-43a4-b238-eb18651732ec","806bdc75-ea6a-4a5f-b4db-a7423e8528d6","590645c4-8ca5-450a-a95f-6fa7c560ee36","cc2e68ee-6d24-4132-8ae5-13d6836e4f69","d0b66984-5e85-4df3-bc88-aa5125354588","7cf9b273-40ed-448e-b948-91c802dc8a22","55d1549f-b746-4c8b-9a12-102a7092668c","fcfc53b9-1455-4895-b8a9-f7bba0adeb4d","be129cb5-6b12-4c83-89fd-2d008adf7947"));
				tanyamoss.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				tanyamoss.setKey((Key)keyHelper.obtainKey(User.class, "brenda@tanyamoss.com"));
				userDao.create(tanyamoss);
			}

			try {
				um = userMenuDao.get("brenda@tanyamoss.com", true);
				userMenuDao.delete("brenda@tanyamoss.com");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("brenda@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				tanyamoss = userDao.get("lupita@tanyamoss.com", true);
			} catch( Exception e ) {
				tanyamoss = new User();
				tanyamoss.setFirstname("Lupita");
				tanyamoss.setLastname("Mexico");
				tanyamoss.setEmail("lupita@tanyamoss.com");
				tanyamoss.getSecuritySettings().setRole(Role.STORE);
				tanyamoss.getSecuritySettings().setStores(Arrays.asList("1493049397673","324e7d47-d156-4f77-992a-adb26318b8a8","1493049398128","1493049398625","d7d10b1f-75e4-4b04-b035-24fede6f76eb","22a028be-3ac2-4cb9-bc09-7beeeca4f024","4e139439-c74f-47b8-a41a-030756322a84","0cbaca45-1045-43a4-b238-eb18651732ec","806bdc75-ea6a-4a5f-b4db-a7423e8528d6","590645c4-8ca5-450a-a95f-6fa7c560ee36","cc2e68ee-6d24-4132-8ae5-13d6836e4f69","d0b66984-5e85-4df3-bc88-aa5125354588","7cf9b273-40ed-448e-b948-91c802dc8a22","55d1549f-b746-4c8b-9a12-102a7092668c","fcfc53b9-1455-4895-b8a9-f7bba0adeb4d","be129cb5-6b12-4c83-89fd-2d008adf7947"));
				tanyamoss.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				tanyamoss.setKey((Key)keyHelper.obtainKey(User.class, "lupita@tanyamoss.com"));
				userDao.create(tanyamoss);
			}

			try {
				um = userMenuDao.get("lupita@tanyamoss.com", true);
				userMenuDao.delete("lupita@tanyamoss.com");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("lupita@tanyamoss.com"));
				userMenuDao.create(um);
			}

			
			try {
				tanyamoss = userDao.get("trini@tanyamoss.com", true);
			} catch( Exception e ) {
				tanyamoss = new User();
				tanyamoss.setFirstname("Trini");
				tanyamoss.setLastname("Mexico");
				tanyamoss.setEmail("trini@tanyamoss.com");
				tanyamoss.getSecuritySettings().setRole(Role.STORE);
				tanyamoss.getSecuritySettings().setStores(Arrays.asList("1493049397673","324e7d47-d156-4f77-992a-adb26318b8a8","1493049398128","1493049398625","d7d10b1f-75e4-4b04-b035-24fede6f76eb","22a028be-3ac2-4cb9-bc09-7beeeca4f024","4e139439-c74f-47b8-a41a-030756322a84","0cbaca45-1045-43a4-b238-eb18651732ec","806bdc75-ea6a-4a5f-b4db-a7423e8528d6","590645c4-8ca5-450a-a95f-6fa7c560ee36","cc2e68ee-6d24-4132-8ae5-13d6836e4f69","d0b66984-5e85-4df3-bc88-aa5125354588","7cf9b273-40ed-448e-b948-91c802dc8a22","55d1549f-b746-4c8b-9a12-102a7092668c","fcfc53b9-1455-4895-b8a9-f7bba0adeb4d","be129cb5-6b12-4c83-89fd-2d008adf7947"));
				tanyamoss.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				tanyamoss.setKey((Key)keyHelper.obtainKey(User.class, "trini@tanyamoss.com"));
				userDao.create(tanyamoss);
			}

			try {
				um = userMenuDao.get("trini@tanyamoss.com", true);
				userMenuDao.delete("trini@tanyamoss.com");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("trini@tanyamoss.com"));
				userMenuDao.create(um);
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
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("tonymoly_mx"));
				userMenuDao.create(um);
			}

			try {
				tonymoly = userDao.get("eflores@grupoeurokordemexico.com", true);
			} catch( Exception e ) {
				tonymoly = new User();
				tonymoly.setFirstname("Tony Moly");
				tonymoly.setLastname("Mexico");
				tonymoly.setEmail("eflores@grupoeurokordemexico.com");
				tonymoly.getSecuritySettings().setRole(Role.STORE);
				tonymoly.getSecuritySettings().setStores(Arrays.asList("6ef41633-4284-43db-9eff-3bb1c05ab0be","7f053945-3118-4625-bdc1-7d47c8fccbcf"));
				tonymoly.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				tonymoly.setKey((Key)keyHelper.obtainKey(User.class, "eflores@grupoeurokordemexico.com"));
				userDao.create(tonymoly);
			}

			try {
				um = userMenuDao.get("eflores@grupoeurokordemexico.com", true);
				userMenuDao.delete("eflores@grupoeurokordemexico.com");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("eflores@grupoeurokordemexico.com"));
				userMenuDao.create(um);
			}

			User farmaciasyza = null;
			try {
				farmaciasyza = userDao.get("farmaciasyza_mx", true);
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
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("farmaciasyza_mx"));
				userMenuDao.create(um);
			}

			User clarins = null;
			try {
				clarins = userDao.get("clarins_mx", true);
			} catch( Exception e ) {
				clarins = new User();
				clarins.setFirstname("Clarins");
				clarins.setLastname("Mexico");
				clarins.setEmail("clarins@allshoppings.mobi");
				clarins.getSecuritySettings().setRole(Role.BRAND);
				clarins.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				clarins.setKey((Key)keyHelper.obtainKey(User.class, "clarins_mx"));
				userDao.create(clarins);
			}

			try {
				um = userMenuDao.get("clarins_mx", true);
				userMenuDao.delete("clarins_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("clarins_mx"));
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

			User liverpoolboutiques = null;
			try {
				liverpoolboutiques = userDao.get("liverpoolboutiques_mx", true);
			} catch( Exception e ) {
				liverpoolboutiques = new User();
				liverpoolboutiques.setFirstname("Liverpool Boutiques");
				liverpoolboutiques.setLastname("Mexico");
				liverpoolboutiques.setEmail("liverpoolboutiques@allshoppings.mobi");
				liverpoolboutiques.getSecuritySettings().setRole(Role.BRAND);
				liverpoolboutiques.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				liverpoolboutiques.setKey((Key)keyHelper.obtainKey(User.class, "liverpoolboutiques_mx"));
				userDao.create(liverpoolboutiques);
			}

			try {
				um = userMenuDao.get("liverpoolboutiques_mx", true);
				userMenuDao.delete("liverpoolboutiques_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("liverpoolboutiques_mx"));
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
			
			User cafeBalcarceAr = null;
			try {
				cafeBalcarceAr = userDao.get("cafe_balcarce_ar", true);
			} catch( Exception e ) {
				cafeBalcarceAr = new User();
				cafeBalcarceAr.setFirstname("Cafe Balcarce");
				cafeBalcarceAr.setLastname("Argentina");
				cafeBalcarceAr.setEmail("cafe_balcarce_ar@allshoppings.mobi");
				cafeBalcarceAr.getSecuritySettings().setRole(Role.BRAND);
				cafeBalcarceAr.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				cafeBalcarceAr.setKey((Key)keyHelper.obtainKey(User.class, "cafe_balcarce_ar"));
				userDao.create(cafeBalcarceAr);
			}

			try {
				um = userMenuDao.get("cafe_balcarce_ar", true);
				userMenuDao.delete("cafe_balcarce_ar");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("cafe_balcarce_ar"));
				userMenuDao.create(um);
			}
			
			User carolinaHerreraIl = null;
			try {
				carolinaHerreraIl = userDao.get("carolina_herrera_il", true);
			} catch( Exception e ) {
				carolinaHerreraIl = new User();
				carolinaHerreraIl.setFirstname("Carolina Herrera");
				carolinaHerreraIl.setLastname("Israel");
				carolinaHerreraIl.setEmail("carolina_herrera_il@allshoppings.mobi");
				carolinaHerreraIl.getSecuritySettings().setRole(Role.BRAND);
				carolinaHerreraIl.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				carolinaHerreraIl.setKey((Key)keyHelper.obtainKey(User.class, "carolina_herrera_il"));
				userDao.create(carolinaHerreraIl);
			}

			try {
				um = userMenuDao.get("carolina_herrera_il", true);
				userMenuDao.delete("carolina_herrera_il");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("carolina_herrera_il"));
				userMenuDao.create(um);
			}
			
			User dentalia = null;
			try {
				dentalia = userDao.get("dentalia_mx", true);
			} catch( Exception e ) {
				dentalia = new User();
				dentalia.setFirstname("Dentalia");
				dentalia.setLastname("Mexico");
				dentalia.setEmail("dentalia_mx@allshoppings.mobi");
				dentalia.getSecuritySettings().setRole(Role.BRAND);
				dentalia.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				dentalia.setKey((Key)keyHelper.obtainKey(User.class, "dentalia_mx"));
				userDao.create(dentalia);
			}

			try {
				um = userMenuDao.get("dentalia_mx", true);
				userMenuDao.delete("dentalia_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("dentalia_mx"));
				userMenuDao.create(um);
			}
			
			User farmaciassimilares = null;
			try {
				farmaciassimilares = userDao.get("farmacias_similares_mx", true);
			} catch( Exception e ) {
				farmaciassimilares = new User();
				farmaciassimilares.setFirstname("Farmacias Similares");
				farmaciassimilares.setLastname("Mexico");
				farmaciassimilares.setEmail("farmacias_similares_mx@allshoppings.mobi");
				farmaciassimilares.getSecuritySettings().setRole(Role.BRAND);
				farmaciassimilares.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				farmaciassimilares.setKey((Key)keyHelper.obtainKey(User.class, "farmacias_similares_mx"));
				userDao.create(farmaciassimilares);
			}

			try {
				um = userMenuDao.get("farmacias_similares_mx", true);
				userMenuDao.delete("farmacias_similares_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("farmacias_similares_mx"));
				userMenuDao.create(um);
			}
			
			User modaHolding = null;
			try {
				modaHolding = userDao.get("moda_holding_mx", true);
			} catch( Exception e ) {
				modaHolding = new User();
				modaHolding.setFirstname("Moda Holding");
				modaHolding.setLastname("Mexico");
				modaHolding.setEmail("moda_holding_mx@allshoppings.mobi");
				modaHolding.getSecuritySettings().setRole(Role.BRAND);
				modaHolding.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				modaHolding.setKey((Key)keyHelper.obtainKey(User.class, "moda_holding_mx"));
				userDao.create(modaHolding);
			}

			try {
				um = userMenuDao.get("moda_holding_mx", true);
				userMenuDao.delete("moda_holding_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("moda_holding_mx"));
				userMenuDao.create(um);
			}

			// Fullsand --------------------------------------------------------------------
			User user = null;

			try {
				um = userMenuDao.get("mayrafranco@fullsand_mx", true);
				userMenuDao.delete("mayrafranco@fullsand_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("mayrafranco@fullsand_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("mayrafranco@fullsand_mx", true);
			} catch( Exception e ) {
				user = new User();
				user.setFirstname("Mayra");
				user.setLastname("Franco");
				user.setEmail("mayrafranco@fullsand_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("c0919283-ef3b-4a62-b5e6-22a3ce9d5271"));
				user.setKey((Key)keyHelper.obtainKey(User.class, "mayrafranco@fullsand_mx"));
				userDao.create(user);
			}

			// Chomarc --------------------------------------------------------------------
			
			User chomarc = null;
			try {
				chomarc = userDao.get("chomarc_mx", true);
			} catch( Exception e ) {
				chomarc = new User();
				chomarc.setFirstname("Chomarc");
				chomarc.setLastname("Mexico");
				chomarc.setEmail("chomarc_mx@allshoppings.mobi");
				chomarc.getSecuritySettings().setRole(Role.BRAND);
				chomarc.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				chomarc.setKey((Key)keyHelper.obtainKey(User.class, "chomarc_mx"));
				userDao.create(chomarc);
			}

			try {
				um = userMenuDao.get("chomarc_mx", true);
				userMenuDao.delete("chomarc_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("chomarc_mx"));
				userMenuDao.create(um);
			}
			
			try {
				um = userMenuDao.get("adassist@grupochomarc.com.mx", true);
				userMenuDao.delete("adassist@grupochomarc.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("adassist@grupochomarc.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("adassist@grupochomarc.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Adassist");
				user.setLastname("");
				user.setEmail("adassist@grupochomarc.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4df8d43b-b8d6-41a2-a342-082884a5e897",
								"8b0d5f06-f5f6-4d70-aab5-b3ef1e807d97", "b9e3dee9-fcb8-4c36-9620-2c897b03566f",
								"b327518a-28a8-4ca0-b82a-bd1e646307ce", "6e39b7d5-dcd5-458b-9088-a2c97be409e3",
								"743003df-cb85-4f8f-98eb-d41ff31f3e36", "e0bb9d40-7639-47d0-ab38-135b280ac769"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "adassist@grupochomarc.com.mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("anallerena@chomarc_mx", true);
				userMenuDao.delete("anallerena@chomarc_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("anallerena@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("anallerena@chomarc_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Ana");
				user.setLastname("Llerena");
				user.setEmail("anallerena@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4df8d43b-b8d6-41a2-a342-082884a5e897",
								"8b0d5f06-f5f6-4d70-aab5-b3ef1e807d97", "b9e3dee9-fcb8-4c36-9620-2c897b03566f",
								"b327518a-28a8-4ca0-b82a-bd1e646307ce", "6e39b7d5-dcd5-458b-9088-a2c97be409e3",
								"743003df-cb85-4f8f-98eb-d41ff31f3e36", "e0bb9d40-7639-47d0-ab38-135b280ac769"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "anallerena@chomarc_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("beatrizcors@chomarc_mx", true);
				userMenuDao.delete("beatrizcors@chomarc_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("beatrizcors@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("beatrizcors@chomarc_mx", true);
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
			} catch (Exception e) {
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
				user.getSecuritySettings().setStores(
						Arrays.asList("b5e35d13-3abc-4629-993a-c742dbd81f0e", "5dce55ee-8506-4b70-b7a7-aee2a7e6cbb4"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "evyreiter@chomarc_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("rogersilva@chomarc_mx", true);
				userMenuDao.delete("rogersilva@chomarc_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("rogersilva@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("rogersilva@chomarc_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Roger");
				user.setLastname("Silva");
				user.setEmail("rogersilva@chomarc_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1480464171515", "28b335d2-9bf2-48c3-a335-d2ae4314247a",
								"4beba5a8-3987-489e-9ef7-8be65d3c1b27", "81b52571-3bd3-44e3-bed9-9e592d568f04",
								"06a99c76-c4ca-4952-a99f-3576c7e4dce0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "rogersilva@chomarc_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("ginatena@chomarc_mx", true);
				userMenuDao.delete("ginatena@chomarc_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ginatena@chomarc_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ginatena@chomarc_mx", true);
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
			} catch (Exception e) {
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
				user.getSecuritySettings().setStores(Arrays.asList("5dce55ee-8506-4b70-b7a7-aee2a7e6cbb4"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "leonardooneto@chomarc_mx"));
				userDao.create(user);
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
				user.setFirstname("Sportium Cuautitlan");
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
				user.setFirstname("Sportium Desierto");
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
				user.setFirstname("Sportium Lomas Verdes");
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
			
			try {
				um = userMenuDao.get("cymfelipesuper@gmail.com", true);
				userMenuDao.delete("cymfelipesuper@gmail.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("cymfelipesuper@gmail.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("cymfelipesuper@gmail.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Outlet Deportes");
				user.setLastname("");
				user.setEmail("cymfelipesuper@gmail.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("d37e8f77-b863-4f71-9117-45c0eaa121e2",
						"f718f26a-c6de-4149-9605-d737450b7bd9", "5cad0cb3-196a-4f3f-b4f4-224f610f6467",
						"c0d2ccb9-0ed4-45f8-8f4c-770ddb4495b4", "3a361ad5-9748-4bfa-9a69-460fd8214e6e",
						"fb111c5b-db32-4129-a09a-f15269f57285", "b6b1a93f-0116-4d9a-ad40-9c6842eaa8c0",
						"115e5c3c-7850-4e3e-82d1-16b15b5256a5", "23b16093-fbbf-4d9a-811b-6c82bd0eb940",
						"0804ce51-a635-40be-8952-28cc25c946dd", "41e68dc9-254d-4803-82b7-c083eeaf28df",
						"9263926c-88e3-435b-ad7e-1920abfb73a6", "649c6de9-05aa-40de-bd39-7b1d37921658",
						"958d7395-fb37-4b8b-a716-b2c1a9ffdb9f", "9bbf47a2-5a32-4ae3-b217-858c7c1e2703",
						"01bf63e5-6b31-4bf9-beb6-2a9dcbb8a304", "791d303b-3f45-4037-a6f4-2ba9c1e15c75",
						"e43a9f92-7db7-46d1-8a74-3f629eea2b47", "dcb748f6-a060-43da-9a13-aea9ca02245a",
						"247b4ead-822f-4713-9465-177666b2e31c", "40b7b23d-f2b4-4f70-be05-a0ad51ce44ba",
						"440dfccd-73b3-48a5-98c8-d893a01a085f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "cymfelipesuper@gmail.com"));
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
			
			try {
				um = userMenuDao.get("enriqueg@98coastav.mx", true);
				userMenuDao.delete("enriqueg@98coastav.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("enriqueg@98coastav.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("enriqueg@98coastav.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("98 Coast Av.");
				user.setLastname("");
				user.setEmail("enriqueg@98coastav.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("f03d2386-3c1e-40c7-9005-d402f565f107",
						"510b13bc-c316-42e1-853a-38dcf8855746", "83d06e8f-4ca8-4a92-bf48-b796bc24ac50",
						"2edff7a5-6000-45b1-9425-a7712e133d80"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "enriqueg@98coastav.mx"));
				userDao.create(user);
			}

			// End 98 Coast av  --------------------------------------------------------------------
			
			// Custom Grupo Pavel  --------------------------------------------------------------------
			try {
				um = userMenuDao.get("aaguilar@pavel.com.mx", true);
				userMenuDao.delete("aaguilar@pavel.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("aaguilar@pavel.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("aaguilar@pavel.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Grupo Pavel");
				user.setLastname("");
				user.setEmail("aaguilar@pavel.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("0cd9f3e4-d932-44dd-a1ae-6b04540484be"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "aaguilar@pavel.com.mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("imorales@pavel.com.mx", true);
				userMenuDao.delete("imorales@pavel.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("imorales@pavel.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("imorales@pavel.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Grupo Pavel");
				user.setLastname("");
				user.setEmail("imorales@pavel.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("48d17b46-6d7f-483a-bf99-18216c958141"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "imorales@pavel.com.mx"));
				userDao.create(user);
			}

			// End Custom Grupo Pavel  --------------------------------------------------------------------
			
			// Farmacias YZA  --------------------------------------------------------------------

			try {
				um = userMenuDao.get("juan.almanza@yza.mx", true);
				userMenuDao.delete("juan.almanza@yza.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("juan.almanza@yza.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("juan.almanza@yza.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Farmacias YZA");
				user.setLastname("");
				user.setEmail("juan.almanza@yza.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("5a7ebef0-fd9f-4948-8514-046858211228", 
		                "fb7eb0d0-fb1f-4c13-b32d-2b61d7735fad", "4556e3e5-9b68-42db-b9f3-7e1b1f13f485", 
		                "d972339b-d7ca-4485-900f-5247e4f93f64", "ed2acf8d-dde1-4953-9800-fa5eef2af236", 
		                "a3d27d3f-6594-49d6-8ad9-951cdbe4c297", "b14d5fbf-c84e-4b5c-884a-2eca72523f43", 
		                "a0692bbd-1e2b-44e5-9f95-b03af314aa2e", "52ded1e4-aa25-4cd0-b812-90d55dbdc1ab",
		                "00bff0d2-436f-4fd3-92b6-77539007f19a"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "juan.almanza@yza.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("alberto.perez@yza.mx", true);
				userMenuDao.delete("alberto.perez@yza.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				um.setKey(userMenuDao.createKey("alberto.perez@yza.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("alberto.perez@yza.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Farmacias YZA");
				user.setLastname("");
				user.setEmail("alberto.perez@yza.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings().setStores(Arrays.asList("5a7ebef0-fd9f-4948-8514-046858211228", 
		                "fb7eb0d0-fb1f-4c13-b32d-2b61d7735fad", "4556e3e5-9b68-42db-b9f3-7e1b1f13f485", 
		                "d972339b-d7ca-4485-900f-5247e4f93f64", "ed2acf8d-dde1-4953-9800-fa5eef2af236", 
		                "a3d27d3f-6594-49d6-8ad9-951cdbe4c297", "b14d5fbf-c84e-4b5c-884a-2eca72523f43", 
		                "a0692bbd-1e2b-44e5-9f95-b03af314aa2e", "52ded1e4-aa25-4cd0-b812-90d55dbdc1ab",
		                "00bff0d2-436f-4fd3-92b6-77539007f19a"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "alberto.perez@yza.mx"));
				userDao.create(user);
			}

			// End Farmacias YZA  --------------------------------------------------------------------
			
			
			// Prada  --------------------------------------------------------------------
			
				// 	Complete access ------------------------------------------------------

			try {
				um = userMenuDao.get("bprada@prada.mx", true);
				userMenuDao.delete("bprada@prada.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("bprada@prada.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("bprada@prada.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Begoña Prada");
				user.setLastname("");
				user.setEmail("bprada@prada.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "bprada@prada.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("gcastellanos@pradastores.mx", true);
				userMenuDao.delete("gcastellanos@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("gcastellanos@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("gcastellanos@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Gabriel Castellanos");
				user.setLastname("");
				user.setEmail("gcastellanos@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "gcastellanos@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("maguirre@prada.mx", true);
				userMenuDao.delete("maguirre@prada.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("maguirre@prada.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("maguirre@prada.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Luis Miguel Aguirre");
				user.setLastname("");
				user.setEmail("maguirre@prada.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "maguirre@prada.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("dperez@prada.mx", true);
				userMenuDao.delete("dperez@prada.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("dperez@prada.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("dperez@prada.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("David Pérez");
				user.setLastname("");
				user.setEmail("dperez@prada.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "dperez@prada.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("truiz@prada.mx", true);
				userMenuDao.delete("truiz@prada.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("truiz@prada.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("truiz@prada.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tania Ruiz");
				user.setLastname("");
				user.setEmail("truiz@prada.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "truiz@prada.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("cbarajas@prada.mx", true);
				userMenuDao.delete("cbarajas@prada.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("cbarajas@prada.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("cbarajas@prada.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Claudia Barajas");
				user.setLastname("");
				user.setEmail("cbarajas@prada.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "cbarajas@prada.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("agutierrez@prada.mx", true);
				userMenuDao.delete("agutierrez@prada.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("agutierrez@prada.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("agutierrez@prada.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada");
				user.setLastname("");
				user.setEmail("agutierrez@prada.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72",
								"2e029627-eb93-4646-999e-ff7894b45cd0", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"61534aa2-ec08-471e-9378-eff26344edec", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "74bd6202-8be7-4ec3-9ebd-5141f3ca49bd",
								"f94a2759-554c-49a4-a32d-84c81cfe98cc", "ce7bdec9-4de3-4d1c-8fe2-80b02f88f083",
								"82979029-3ca9-4bc3-aaaf-d9ccdfca562c", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
								"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
								"f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1", "dcbce874-c1c8-4b33-ac18-ffa2532ca3f9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "d662d502-4d45-40e1-a160-a8d1639d57c3",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"3acd7c49-a32d-4b45-9064-d7360c11b6ed", "f4fea5b7-475c-483f-896d-64422319382d",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "bd39ba69-eb84-4679-b3e2-0f9276eb76b7",
								"a062c268-877d-47e4-80aa-894da6ec93cc", "1479926604326",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59", "4568bab3-27eb-4d49-84cd-3fa594acd3df",
								"1479933115741", "1479926604340", 
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "agutierrez@prada.mx"));
				userDao.create(user);
			}
			
				// End complete access --------------------------------------------------
			
				// Zona Norte -----------------------------------------------------------

			try {
				um = userMenuDao.get("lamartinez@pradastores.mx", true);
				userMenuDao.delete("lamartinez@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("lamartinez@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("lamartinez@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Luis Arturo Martínez");
				user.setLastname("");
				user.setEmail("lamartinez@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1479933115741",
								"2179a275-e43a-42ac-b6e7-6eedf3f4ee0a", "a062c268-877d-47e4-80aa-894da6ec93cc",
								"bd39ba69-eb84-4679-b3e2-0f9276eb76b7", "82979029-3ca9-4bc3-aaaf-d9ccdfca562c",
								"6b51b0f6-4e52-41b0-b59d-cec9e89c042b", "3bc93117-4e24-4298-bcbe-7d5de3c38efb",
								"49272db7-dee7-4230-bbfb-d9bdcc296f59"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "lamartinez@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("santafe@pradastores.mx", true);
				userMenuDao.delete("santafe@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("santafe@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("santafe@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Santa Fe");
				user.setLastname("");
				user.setEmail("santafe@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1479933115741"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "santafe@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("atizapan@pradastores.mx", true);
				userMenuDao.delete("atizapan@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("atizapan@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("atizapan@pradastores.mx", true);				
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerias Atizapan");
				user.setLastname("");
				user.setEmail("atizapan@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("2179a275-e43a-42ac-b6e7-6eedf3f4ee0a"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "atizapan@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("interlomas@pradastores.mx", true);
				userMenuDao.delete("interlomas@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("interlomas@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("interlomas@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Paseo Interlomas");
				user.setLastname("");
				user.setEmail("interlomas@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("a062c268-877d-47e4-80aa-894da6ec93cc"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "interlomas@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("pviavallejo@pradastores.mx", true);
				userMenuDao.delete("pviavallejo@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("pviavallejo@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("pviavallejo@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Parque Via Vallejo");
				user.setLastname("");
				user.setEmail("pviavallejo@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("bd39ba69-eb84-4679-b3e2-0f9276eb76b7"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "pviavallejo@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("gtoluca@pradastores.mx", true);
				userMenuDao.delete("gtoluca@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("gtoluca@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("gtoluca@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerias Toluca");
				user.setLastname("");
				user.setEmail("gtoluca@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("82979029-3ca9-4bc3-aaaf-d9ccdfca562c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "gtoluca@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("guadalajara@pradastores.mx", true);
				userMenuDao.delete("guadalajara@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("guadalajara@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("guadalajara@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada GDL");
				user.setLastname("");
				user.setEmail("guadalajara@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("6b51b0f6-4e52-41b0-b59d-cec9e89c042b"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "guadalajara@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("granplaza@pradastores.mx", true);
				userMenuDao.delete("granplaza@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("granplaza@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("granplaza@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Gran Plaza");
				user.setLastname("");
				user.setEmail("granplaza@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("3bc93117-4e24-4298-bcbe-7d5de3c38efb"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "granplaza@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("pmayor@pradastores.mx", true);
				userMenuDao.delete("pmayor@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("pmayor@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("pmayor@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Plaza Mayor");
				user.setLastname("");
				user.setEmail("pmayor@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("49272db7-dee7-4230-bbfb-d9bdcc296f59"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "pmayor@pradastores.mx"));
				userDao.create(user);
			}
			
				// End Zona Norte -----------------------------------------------------------
			
				// Zona Sur -------------------------------------------------------------
			
			try {
				um = userMenuDao.get("fcanela@pradastores.mx", true);
				userMenuDao.delete("fcanela@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("fcanela@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("fcanela@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Francisco Canela");
				user.setLastname("");
				user.setEmail("fcanela@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1479926604326",
								"74bd6202-8be7-4ec3-9ebd-5141f3ca49bd", "afb95e1f-1774-46fe-b777-16488c2bcd65",
								"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "f4fea5b7-475c-483f-896d-64422319382d",
								"ce7bdec9-4de3-4d1c-8fe2-80b02f88f083", "5982c91b-1465-446b-afb2-53a5e7372b72",
								"7cfdde1e-b869-4ee9-b787-3c5a3f297bb1", "2e029627-eb93-4646-999e-ff7894b45cd0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "fcanela@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("perisur@pradastores.mx", true);
				userMenuDao.delete("perisur@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("perisur@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("perisur@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Perisur");
				user.setLastname("");
				user.setEmail("perisur@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1479926604326"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "perisur@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("insurgentes@pradastores.mx", true);
				userMenuDao.delete("insurgentes@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("insurgentes@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("insurgentes@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerias Insurgentes");
				user.setLastname("");
				user.setEmail("insurgentes@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("74bd6202-8be7-4ec3-9ebd-5141f3ca49bd"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "insurgentes@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("coyoacan@pradastores.mx", true);
				userMenuDao.delete("coyoacan@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("coyoacan@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("coyoacan@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Coyoacan");
				user.setLastname("");
				user.setEmail("coyoacan@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("afb95e1f-1774-46fe-b777-16488c2bcd65"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "coyoacan@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("galeriascoapa@pradastores.mx", true);
				userMenuDao.delete("galeriascoapa@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("galeriascoapa@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("galeriascoapa@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerias Coapa");
				user.setLastname("");
				user.setEmail("galeriascoapa@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("8b3c8cd2-0727-48f8-bc61-ab47d507969c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "galeriascoapa@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("pdelta@pradastores.mx", true);
				userMenuDao.delete("pdelta@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("pdelta@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("pdelta@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Parque Delta");
				user.setLastname("");
				user.setEmail("pdelta@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f4fea5b7-475c-483f-896d-64422319382d"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "pdelta@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("tabasco@pradastores.mx", true);
				userMenuDao.delete("tabasco@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("tabasco@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("tabasco@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerias Tabasco");
				user.setLastname("");
				user.setEmail("tabasco@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("ce7bdec9-4de3-4d1c-8fe2-80b02f88f083"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "tabasco@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("altabrisa@pradastores.mx", true);
				userMenuDao.delete("altabrisa@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("altabrisa@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("altabrisa@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Altabrisa (Tabasco II)");
				user.setLastname("");
				user.setEmail("altabrisa@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("5982c91b-1465-446b-afb2-53a5e7372b72"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "altabrisa@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("merida@pradastores.mx", true);
				userMenuDao.delete("merida@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("merida@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("merida@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Merida");
				user.setLastname("");
				user.setEmail("merida@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("7cfdde1e-b869-4ee9-b787-3c5a3f297bb1"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "merida@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("pcancun@pradastores.mx", true);
				userMenuDao.delete("pcancun@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("pcancun@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("pcancun@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Cancun");
				user.setLastname("");
				user.setEmail("pcancun@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("2e029627-eb93-4646-999e-ff7894b45cd0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "pcancun@pradastores.mx"));
				userDao.create(user);
			}
			
				// End Zona Sur -------------------------------------------------------------
			
				// Zona Centro -------------------------------------------------------------
			
			try {
				um = userMenuDao.get("crua@pradastores.mx", true);
				userMenuDao.delete("crua@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("crua@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("crua@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Claudia Rúa");
				user.setLastname("");
				user.setEmail("crua@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1479926604340",
								"4f9302f7-c155-46c7-b780-17834270a7f7", "f94a2759-554c-49a4-a32d-84c81cfe98cc",
								"61534aa2-ec08-471e-9378-eff26344edec", "3acd7c49-a32d-4b45-9064-d7360c11b6ed",
								"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c", "a604b9c3-949a-4581-9196-5b17e1de989a",
								"4523b563-2e0d-4fe2-8321-9e53740854b2"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "crua@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("satelite@pradastores.mx", true);
				userMenuDao.delete("satelite@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("satelite@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("satelite@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Satelite");
				user.setLastname("");
				user.setEmail("satelite@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1479926604340"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "satelite@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("ptoreo@pradastores.mx", true);
				userMenuDao.delete("ptoreo@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ptoreo@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ptoreo@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Parque Toreo");
				user.setLastname("");
				user.setEmail("ptoreo@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4f9302f7-c155-46c7-b780-17834270a7f7"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ptoreo@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("gserdan@pradastores.mx", true);
				userMenuDao.delete("gserdan@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("gserdan@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("gserdan@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerias Serdan");
				user.setLastname("");
				user.setEmail("gserdan@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f94a2759-554c-49a4-a32d-84c81cfe98cc"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "gserdan@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("fashiondrive@pradastores.mx", true);
				userMenuDao.delete("fashiondrive@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("fashiondrive@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("fashiondrive@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Fashion Drive");
				user.setLastname("");
				user.setEmail("fashiondrive@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("61534aa2-ec08-471e-9378-eff26344edec"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "fashiondrive@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("polanco@pradastores.mx", true);
				userMenuDao.delete("polanco@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("polanco@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("polanco@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Pabellon Polanco");
				user.setLastname("");
				user.setEmail("polanco@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("3acd7c49-a32d-4b45-9064-d7360c11b6ed"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "polanco@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("xalapa@pradastores.mx", true);
				userMenuDao.delete("xalapa@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("xalapa@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("xalapa@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Xalapa");
				user.setLastname("");
				user.setEmail("xalapa@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("b072f5cf-2f5e-4a28-9be4-5525b5c6f83c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "xalapa@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("puebla@pradastores.mx", true);
				userMenuDao.delete("puebla@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("puebla@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("puebla@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Angelópolis");
				user.setLastname("");
				user.setEmail("puebla@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("a604b9c3-949a-4581-9196-5b17e1de989a"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "puebla@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("monterrey@pradastores.mx", true);
				userMenuDao.delete("monterrey@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("monterrey@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("monterrey@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Galerías Valle Oriente");
				user.setLastname("");
				user.setEmail("monterrey@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4523b563-2e0d-4fe2-8321-9e53740854b2"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "monterrey@pradastores.mx"));
				userDao.create(user);
			}
			
				// End Zona Centro -------------------------------------------------------------
			
				// Outlets -------------------------------------------------------------
				
			try {
				um = userMenuDao.get("jcruz@pradastores.mx", true);
				userMenuDao.delete("jcruz@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("jcruz@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("jcruz@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("José Luis Cruz");
				user.setLastname("");
				user.setEmail("jcruz@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("e53ead22-8663-4e09-b0e7-069e91c1fae9",
								"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1",
								"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "d662d502-4d45-40e1-a160-a8d1639d57c3"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "jcruz@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("puntanorte@pradastores.mx", true);
				userMenuDao.delete("puntanorte@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("puntanorte@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("puntanorte@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Outlet Punta Norte");
				user.setLastname("");
				user.setEmail("puntanorte@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("e53ead22-8663-4e09-b0e7-069e91c1fae9"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "puntanorte@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("galerias@pradastores.mx", true);
				userMenuDao.delete("galerias@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("galerias@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("galerias@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Outlet Galerias");
				user.setLastname("");
				user.setEmail("galerias@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("e3d005b1-9162-49c1-855c-cc0eaf19b8b7"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "galerias@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("miramontes@pradastores.mx", true);
				userMenuDao.delete("miramontes@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("miramontes@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("miramontes@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Miramontes/Outlet Zapamundi");
				user.setLastname("");
				user.setEmail("miramontes@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "miramontes@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("outletpuebla@pradastores.mx", true);
				userMenuDao.delete("outletpuebla@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("outletpuebla@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("outletpuebla@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Outlet Puebla");
				user.setLastname("");
				user.setEmail("outletpuebla@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "outletpuebla@pradastores.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("omulza@pradastores.mx", true);
				userMenuDao.delete("omulza@pradastores.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("omulza@pradastores.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("omulza@pradastores.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Prada Outlet Mulza");
				user.setLastname("");
				user.setEmail("omulza@pradastores.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
				user.getSecuritySettings()
						.setStores(Arrays.asList("d662d502-4d45-40e1-a160-a8d1639d57c3"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "omulza@pradastores.mx"));
				userDao.create(user);
			}
			
				// End Outlets -------------------------------------------------------------

			// End Prada  --------------------------------------------------------------------
			
			// Pakmail  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("atenas@pakmail.com.mx", true);
				userMenuDao.delete("atenas@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("atenas@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("atenas@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Atenas");
				user.setLastname("");
				user.setEmail("atenas@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("8bbd9f18-57b4-4e78-95e0-e2e3398c172d"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "atenas@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("coyuya@pakmail.com.mx", true);
				userMenuDao.delete("coyuya@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("coyuya@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("coyuya@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Coyuya");
				user.setLastname("");
				user.setEmail("coyuya@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("208e62ab-3b01-4c92-9799-81ffdb00534e"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "coyuya@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("echegaray@pakmail.com.mx", true);
				userMenuDao.delete("echegaray@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("echegaray@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("echegaray@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Echegaray");
				user.setLastname("");
				user.setEmail("echegaray@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("fcc3bf83-1b3c-4262-a983-afc958a1f144"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "echegaray@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("felixcuevas@pakmail.com.mx", true);
				userMenuDao.delete("felixcuevas@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("felixcuevas@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("felixcuevas@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Felix Cuevas");
				user.setLastname("");
				user.setEmail("felixcuevas@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("a30c36b6-5693-4cad-bc21-44e216b07f66"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "felixcuevas@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("lomasestrella@pakmail.com.mx", true);
				userMenuDao.delete("lomasestrella@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("lomasestrella@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("lomasestrella@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Lomas Estrella");
				user.setLastname("");
				user.setEmail("lomasestrella@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("10b4e6d4-4d68-4545-970f-e0fa6c74964f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "lomasestrella@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("marianootero@pakmail.com.mx", true);
				userMenuDao.delete("marianootero@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("marianootero@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("marianootero@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Mariano Otero");
				user.setLastname("");
				user.setEmail("marianootero@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("77da3d47-1320-4a84-b6b1-5b84d605a657"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "marianootero@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("metepec@pakmail.com.mx", true);
				userMenuDao.delete("metepec@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("metepec@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("metepec@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Metepec");
				user.setLastname("");
				user.setEmail("metepec@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("fdb2636b-38f9-4f6f-a677-5977b29d4340"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "metepec@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("palosolo@pakmail.com.mx", true);
				userMenuDao.delete("palosolo@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("palosolo@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("palosolo@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Palo Solo");
				user.setLastname("");
				user.setEmail("palosolo@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("dc810723-71ec-4771-9dcc-cac8a2820f8c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "palosolo@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("roma@pakmail.com.mx", true);
				userMenuDao.delete("roma@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("roma@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("roma@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Roma");
				user.setLastname("");
				user.setEmail("roma@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4cb29596-70fe-4a59-a5b6-d04944d322ab"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "roma@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("sanjeronimo@pakmail.com.mx", true);
				userMenuDao.delete("sanjeronimo@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("sanjeronimo@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("sanjeronimo@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail San Jeronimo");
				user.setLastname("");
				user.setEmail("sanjeronimo@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("51e0fc41-7007-45ce-aae4-f1239336243e"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "sanjeronimo@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("sanmateoatenco@pakmail.com.mx", true);
				userMenuDao.delete("sanmateoatenco@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("sanmateoatenco@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("sanmateoatenco@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail San Mateo Atenco");
				user.setLastname("");
				user.setEmail("sanmateoatenco@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("de85ff39-d042-47e9-92cc-a6c5f8e68ed4"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "sanmateoatenco@pakmail.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("valladolid@pakmail.com.mx", true);
				userMenuDao.delete("valladolid@pakmail.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("valladolid@pakmail.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("valladolid@pakmail.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Pakmail Valladolid");
				user.setLastname("");
				user.setEmail("valladolid@pakmail.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("EA6B2F91EFA04DA3B6A451BD69EC46F0CDDE28D9B978F5D1CC59448205E646E9");
				user.getSecuritySettings()
						.setStores(Arrays.asList("fd13fe01-9834-465a-8f03-83b36d317c0c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "valladolid@pakmail.com.mx"));
				userDao.create(user);
			}
			
			// End Pakmail  --------------------------------------------------------------------
			
			// Opticas Devlyn  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("ycastor@devlyn.com.mx", true);
				userMenuDao.delete("ycastor@devlyn.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ycastor@devlyn.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ycastor@devlyn.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Opticas Devlyn");
				user.setLastname("");
				user.setEmail("ycastor@devlyn.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("DC8CCB2710BCF5A5404F10C72BF0B8E5A0F572C2382F3F6231D22C009DCA7ACB");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4592b7bd-c2b5-40b8-b99f-ffed79a000cb",
								"67dee1cc-ffbc-4c1d-b97a-78381df35d48", "bc5c771e-535c-4eb8-a85f-9ccf81e63435",
								"af50c63a-144a-4fce-b816-478351fdd27e", "4234966c-8d24-4179-b255-1f095123034f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ycastor@devlyn.com.mx"));
				userDao.create(user);
			}	
			
			try {
				um = userMenuDao.get("vmendivil@devlyn.com.mx", true);
				userMenuDao.delete("vmendivil@devlyn.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("vmendivil@devlyn.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("vmendivil@devlyn.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Opticas Devlyn");
				user.setLastname("");
				user.setEmail("vmendivil@devlyn.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("DC8CCB2710BCF5A5404F10C72BF0B8E5A0F572C2382F3F6231D22C009DCA7ACB");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4592b7bd-c2b5-40b8-b99f-ffed79a000cb",
								"67dee1cc-ffbc-4c1d-b97a-78381df35d48", "bc5c771e-535c-4eb8-a85f-9ccf81e63435",
								"af50c63a-144a-4fce-b816-478351fdd27e", "4234966c-8d24-4179-b255-1f095123034f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "vmendivil@devlyn.com.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("devlyn.andrew@devlyn.com.mx", true);
				userMenuDao.delete("devlyn.andrew@devlyn.com.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("devlyn.andrew@devlyn.com.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("devlyn.andrew@devlyn.com.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Opticas Devlyn");
				user.setLastname("");
				user.setEmail("devlyn.andrew@devlyn.com.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("DC8CCB2710BCF5A5404F10C72BF0B8E5A0F572C2382F3F6231D22C009DCA7ACB");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4592b7bd-c2b5-40b8-b99f-ffed79a000cb",
								"67dee1cc-ffbc-4c1d-b97a-78381df35d48", "bc5c771e-535c-4eb8-a85f-9ccf81e63435",
								"af50c63a-144a-4fce-b816-478351fdd27e", "4234966c-8d24-4179-b255-1f095123034f"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "devlyn.andrew@devlyn.com.mx"));
				userDao.create(user);
			}
			
			// End Opticas Devlyn  --------------------------------------------------------------------
			
			// Tanya Moss  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("aeropuerto_terminal_2@tanyamoss.com", true);
				userMenuDao.delete("aeropuerto_terminal_2@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("aeropuerto_terminal_2@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("aeropuerto_terminal_2@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Aeropuerto CDMX Terminal 2");
				user.setLastname("");
				user.setEmail("aeropuerto_terminal_2@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("be129cb5-6b12-4c83-89fd-2d008adf7947"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "aeropuerto_terminal_2@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("aeropuerto_guadalajara@tanyamoss.com", true);
				userMenuDao.delete("aeropuerto_guadalajara@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("aeropuerto_guadalajara@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("aeropuerto_guadalajara@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Aeropuerto Guadalajara");
				user.setLastname("");
				user.setEmail("aeropuerto_guadalajara@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("7cf9b273-40ed-448e-b948-91c802dc8a22"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "aeropuerto_guadalajara@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("altavista@tanyamoss.com", true);
				userMenuDao.delete("altavista@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("altavista@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("altavista@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Altavista");
				user.setLastname("");
				user.setEmail("altavista@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4e139439-c74f-47b8-a41a-030756322a84"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "altavista@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("andares@tanyamoss.com", true);
				userMenuDao.delete("andares@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("andares@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("andares@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Andares");
				user.setLastname("");
				user.setEmail("andares@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("d0b66984-5e85-4df3-bc88-aa5125354588"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "andares@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("angelopolis@tanyamoss.com", true);
				userMenuDao.delete("angelopolis@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("angelopolis@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("angelopolis@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Angelopolis");
				user.setLastname("");
				user.setEmail("angelopolis@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("55d1549f-b746-4c8b-9a12-102a7092668c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "angelopolis@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("interlomas@tanyamoss.com", true);
				userMenuDao.delete("interlomas@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("interlomas@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("interlomas@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Interlomas");
				user.setLastname("");
				user.setEmail("interlomas@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("806bdc75-ea6a-4a5f-b4db-a7423e8528d6"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "interlomas@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("isla_coyoacan@tanyamoss.com", true);
				userMenuDao.delete("isla_coyoacan@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("isla_coyoacan@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("isla_coyoacan@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Isla Coyoacan");
				user.setLastname("");
				user.setEmail("isla_coyoacan@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("cc2e68ee-6d24-4132-8ae5-13d6836e4f69"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "isla_coyoacan@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("oasis_coyoacan@tanyamoss.com", true);
				userMenuDao.delete("oasis_coyoacan@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("oasis_coyoacan@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("oasis_coyoacan@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Oasis Coyoacan");
				user.setLastname("");
				user.setEmail("oasis_coyoacan@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("fcfc53b9-1455-4895-b8a9-f7bba0adeb4d"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "oasis_coyoacan@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("pabellon_polanco@tanyamoss.com", true);
				userMenuDao.delete("pabellon_polanco@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("pabellon_polanco@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("pabellon_polanco@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Pabellón Polanco");
				user.setLastname("");
				user.setEmail("pabellon_polanco@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("22a028be-3ac2-4cb9-bc09-7beeeca4f024"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "pabellon_polanco@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("palacio_polanco@tanyamoss.com", true);
				userMenuDao.delete("palacio_polanco@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("palacio_polanco@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("palacio_polanco@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Palacio de Hierro Polanco");
				user.setLastname("");
				user.setEmail("palacio_polanco@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("324e7d47-d156-4f77-992a-adb26318b8a8"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "palacio_polanco@tanyamoss.com"));
				userDao.create(user);
			}	
			
			try {
				um = userMenuDao.get("parque_delta@tanyamoss.com", true);
				userMenuDao.delete("parque_delta@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("parque_delta@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("parque_delta@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Parque Delta");
				user.setLastname("");
				user.setEmail("parque_delta@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1493049398625"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "parque_delta@tanyamoss.com"));
				userDao.create(user);
			}	
			
			try {
				um = userMenuDao.get("parque_duraznos@tanyamoss.com", true);
				userMenuDao.delete("parque_duraznos@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("parque_duraznos@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("parque_duraznos@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Parque Duraznos");
				user.setLastname("");
				user.setEmail("parque_duraznos@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("d7d10b1f-75e4-4b04-b035-24fede6f76eb"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "parque_duraznos@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("plaza_carso@tanyamoss.com", true);
				userMenuDao.delete("plaza_carso@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("plaza_carso@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("plaza_carso@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Plaza Carso");
				user.setLastname("");
				user.setEmail("plaza_carso@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1493049397673"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "plaza_carso@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("santa_fe@tanyamoss.com", true);
				userMenuDao.delete("santa_fe@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("santa_fe@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("santa_fe@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Santa Fe");
				user.setLastname("");
				user.setEmail("santa_fe@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("590645c4-8ca5-450a-a95f-6fa7c560ee36"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "santa_fe@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("santa_teresa@tanyamoss.com", true);
				userMenuDao.delete("santa_teresa@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("santa_teresa@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("santa_teresa@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Santa Teresa");
				user.setLastname("");
				user.setEmail("santa_teresa@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("0cbaca45-1045-43a4-b238-eb18651732ec"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "santa_teresa@tanyamoss.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("toreo_parque_central@tanyamoss.com", true);
				userMenuDao.delete("toreo_parque_central@tanyamoss.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("toreo_parque_central@tanyamoss.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("toreo_parque_central@tanyamoss.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Tanya Moss Toreo Parque Central");
				user.setLastname("");
				user.setEmail("toreo_parque_central@tanyamoss.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E3C9360C00CE49925A4EC8C4841587EF0932E97CF2CE9C5E7967B02309296B70");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1493049398128"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "toreo_parque_central@tanyamoss.com"));
				userDao.create(user);
			}
			
			// End Tanya Moss  --------------------------------------------------------------------
			
			// Aditivo  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("juan@aditivo.mx", true);
				userMenuDao.delete("juan@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("juan@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("juan@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Juan Ramirez");
				user.setLastname("");
				user.setEmail("juan@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("ffc0b360-00d5-4e8f-8bef-f0472df6cb5f",
								"1fac0105-51f2-4a5f-ac3f-eaf4b6d311ed", "f33140b3-3ecd-4d70-bfcc-159f47ac9058",
								"349b85b9-a083-4e65-9740-f3d59278f635", "4dbf03a7-f321-4109-abd0-58780310f09c",
								"49264559-dddb-42c5-b1bf-1a52a0eb659f", "2810ac0e-480b-4374-8130-134862088a86",
								"fcc7aa01-7b2d-4773-ba65-acc2dd7e592c", "f7b002fd-5c0c-4e2f-9879-0e98bda6cd5d",
								"674626e2-4537-44ba-a6a9-58632ec9f5ce", "b251d67f-b441-42d2-b69d-6a84c036e123",
								"13b66935-ec48-4fea-acc9-9e99f025a63b", "92ec9131-dbf2-4a42-a3ef-1d68170de391",
								"5da4f3c0-fe1f-47cb-9b7b-5fc4242240ce", "b087d73e-ccb5-4457-8b09-e85ba72de7e7",
								"4fe543ea-610e-444b-bde3-e0cf12092ae6", "71352b76-f76e-421b-b114-72f071633b61",
								"f9c4de20-c3c1-464c-9d8e-d5158312a9db", "32543b75-32b2-4b41-9e03-f876a9e88d18",
								"f1c0b0d9-b2b4-4d63-a553-cec5653a79c3", "9beaf247-e674-47a2-9d4c-c550bb1aa7cc",
								"3338e021-59c4-4482-9603-fac42d656c7b", "98abde27-4dcc-4d5b-ac16-d43eac63b94b",
								"7adbc141-fbea-4203-8f3e-db3108638c30", "78416e3d-2274-4a24-9186-3616588f6197",
								"129f18c1-c531-4488-9125-6d4e4ccf6d4d", "d2112ef6-7cfa-49a4-94de-a127e45ff1c1",
								"1b509e3a-068e-4062-9781-d04c175db304", "95744605-8649-4091-bdfe-5426ad0b6b3e",
								"fbbc3da9-1403-4206-8b22-e1aed2b0ec40", "ce91457a-f7dc-49d0-93ff-79259e553769",
								"61374a58-a679-4532-811a-aa3340bcc47e", "2b06d29f-204e-4a38-ac87-b77cb7d39578",
								"07c6552d-c3fe-445b-aac4-e1d2c234d2ca"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "juan@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("ricardol@aditivo.mx", true);
				userMenuDao.delete("ricardol@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ricardol@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ricardol@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Ricardo Lopez");
				user.setLastname("");
				user.setEmail("ricardol@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("63644d2e-f052-45cb-94f2-911331f298f2",
								"4d768d73-b9a9-44a0-bbbe-dc00a04f52ec", "5d8fa91b-b783-46ef-b615-c6aba8dec1fd",
								"f04111dd-c59d-419c-bf6f-36ebecbcbd0d", "bde9a482-df2e-410e-9a51-06a90d2294d0",
								"0221582e-c2fd-49d0-98ed-4635cd5e22db", "4d217df4-d2ad-44e6-81eb-ac10a9c040ec",
								"06b26796-bbda-49ad-b0b8-e24bc8cbeef6", "3bd9d22b-65d9-44af-805f-87a77af5f691",
								"3928b1d6-2fb7-4a62-a081-9e5a23e78e91", "4d1d6d54-0cc1-4ba5-a40f-ed61284149c9",
								"e41ddf46-b7fe-4d56-b52d-05a6cee7adf0", "11a2f4a2-75e3-4ee2-be94-391e02739d28",
								"da761750-c568-4e8b-9965-ee588c3d1d9a", "77bfffb3-48a9-43b6-b1ec-51d526e96da8",
								"8cac6f24-fc71-4e4a-b556-5bfe06191f3f", "23178716-9ef5-4b57-b88e-ea85d080c0f7",
								"6ad6e636-f5ec-4d8f-a499-c85055e03f4e", "2fc001ca-b8c4-4a5c-b7e2-c732c9f98ce0",
								"b6d96f4a-d9f7-4537-87ae-c6b3f4b3c5e5", "9a14f70c-52eb-4756-8fb3-b48ee8b86094",
								"625f5d03-6726-4bb2-894e-60749397dba6"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ricardol@aditivo.mx"));
				userDao.create(user);
			}
			
			// End Aditivo  --------------------------------------------------------------------
			
			// Modatelas  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("locales_modatelas_mx", true);
				userMenuDao.delete("locales_modatelas_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.patternheatmap", "fa-building", "Patrones"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.setKey(userMenuDao.createKey("locales_modatelas_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("locales_modatelas_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Modatelas Tiendas Locales");
				user.setLastname("");
				user.setEmail("locales_modatelas_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("740547b3-5c3a-492c-a2f8-bc88345fcc5d",
								"519d19e7-ddf4-47a2-abdc-09e89d1f5c64", "0125a1b9-0d3a-4383-8377-04674b1fc08f",
								"7e47d9df-f011-4203-9ac5-5aa7222cccb5", "b7cb1719-2f50-45c2-a1fa-66c98cd1a7e4",
								"865adafe-7df9-46fc-8201-32f260e5ff06", "58131ca7-ae67-4022-bcfa-0af9301bdddc",
								"08644a45-800d-43ac-b228-78c28d82bad1", "2bad1b20-31c4-44c4-9015-6e9dd5c30b00",
								"76eccdaf-348d-4540-af6d-145463e0844e", "7c10f699-b944-4627-9155-22af377da01f",
								"50b32c68-0998-4323-a404-eee8e316e3eb", "9ccdd1eb-30fe-4304-9bd5-7a4b0614c842",
								"4da00bc6-ae1a-418a-a784-1f764d281908", "07b5d16f-6b65-4beb-b763-d47f8a089efd",
								"7b7d1c1e-f6af-40b7-8a55-f3bdbf19cde1", "6d2955f9-f112-47b3-ae50-6528697c249c",
								"ceade140-174b-464c-9116-70ef2b93317a", "70e3f099-03ef-4df2-9849-0f4f4a3dd411",
								"9202f7a2-4354-47bb-a6c3-dd775af93a17", "f4a78ecf-07d9-4158-bcd4-159a68a247df",
								"3307ec01-1e57-40e2-97a3-d1e02227a4b0", "3f7497de-95c8-4bb5-a9a5-6d097133bf7c",
								"80175990-43cd-4c27-a5ca-d16179c7f55f", "dd1edbcb-5eeb-467e-af3d-223611492520",
								"d93f2cf8-de1f-4e5c-884b-adb87cbbcdf2", "fe9ad717-4f1e-4b28-b9ea-bfa94ace3141",
								"dbd5021e-4d65-47da-a64e-42006513d998", "94634900-964f-4092-a0dd-e1dc287144ee"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "locales_modatelas_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("foraneas_modatelas_mx", true);
				userMenuDao.delete("foraneas_modatelas_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.patternheatmap", "fa-building", "Patrones"));
				um.getEntries().add(new UserMenuEntry("index.opentimes", "fa-lightbulb-o", "Horarios de Apertura"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.setKey(userMenuDao.createKey("foraneas_modatelas_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("foraneas_modatelas_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Modatelas Tiendas Foraneas");
				user.setLastname("");
				user.setEmail("foraneas_modatelas_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("9658cd89-f277-4946-bfcb-9330b2c886c2", 
								"a118c701-143d-4ebc-a40f-ea3277949e10", "44e14c13-54cb-4592-a84c-538399074698",
								"9e08742a-b37a-4d7d-9427-c0c585e89fce", "7dadd279-08cc-4a51-86cd-d85df6abf6c3",
								"2b2b88d1-afbc-4da0-be04-1ba057c07e95", "4bb5fd5f-e161-4461-8c9c-84e8172f5466",
								"8e84228e-cc5e-42b5-a665-179ebcd81511", "06c5b1f3-0f78-4100-9038-2ba9603087c9",
								"3c506e3a-c23a-4454-b2e2-578f6f4e081c", "7daf5ac9-bd14-47c2-9909-d849fdc09be2",
								"24bb9d59-a029-4cdc-9341-e2c6b1061126", "9ab15245-554a-4612-8f33-b57d03f58c17",
								"4e8dd00e-a179-40d1-8b72-9389e6e52fb0", "b26c6912-d357-4554-9df9-c054351fa7f6",
								"536a6470-a8e1-46f4-bc2d-be7b0ace6ea7", "92fbf40a-20af-479a-88fd-7a2b8c8f4401",
								"a23edfab-aed9-4562-8100-65544550adf4", "7e129304-5169-4f2d-a51a-9afc61cc3ad7",
								"a7d255b5-3d20-46aa-8c9e-e0696f52b908", "2d59df41-fae4-4570-8afc-16f0c50ff917",
								"a3bbd458-fe46-4799-8f78-11d642196a2c", "3d64ba77-e24c-4c06-83f0-63444ab8eaed",
								"76749515-b97a-4bfa-9cfa-e1521eccb33c", "ac082169-dcd8-4e01-adf0-df3054a5b7af",
								"a6f96106-1f29-4b47-8a2f-b3b28d7e1a8c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "foraneas_modatelas_mx"));
				userDao.create(user);
			}
			
			// End Modatelas  --------------------------------------------------------------------
			
			// Mobo  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("mobo_mx", true);
				userMenuDao.delete("mobo_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("mobo_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("mobo_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Mobo");
				user.setLastname("");
				user.setEmail("mobo_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("51a62c6b-9596-4e63-8241-21563bf5925e",
								"5eea383c-c550-44d3-bc5d-a3bbf8cff283", "4459e3ee-0085-4382-95f4-042af4b89195",
								"1498839031159", "f7e7ac30-e0f3-4c8b-be85-9a4ae88c0400",
								"749fe355-833e-4d93-a0db-e613135aa2d0",
								"7fe9aeb4-a017-45b5-a388-90f88a3b2455", "fe83b69e-7a67-45c8-a08c-8c78c8867d3a",
								"320f0fb2-713b-46ba-8930-36fe497f0c47"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "mobo_mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("mtorre@mobo.mx", true);
				userMenuDao.delete("mtorre@mobo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("mtorre@mobo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("mtorre@mobo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Mobo");
				user.setLastname("");
				user.setEmail("mtorre@mobo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f7e7ac30-e0f3-4c8b-be85-9a4ae88c0400", 
								"749fe355-833e-4d93-a0db-e613135aa2d0", "fe83b69e-7a67-45c8-a08c-8c78c8867d3a"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "mtorre@mobo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("ugodinez@mobo.mx", true);
				userMenuDao.delete("ugodinez@mobo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ugodinez@mobo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ugodinez@mobo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Mobo");
				user.setLastname("");
				user.setEmail("ugodinez@mobo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("51a62c6b-9596-4e63-8241-21563bf5925e",
								"5eea383c-c550-44d3-bc5d-a3bbf8cff283", "4459e3ee-0085-4382-95f4-042af4b89195",
								"1498839031159",
								"7fe9aeb4-a017-45b5-a388-90f88a3b2455", "320f0fb2-713b-46ba-8930-36fe497f0c47"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ugodinez@mobo.mx"));
				userDao.create(user);
			}
			
			// End Mobo  --------------------------------------------------------------------
			
			// GamePlanet  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("rodolfo.urbina@gameplanet.com", true);
				userMenuDao.delete("rodolfo.urbina@gameplanet.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("rodolfo.urbina@gameplanet.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("rodolfo.urbina@gameplanet.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("GamePlanet");
				user.setLastname("");
				user.setEmail("rodolfo.urbina@gameplanet.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("e995f55c-fe80-4c2e-afb7-e5300836effc",
								"ed8d13c7-2ab9-4c88-b0df-d03e2c102247", "42497afe-5bd9-4aa7-8d66-c2161b8feee8"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "rodolfo.urbina@gameplanet.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("marcopolo@gameplanet.com", true);
				userMenuDao.delete("marcopolo@gameplanet.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("marcopolo@gameplanet.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("marcopolo@gameplanet.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("GamePlanet");
				user.setLastname("");
				user.setEmail("marcopolo@gameplanet.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("e995f55c-fe80-4c2e-afb7-e5300836effc",
								"ed8d13c7-2ab9-4c88-b0df-d03e2c102247", "42497afe-5bd9-4aa7-8d66-c2161b8feee8"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "marcopolo@gameplanet.com"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("alberto.vacas@gameplanet.com", true);
				userMenuDao.delete("alberto.vacas@gameplanet.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("alberto.vacas@gameplanet.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("alberto.vacas@gameplanet.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("GamePlanet");
				user.setLastname("");
				user.setEmail("alberto.vacas@gameplanet.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("e995f55c-fe80-4c2e-afb7-e5300836effc",
								"ed8d13c7-2ab9-4c88-b0df-d03e2c102247", "42497afe-5bd9-4aa7-8d66-c2161b8feee8"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "alberto.vacas@gameplanet.com"));
				userDao.create(user);
			}
			
			// End GamePlanet  --------------------------------------------------------------------
			
			// Squalo  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("jogomez@squalo.com", true);
				userMenuDao.delete("jogomez@squalo.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("jogomez@squalo.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("jogomez@squalo.com", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Squalo");
				user.setLastname("");
				user.setEmail("jogomez@squalo.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("1e98484d-2d9a-40f7-bdb5-9269387390f5",
								"472407b7-2add-4552-aa8c-1d3580ff2994", "edca05c6-9f65-4cde-a841-84e62b9108d3",
								"61c72775-af59-4c4a-b10e-ede1add1d969", "071b259f-eb59-44a6-b471-141a2b5bcfd4",
								"42e25fcc-c115-4e98-aabf-e9e83109f2bf", "e781cf2e-960a-4517-800c-305dcfab0af7",
								"c6da6b64-a3e4-4b56-aad8-214e9cdc0dbb", "3b809e40-dae1-43a0-af94-73c4ccd8c943",
								"1479331295161", "e1a08bb8-b4ea-488c-8dcf-e3c8ade1f7ed",
								"cb7a83fb-4946-407f-ae7a-c7110f975997", "600633c7-ad23-4358-a215-89355cd80871",
								"57a5eaa6-a675-472a-ab71-48975d0e204f", "1479331293219",
								"d0e0a2d7-95b2-4d6f-8b5c-f36e2a697e6c", "fb994fb0-099c-4ae3-902f-f45df7c77cbe",
								"1479331294155", "b0abfa8a-8c56-4a00-9ab8-a5ccf72cfa2f",
								"b6aa02b2-8e4d-4e94-93f6-6cae7215cff1", "9ee4d4be-0d6f-4516-804c-8ecacbb5dd30",
								"fb9ba533-212b-4b26-b9a8-73026e5059f0", "1479331293676"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "jogomez@squalo.com"));
				userDao.create(user);
			}
			
			// End Squalo  --------------------------------------------------------------------
			
			
			try {
				um = userMenuDao.get("modaholding_mx", true);
				userMenuDao.delete("modaholding_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("modaholding_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("modaholding_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Moda holding");
				user.setLastname("");
				user.setEmail("modaholding_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f0b0e659-9355-4abd-956f-65ab53f38e99",
								"7834f952-f64a-4227-9eac-8d9a073311a3", "2d3f152a-85cf-4f07-8c73-b99b2928b011",
								"2342e42f-d937-417f-bb13-0083588b7812"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "modaholding_mx"));
				userDao.create(user);
			}
			
			// Aditivo Franquicias  --------------------------------------------------------------------
			
			try {
				um = userMenuDao.get("franquiciasmichan_mx", true);
				userMenuDao.delete("franquiciasmichan_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.storerevenue", "fa-money", "Revenue"));
				um.setKey(userMenuDao.createKey("franquiciasmichan_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("franquiciasmichan_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Aditivo Franquicias Michan");
				user.setLastname("");
				user.setEmail("franquiciasmichan_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				user.getSecuritySettings()
						.setStores(Arrays.asList("aff0af5d-45b8-46b6-81ee-12c79990653b"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "franquiciasmichan_mx"));
				userDao.create(user);
			}
			
			// End Aditivo Franquicias  --------------------------------------------------------------------

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
