package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.CampaignDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Campaign;
import mobi.allshoppings.model.CampaignAction;

public class CampaignDAOJDOImpl extends GenericDAOJDO<Campaign> implements CampaignDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CampaignDAOJDOImpl.class.getName());

	public CampaignDAOJDOImpl() {
		super(Campaign.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(CampaignAction.class);
	}
	
}
