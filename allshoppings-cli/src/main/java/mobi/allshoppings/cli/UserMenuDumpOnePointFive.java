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

public class UserMenuDumpOnePointFive extends AbstractCLI {

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
			
			UserMenuDAO userMenuDao = (UserMenuDAO)getApplicationContext().getBean("usermenu.dao.ref");
			UserDAO userDao = (UserDAO)getApplicationContext().getBean("user.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");

			log.log(Level.INFO, "Dumping Getin Users....");

			UserMenu um = null;
			User user = null;
			
			
			
			
			try {
			user = userDao.get("ricardol@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Ricardo Lopez");
				user.setLastname("");
				user.setEmail("ricardol@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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
								"625f5d03-6726-4bb2-894e-60749397dba6", "2a90e8ab-fe34-4a3f-8bd4-0480ce4f40f8"));
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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

			try {
				um = userMenuDao.get("hugo_rodriguez", true);
				userMenuDao.delete("hugo_rodriguez");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("hugo_rodriguez"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("hugo_rodriguez", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Hugo Rodriguez");
				user.setLastname("Modatelas");
				user.setEmail("hugo_rodriguez");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("b7cb1719-2f50-45c2-a1fa-66c98cd1a7e4",
								"f4a78ecf-07d9-4158-bcd4-159a68a247df", "7c10f699-b944-4627-9155-22af377da01f",
								"6d2955f9-f112-47b3-ae50-6528697c249c", "4da00bc6-ae1a-418a-a784-1f764d281908",
								"2bad1b20-31c4-44c4-9015-6e9dd5c30b00", "7b7d1c1e-f6af-40b7-8a55-f3bdbf19cde1",
								"4e8dd00e-a179-40d1-8b72-9389e6e52fb0", "9ccdd1eb-30fe-4304-9bd5-7a4b0614c842",
								"50b32c68-0998-4323-a404-eee8e316e3eb", "07b5d16f-6b65-4beb-b763-d47f8a089efd",
								"d93f2cf8-de1f-4e5c-884b-adb87cbbcdf2", "865adafe-7df9-46fc-8201-32f260e5ff06"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "hugo_rodriguez"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("hector_rodriguez", true);
				userMenuDao.delete("hector_rodriguez");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("hector_rodriguez"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("hector_rodriguez", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Hector Rodriguez");
				user.setLastname("Modatelas");
				user.setEmail("hector_rodriguez");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("dd1edbcb-5eeb-467e-af3d-223611492520",
								"70e3f099-03ef-4df2-9849-0f4f4a3dd411", "94634900-964f-4092-a0dd-e1dc287144ee",
								"3f7497de-95c8-4bb5-a9a5-6d097133bf7c", "dbd5021e-4d65-47da-a64e-42006513d998",
								"0125a1b9-0d3a-4383-8377-04674b1fc08f", "9202f7a2-4354-47bb-a6c3-dd775af93a17",
								"80175990-43cd-4c27-a5ca-d16179c7f55f", "ceade140-174b-464c-9116-70ef2b93317a",
								"fe9ad717-4f1e-4b28-b9ea-bfa94ace3141", "3307ec01-1e57-40e2-97a3-d1e02227a4b0"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "hector_rodriguez"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("alejandro_saucedo", true);
				userMenuDao.delete("alejandro_saucedo");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("alejandro_saucedo"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("alejandro_saucedo", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Alejandro Saucedo");
				user.setLastname("Modatelas");
				user.setEmail("alejandro_saucedo");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("76eccdaf-348d-4540-af6d-145463e0844e",
								"740547b3-5c3a-492c-a2f8-bc88345fcc5d", "7e47d9df-f011-4203-9ac5-5aa7222cccb5",
								"519d19e7-ddf4-47a2-abdc-09e89d1f5c64"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "alejandro_saucedo"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("jorge_velazquez", true);
				userMenuDao.delete("jorge_velazquez");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("jorge_velazquez"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("jorge_velazquez", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Jorge Velazquez");
				user.setLastname("Modatelas");
				user.setEmail("jorge_velazquez");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("08644a45-800d-43ac-b228-78c28d82bad1"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "jorge_velazquez"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("gustavo_sandoval", true);
				userMenuDao.delete("gustavo_sandoval");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("gustavo_sandoval"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("gustavo_sandoval", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Gustavo Sandoval");
				user.setLastname("Modatelas");
				user.setEmail("gustavo_sandoval");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("58131ca7-ae67-4022-bcfa-0af9301bdddc"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "gustavo_sandoval"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("daniel_gonzalez", true);
				userMenuDao.delete("daniel_gonzalez");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("daniel_gonzalez"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("daniel_gonzalez", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Daniel Gonzalez");
				user.setLastname("Modatelas");
				user.setEmail("daniel_gonzalez");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("92fbf40a-20af-479a-88fd-7a2b8c8f4401",
								"44e14c13-54cb-4592-a84c-538399074698", "9e08742a-b37a-4d7d-9427-c0c585e89fce",
								"7e129304-5169-4f2d-a51a-9afc61cc3ad7", "8e84228e-cc5e-42b5-a665-179ebcd81511",
								"9ab15245-554a-4612-8f33-b57d03f58c17", "a6f96106-1f29-4b47-8a2f-b3b28d7e1a8c",
								"ac082169-dcd8-4e01-adf0-df3054a5b7af"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "daniel_gonzalez"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("francisco_badillo", true);
				userMenuDao.delete("francisco_badillo");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("francisco_badillo"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("francisco_badillo", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Francisco Badillo");
				user.setLastname("Modatelas");
				user.setEmail("francisco_badillo");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("a118c701-143d-4ebc-a40f-ea3277949e10",
								"7dadd279-08cc-4a51-86cd-d85df6abf6c3", "3d64ba77-e24c-4c06-83f0-63444ab8eaed",
								"9658cd89-f277-4946-bfcb-9330b2c886c2", "9658cd89-f277-4946-bfcb-9330b2c886c2",
								"536a6470-a8e1-46f4-bc2d-be7b0ace6ea7", "2b2b88d1-afbc-4da0-be04-1ba057c07e95",
								"76749515-b97a-4bfa-9cfa-e1521eccb33c", "a7d255b5-3d20-46aa-8c9e-e0696f52b908",
								"a23edfab-aed9-4562-8100-65544550adf4"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "francisco_badillo"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("adan_hernandez", true);
				userMenuDao.delete("adan_hernandez");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("adan_hernandez"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("adan_hernandez", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Adan Hernandez");
				user.setLastname("Modatelas");
				user.setEmail("adan_hernandez");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("4bb5fd5f-e161-4461-8c9c-84e8172f5466",
								"06c5b1f3-0f78-4100-9038-2ba9603087c9", "3c506e3a-c23a-4454-b2e2-578f6f4e081c",
								"24bb9d59-a029-4cdc-9341-e2c6b1061126", "b26c6912-d357-4554-9df9-c054351fa7f6",
								"7daf5ac9-bd14-47c2-9909-d849fdc09be2", "2d59df41-fae4-4570-8afc-16f0c50ff917",
								"a3bbd458-fe46-4799-8f78-11d642196a2c"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "adan_hernandez"));
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("51a62c6b-9596-4e63-8241-21563bf5925e",
								"5eea383c-c550-44d3-bc5d-a3bbf8cff283", "4459e3ee-0085-4382-95f4-042af4b89195",
								"1498839031159", "f7e7ac30-e0f3-4c8b-be85-9a4ae88c0400",
								"749fe355-833e-4d93-a0db-e613135aa2d0",
								"7fe9aeb4-a017-45b5-a388-90f88a3b2455", "fe83b69e-7a67-45c8-a08c-8c78c8867d3a",
								"320f0fb2-713b-46ba-8930-36fe497f0c47", "9d760b01-cdae-4391-bab9-32e40f6bb338",
								"a8fad67e-e27f-4c5a-9313-e877015943fd"));
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
				userDao.delete(user);
			} catch (Exception e) {

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
				userDao.delete(user);
			} catch (Exception e) {

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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
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
								"fb9ba533-212b-4b26-b9a8-73026e5059f0", "1479331293676",
								"976e42c8-2cef-4802-8fb6-0c7eba3900cb"));
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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("f0b0e659-9355-4abd-956f-65ab53f38e99",
								"7834f952-f64a-4227-9eac-8d9a073311a3", "2d3f152a-85cf-4f07-8c73-b99b2928b011",
								"2342e42f-d937-417f-bb13-0083588b7812"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "modaholding_mx"));
				userDao.create(user);
			}

			// Aditivo Franquicias Michan  --------------------------------------------------------------------

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
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("aff0af5d-45b8-46b6-81ee-12c79990653b"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "franquiciasmichan_mx"));
				userDao.create(user);
			}

			// End Aditivo Franquicias Michan  --------------------------------------------------------------------

			// Aditivo Franquicias  --------------------------------------------------------------------

			try {
				um = userMenuDao.get("daniel@aditivofranquicias_mx", true);
				userMenuDao.delete("daniel@aditivofranquicias_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("daniel@aditivofranquicias_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("daniel@aditivofranquicias_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Aditivo Franquicias");
				user.setLastname("");
				user.setEmail("daniel@aditivofranquicias_mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("a45fc81f-900e-457e-854e-df4a312bb0e1",
								"d2eab997-2497-49b3-b1bd-15cf80f05fc7", "8d2335b7-4cc3-4f76-b274-86137b34b4e5",
								"3c9e6b60-bdcd-4268-99eb-a9c9f719f625"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "daniel@aditivofranquicias_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("aditivo_franquicias_mx", true);
				userMenuDao.delete("aditivo_franquicias_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.getEntries().add(new UserMenuEntry("index.employeetimes", "fa-address-card-o", "Horario de Empleados"));
				um.setKey(userMenuDao.createKey("aditivo_franquicias_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("aditivo_franquicias_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Aditivo Franquicias");
				user.setLastname("");
				user.setEmail("aditivo_franquicias_mx");
				user.getSecuritySettings().setRole(Role.BRAND);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.setKey((Key) keyHelper.obtainKey(User.class, "aditivo_franquicias_mx"));
				userDao.create(user);
			}

			// End Aditivo Franquicias  --------------------------------------------------------------------

			// Aditivo Franquicias 2  --------------------------------------------------------------------

			try {
				um = userMenuDao.get("aditivofranquicias2_mx", true);
				userMenuDao.delete("aditivofranquicias2_mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("aditivofranquicias2_mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("aditivofranquicias2_mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Aditivo Franquicias 2");
				user.setLastname("");
				user.setEmail("aditivofranquicias2_mx");
				user.getSecuritySettings().setRole(Role.BRAND);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.setKey((Key) keyHelper.obtainKey(User.class, "aditivofranquicias2_mx"));
				userDao.create(user);
			}

			try {
				um = userMenuDao.get("ricardo@aditivo.mx", true);
				userMenuDao.delete("ricardo@aditivo.mx");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("ricardo@aditivo.mx"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("ricardo@aditivo.mx", true);
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Aditivo");
				user.setLastname("");
				user.setEmail("ricardo@aditivo.mx");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
						.setStores(Arrays.asList("07acea43-4a6c-4adc-896c-00b1f4781242",
								"2fea9047-6da8-4493-b97c-f3cdd809a18d", "800f4a09-34f7-4116-bf5c-4b0ab45175c8",
								"75746850-9ae4-4aa4-bf8f-3bf01daf2775", "0867dbd4-53f1-4602-8a48-e0c07bd752da"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "ricardo@aditivo.mx"));
				userDao.create(user);
			}

			// End Aditivo Franquicias 2 --------------------------------------------------------------------

			try {
				um = userMenuDao.get("angel@yogome.com", true);
				userMenuDao.delete("angel@yogome.com");
				throw new Exception();
			} catch (Exception e) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.devicemessage", "fa-laptop", "Notificaciones"));
				um.setKey(userMenuDao.createKey("angel@yogome.com"));
				userMenuDao.create(um);
			}

			try {
				user = userDao.get("angel@yogome.com", true);
				userDao.delete(user);
				throw new Exception();
			} catch (Exception e) {
				user = new User();
				user.setFirstname("Angel Hernandez");
				user.setLastname("");
				user.setEmail("angel@yogome.com");
				user.getSecuritySettings().setRole(Role.STORE);
				user.getSecuritySettings()
						.setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				user.getSecuritySettings()
					    .setStores(Arrays.asList("241f8851-fa36-4de4-a3e9-673b6879b00b"));
				user.setKey((Key) keyHelper.obtainKey(User.class, "angel@yogome.com"));
				userDao.create(user);
			}

			User userChilimbalam = null;
 			try {
 				userChilimbalam = userDao.get("ariel@chilimbalam.com.mx", true);
 			} catch( Exception e ) {
 				userChilimbalam = new User();
 				userChilimbalam.setFirstname("Ariel");
 				userChilimbalam.setLastname("Chilim");
 				userChilimbalam.setEmail("ariel@chilimbalam.com.mx");
 				userChilimbalam.getSecuritySettings().setRole(Role.BRAND);
 				userChilimbalam.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
 				userChilimbalam.setKey((Key)keyHelper.obtainKey(User.class, "ariel@chilimbalam.com.mx"));
 				userDao.create(userChilimbalam);

 			}


 			userChilimbalam = null;
 			try {
 				userChilimbalam = userDao.get("hmorales@chilimbalam.com.mx", true);
 			} catch( Exception e ) {
 				userChilimbalam = new User();
 				userChilimbalam.setFirstname("H");
 				userChilimbalam.setLastname("Morales");
 				userChilimbalam.setEmail("hmorales@chilimbalam.com.mx");
 				userChilimbalam.getSecuritySettings().setRole(Role.BRAND);
 				userChilimbalam.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
 				userChilimbalam.setKey((Key)keyHelper.obtainKey(User.class, "hmorales@chilimbalam.com.mx"));
 				userDao.create(userChilimbalam);
 			}

 			userChilimbalam = null;
 			try {
 				userChilimbalam = userDao.get("arocha@chilimbalam.com.mx", true);
 			} catch( Exception e ) {
 				userChilimbalam = new User();
 				userChilimbalam.setFirstname("A");
 				userChilimbalam.setLastname("Rocha");
 				userChilimbalam.setEmail("arocha@chilimbalam.com.mx");
 				userChilimbalam.getSecuritySettings().setRole(Role.BRAND);
 				userChilimbalam.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
 				userChilimbalam.setKey((Key)keyHelper.obtainKey(User.class, "arocha@chilimbalam.com.mx"));
 				userDao.create(userChilimbalam);
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

 			try {
				um = userMenuDao.get("hmorales@chilimbalam.com.mx", true);
				userMenuDao.delete("hmorales@chilimbalam.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("hmorales@chilimbalam.com.mx"));
				userMenuDao.create(um);
			}

			try {
				um = userMenuDao.get("arocha@chilimbalam.com.mx", true);
				userMenuDao.delete("arocha@chilimbalam.com.mx");
				throw new Exception();
			} catch( Exception e ) {
				um = new UserMenu();
				um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				um.setKey(userMenuDao.createKey("arocha@chilimbalam.com.mx"));
				userMenuDao.create(um);
			}
			um = new UserMenu();
			um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			um.setKey(userMenuDao.createKey("juguetron_mx"));
			userMenuDao.createOrUpdate(um);
			User erick1 = null;
			erick1 = new User();
			erick1.setFirstname("juguetron");
			erick1.setLastname("");
			erick1.getSecuritySettings().setRole(Role.BRAND);
			erick1.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
			erick1.setKey((Key)keyHelper.obtainKey(User.class, "juguetron_mx"));
			userDao.createOrUpdate(erick1);
			
			User miguel = null;
			miguel = new User();
			miguel.setFirstname("Miguel");
			miguel.setLastname("Roldán");
			miguel.setEmail("miguel@getin.mx");
			miguel.getSecuritySettings().setRole(Role.ADMIN);
			miguel.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
			miguel.setKey((Key)keyHelper.obtainKey(User.class, "miguel@getin.mx"));
			userDao.createOrUpdate(miguel);

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
			um.setKey(userMenuDao.createKey("miguel@getin.mx"));
			userMenuDao.createOrUpdate(um);
			
		}catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}