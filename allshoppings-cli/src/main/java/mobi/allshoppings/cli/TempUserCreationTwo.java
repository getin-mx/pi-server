package mobi.allshoppings.cli;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.dao.UserMenuDAO;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserMenu;
import mobi.allshoppings.model.UserMenuEntry;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.tools.KeyHelper;


public class TempUserCreationTwo extends AbstractCLI {
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

			/*
			User trenderU = null;
			UserMenu um = null;

			try {
			  trenderU = userDao.get("trender_mx", true);
			  userDao.delete("trender_mx");
			  throw new Exception();
			} catch( Exception e ) {
			    log.log(Level.INFO, "Inserting trender_mx...");
			  trenderU = new User();
			  trenderU.setFirstname("Trender");
			  trenderU.setLastname("Mexico");
			  trenderU.setEmail("trenderU@getin.mx");
			  trenderU.getSecuritySettings().setRole(Role.BRAND);
			  trenderU.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
			  trenderU.setKey((Key)keyHelper.obtainKey(User.class, "trender_mx"));
			  userDao.create(trenderU);
			    log.log(Level.INFO, "Inserting Trender...");

			}
			try {
			  um = userMenuDao.get("trender_mx", true);
			  userMenuDao.delete("trender_mx");
			  throw new Exception();
			} catch( Exception e ) {
			  um = new UserMenu();
			  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			  um.setKey(userMenuDao.createKey("trender_mx"));
			  userMenuDao.create(um);
			}
			
			User asiaPacificoU = null;

			
			try {
				  asiaPacificoU = userDao.get("asiapacifico_mx", true);
				  userDao.delete("asiapacifico_mx");
				  throw new Exception();
				} catch( Exception e ) {
				    log.log(Level.INFO, "Inserting asiapacifico...");
				  asiaPacificoU = new User();
				  asiaPacificoU.setFirstname("Asia Pacífico Shoes");
				  asiaPacificoU.setLastname("Mexico");
				  asiaPacificoU.setEmail("asiapacifico@getin.mx");
				  asiaPacificoU.getSecuritySettings().setRole(Role.BRAND);
				  asiaPacificoU.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				  asiaPacificoU.setKey((Key)keyHelper.obtainKey(User.class, "asiapacifico_mx"));
				  userDao.create(asiaPacificoU);
				  
				  log.log(Level.INFO, "Inserting Asia Pacifico...");
				}
				try {
				  um = userMenuDao.get("asiapacifico_mx", true);
				  userMenuDao.delete("asiapacifico_mx");
				  throw new Exception();
				} catch( Exception e ) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.setKey(userMenuDao.createKey("asiapacifico_mx"));
				  userMenuDao.create(um);
				}
				
				
				User tiendaTecUser = null;
				
				try {
				    tiendaTecUser = userDao.get("tiendatec_mx", true);
				    userDao.delete("tiendatec_mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    log.log(Level.INFO, "Inserting tienda Tec...");
				    tiendaTecUser = new User();
				    tiendaTecUser.setFirstname("Tienda Tec");
				    tiendaTecUser.setLastname("Mexico");
				    tiendaTecUser.setEmail("tiendatec@getin.mx");
				    tiendaTecUser.getSecuritySettings().setRole(Role.BRAND);
				    tiendaTecUser.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				    tiendaTecUser.setKey((Key)keyHelper.obtainKey(User.class, "tiendatec_mx"));
				    userDao.create(tiendaTecUser);
				  }
				  try {
				    um = userMenuDao.get("tiendatec_mx", true);
				    userMenuDao.delete("tiendatec_mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    um = new UserMenu();
				    um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				    um.setKey(userMenuDao.createKey("tiendatec_mx"));
				    userMenuDao.create(um);
				  }
				
				
				User aditivoCelia = null;
				  
				  try {
				      aditivoCelia = userDao.get("aditivofranquicias2_mx", true);
				      userDao.delete("aditivofranquicias2_mx");
				      throw new Exception();
				    } catch( Exception e ) {
				        log.log(Level.INFO, "Inserting aditivofranquicias2_mx...");
				      aditivoCelia = new User();
				      aditivoCelia.setFirstname("Aditivo Celia");
				      aditivoCelia.setLastname("Mexico");
				      aditivoCelia.setEmail("aditivoCelia@getin.mx");
				      aditivoCelia.getSecuritySettings().setRole(Role.BRAND);
				      aditivoCelia.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				      aditivoCelia.setKey((Key)keyHelper.obtainKey(User.class, "aditivofranquicias2_mx"));
				      userDao.create(aditivoCelia);
				    }
				    try {
				      um = userMenuDao.get("aditivofranquicias2_mx", true);
				      userMenuDao.delete("aditivofranquicias2_mx");
				      throw new Exception();
				    } catch( Exception e ) {
				      um = new UserMenu();
				      um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				      um.setKey(userMenuDao.createKey("aditivofranquicias2_mx"));
				      userMenuDao.create(um);
				    }
				    
				    
				    
				
				try {
					  um = userMenuDao.get("victor@tanyamoss.com", true);
					  userMenuDao.delete("victor@tanyamoss.com");
					  throw new Exception();
					} catch (Exception e) {
					  um = new UserMenu();
					  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
					  um.setKey(userMenuDao.createKey("victor@tanyamoss.com"));
					  userMenuDao.create(um);
				       
					  log.log(Level.INFO, "Inserting victor@tanyamoss...");
					}

				User victorUser = null;

				try {
				  um = userMenuDao.get("victor@tanyamoss.com", true);
				  userMenuDao.delete("victor@tanyamoss.com");
				  throw new Exception();
				} catch (Exception e) {
				  um = new UserMenu();
				  um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				  um.setKey(userMenuDao.createKey("victor@tanyamoss.com"));
				  userMenuDao.create(um);
				}
				
				try {
				  victorUser = userDao.get("victor@tanyamoss.com", true);
				} catch (Exception e) {
				  victorUser = new User();
				  victorUser.setFirstname("Victor");
				  victorUser.setLastname("");
				  victorUser.setEmail("victor@tanyamoss.com");
				  victorUser.getSecuritySettings().setRole(Role.STORE);
				  victorUser.getSecuritySettings()
				      .setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				  victorUser.getSecuritySettings()
				      .setStores(Arrays.asList("1493049398128",
				                              "1493049398625",
				                              "590645c4-8ca5-450a-a95f-6fa7c560ee36",
				                              "806bdc75-ea6a-4a5f-b4db-a7423e8528d6",
				                              "0cbaca45-1045-43a4-b238-eb18651732ec",
				                              "324e7d47-d156-4f77-992a-adb26318b8a8",
				                              "22a028be-3ac2-4cb9-bc09-7beeeca4f024",
				                              "4e139439-c74f-47b8-a41a-030756322a84",
				                              "7cf9b273-40ed-448e-b948-91c802dc8a22",
				                              "1493049397673",
				                              "d7d10b1f-75e4-4b04-b035-24fede6f76eb",
				                              "cc2e68ee-6d24-4132-8ae5-13d6836e4f69",
				                              "d0b66984-5e85-4df3-bc88-aa5125354588",
				                              "fcfc53b9-1455-4895-b8a9-f7bba0adeb4d",
				                              "be129cb5-6b12-4c83-89fd-2d008adf7947"));
				  victorUser.setKey((Key) keyHelper.obtainKey(User.class, "victor@tanyamoss.com"));
				  userDao.create(victorUser);
				}
				
				User juanLuisChomarc = null;

			    try {
			      um = userMenuDao.get("juanluis.fernandez@grupochomarc.com.mx", true);
			      userMenuDao.delete("juanluis.fernandez@grupochomarc.com.mx");
			      throw new Exception();
			    } catch (Exception e) {
			      um = new UserMenu();
			      um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
			      um.setKey(userMenuDao.createKey("juanluis.fernandez@grupochomarc.com.mx"));
			      userMenuDao.create(um);
			    }
			    
			    try {
			      juanLuisChomarc = userDao.get("juanluis.fernandez@grupochomarc.com.mx", true);
			      userDao.delete(juanLuisChomarc);
			      throw new Exception();
			    } catch (Exception e) {
			      juanLuisChomarc = new User();
			      juanLuisChomarc.setFirstname("Juan Luis");
			      juanLuisChomarc.setLastname("Fernández");
			      juanLuisChomarc.setEmail("juanluis.fernandez@grupochomarc.com.mx");
			      juanLuisChomarc.getSecuritySettings().setRole(Role.STORE);
			      juanLuisChomarc.getSecuritySettings()
			          .setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
			      juanLuisChomarc.getSecuritySettings()
			          .setStores(Arrays.asList("95d98d90-ba0b-42e1-843b-4a0e5c09db4b",
			          "81b52571-3bd3-44e3-bed9-9e592d568f04",
			          "4df8d43b-b8d6-41a2-a342-082884a5e897",
			          "06a99c76-c4ca-4952-a99f-3576c7e4dce0",
			          "93ec0988-c44a-402e-bbe4-83d1e377a559",
			          "2ed6fea4-efb3-4aef-bc5c-af1e3d712b4b",
			          "3729ac49-92f5-44ba-b40c-4c31bcaf85a9",
			          "5dce55ee-8506-4b70-b7a7-aee2a7e6cbb4",
			          "4beba5a8-3987-489e-9ef7-8be65d3c1b27",
			          "8b0d5f06-f5f6-4d70-aab5-b3ef1e807d97",
			          "62c734bd-15fa-4bc5-a542-d38dd30e4546",
			          "28b335d2-9bf2-48c3-a335-d2ae4314247a",
			          "6e39b7d5-dcd5-458b-9088-a2c97be409e3",
			          "743003df-cb85-4f8f-98eb-d41ff31f3e36",
			          "94b9e9fc-3f73-4926-9890-fe0d924952fc",
			          "1480464171515",
			          "b5e35d13-3abc-4629-993a-c742dbd81f0e",
			          "b9e3dee9-fcb8-4c36-9620-2c897b03566f",
			          "b327518a-28a8-4ca0-b82a-bd1e646307ce",
			          "ba26aea6-dda1-4bfe-a270-23350be7105e",
			          "cc13c199-5969-4010-aedb-bf01a4428786",
			          "d22be9e5-74a7-4671-aa7e-1a464bb748b7",
			          "e0bb9d40-7639-47d0-ab38-135b280ac769",
			          "ec77ff67-9221-4c41-afba-0c47f73e0ba3"));
			      juanLuisChomarc.setKey((Key) keyHelper.obtainKey(User.class, "juanluis.fernandez@grupochomarc.com.mx"));
			      userDao.create(juanLuisChomarc);
			      log.log(Level.INFO, "Inserted Juan Luis Chomarc...");
			    }
			    
			    User cloe;
			    UserMenu um = null;
			    
				try {
					  cloe = userDao.get("cloe_mx", true);
					  userDao.delete("cloe_mx");
					  throw new Exception();
					} catch( Exception e ) {
					    log.log(Level.INFO, "Inserting Cloe_mx...");
					  cloe = new User();
					  cloe.setFirstname("Cloe");
					  cloe.setLastname("Mexico");
					  cloe.setEmail("cloe@getin.mx");
					  cloe.getSecuritySettings().setRole(Role.BRAND);
					  cloe.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
					  cloe.setKey((Key)keyHelper.obtainKey(User.class, "cloe_mx"));
					  userDao.create(cloe);
					    log.log(Level.INFO, "Inserting cloe...");

					}
				  try {
					    um = userMenuDao.get("cloe_mx", true);
					    userMenuDao.delete("cloe_mx");
					    throw new Exception();
					  } catch( Exception e ) {
					    um = new UserMenu();
					    um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
					    um.setKey(userMenuDao.createKey("cloe_mx"));
					    userMenuDao.create(um);
					  }
				  	
				    User cloe= null;
				    UserMenu um = null;
				    
					try {
						cloe = userDao.get("cloe_mx", true);
						  userDao.delete("cloe_mx");
						  throw new Exception();
						} catch( Exception e ) {
						    log.log(Level.INFO, "Inserting Cloe_mx...");
						    cloe = new User();
						    cloe.setFirstname("Cloe");
						    cloe.setLastname("Mexico");
						    cloe.setEmail("cloe@getin.mx");
						    cloe.getSecuritySettings().setRole(Role.BRAND);
						    cloe.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
						    cloe.setKey((Key)keyHelper.obtainKey(User.class, "cloe_mx"));
						  userDao.create(cloe);
						    log.log(Level.INFO, "Inserting cloe...");

						}
					  try {
						    um = userMenuDao.get("cloe_mx", true);
						    userMenuDao.delete("cloe_mx");
						    throw new Exception();
						  } catch( Exception e ) {
						    um = new UserMenu();
						    um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
						    um.setKey(userMenuDao.createKey("cloe_mx"));
						    userMenuDao.create(um);
						  }
				    
			    
			
			User lizbeth = null;
			UserMenu um = null;
			try {
				

				
				lizbeth = userDao.get("lizbeth@aditivo.mx", true);
			      userDao.delete(lizbeth);
			      throw new Exception();
			    } catch (Exception e) {
			    	lizbeth = new User();
			    	lizbeth.setFirstname("Liliana Lizbeth");
			    	lizbeth.setLastname("Arroyo Saucedo");
			    	lizbeth.setEmail("lizbeth@aditivo.mx");
			    	lizbeth.getSecuritySettings().setRole(Role.STORE);
			    	lizbeth.getSecuritySettings()
			          .setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
			    	lizbeth.getSecuritySettings()
			          .setStores(Arrays.asList("3338e021-59c4-4482-9603-fac42d656c7b",
			        		  "78416e3d-2274-4a24-9186-3616588f6197",
			        		  "9beaf247-e674-47a2-9d4c-c550bb1aa7cc",
			        		  "129f18c1-c531-4488-9125-6d4e4ccf6d4d",
			        		  "f33140b3-3ecd-4d70-bfcc-159f47ac9058"));
			    	lizbeth.setKey((Key) keyHelper.obtainKey(User.class, "lizbeth@aditivo.mx"));
			      userDao.create(lizbeth);
			      log.log(Level.INFO, "Inserted Lizbeth Aditivo...");
			    }
			  try {
				    um = userMenuDao.get("lizbeth@aditivo.mx", true);
				    userMenuDao.delete("lizbeth@aditivo.mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    um = new UserMenu();
				    um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				    um.setKey(userMenuDao.createKey("lizbeth@aditivo.mx"));
				    userMenuDao.create(um);
				  }
				
			
		    User aditivo= null;
		    UserMenu um = null;
		    
			try {
				aditivo = userDao.get("aditivofranquiciascelia_mx", true);
				  userDao.delete("aditivofranquiciascelia_mx");
				  throw new Exception();
				} catch( Exception e ) {
				    log.log(Level.INFO, "Inserting aditivo...");
				    aditivo = new User();
				    aditivo.setFirstname("Aditivo Celia");
				    aditivo.setLastname("Mexico");
				    aditivo.setEmail("aditivocelia2@getin.mx");
				    aditivo.getSecuritySettings().setRole(Role.STORE);
				    aditivo.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				    aditivo.getSecuritySettings().setStores(Arrays.asList("07acea43-4a6c-4adc-896c-00b1f4781242",
				    		"0867dbd4-53f1-4602-8a48-e0c07bd752da",
				    		"2fea9047-6da8-4493-b97c-f3cdd809a18d",
				    		"75746850-9ae4-4aa4-bf8f-3bf01daf2775",
				    		"800f4a09-34f7-4116-bf5c-4b0ab45175c8"));
				    aditivo.setKey((Key)keyHelper.obtainKey(User.class, "aditivofranquiciascelia_mx"));
				  userDao.create(aditivo);
				    log.log(Level.INFO, "Inserting cloe...");

				}
			  try {
				    um = userMenuDao.get("aditivofranquiciascelia_mx", true);
				    userMenuDao.delete("aditivofranquiciascelia_mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    um = new UserMenu();
				    um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				    um.setKey(userMenuDao.createKey("aditivofranquiciascelia_mx"));
				    userMenuDao.create(um);
				  }
				  
				  */
			
		    User coastv= null;
		    UserMenu um = null;
		    
			try {
				coastv = userDao.get("tiendas@98coastav.mx", true);
				  userDao.delete("tiendas@98coastav.mx");
				  throw new Exception();
				} catch( Exception e ) {
				    log.log(Level.INFO, "Inserting 98 Coast...");
				    coastv = new User();
				    coastv.setFirstname("98 Coast av");
				    coastv.setLastname("Mexico");
				    coastv.setEmail("tiendas@98coastav.mx");
				    coastv.getSecuritySettings().setRole(Role.STORE);
				    coastv.getSecuritySettings().setPassword("4B3785A1117F3F2B6700CF71B3A6DF0CC7BD8D11F2296A1054FB52CB102B5BB1");
				    coastv.getSecuritySettings().setStores(Arrays.asList("83d06e8f-4ca8-4a92-bf48-b796bc24ac50",
				    		"2edff7a5-6000-45b1-9425-a7712e133d80",
				    		"510b13bc-c316-42e1-853a-38dcf8855746",
				    		"f03d2386-3c1e-40c7-9005-d402f565f107",
				    		"a98dff46-f725-4e96-8a7f-6f74347c7ab1",
				    		"0d8d0802-b950-4450-90ce-9742cef0ad3b"));
				    coastv.setKey((Key)keyHelper.obtainKey(User.class, "tiendas@98coastav.mx"));
				  userDao.create(coastv);
				    log.log(Level.INFO, "Inserting 98 Coast av...");

				}
			  try {
				    um = userMenuDao.get("tiendas@98coastav.mx", true);
				    userMenuDao.delete("tiendas@98coastav.mx");
				    throw new Exception();
				  } catch( Exception e ) {
				    um = new UserMenu();
				    um.getEntries().add(new UserMenuEntry("index.apdvisits", "fa-area-chart", "Tráfico"));
				    um.setKey(userMenuDao.createKey("tiendas@98coastav.mx"));
				    userMenuDao.create(um);
				  }
			
		}
		catch(Exception e){
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
		}

}
