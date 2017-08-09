package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class TestExcelReport extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			final ExcelExportHelper helper = (ExcelExportHelper)getApplicationContext().getBean("excel.export.helper");
			final Logger log = Logger.getLogger(TestExcelReport.class.getName());
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			//final String STORE_ID = "1478641696271"; // Botanicus Angelopolis
			//final String STORE_ID = "1473220851475"; // Adolfo Dominguez interlomas
			//final String STORE_ID = "b52192c9-37aa-464d-9243-81eb7cf51124"; // Areas Mexico A70
			// final List<String> storeIds = Arrays.asList("ec9cec0d-d8a1-488e-b3e2-bf4b4f667b66","aebd6f9e-158e-469a-8c43-2668677a4edb","b52192c9-37aa-464d-9243-81eb7cf51124","bcd4d47a-432b-4fab-8391-83f56080ebd4");
			// final List<String> storeIds = Arrays.asList("d40631ed-c981-4be9-8585-b74fe552832e","1471039822790","1471039822813","c0919283-ef3b-4a62-b5e6-22a3ce9d5271","d2cfebcb-2cfd-4b63-90a1-8235b5d51e1d","8aeea969-57d3-4bd5-bddb-dd98caf1fc00","39132498-241e-4efb-a416-06f37f2f5b10","1473220850997");
			// final List<String> storeIds = Arrays.asList("d030cc71-91c1-46a1-9309-bbf4ddcfa393","d40631ed-c981-4be9-8585-b74fe552832e","1471039822790","1471039822813","c0919283-ef3b-4a62-b5e6-22a3ce9d5271","d2cfebcb-2cfd-4b63-90a1-8235b5d51e1d","8aeea969-57d3-4bd5-bddb-dd98caf1fc00","39132498-241e-4efb-a416-06f37f2f5b10","9f5047d7-3077-4ec6-b7f9-4ee71fbb1797","33afb27e-a6aa-4ae1-a911-578554cdb335","6a434d5a-7e2c-44c5-a9e0-111335527aec","256991a7-df84-4cde-8f3f-82aa24431bd8","00d861df-a9e4-42ff-b76c-057a73398bf4"); // roku y fullsand y casablanca
			// final List<String> storeIds = Arrays.asList("d030cc71-91c1-46a1-9309-bbf4ddcfa393"); // roku
//			final List<String> storeIds = Arrays.asList("39132498-241e-4efb-a416-06f37f2f5b10","d40631ed-c981-4be9-8585-b74fe552832e"); // fullsand cancun y playa
//			final List<String> storeIds = Arrays.asList("0804ce51-a635-40be-8952-28cc25c946dd","b6b1a93f-0116-4d9a-ad40-9c6842eaa8c0","c0d2ccb9-0ed4-45f8-8f4c-770ddb4495b4","5cad0cb3-196a-4f3f-b4f4-224f610f6467","f718f26a-c6de-4149-9605-d737450b7bd9","e43a9f92-7db7-46d1-8a74-3f629eea2b47","40b7b23d-f2b4-4f70-be05-a0ad51ce44ba","fb111c5b-db32-4129-a09a-f15269f57285","dcb748f6-a060-43da-9a13-aea9ca02245a","791d303b-3f45-4037-a6f4-2ba9c1e15c75","958d7395-fb37-4b8b-a716-b2c1a9ffdb9f","440dfccd-73b3-48a5-98c8-d893a01a085f","247b4ead-822f-4713-9465-177666b2e31c","3a361ad5-9748-4bfa-9a69-460fd8214e6e","23b16093-fbbf-4d9a-811b-6c82bd0eb940","01bf63e5-6b31-4bf9-beb6-2a9dcbb8a304","9bbf47a2-5a32-4ae3-b217-858c7c1e2703","115e5c3c-7850-4e3e-82d1-16b15b5256a5","41e68dc9-254d-4803-82b7-c083eeaf28df","9263926c-88e3-435b-ad7e-1920abfb73a6","649c6de9-05aa-40de-bd39-7b1d37921658");
			final List<String> storeIds = Arrays.asList("d3b6966e-c922-4e70-a473-9a549ed0ee89","e4916614-3cb8-4fc2-a58c-8b16c63d966c","d030cc71-91c1-46a1-9309-bbf4ddcfa393","7665f9e2-c66a-43ad-aa0e-8fc93feffb4e","ec9cec0d-d8a1-488e-b3e2-bf4b4f667b66","bcd4d47a-432b-4fab-8391-83f56080ebd4","aebd6f9e-158e-469a-8c43-2668677a4edb","b52192c9-37aa-464d-9243-81eb7cf51124","94b9e9fc-3f73-4926-9890-fe0d924952fc","e0bb9d40-7639-47d0-ab38-135b280ac769"); 
			final String FROM_DATE = "2017-01-02"; // Retail Calendar initial 2017 day
			final String TO_DATE = "2017-07-30"; // Retail Calendar final march day
			final String outDir = "/usr/local/allshoppings/dump/";

			final List<Thread> tList = CollectionFactory.createList();
			
			for( final String store : storeIds ) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							helper.export(store, FROM_DATE, TO_DATE, 5, outDir);
						} catch (ASException e) {
							e.printStackTrace();
						}
					}
				});
				t.setName("Thread-" + store);
				tList.add(t);
				t.start();
			}
			
			for( Thread t : tList ) {
				t.join();
			}
			
			log.log(Level.INFO, "Process Finished!");
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
