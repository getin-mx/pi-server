package mobi.allshoppings.apdevice.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	/**
	 * 
	 */
	private static final Logger log = Logger.getLogger(FloorMapJourneyHelperImpl.class.getName());

	@Autowired
	private FloorMapJourneyDAO fmjDao = new FloorMapJourneyDAOJDOImpl();
	
	private int LIMIT = 10;
	
	public void process() throws ASException{

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<Integer, List<String>> map2 = new HashMap<Integer, List<String>>();
		String myWord;
		Integer count= new Integer(0);
		Range range = new Range(0, 100);
		List<FloorMapJourney> fmj = fmjDao.getUsingRange(range);
		

		for (FloorMapJourney curr : fmj){
			List<String> word =	curr.getWord();
			myWord = merge(word);
			count = map.get(myWord);
			if(null == count)
				count = 1;
			else
				count++;
			System.out.println("<"+ myWord + ">"+" : "+ count);
			map.put(myWord, count);
		}
		
		System.out.println("map.size ==>" + map.size());
		System.out.println("map: "+ map);
		
		map2 = reverse(map);
		System.out.println("map2: "+ map2);
		
		
		
		System.out.println("------- ");
		//HashMap<Integer, String[]> mvalue = mostValuable(map2);
		//System.out.println("mvalue: "+ mvalue);
		//select(mvalue);
	}

	/**
	 * Function for convert array to String
	 * @param arr
	 * @return
	 */
	public String merge(List<String> arr){
		String ret="";
		for(String elem : arr) {
			ret = ret + elem + ',';
		}
		return ret;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public HashMap<Integer, List<String>> reverse(HashMap<String, Integer> map) {
		
		HashMap<Integer, List<String>> ret = new HashMap<Integer, List<String>>();
		String currkey;
		Integer currvalue ;
		List<String> fullwords = CollectionFactory.createList();
		
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			currvalue = new Integer(entry.getValue());
			currkey = entry.getKey();
			
			//validamos la llave 
			if(!ret.containsKey(currvalue)){
				//Insert nueva llave.
				fullwords.clear();
				fullwords.add(currkey);
				ret.put(currvalue,fullwords);
			}
			else{
				System.out.println("ret: "+ ret);
				fullwords.clear();
				fullwords = ret.get(1);
				System.out.println("current list:: "+ fullwords);
				fullwords.add(currkey);
				ret.put(currvalue,fullwords);
			}
			System.out.println("------");
			System.out.println("ret: "+ ret);
		}
    
		return ret;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public HashMap<Integer, String[]> mostValuable(HashMap<Integer, String[]> map2) {

		HashMap<Integer, String[]> ret = new HashMap<Integer, String[]>();
		//keys = map.getKeys.sort;
		List<Integer> keys = CollectionFactory.createList();
		
		for (Map.Entry<Integer, String[]> entry : map2.entrySet() ) {
			keys.add(entry.getKey());
		}
		
		keys.sort(null);
		System.out.println("keys: " + keys + "size: "+ keys.size());
		
		for(int  i = keys.size()- 1; i >= 0 && ret.size() < 10; i--) {
			log.info("("+i+"): " + "key: " +  keys.get(i) + "Value: " + map2.get(i));
			ret.put(keys.get(i),map2.get(i));
		}
		return ret;
}
	/**
	 * 
	 * @param mostValue
	 * @return
	 */
	public List<String> select(HashMap<Integer, String[]> mostValue) {
		List<String> ret = CollectionFactory.createList();
		int currkey;
		String[] currvalue;

		for(Map.Entry<Integer, String[]> entry : mostValue.entrySet()){
			currvalue = entry.getValue();
			currkey = entry.getKey();
			System.out.println("key:<"+currkey+"> Value:<"+currvalue.toString()+">");
			if(ret.size() < LIMIT){
				ret.add(currvalue.toString());
			System.out.println("key:<"+currkey+"> Value:<"+currvalue.toString()+">");
			}
			else
				break;
			
		}
		System.out.println("ret: "+ret);
		return ret;
	}
}
