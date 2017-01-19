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
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.getEntries().add(new UserMenuEntry("index.users", "fa-laptop", "Usuarios"));
				um.getEntries().add(new UserMenuEntry("index.applications", "fa-laptop", "Aplicaciones"));
				um.getEntries().add(new UserMenuEntry("index.brands", "fa-laptop", "Cadenas"));
				um.getEntries().add(new UserMenuEntry("index.shoppings", "fa-laptop", "Centros Comerciales"));
				um.setKey(userMenuDao.createKey("admin"));
				userMenuDao.create(um);
			}

			User alberto = null;
			try {
				alberto = userDao.get("alberto@getin.mx", true);
			} catch( Exception e ) {
				alberto = new User();
				alberto.setFirstname("Alberto");
				alberto.setLastname("");
				alberto.setEmail("alberto@getin.mx");
				alberto.getSecuritySettings().setRole(Role.ADMIN);
				alberto.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				alberto.setKey((Key)keyHelper.obtainKey(User.class, "alberto@getin.mx"));
				userDao.create(alberto);
			}

			try {
				um = userMenuDao.get("alberto@getin.mx", true);
				userMenuDao.delete("alberto@getin.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-laptop", "Antenas"));
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.setKey(userMenuDao.createKey("alberto@getin.mx"));
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				um.setKey(userMenuDao.createKey("matias@getin.mx"));
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
				modatelas.getSecuritySettings().setRole(Role.BRAND);
				modatelas.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				modatelas.setKey((Key)keyHelper.obtainKey(User.class, "modatelas_mx"));
				userDao.create(modatelas);
			}

			try {
				um = userMenuDao.get("modatelas_mx", true);
				userMenuDao.delete("modatelas_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.mtheatmap", "fa-building", "Heat Map"));
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
			} catch( Exception e ) {
				sbarro = new User();
				sbarro.setFirstname("Sbarro");
				sbarro.setLastname("Mexico");
				sbarro.setEmail("sbarro@allshoppings.mobi");
				sbarro.getSecuritySettings().setRole(Role.BRAND);
				sbarro.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				sbarro.setKey((Key)keyHelper.obtainKey(User.class, "sbarro_mx"));
				userDao.create(sbarro);
			}

			try {
				um = userMenuDao.get("sbarro_mx", true);
				userMenuDao.delete("sbarro_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("sbarro_mx"));
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
				um.getEntries().add(new UserMenuEntry("index.demoheatmap", "fa-map-marker", "Heatmap"));
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
			
			User demo4 = null;
			try {
				demo4 = userDao.get("demo4_mx", true);
				userMenuDao.delete("demo4_mx");
				throw new Exception();
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
				um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
				um.setKey(userMenuDao.createKey("droc_mx"));
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
				blulagoon = new User();
				blulagoon.setFirstname("Blu Lagoon");
				blulagoon.setLastname("Mexico");
				blulagoon.setEmail("blulagoon@allshoppings.mobi");
				blulagoon.getSecuritySettings().setRole(Role.BRAND);
				blulagoon.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
				blulagoon.setKey((Key)keyHelper.obtainKey(User.class, "blulagoon_mx"));
				userDao.create(blulagoon);
			}

			try {
				um = userMenuDao.get("blulagoon_mx", true);
				userMenuDao.delete("blulagoon_mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("blulagoon_mx"));
				userMenuDao.create(um);
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
				um.setKey(userMenuDao.createKey("roku_mx"));
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
								"95d98d90-ba0b-42e1-843b-4a0e5c09db4b", "62c734bd-15fa-4bc5-a542-d38dd30e4546"));
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
						.setStores(Arrays.asList("81b52571-3bd3-44e3-bed9-9e592d568f04",
								"06a99c76-c4ca-4952-a99f-3576c7e4dce0"));
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
								"95d98d90-ba0b-42e1-843b-4a0e5c09db4b", "62c734bd-15fa-4bc5-a542-d38dd30e4546"));
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
			// Sportium --------------------------------------------------------------------
			
			//Lomas Verdes
			try {
				um = userMenuDao.get("lomasverdes@sportium", true);
				userMenuDao.delete("lomasverdes@sportium");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
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
			
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
