package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.interfaces.StatusAware;


public class ModatelasDumpPoints extends AbstractCLI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ModatelasDumpPoints.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			WifiSpotDAO wifispotDao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");
			FloorMapDAO fmDao = (FloorMapDAO)getApplicationContext().getBean("floormap.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");

			List<Store> stores = storeDao.getUsingBrandAndStatus("modatelas_mx", Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null);
			for( Store store : stores ) {
				if( store.getName().contains("Tacubaya")) {
					List<FloorMap> fms = fmDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, store.getIdentifier());
					for( FloorMap fm : fms ) {
						int minX = 100000;
						int maxX = 0;
						int minY = 100000;
						int maxY = 0;
						int midX = 0;
						int midY = 0;
						
						WifiSpot[] points = new WifiSpot[] {null,null,null,null,null};
						int[] dist = new int[] {99999,99999,99999,99999,99999};
						
						List<WifiSpot> list = wifispotDao.getUsingFloorMapId(fm.getIdentifier());
						for( WifiSpot ws : list ) {
							System.out.println(ws);
							if( ws.getX() < minX ) minX = ws.getX();
							if( ws.getX() > maxX ) maxX = ws.getX();
							if( ws.getY() < minY ) minY = ws.getY();
							if( ws.getY() > maxY ) maxY = ws.getY();
						}
						
						midX = minX + ((maxX - minX) / 2);
						midY = minY + ((maxY - minY) / 2);
						System.out.println("MinX: " + minX );
						System.out.println("MinY: " + minY );
						System.out.println("MaxX: " + maxX );
						System.out.println("MaxY: " + maxY );
						System.out.println("MidX: " + midX );
						System.out.println("MidY: " + midY );

						int cat1 = 0;
						int cat2 = 0;
						int hypo = 0;
						for( WifiSpot ws : list ) {
							// posicion 0
							cat1 = ws.getX() - minX;
							cat2 = ws.getY() - minY;
							hypo = (int)Math.sqrt(Math.pow(cat1, 2) + Math.pow(cat2, 2));
							if( hypo < dist[0] ) {
								dist[0] = hypo;
								points[0] = ws;
							}

							// posicion 1
							cat1 = maxX - ws.getX();
							cat2 = ws.getY() - minY;
							hypo = (int)Math.sqrt(Math.pow(cat1, 2) + Math.pow(cat2, 2));
							if( hypo < dist[1] ) {
								dist[1] = hypo;
								points[1] = ws;
							}

							// posicion 2
							cat1 = ws.getX() - minX;
							cat2 = maxY - ws.getY();
							hypo = (int)Math.sqrt(Math.pow(cat1, 2) + Math.pow(cat2, 2));
							if( hypo < dist[3] ) {
								dist[2] = hypo;
								points[2] = ws;
							}

							// posicion 3
							cat1 = maxX - ws.getX();
							cat2 = maxY - ws.getY();
							hypo = (int)Math.sqrt(Math.pow(cat1, 2) + Math.pow(cat2, 2));
							if( hypo < dist[3] ) {
								dist[3] = hypo;
								points[3] = ws;
							}

							// posicion 4
							cat1 = ws.getX() > midX ? ws.getX() - midX : midX - ws.getX();
							cat2 = ws.getY() > midY ? ws.getY() - midY : midY - ws.getY();
							if( cat2 < 0 ) cat2 = cat2 * -1;
							hypo = (int)Math.sqrt(Math.pow(cat1, 2) + Math.pow(cat2, 2));
							if( hypo < dist[4] ) {
								dist[4] = hypo;
								points[4] = ws;
							}

						}
					
						points[0].setApDevice("ashs-0104");
						wifispotDao.update(points[0]);
						points[1].setApDevice("ashs-0098");
						wifispotDao.update(points[1]);
						points[2].setApDevice("ashs-0102");
						wifispotDao.update(points[2]);
						points[3].setApDevice("ashs-0103");
						wifispotDao.update(points[3]);
						points[4].setApDevice("ashs-0105");
						wifispotDao.update(points[4]);
						
						System.out.println("Pos 0: " + points[0]);
						System.out.println("Pos 1: " + points[1]);
						System.out.println("Pos 2: " + points[2]);
						System.out.println("Pos 3: " + points[3]);
						System.out.println("Pos 4: " + points[4]);
						
					}
				}
			}


		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

}
