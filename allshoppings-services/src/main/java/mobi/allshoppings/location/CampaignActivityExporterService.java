package mobi.allshoppings.location;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;

import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class CampaignActivityExporterService {

	private static final Logger log = Logger.getLogger(CampaignActivityExporterService.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void exportCampaignActivities(String brandId, Date fromDate, Date toDate, File outFile) throws ASException {

		try {
			FileOutputStream fos = new FileOutputStream(outFile);

			long counter = 0;
			long results = 0;

			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			final BasicDBObject query = new BasicDBObject("$and", Arrays.asList(new BasicDBObject("creationDateTime", new BasicDBObject("$gte", fromDate)), new BasicDBObject("creationDateTime", new BasicDBObject("$lte", toDate))));
			DBCursor c1 = db.getCollection("CampaignActivity").find(query);
			c1.sort(new BasicDBObject("creationDateTime", 1));
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c1.iterator();

			StringBuffer sb = new StringBuffer();
			sb.append("\"ID\",");
			sb.append("\"Type\",");
			sb.append("\"Device\",");
			sb.append("\"Platform\",");
			sb.append("\"DeviceName\",");
			sb.append("\"DeviceVersion\",");
			sb.append("\"Date\",");
			sb.append("\"RedeemStatus\",");
			sb.append("\"ViewedTime\",");
			sb.append("\"Viewed\",");
			sb.append("\"ChangeTime\",");
			sb.append("\"Change\",");
			sb.append("\"Count\"");
			sb.append("\n");
			fos.write(sb.toString().getBytes());

			
			// Fetches the activity list
			while(i.hasNext()) {
				DBObject dbo = i.next();
				Key key = new Key(dbo.get("_id").toString());
				counter++;
				
				try {
					
					// Finds if it has an asociated user & device
					if( dbo.containsField("userId") && dbo.containsField("brandId") && dbo.get("brandId").toString().equals(brandId) ) {
						BasicDBObject deviceQuery = new BasicDBObject(new BasicDBObject("userId", dbo.get("userId")));						
						DBCursor cDevice = db.getCollection("DeviceInfo").find(deviceQuery);
						cDevice.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
						Iterator<DBObject> iDevice = cDevice.iterator();
						while(iDevice.hasNext()) {
							DBObject dbDevice = iDevice.next();
							Key deviceKey = new Key(dbDevice.get("_id").toString());
							results++;
							
							sb = new StringBuffer();
							sb.append("\"").append(key.getName()).append("\",");
							sb.append("\"").append(dbo.get("promotionType")).append("\",");
							sb.append("\"").append(deviceKey.getName()).append("\",");
							sb.append("\"").append(dbDevice.get("devicePlatform")).append("\",");
							sb.append("\"").append(dbDevice.get("deviceName")).append("\",");
							sb.append("\"").append(dbDevice.get("deviceVersion")).append("\",");

							Date creationDateTime = (Date)dbo.get("creationDateTime");
							Date viewDateTime = dbo.containsField("viewDateTime") ? (Date)dbo.get("viewDateTime") : null; 
							Date statusChangeDateTime = dbo.containsField("statusChangeDateTime") ? (Date)dbo.get("statusChangeDateTime") : null;

							sb.append(sdf.format(creationDateTime)).append(",");
							sb.append(dbo.get("redeemStatus")).append(",");

							if( viewDateTime != null ) {
								sb.append(viewDateTime.getTime() - creationDateTime.getTime()).append(",");
								sb.append("1,");
							} else {
								sb.append("0,");
								sb.append("0,");
							}

							if( statusChangeDateTime != null ) {
								sb.append(statusChangeDateTime.getTime() - creationDateTime.getTime()).append(",");
								sb.append("1,");
							} else {
								sb.append("0,");
								sb.append("0,");
							}
							
							sb.append("1");
							sb.append("\n");

							fos.write(sb.toString().getBytes());
							break;
						}
					}	
				} catch( Exception e ) {
					log.log(Level.WARNING, e.getMessage(), e);
				}

				if( counter % 100 == 0 )
					log.log(Level.INFO, "Processed " + counter + " registers with " + results + " results!");
			}

			jdoConn.close();
			pm.currentTransaction().commit();
			pm.close();
			fos.close();

			log.log(Level.INFO, "Process Finished! Processed " + counter + " registers with " + results + " results!");

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
}
