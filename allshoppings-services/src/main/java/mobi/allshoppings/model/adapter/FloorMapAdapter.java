package mobi.allshoppings.model.adapter;

import java.util.Map;

import org.springframework.util.StringUtils;

import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;

public class FloorMapAdapter extends FloorMap implements IGenericAdapter, ICompletableAdapter {

	private static final long serialVersionUID = 5657858175564391408L;
	public static final String OPTIONS_SHOPPINGDAO = "ShoppingDAO";
	public static final String OPTIONS_STOREDAO = "SotreDAO";
	
	private String shoppingName;

	/**
	 * @return the shoppingName
	 */
	public String getShoppingName() {
		return shoppingName;
	}
	/**
	 * @param shoppingName the shoppingName to set
	 */
	public void setShoppingName(String shoppingName) {
		this.shoppingName = shoppingName;
	}

	@Override
	public void completeAdaptation(Map<String, Object> options) throws ASException {
		ShoppingDAO shoppingDao = (ShoppingDAO)options.get(OPTIONS_SHOPPINGDAO);
		StoreDAO storeDao = (StoreDAO)options.get(OPTIONS_STOREDAO);
		if( shoppingDao == null ) return;

		if( StringUtils.hasText(getShoppingId())) {
			try {
				if( null != shoppingDao ) {
					Shopping shopping = shoppingDao.get(getShoppingId());
					setShoppingName(shopping.getName());
				}
			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					try {
						if( null != storeDao ) {
							Store store = storeDao.get(getShoppingId());
							setShoppingName(store.getName());
						}
					} catch( ASException e1) {
						if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
							return;
						} else {
							throw e;
						}
					}
					
					return;
				} else {
					throw e;
				}
			}
		}
	}

}
