package mobi.allshoppings.apdevice.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.FloorMapJourneyHelper;
import mobi.allshoppings.dao.FloorMapJourneyDAO;
import mobi.allshoppings.dao.spi.FloorMapJourneyDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.FloorMapJourney;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

/**
 * 
 * @author getin-dev
 * 
 */
public class FloorMapJourneyHelperImpl implements FloorMapJourneyHelper {


	private static final Logger log = Logger.getLogger(FloorMapJourneyHelperImpl.class.getName());

	@Autowired
	private FloorMapJourneyDAO fmjDao = new FloorMapJourneyDAOJDOImpl();
	
	/**
	 * 
	 */
	public List<FloorMapJourney> process(String floorMapId, String mac, String fromDate, String toDate, Range range) throws ASException {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		Map<Integer, List<String>> map2 = new HashMap<Integer, List<String>>();
		
		Map<String, FloorMapJourney> res = new HashMap<String, FloorMapJourney>();
		FloorMapJourney elem;
		
		List<FloorMapJourney> ret = CollectionFactory.createList();
		String myWord;
		Integer count = new Integer(0);

		//TODO: change method getUsingRange by getUsingFloorMapAndMacAndDate
		List<FloorMapJourney> fmj = fmjDao.getUsingFloorMapAndMacAndDate(floorMapId, mac, fromDate, toDate,null,null);
		//iterate
		for (FloorMapJourney curr : fmj) {
			List<String> word = curr.getWord();
			myWord = merge(word);
			count = map.get(myWord);
			if (null == count)
				count = 1;
			else
				count++;
			
			map.put(myWord, count);
			
			elem = res.get(myWord);
			if(null == elem){
				res.put(myWord, curr);
			}
			else{
				if(curr.getDataCount()< elem.getDataCount()){
					res.put(myWord, elem);
				}
			}
			
			// System.out.println("<"+ myWord + ">"+" : "+ count);

		}

		log.log(Level.INFO, "map.size ==>" + map.size());
		log.log(Level.INFO,"map: " + map);

		
		log.log(Level.INFO, "map.size ==>" + res.size());
		log.log(Level.INFO,"map: " + res);

		
		map2 = reverse(map);
		log.log(Level.INFO,"map2: " + map2);

		log.log(Level.INFO,"------- ");
		// HashMap<Integer, String[]> mvalue = mostValuable(map2);
		// System.out.println("mvalue: "+ mvalue);
		
		//TODO change hard value 10 by real limit
		//List<String> result = select(map2,10);
		
		List<String> result = select(map2,10);
		log.log(Level.INFO,"result: " + result);

		for (String word : result) {
			ret.add(res.get(word));
		}
		log.log(Level.INFO,"res:: "+res);
		
		
		return ret;
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public String clearWord(String str) {
		if (str != null && str.length() > 0
				&& str.charAt(str.length() - 1) == ',') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * Function for convert array to String
	 * 
	 * @param arr
	 * @return
	 */
	public String merge(List<String> arr) {
		String ret = "";
		for (String elem : arr) {
			ret = ret + elem + ',';
		}
		return clearWord(ret);
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public Map<Integer, List<String>> reverse(HashMap<String, Integer> map) {

		HashMap<Integer, List<String>> ret = new HashMap<Integer, List<String>>();
		String currkey;
		Integer currvalue;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			currvalue = new Integer(entry.getValue());
			currkey = entry.getKey();

			// validamos la llave
			// System.out.println("ret: "+ ret);
			List<String> fullwords = ret.get(currvalue);
			// System.out.println("fullwords"+fullwords);
			if (null == fullwords)
				fullwords = CollectionFactory.createList();
			fullwords.add(currkey);
			// System.out.println("current list:: "+ fullwords);
			ret.put(currvalue, fullwords);
		}

		// System.out.println("\nSorted Map......By Key");
		Map<Integer, List<String>> treeMap = new TreeMap<Integer, List<String>>(
				new Comparator<Integer>() {

					@Override
					public int compare(Integer o1, Integer o2) {
						return o2.compareTo(o1);
					}

				});

		treeMap.putAll(ret);

		return treeMap;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public HashMap<Integer, String[]> mostValuable(
			HashMap<Integer, String[]> map2) {

		HashMap<Integer, String[]> ret = new HashMap<Integer, String[]>();
		// keys = map.getKeys.sort;

		List<Integer> keys = CollectionFactory.createList();

		for (Map.Entry<Integer, String[]> entry : map2.entrySet()) {
			keys.add(entry.getKey());
		}
		
		//keys.sort(null);
		
		for(int  i = keys.size()- 1; i >= 0 && ret.size() < 10; i--) {
			log.info("("+i+"): " + "key: " +  keys.get(i) + "Value: " + map2.get(i));
			ret.put(keys.get(i),map2.get(i));
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public List<String> select(Map<Integer, List<String>> mostValue,Integer limit) {

		List<String> ret = CollectionFactory.createList();
		int currkey;

		List<String> currvalue = CollectionFactory.createList();
		for (Entry<Integer, List<String>> entry : mostValue.entrySet()) {
			currvalue = entry.getValue();
			currkey = entry.getKey();
			log.log(Level.INFO,"currkey:: " + currkey + "(size):: " + currvalue.size());
			if (ret.size() < limit) {
				Iterator<String> iterator = currvalue.iterator();
				while (iterator.hasNext()) {
					ret.add(iterator.next());
					if (ret.size() >= limit) {
						break;
					}

				}
			} else
				break;
		}
		// System.out.println("ret: "+ret);
		return ret;
	}
}
