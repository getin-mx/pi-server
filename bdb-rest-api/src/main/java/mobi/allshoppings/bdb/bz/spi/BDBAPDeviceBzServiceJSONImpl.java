package mobi.allshoppings.bdb.bz.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.StatusAware;

public class BDBAPDeviceBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APDevice> implements BDBCrudBzService {

	@Autowired
	private APDeviceDAO dao;

	@Autowired
	private APDeviceHelper apdeviceHelper;

	@Override
	public String[] getMandatoryUpdateFields() {
		return new String[] {
				"identifier",
				"hostname"
		};
	}

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"hostname",
				"description",
				"lastRecordDate",
				"reportable",
				"reportStatus",
				"model",
				"mode",
				"version",
				"status",
				"reportable",
				"reportStatus"
		};
	}
	
	@Override
	public void postChange(APDevice obj) throws ASException {
		try {
			if( obj.getStatus().equals(StatusAware.STATUS_DISABLED))
				apdeviceHelper.unassignUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void postDelete(APDevice obj) throws ASException {
		try {
			apdeviceHelper.unassignUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(APDevice.class);
	}

	@Override
	public void setKey(APDevice obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(seed.getString("hostname")));
	}
	@Override
	public String change(JsonRepresentation entity) {
		if( myDao == null ) config();
		long start = markStart();
		Date datef = new Date();
		DateFormat frmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {

			// validate authToken
			final User user = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			// check mandatory fields
			log.info("check mandatory fields");
			checkMandatoryFields(obj, getMandatoryUpdateFields());

			final String identifier = obtainLowerCaseIdentifierFromJSON(obj);

			APDevice modObj = myDao.get(identifier, true);
			modObj.setVisitMaxThreshold(obj.getLong("visitMaxThreshold"));
			JSONArray mails = obj.getJSONArray("reportMailList");
			ArrayList<String> reportMail = new ArrayList<>();
			for(int i = 0; i < mails.length(); i++) reportMail.add(mails.getString(i));
			modObj.setReportMailList(reportMail	);
			modObj.setVisitEndFri(obj.getString("visitEndFri"));
			modObj.setVisitStartSun(obj.getString("visitStartSun"));
			modObj.setVisitStartWed(obj.getString("visitStartWed"));
			modObj.setVisitCountThreshold(obj.getLong("visitCountThreshold"));
			//modObj.setLat(obj.getDouble("lat"));
			modObj.setVisitEndThu(obj.getString("visitEndThu"));
			modObj.setDoIndexNow(obj.getBoolean("doIndexNow"));
			modObj.setVisitEndTue(obj.getString("visitEndTue"));
			modObj.setVisitsOnFri(obj.getBoolean("visitsOnFri"));
			modObj.setVisitEndMon(obj.getString("visitEndMon"));
			modObj.setViewerPowerThreshold(obj.getLong("viewerPowerThreshold"));
			modObj.setVisitStartFri(obj.getString("visitStartFri"));
			modObj.setMonitorStart(obj.getString("monitorStart"));
			//modObj.setExternal(obj.getBoolean("external"));
			modObj.setOffsetView(obj.getInt("offsetViewer"));
			modObj.setLastUpdate(datef);
			modObj.setVisitsOnMon(obj.getBoolean("visitsOnMon"));
			//modObj.setStatus(obj.getInt("status"));
			modObj.setVisitsOnTue(obj.getBoolean("visitsOnTue"));
			modObj.setVisitStartMon(obj.getString("visitStartMon"));
			modObj.setPassEnd(obj.getString("passEnd"));
			modObj.setVisitsOnThu(obj.getBoolean("visitsOnThu"));
			modObj.setMonitorEnd(obj.getString("monitorEnd"));
			modObj.setVisitEndSat(obj.getString("visitEndSat"));
			modObj.setVisitGapThreshold(obj.getLong("visitGapThreshold"));
			//modObj.setDescription(obj.getString("description"));
			//modObj.setLon(obj.getDouble("lon"));
			modObj.setViewerMinTimeThreshold(obj.getInt("viewerMinTimeThreshold"));
			modObj.setVisitDecay(obj.getLong("visitDecay"));
			modObj.setVisitEndWed(obj.getString("visitEndWed"));
			modObj.setPeasantDecay(obj.getLong("peasantDecay"));
			modObj.setVisitStartTue(obj.getString("visitStartTue"));
			modObj.setVisitStartThu(obj.getString("visitStartThu"));
			modObj.setVisitsOnSat(obj.getBoolean("visitsOnSat"));
			modObj.setVisitTimeThreshold(obj.getLong("visitTimeThreshold"));
			modObj.setPassStart(obj.getString("passStart"));
			modObj.setVisitEndSun(obj.getString("visitEndSun"));
			modObj.setPeasantPowerThreshold(obj.getLong("peasantPowerThreshold"));
			modObj.setViewerMaxTimeThreshold(obj.getInt("viewerMaxTimeThreshold"));
			modObj.setVisitsOnWed(obj.getBoolean("visitsOnWed"));
			modObj.setRepeatThreshold(obj.getInt("repeatThreshold"));
			modObj.setVisitPowerThreshold(obj.getLong("visitPowerThreshold"));
			modObj.setVisitStartSat(obj.getString("visitStartSat"));
			modObj.setVisitsOnSun(obj.getBoolean("visitsOnSun"));
			
			//preModify(modObj, obj);
			//setPropertiesFromJSONObject(obj, modObj, EMPTY_SET);
			//prePersist(modObj, obj);

			myDao.update(modObj);
			log.info("object updated");
			postChange(modObj);

			// index object if needed
			if( modObj instanceof Indexable ) {
				indexHelper.indexObject(modObj);
				log.info("object indexed: " + modObj.getIdentifier());
			}

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getName() + "BzService.put"), 
					null, null);

			return getJSONRepresentationFromObject(modObj, obtainOutputFields(myClazz)).toString();
			
		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (Exception e) {
			if( e instanceof ASException && ((ASException)e).getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE)
				return getJSONRepresentationFromException(ASExceptionHelper.notFoundException()).toString();
			
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

	}

}
