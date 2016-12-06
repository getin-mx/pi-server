package mobi.allshoppings.cinepolis.services;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.spi.DashboardIndicatorDataDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;


public class DashboardExporterService {

	private static final Logger log = Logger.getLogger(DashboardExporterService.class.getName());

	private static final int KIND_BRAND = 1; 
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static long TWENTY_FOUR_HOURS = 86400000;

	private DashboardIndicatorDataDAO dao = new DashboardIndicatorDataDAOJDOImpl();

	@SuppressWarnings("resource")
	public void doExport(String elementId, String subentityId, Date fromDate, Date toDate, File outFile) throws ASException {

		List<Map<String, String>> results = CollectionFactory.createList();
		List<String> rows = CollectionFactory.createList();
		Date thisDate = new Date(fromDate.getTime());

		// Prepare phase
		while(thisDate.before(toDate) || thisDate.equals(toDate)) {
			try {
				List<DashboardIndicatorData> list = dao.getUsingFilters(
						"cinepolis_mx", KIND_BRAND, elementId, null, null, subentityId,
						DashboardIndicatorData.PERIOD_TYPE_DAILY,
						sdf.format(thisDate), sdf.format(thisDate), null, null,
						null, null, null, null, null, null);

				Map<String, Double> tmpElement = CollectionFactory.createMap();
				for(DashboardIndicatorData obj : list) {
					Double val = tmpElement.get(obj.getElementSubName());
					if( val == null ) val = 0D;
					val += obj.getDoubleValue();
					tmpElement.put(obj.getElementSubName(), val);
				}

				Map<String, String> element = CollectionFactory.createMap();
				element.put("Fecha", sdf.format(thisDate));
				Iterator<String> i = tmpElement.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					if(!rows.contains(key)) rows.add(key);
					element.put(key, String.valueOf(tmpElement.get(key)));
				}

				results.add(element);

			} catch( ASException e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			
			thisDate = new Date(thisDate.getTime() + TWENTY_FOUR_HOURS);
		}

		// Write phase
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			
			// Rows
			StringBuffer sb = new StringBuffer();
			sb.append("\"Fecha\"");
			for( String row : rows ) {
				sb.append(";\"").append(row).append("\"");
			}
			sb.append("\n");
			fos.write(sb.toString().getBytes());
			
			// lines
			for(Map<String, String> element : results ) {
				sb = new StringBuffer();
				sb.append("\"").append(element.get("Fecha")).append("\"");
				for( String row : rows ) {
					sb.append(";\"").append(StringUtils.hasText(element.get(row)) ? element.get(row) : "0.0").append("\"");
				}
				sb.append("\n");
				fos.write(sb.toString().getBytes());
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}
}
