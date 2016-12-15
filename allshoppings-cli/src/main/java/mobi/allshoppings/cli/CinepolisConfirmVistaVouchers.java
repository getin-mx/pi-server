package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.cinepolis.services.ResetVistaVouchersService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;


public class CinepolisConfirmVistaVouchers extends AbstractCLI {

	private static final Logger log = Logger.getLogger(CinepolisConfirmVistaVouchers.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "status", "Status to confirm: default is 3 (redeemed)" ).withRequiredArg().ofType( Integer.class );
		return parser;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			Integer statusSelected = 1;

			try {
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				if( options.has("status")) statusSelected = (Integer)options.valueOf("status");

				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse(sdf.format(new Date(new Date().getTime() - 43200000 /* 12 hours */)));
				}

				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date(fromDate.getTime());
				}

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Confirming Cinepolis Vista Vouchers from " + fromDate + " to " + toDate);
			List<String> types = Arrays.asList(new String[] {
					"2D", "3D"
			});
			List<Integer> status = Arrays.asList(new Integer[] {statusSelected});
			
			VoucherDAO dao = new VoucherDAOJDOImpl();

//			List<Voucher> vouchers = dao.getUsingDatesAndType(fromDate, toDate, types, null, "code");
			List<Voucher> vouchers = dao.getUsingStatusAndBrandAndType(status, "cinepolis_mx", types);
			ResetVistaVouchersService rs = new ResetVistaVouchersService();
			for( Voucher voucher : vouchers ) {
				if( voucher.getAssignationDate() == null || voucher.getAssignationDate().after(fromDate) && voucher.getAssignationDate().before(toDate)) {
					rs.confirmVoucher(voucher.getCode());
				}
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

}
