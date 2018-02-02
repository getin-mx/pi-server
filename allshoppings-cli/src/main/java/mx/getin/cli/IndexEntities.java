package mx.getin.cli;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.cli.AbstractCLI;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.EntityKind;
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

	public static void main(String[] args) throws ASException {
		LOG.log(Level.INFO, "Indexing entities...");
		GenericDAO<ModelKey> dao = getEntityDao("brand.dao.ref");
		LOG.log(Level.INFO, "Indexing brands...");
		index(dao.getAll(), dao.getIndexHelper());
		dao = getEntityDao("user.dao.ref");
		LOG.log(Level.INFO, "Indexing users...");
		index(dao.getAll(), dao.getIndexHelper());
		StoreDAO sDao = (StoreDAO) getApplicationContext().getBean("store.dao.ref");
		LOG.log(Level.INFO, "Indexing stores...");
		index(dao.getAll(), sDao.getIndexHelper());
		dao = getEntityDao("apdevice.dao.ref");
		List<ModelKey> devs = dao.getAll();
		LOG.log(Level.INFO, "Completing Antennas' defaults");
		updateAntennas(devs, dao, sDao);
		LOG.log(Level.INFO, "Indexing APDevices...");
		index(devs,  dao.getIndexHelper());
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
	
	private static void updateAntennas(List<ModelKey> devs, GenericDAO<ModelKey> dao, StoreDAO storeDao) {
		String id = null;
		APDAssignationDAO assigDao = (APDAssignationDAO) getApplicationContext().getBean("apdassignation.dao.ref");
		Date now = new Date();
		InnerZoneDAO zoneDao = (InnerZoneDAO) getApplicationContext().getBean("innerzone.dao.ref");
		for(ModelKey dev : devs) {
			APDevice d = (APDevice) dev;
			d.completeDefaults();
			try {
				List<APDAssignation> assigs = assigDao.getUsingHostnameAndDate(d.getHostname(), now);
				if(assigs.isEmpty()) continue;
				boolean inShopping = false;
				for(APDAssignation assig : assigs) {
					if(assig.getEntityKind() == EntityKind.KIND_INNER_ZONE) {
						inShopping = true;
						id = assig.getEntityId();
						break;
					}
				}
				d.setDescription(inShopping ? zoneDao.get(id).getName() :
					storeDao.get(assigs.get(0).getEntityId()).getName());
			} catch(ASException e) {
				LOG.log(Level.WARNING, "Couldn't update antenna description", e);
			} try {
				dao.update(d);//TODO optimizar
			} catch(ASException e) {
				LOG.log(Level.WARNING, "Couldn't update antenna", e);
			}
			
		}
	}

}
