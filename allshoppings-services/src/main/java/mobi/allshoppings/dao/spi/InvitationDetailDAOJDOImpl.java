package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.InvitationDetailDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.InvitationDetail;
import mobi.allshoppings.tools.CollectionFactory;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

public class InvitationDetailDAOJDOImpl extends GenericDAOJDO<InvitationDetail> implements InvitationDetailDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(InvitationDetailDAOJDOImpl.class.getName());

	public InvitationDetailDAOJDOImpl() {
		super(InvitationDetail.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(InvitationDetail.class);
	}

	@Override
	public List<InvitationDetail> getUsingInvitedIdAndStatus(String invitedId, Integer status, String source) throws ASException {

		List<InvitationDetail> result = CollectionFactory.createList();
		try {
			PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			Map<String, Object> params = CollectionFactory.createMap();
			Query query = pm.newQuery(InvitationDetail.class);

			List<String> declaredParams = CollectionFactory.createList();
			
			if( status != null ) {
				declaredParams.add("Integer statusParam");
				params.put("statusParam", status);
			}

			if( StringUtils.hasText(invitedId)) {
				declaredParams.add("String invitedIdParam");
				params.put("invitedIdParam", invitedId);
			}

			if( StringUtils.hasText(source)) {
				declaredParams.add("String sourceParam");
				params.put("sourceParam", source);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(params));

			@SuppressWarnings("unchecked")
			List<InvitationDetail> list = (List<InvitationDetail>)query.executeWithMap(params);
			for( InvitationDetail obj : list ) {
				result.add(pm.detachCopy(obj));
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

		return result;

	}

	@Override
	public List<InvitationDetail> getUsingReferralCodeAndStatus(String referralCode, Integer status, String source) throws ASException {
		
		List<InvitationDetail> result = CollectionFactory.createList();
		try {
			PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			Map<String, Object> params = CollectionFactory.createMap();
			Query query = pm.newQuery(InvitationDetail.class);

			List<String> declaredParams = CollectionFactory.createList();
			
			if( status != null ) {
				declaredParams.add("Integer statusParam");
				params.put("statusParam", status);
			}

			if( StringUtils.hasText(referralCode)) {
				declaredParams.add("String referralCodeParam");
				params.put("referralCodeParam", referralCode);
			}

			if( StringUtils.hasText(source)) {
				declaredParams.add("String sourceParam");
				params.put("sourceParam", source);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(params));

			@SuppressWarnings("unchecked")
			List<InvitationDetail> list = (List<InvitationDetail>)query.executeWithMap(params);
			for( InvitationDetail obj : list ) {
				result.add(pm.detachCopy(obj));
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

		return result;

	}

}
