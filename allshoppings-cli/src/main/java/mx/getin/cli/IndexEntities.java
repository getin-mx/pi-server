package mx.getin.cli;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.cli.AbstractCLI;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.IndexHelper;

public class IndexEntities extends AbstractCLI {

	private static final Logger LOG = Logger.getLogger(IndexEntities.class.getName());

	public static OptionParser buildOptionParser(OptionParser base) {
		parser = base == null ? new OptionParser() : base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static void main(String args[]) throws ASException {
		LOG.log(Level.INFO, "Indexing entities...");
		GenericDAO<ModelKey> dao = getEntityDao("brand.dao.ref");
		LOG.log(Level.INFO, "Indexing brands...");
		index(dao.getAll(), dao.getIndexHelper());
		dao = getEntityDao("user.dao.ref");
		LOG.log(Level.INFO, "Indexing users...");
		index(dao.getAll(), dao.getIndexHelper());
		dao = getEntityDao("store.dao.ref");
		LOG.log(Level.INFO, "Indexing stores...");
		index(dao.getAll(), dao.getIndexHelper());
		dao = getEntityDao("apdevice.dao.ref");
		LOG.log(Level.INFO, "Indexing APDevices...");
		index(dao.getAll(),  dao.getIndexHelper());
		LOG.log(Level.INFO, "Process finished!");
	}
	
	@SuppressWarnings("unchecked")
	private static GenericDAO<ModelKey> getEntityDao(String beanKey) {
		return (GenericDAO<ModelKey>) getApplicationContext().getBean(beanKey);
	}
	
	private static void index(List<ModelKey> entities, IndexHelper indexer) throws ASException {
		long total = 0;
		for(ModelKey entity : entities) {
			indexer.indexObject(entity);
			total++;
			if(total %100 == 0) LOG.log(Level.INFO, "Indexed " +total +" of " +entities.size());
		}
	}

}
