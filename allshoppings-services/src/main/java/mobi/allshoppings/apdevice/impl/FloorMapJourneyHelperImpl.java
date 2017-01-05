package mobi.allshoppings.apdevice.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	private int LIMIT = 10;
	
	public void process() throws ASException{

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<Integer, String[]> map2 = new HashMap<Integer, String[]>();
		String myWord;
		List<FloorMapJourney> fmj = fmjDao.getAll();
		

		for (FloorMapJourney curr : fmj){
			List<String> word =	curr.getWord();
			myWord = merge(word);
			int count = map.get(myWord);
			count++;
			map.put(myWord, count);
		}
		
		map2 = reverse(map);
		HashMap<Integer, String[]> mvalue = mostValuable(map2);
		select(mvalue);
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
	public HashMap<Integer, String[]> reverse(HashMap<String, Integer> map) {
		HashMap<Integer, String[]> ret = new HashMap<Integer, String[]>();
		int i = 0;
		int value = 0;
		String currkey;
		int currvalue ;
		String[] fullwords = new String[10];
		
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			currvalue = entry.getValue();
			currkey = entry.getKey();
			
			if(currvalue == value){
				fullwords[i] = currkey;
				i++;
			}
			else{
				ret.put(currvalue,fullwords);
				fullwords = new String[10];
				i=0;
			}
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
		List<Integer> keys = CollectionFactory.createList();;
		
		for (Map.Entry<Integer, String[]> entry : map2.entrySet() ) {
			keys.add(entry.getKey());
		}
		
		keys.sort(null);
		
		for(int  i = keys.size(); i > 0 && ret.size() < 10; i--) {
			ret.put(keys.get(i),map2.get(i));
			log.info("("+i+"): " + "key: " +  keys.get(i) + "Value: " + map2.get(i));
		}
		return ret;
}
	/**
	 * 
	 * @param mostValue
	 * @return
	 */
	public HashMap<Integer, String[]> select(HashMap<Integer, String[]> mostValue) {
		HashMap<Integer, String[]> ret = new HashMap<Integer, String[]>();
		int currkey;
		String currvalue[];

		for(Map.Entry<Integer, String[]> entry : mostValue.entrySet()){
			currvalue = entry.getValue();
			currkey = entry.getKey();
			if(ret.size() < LIMIT)
				ret.put(currkey,currvalue);
			else
				break;
			
		}
		return ret;
	}
}
