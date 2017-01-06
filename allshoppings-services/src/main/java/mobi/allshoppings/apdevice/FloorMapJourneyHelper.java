package mobi.allshoppings.apdevice;

import java.util.HashMap;
import java.util.List;

import mobi.allshoppings.exception.ASException;

public interface FloorMapJourneyHelper {
	
	public List<String> select(HashMap<Integer, String[]> mostValue);
	public HashMap<Integer, String[]> mostValuable(HashMap<Integer, String[]> map2);
	public HashMap<Integer, List<String>> reverse(HashMap<String, Integer> map);
	public String merge(List<String> arr);
	public void process() throws ASException;
}
