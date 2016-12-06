package mobi.allshoppings.bdb.bz.spi;

import mobi.allshoppings.bdb.bz.BDBGetBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;

public class BDBIPAddressBzServicePlainImpl extends BDBRestBaseServerResource implements BDBGetBzService {

	@Override
	public String retrieve() {
		return getRequestIP();
	}

}
