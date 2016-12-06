package mobi.allshoppings.cinepolis.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;

import org.springframework.util.StringUtils;

public class VistaLoyaltyImporterService {

	private static final Logger log = Logger.getLogger(VistaDataService.class.getName());
	private static final String BRAND = "cinepolis_mx";

	private VoucherDAO dao = new VoucherDAOJDOImpl();

	public void doImport(File file, String brandId, String type, Integer forcedStatus) throws ASException {

		try {
			// Get file totals
			long total = getTotals(file);

			log.log(Level.INFO, "Updating " + total + " vouchers");

			// Read the selected file
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			long count = 0;
			while ((line = reader.readLine()) != null) {

				boolean newObject = false;
				line = line.trim();
				if(StringUtils.hasText(line)) {
					
					String parts[] = line.split(",");

					// For each line, we add a new voucher
					Voucher obj;
					try {
						obj = dao.get(parts[1], true);
					} catch( ASException e ) {
						obj = new Voucher();
						obj.setBrandId(BRAND);
						obj.setCode(parts[1]);
						obj.setKey(dao.createKey(parts[1]));
						obj.setType(parts[1].substring(0,5));
						obj.setSubcode1(parts[0]);
						obj.setSubcode2(parts[2]);
						if( forcedStatus != null ) {
							obj.setStatus(forcedStatus);
						}
						newObject = true;
					}

					if( newObject ) {
						dao.create(obj);
					} else {
						dao.update(obj);
					}

					count++;
					if( count % 20 == 0 ) {
						log.log(Level.INFO, count + " of " + total + " vouchers updated");
					}
				}
			}

			log.log(Level.INFO, "Process finished with "+ count + " vouchers updated");
			reader.close();

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}

	public long getTotals(File file) throws Exception {
		// Read the selected file
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
		long count = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			if( StringUtils.hasText(line)) {
				count++;
			}
		}
		return count;
	}
}
