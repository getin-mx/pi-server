package mobi.allshoppings.dump.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mobi.allshoppings.dump.DumperFileNameResolver;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;

public class LegacyFileNameResolver implements DumperFileNameResolver<ModelKey> {

	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final SimpleDateFormat day = new SimpleDateFormat("dd");
	private static final SimpleDateFormat hour = new SimpleDateFormat("HH");

	public LegacyFileNameResolver() {
		super();
		TimeZone tz = TimeZone.getTimeZone("CDT");
		year.setTimeZone(tz);
		month.setTimeZone(tz);
		day.setTimeZone(tz);
		hour.setTimeZone(tz);
	}

	@Override
	public boolean isAvailableFor(ModelKey element) {
		return true;
	}
	
	@Override
	public String resolveDumpFileName(String baseDir, String baseName, Date forDate, ModelKey element, String filter) {
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
		sb.append(baseName).append(".json");

		return sb.toString();
	}

	@Override
	public boolean mayHaveMultiple() {
		return false;
	}

	@Override
	public List<String> getMultipleFileOptions(String baseDir, String baseName, Date forDate ) {
		List<String> ret = CollectionFactory.createList();
		return ret;
	}
	
}
