package mobi.allshoppings.model.adapter;

import java.util.List;

import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.tools.CollectionFactory;

public class DeviceLocationAdapter extends DeviceLocation implements IGenericAdapter {

	private static final long serialVersionUID = 5657858175564391408L;

	private List<HotSpotAdapter> nearSpots = CollectionFactory.createList();
	private long requestInterval = 0;
	private long reportInterval = 0;
	private long wifiInterval = 0;
	private String beaconProximityUUID = "";
	private String baseUrl = "";
	private long dwellTime = 0;

	/**
	 * @return the nearSpots
	 */
	public List<HotSpotAdapter> getNearSpots() {
		return nearSpots;
	}

	/**
	 * @param nearSpots the nearSpots to set
	 */
	public void setNearSpots(List<HotSpotAdapter> nearSpots) {
		this.nearSpots = nearSpots;
	}

	/**
	 * @return the requestInterval
	 */
	public long getRequestInterval() {
		return requestInterval;
	}

	/**
	 * @param requestInterval the requestInterval to set
	 */
	public void setRequestInterval(long requestInterval) {
		this.requestInterval = requestInterval;
	}

	/**
	 * @return the reportInterval
	 */
	public long getReportInterval() {
		return reportInterval;
	}

	/**
	 * @param reportInterval the reportInterval to set
	 */
	public void setReportInterval(long reportInterval) {
		this.reportInterval = reportInterval;
	}

	/**
	 * @return the beaconProximityUUID
	 */
	public String getBeaconProximityUUID() {
		return beaconProximityUUID;
	}

	/**
	 * @param beaconProximityUUID the beaconProximityUUID to set
	 */
	public void setBeaconProximityUUID(String beaconProximityUUID) {
		this.beaconProximityUUID = beaconProximityUUID;
	}

	/**
	 * @return the wifiInterval
	 */
	public long getWifiInterval() {
		return wifiInterval;
	}

	/**
	 * @param wifiInterval the wifiInterval to set
	 */
	public void setWifiInterval(long wifiInterval) {
		this.wifiInterval = wifiInterval;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the dwellTime
	 */
	public long getDwellTime() {
		return dwellTime;
	}

	/**
	 * @param dwellTime the dwellTime to set
	 */
	public void setDwellTime(long dwellTime) {
		this.dwellTime = dwellTime;
	}
	
}
