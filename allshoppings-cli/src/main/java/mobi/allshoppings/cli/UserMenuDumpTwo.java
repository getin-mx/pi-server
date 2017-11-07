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

public class UserMenuDumpTwo extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UserMenuDump.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}
	
	public static void main(String[] args) throws ASException {
		createMissingUsers();
	}

	public static void createMissingUsers() throws ASException {
		try {
			UserMenuDAO userMenuDao = (UserMenuDAO)getApplicationContext().getBean("usermenu.dao.ref");
			UserDAO userDao = (UserDAO)getApplicationContext().getBean("user.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");

			log.log(Level.INFO, "Dumping Getin Users....");

			UserMenu um = null;
			User user = null;
			
			// Aditivo -----------------------------------------------------------------
			try {
				um = userMenuDao.get("carlos@aditivo.mx", true);
				userMenuDao.delete("carlos@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("carlos@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("carlos@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Carlos");
				user.setLastname("");
				user.setEmail("carlos@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("ffc0b360-00d5-4e8f-8bef-f0472df6cb5f",
								"1fac0105-51f2-4a5f-ac3f-eaf4b6d311ed", "f33140b3-3ecd-4d70-bfcc-159f47ac9058",
								"349b85b9-a083-4e65-9740-f3d59278f635", "4dbf03a7-f321-4109-abd0-58780310f09c",
								"49264559-dddb-42c5-b1bf-1a52a0eb659f", "2810ac0e-480b-4374-8130-134862088a86"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "carlos@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("hector@aditivo.mx", true);
				userMenuDao.delete("hector@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("hector@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("hector@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Hector");
				user.setLastname("");
				user.setEmail("hector@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("b087d73e-ccb5-4457-8b09-e85ba72de7e7",
								"4fe543ea-610e-444b-bde3-e0cf12092ae6", "71352b76-f76e-421b-b114-72f071633b61",
								"54d1aba3-3e2c-4de4-8065-c55a50109dbc", "32543b75-32b2-4b41-9e03-f876a9e88d18",
								"f1c0b0d9-b2b4-4d63-a553-cec5653a79c3", "efb69866-6bc0-44e3-8dc2-f436dfc82773"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "hector@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("isaias@aditivo.mx", true);
				userMenuDao.delete("isaias@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("isaias@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("isaias@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Isaias");
				user.setLastname("");
				user.setEmail("isaias@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("07c6552d-c3fe-445b-aac4-e1d2c234d2ca",
								"d2112ef6-7cfa-49a4-94de-a127e45ff1c1", "1b509e3a-068e-4062-9781-d04c175db304",
								"fbbc3da9-1403-4206-8b22-e1aed2b0ec40", "ce91457a-f7dc-49d0-93ff-79259e553769",
								"61374a58-a679-4532-811a-aa3340bcc47e", "95744605-8649-4091-bdfe-5426ad0b6b3e"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "isaias@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("alberto@aditivo.mx", true);
				userMenuDao.delete("alberto@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("alberto@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("alberto@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Isaias");
				user.setLastname("");
				user.setEmail("alberto@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("fcc7aa01-7b2d-4773-ba65-acc2dd7e592c",
								"f7b002fd-5c0c-4e2f-9879-0e98bda6cd5d", "2b06d29f-204e-4a38-ac87-b77cb7d39578",
								"674626e2-4537-44ba-a6a9-58632ec9f5ce", "b251d67f-b441-42d2-b69d-6a84c036e123",
								"13b66935-ec48-4fea-acc9-9e99f025a63b", "92ec9131-dbf2-4a42-a3ef-1d68170de391"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "alberto@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("angelo@aditivo.mx", true);
				userMenuDao.delete("angelo@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("angelo@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("angelo@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Angelo");
				user.setLastname("");
				user.setEmail("angelo@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("9beaf247-e674-47a2-9d4c-c550bb1aa7cc",
								"3338e021-59c4-4482-9603-fac42d656c7b", "98abde27-4dcc-4d5b-ac16-d43eac63b94b",
								"7adbc141-fbea-4203-8f3e-db3108638c30", "78416e3d-2274-4a24-9186-3616588f6197",
								"129f18c1-c531-4488-9125-6d4e4ccf6d4d", "dd77a5a2-6523-4325-b65c-1e3e3554028d"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "angelo@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("aaron@aditivo.mx", true);
				userMenuDao.delete("aaron@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("aaron@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("aaron@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Aron");
				user.setLastname("");
				user.setEmail("aaron@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("63644d2e-f052-45cb-94f2-911331f298f2",
								"4d768d73-b9a9-44a0-bbbe-dc00a04f52ec", "5d8fa91b-b783-46ef-b615-c6aba8dec1fd",
								"f04111dd-c59d-419c-bf6f-36ebecbcbd0d", "bde9a482-df2e-410e-9a51-06a90d2294d0",
								"0221582e-c2fd-49d0-98ed-4635cd5e22db", "4d217df4-d2ad-44e6-81eb-ac10a9c040ec"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "aaron@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("joser@aditivo.mx", true);
				userMenuDao.delete("joser@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("joser@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("joser@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Jose");
				user.setLastname("");
				user.setEmail("joser@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("11a2f4a2-75e3-4ee2-be94-391e02739d28",
								"da761750-c568-4e8b-9965-ee588c3d1d9a", "77bfffb3-48a9-43b6-b1ec-51d526e96da8",
								"8cac6f24-fc71-4e4a-b556-5bfe06191f3f", "23178716-9ef5-4b57-b88e-ea85d080c0f7",
								"6ad6e636-f5ec-4d8f-a499-c85055e03f4e", "2fc001ca-b8c4-4a5c-b7e2-c732c9f98ce0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "joser@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("ignacio@aditivo.mx", true);
				userMenuDao.delete("ignacio@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ignacio@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ignacio@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Ignacio");
				user.setLastname("");
				user.setEmail("ignacio@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("b6d96f4a-d9f7-4537-87ae-c6b3f4b3c5e5",
								"9a14f70c-52eb-4756-8fb3-b48ee8b86094", "625f5d03-6726-4bb2-894e-60749397dba6",
								"2a90e8ab-fe34-4a3f-8bd4-0480ce4f40f8", "5da4f3c0-fe1f-47cb-9b7b-5fc4242240ce"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ignacio@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("miguel@aditivo.mx", true);
				userMenuDao.delete("miguel@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("miguel@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("miguel@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Miguel");
				user.setLastname("");
				user.setEmail("miguel@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("06b26796-bbda-49ad-b0b8-e24bc8cbeef6",
								"3bd9d22b-65d9-44af-805f-87a77af5f691", "3928b1d6-2fb7-4a62-a081-9e5a23e78e91",
								"4d1d6d54-0cc1-4ba5-a40f-ed61284149c9", "e41ddf46-b7fe-4d56-b52d-05a6cee7adf0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "miguel@aditivo.mx"));
				userDao.create(user);
			}
			
			try {
				um = userMenuDao.get("daniel@aditivo.mx", true);
				userMenuDao.delete("daniel@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("daniel@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("daniel@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Daniel");
				user.setLastname("");
				user.setEmail("daniel@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("E27EF9D38FBCA9628E996CBB113621E22764C88363499544FDF3E0C369FF7444");
				user.getSecuritySettings()
						.setStores(Arrays.asList("a45fc81f-900e-457e-854e-df4a312bb0e1",
								"d2eab997-2497-49b3-b1bd-15cf80f05fc7", "8d2335b7-4cc3-4f76-b274-86137b34b4e5",
								"3c9e6b60-bdcd-4268-99eb-a9c9f719f625"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "daniel@aditivo.mx"));
				userDao.create(user);
			}
			
			// End Aditivo ------------------------------------------------------------------------
			
			// Droc Users -------------------------------------------------------------------------
				// Admins
		
		User julius = null;
		try {
			julius = userDao.get("julius", true);
		} catch( Exception e ) {
			julius = new User(); 
			julius.setFirstname("Julius"); 
			julius.setLastname("Dokulil"); 
			julius.setEmail("julius@allshoppings.mobi"); 
			julius.getSecuritySettings().setRole(Role.SHOPPING); 
			julius.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			julius.getSecuritySettings().getShoppings().add("mundoe"); 
			julius.getSecuritySettings().getShoppings().add("plazaaragon");
			julius.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			julius.setKey((Key)keyHelper.obtainKey(User.class, "julius")); 
		     userDao.create(julius); 
		}
		
		try {
			um = userMenuDao.get("julius", true);
			userMenuDao.delete("julius");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("julius")); 
	        userMenuDao.create(um); 
		}
		
		User eross = null;
		try {
			eross = userDao.get("eross", true);
		} catch( Exception e ) {
			eross = new User(); 
			eross.setFirstname("Eduardo"); 
			eross.setLastname("Ross"); 
			eross.setEmail("eross@allshoppings.mobi"); 
			eross.getSecuritySettings().setRole(Role.SHOPPING); 
			eross.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			eross.getSecuritySettings().getShoppings().add("mundoe"); 
			eross.getSecuritySettings().getShoppings().add("plazaaragon");
			eross.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			eross.setKey((Key)keyHelper.obtainKey(User.class, "eross")); 
		     userDao.create(eross); 
		}
		
		try {
			um = userMenuDao.get("eross", true);
			userMenuDao.delete("eross");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("eross")); 
	        userMenuDao.create(um); 
		}
		
		
		User fdiaz = null;
		try {
			fdiaz = userDao.get("fdiaz", true);
		} catch( Exception e ) {
			fdiaz = new User(); 
			fdiaz.setFirstname("Fernando"); 
			fdiaz.setLastname("Diaz"); 
			fdiaz.setEmail("fdiaz@allshoppings.mobi"); 
			fdiaz.getSecuritySettings().setRole(Role.SHOPPING); 
			fdiaz.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			fdiaz.getSecuritySettings().getShoppings().add("mundoe"); 
			fdiaz.getSecuritySettings().getShoppings().add("plazaaragon");
			fdiaz.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			fdiaz.setKey((Key)keyHelper.obtainKey(User.class, "fdiaz")); 
		     userDao.create(fdiaz); 
		}
		
		try {
			um = userMenuDao.get("fdiaz", true);
			userMenuDao.delete("fdiaz");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("fdiaz")); 
	        userMenuDao.create(um); 
		}
		
		
			// End Andmins
		
			// Cliente
			
		User octavio = null;
		try {
			octavio = userDao.get("octavio", true);
		} catch( Exception e ) {
			octavio = new User(); 
			octavio.setFirstname("Octavio"); 
			octavio.setLastname("Perez"); 
			octavio.setEmail("octavio@allshoppings.mobi"); 
			octavio.getSecuritySettings().setRole(Role.SHOPPING); 
			octavio.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			octavio.getSecuritySettings().getShoppings().add("mundoe"); 
			octavio.getSecuritySettings().getShoppings().add("plazaaragon");
			octavio.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			octavio.setKey((Key)keyHelper.obtainKey(User.class, "octavio")); 
		     userDao.create(octavio); 
		}
		
		try {
			um = userMenuDao.get("octavio", true);
			userMenuDao.delete("octavio");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("octavio")); 
	        userMenuDao.create(um); 
		}
		
		
		User frisa = null;
		try {
			frisa = userDao.get("dir_frisa", true);
		} catch( Exception e ) {
			frisa = new User(); 
			frisa.setFirstname("Gerardo"); 
			frisa.setLastname("H"); 
			frisa.setEmail("dir_frisa@allshoppings.mobi"); 
			frisa.getSecuritySettings().setRole(Role.SHOPPING); 
			frisa.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			frisa.getSecuritySettings().getShoppings().add("mundoe"); 
			frisa.getSecuritySettings().getShoppings().add("plazaaragon");
			frisa.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			frisa.setKey((Key)keyHelper.obtainKey(User.class, "dir_frisa")); 
		     userDao.create(frisa); 
		}
		
		try {
			um = userMenuDao.get("dir_frisa", true);
			userMenuDao.delete("dir_frisa");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("dir_frisa")); 
	        userMenuDao.create(um); 
		}
		
		
		User jmiguel = null;
		try {
			jmiguel = userDao.get("jmiguel", true);
		} catch( Exception e ) {
			jmiguel = new User(); 
			jmiguel.setFirstname("Jose Miguel"); 
			jmiguel.setLastname("Cobo"); 
			jmiguel.setEmail("jmiguel@allshoppings.mobi"); 
			jmiguel.getSecuritySettings().setRole(Role.SHOPPING); 
			jmiguel.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			jmiguel.getSecuritySettings().getShoppings().add("mundoe"); 
			jmiguel.getSecuritySettings().getShoppings().add("plazaaragon");
			jmiguel.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			jmiguel.setKey((Key)keyHelper.obtainKey(User.class, "jmiguel")); 
		     userDao.create(jmiguel); 
		}
		
		try {
			um = userMenuDao.get("jmiguel", true);
			userMenuDao.delete("jmiguel");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("jmiguel")); 
	        userMenuDao.create(um); 
		}
		
			
			// End Cliente
		
			// Mundo E
			
		User gerenciaME = null;
		try {
			gerenciaME = userDao.get("gerenciaME", true);
		} catch( Exception e ) {
			gerenciaME = new User(); 
			gerenciaME.setFirstname("gerencia Mundo E"); 
			gerenciaME.setLastname(""); 
			gerenciaME.setEmail("gerenciaME@allshoppings.mobi"); 
			gerenciaME.getSecuritySettings().setRole(Role.SHOPPING); 
			gerenciaME.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			gerenciaME.getSecuritySettings().getShoppings().add("mundoe"); 
			gerenciaME.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			gerenciaME.setKey((Key)keyHelper.obtainKey(User.class, "gerenciaME")); 
		     userDao.create(gerenciaME); 
		}
		
		try {
			um = userMenuDao.get("gerenciaME", true);
			userMenuDao.delete("gerenciaME");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("gerenciaME")); 
	        userMenuDao.create(um); 
		}
		
		
		User marketing = null;
		try {
			marketing = userDao.get("marketing", true);
		} catch( Exception e ) {
			marketing = new User(); 
			marketing.setFirstname("marketing"); 
			marketing.setLastname(""); 
			marketing.setEmail("marketing@allshoppings.mobi"); 
			marketing.getSecuritySettings().setRole(Role.SHOPPING); 
			marketing.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			marketing.getSecuritySettings().getShoppings().add("mundoe"); 
			marketing.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			marketing.setKey((Key)keyHelper.obtainKey(User.class, "marketing")); 
		     userDao.create(marketing); 
		}
		
		try {
			um = userMenuDao.get("marketing", true);
			userMenuDao.delete("marketing");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("marketing")); 
	        userMenuDao.create(um); 
		}
		
		
		User alejandro = null;
		try {
			alejandro = userDao.get("alejandro", true);
		} catch( Exception e ) {
			alejandro = new User(); 
			alejandro.setFirstname("Comnet"); 
			alejandro.setLastname(""); 
			alejandro.setEmail("alejandro@allshoppings.mobi"); 
			alejandro.getSecuritySettings().setRole(Role.SHOPPING); 
			alejandro.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			alejandro.getSecuritySettings().getShoppings().add("mundoe"); 
			alejandro.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			alejandro.setKey((Key)keyHelper.obtainKey(User.class, "alejandro")); 
		     userDao.create(alejandro); 
		}
		
		try {
			um = userMenuDao.get("alejandro", true);
			userMenuDao.delete("alejandro");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("alejandro")); 
	        userMenuDao.create(um); 
		}
		
		
			// End Mundo E
		
			// Aragon
			
		User gerenciaA = null;
		try {
			gerenciaA = userDao.get("gerenciaA", true);
		} catch( Exception e ) {
			gerenciaA = new User(); 
			gerenciaA.setFirstname("Gerencia Aragon"); 
			gerenciaA.setLastname(""); 
			gerenciaA.setEmail("gerenciaA@allshoppings.mobi"); 
			gerenciaA.getSecuritySettings().setRole(Role.SHOPPING); 
			gerenciaA.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			frisa.getSecuritySettings().getShoppings().add("plazaaragon");
			gerenciaA.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			gerenciaA.setKey((Key)keyHelper.obtainKey(User.class, "gerenciaA")); 
		     userDao.create(gerenciaA); 
		}
		
		try {
			um = userMenuDao.get("gerenciaA", true);
			userMenuDao.delete("gerenciaA");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("gerenciaA")); 
	        userMenuDao.create(um); 
		}
		
		
		User cesar = null;
		try {
			cesar = userDao.get("cesar", true);
		} catch( Exception e ) {
			cesar = new User(); 
			cesar.setFirstname("Marketing"); 
			cesar.setLastname(""); 
			cesar.setEmail("cesar@allshoppings.mobi"); 
			cesar.getSecuritySettings().setRole(Role.SHOPPING); 
			cesar.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			frisa.getSecuritySettings().getShoppings().add("plazaaragon");
			cesar.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			cesar.setKey((Key)keyHelper.obtainKey(User.class, "cesar")); 
		     userDao.create(cesar); 
		}
		
		try {
			um = userMenuDao.get("cesar", true);
			userMenuDao.delete("cesar");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu(); 
	        um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC")); 
	        um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map")); 
	        um.setKey(userMenuDao.createKey("cesar")); 
	        userMenuDao.create(um); 
		}
		
		
		User demodevlyn = null;
		try {
			demodevlyn = userDao.get("demo_devlyn_mx", true);
		} catch( Exception e ) {
			demodevlyn = new User();
			demodevlyn.setFirstname("DevlynDemo");
			demodevlyn.setLastname("Mexico");
			demodevlyn.setEmail("demodevlyn@allshoppings.mobi");
			demodevlyn.getSecuritySettings().setRole(Role.BRAND);
			demodevlyn.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
			demodevlyn.setKey((Key)keyHelper.obtainKey(User.class, "demo_devlyn_mx"));
			userDao.create(demodevlyn);
		}

		
		try {
			um = userMenuDao.get("demo_devlyn_mx", true);
			userMenuDao.delete("demo_devlyn_mx");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.demodevlyn", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("demo_devlyn_mx"));
			userMenuDao.create(um);
		}
		
		//User Ariel
		//Inserting Ariel
		log.log(Level.INFO, "Inserting Ariel user...");

		User userAriel;
		
		try {
			userDao.delete("ariel@chilimbalam.com.mx");
			throw new Exception();
		} catch( Exception e ) {
			userAriel = new User();
			userAriel.setFirstname("Ariel");
			userAriel.setLastname("");
			userAriel.setEmail("ariel@chilimbalam.com.mx");
			userAriel.getSecuritySettings().setRole(Role.STORE);
			userAriel.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
			userAriel.getSecuritySettings()
			.setStores(Arrays.asList("1471039822636", "1471039822656", "1471039822677", "1503537460654", "1503537460669", 
			                          "65ceefa9-f1ef-4007-a6f2-e3dd0d8ac10b", "15457473-3aef-4c31-8cca-7f4e0c089bf1",
			                          "f36a7e58-d155-49f7-a130-39ecbae772a2", "ce3944c0-5678-4709-9555-10b71d647199",
			                          "f1b7a285-f0e8-47e6-8063-04e7d09108e3", "cfdc5dcc-a6af-47ea-9cd4-29ef8a08ada4"));
			userAriel.setKey((Key)keyHelper.obtainKey(User.class, "ariel@chilimbalam.com.mx"));
			userDao.create(userAriel);
		}

		try {
			um = userMenuDao.get("ariel@chilimbalam.com.mx", true);
			userMenuDao.delete("ariel@chilimbalam.com.mx");
			throw new Exception();
		} catch( Exception e ) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("ariel@chilimbalam.com.mx"));
			userMenuDao.create(um);
		}

		
		
			// End Aragon
		
		// End Droc Users ----------------------------------------------------------------------------

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
