package mobi.allshoppings.apdevice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.FloorMapJourneyHelper;
import mobi.allshoppings.dao.FloorMapJourneyDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.FloorMapJourney;
import mobi.allshoppings.tools.CollectionFactory;
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
	private FloorMapJourneyDAO fmjDao;
	
	public void process() throws ASException{

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String myWord;
		List<FloorMapJourney> fmj = fmjDao.getAll();
		List<Integer> mvalue;

		for (FloorMapJourney curr : fmj){
			List<String> word =	curr.getWord();
			myWord = merge(word);
			int count = map.get(myWord);
			count++;
			map.put(myWord, count);
			
		}
		
		//map2 = reverse(map);
		mvalue = mostValuable(map);
		
		//results = select(array, map2);
		
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
	
	public void reverse(HashMap<String, String> map) {
	/*	HashMap<String, String> ret;
		
		for( Map.Entry<String, String> elem : map) {
			key = element;
			val = map.get(element);
			
			array = ret.get(val);
			array.push(key)
			ret.put(val);
		}
		*/
	}
	/**
	 * 
	 * @param map
	 * @return
	 */
	public List<Integer> mostValuable(HashMap<String, Integer> map) {
	
		List<Integer> ret = CollectionFactory.createList();
		//keys = map.getKeys.sort;
		List<String> keys = CollectionFactory.createList();;
		for ( String key : map.keySet() ) {
			keys.add(key);
		}
		keys.sort(null);
		
		for(int  i = keys.size(); i > 0 && ret.size() < 10; i--) {
			ret.add(map.get((keys.get(i))));
		}
		return ret;

}
	
	
	
	

}
