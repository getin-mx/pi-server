package mobi.allshoppings.apdevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.tools.Range;

public interface FloorMapJourneyHelper {
	
	public List<String> select(Map<Integer, List<String>> mostValue,Integer limit);
	public HashMap<Integer, String[]> mostValuable(HashMap<Integer, String[]> map2);
	public Map<Integer, List<String>> reverse(HashMap<String, Integer> map);
	public String merge(List<String> arr);
	public void process(Integer limit,Range range) throws ASException;
}
