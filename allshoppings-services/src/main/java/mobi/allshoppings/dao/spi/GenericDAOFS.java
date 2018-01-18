package mobi.allshoppings.dao.spi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.CacheHelper;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.GsonFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public abstract class GenericDAOFS<T extends ModelKey> implements GenericDAO<T> {

	protected KeyHelper keyHelper = new KeyHelperGaeImpl();
	@Autowired
	protected IndexHelper indexHelper;
	@Autowired
	protected CacheHelper cacheHelper;
	
	protected Gson gson = GsonFactory.getInstance();

	Class<T> clazz;
	Logger log;

	/**
	 * Generic Constructor
	 * 
	 * @param clazz
	 */
	public GenericDAOFS(Class<T> clazz) {
		super();
		this.clazz = clazz;
		this.log = Logger.getLogger(clazz.getName());
	}

	/**
	 * Obtains the class affected by this DAO
	 */
	@Override
	public Class<T> getAffectedClass() {
		return clazz;
	}

	/**
	 * @return the indexHelper
	 */
	public IndexHelper getIndexHelper() {
		return indexHelper;
	}

	/**
	 * Creates the file name
	 * @param identifier
	 * @return
	 * @throws ASException
	 */
	public abstract String resolveFileName(String identifier) throws ASException;
	
	@Override
	public T get(String identifier) throws ASException {
		String filename = resolveFileName(identifier);
		File f = new File(filename);
		return deserialize(f);
	}
	
	public T deserialize(File f) throws ASException {
		if( gson == null ) 
			gson = new Gson();
		
		if( f.exists() && f.isFile() && f.canRead()) {
			try {
				try(BufferedReader br = new BufferedReader(new FileReader(f))) {
					for(String line; (line = br.readLine()) != null ;) {
						T element = (T) gson.fromJson(line, clazz);
						br.close();
						return(element);
					}
				}
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
		}
		
		throw ASExceptionHelper.notFoundException(f.getAbsolutePath());
	}

	@Override
	public T get(String identifier, Boolean detachable) throws ASException {
		return get(identifier);
	}

	@Override
	public T get(PersistenceProvider pp, String identifier, Boolean detachable) throws ASException {
		return get(identifier);
	}

	@Override
	public List<T> getAll() throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAll(boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAll(PersistenceProvider pp) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAll(PersistenceProvider pp, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingRange(Range range) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingRange(Range range, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingRange(PersistenceProvider pp, Range range) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingRange(PersistenceProvider pp, Range range, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingIdList(List<String> idList) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingIdList(List<String> idList, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingIdList(PersistenceProvider pp, List<String> idList) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingIdList(PersistenceProvider pp, List<String> idList, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAllAndOrder(String order) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAllAndOrder(String order, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAllAndOrder(PersistenceProvider pp, String order) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getAllAndOrder(PersistenceProvider pp, String order, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingStatusAndRange(List<Byte> status, Range range, String order) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingStatusAndRange(List<Byte> status, Range range, String order,
			Map<String, String> aattributes, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingStatusAndRangeAndCountry(List<Byte> status, Range range, String country, String order,
			Map<String, String> aattributes, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingStatusAndRange(PersistenceProvider pp, List<Byte> status, Range range, String order)
			throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingStatusAndRange(PersistenceProvider pp, List<Byte> status, Range range, String order,
			Map<String, String> attributes, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingLastUpdateStatusAndRange(PersistenceProvider pp, Date lastUpdate,
			boolean afterLastUpdateDate, List<Byte> status, Range range, String order,
			Map<String, String> attributes, boolean detachable) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public void create(T obj) throws ASException {
		String filename = resolveFileName(obj.getIdentifier());
		File f = new File(filename);
		File dir = f.getParentFile();

		if(!dir.exists())
			dir.mkdirs();

		if( f.exists() ) 
			f.delete();

		try {
			FileOutputStream fos = new FileOutputStream(f);
			if( gson == null )
				gson = new Gson();
			String jsonObject = gson.toJson(obj);
			fos.write(jsonObject.getBytes());
			fos.close();
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}

	@Override
	public void create(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException {
		create(obj);
	}

	@Override
	public void createOrUpdate(T obj) throws ASException {
		create(obj);
	}

	@Override
	public void createOrUpdate(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException {
		create(obj);
	}

	@Override
	public void createOrUpdate(PersistenceProvider pp, List<T> obj, boolean performPreStore) throws ASException {
		for(T o : obj) {
			create(o);
		}
	}

	@Override
	public void update(T obj) throws ASException {
		create(obj);
	}

	@Override
	public void update(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException {
		create(obj);
	}

	@Override
	public void delete(T obj) throws ASException {
		delete(obj.getIdentifier());
	}

	@Override
	public void delete(PersistenceProvider pp, T obj) throws ASException {
		delete(obj.getIdentifier());
	}

	@Override
	public void delete(String identifier) throws ASException {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(PersistenceProvider pp, String identifier) throws ASException {
		delete(identifier);
	}

	@Override
	public void deleteAll() throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public void deleteAll(PersistenceProvider pp) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingIndex(String q, ViewLocation viewLocation, List<Byte> status, Range range,
			Map<String, String> additionalFields, String order, String lang) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getUsingIndex(String indexName, String q, ViewLocation viewLocation, List<Byte> status,
			Range range, Map<String, String> additionalFields, String order, String lang) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public long count() throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public long count(UserInfo userInfo) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public long count(String keyName, String keyValue, UserInfo userInfo) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public long count(List<Byte> status) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getForTable(String[] columnSort, String sortDirection, String[] searchFields, String search,
			long first, long last, UserInfo userInfo) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public List<T> getForTableWidthKey(String keyName, String keyValue, String[] columnSort, String sortType,
			String[] searchFields, String search, long first, long last, UserInfo userInfo) throws ASException {
		throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	public String toParameterList(List<String> parms) {
		return null;
	}

	@Override
	public String toWellParametrizedFilter(List<String> filters) {
		return null;
	}

	@Override
	public String toWellParametrizedFilter(Map<String, Object> map) {
		return null;
	}

	@Override
	public CustomDatatableFilter buildCustomFilter(UserInfo userInfo) {
		return null;
	}

	@Override
	public boolean safeAndInLimits(T obj, UserInfo userInfo) {
		return true;
	}

}
