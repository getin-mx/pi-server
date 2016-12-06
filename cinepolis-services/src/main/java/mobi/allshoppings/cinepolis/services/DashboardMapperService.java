package mobi.allshoppings.cinepolis.services;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.ExternalActivityLogDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.dao.spi.CampaignActivityDAOJDOImpl;
import mobi.allshoppings.dao.spi.CampaignSpecialDAOJDOImpl;
import mobi.allshoppings.dao.spi.CheckinDAOJDOImpl;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.DashboardIndicatorAliasDAOJDOImpl;
import mobi.allshoppings.dao.spi.DashboardIndicatorDataDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dao.spi.ExternalActivityLogDAOJDOImpl;
import mobi.allshoppings.dao.spi.FloorMapDAOJDOImpl;
import mobi.allshoppings.dao.spi.MovieDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShoppingDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShowtimeDAOJDOImpl;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.dao.spi.WifiSpotDAOJDOImpl;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.DashboardIndicatorAlias;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExternalActivityLog;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.tools.CollectionFactory;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Text;

public class DashboardMapperService {

	private static final Logger log = Logger.getLogger(DashboardMapperService.class.getName());

	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	/**
	 * Campaign special to use
	 */
	private static final String CAMPAIGN_SPECIAL_ID = "1430288511084";

	/**
	 * Shopping ID to use
	 */
	private static final String SHOPPING_ID = "espaciolasamericas";

	/**
	 * Entity Kind to use
	 */
	public static final int KIND_BRAND = 1;

	private static final SimpleDateFormat timeSDF = new SimpleDateFormat("HHmmss");
	private static final SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat fullSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * Campaign special to use
	 */
	private static final String CAMPAIGN_SPECIAL_IDS[] = new String[] {
		"1432724531038", /* Bagui */ 
		"1432724594627" /* Crepa */ };
	
	/**
	 * Invalid Test Vouchers
	 */
	private static final List<String> testVouchers = Arrays.asList(new String[] {
			"00000000001", "00000000002", "00000000003"
	});

	/**
	 * DAOs 
	 */
	private DashboardIndicatorDataDAO dao = new DashboardIndicatorDataDAOJDOImpl();
	private DashboardIndicatorAliasDAO diAliasDao = new DashboardIndicatorAliasDAOJDOImpl();
	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private MovieDAO movieDao = new MovieDAOJDOImpl();
	private ExternalActivityLogDAO ealDao = new ExternalActivityLogDAOJDOImpl();
	private CampaignActivityDAO caDao = new CampaignActivityDAOJDOImpl();
	private CampaignSpecialDAO csDao = new CampaignSpecialDAOJDOImpl();
	private VoucherDAO voucherDao = new VoucherDAOJDOImpl();
	private ShowtimeDAO showtimeDao = new ShowtimeDAOJDOImpl();
	private DeviceInfoDAO deviceInfoDao = new DeviceInfoDAOJDOImpl();
	private CheckinDAO checkinDao = new CheckinDAOJDOImpl();
	private ShoppingDAO shoppingDao = new ShoppingDAOJDOImpl();
	private WifiSpotDAO wifiSpotDao = new WifiSpotDAOJDOImpl();
	private FloorMapDAO floorMapDao = new FloorMapDAOJDOImpl();

	public void createDashboardDataForDay(String baseDir, Date date) throws ASException {
		createTicketPerformanceDashboardForDay(date);
		createPromoPerformanceDashboardForDay(date);
		createCheckinPerformanceDashboardForDay(date);
		createHeatmapDashboardForDay(baseDir, date);
	}

	public void createTicketPerformanceDashboardForDay(Date date) throws ASException {

		log.log(Level.INFO, "Starting to create Cinepolis Performance Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		// All External Activities
		List<ExternalActivityLog> externalActivityList = ealDao.getUsingDatesAndCampaignSpecial(processDate, limitDate, CAMPAIGN_SPECIAL_ID, null, "creationDateTime");

		// All Campaign Activities
		List<CampaignActivity> campaignActivityList = caDao.getUsingDatesAndCampaignSpecial(processDate, limitDate, CAMPAIGN_SPECIAL_ID, null, "creationDateTime");

		// Showtimes
		List<Showtime> showList = showtimeDao.getUsingCinemaAndDateAndStatusAndRange(null, processDate, null, null, null);

		// All Vouchers
		List<String> voucherTypes = Arrays.asList(new String[] {"2D", "3D", "IMAX2D", "IMAX3D", "4DX2D", "4DX3D"});
		List<Voucher> voucherList = voucherDao.getUsingDatesAndType(processDate, limitDate, voucherTypes, null, "assignationDate");

		// Redeemed Campaign Activities
		List<CampaignActivity> redeemedCampaignActivityList = CollectionFactory.createList();

		// Vouchers assigned to a campaign
		Map<String, CampaignActivity> voucherAssignation = CollectionFactory.createMap();

		// Cinemas and movies cache
		Map<String, Movie> movies = CollectionFactory.createMap();
		Map<String, Cinema> cinemas = CollectionFactory.createMap();

		// Looks for all campaign Activities
		// Creates activities_sent
		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
		for(CampaignActivity ca : campaignActivityList ) {

			try {
				// General data
				JSONObject extras = new JSONObject(ca.getExtras().getValue());
				if( extras.has("showtimeId") && isValidActivity(ca)) {
					String format = CinepolisFormatMapper.map(extras.getString("format"));
					Showtime showtime = showtimeDao.get(extras.getString("showtimeId"), true);
					String shoppingId = SHOPPING_ID;
					Cinema cinema = cinemas.containsKey(showtime.getCinema().getIdentifier()) ? cinemas.get(showtime.getCinema().getIdentifier()) : cinemaDao.get(showtime.getCinema().getIdentifier(), true);
					Movie movie = movies.containsKey(showtime.getMovie().getIdentifier()) ? movies.get(showtime.getMovie().getIdentifier()) : movieDao.get(showtime.getMovie().getIdentifier(), true);
					movies.put(showtime.getMovie().getIdentifier(), movie);
					cinemas.put(showtime.getCinema().getIdentifier(), cinema);

					DashboardIndicatorData obj;

					// activities_sent --------------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					obj = buildBasicDashboardIndicatorData(
							"ticket_performance", "Performance", "activities_sent",
							"Promociones Enviadas", ca.getCreationDateTime(),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, movie.getIdentifier(), movie.getName(), format);

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(obj.getDoubleValue() + 1);

					indicatorsSet.put(obj.getKey().getName(), obj);

					obj = buildBasicDashboardIndicatorData(
							"ticket_engagement_funnel", "Engagement", "activities_sent",
							"Promociones Enviadas", ca.getCreationDateTime(),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, movie.getIdentifier(), movie.getName(), format);

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(obj.getDoubleValue() + 1);

					indicatorsSet.put(obj.getKey().getName(), obj);

					// activities_viewed ------------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					if( ca.getViewDateTime() != null || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REJECTED)) {
						obj = buildBasicDashboardIndicatorData(
								"ticket_performance", "Performance", "activities_viewed",
								"Promociones Leidas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);
					}

					// activities_accepted ----------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_ACCEPTED) || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REDEEMED)) {
						obj = buildBasicDashboardIndicatorData(
								"ticket_performance", "Performance", "activities_accepted",
								"Promociones Aceptadas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						obj = buildBasicDashboardIndicatorData(
								"ticket_engagement_funnel", "Engagement", "activities_accepted",
								"Promociones Aceptadas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						// vouchers_sent ----------------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						obj = buildBasicDashboardIndicatorData(
								"ticket_performance", "Performance", "vouchers_sent",
								"Folios Enviados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						try {
							obj.setDoubleValue(obj.getDoubleValue() + Integer.valueOf(extras.getInt("couponCount")));
						} catch( Exception e ) {
							log.log(Level.WARNING, e.getMessage(), e);
						}

						indicatorsSet.put(obj.getKey().getName(), obj);

						obj = buildBasicDashboardIndicatorData(
								"ticket_engagement_funnel", "Engagement", "vouchers_sent",
								"Folios Enviados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema,	movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						try {
							obj.setDoubleValue(obj.getDoubleValue() + Integer.valueOf(extras.getInt("couponCount")));
						} catch( Exception e ) {
							log.log(Level.WARNING, e.getMessage(), e);
						}

						indicatorsSet.put(obj.getKey().getName(), obj);

						JSONArray vouchers = extras.getJSONArray("suggestedCoupons");
						for( int i = 0; i < vouchers.length(); i++ ) {
							voucherAssignation.put(vouchers.getString(i), ca);
						}
					}

					// activities_rejected ----------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REJECTED) || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_RULE_REJECTED)) {
						obj = buildBasicDashboardIndicatorData(
								"ticket_performance", "Performance", "activities_rejected",
								"Promociones Rechazadas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						// ticket_rejection_motives -----------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						// rejection_motive_0 -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().contains("0")) {
							obj = buildBasicDashboardIndicatorData(
									"ticket_rejection_motives", "Motivos de Rechazo", "rejection_motive_0",
									"Ya compre", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, movie.getIdentifier(), movie.getName(), format);

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}

						// rejection_motive_1 -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().contains("1")) {
							obj = buildBasicDashboardIndicatorData(
									"ticket_rejection_motives", "Motivos de Rechazo", "rejection_motive_1",
									"Tengo otros planes", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, movie.getIdentifier(), movie.getName(), format);

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}

						// rejection_motive_3 -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().contains("3")) {
							obj = buildBasicDashboardIndicatorData(
									"ticket_rejection_motives", "Motivos de Rechazo", "rejection_motive_3",
									"No me gusta la pelicula", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, movie.getIdentifier(), movie.getName(), format);

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}

						// rejection_motive_null --------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().size() == 0 ) {
							obj = buildBasicDashboardIndicatorData(
									"ticket_rejection_motives", "Motivos de Rechazo", "rejection_motive_null",
									"Sin Respuesta", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, movie.getIdentifier(), movie.getName(), format);

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}
					}
				}

			} catch( Exception e ) {
				if( !(e instanceof JSONException )) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		// available seats --------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------
		for( Showtime show : showList ) {
			if( show.getAvailableSeats() != null && show.getAvailableSeats() > 0 ) {
				try {
					String cinemaId = show.getCinema().getIdentifier();
					String movieId = show.getMovie().getIdentifier();
					Cinema cinema = cinemas.containsKey(cinemaId) ? cinemas.get(cinemaId) : cinemaDao.get(cinemaId, true);
					Movie movie = movies.containsKey(movieId) ? movies.get(movieId) : movieDao.get(movieId, true);
					String shoppingId = SHOPPING_ID;
					Date innerDate = fullSDF.parse(show.getShowDate() + " " + show.getShowTime());
					String format = CinepolisFormatMapper.map(show.getFormatName());

					DashboardIndicatorData obj;

					obj = buildBasicDashboardIndicatorData(
							"ticket_performance", "Performance", "available_seats",
							"Butacas Disponibles", innerDate,
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, movie.getIdentifier(), movie.getName(), format);

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(show.getAvailableSeats().doubleValue());

					indicatorsSet.put(obj.getKey().getName(), obj);

					obj = buildBasicDashboardIndicatorData(
							"ticket_engagement_funnel", "Engagement", "available_seats",
							"Butacas Disponibles", innerDate,
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, movie.getIdentifier(), movie.getName(), format);

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(show.getAvailableSeats().doubleValue());

					indicatorsSet.put(obj.getKey().getName(), obj);

				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		// voucher_redeemed -------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------
		for( Voucher voucher : voucherList) {

			if(voucher.getStatus().equals(Voucher.STATUS_USED)) {
				CampaignActivity ca = voucherAssignation.get(voucher.getCode());
				if( ca == null ) {
					ca = new CampaignActivity();
					try {
						Showtime showtime = showtimeDao.get(voucher.getAssignationMember(), true);
						DeviceInfo device = deviceInfoDao.get(voucher.getDeviceUUID(), true);
						Cinema cinema = cinemas.containsKey(showtime.getCinema().getIdentifier()) ? cinemas.get(showtime.getCinema().getIdentifier()) : cinemaDao.get(showtime.getCinema().getIdentifier(), true);
						Movie movie = movies.containsKey(showtime.getMovie().getIdentifier()) ? movies.get(showtime.getMovie().getIdentifier()) : movieDao.get(showtime.getMovie().getIdentifier(), true);
						movies.put(showtime.getMovie().getIdentifier(), movie);
						cinemas.put(showtime.getCinema().getIdentifier(), cinema);

						String format = CinepolisFormatMapper.map(showtime.getFormatName());
						Double price = cinema.getPriceForFormat(format);

						ca.setBrandId(showtime.getBrandId());
						ca.setCampaignSpecialId(CAMPAIGN_SPECIAL_ID);
						ca.setCouponCode(voucher.getCode());
						ca.setCreationDateTime(voucher.getAssignationDate());
						ca.setDisplayable(false);
						ca.setDeviceUUID(voucher.getDeviceUUID());
						ca.setUserId(device.getUserId());
						ca.setRedeemStatus(CampaignActivity.REDEEM_STATUS_REDEEMED);
						ca.setRedeemDateTime(voucher.getAssignationDate());
						JSONObject extras = new JSONObject();
						extras.put("showtimeId", showtime.getIdentifier());
						try {
							extras.put("showDateTime", fullSDF.parse(showtime.getShowDate() + " " + showtime.getShowTime() + " CDT").getTime());
						} catch (Exception e) {}
						extras.put("couponCount", 1);
						extras.put("suggestedCoupons", Arrays.asList(new String[] {voucher.getCode()}));

						extras.put("name", movie.getName());
						extras.put("format", showtime.getFormatName().toUpperCase());
						extras.put("screen", showtime.getScreen());
						extras.put("price", price);
						extras.put("description", "Entrada a precio especial de $" + extras.get("price"));
						extras.put("availableSeats", showtime.getAvailableSeats());
						extras.put("cinema", cinema.getName());
						extras.put("rate", movie.getRate());
						extras.put("length", movie.getLenght());
						extras.put("movieGender", movie.getMovieGender());

						ca.setExtras(new Text(extras.toString()));
						ca.setKey(caDao.createKey());
						caDao.create(ca);
						ca = caDao.get(ca.getIdentifier(), true);
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				if( ca != null ) {

					// General data
					try {
						JSONObject extras = new JSONObject(ca.getExtras().getValue());
						String format = CinepolisFormatMapper.map(extras.getString("format"));
						Showtime showtime = showtimeDao.get(extras.getString("showtimeId"), true);
						String shoppingId = SHOPPING_ID;
						Cinema cinema = cinemas.containsKey(showtime.getCinema().getIdentifier()) ? cinemas.get(showtime.getCinema().getIdentifier()) : cinemaDao.get(showtime.getCinema().getIdentifier(), true);
						Movie movie = movies.containsKey(showtime.getMovie().getIdentifier()) ? movies.get(showtime.getMovie().getIdentifier()) : movieDao.get(showtime.getMovie().getIdentifier(), true);
						movies.put(showtime.getMovie().getIdentifier(), movie);
						cinemas.put(showtime.getCinema().getIdentifier(), cinema);

						DashboardIndicatorData obj;

						// vouchers_redeemed
						obj = buildBasicDashboardIndicatorData(
								"ticket_performance", "Performance", "vouchers_redeemed",
								"Folios Canjeados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						//
						obj = buildBasicDashboardIndicatorData(
								"ticket_engagement_funnel", "Engagement", "vouchers_redeemed",
								"Folios Canjeados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						//
						obj = buildBasicDashboardIndicatorData(
								"ticket_revenue", "Canjes / Revenue", "vouchers_redeemed",
								"Folios Canjeados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						// revenue
						obj = buildBasicDashboardIndicatorData(
								"ticket_performance", "Performance", "revenue",
								"Revenue", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + extras.getDouble("price"));

						indicatorsSet.put(obj.getKey().getName(), obj);

						// revenue
						obj = buildBasicDashboardIndicatorData(
								"ticket_revenue", "Canjes / Revenue", "revenue",
								"Revenue", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, movie.getIdentifier(), movie.getName(), format);

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + extras.getDouble("price"));

						indicatorsSet.put(obj.getKey().getName(), obj);

						if(!redeemedCampaignActivityList.contains(ca)) {
							redeemedCampaignActivityList.add(ca);
						}
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}

		// activities_redeemed ----------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------
		for( CampaignActivity ca : redeemedCampaignActivityList ) {

			if( isValidActivity(ca)) {
				// General data
				JSONObject extras = new JSONObject(ca.getExtras().getValue());
				String format = CinepolisFormatMapper.map(extras.getString("format"));
				Showtime showtime = showtimeDao.get(extras.getString("showtimeId"), true);
				String shoppingId = SHOPPING_ID;
				Cinema cinema = cinemas.containsKey(showtime.getCinema().getIdentifier()) ? cinemas.get(showtime.getCinema().getIdentifier()) : cinemaDao.get(showtime.getCinema().getIdentifier(), true);
				Movie movie = movies.containsKey(showtime.getMovie().getIdentifier()) ? movies.get(showtime.getMovie().getIdentifier()) : movieDao.get(showtime.getMovie().getIdentifier(), true);
				movies.put(showtime.getMovie().getIdentifier(), movie);
				cinemas.put(showtime.getCinema().getIdentifier(), cinema);

				DashboardIndicatorData obj;

				// activities_redeemed
				obj = buildBasicDashboardIndicatorData(
						"ticket_performance", "Performance", "activities_redeemed",
						"Promociones Canjeadas", ca.getCreationDateTime(),
						DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
						cinema, movie.getIdentifier(), movie.getName(), format);

				if(indicatorsSet.containsKey(obj.getKey().getName())) 
					obj = indicatorsSet.get(obj.getKey().getName());

				obj.setDoubleValue(obj.getDoubleValue() + 1);

				indicatorsSet.put(obj.getKey().getName(), obj);
			}
		}

		// mall_traffic -----------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------

		List<String> generalDevices = CollectionFactory.createList();
		Map<String, List<String>> showCount = CollectionFactory.createMap();
		for( ExternalActivityLog log : externalActivityList) {
			List<String> devices = showCount.get(log.getEntityId());
			if( devices == null ) devices = CollectionFactory.createList();
			for( String device : log.getSuggestedDevices()) {
				if(!devices.contains(device) && !generalDevices.contains(device)) {
					generalDevices.add(device);
					devices.add(device);
				}
			}
			showCount.put(log.getEntityId(), devices);
		}

		SimpleDateFormat showtimeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Iterator<String> ix = showCount.keySet().iterator();
		while(ix.hasNext()) {
			String key = ix.next();
			long value = showCount.get(key).size();

			try {
				Showtime showtime = showtimeDao.get(key, true);
				String format = CinepolisFormatMapper.map(showtime.getFormatName());
				String shoppingId = SHOPPING_ID;
				Cinema cinema = cinemas.containsKey(showtime.getCinema().getIdentifier()) ? cinemas.get(showtime.getCinema().getIdentifier()) : cinemaDao.get(showtime.getCinema().getIdentifier(), true);
				Movie movie = movies.containsKey(showtime.getMovie().getIdentifier()) ? movies.get(showtime.getMovie().getIdentifier()) : movieDao.get(showtime.getMovie().getIdentifier(), true);
				movies.put(showtime.getMovie().getIdentifier(), movie);
				cinemas.put(showtime.getCinema().getIdentifier(), cinema);

				DashboardIndicatorData obj;

				// mall_traffic -----------------------------------------------------------------------------------------
				// ------------------------------------------------------------------------------------------------------
				obj = buildBasicDashboardIndicatorData(
						"ticket_performance", "Performance", "mall_traffic",
						"Trafico Plaza Comercial", showtimeSDF.parse(showtime.getShowDate() + " " + showtime.getShowTime()),
						DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
						cinema, movie.getIdentifier(), movie.getName(), format);

				if(indicatorsSet.containsKey(obj.getKey().getName())) 
					obj = indicatorsSet.get(obj.getKey().getName());

				obj.setDoubleValue(obj.getDoubleValue() + value);

				indicatorsSet.put(obj.getKey().getName(), obj);
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create Cinepolis Performance Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

	}

	public void createPromoPerformanceDashboardForDay(Date date) throws ASException {

		log.log(Level.INFO, "Starting to create Cinepolis Food Performance Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		// All Campaign Specials that belongs to Cinepolis and Alimentos
		Map<String, CampaignSpecial> specialsMap = CollectionFactory.createMap();
		List<CampaignSpecial> specials = csDao.getUsingIdList(Arrays.asList(CAMPAIGN_SPECIAL_IDS));
		for( CampaignSpecial obj : specials ) {
			specialsMap.put(obj.getIdentifier(), obj);
		}

		// All External Activities
		List<ExternalActivityLog> externalActivityList = CollectionFactory.createList();
		for(CampaignSpecial obj : specials) {
			externalActivityList.addAll(ealDao.getUsingDatesAndCampaignSpecial(processDate, limitDate, obj.getIdentifier(), null, "creationDateTime"));
		}

		// All Campaign Activities
		List<CampaignActivity> campaignActivityList = CollectionFactory.createList();
		for(CampaignSpecial obj : specials) {
			campaignActivityList.addAll(caDao.getUsingDatesAndCampaignSpecial(processDate, limitDate, obj.getIdentifier(), null, "creationDateTime"));
		}

		// All Vouchers
		List<String> voucherTypes = Arrays.asList(new String[] {"70118", "70119"});
		List<Voucher> voucherList = voucherDao.getUsingDatesAndType(processDate, limitDate, voucherTypes, null, "assignationDate");

		// Redeemed Campaign Activities
		List<CampaignActivity> redeemedCampaignActivityList = CollectionFactory.createList();

		// Vouchers assigned to a campaign
		Map<String, CampaignActivity> voucherAssignation = CollectionFactory.createMap();

		// Cinemas and movies cache
		//		Map<String, Movie> movies = CollectionFactory.createMap();
		Map<String, Cinema> cinemas = CollectionFactory.createMap();

		// Looks for all campaign Activities
		// Creates activities_sent
		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
		for(CampaignActivity ca : campaignActivityList ) {

			try {
				// General data
				JSONObject extras = new JSONObject(ca.getExtras().getValue());
				if( extras.has("cinemaId") && isValidActivity(ca)) {
					String shoppingId = SHOPPING_ID;
					Cinema cinema = cinemas.containsKey(extras.getString("cinemaId")) ? cinemas.get(extras.getString("cinemaId")) : cinemaDao.get(extras.getString("cinemaId"), true);
					cinemas.put(extras.getString("cinemaId"), cinema);

					DashboardIndicatorData obj;

					// activities_sent --------------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					obj = buildBasicDashboardIndicatorData(
							"promo_performance", "Performance", "activities_sent",
							"Promociones Enviadas", ca.getCreationDateTime(),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(obj.getDoubleValue() + 1);

					indicatorsSet.put(obj.getKey().getName(), obj);

					obj = buildBasicDashboardIndicatorData(
							"promo_engagement_funnel", "Engagement", "activities_sent",
							"Promociones Enviadas", ca.getCreationDateTime(),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(obj.getDoubleValue() + 1);

					indicatorsSet.put(obj.getKey().getName(), obj);

					// activities_viewed ------------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					if( ca.getViewDateTime() != null || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REJECTED)) {
						obj = buildBasicDashboardIndicatorData(
								"promo_performance", "Performance", "activities_viewed",
								"Promociones Leidas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);
					}

					// activities_accepted ----------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_ACCEPTED) || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REDEEMED)) {
						obj = buildBasicDashboardIndicatorData(
								"promo_performance", "Performance", "activities_accepted",
								"Promociones Aceptadas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						obj = buildBasicDashboardIndicatorData(
								"promo_engagement_funnel", "Engagement", "activities_accepted",
								"Promociones Aceptadas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						// vouchers_sent ----------------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						obj = buildBasicDashboardIndicatorData(
								"promo_performance", "Performance", "vouchers_sent",
								"Folios Enviados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						try {
							obj.setDoubleValue(obj.getDoubleValue() + Integer.valueOf(extras.getInt("couponCount")));
						} catch( Exception e ) {
							log.log(Level.WARNING, e.getMessage(), e);
						}

						indicatorsSet.put(obj.getKey().getName(), obj);

						obj = buildBasicDashboardIndicatorData(
								"promo_engagement_funnel", "Engagement", "vouchers_sent",
								"Folios Enviados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						try {
							obj.setDoubleValue(obj.getDoubleValue() + 1);
						} catch( Exception e ) {
							log.log(Level.WARNING, e.getMessage(), e);
						}

						indicatorsSet.put(obj.getKey().getName(), obj);

						JSONArray vouchers = extras.getJSONArray("suggestedCoupons");
						for( int i = 0; i < vouchers.length(); i++ ) {
							voucherAssignation.put(vouchers.getString(i), ca);
						}

					}

					// activities_rejected ----------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REJECTED) || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_RULE_REJECTED)) {
						obj = buildBasicDashboardIndicatorData(
								"promo_performance", "Performance", "activities_rejected",
								"Promociones Rechazadas", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						// promo_rejection_motives -----------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						// rejection_motive_0 -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().contains("0")) {
							obj = buildBasicDashboardIndicatorData(
									"promo_rejection_motives", "Motivos de Rechazo", "rejection_motive_0",
									"Ya compre", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}

						// rejection_motive_1 -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().contains("1")) {
							obj = buildBasicDashboardIndicatorData(
									"promo_rejection_motives", "Motivos de Rechazo", "rejection_motive_1",
									"Tengo otros planes", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}

						// rejection_motive_3 -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().contains("3")) {
							obj = buildBasicDashboardIndicatorData(
									"promo_rejection_motives", "Motivos de Rechazo", "rejection_motive_3",
									"No me gusta la promocion", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}

						// rejection_motive_null --------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						if( ca.getRejectionMotives().size() == 0 ) {
							obj = buildBasicDashboardIndicatorData(
									"promo_rejection_motives", "Motivos de Rechazo", "rejection_motive_null",
									"Sin Respuesta", ca.getCreationDateTime(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}
					}
				}

			} catch( Exception e ) {
				if( !(e instanceof JSONException )) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		// voucher_redeemed -------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------
		for( Voucher voucher : voucherList) {

			if(voucher.getStatus().equals(Voucher.STATUS_USED)) {
				CampaignActivity ca = voucherAssignation.get(voucher.getCode());
				if( ca != null ) {

					// General data
					JSONObject extras = new JSONObject(ca.getExtras().getValue());
					if( extras.has("cinemaId")) {
						String shoppingId = SHOPPING_ID;
						Cinema cinema = cinemas.containsKey(extras.getString("cinemaId")) ? cinemas.get(extras.getString("cinemaId")) : cinemaDao.get(extras.getString("cinemaId"), true);
						cinemas.put(extras.getString("cinemaId"), cinema);

						DashboardIndicatorData obj;

						// vouchers_redeemed
						obj = buildBasicDashboardIndicatorData(
								"promo_performance", "Performance", "vouchers_redeemed",
								"Folios Canjeados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						//
						obj = buildBasicDashboardIndicatorData(
								"promo_engagement_funnel", "Engagement", "vouchers_redeemed",
								"Folios Canjeados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						//
						obj = buildBasicDashboardIndicatorData(
								"promo_revenue", "Canjes / Revenue", "vouchers_redeemed",
								"Folios Canjeados", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);

						// revenue
						obj = buildBasicDashboardIndicatorData(
								"promo_performance", "Performance", "revenue",
								"Revenue", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + extras.getDouble("price"));

						indicatorsSet.put(obj.getKey().getName(), obj);

						// revenue
						obj = buildBasicDashboardIndicatorData(
								"promo_revenue", "Canjes / Revenue", "revenue",
								"Revenue", ca.getCreationDateTime(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + extras.getDouble("price"));

						indicatorsSet.put(obj.getKey().getName(), obj);

						if(!redeemedCampaignActivityList.contains(ca)) {
							redeemedCampaignActivityList.add(ca);
						}
					}
				}
			}
		}

		// activities_redeemed ----------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------
		for( CampaignActivity ca : redeemedCampaignActivityList ) {

			// General data
			JSONObject extras = new JSONObject(ca.getExtras().getValue());
			if( extras.has("cinemaId") && isValidActivity(ca)) {
				String shoppingId = SHOPPING_ID;
				Cinema cinema = cinemas.containsKey(extras.getString("cinemaId")) ? cinemas.get(extras.getString("cinemaId")) : cinemaDao.get(extras.getString("cinemaId"), true);
				cinemas.put(extras.getString("cinemaId"), cinema);

				DashboardIndicatorData obj;

				// activities_redeemed
				obj = buildBasicDashboardIndicatorData(
						"promo_performance", "Performance", "activities_redeemed",
						"Promociones Canjeadas", ca.getCreationDateTime(),
						DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
						cinema, ca.getCampaignSpecialId(), specialsMap.get(ca.getCampaignSpecialId()).getName(), "");

				if(indicatorsSet.containsKey(obj.getKey().getName())) 
					obj = indicatorsSet.get(obj.getKey().getName());

				obj.setDoubleValue(obj.getDoubleValue() + 1);

				indicatorsSet.put(obj.getKey().getName(), obj);
			}
		}

		// mall_traffic -----------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------

		SimpleDateFormat showtimeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<String> generalDevices = CollectionFactory.createList();
		Map<String, List<String>> showCount = CollectionFactory.createMap();
		for( ExternalActivityLog log : externalActivityList) {
			List<String> devices = showCount.get(log.getEntityId());
			if( devices == null ) devices = CollectionFactory.createList();
			for( String device : log.getSuggestedDevices()) {
				if(!devices.contains(device) && !generalDevices.contains(device)) {
					generalDevices.add(device);
					devices.add(device);
				}
			}
			showCount.put(log.getCampaignSpecialId() + "||" + log.getEntityId() + "||" + showtimeSDF.format(log.getCreationDateTime()), devices);
		}

		Iterator<String> ix = showCount.keySet().iterator();
		while(ix.hasNext()) {
			String mainParts = ix.next(); 
			String parts[] = mainParts.split("\\|\\|");
			String csId = parts[0];
			String key = parts[1];
			String datetime = parts[2];
			long value = showCount.get(mainParts).size();

			try {
				if( StringUtils.hasText(key)) {
					Cinema cinema = cinemaDao.get(key, true);
					CampaignSpecial cs = specialsMap.get(csId); 
					String shoppingId = SHOPPING_ID;

					DashboardIndicatorData obj;

					// mall_traffic -----------------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					obj = buildBasicDashboardIndicatorData(
							"promo_performance", "Performance", "mall_traffic",
							"Trafico Plaza Comercial", showtimeSDF.parse(datetime),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
							cinema, cs.getIdentifier(), cs.getName(), "");

					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());

					obj.setDoubleValue(obj.getDoubleValue() + value);

					indicatorsSet.put(obj.getKey().getName(), obj);
				}
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create Cinepolis Promo Performance Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");


	}

	public void createCheckinPerformanceDashboardForDay(Date date) throws ASException {

		log.log(Level.INFO, "Starting to create Cinepolis Checkin Performance Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		// All Checkins
		List<Checkin> checkins = checkinDao.getUsingEntityKindAndPossibleFakeAndDates(null, false, processDate, limitDate);
		List<Cinema> cinemas = cinemaDao.getAll();
		List<Shopping> shoppings = shoppingDao.getAll();

		Map<String, List<Cinema>> cinemaCache = CollectionFactory.createMap();
		for(Cinema cinema : cinemas ) {
			if( StringUtils.hasText(cinema.getShoppingId())) {
				String key = cinema.getShoppingId();
				List<Cinema> val = cinemaCache.get(key);
				if( val == null ) val = CollectionFactory.createList();
				val.add(cinema);
				cinemaCache.put(key, val);
			}
		}
		
		Map<String, Cinema> cinemaCache2 = CollectionFactory.createMap();
		for(Cinema cinema : cinemas ) {
			cinemaCache2.put(cinema.getIdentifier(), cinema);
		}
		
		Map<String, Shopping> shoppingCache = CollectionFactory.createMap();
		for(Shopping shopping : shoppings ) {
			shoppingCache.put(shopping.getIdentifier(), shopping);
		}
		
		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
		for(Checkin checkin : checkins ) {
			try {
			
				// Checkin data
				DashboardIndicatorData obj;

				if( checkin.getEntityKind().equals(EntityKind.KIND_SHOPPING)) {

					List<Cinema> list = cinemaCache.get(checkin.getEntityId());
					if(list != null) for( Cinema cinema : list ) {
						Shopping shopping = shoppingCache.get(checkin.getEntityId());
						if( shopping != null ) {
							// mall_checkins ----------------------------------------------------------------------------------------
							// ------------------------------------------------------------------------------------------------------
							obj = buildBasicDashboardIndicatorData(
									"conversion_funnel", "Flujo de Personas", "mall_checkins",
									"Centro Comercial", checkin.getCheckinStarted(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, checkin.getEntityId(),
									cinema, "", "", "", shopping.getName());

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());

							obj.setDoubleValue(obj.getDoubleValue() + 1);

							indicatorsSet.put(obj.getKey().getName(), obj);
						}
					}
				}
			
				if( checkin.getEntityKind().equals(EntityKind.KIND_STORE) && checkin.getEntityId().startsWith("cinepolis_mx")) {

					Cinema cinema = cinemaCache2.get(checkin.getEntityId());
					if( cinema != null ) {
						// cinepolis_checkins -----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						obj = buildBasicDashboardIndicatorData(
								"conversion_funnel", "Flujo de Personas", "cinema_checkins",
								"Cinepolis", checkin.getCheckinStarted(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, cinema.getShoppingId(),
								cinema, "", "", "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);				
					}
				}
				
				if( checkin.getEntityKind().equals(EntityKind.KIND_STORE) && checkin.getEntityId().startsWith("cinemex_mx")) {

					Cinema cinema = cinemaCache2.get(checkin.getEntityId());
					if( cinema != null ) {
						// cinemex_checkins ----------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						obj = buildBasicDashboardIndicatorData(
								"conversion_funnel", "Flujo de Personas", "cinemex_checkins",
								"Cinemex", checkin.getCheckinStarted(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, cinema.getShoppingId(),
								cinema, "", "", "");

						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());

						obj.setDoubleValue(obj.getDoubleValue() + 1);

						indicatorsSet.put(obj.getKey().getName(), obj);				
					}
				}

			} catch( Exception e ) {
				if( !(e instanceof JSONException )) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		
		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create Cinepolis Checkin Performance Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

	}

	public void createHeatmapDashboardForDay(String baseDir, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create Cinepolis Heatmap Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		// All Wifi Data
		DumperHelper<DeviceWifiLocationHistory> dumper = new DumperHelperImpl<DeviceWifiLocationHistory>(baseDir, DeviceWifiLocationHistory.class);
		Iterator<DeviceWifiLocationHistory> i = dumper.iterator(processDate, limitDate);
		List<Cinema> cinemas = cinemaDao.getAll();
		List<Shopping> shoppings = shoppingDao.getAll();
		List<FloorMap> floorMaps = floorMapDao.getAll();
		List<WifiSpot> wifiSpots = wifiSpotDao.getAll();
		
		Map<String, List<Cinema>> cinemaCache = CollectionFactory.createMap();
		for(Cinema cinema : cinemas ) {
			if( StringUtils.hasText(cinema.getShoppingId())) {
				String key = cinema.getShoppingId();
				List<Cinema> val = cinemaCache.get(key);
				if( val == null ) val = CollectionFactory.createList();
				val.add(cinema);
				cinemaCache.put(key, val);
			}
		}
		
		Map<String, Cinema> cinemaCache2 = CollectionFactory.createMap();
		for(Cinema cinema : cinemas ) {
			cinemaCache2.put(cinema.getIdentifier(), cinema);
		}
		
		Map<String, Shopping> shoppingCache = CollectionFactory.createMap();
		for(Shopping shopping : shoppings ) {
			shoppingCache.put(shopping.getIdentifier(), shopping);
		}
		
		Map<String, WifiSpot> wifiSpotCache = CollectionFactory.createMap();
		for(WifiSpot wifiSpot : wifiSpots) {
			wifiSpotCache.put(wifiSpot.getIdentifier(), wifiSpot);
		}
		
		Map<String, FloorMap> floorMapCache = CollectionFactory.createMap();
		for(FloorMap floorMap : floorMaps) {
			floorMapCache.put(floorMap.getIdentifier(), floorMap);
		}
		
		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
		while(i.hasNext()) {
			DeviceWifiLocationHistory location = i.next();
			try {

				// Checkin data
				DashboardIndicatorData obj;

				if( StringUtils.hasText(location.getWifiSpotId())) {
					WifiSpot wifiSpot = wifiSpotCache.get(location.getWifiSpotId());
					if(wifiSpot != null ) {
						FloorMap floorMap = floorMapCache.get(wifiSpot.getFloorMapId());
						if( floorMap != null ) {
							Shopping shopping = shoppingCache.get(floorMap.getShoppingId());
							if( shopping != null ) {

								List<Cinema> list = cinemaCache.get(shopping.getIdentifier());
								if(list != null) for( Cinema cinema : list ) {

									// heatmap ----------------------------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData(
											"heatmap", "Heat Map", wifiSpot.getIdentifier(),
											wifiSpot.getZoneName(), location.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
											cinema, "", "", "", floorMap.getFloor());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());

									obj.setDoubleValue(obj.getDoubleValue() + 1);

									indicatorsSet.put(obj.getKey().getName(), obj);

									// heatmap ----------------------------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData(
											"heatmap_data", "Heat Map Data", wifiSpot.getIdentifier(),
											wifiSpot.getIdentifier(), location.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
											cinema, "", "", "");
									obj.setSubentityId(wifiSpot.getFloorMapId());
									obj.setSubentityName(floorMap.getFloor());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());

									obj.setDoubleValue(obj.getDoubleValue() + 1);

									indicatorsSet.put(obj.getKey().getName(), obj);

								}

							}
						}
					}
				}

			} catch( Exception e ) {
				if( !(e instanceof JSONException )) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		
		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create Cinepolis Heatmap Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

	}

	public void saveIndicatorAliasSet(List<DashboardIndicatorAlias> aliases) throws ASException {
		for( DashboardIndicatorAlias alias : aliases ) {
			try {
				diAliasDao.delete(alias.getIdentifier());
			} catch( Exception e ) {}

			diAliasDao.create(alias);
		}
	}

	public void saveIndicatorSet(Map<String, DashboardIndicatorData> indicatorsSet) throws ASException {

		List<DashboardIndicatorAlias> aliases = createAliasList(indicatorsSet);
		saveIndicatorAliasSet(aliases);

		Iterator<String> x = indicatorsSet.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();

			try {
				dao.delete(key);
			} catch( Exception e ) {}

			dao.create(indicatorsSet.get(key));
		}
	}

	public List<DashboardIndicatorAlias> createAliasList(Map<String, DashboardIndicatorData> indicatorsSet) throws ASException {
		List<DashboardIndicatorAlias> aliases = CollectionFactory.createList();

		Iterator<String> x = indicatorsSet.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();
			DashboardIndicatorData data = indicatorsSet.get(key);
			DashboardIndicatorAlias alias = new DashboardIndicatorAlias(
					data.getEntityId(), data.getEntityKind(),
					data.getScreenName(), data.getElementId(),
					data.getElementName(), data.getElementSubId(),
					data.getElementSubName(), data.getSubentityId(),
					data.getSubentityName());
			alias.setKey(diAliasDao.createKey(alias));
			if(!aliases.contains(alias)) aliases.add(alias);

			alias = new DashboardIndicatorAlias(
					data.getEntityId(), data.getEntityKind(),
					data.getScreenName(), data.getElementId().replace("ticket_", "promo_"),
					data.getElementName(), data.getElementSubId(),
					data.getElementSubName(), data.getSubentityId(),
					data.getSubentityName());
			alias.setKey(diAliasDao.createKey(alias));
			if(!aliases.contains(alias)) aliases.add(alias);
		}

		return aliases;
	}

	public DashboardIndicatorData buildBasicDashboardIndicatorData(
			String elementId, String elementName, String elementSubId,
			String elementSubName, Date date, String periodType,
			String shoppingId, Cinema cinema,
			String movieId, String movieName, String voucherType) throws ASException {
		return buildBasicDashboardIndicatorData(elementId, elementName, elementSubId, elementSubName, date, periodType, shoppingId, cinema, movieId, movieName, voucherType, null);
	}
	
	public DashboardIndicatorData buildBasicDashboardIndicatorData(
			String elementId, String elementName, String elementSubId,
			String elementSubName, Date date, String periodType,
			String shoppingId, Cinema cinema,
			String movieId, String movieName, String voucherType, String subentityName) throws ASException {

		DashboardIndicatorData obj = new DashboardIndicatorData();
		obj.setEntityId(BASE_ID);
		obj.setEntityKind(KIND_BRAND);
		obj.setDate(DateUtils.truncate(date, Calendar.DATE));
		obj.setStringDate(dateSDF.format(date));

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

		obj.setDayOfWeek(dayOfWeek);

		obj.setElementId(elementId);
		obj.setElementName(elementName);
		obj.setElementSubId(elementSubId);
		obj.setElementSubName(elementSubName);
		obj.setTimeZone(getTimeZone(date));
		obj.setMovieId(movieId);
		obj.setMovieName(movieName);
		obj.setSubentityId(cinema.getIdentifier());
		obj.setSubentityName(subentityName != null ? subentityName : cinema.getName());
		obj.setCountry(cinema.getAddress().getCountry());
		obj.setCity(cinema.getAddress().getCity());
		obj.setProvince(cinema.getAddress().getProvince());
		obj.setVoucherType(voucherType);
		obj.setPeriodType(periodType);

		obj.setKey(dao.createKey(obj));

		return obj;
	}

	public int getTimeZone(Date date) {
		int time = Integer.valueOf(timeSDF.format(date));
		if( time < 120000) return DashboardIndicatorData.TIME_ZONE_MORNING;
		else if( time < 150000) return DashboardIndicatorData.TIME_ZONE_NOON;
		else if( time < 190000) return DashboardIndicatorData.TIME_ZONE_AFTERNOON;
		else return DashboardIndicatorData.TIME_ZONE_NIGHT;
	}

	private boolean isValidActivity(CampaignActivity ca ) {
		try {
			JSONObject extras = new JSONObject(ca.getExtras().getValue());
			boolean isValid = true;
			if( extras.has("suggestedCoupons")) {
				JSONArray suggestedCoupons = extras.getJSONArray("suggestedCoupons");
				for( int i = 0; i < suggestedCoupons.length(); i++ ) {
					String voucher = suggestedCoupons.getString(i);
					if( testVouchers.contains(voucher)) isValid = false;
				}
			} else {
				isValid = false;
			}

			return isValid;

		} catch (JSONException e ) {
			return false;
		}
	}
}
