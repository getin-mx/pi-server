package mobi.allshoppings.cinepolis.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.cinepolis.vista.loyalty.VistaLoyaltyService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.CollectionUtils;

public class ResetLoyaltyVouchersService {

	private static final Logger log = Logger.getLogger(ResetLoyaltyVouchersService.class.getName());
	private static final List<String> formats = Arrays.asList(new String[] {"70118", "70119"});
	private static final List<Integer> statuses = Arrays.asList(new Integer[] {Voucher.STATUS_OFFERED});
	private static final String BRAND = "cinepolis_mx";
	private static final int MAX_RETRY_COUNT = 3;

	private VoucherDAO dao = new VoucherDAOJDOImpl();

	public void doProcess(long millis) throws ASException {

		int revertedCount = 0;
		int usedCount = 0;

		try {
			VistaLoyaltyService vs = VistaLoyaltyService.getInstance();
			Date limitDate = new Date(new Date().getTime() - millis);
			List<Voucher> list = dao.getUsingStatusAndBrandAndType(statuses, BRAND, formats);
			if(!CollectionUtils.isEmpty(list)) {
				log.log(Level.INFO, list.size() + " vouchers found");
				for( Voucher voucher : list ) {
					if (voucher.getAssignationDate().before(limitDate)) {
						int retryCount = 0;
						while( retryCount < MAX_RETRY_COUNT ) {
							try {
								Map<String,String> result = vs.getStatus(voucher.getCode(), voucher.getSubcode1(), voucher.getSubcode2());
								if("0".equals(result.get("ResponseCode")) && !result.containsKey("RecogID")) {
									// Voucher was used
									voucher.setStatus(Voucher.STATUS_USED);
									usedCount++;
									dao.update(voucher);
									retryCount = MAX_RETRY_COUNT;
								} else {
									// Voucher was not used
									voucher.setStatus(Voucher.STATUS_EXPIRED);
									voucher.setAssignationDate(null);
									voucher.setAssignationMember(null);
									voucher.setDeviceUUID(null);
									dao.update(voucher);
									retryCount = MAX_RETRY_COUNT;
								}
							} catch( Exception e ) {
								log.log(Level.SEVERE, e.getMessage(), e);
								retryCount++;
								Thread.sleep(5000);
							}
						}
					}
				}
			}

			log.log(Level.INFO, "Processed " + (revertedCount+usedCount) + " vouchers");
			log.log(Level.INFO, "Reverted " + (revertedCount) + " vouchers");
			log.log(Level.INFO, "Confirmed " + (usedCount) + " vouchers");
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}


	public void resetVoucher(String code) throws ASException {
		int revertedCount = 0;
		int usedCount = 0;

		try {
			VistaLoyaltyService vs = VistaLoyaltyService.getInstance();
			List<Voucher> list = Arrays.asList(new Voucher[] {dao.get(code, true)});
			if(!CollectionUtils.isEmpty(list)) {
				log.log(Level.INFO, list.size() + " vouchers found");
				for( Voucher voucher : list ) {
					int retryCount = 0;
					while( retryCount < MAX_RETRY_COUNT ) {
						try {
							Map<String,String> result = vs.getStatus(voucher.getCode(), voucher.getSubcode1(), voucher.getSubcode2());
							if("0".equals(result.get("ResponseCode")) && !result.containsKey("RecogID")) {
								// Voucher was used
								voucher.setStatus(Voucher.STATUS_USED);
								usedCount++;
								dao.update(voucher);
								retryCount = MAX_RETRY_COUNT;
							} else {
								// Voucher was not used
								voucher.setStatus(Voucher.STATUS_EXPIRED);
								voucher.setAssignationDate(null);
								voucher.setAssignationMember(null);
								voucher.setDeviceUUID(null);
								dao.update(voucher);
								retryCount = MAX_RETRY_COUNT;
							}
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
							retryCount++;
							Thread.sleep(5000);
						}
					}
				}
			}

			log.log(Level.INFO, "Processed " + (revertedCount+usedCount) + " vouchers");
			log.log(Level.INFO, "Reverted " + (revertedCount) + " vouchers");
			log.log(Level.INFO, "Confirmed " + (usedCount) + " vouchers");
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

}
