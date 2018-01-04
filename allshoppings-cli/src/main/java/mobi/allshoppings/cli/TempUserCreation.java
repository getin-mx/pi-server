package mobi.allshoppings.cli;

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

public class TempUserCreation extends AbstractCLI {

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
		if (base == null)
			parser = new OptionParser();
		else
			parser = base;
		return parser;
	}

	public static void main(String[] args) throws ASException {
		createMissingUsers();
	}

	public static void createMissingUsers() throws ASException {
		try {
			UserMenuDAO userMenuDao = (UserMenuDAO) getApplicationContext().getBean("usermenu.dao.ref");
			UserDAO userDao = (UserDAO) getApplicationContext().getBean("user.dao.ref");
			KeyHelper keyHelper = (KeyHelper) getApplicationContext().getBean("key.helper");
			

			  User juanUser = null;
			  try {
			    userDao.delete("juan@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting juan@aditivo.mx...");
			    juanUser = new User();
			    juanUser.setFirstname("Juan");
			    juanUser.setLastname("");
			    juanUser.setEmail("juan@aditivo.mx");
			    juanUser.getSecuritySettings().setRole(Role.STORE);
			    juanUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    juanUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "92ec9131-dbf2-4a42-a3ef-1d68170de391",
			        "2810ac0e-480b-4374-8130-134862088a86",
			        "32543b75-32b2-4b41-9e03-f876a9e88d18",
			        "98abde27-4dcc-4d5b-ac16-d43eac63b94b",
			        "2b06d29f-204e-4a38-ac87-b77cb7d39578",
			        "49264559-dddb-42c5-b1bf-1a52a0eb659f",
			        "71352b76-f76e-421b-b114-72f071633b61",
			        "129f18c1-c531-4488-9125-6d4e4ccf6d4d",
			        "3338e021-59c4-4482-9603-fac42d656c7b",
			        "4dbf03a7-f321-4109-abd0-58780310f09c",
			        "1fac0105-51f2-4a5f-ac3f-eaf4b6d311ed",
			        "54d1aba3-3e2c-4de4-8065-c55a50109dbc",
			        "78416e3d-2274-4a24-9186-3616588f6197",
			        "13b66935-ec48-4fea-acc9-9e99f025a63b",
			        "4fe543ea-610e-444b-bde3-e0cf12092ae6",
			        "349b85b9-a083-4e65-9740-f3d59278f635",
			        "674626e2-4537-44ba-a6a9-58632ec9f5ce",
			        "95744605-8649-4091-bdfe-5426ad0b6b3e",
			        "9beaf247-e674-47a2-9d4c-c550bb1aa7cc",
			        "b251d67f-b441-42d2-b69d-6a84c036e123",
			        "b087d73e-ccb5-4457-8b09-e85ba72de7e7",
			        "efb69866-6bc0-44e3-8dc2-f436dfc82773",
			        "f1c0b0d9-b2b4-4d63-a553-cec5653a79c3",
			        "f7b002fd-5c0c-4e2f-9879-0e98bda6cd5d",
			        "dd77a5a2-6523-4325-b65c-1e3e3554028d",
			        "ffc0b360-00d5-4e8f-8bef-f0472df6cb5f",
			        "fcc7aa01-7b2d-4773-ba65-acc2dd7e592c"
			        ));
			    juanUser.setKey((Key) keyHelper.obtainKey(User.class, "juan@aditivo.mx"));
			    userDao.create(juanUser);
			  }
			
			  
			  
			  User carlosUser = null;
			  try {
			    userDao.delete("carlos@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting carlos@aditivo.mx...");
			    carlosUser = new User();
			    carlosUser.setFirstname("Carlos");
			    carlosUser.setLastname("");
			    carlosUser.setEmail("carlos@aditivo.mx");
			    carlosUser.getSecuritySettings().setRole(Role.STORE);
			    carlosUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    carlosUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "2810ac0e-480b-4374-8130-134862088a86",
			        "49264559-dddb-42c5-b1bf-1a52a0eb659f",
			        "4dbf03a7-f321-4109-abd0-58780310f09c",
			        "1fac0105-51f2-4a5f-ac3f-eaf4b6d311ed",
			        "349b85b9-a083-4e65-9740-f3d59278f635",
			        "ffc0b360-00d5-4e8f-8bef-f0472df6cb5f"
			        ));
			    carlosUser.setKey((Key) keyHelper.obtainKey(User.class, "carlos@aditivo.mx"));
			    userDao.create(carlosUser);
			  }
			  
			  
			  User hectorUser = null;
			  try {
			    userDao.delete("hector@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting hector@aditivo.mx...");
			    hectorUser = new User();
			    hectorUser.setFirstname("Hector");
			    hectorUser.setLastname("");
			    hectorUser.setEmail("hector@aditivo.mx");
			    hectorUser.getSecuritySettings().setRole(Role.STORE);
			    hectorUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    hectorUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "32543b75-32b2-4b41-9e03-f876a9e88d18",
			        "71352b76-f76e-421b-b114-72f071633b61",
			        "4fe543ea-610e-444b-bde3-e0cf12092ae6",
			        "b087d73e-ccb5-4457-8b09-e85ba72de7e7",
			        "f1c0b0d9-b2b4-4d63-a553-cec5653a79c3"
			        ));
			    hectorUser.setKey((Key) keyHelper.obtainKey(User.class, "hector@aditivo.mx"));
			    userDao.create(hectorUser);
			  }
			  
			  UserMenu um = null;
			  try {
			    um = userMenuDao.get("carlosc@aditivo.mx", true);
			    userMenuDao.delete("carlosc@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				
				um.setKey(userMenuDao.createKey("carlosc@aditivo.mx"));
				userMenuDao.create(um);
			  }
			  
			  User carloscUser = null;
			  try {
			    userDao.delete("carlosc@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting carlosc@aditivo.mx...");
			    carloscUser = new User();
			    carloscUser.setFirstname("Carlos C");
			    carloscUser.setLastname("");
			    carloscUser.setEmail("carlosc@aditivo.mx");
			    carloscUser.getSecuritySettings().setRole(Role.STORE);
			    carloscUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    carloscUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "b251d67f-b441-42d2-b69d-6a84c036e123",
			        "efb69866-6bc0-44e3-8dc2-f436dfc82773",
			        "f7b002fd-5c0c-4e2f-9879-0e98bda6cd5d"
			        ));
			    carloscUser.setKey((Key) keyHelper.obtainKey(User.class, "carlosc@aditivo.mx"));
			    userDao.create(carloscUser);
			  }
			  
			  
			  
			  User albertoUser = null;
			  try {
			    userDao.delete("alberto@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting alberto@aditivo.mx...");
			    albertoUser = new User();
			    albertoUser.setFirstname("Alberto");
			    albertoUser.setLastname("");
			    albertoUser.setEmail("alberto@aditivo.mx");
			    albertoUser.getSecuritySettings().setRole(Role.STORE);
			    albertoUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    albertoUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "92ec9131-dbf2-4a42-a3ef-1d68170de391",
			        "2b06d29f-204e-4a38-ac87-b77cb7d39578",
			        "13b66935-ec48-4fea-acc9-9e99f025a63b",
			        "674626e2-4537-44ba-a6a9-58632ec9f5ce",
			        "95744605-8649-4091-bdfe-5426ad0b6b3e",
			        "fcc7aa01-7b2d-4773-ba65-acc2dd7e592c"
			        ));
			    albertoUser.setKey((Key) keyHelper.obtainKey(User.class, "alberto@aditivo.mx"));
			    userDao.create(albertoUser);
			  }
			  
			  
			  User anegeloUser = null;
			  try {
			    userDao.delete("angelo@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting angelo@aditivo.mx...");
			    anegeloUser = new User();
			    anegeloUser.setFirstname("Angelo");
			    anegeloUser.setLastname("");
			    anegeloUser.setEmail("angelo@aditivo.mx");
			    anegeloUser.getSecuritySettings().setRole(Role.STORE);
			    anegeloUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    anegeloUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "98abde27-4dcc-4d5b-ac16-d43eac63b94b",
			        "129f18c1-c531-4488-9125-6d4e4ccf6d4d",
			        "3338e021-59c4-4482-9603-fac42d656c7b",
			        "78416e3d-2274-4a24-9186-3616588f6197",
			        "9beaf247-e674-47a2-9d4c-c550bb1aa7cc",
			        "dd77a5a2-6523-4325-b65c-1e3e3554028d"
			        ));
			    anegeloUser.setKey((Key) keyHelper.obtainKey(User.class, "angelo@aditivo.mx"));
			    userDao.create(anegeloUser);
			  }

			  
			  User ricardoUser = null;
			  try {
			    userDao.delete("ricardol@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting ricardol@aditivo.mx...");
			    ricardoUser = new User();
			    ricardoUser.setFirstname("Ricardo");
			    ricardoUser.setLastname("");
			    ricardoUser.setEmail("ricardol@aditivo.mx");
			    ricardoUser.getSecuritySettings().setRole(Role.STORE);
			    ricardoUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    ricardoUser.getSecuritySettings()
			        .setStores(Arrays.asList(
	        		"0221582e-c2fd-49d0-98ed-4635cd5e22db",
			        "5d8fa91b-b783-46ef-b615-c6aba8dec1fd",
			        "06b26796-bbda-49ad-b0b8-e24bc8cbeef6",
			        "4d217df4-d2ad-44e6-81eb-ac10a9c040ec",
			        "4d1d6d54-0cc1-4ba5-a40f-ed61284149c9",
			        "e41ddf46-b7fe-4d56-b52d-05a6cee7adf0",
			        "77bfffb3-48a9-43b6-b1ec-51d526e96da8",
	        		"11a2f4a2-75e3-4ee2-be94-391e02739d28",
	        		"2fc001ca-b8c4-4a5c-b7e2-c732c9f98ce0",
	        		"f04111dd-c59d-419c-bf6f-36ebecbcbd0d",
	        		"bde9a482-df2e-410e-9a51-06a90d2294d0",
	        		"da761750-c568-4e8b-9965-ee588c3d1d9a",
	        		"9a14f70c-52eb-4756-8fb3-b48ee8b86094",
			        "5da4f3c0-fe1f-47cb-9b7b-5fc4242240ce",
			        "2a90e8ab-fe34-4a3f-8bd4-0480ce4f40f8",
			        "3bd9d22b-65d9-44af-805f-87a77af5f691",
			        "625f5d03-6726-4bb2-894e-60749397dba6",
			        "b6d96f4a-d9f7-4537-87ae-c6b3f4b3c5e5"
			        ));
			    ricardoUser.setKey((Key) keyHelper.obtainKey(User.class, "ricardol@aditivo.mx"));
			    userDao.create(ricardoUser);
			  }
			  
			  
			  User miguelUser = null;
			  try {
			    userDao.delete("miguel@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting miguel@aditivo.mx...");
			    miguelUser = new User();
			    miguelUser.setFirstname("Miguel");
			    miguelUser.setLastname("");
			    miguelUser.setEmail("miguel@aditivo.mx");
			    miguelUser.getSecuritySettings().setRole(Role.STORE);
			    miguelUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    miguelUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "0221582e-c2fd-49d0-98ed-4635cd5e22db",
			        "5d8fa91b-b783-46ef-b615-c6aba8dec1fd",
			        "06b26796-bbda-49ad-b0b8-e24bc8cbeef6",
			        "4d217df4-d2ad-44e6-81eb-ac10a9c040ec",
			        "4d1d6d54-0cc1-4ba5-a40f-ed61284149c9",
			        "e41ddf46-b7fe-4d56-b52d-05a6cee7adf0"
			        ));
			    miguelUser.setKey((Key) keyHelper.obtainKey(User.class, "miguel@aditivo.mx"));
			    userDao.create(miguelUser);
			  }
			  
			  
			  User joseUser = null;
			  try {
			    userDao.delete("joser@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting joser@aditivo.mx...");
			    joseUser = new User();
			    joseUser.setFirstname("Ricardo");
			    joseUser.setLastname("");
			    joseUser.setEmail("joser@aditivo.mx");
			    joseUser.getSecuritySettings().setRole(Role.STORE);
			    joseUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    joseUser.getSecuritySettings()
			        .setStores(Arrays.asList(
	        		"77bfffb3-48a9-43b6-b1ec-51d526e96da8",
	        		"11a2f4a2-75e3-4ee2-be94-391e02739d28",
	        		"2fc001ca-b8c4-4a5c-b7e2-c732c9f98ce0",
	        		"f04111dd-c59d-419c-bf6f-36ebecbcbd0d",
	        		"bde9a482-df2e-410e-9a51-06a90d2294d0",
	        		"da761750-c568-4e8b-9965-ee588c3d1d9a"
			        ));
			    joseUser.setKey((Key) keyHelper.obtainKey(User.class, "joser@aditivo.mx"));
			    userDao.create(joseUser);
			  }
			  
			  User ignacioUser = null;
			  try {
			    userDao.delete("ignacio@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting ignacio@aditivo.mx...");
			    ignacioUser = new User();
			    ignacioUser.setFirstname("Ignacio");
			    ignacioUser.setLastname("");
			    ignacioUser.setEmail("ignacio@aditivo.mx");
			    ignacioUser.getSecuritySettings().setRole(Role.STORE);
			    ignacioUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    ignacioUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "9a14f70c-52eb-4756-8fb3-b48ee8b86094",
			        "5da4f3c0-fe1f-47cb-9b7b-5fc4242240ce",
			        "2a90e8ab-fe34-4a3f-8bd4-0480ce4f40f8",
			        "3bd9d22b-65d9-44af-805f-87a77af5f691",
			        "625f5d03-6726-4bb2-894e-60749397dba6",
			        "b6d96f4a-d9f7-4537-87ae-c6b3f4b3c5e5"
			        ));
			    ignacioUser.setKey((Key) keyHelper.obtainKey(User.class, "ignacio@aditivo.mx"));
			    userDao.create(ignacioUser);
			  }
			  
			  
			  User aaronUser = null;
			  try {
			    userDao.delete("aaron@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting aaron@aditivo.mx...");
			    aaronUser = new User();
			    aaronUser.setFirstname("Aaron");
			    aaronUser.setLastname("");
			    aaronUser.setEmail("aaron@aditivo.mx");
			    aaronUser.getSecuritySettings().setRole(Role.STORE);
			    aaronUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    aaronUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "4d768d73-b9a9-44a0-bbbe-dc00a04f52ec",
			        "23178716-9ef5-4b57-b88e-ea85d080c0f7",
			        "3928b1d6-2fb7-4a62-a081-9e5a23e78e91",
			        "07c6552d-c3fe-445b-aac4-e1d2c234d2ca",
			        "8cac6f24-fc71-4e4a-b556-5bfe06191f3f",
			        "6ad6e636-f5ec-4d8f-a499-c85055e03f4e",
			        "1b509e3a-068e-4062-9781-d04c175db304",
			        "61374a58-a679-4532-811a-aa3340bcc47e",
			        "d2112ef6-7cfa-49a4-94de-a127e45ff1c1",
			        "f33140b3-3ecd-4d70-bfcc-159f47ac9058",
			        "fbbc3da9-1403-4206-8b22-e1aed2b0ec40",
			        "ce91457a-f7dc-49d0-93ff-79259e553769"
			        ));
			    aaronUser.setKey((Key) keyHelper.obtainKey(User.class, "aaron@aditivo.mx"));
			    userDao.create(aaronUser);
			  }

			  
			  
			  User isaiasUser = null;
			  try {
			    userDao.delete("isaias@aditivo.mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting isaias@aditivo.mx...");
			    isaiasUser = new User();
			    isaiasUser.setFirstname("Isaias");
			    isaiasUser.setLastname("");
			    isaiasUser.setEmail("isaias@aditivo.mx");
			    isaiasUser.getSecuritySettings().setRole(Role.STORE);
			    isaiasUser.getSecuritySettings()
			        .setPassword(encodeString("Aditivo2017"));
			    isaiasUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "07c6552d-c3fe-445b-aac4-e1d2c234d2ca",
			        "1b509e3a-068e-4062-9781-d04c175db304",
			        "61374a58-a679-4532-811a-aa3340bcc47e",
			        "d2112ef6-7cfa-49a4-94de-a127e45ff1c1",
			        "f33140b3-3ecd-4d70-bfcc-159f47ac9058",
			        "fbbc3da9-1403-4206-8b22-e1aed2b0ec40",
			        "ce91457a-f7dc-49d0-93ff-79259e553769"
			        ));
			    isaiasUser.setKey((Key) keyHelper.obtainKey(User.class, "isaias@aditivo.mx"));
			    userDao.create(isaiasUser);
			  }
			  
			  UserMenu umBalcarce = null;
			  try {
				umBalcarce = userMenuDao.get("cafe_balcarce_ar", true);
			    userMenuDao.delete("cafe_balcarce_ar");
			    throw new Exception();
			  } catch (Exception e) {
			    umBalcarce = new UserMenu();
				umBalcarce.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				umBalcarce.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));
				umBalcarce.setKey(userMenuDao.createKey("cafe_balcarce_ar"));
				userMenuDao.create(umBalcarce);
			  }
			  

			  try {
			    um = userMenuDao.get("universodefragancias_vallejo_mx", true);
			    userMenuDao.delete("universodefragancias_vallejo_mx");
			    throw new Exception();
			  } catch (Exception e) {
			    um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.storetickets", "fa-ticket", "Tickets"));

				
				um.setKey(userMenuDao.createKey("universodefragancias_vallejo_mx"));
				userMenuDao.create(um);
			  }
			  
			  User universoUser = null;
			  try {
			    userDao.delete("universodefragancias_vallejo_mx");
			    throw new Exception();
			  } catch (Exception e) {
			    log.log(Level.INFO, "Inserting universo de fragancias Vallejo...");
			    universoUser = new User();
			    universoUser.setFirstname("Universo de fragancias Vallejo");
			    universoUser.setLastname("");
			    universoUser.setEmail("universodefragancias_vallejo_mx");
			    universoUser.getSecuritySettings().setRole(Role.STORE);
			    universoUser.getSecuritySettings()
			        .setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
			    universoUser.getSecuritySettings()
			        .setStores(Arrays.asList(
			        "2d856f3f-8793-49b5-a2a6-f662edb07d76"
			        
			        ));
			    universoUser.setKey((Key) keyHelper.obtainKey(User.class, "universodefragancias_vallejo_mx"));
			    userDao.create(universoUser);
			  }
			  
			  
			  
			  
			  try {
				    um = userMenuDao.get("elgalpontacuara_ar", true);
				    userMenuDao.delete("elgalpontacuara_ar");
				    throw new Exception();
				  } catch (Exception e) {
				    um = new UserMenu();
					um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
					
					um.setKey(userMenuDao.createKey("elgalpontacuara_ar"));
					userMenuDao.create(um);
				  }
				  
				  User galponUser = null;
				  try {
				    userDao.delete("elgalpontacuara_ar");
				    throw new Exception();
				  } catch (Exception e) {
				    log.log(Level.INFO, "Inserting elgalpontacuara_ar...");
				    galponUser = new User();
				    galponUser.setFirstname("El galpón de Tacuara");
				    galponUser.setLastname("");
				    galponUser.setEmail("elgalpontacuara_ar");
				    galponUser.getSecuritySettings().setRole(Role.STORE);
				    galponUser.getSecuritySettings()
				        .setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				    galponUser.getSecuritySettings()
				        .setStores(Arrays.asList(
				        "af28a490-208c-4213-a967-eb1982980435"
				        
				        ));
				    galponUser.setKey((Key) keyHelper.obtainKey(User.class, "elgalpontacuara_ar"));
				    userDao.create(galponUser);
				  }
				  
				  User arturo = null;
				  try {
				    arturo = userDao.get("arturo@getin.mx", true);
				    userDao.delete(arturo);
				    throw new Exception();
				  } catch( Exception e ) {
				    arturo = new User();
				    arturo.setFirstname("Arturo Armengod");
				    arturo.setLastname("");
				    arturo.setEmail("arturo@getin.mx");
				    arturo.getSecuritySettings().setRole(Role.ADMIN);
				    arturo.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				    arturo.setKey((Key)keyHelper.obtainKey(User.class, "arturo@getin.mx"));
				    userDao.create(arturo);
				  }

				  try {
				    um = userMenuDao.get("arturo@getin.mx", true);
				    userMenuDao.delete("arturo@getin.mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdevices", "fa-signal", "Antenas"));
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Dashboard"));
				  um.getEntries().add(new UserMenuEntry("index.apreport", "fa-table", "Reporte"));
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
				    um.setKey(userMenuDao.createKey("arturo@getin.mx"));
				    userMenuDao.create(um);
				  }
				  
				  User demo5User = null;
				  try {
				    demo5User = userDao.get("demo5_mx", true);
				    userDao.delete(demo5User);
				    throw new Exception();
				  } catch( Exception e ) {
				    demo5User = new User();
				    demo5User.setFirstname("Demo Heatmap");
				    demo5User.setLastname("Mexico");
				    demo5User.setEmail("demo5User@allshoppings.mobi");
				    demo5User.getSecuritySettings().setRole(Role.BRAND);
				    demo5User.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				    demo5User.getSecuritySettings().setShoppings(Arrays.asList("a9f9d78e-d5f6-42b5-97be-2a84aca5165d"));
				    demo5User.setKey((Key)keyHelper.obtainKey(User.class, "demo5_mx"));
				    userDao.create(demo5User);
				  }
				  try {
				    um = userMenuDao.get("demo5_mx", true);
				    userMenuDao.delete("demo5_mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    um = new UserMenu();
				    um.getEntries().add(new UserMenuEntry("index.demovisits5", "fa-area-chart", "Tráfico"));
				    um.getEntries().add(new UserMenuEntry("index.patternheatmapdemo", "fa-building", "Heatmaps"));
				    um.setKey(userMenuDao.createKey("demo5_mx"));
				    userMenuDao.create(um);
				  }
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}