package mobi.allshoppings.dump.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobi.allshoppings.dump.DumperFileNameResolver;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.interfaces.ModelKey;

public class APHotspotFileNameResolver implements DumperFileNameResolver<ModelKey> {

	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final SimpleDateFormat day = new SimpleDateFormat("dd");
	private static final SimpleDateFormat hour = new SimpleDateFormat("HH");

	public APHotspotFileNameResolver() {
		super();
	}

	@Override
	public boolean isAvailableFor(ModelKey element) {
		if( element instanceof APHotspot ) return true;
		return false;
	}

	@Override
	public String resolveDumpFileName(String baseDir, String baseName, Date forDate, ModelKey element) {
		String myYear = year.format(forDate);
		String myMonth = month.format(forDate);
		String myDay = day.format(forDate);
		String myHour = hour.format(forDate);

		StringBuffer sb = new StringBuffer();
		sb.append(baseDir);
		if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
		sb.append(myYear).append(File.separator);
		sb.append(myMonth).append(File.separator);
		sb.append(myDay).append(File.separator);
		sb.append(myHour).append(File.separator);
		sb.append(baseName).append(File.separator);
		sb.append(((APHotspot)element).getHostname()).append(".json");

		return sb.toString();
	}

}
