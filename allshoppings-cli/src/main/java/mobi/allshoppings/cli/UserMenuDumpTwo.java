package mobi.allshoppings.cli;

import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

private static final char HEXES[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	private static String encodeString(String input) {
		 String output = "";
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte keyBytes[] = "MSIR33L264H1VVXSINHR".getBytes(); 
			SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
			mac.init(key);
			mac.update(input.getBytes());
			byte macBytes[] = mac.doFinal();

			StringBuilder hexString = new StringBuilder(macBytes.length * 2);
			for (byte b : macBytes) {
				hexString.append(HEXES[((b & 0xF0) >> 4)]).append(HEXES[(b & 0x0F)]);
			}
			output = hexString.toString();
		} catch (Exception e) {
		}
		return output;
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
			julius.getSecuritySettings().setRole(Role.BRAND); 
			julius.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			julius.getSecuritySettings().getShoppings().add("mundoe"); 
			julius.getSecuritySettings().getShoppings().add("plazaaragon");
			julius.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			julius.setKey((Key)keyHelper.obtainKey(User.class, "julius")); 
		     userDao.create(julius); 
		}
		
		User eross = null;
		try {
			eross = userDao.get("eross", true);
		} catch( Exception e ) {
			eross = new User(); 
			eross.setFirstname("Eduardo"); 
			eross.setLastname("Ross"); 
			eross.setEmail("eross@allshoppings.mobi"); 
			eross.getSecuritySettings().setRole(Role.BRAND); 
			eross.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			eross.getSecuritySettings().getShoppings().add("mundoe"); 
			eross.getSecuritySettings().getShoppings().add("plazaaragon");
			eross.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			eross.setKey((Key)keyHelper.obtainKey(User.class, "eross")); 
		     userDao.create(eross); 
		}
		
		User fdiaz = null;
		try {
			fdiaz = userDao.get("fdiaz", true);
		} catch( Exception e ) {
			fdiaz = new User(); 
			fdiaz.setFirstname("Fernando"); 
			fdiaz.setLastname("Diaz"); 
			fdiaz.setEmail("fdiaz@allshoppings.mobi"); 
			fdiaz.getSecuritySettings().setRole(Role.BRAND); 
			fdiaz.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			fdiaz.getSecuritySettings().getShoppings().add("mundoe"); 
			fdiaz.getSecuritySettings().getShoppings().add("plazaaragon");
			fdiaz.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			fdiaz.setKey((Key)keyHelper.obtainKey(User.class, "fdiaz")); 
		     userDao.create(fdiaz); 
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
			octavio.getSecuritySettings().setRole(Role.BRAND); 
			octavio.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			octavio.getSecuritySettings().getShoppings().add("mundoe"); 
			octavio.getSecuritySettings().getShoppings().add("plazaaragon");
			octavio.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			octavio.setKey((Key)keyHelper.obtainKey(User.class, "octavio")); 
		     userDao.create(octavio); 
		}
		
		User frisa = null;
		try {
			frisa = userDao.get("dir_frisa", true);
		} catch( Exception e ) {
			frisa = new User(); 
			frisa.setFirstname("Gerardo"); 
			frisa.setLastname("H"); 
			frisa.setEmail("dir_frisa@allshoppings.mobi"); 
			frisa.getSecuritySettings().setRole(Role.BRAND); 
			frisa.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			frisa.getSecuritySettings().getShoppings().add("mundoe"); 
			frisa.getSecuritySettings().getShoppings().add("plazaaragon");
			frisa.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			frisa.setKey((Key)keyHelper.obtainKey(User.class, "dir_frisa")); 
		     userDao.create(frisa); 
		}
		
		User jmiguel = null;
		try {
			jmiguel = userDao.get("jmiguel", true);
		} catch( Exception e ) {
			jmiguel = new User(); 
			jmiguel.setFirstname("Jose Miguel"); 
			jmiguel.setLastname("Cobo"); 
			jmiguel.setEmail("jmiguel@allshoppings.mobi"); 
			jmiguel.getSecuritySettings().setRole(Role.BRAND); 
			jmiguel.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			jmiguel.getSecuritySettings().getShoppings().add("mundoe"); 
			jmiguel.getSecuritySettings().getShoppings().add("plazaaragon");
			jmiguel.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			jmiguel.setKey((Key)keyHelper.obtainKey(User.class, "jmiguel")); 
		     userDao.create(jmiguel); 
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
			gerenciaME.getSecuritySettings().setRole(Role.BRAND); 
			gerenciaME.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			gerenciaME.getSecuritySettings().getShoppings().add("mundoe"); 
			gerenciaME.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			gerenciaME.setKey((Key)keyHelper.obtainKey(User.class, "gerenciaME")); 
		     userDao.create(gerenciaME); 
		}
		
		User marketing = null;
		try {
			marketing = userDao.get("marketing", true);
		} catch( Exception e ) {
			marketing = new User(); 
			marketing.setFirstname("marketing"); 
			marketing.setLastname(""); 
			marketing.setEmail("marketing@allshoppings.mobi"); 
			marketing.getSecuritySettings().setRole(Role.BRAND); 
			marketing.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			marketing.getSecuritySettings().getShoppings().add("mundoe"); 
			marketing.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			marketing.setKey((Key)keyHelper.obtainKey(User.class, "marketing")); 
		     userDao.create(marketing); 
		}
		
		User alejandro = null;
		try {
			alejandro = userDao.get("alejandro", true);
		} catch( Exception e ) {
			alejandro = new User(); 
			alejandro.setFirstname("Comnet"); 
			alejandro.setLastname(""); 
			alejandro.setEmail("alejandro@allshoppings.mobi"); 
			alejandro.getSecuritySettings().setRole(Role.BRAND); 
			alejandro.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			alejandro.getSecuritySettings().getShoppings().add("mundoe"); 
			alejandro.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			alejandro.setKey((Key)keyHelper.obtainKey(User.class, "alejandro")); 
		     userDao.create(alejandro); 
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
			gerenciaA.getSecuritySettings().setRole(Role.BRAND); 
			gerenciaA.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			frisa.getSecuritySettings().getShoppings().add("plazaaragon");
			gerenciaA.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			gerenciaA.setKey((Key)keyHelper.obtainKey(User.class, "gerenciaA")); 
		     userDao.create(gerenciaA); 
		}
		
		User cesar = null;
		try {
			cesar = userDao.get("cesar", true);
		} catch( Exception e ) {
			cesar = new User(); 
			cesar.setFirstname("Marketing"); 
			cesar.setLastname(""); 
			cesar.setEmail("cesar@allshoppings.mobi"); 
			cesar.getSecuritySettings().setRole(Role.BRAND); 
			cesar.getSecuritySettings().setShoppings(new ArrayList<String>()); 
			frisa.getSecuritySettings().getShoppings().add("plazaaragon");
			cesar.getSecuritySettings().setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA"); 
			cesar.setKey((Key)keyHelper.obtainKey(User.class, "cesar")); 
		     userDao.create(cesar); 
		}
		
			// End Aragon
		
		try {
			um = userMenuDao.get("cesar", true);
			userMenuDao.delete("cesar");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.shoppingvisits", "fa-area-chart", "Tráfico en CC"));
			um.getEntries().add(new UserMenuEntry("index.heatmap", "fa-building", "Heat Map"));
			um.setKey(userMenuDao.createKey("cesar"));
			userMenuDao.create(um);
		}

		User demodevlyn = null;
		try {
			demodevlyn = userDao.get("demo_devlyn_mx", true);
		} catch (Exception e) {
			demodevlyn = new User();
			demodevlyn.setFirstname("DevlynDemo");
			demodevlyn.setLastname("Mexico");
			demodevlyn.setEmail("demodevlyn@allshoppings.mobi");
			demodevlyn.getSecuritySettings().setRole(Role.BRAND);
			demodevlyn.getSecuritySettings()
					.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
			demodevlyn.setKey((Key) keyHelper.obtainKey(User.class, "demo_devlyn_mx"));
			userDao.create(demodevlyn);
		}

		try {
			um = userMenuDao.get("demo_devlyn_mx", true);
			userMenuDao.delete("demo_devlyn_mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.demodevlyn", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("demo_devlyn_mx"));
			userMenuDao.create(um);
		}

		// User Ariel
		// Inserting Ariel
		log.log(Level.INFO, "Inserting Chilimbalam users...");

		User userAriel;

		try {
			userDao.delete("ariel@chilimbalam.com.mx");
			throw new Exception();
		} catch (Exception e) {
			userAriel = new User();
			userAriel.setFirstname("Ariel");
			userAriel.setLastname("");
			userAriel.setEmail("ariel@chilimbalam.com.mx");
			userAriel.getSecuritySettings().setRole(Role.STORE);
			userAriel.getSecuritySettings()
					.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
			userAriel.getSecuritySettings()
					.setStores(Arrays.asList("1471039822636", "1471039822656", "1471039822677", "1503537460654",
							"1503537460669", "65ceefa9-f1ef-4007-a6f2-e3dd0d8ac10b",
							"15457473-3aef-4c31-8cca-7f4e0c089bf1", "f36a7e58-d155-49f7-a130-39ecbae772a2",
							"ce3944c0-5678-4709-9555-10b71d647199", "f1b7a285-f0e8-47e6-8063-04e7d09108e3",
							"cfdc5dcc-a6af-47ea-9cd4-29ef8a08ada4"));
			userAriel.setKey((Key) keyHelper.obtainKey(User.class, "ariel@chilimbalam.com.mx"));
			userDao.create(userAriel);
		}

		try {
			um = userMenuDao.get("ariel@chilimbalam.com.mx", true);
			userMenuDao.delete("ariel@chilimbalam.com.mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("ariel@chilimbalam.com.mx"));
			userMenuDao.create(um);
		}

		// hmorales
		User HMorales = null;
		try {
			userDao.delete("hmorales@chilimbalam.com.mx");
			throw new Exception();
		} catch (Exception e) {
			HMorales = new User();
			HMorales.setFirstname("H");
			HMorales.setLastname("Morales");
			HMorales.setEmail("hmorales@chilimbalam.com.mx");
			HMorales.getSecuritySettings().setRole(Role.STORE);
			HMorales.getSecuritySettings()
					.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
			HMorales.getSecuritySettings()
					.setStores(Arrays.asList("1471039822636", "1471039822656", "1471039822677", "1503537460654",
							"1503537460669", "65ceefa9-f1ef-4007-a6f2-e3dd0d8ac10b",
							"15457473-3aef-4c31-8cca-7f4e0c089bf1", "f36a7e58-d155-49f7-a130-39ecbae772a2",
							"ce3944c0-5678-4709-9555-10b71d647199", "f1b7a285-f0e8-47e6-8063-04e7d09108e3",
							"cfdc5dcc-a6af-47ea-9cd4-29ef8a08ada4"));
			HMorales.setKey((Key) keyHelper.obtainKey(User.class, "hmorales@chilimbalam.com.mx"));
			userDao.create(HMorales);
		}

		try {
			// um = userMenuDao.get("hmorales@chilimbalam.com.mx", true);
			userMenuDao.delete("hmorales@chilimbalam.com.mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("hmorales@chilimbalam.com.mx"));
			userMenuDao.create(um);
		}

		// arocha
		User Arocha = null;
		try {
			userDao.delete("arocha@chilimbalam.com.mx");
			throw new Exception();
		} catch (Exception e) {
			Arocha = new User();
			Arocha.setFirstname("A");
			Arocha.setLastname("Rocha");
			Arocha.setEmail("arocha@chilimbalam.com.mx");
			Arocha.getSecuritySettings().setRole(Role.STORE);
			Arocha.getSecuritySettings()
					.setPassword("279FE88523A2435CBDD676FEB2F134F45F5F43E179CFEEAFEDB72F2750AC29EA");
			Arocha.getSecuritySettings()
					.setStores(Arrays.asList("1471039822636", "1471039822656", "1471039822677", "1503537460654",
							"1503537460669", "65ceefa9-f1ef-4007-a6f2-e3dd0d8ac10b",
							"15457473-3aef-4c31-8cca-7f4e0c089bf1", "f36a7e58-d155-49f7-a130-39ecbae772a2",
							"ce3944c0-5678-4709-9555-10b71d647199", "f1b7a285-f0e8-47e6-8063-04e7d09108e3",
							"cfdc5dcc-a6af-47ea-9cd4-29ef8a08ada4"));
			Arocha.setKey((Key) keyHelper.obtainKey(User.class, "arocha@chilimbalam.com.mx"));
			userDao.create(Arocha);
		}

		try {
			um = userMenuDao.get("arocha@chilimbalam.com.mx", true);
			userMenuDao.delete("arocha@chilimbalam.com.mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("arocha@chilimbalam.com.mx"));
			userMenuDao.create(um);
		}

	
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

		user = null;
		try {
			userDao.delete("lamartinez@pradastores.mx");
			throw new Exception();
		} catch (Exception e) {
			log.log(Level.INFO, "Inserting Luis arturo Prada...");
			user = new User();
			user.setFirstname("Luis Arturo Martínez");
			user.setLastname("");
			user.setEmail("lamartinez@pradastores.mx");
			user.getSecuritySettings().setRole(Role.STORE);
			user.getSecuritySettings()
					.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
			user.getSecuritySettings()
					.setStores(Arrays.asList("1479933115741", "2179a275-e43a-42ac-b6e7-6eedf3f4ee0a",
							"a062c268-877d-47e4-80aa-894da6ec93cc", "82979029-3ca9-4bc3-aaaf-d9ccdfca562c",
							"f4fea5b7-475c-483f-896d-64422319382d", "1479926604340", "6b51b0f6-4e52-41b0-b59d-cec9e89c042b",
							"3bc93117-4e24-4298-bcbe-7d5de3c38efb", "5982c91b-1465-446b-afb2-53a5e7372b72",
							"ce7bdec9-4de3-4d1c-8fe2-80b02f88f083", "9ce30430-d923-481f-9b32-caa5139972fe"));
			user.setKey((Key) keyHelper.obtainKey(User.class, "lamartinez@pradastores.mx"));
			userDao.create(user);
		}
		
		
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

		User userClaudia = null;
		try {
			userDao.delete("crua@pradastores.mx");
			throw new Exception();
		} catch (Exception e) {
			log.log(Level.INFO, "Inserting Claudia Rúa...");
			userClaudia = new User();
			userClaudia.setFirstname("Claudia Rúa");
			userClaudia.setLastname("");
			userClaudia.setEmail("crua@pradastores.mx");
			userClaudia.getSecuritySettings().setRole(Role.STORE);
			userClaudia.getSecuritySettings()
					.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
			userClaudia.getSecuritySettings()
					.setStores(Arrays.asList("61534aa2-ec08-471e-9378-eff26344edec",
							"4f9302f7-c155-46c7-b780-17834270a7f7", "f94a2759-554c-49a4-a32d-84c81cfe98cc",
							"b072f5cf-2f5e-4a28-9be4-5525b5c6f83c", "a604b9c3-949a-4581-9196-5b17e1de989a",
							"4523b563-2e0d-4fe2-8321-9e53740854b2", "4db491d4-3205-40ae-b048-f5b75ac35040", 
							"bd39ba69-eb84-4679-b3e2-0f9276eb76b7", "3acd7c49-a32d-4b45-9064-d7360c11b6ed",
							"4568bab3-27eb-4d49-84cd-3fa594acd3df", "1479926604326"));
			userClaudia.setKey((Key) keyHelper.obtainKey(User.class, "crua@pradastores.mx"));
			userDao.create(userClaudia);
		}
		
		
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

		User userJCruz = null;
		try {
			userDao.delete("jcruz@pradastores.mx");
			throw new Exception();
		} catch (Exception e) {
			log.log(Level.INFO, "Inserting José Luis Cruz...");
			userJCruz = new User();
			userJCruz.setFirstname("José Luis Cruz");
			userJCruz.setLastname("");
			userJCruz.setEmail("jcruz@pradastores.mx");
			userJCruz.getSecuritySettings().setRole(Role.STORE);
			userJCruz.getSecuritySettings()
					.setPassword("D461CFE028CE59C64C3B3CB7876FA4F92A7CB9540A65D750FED44321C8BA2F4E");
			userJCruz.getSecuritySettings()
					.setStores(Arrays.asList("e53ead22-8663-4e09-b0e7-069e91c1fae9",
							"e3d005b1-9162-49c1-855c-cc0eaf19b8b7", "f8610b0e-158d-4f38-9c8a-dd2bf7a3f3a1",
							"d9ea5f0b-2ac8-48a0-87e1-0b2e02d74cef", "d662d502-4d45-40e1-a160-a8d1639d57c3",
							"74bd6202-8be7-4ec3-9ebd-5141f3ca49bd", "afb95e1f-1774-46fe-b777-16488c2bcd65",
							"8b3c8cd2-0727-48f8-bc61-ab47d507969c", "7cfdde1e-b869-4ee9-b787-3c5a3f297bb1",
							"2e029627-eb93-4646-999e-ff7894b45cd0", "49272db7-dee7-4230-bbfb-d9bdcc296f59"));
			userJCruz.setKey((Key) keyHelper.obtainKey(User.class, "jcruz@pradastores.mx"));
			userDao.create(userJCruz);
		}
		
		try {
			um = userMenuDao.get("moisesgaray@outletdeportes_mx", true);
			userMenuDao.delete("moisesgaray@outletdeportes_mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("moisesgaray@outletdeportes_mx"));
			userMenuDao.create(um);
		}
		
		
		User userMoises = null;
		try {
			userDao.delete("moisesgaray@outletdeportes_mx");
			throw new Exception();
		} catch (Exception e) {
			log.log(Level.INFO, "Inserting Moises...");
			userMoises = new User();
			userMoises.setFirstname("Moises Garay");
			userMoises.setLastname("");
			userMoises.setEmail("moisesgaray@outletdeportes_mx");
			userMoises.getSecuritySettings().setRole(Role.STORE);
			userMoises.getSecuritySettings()
					.setPassword(encodeString("Outletdeportes01"));
			userMoises.getSecuritySettings()
					.setStores(Arrays.asList("41e68dc9-254d-4803-82b7-c083eeaf28df", "9263926c-88e3-435b-ad7e-1920abfb73a6",
							"649c6de9-05aa-40de-bd39-7b1d37921658", "b6b1a93f-0116-4d9a-ad40-9c6842eaa8c0"));
			userMoises.setKey((Key) keyHelper.obtainKey(User.class, "moisesgaray@outletdeportes_mx"));
			userDao.create(userMoises);
		}
		
		try {
			um = userMenuDao.get("joseivan@outletdeportes_mx", true);
			userMenuDao.delete("joseivan@outletdeportes_mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("joseivan@outletdeportes_mx"));
			userMenuDao.create(um);
		}
		

		User userJoseIvan = null;
		try {
			userDao.delete("joseivan@outletdeportes_mx");
			throw new Exception();
		} catch (Exception e) {
			log.log(Level.INFO, "Inserting Jose Ivan...");
			userJoseIvan = new User();
			userJoseIvan.setFirstname("José Ivan");
			userJoseIvan.setLastname("");
			userJoseIvan.setEmail("joseivan@outletdeportes_mx");
			userJoseIvan.getSecuritySettings().setRole(Role.STORE);
			userJoseIvan.getSecuritySettings()
					.setPassword(encodeString("Outletdeportes01"));
			userJoseIvan.getSecuritySettings()
					.setStores(Arrays.asList("23b16093-fbbf-4d9a-811b-6c82bd0eb940", "0804ce51-a635-40be-8952-28cc25c946dd",
							"247b4ead-822f-4713-9465-177666b2e31c", "3a361ad5-9748-4bfa-9a69-460fd8214e6e",
							"e43a9f92-7db7-46d1-8a74-3f629eea2b47", "dcb748f6-a060-43da-9a13-aea9ca02245a"));
			userJoseIvan.setKey((Key) keyHelper.obtainKey(User.class, "joseivan@outletdeportes_mx"));
			userDao.create(userJoseIvan);
		}
		
		
		
		try {
			um = userMenuDao.get("lcepeda@sportium.com.mx", true);
			userMenuDao.delete("lcepeda@sportium.com.mx");
			throw new Exception();
		} catch (Exception e) {
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
			um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
			um.setKey(userMenuDao.createKey("lcepeda@sportium.com.mx"));
			userMenuDao.create(um);
		}
		

		User userLCepeda = null;
		try {
			userDao.delete("lcepeda@sportium.com.mx");
			throw new Exception();
		} catch (Exception e) {
			log.log(Level.INFO, "Inserting LCepeda...");
			userLCepeda = new User();
			userLCepeda.setFirstname("Luis Cepeda");
			userLCepeda.setLastname("");
			userLCepeda.setEmail("lcepeda@sportium.com.mx");
			userLCepeda.getSecuritySettings().setRole(Role.STORE);
			userLCepeda.getSecuritySettings()
					.setPassword(encodeString("Sportium01"));
			userLCepeda.getSecuritySettings()
					.setStores(Arrays.asList("8cd52856-7e34-4f19-8c45-e25e325d4ff9"));
			userLCepeda.setKey((Key) keyHelper.obtainKey(User.class, "lcepeda@sportium.com.mx"));
			userDao.create(userLCepeda);
		}
		
		//jcardenas
		try {
			  um = userMenuDao.get("jcardenas@sportium.com.mx", true);
			  userMenuDao.delete("jcardenas@sportium.com.mx");
			  throw new Exception();
			} catch (Exception e) {
			  um = new UserMenu();
			  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
			  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
			  um.setKey(userMenuDao.createKey("jcardenas@sportium.com.mx"));
			  userMenuDao.create(um);
			}


			User userJCardenas = null;
			try {
			  userDao.delete("jcardenas@sportium.com.mx");
			  throw new Exception();
			} catch (Exception e) {
			  log.log(Level.INFO, "Inserting jcardenas...");
			  userJCardenas = new User();
			  userJCardenas.setFirstname("Juan Carlos Cardenas");
			  userJCardenas.setLastname("");
			  userJCardenas.setEmail("jcardenas@sportium.com.mx");
			  userJCardenas.getSecuritySettings().setRole(Role.STORE);
			  userJCardenas.getSecuritySettings()
			      .setPassword(encodeString("Sportium01"));
			  userJCardenas.getSecuritySettings()
			      .setStores(Arrays.asList("8cd52856-7e34-4f19-8c45-e25e325d4ff9"));
			  userJCardenas.setKey((Key) keyHelper.obtainKey(User.class, "jcardenas@sportium.com.mx"));
			  userDao.create(userJCardenas);
			}
			
			//vvargas
			
			try {
				  um = userMenuDao.get("vvargas@sportium.com.mx", true);
				  userMenuDao.delete("vvargas@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("vvargas@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userVVargas = null;
				try {
				  userDao.delete("vvargas@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting vvargas...");
				  userVVargas = new User();
				  userVVargas.setFirstname("Victor Vargas");
				  userVVargas.setLastname("");
				  userVVargas.setEmail("vvargas@sportium.com.mx");
				  userVVargas.getSecuritySettings().setRole(Role.STORE);
				  userVVargas.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userVVargas.getSecuritySettings()
				      .setStores(Arrays.asList("67af6e6e-9f11-4948-9887-65679bfd3d69"));
				  userVVargas.setKey((Key) keyHelper.obtainKey(User.class, "vvargas@sportium.com.mx"));
				  userDao.create(userVVargas);
				}
				
			//oesquivel
			try {
				  um = userMenuDao.get("oesquivel@sportium.com.mx", true);
				  userMenuDao.delete("oesquivel@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("oesquivel@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userOEsquivel = null;
				try {
				  userDao.delete("oesquivel@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting oesquivel...");
				  userOEsquivel = new User();
				  userOEsquivel.setFirstname("Oscar Esquivel");
				  userOEsquivel.setLastname("");
				  userOEsquivel.setEmail("oesquivel@sportium.com.mx");
				  userOEsquivel.getSecuritySettings().setRole(Role.STORE);
				  userOEsquivel.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userOEsquivel.getSecuritySettings()
				      .setStores(Arrays.asList("970b5795-ad0a-49ac-a7eb-110d826c7b8f"));
				  userOEsquivel.setKey((Key) keyHelper.obtainKey(User.class, "oesquivel@sportium.com.mx"));
				  userDao.create(userOEsquivel);
				}
			//oroa
			try {
				  um = userMenuDao.get("oroa@sportium.com.mx", true);
				  userMenuDao.delete("oroa@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("oroa@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userORoa = null;
				try {
				  userDao.delete("oroa@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting oroa...");
				  userORoa = new User();
				  userORoa.setFirstname("Oscar Roa");
				  userORoa.setLastname("");
				  userORoa.setEmail("oroa@sportium.com.mx");
				  userORoa.getSecuritySettings().setRole(Role.STORE);
				  userORoa.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userORoa.getSecuritySettings()
				      .setStores(Arrays.asList("c726776f-0a96-43d1-ae97-4169e595e5c6"));
				  userORoa.setKey((Key) keyHelper.obtainKey(User.class, "oroa@sportium.com.mx"));
				  userDao.create(userORoa);
				}
					
			//ucarrillo
			try {
				  um = userMenuDao.get("ucarrillo@sportium.com.mx", true);
				  userMenuDao.delete("ucarrillo@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("ucarrillo@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userUCarrillo = null;
				try {
				  userDao.delete("ucarrillo@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting ucarrillo...");
				  userUCarrillo = new User();
				  userUCarrillo.setFirstname("Ulises Carrillo");
				  userUCarrillo.setLastname("");
				  userUCarrillo.setEmail("ucarrillo@sportium.com.mx");
				  userUCarrillo.getSecuritySettings().setRole(Role.STORE);
				  userUCarrillo.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userUCarrillo.getSecuritySettings()
				      .setStores(Arrays.asList("109ec028-6749-4332-9427-a39ccbfe7244"));
				  userUCarrillo.setKey((Key) keyHelper.obtainKey(User.class, "ucarrillo@sportium.com.mx"));
				  userDao.create(userUCarrillo);
				}
			//gesquivel
			try {
				  um = userMenuDao.get("gesquivel@sportium.com.mx", true);
				  userMenuDao.delete("gesquivel@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("gesquivel@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userGEsquivel = null;
				try {
				  userDao.delete("gesquivel@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting gesquivel...");
				  userGEsquivel = new User();
				  userGEsquivel.setFirstname("Ulises Gabriel Esquivel");
				  userGEsquivel.setLastname("");
				  userGEsquivel.setEmail("gesquivel@sportium.com.mx");
				  userGEsquivel.getSecuritySettings().setRole(Role.STORE);
				  userGEsquivel.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userGEsquivel.getSecuritySettings()
				      .setStores(Arrays.asList("1471039822614"));
				  userGEsquivel.setKey((Key) keyHelper.obtainKey(User.class, "gesquivel@sportium.com.mx"));
				  userDao.create(userGEsquivel);
				}
			//clopez
			try {
				  um = userMenuDao.get("clopez@sportium.com.mx", true);
				  userMenuDao.delete("clopez@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("clopez@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userCLopez = null;
				try {
				  userDao.delete("clopez@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting clopez...");
				  userCLopez = new User();
				  userCLopez.setFirstname("César López");
				  userCLopez.setLastname("");
				  userCLopez.setEmail("clopez@sportium.com.mx");
				  userCLopez.getSecuritySettings().setRole(Role.STORE);
				  userCLopez.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userCLopez.getSecuritySettings()
				      .setStores(Arrays.asList("1471039822614"));
				  userCLopez.setKey((Key) keyHelper.obtainKey(User.class, "clopez@sportium.com.mx"));
				  userDao.create(userCLopez);
				}
			//zaguilar
			try {
				  um = userMenuDao.get("zaguilar@sportium.com.mx", true);
				  userMenuDao.delete("zaguilar@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("zaguilar@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userZAguilar = null;
				try {
				  userDao.delete("zaguilar@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting zaguilar...");
				  userZAguilar = new User();
				  userZAguilar.setFirstname("Z Aguilar");
				  userZAguilar.setLastname("");
				  userZAguilar.setEmail("zaguilar@sportium.com.mx");
				  userZAguilar.getSecuritySettings().setRole(Role.STORE);
				  userZAguilar.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userZAguilar.getSecuritySettings()
				      .setStores(Arrays.asList("1471039822461"));
				  userZAguilar.setKey((Key) keyHelper.obtainKey(User.class, "zaguilar@sportium.com.mx"));
				  userDao.create(userZAguilar);
				}
			//pcervantes
			try {
				  um = userMenuDao.get("pcervantes@sportium.com.mx", true);
				  userMenuDao.delete("pcervantes@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.getEntries().add(new UserMenuEntry("index.influencemap", "fa-map-marker", "Mapa de Influencia"));
				  um.getEntries().add(new UserMenuEntry("index.trafficmap", "fa-car", "Tráfico Vehicular"));
				  um.setKey(userMenuDao.createKey("pcervantes@sportium.com.mx"));
				  userMenuDao.create(um);
				}


				User userPCervantes = null;
				try {
				  userDao.delete("pcervantes@sportium.com.mx");
				  throw new Exception();
				} catch (Exception e) {
				  log.log(Level.INFO, "Inserting pcervantes...");
				  userPCervantes = new User();
				  userPCervantes.setFirstname("Patricia Cervantes");
				  userPCervantes.setLastname("");
				  userPCervantes.setEmail("pcervantes@sportium.com.mx");
				  userPCervantes.getSecuritySettings().setRole(Role.STORE);
				  userPCervantes.getSecuritySettings()
				      .setPassword(encodeString("Sportium01"));
				  userPCervantes.getSecuritySettings()
				      .setStores(Arrays.asList("1471039822461"));
				  userPCervantes.setKey((Key) keyHelper.obtainKey(User.class, "pcervantes@sportium.com.mx"));
				  userDao.create(userPCervantes);
			}
		
		// End Droc Users ----------------------------------------------------------------------------

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
