package mobi.allshoppings.model.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.PropertyUtils;


public class GenericAdapterImpl<T extends IGenericAdapter> {

	private static final Logger logger = Logger.getLogger(GenericAdapterImpl.class.getName());
	
	public T adapt(Identificable obj)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, 
			NoSuchMethodException, ClassNotFoundException, ASException {
		return adapt(obj, null, null, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public T adapt(Identificable obj, String requester, Class<T> adapter, List<String> favorites, Map<String, Object> options)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, 
			NoSuchMethodException, ClassNotFoundException, ASException {

		if( adapter == null ) {
			String clazzName = "mobi.allshoppings.model.adapter." + obj.getClass().getSimpleName() + "Adapter";
			adapter = (Class<T>)Class.forName(clazzName);
		}
		T ga = adapter.newInstance();
		
		Map<String, Object> properties = PropertyUtils.describe(obj);
		Iterator<String> i = properties.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
			try {
				Method method = obj.getClass().getMethod(methodName);
				Object property = method.invoke(obj);
				try {
					BeanUtils.setProperty(ga, key, property);
				} catch (ConversionException e ) {
					// nothing to do here!
				}
			} catch( NoSuchMethodException e1 ) {
				// nothing to do here!
			}
		}

		if( ga instanceof ICompletableAdapter) {
			((ICompletableAdapter)ga).completeAdaptation(options);
		}
		
		try {
			if( ga instanceof IFavorite && (requester != null || favorites != null)) {
				logger.log(Level.INFO, "obj " + obj.getIdentifier() + (CollectionUtils.contains(favorites, obj.getIdentifier()) ? " is favorite" : " is not favorite" ) + " for " + requester);
				((IFavorite)ga).setFavorite(CollectionUtils.contains(favorites, obj.getIdentifier()));
				((IFavorite)ga).setRequester(requester);
			}
		} catch( Throwable t ) {
			// Nothing to do
		}

		
		return ga;
	}

	public List<T> adaptList(List<?> list) 
			throws InstantiationException, IllegalAccessException, InvocationTargetException, 
			NoSuchMethodException, ClassNotFoundException, ASException {
		return adaptList(list, null, null, null, null);
	}
	
	public List<T> adaptList(List<?> list, String requester, Class<T> adapter, List<String> favorites, Map<String, Object> options) 
			throws InstantiationException, IllegalAccessException, InvocationTargetException, 
			NoSuchMethodException, ClassNotFoundException, ASException {
		
		List<T> ret = CollectionFactory.createList();
		Iterator<?> i = list.iterator();
		while(i.hasNext()) {
			ret.add(adapt((Identificable)i.next(), requester, adapter, favorites, options));
		}
		return ret;
	}

}
