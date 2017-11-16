package mobi.allshoppings.process.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.dao.ProcessDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Process;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.process.ProcessUtils;

public class ProcessUtilsImpl implements ProcessUtils {

	private static final Logger log = Logger.getLogger(ProcessUtilsImpl.class.getName());
	
	@Autowired
	private ProcessDAO dao;
	
	@Autowired
	private SystemConfiguration systemConfiguration;

	
	/**
	 * Generates the required command line for a process
	 * 
	 * @param process
	 *            The process to generate the command line for
	 * @return A fully formed command line
	 * @throws ASException
	 */
	private String getCommandLineForProcess(Process process) throws ASException {
		
		JSONObject json = new JSONObject(process.getData());
		StringBuffer sb = new StringBuffer();
		
		if( process.getProcessType().equals(Process.PROCESS_TYPE_GENERATE_VISITS)) {
			sb.append("/usr/local/allshoppings/bin/aspi2 GenerateAPDVisits "
					+ "--datastore /usr/local/allshoppings/etc/datastore.nocache.properties "
					+ "--updateDashboards true --storeIds ")
			.append(process.getEntityId())
			.append(" --fromDate ")
			.append(json.getString("fromDate"))
			.append(" --toDate ")
			.append(json.getString("toDate"));			
		}
		
		return sb.toString();

	}
	
	/**
	 * Starts a process
	 * 
	 * @param identifier
	 *            The Process identifier to start
	 * @throws ASException
	 */
	@Override
	public void startProcess(String identifier, boolean wait) throws ASException {
		
		Process p = dao.get(identifier, true);
		if(!p.getStatus().equals(StatusAware.STATUS_PREPARED))
			throw ASExceptionHelper.defaultException("Invalid status for process " + p.getIdentifier(), new Exception());
		
		if( !StringUtils.hasText(p.getCmdLine()))
			p.setCmdLine(getCommandLineForProcess(p));
		
		p.setServer(systemConfiguration.getProcessHost());
		p.setStartDateTime(new Date());
		p.setStatus(StatusAware.STATUS_RUNNING);
		
		dao.update(p);
		
		final Process fProcess = p;
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				StringBuffer stdout = new StringBuffer();
				StringBuffer stderr = new StringBuffer();

				try {
					
					int errorCode = executeProcess(new String[] { fProcess.getCmdLine() }, stdout, stderr);
					
					fProcess.setEndDateTime(new Date());
					fProcess.setStatus(errorCode == 0 ? StatusAware.STATUS_SUCCEEDED : StatusAware.STATUS_ERROR);
					
					fProcess.setLog(stdout.toString() + stderr.toString());
					// This is a control to avoid memcached overflow... the
					// contents of this field are just logs, so they are not
					// SOOOO important
					if( fProcess.getLog().length() > 786432 )
						fProcess.setLog(fProcess.getLog().substring(0, 786432));
					
					try {
						dao.update(fProcess);
					} catch( Exception e1 ) {
						log.log(Level.SEVERE, e1.getMessage(), e1);
					}

				} catch( Exception e ) {

					log.log(Level.SEVERE, e.getMessage(), e);
					
					fProcess.setEndDateTime(new Date());
					fProcess.setStatus(StatusAware.STATUS_ERROR);

					fProcess.setLog(stdout.toString() + stderr.toString());

					try {
						dao.update(fProcess);
					} catch( Exception e1 ) {
						log.log(Level.SEVERE, e1.getMessage(), e1);
					}
				}
			}
		});
		
		t.start();
		if (wait) {
			try {
				t.join();
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * Executes a command line on the process server
	 * 
	 * @param cmdLine
	 *            The array of command lines to execute
	 * @param stdout
	 *            A buffer for the standard output on the server
	 * @param stderr
	 *            A buffer for the standard error on the server
	 * @throws ASException
	 */
	private int executeProcess(String[] cmdLine, StringBuffer stdout, StringBuffer stderr) throws ASException {
		ProcessSSHSession s = new ProcessSSHSession(systemConfiguration);
		try {
			int errorCode = 0;
			s.connect();
			for( String cmd : cmdLine ) {
				errorCode = s.executeCommandOnSSHSession(s.getSession(), cmd, stdout, stderr);
				if( errorCode != 0 )
					return errorCode;
			}
			return errorCode;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			s.disconnect(); 
		}
	}
	
	
	@Override
	public void cancelPendingProcesses() throws ASException {
		List<Process> list = dao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_RUNNING }), null, null, null, true);
		
		long now = System.currentTimeMillis();
		
		for( Process p : list ) {
			if( p.getStartDateTime().getTime() + systemConfiguration.getProcessRunTimeLimit() < now ) {
				log.log(Level.INFO, "Cancelling process " + p.getIdentifier());
				p.setStatus(StatusAware.STATUS_CANCELLED);
				p.setEndDateTime(new Date());
				
				dao.update(p);
			}
		}
	}

	@Override
	public void completePendingProcesses() throws ASException {
		List<Process> list = dao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_RUNNING }), null, null, null, true);

		for( Process p : list ) {
			log.log(Level.INFO, "Completing process " + p.getIdentifier());
			p.setStatus(StatusAware.STATUS_SUCCEEDED);
			p.setEndDateTime(new Date());

			dao.update(p);
		}
	}

	/**
	 * @return the dao
	 */
	public ProcessDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(ProcessDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the systemConfiguration
	 */
	public SystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}

	/**
	 * @param systemConfiguration the systemConfiguration to set
	 */
	public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
		this.systemConfiguration = systemConfiguration;
	}

}
