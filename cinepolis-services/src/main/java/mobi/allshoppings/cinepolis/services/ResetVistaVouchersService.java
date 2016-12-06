package mobi.allshoppings.cinepolis.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.cinepolis.vista.voucher.VistaVoucherService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.CollectionUtils;

public class ResetVistaVouchersService {

	private static final Logger log = Logger.getLogger(ResetVistaVouchersService.class.getName());
	private static final List<String> formats = Arrays.asList(new String[] {"2D", "3D", "IMAX2D", "IMAX3D", "4DX2D", "4DX3D"});
	private static final List<Integer> statuses = Arrays.asList(new Integer[] {Voucher.STATUS_OFFERED});
	private static final String BRAND = "cinepolis_mx";
	private static final int MAX_RETRY_COUNT = 3;

	private VoucherDAO dao = new VoucherDAOJDOImpl();

	public void doProcess(long millis) throws ASException {

		int revertedCount = 0;
		int usedCount = 0;

		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			Date limitDate = new Date(new Date().getTime() - millis);
			List<Voucher> list = dao.getUsingStatusAndBrandAndType(statuses, BRAND, formats);
			if(!CollectionUtils.isEmpty(list)) {
				log.log(Level.INFO, list.size() + " vouchers found");
				for( Voucher voucher : list ) {
					try {
						log.log(Level.INFO, "Processing voucher " + voucher.getCode());
						if (voucher.getAssignationDate() == null || voucher.getAssignationDate().before(limitDate)) {
							int retryCount = 0;
							while( retryCount < MAX_RETRY_COUNT ) {
								try {
									Map<String,String> result = vs.refundAndCommit(voucher.getCode(), dao.getNextSequence());
									if("0".equals(result.get("RESPONSECODE"))) {
										// Voucher was not used
										voucher.setStatus(Voucher.STATUS_AVAILABLE);
										voucher.setAssignationDate(null);
										voucher.setAssignationMember(null);
										voucher.setDeviceUUID(null);
										revertedCount++;
									} else {
										// Voucher was used
										Map<String, String> resp = vs.getStatus(voucher.getCode());
										if( resp.get("VOUCHERSTATUS").equals("R")) {
											voucher.setStatus(Voucher.STATUS_USED);
											usedCount++;
										} else {
											if( resp.get("VOUCHERSTATUS").equals("F") && resp.get("VOUCHERTICKETREDEMPTIONS").equals("1")) {
												// Voucher was not used
												voucher.setStatus(Voucher.STATUS_AVAILABLE);
												voucher.setAssignationDate(null);
												voucher.setAssignationMember(null);
												voucher.setDeviceUUID(null);
												revertedCount++;
											} else {
												log.log(Level.INFO, "Cannot confirm use of voucher " + voucher.getCode());
											}
										}
									}
									dao.update(voucher);
									retryCount = MAX_RETRY_COUNT;
								} catch( Exception e ) {
									log.log(Level.SEVERE, e.getMessage(), e);
									retryCount++;
									Thread.sleep(5000);
								}
							}
						}
					} catch( Exception e ) {
						log.log(Level.SEVERE, "Unabe to process voucher " + voucher.getCode(), e);
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
			VistaVoucherService vs = VistaVoucherService.getInstance();
			List<Voucher> list = Arrays.asList(new Voucher[] {dao.get(code, true)});
			if(!CollectionUtils.isEmpty(list)) {
				for( Voucher voucher : list ) {
					log.log(Level.INFO, "Processing voucher " + voucher.getCode());
					int retryCount = 0;
					while( retryCount < MAX_RETRY_COUNT ) {
						try {
							Map<String,String> result = vs.refundAndCommit(voucher.getCode(), dao.getNextSequence());
							if("0".equals(result.get("RESPONSECODE"))) {
								// Voucher was not used
								voucher.setStatus(Voucher.STATUS_AVAILABLE);
								voucher.setAssignationDate(null);
								voucher.setAssignationMember(null);
								voucher.setDeviceUUID(null);
								revertedCount++;
							} else {
								// Voucher was used
								Map<String, String> resp = vs.getStatus(voucher.getCode());
								if( resp.get("VOUCHERSTATUS").equals("R")) {
									voucher.setStatus(Voucher.STATUS_USED);
									usedCount++;
								} else {
									if( resp.get("VOUCHERSTATUS").equals("F") && resp.get("VOUCHERTICKETREDEMPTIONS").equals("1")) {
										// Voucher was not used
										voucher.setStatus(Voucher.STATUS_AVAILABLE);
										voucher.setAssignationDate(null);
										voucher.setAssignationMember(null);
										voucher.setDeviceUUID(null);
										revertedCount++;
									} else {
										log.log(Level.INFO, "Cannot confirm use of voucher " + voucher.getCode());
									}
								}
							}
							dao.update(voucher);
							retryCount = MAX_RETRY_COUNT;
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

	public void confirmVoucher(String code) throws ASException {
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			List<Voucher> list = Arrays.asList(new Voucher[] {dao.get(code, true)});
			if(!CollectionUtils.isEmpty(list)) {
				for( Voucher voucher : list ) {
					int retryCount = 0;
					while( retryCount < MAX_RETRY_COUNT ) {
						try {
							log.log(Level.INFO, "Processing voucher " + voucher.getCode());
							Map<String, String> resp = vs.getStatus(voucher.getCode());
							if( resp.get("VOUCHERSTATUS").equals("R")) {
								if( voucher.getStatus().equals(Voucher.STATUS_USED)) {
									// do nothing
								} else {
									if( voucher.getAssignationDate() != null ) {
										voucher.setStatus(Voucher.STATUS_USED);
										log.log(Level.INFO, "Confirming voucher " + voucher.getCode());
										dao.update(voucher);
										retryCount = MAX_RETRY_COUNT;
									}
								}
							} else {
								if( voucher.getStatus().equals(Voucher.STATUS_USED)) {
									voucher.setStatus(Voucher.STATUS_AVAILABLE);
									voucher.setAssignationDate(null);
									voucher.setAssignationMember(null);
									voucher.setDeviceUUID(null);
									log.log(Level.INFO, "Rejecting voucher " + voucher.getCode());
									dao.update(voucher);
									retryCount = MAX_RETRY_COUNT;
								}
							}
							retryCount = MAX_RETRY_COUNT;
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
							retryCount++;
							Thread.sleep(5000);
						}
					}
				}
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
}
