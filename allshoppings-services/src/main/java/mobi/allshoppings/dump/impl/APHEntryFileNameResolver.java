package mobi.allshoppings.dump.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import mobi.allshoppings.dump.CloudFileManager;
import mobi.allshoppings.dump.DumperFileNameResolver;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;

public class APHEntryFileNameResolver implements DumperFileNameResolver<ModelKey> {

	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final SimpleDateFormat day = new SimpleDateFormat("dd");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public APHEntryFileNameResolver() {
		super();
		TimeZone tz = TimeZone.getTimeZone("GMT");
		year.setTimeZone(tz);
		month.setTimeZone(tz);
		day.setTimeZone(tz);
		sdf.setTimeZone(tz);
	}

	@Override
	public boolean isAvailableFor(ModelKey element) {
		if( element instanceof APHEntry ) return true;
		return false;
	}
	
	@Override
	public String resolveDumpFileName(String baseDir, String baseName, Date forDate, ModelKey element, String filter) {

		String myYear = year.format(forDate);
		String myMonth = month.format(forDate);
		String myDay = day.format(forDate);

		StringBuffer sb = new StringBuffer();

		if( element == null && !StringUtils.hasText(filter) ) {
			sb.append(baseDir);
			if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
			sb.append(myYear).append(File.separator);
			sb.append(myMonth).append(File.separator);
			sb.append(myDay).append(File.separator);
			sb.append(baseName).append(".json");
		} else {
			if( element != null ) {
				try {
					forDate = sdf.parse(((APHEntry)element).getDate());
					myYear = year.format(forDate);
					myMonth = month.format(forDate);
					myDay = day.format(forDate);
				} catch (ParseException e) {}
			}
			sb.append(baseDir);
			if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
			sb.append(myYear).append(File.separator);
			sb.append(myMonth).append(File.separator);
			sb.append(myDay).append(File.separator);
			sb.append(baseName).append(File.separator);
			if( element != null )
				sb.append(((APHEntry)element).getHostname()).append(".json");
			else 
				sb.append(filter).append(".json");
		}
		
		return sb.toString();
	}

	@Override
	public boolean mayHaveMultiple() {
		return true;
	}

	@Override
	public List<String> getMultipleFileOptions(String baseDir, String baseName, Date forDate, CloudFileManager cfm ) throws ASException {

		List<String> ret = CollectionFactory.createList();
		
		String myYear = year.format(forDate);
		String myMonth = month.format(forDate);
		String myDay = day.format(forDate);

		StringBuffer sb = new StringBuffer();
		sb.append(baseDir);
		if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
		sb.append(myYear).append(File.separator);
		sb.append(myMonth).append(File.separator);
		sb.append(myDay).append(File.separator);
		sb.append(baseName).append(File.separator);

		if( cfm == null ) {

			File dir = new File(sb.toString());
			if( dir.exists() && dir.isDirectory() ) {
				String [] names = dir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if( name.endsWith(".json"))
							return true;
						return false;
					}
				});

				for(String name : names ) {
					ret.add(sb.toString() + name);
				}
			}

		} else {
			List<String> tmp = cfm.getDirectoryListing(sb.toString());
			ret.clear();
			if( !baseDir.endsWith(File.separator)) 
				baseDir = baseDir + File.separator;
			for( String t : tmp ) {
				ret.add(baseDir + t);
			}
		}
		
		return ret;
	}
	
}
