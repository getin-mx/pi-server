package mobi.allshoppings.cli;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.ibm.icu.text.SimpleDateFormat;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.EntityKind;


public class Test extends AbstractCLI {

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

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");

			List<APDAssignation> apdas = apdaDao.getUsingEntityIdAndEntityKindAndDate("1480009555292", EntityKind.KIND_STORE, sdf.parse("2017-01-01"));
			System.out.println(apdas.size());
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
