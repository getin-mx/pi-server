package mobi.allshoppings.apdevice;

import mobi.allshoppings.exception.ASException;

public interface IAPDeviceTrigger {

	public void execute(String hostname, String mac, Integer rssi, String metadata) throws ASException;
	
}
