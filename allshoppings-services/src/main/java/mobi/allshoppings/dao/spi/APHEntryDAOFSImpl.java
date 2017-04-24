package mobi.allshoppings.dao.spi;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class APHEntryDAOFSImpl extends GenericDAOFS<APHEntry> implements APHEntryDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APHEntryDAOFSImpl.class.getName());

	private static final DecimalFormat df4 = new DecimalFormat("0000");
	private static final DecimalFormat df2 = new DecimalFormat("00");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	private String baseDir;
	
	public APHEntryDAOFSImpl() {
		super(APHEntry.class);
	}

	@Override
	public Key createKey(APHEntry obj) throws ASException {
		String hashKey = obj.getMac() + ":" + obj.getHostname() + ":" + obj.getDate();
		return keyHelper.obtainKey(APHEntry.class, hashKey);
	}
	
	@Override
	public String resolveFileName(String identifier) throws ASException {
		String p[] = identifier.split(":");
		if( p.length < 8 ) throw ASExceptionHelper.invalidArgumentsException(identifier);

		try {
			StringBuffer nsb = new StringBuffer();
			for( int i = 0; i < 6; i++ ) {
				nsb.append(p[i]);
			}

			String hostname = p[6];
			String stringDate = p[7];
			Date date = sdf.parse(stringDate);
			String filename = nsb.append(".json").toString().toLowerCase();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			if( baseDir == null )
				baseDir = systemConfiguration.getDefaultDumpDirectory();

			StringBuffer sb = new StringBuffer(baseDir);
			if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
			
			sb.append(df4.format(cal.get(Calendar.YEAR))).append(File.separator);
			sb.append(df2.format(cal.get(Calendar.MONTH) + 1)).append(File.separator);
			sb.append(df2.format(cal.get(Calendar.DATE))).append(File.separator);
			sb.append("APHEntry").append(File.separator);
			sb.append(hostname).append(File.separator);
			sb.append(filename);
			
			return sb.toString();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<APHEntry> getUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException {

		List<APHEntry> ret = CollectionFactory.createList();
		
		Date curDate = new Date(fromDate.getTime());
		while(curDate.before(toDate) || curDate.equals(toDate)) {
			
			for( String host : hostname ) {
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(curDate);
				
				if( baseDir == null )
					baseDir = systemConfiguration.getDefaultDumpDirectory();

				StringBuffer sb = new StringBuffer(baseDir);
				if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
				
				sb.append(df4.format(cal.get(Calendar.YEAR))).append(File.separator);
				sb.append(df2.format(cal.get(Calendar.MONTH) + 1)).append(File.separator);
				sb.append(df2.format(cal.get(Calendar.DATE))).append(File.separator);
				sb.append("APHEntry").append(File.separator);
				sb.append(host).append(File.separator);

				File folder = new File(sb.toString());
			    File[] files = folder.listFiles();
			    if(files!=null) { //some JVMs return null for empty dirs
			        for(File f: files) {
			            if(!f.isDirectory()) {
			            	ret.add(deserialize(f));
			            }
			        }
			    }
			}
			
			curDate = new Date(curDate.getTime() + 86400000);
		}
		
		return ret;
		
	}

	@Override
	public List<String> getMacsUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate) throws ASException {

		Set<String> ret = CollectionFactory.createSet();
		
		Date curDate = new Date(fromDate.getTime());
		while(curDate.before(toDate) || curDate.equals(toDate)) {
			
			for( String host : hostname ) {
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(curDate);
				
				if( baseDir == null )
					baseDir = systemConfiguration.getDefaultDumpDirectory();

				StringBuffer sb = new StringBuffer(baseDir);
				if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
				
				sb.append(df4.format(cal.get(Calendar.YEAR))).append(File.separator);
				sb.append(df2.format(cal.get(Calendar.MONTH) + 1)).append(File.separator);
				sb.append(df2.format(cal.get(Calendar.DATE))).append(File.separator);
				sb.append("APHEntry").append(File.separator);
				sb.append(host).append(File.separator);

				File folder = new File(sb.toString());
			    File[] files = folder.listFiles();
			    if(files!=null) { //some JVMs return null for empty dirs
			        for(File f: files) {
			            if(!f.isDirectory()) {
			            	ret.add(deserialize(f).getMac().toLowerCase());
			            }
			        }
			    }
			}
			
			curDate = new Date(curDate.getTime() + 86400000);
		}
		
		List<String> ret2 = CollectionFactory.createList();
		ret2.addAll(ret);
		return ret2;

	}

}
