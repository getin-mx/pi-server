package mobi.allshoppings.cli;

import java.io.File;
import java.text.SimpleDateFormat;

import joptsimple.OptionParser;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.location.CampaignActivityExporterService;


public class ExportCampaignActivity extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			File f1 = new File("/tmp/exportActivity.csv");

			CampaignActivityExporterService service = new CampaignActivityExporterService();
			service.exportCampaignActivities("cinepolis_mx", sdf.parse("2015-06-26"), sdf.parse("2015-09-01"), f1);
			
			System.exit(0);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}
}
