package mobi.allshoppings.bdb.tools;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.tools.CSVMarshaller;
import mobi.allshoppings.tools.CollectionFactory;

public class CSVAPDeviceExport {

	@Autowired
	private APDeviceDAO apdeviceDao;
	
	public byte[] createCSVRepresentation(String authToken, String status) throws ASException {
		
		CSVMarshaller marshaller = new CSVMarshaller();
		marshaller.setHeaders(Arrays.asList(
				"identifier",
				"hostname",
				"description",
				"area",
				"model",
				"mode",
				"version",
				"tunnelIp",
				"lanIp",
				"wanIp",
				"publicIp",
				"lastInfoUpdate",
				"external",
				"visitTimeThreshold",
				"visitGapThreshold",
				"visitPowerThreshold",
				"visitMaxThreshold",
				"peasantPowerThreshold",
				"visitCountThreshold",
			    "timezone",
			    "visitsOnMon",
			    "visitsOnTue",
			    "visitsOnWed",
			    "visitsOnThu",
			    "visitsOnFri",
			    "visitsOnSat",
			    "visitsOnSun",
			    "visitStartMon",
			    "visitEndMon",
			    "visitStartTue",
			    "visitEndTue",
			    "visitStartWed",
			    "visitEndWed",
			    "visitStartThu",
			    "visitEndThu",
			    "visitStartFri",
			    "visitEndFri",
			    "visitStartSat",
			    "visitEndSat",
			    "visitStartSun",
			    "visitEndSun",
			    "monitorStart",
			    "monitorEnd",
			    "passStart",
			    "passEnd",
				"status",
				"creationDateTime",
				"lastRecordDate",
				"lastRecordCount",
				"lastUpdate"
				));
		
		List<Integer> statusList = CollectionFactory.createList();
		if(StringUtils.hasText(status))
			statusList.add(Integer.valueOf(status));
		
		List<APDevice> list = apdeviceDao.getUsingStatusAndRange(statusList, null, "hostname");
		StringBuffer sb = new StringBuffer();

		sb.append(marshaller.marshallHeaders());

		for( APDevice dev : list ) {
			try {
				sb.append(marshaller.marshall(dev));
			} catch( Exception e ) {}
		}
			
		return sb.toString().getBytes();
	}
}
