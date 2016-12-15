package mobi.allshoppings.cinepolis.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.inodes.datanucleus.model.Text;
import com.inodes.util.CollectionFactory;

import junit.framework.TestCase;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.CampaignActivityDAOJDOImpl;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.MovieDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShowtimeDAOJDOImpl;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.tools.Range;

public class Fixers extends TestCase {

	private MovieDAO movieDao = new MovieDAOJDOImpl();
	private CampaignActivityDAO caDao = new CampaignActivityDAOJDOImpl();
	private ShowtimeDAO showDao = new ShowtimeDAOJDOImpl();

	@Test
	public void testFixCampaignActivitiesExtras() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

			Map<String, String> movies = CollectionFactory.createMap(); 
			List<Movie> movieList = movieDao.getAll();
			for( Movie obj : movieList ) {
				movies.put(obj.getIdentifier(), obj.getName());
			}

			Range range = new Range(0,100);

			List<CampaignActivity> list = caDao.getUsingDatesAndCampaignSpecial(sdf.parse("2015-06-26"), sdf.parse("2015-08-01"), "1430288511084", range, "creationDateTime");
			while(!CollectionUtils.isEmpty(list)) {

				for( CampaignActivity ca : list ) {

					try {
						JSONObject extras = new JSONObject(ca.getExtras().getValue());
						if(!extras.has("showtimeId")) {

							String showDate = sdf.format(new Date(extras.getLong("showDateTime")));
							String showTime = sdf2.format(new Date(extras.getLong("showDateTime")));
							String cinemaId = "cinepolis_mx_339";
							String movieId = getMovieByName(movies, extras.getString("name"));
							System.out.println(ca.getCreationDateTime());

							List<Showtime> shows = showDao.getUsingCinemaAndMovieAndDate(cinemaId, movieId, showDate, showTime);
							if(!CollectionUtils.isEmpty(shows)) {
								extras.put("showtimeId", shows.get(0).getIdentifier());
							}

							ca.setExtras(new Text(extras.toString()));
							caDao.update(ca);
						}

					} catch( Throwable t ) {
						t.printStackTrace();
					}
				}

				// new page
				range.setFrom(range.getTo());
				range.setTo(range.getTo() + 100);
				list = caDao.getUsingDatesAndCampaignSpecial(sdf.parse("2015-06-26"), sdf.parse("2015-08-01"), "1430288511084", range, "creationDateTime");
			}



		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void testClearMovieCinemas() {
		try {
			Range range = new Range(0,100);

			List<Movie> list = movieDao.getUsingStatusAndRange(null, range, null); 
			while(!CollectionUtils.isEmpty(list)) {

				for( Movie obj : list ) {

					try {
						obj.getCinemas().clear();
						movieDao.update(obj);
					} catch( Throwable t ) {
						t.printStackTrace();
					}
				}

				// new page
				range.setFrom(range.getTo());
				range.setTo(range.getTo() + 100);
				list = movieDao.getUsingStatusAndRange(null, range, null);
			}
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}


	@Test
	public void testFixCampaignActivitiesExtras2() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			List<CampaignActivity> list = caDao.getUsingDatesAndCampaignSpecial(sdf.parse("2015-06-26"), sdf.parse("2015-08-01"), 
					"1430288511084", null, "creationDateTime");
			while(!CollectionUtils.isEmpty(list)) {

				for( CampaignActivity ca : list ) {

					try {
						if( ca.getViewDateTime() == null ) {
							if (ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_ACCEPTED)
									|| ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REDEEMED)
									|| ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REJECTED)) {
								if( ca.getStatusChangeDateTime() != null ) {
									ca.setViewDateTime(ca.getStatusChangeDateTime());
								} else {
									ca.setViewDateTime(ca.getCreationDateTime());
								}
								System.out.println(ca.getCreationDateTime());
								caDao.update(ca);
							}
						}
					} catch( Throwable t ) {
						t.printStackTrace();
					}
				}

				list = CollectionFactory.createList();
			}



		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	public String getMovieByName(Map<String, String> movies, String name) {
		Iterator<String> i = movies.keySet().iterator();
		String newName = null;
		while(i.hasNext()) {
			String key = i.next();
			String val = movies.get(key);

			if( name.indexOf("?") > 0 ) {
				newName = name.substring(0, name.indexOf("?"));
			} else {
				newName = name;
			}

			if( val.startsWith(newName)) {
				System.out.println("Movie " + name + " is " + val);
				return key;
			}
		}

		System.out.println("Didn't find a movie for " + name);

		return null;
	}

	@Test
	public void testFixCinemaIdInPromos() {
		CampaignActivityDAO caDao = new CampaignActivityDAOJDOImpl();
		//		CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
		String CAMPAIGN_SPECIAL_IDS[] = new String[] {
				"1432724531038", /* Bagui */ 
		"1432724594627" /* Crepa */ };

		try {
			for( String CAMPAIGN_SPECIAL : CAMPAIGN_SPECIAL_IDS ) {
				List<CampaignActivity> list = caDao.getUsingDatesAndCampaignSpecial(null, null, CAMPAIGN_SPECIAL, null, null);
				for(CampaignActivity obj : list ) {
					try {
						JSONObject extras = new JSONObject(obj.getExtras().getValue());
						if( extras.has("cinema") && !extras.has("cinemaId")) {
							if( extras.getString("cinema").contains("Las Am") && !extras.getString("cinema").contains("VIP")) {
								//								System.out.println("cinema " + extras.getString("cinema") + " is Las Americas");
								extras.put("cinemaId", "cinepolis_mx_339");
								obj.setExtras(new Text(extras.toString()));
								caDao.update(obj);
							} else if( extras.getString("cinema").contains("Centro")) {
								System.out.println("cinema " + extras.getString("cinema") + " is Morelia Centro");
								extras.put("cinemaId", "cinepolis_mx_96");
								obj.setExtras(new Text(extras.toString()));
								caDao.update(obj);
							} else {
								System.out.println(extras.getString("cinema"));
							}
						}
					} catch( Exception e ) {}
				}
			}
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void testFixCampaignActivityRedeemStatus() {
		try {
			VoucherDAO voucherDao = new VoucherDAOJDOImpl();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String CAMPAIGN_SPECIAL_ID = "1430288511084";
			Date checkDate = sdf.parse("2015-07-04");
			Date limitDate = sdf.parse("2015-09-10");
			List<CampaignActivity> campaignActivityList = caDao.getUsingDatesAndCampaignSpecial(checkDate, limitDate, CAMPAIGN_SPECIAL_ID, null, "creationDateTime");
			for( CampaignActivity ca : campaignActivityList ) {
				if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REDEEMED)) {
					JSONObject extras = new JSONObject(ca.getExtras().getValue());
					JSONArray vouchers = extras.getJSONArray("suggestedCoupons");
					for( int i = 0; i < vouchers.length(); i++ ) {
						Voucher v = voucherDao.get(vouchers.getString(i));
						if(!v.getStatus().equals(3)) {
							System.out.println("Rejecting ca " + ca);
							ca.setRedeemDateTime(null);
							ca.setRedeemStatus(CampaignActivity.REDEEM_STATUS_DELIVERED);
							caDao.update(ca);
							break;
						}
					}
				}
			}
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test 
	public void testFixCinemaShoppings() {
		try {
			
			CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
			Cinema cinema;
			
			cinema = cinemaDao.get("cinepolis_mx_339", true);
			cinema.setShoppingId("espaciolasamericas");
			cinemaDao.update(cinema);
			
			cinema = cinemaDao.get("cinepolis_mx_166", true);
			cinema.setShoppingId("galeriasvalleoriente");
			cinemaDao.update(cinema);
			
			cinema = cinemaDao.get("cinepolis_mx_449", true);
			cinema.setShoppingId("interlomas");
			cinemaDao.update(cinema);
			
			cinema = cinemaDao.get("cinepolis_mx_479", true);
			cinema.setShoppingId("towncenterelrosario");
			cinemaDao.update(cinema);
			
			cinema = cinemaDao.get("cinepolis_mx_59", true);
			cinema.setShoppingId("granplazaguadalajara");
			cinemaDao.update(cinema);
			
		} catch( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test 
	public void testFixCinemaEnabled() {
		try {
			
			CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
			Cinema cinema;
			
			cinema = cinemaDao.get("cinepolis_mx_166", true);
			cinema.setStatus(Cinema.STATUS_DISABLED);
			cinemaDao.update(cinema);
			
		} catch( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
