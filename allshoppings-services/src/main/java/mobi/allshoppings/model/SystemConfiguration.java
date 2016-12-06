package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SystemConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	private int authTokenValidityInDays;
	private int appTokenValidityInDays;
	private String defaultLevel;
	private String defaultLevelOnShoppingBzService;
	private String defaultLevelOnBrandBzService;
	private String defaultLevelOnUserBzService;
	private String ownLevelOnUserBzService;
	private int defaultFromRange;
	private int defaultToRange;
	private long maxUploadSize;
	private Boolean defaultFavoritesFirst;
	private String baseShareURL;
	private String baseImageURL;
	private String facebookShareCaption;
	private String homeCarouselImages;
	private int maxNearMeLocations;
	private String defaultViewLocationCountry;
	private Boolean saveLocationHistory;
	private String collapseKey;
	private int pushMessageRetries;
	private String pushMessageResponseUrl;
	private long requestLocationLimit;
	private String pushMessageOfferTitle;
	private int defaultFenceSize;
	private int defaultCheckinAreaSize;
	private int defaultTaskChunkSize;
	private boolean useFakeMessages;
	private boolean useFakeMails;
	private String defaultLang;
	private long deviceLocationRequestInterval;
	private long deviceLocationReportInterval;
	private String trackerUrl;
	private String trackerId;
	private String trackerTokenAuth;
	private String generalAppMail;
	private String generalAppMailName;
	private String staticContentURL;
	private String passwordRecoveryUrl;
	private String defaultCountry;
	private Integer defaultGeoEntityPresition;
	private String defaultOfferTypeForProposals;
	private String facebookAppId;
	private String facebookAppSecret;
	private long checkinCloseLimitMillis;
	private String defaultLevelOnChallengeBzService;
	private long defaultMessageLockTimeMillis;
	private String beaconProximityUUID;
	private long defaultRejectionLock;
	private long defaultAcceptanceLock;
	private long defaultProximityLock;
	private boolean enqueueHistoryReplicableObjects;
	private String lockZones;
	private boolean trackerEnabled;
	private boolean trackOnlyValidDevices;
	private boolean trackOnlyValidZones;
	private long deviceLocationPullQueueCount;
	private String defaultAppId;
	private String APNAppsCertificatePath;
	private List<String> cinepolisHotspots;
	private List<String> defaultBehavioursApps;
	private Map<String, String> GCMSenders;
	private Map<String, String> APNCertPassword;
	private Map<String, String> APNSandboxCertPassword;
	private String defaultInternalToken;
	private String getinURI;
	private String mailFrom;
	private String mailFromName;
	private String replyTo;
	private String smtpServer;
	private long smtpPort;
	private String smtpUser;
	private String smtpPassword;
	private String smtpEncription;
	private String solrUrl;
	private String getinSqlUrl;
	private List<String> floorMapTracking;
	private String apdeviceJumpHost;
	private String apdeviceJumpHostUser;
	private String apdeviceJumpHostPass;
	private String apdeviceVPNHost;
	private String apdeviceVPNHostUser;
	private String apdeviceVPNHostPass;
	private String apdeviceUser;
	private String apdevicePass;
	private String processHost;
	private String processUser;
	private String processPass;
	private String externalActivityTriggerURL;
	private long processRunTimeLimit;
	private List<String> apdReportMailList;
	
	public String getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(String defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public Boolean getDefaultFavoritesFirst() {
		return defaultFavoritesFirst;
	}

	public void setDefaultFavoritesFirst(Boolean defaultFavoritesFirst) {
		this.defaultFavoritesFirst = defaultFavoritesFirst;
	}

	public int getAuthTokenValidityInDays() {
		return authTokenValidityInDays;
	}

	public void setAuthTokenValidityInDays(int authTokenValidityInDays) {
		this.authTokenValidityInDays = authTokenValidityInDays;
	}

	public String getDefaultLevelOnShoppingBzService() {
		return defaultLevelOnShoppingBzService;
	}

	public void setDefaultLevelOnShoppingBzService(String defaultLevelOnShoppingBzService) {
		this.defaultLevelOnShoppingBzService = defaultLevelOnShoppingBzService;
	}
	
	public String getDefaultLevelOnUserBzService() {
		return defaultLevelOnUserBzService;
	}

	public void setDefaultLevelOnUserBzService(String defaultLevelOnUserBzService) {
		this.defaultLevelOnUserBzService = defaultLevelOnUserBzService;
	}

	public int getDefaultFromRange() {
		return defaultFromRange;
	}

	public void setDefaultFromRange(int defaultFromRange) {
		this.defaultFromRange = defaultFromRange;
	}

	public int getDefaultToRange() {
		return defaultToRange;
	}

	public void setDefaultToRange(int defaultToRange) {
		this.defaultToRange = defaultToRange;
	}

	public String getDefaultLevelOnBrandBzService() {
		return defaultLevelOnBrandBzService;
	}

	public void setDefaultLevelOnBrandBzService(String defaultLevelOnBrandBzService) {
		this.defaultLevelOnBrandBzService = defaultLevelOnBrandBzService;
	}

	public String getOwnLevelOnUserBzService() {
		return ownLevelOnUserBzService;
	}

	public void setOwnLevelOnUserBzService(String ownLevelOnUserBzService) {
		this.ownLevelOnUserBzService = ownLevelOnUserBzService;
	}

	public String getBaseShareURL() {
		return baseShareURL;
	}

	public void setBaseShareURL(String baseShareURL) {
		this.baseShareURL = baseShareURL;
	}

	public String getBaseImageURL() {
		return baseImageURL;
	}

	public void setBaseImageURL(String baseImageURL) {
		this.baseImageURL = baseImageURL;
	}

	public String getFacebookShareCaption() {
		return facebookShareCaption;
	}

	public void setFacebookShareCaption(String facebookShareCaption) {
		this.facebookShareCaption = facebookShareCaption;
	}

	public String getHomeCarouselImages() {
		return homeCarouselImages;
	}

	public void setHomeCarouselImages(String homeCarouselImages) {
		this.homeCarouselImages = homeCarouselImages;
	}

	public int getMaxNearMeLocations() {
		return maxNearMeLocations;
	}

	public void setMaxNearMeLocations(int maxNearMeLocations) {
		this.maxNearMeLocations = maxNearMeLocations;
	}

	public String getDefaultViewLocationCountry() {
		return defaultViewLocationCountry;
	}

	public void setDefaultViewLocationCountry(String defaultViewLocationCountry) {
		this.defaultViewLocationCountry = defaultViewLocationCountry;
	}

	public Boolean getSaveLocationHistory() {
		return saveLocationHistory;
	}

	public void setSaveLocationHistory(Boolean saveLocationHistory) {
		this.saveLocationHistory = saveLocationHistory;
	}

	public String getCollapseKey() {
		return collapseKey;
	}

	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	public int getPushMessageRetries() {
		return pushMessageRetries;
	}

	public void setPushMessageRetries(int pushMessageRetries) {
		this.pushMessageRetries = pushMessageRetries;
	}

	public String getPushMessageResponseUrl() {
		return pushMessageResponseUrl;
	}

	public void setPushMessageResponseUrl(String pushMessageResponseUrl) {
		this.pushMessageResponseUrl = pushMessageResponseUrl;
	}

	public long getRequestLocationLimit() {
		return requestLocationLimit;
	}

	public void setRequestLocationLimit(long requestLocationLimit) {
		this.requestLocationLimit = requestLocationLimit;
	}

	public String getPushMessageOfferTitle() {
		return pushMessageOfferTitle;
	}

	public void setPushMessageOfferTitle(String pushMessageOfferTitle) {
		this.pushMessageOfferTitle = pushMessageOfferTitle;
	}

	public int getDefaultFenceSize() {
		return defaultFenceSize;
	}

	public void setDefaultFenceSize(int defaultFenceSize) {
		this.defaultFenceSize = defaultFenceSize;
	}

	public int getDefaultTaskChunkSize() {
		return defaultTaskChunkSize;
	}

	public void setDefaultTaskChunkSize(int defaultTaskChunkSize) {
		this.defaultTaskChunkSize = defaultTaskChunkSize;
	}

	public boolean getUseFakeMessages() {
		return useFakeMessages;
	}

	public void setUseFakeMessages(boolean useFakeMessages) {
		this.useFakeMessages = useFakeMessages;
	}

	public boolean getUseFakeMails() {
		return useFakeMails;
	}

	public void setUseFakeMails(boolean useFakeMails) {
		this.useFakeMails = useFakeMails;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	public long getDeviceLocationRequestInterval() {
		return deviceLocationRequestInterval;
	}

	public void setDeviceLocationRequestInterval(long deviceLocationRequestInterval) {
		this.deviceLocationRequestInterval = deviceLocationRequestInterval;
	}

	public long getDeviceLocationReportInterval() {
		return deviceLocationReportInterval;
	}

	public void setDeviceLocationReportInterval(long deviceLocationReportInterval) {
		this.deviceLocationReportInterval = deviceLocationReportInterval;
	}

	public String getTrackerUrl() {
		return trackerUrl;
	}

	public void setTrackerUrl(String trackerUrl) {
		this.trackerUrl = trackerUrl;
	}

	public String getTrackerId() {
		return trackerId;
	}

	public void setTrackerId(String trackerId) {
		this.trackerId = trackerId;
	}

	public String getTrackerTokenAuth() {
		return trackerTokenAuth;
	}

	public void setTrackerTokenAuth(String trackerTokenAuth) {
		this.trackerTokenAuth = trackerTokenAuth;
	}

	public String getGeneralAppMail() {
		return generalAppMail;
	}

	public void setGeneralAppMail(String generalAppMail) {
		this.generalAppMail = generalAppMail;
	}

	public String getGeneralAppMailName() {
		return generalAppMailName;
	}

	public void setGeneralAppMailName(String generalAppMailName) {
		this.generalAppMailName = generalAppMailName;
	}

	public String getStaticContentURL() {
		return staticContentURL;
	}

	public void setStaticContentURL(String staticContentURL) {
		this.staticContentURL = staticContentURL;
	}

	public String getPasswordRecoveryUrl() {
		return passwordRecoveryUrl;
	}

	public void setPasswordRecoveryUrl(String passwordRecoveryUrl) {
		this.passwordRecoveryUrl = passwordRecoveryUrl;
	}

	public int getDefaultCheckinAreaSize() {
		return defaultCheckinAreaSize;
	}

	public void setDefaultCheckinAreaSize(int defaultCheckinAreaSize) {
		this.defaultCheckinAreaSize = defaultCheckinAreaSize;
	}

	public String getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(String defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public Integer getDefaultGeoEntityPresition() {
		return defaultGeoEntityPresition;
	}

	public void setDefaultGeoEntityPresition(Integer defaultGeoEntityPresition) {
		this.defaultGeoEntityPresition = defaultGeoEntityPresition;
	}

	public String getDefaultOfferTypeForProposals() {
		return defaultOfferTypeForProposals;
	}

	public void setDefaultOfferTypeForProposals(String defaultOfferTypeForProposals) {
		this.defaultOfferTypeForProposals = defaultOfferTypeForProposals;
	}

	public String getFacebookAppId() {
		return facebookAppId;
	}

	public void setFacebookAppId(String facebookAppId) {
		this.facebookAppId = facebookAppId;
	}

	public String getFacebookAppSecret() {
		return facebookAppSecret;
	}

	public void setFacebookAppSecret(String facebookAppSecret) {
		this.facebookAppSecret = facebookAppSecret;
	}

	public long getCheckinCloseLimitMillis() {
		return checkinCloseLimitMillis;
	}

	public void setCheckinCloseLimitMillis(long checkinCloseLimitMillis) {
		this.checkinCloseLimitMillis = checkinCloseLimitMillis;
	}

	public String getDefaultLevelOnChallengeBzService() {
		return defaultLevelOnChallengeBzService;
	}

	public void setDefaultLevelOnChallengeBzService(
			String defaultLevelOnChallengeBzService) {
		this.defaultLevelOnChallengeBzService = defaultLevelOnChallengeBzService;
	}

	public int getAppTokenValidityInDays() {
		return appTokenValidityInDays;
	}

	public void setAppTokenValidityInDays(int appTokenValidityInDays) {
		this.appTokenValidityInDays = appTokenValidityInDays;
	}

	public String getAPNAppsCertificatePath() {
		return APNAppsCertificatePath;
	}

	public void setAPNAppsCertificatePath(String aPNAppsCertificatePath) {
		APNAppsCertificatePath = aPNAppsCertificatePath;
	}

	public long getDefaultMessageLockTimeMillis() {
		return defaultMessageLockTimeMillis;
	}

	public void setDefaultMessageLockTimeMillis(long defaultMessageLockTimeMillis) {
		this.defaultMessageLockTimeMillis = defaultMessageLockTimeMillis;
	}

	public String getBeaconProximityUUID() {
		return beaconProximityUUID;
	}

	public void setBeaconProximityUUID(String beaconProximityUUID) {
		this.beaconProximityUUID = beaconProximityUUID;
	}

	public long getDefaultRejectionLock() {
		return defaultRejectionLock;
	}

	public void setDefaultRejectionLock(long defaultRejectionLock) {
		this.defaultRejectionLock = defaultRejectionLock;
	}

	public long getDefaultAcceptanceLock() {
		return defaultAcceptanceLock;
	}

	public void setDefaultAcceptanceLock(long defaultAcceptanceLock) {
		this.defaultAcceptanceLock = defaultAcceptanceLock;
	}

	public long getDefaultProximityLock() {
		return defaultProximityLock;
	}

	public void setDefaultProximityLock(long defaultProximityLock) {
		this.defaultProximityLock = defaultProximityLock;
	}
	
	public String getLockZones() {
		return lockZones;
	}
	
	public void setLockZones(String lockZones) {
		this.lockZones = lockZones;
	}
	
	public boolean isEnqueueHistoryReplicableObjects() {
		return enqueueHistoryReplicableObjects;
	}
	
	public void setEnqueueHistoryReplicableObjects(
			boolean enqueueHistoryReplicableObjects) {
		this.enqueueHistoryReplicableObjects = enqueueHistoryReplicableObjects;
	}

	public boolean isTrackerEnabled() {
		return trackerEnabled;
	}

	public void setTrackerEnabled(boolean trackerEnabled) {
		this.trackerEnabled = trackerEnabled;
	}

	public boolean isTrackOnlyValidDevices() {
		return trackOnlyValidDevices;
	}

	public void setTrackOnlyValidDevices(boolean trackOnlyValidDevices) {
		this.trackOnlyValidDevices = trackOnlyValidDevices;
	}

	public boolean isTrackOnlyValidZones() {
		return trackOnlyValidZones;
	}

	public void setTrackOnlyValidZones(boolean trackOnlyValidZones) {
		this.trackOnlyValidZones = trackOnlyValidZones;
	}

	public long getDeviceLocationPullQueueCount() {
		return deviceLocationPullQueueCount;
	}

	public void setDeviceLocationPullQueueCount(long deviceLocationPullQueueCount) {
		this.deviceLocationPullQueueCount = deviceLocationPullQueueCount;
	}

	public List<String> getCinepolisHotspots() {
		return cinepolisHotspots;
	}

	public void setCinepolisHotspots(List<String> cinepolisHotspots) {
		this.cinepolisHotspots = cinepolisHotspots;
	}
	
	public List<String> getDefaultBehavioursApps() {
		return defaultBehavioursApps;
	}
	
	public void setDefaultBehavioursApps(List<String> defaultBehavioursApps) {
		this.defaultBehavioursApps = defaultBehavioursApps;
	}

	public String getDefaultAppId() {
		return defaultAppId;
	}

	public void setDefaultAppId(String defaultAppId) {
		this.defaultAppId = defaultAppId;
	}

	public Map<String, String> getGCMSenders() {
		return GCMSenders;
	}

	public void setGCMSenders(Map<String, String> gCMSenders) {
		GCMSenders = gCMSenders;
	}

	public Map<String, String> getAPNCertPassword() {
		return APNCertPassword;
	}

	public void setAPNCertPassword(Map<String, String> aPNCertPassword) {
		APNCertPassword = aPNCertPassword;
	}

	public Map<String, String> getAPNSandboxCertPassword() {
		return APNSandboxCertPassword;
	}

	public void setAPNSandboxCertPassword(Map<String, String> aPNSandboxCertPassword) {
		APNSandboxCertPassword = aPNSandboxCertPassword;
	}

	public String getDefaultInternalToken() {
		return defaultInternalToken;
	}

	public void setDefaultInternalToken(String defaultInternalToken) {
		this.defaultInternalToken = defaultInternalToken;
	}

	public long getMaxUploadSize() {
		return maxUploadSize;
	}

	public void setMaxUploadSize(long maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	public String getGetinURI() {
		return getinURI;
	}

	public void setGetinURI(String getinURI) {
		this.getinURI = getinURI;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailFromName() {
		return mailFromName;
	}

	public void setMailFromName(String mailFromName) {
		this.mailFromName = mailFromName;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public long getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(long smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getSmtpEncription() {
		return smtpEncription;
	}

	public void setSmtpEncription(String smtpEncription) {
		this.smtpEncription = smtpEncription;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

	public String getGetinSqlUrl() {
		return getinSqlUrl;
	}

	public void setGetinSqlUrl(String getinSqlUrl) {
		this.getinSqlUrl = getinSqlUrl;
	}

	public String getApdeviceJumpHost() {
		return apdeviceJumpHost;
	}

	public void setApdeviceJumpHost(String apdeviceJumpHost) {
		this.apdeviceJumpHost = apdeviceJumpHost;
	}

	public String getApdeviceJumpHostUser() {
		return apdeviceJumpHostUser;
	}

	public void setApdeviceJumpHostUser(String apdeviceJumpHostUser) {
		this.apdeviceJumpHostUser = apdeviceJumpHostUser;
	}

	public String getApdeviceJumpHostPass() {
		return apdeviceJumpHostPass;
	}

	public void setApdeviceJumpHostPass(String apdeviceJumpHostPass) {
		this.apdeviceJumpHostPass = apdeviceJumpHostPass;
	}

	public String getApdeviceVPNHost() {
		return apdeviceVPNHost;
	}

	public void setApdeviceVPNHost(String apdeviceVPNHost) {
		this.apdeviceVPNHost = apdeviceVPNHost;
	}

	public String getApdeviceVPNHostUser() {
		return apdeviceVPNHostUser;
	}

	public void setApdeviceVPNHostUser(String apdeviceVPNHostUser) {
		this.apdeviceVPNHostUser = apdeviceVPNHostUser;
	}

	public String getApdeviceVPNHostPass() {
		return apdeviceVPNHostPass;
	}

	public void setApdeviceVPNHostPass(String apdeviceVPNHostPass) {
		this.apdeviceVPNHostPass = apdeviceVPNHostPass;
	}

	public String getApdeviceUser() {
		return apdeviceUser;
	}

	public void setApdeviceUser(String apdeviceUser) {
		this.apdeviceUser = apdeviceUser;
	}

	public String getApdevicePass() {
		return apdevicePass;
	}

	public void setApdevicePass(String apdevicePass) {
		this.apdevicePass = apdevicePass;
	}

	public List<String> getFloorMapTracking() {
		return floorMapTracking;
	}

	public void setFloorMapTracking(List<String> floorMapTracking) {
		this.floorMapTracking = floorMapTracking;
	}

	public String getProcessHost() {
		return processHost;
	}

	public void setProcessHost(String processHost) {
		this.processHost = processHost;
	}

	public String getProcessUser() {
		return processUser;
	}

	public void setProcessUser(String processUser) {
		this.processUser = processUser;
	}

	public String getProcessPass() {
		return processPass;
	}

	public void setProcessPass(String processPass) {
		this.processPass = processPass;
	}

	public long getProcessRunTimeLimit() {
		return processRunTimeLimit;
	}

	public void setProcessRunTimeLimit(long processRunTimeLimit) {
		this.processRunTimeLimit = processRunTimeLimit;
	}

	public List<String> getApdReportMailList() {
		return apdReportMailList;
	}

	public void setApdReportMailList(List<String> apdReportMailList) {
		this.apdReportMailList = apdReportMailList;
	}

	public String getExternalActivityTriggerURL() {
		return externalActivityTriggerURL;
	}

	public void setExternalActivityTriggerURL(String externalActivityTriggerURL) {
		this.externalActivityTriggerURL = externalActivityTriggerURL;
	}

}
