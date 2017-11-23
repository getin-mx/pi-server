package mobi.allshoppings.model;

/**
 * Decribes the daily process configuration: the number of servers involved,
 * and the load of each one.
 * @author <a href="mailto:ignacio@getin.mx" >Ignacio "Nachintoch" Castillo</a>
 * @version 1.0, Daily Process Configuration
 * @since Mark III, november 2017
 */
public class DailyProcessConfiguration {

	private String[] serverList;
	private float[] serversLoad;
	
	public String[] getServerList() {
		return serverList;
	}
	public void setServerList(String[] serverList) {
		this.serverList = serverList;
	}
	public float[] getServersLoad() {
		return serversLoad;
	}
	public void setServersLoad(float[] serversCapacity) {
		this.serversLoad = serversCapacity;
	}
	
}//Daily Process Configuration
