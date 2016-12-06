package mobi.allshoppings.cinepolis.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.cinepolis.vista.voucher.VistaVoucherService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;

import org.springframework.util.StringUtils;

public class VistaVoucherImporterService {

	private static final Logger log = Logger.getLogger(VistaDataService.class.getName());
	private static final String BRAND = "cinepolis_mx";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	private VoucherDAO dao = new VoucherDAOJDOImpl();
	private VistaVoucherService vs = VistaVoucherService.getInstance();

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

					// For each line, we add a new voucher
					Voucher obj;
					try {
						obj = dao.get(line, true);
					} catch( ASException e ) {
						obj = new Voucher();
						obj.setBrandId(BRAND);
						obj.setCode(line);
						obj.setKey(dao.createKey(line));
						obj.setType(type);
						if( forcedStatus != null ) {
							obj.setStatus(forcedStatus);
						}
						newObject = true;
					}

					if( forcedStatus == null ) {
						Map<String, String> data = vs.getStatus(obj.getCode());
						String status = data.get("VOUCHERSTATUS");

						if(StringUtils.hasText(data.get("EXPIRYDATE"))) {
							try {
								obj.setExpirationDate(sdf.parse(data.get("EXPIRYDATE")));
							} catch( Exception e ) {
								obj.setExpirationDate(null);
							}
						} else {
							obj.setExpirationDate(null);
						}

						if( newObject ) {
							if( status.equals("I")) obj.setStatus(Voucher.STATUS_OFFERED);
							if( status.equals("F") || status.equals("T")) obj.setStatus(Voucher.STATUS_AVAILABLE);

							if( obj.getStatus().equals(Voucher.STATUS_OFFERED)) {
								// try to revert the status
								data = vs.refundAndCommit(obj.getCode(), dao.getNextSequence());
								if( data.get("RESPONSECODE").equals("0")) {
									obj.setStatus(Voucher.STATUS_AVAILABLE);
								} else {
									obj.setStatus(Voucher.STATUS_USED);
								}
							}
						}

						if( status.equals("E")) obj.setStatus(Voucher.STATUS_EXPIRED);
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
