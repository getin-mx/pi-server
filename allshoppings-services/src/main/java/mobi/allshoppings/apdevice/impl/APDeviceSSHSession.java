package mobi.allshoppings.apdevice.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.SystemConfiguration;

/**
 * APDevice SSH Session Descriptor
 * @author mhapanowicz
 *
 */
public class APDeviceSSHSession {

	private static final Logger log = Logger.getLogger(APDeviceSSHSession.class.getName());
	private static final int PASSTHROUGHPORT = 22;
	private static final int APDEVICEPORT = 22;
	private static final String LOCALHOST = "127.0.0.1";

	private JSch jsch;
	private APDevice apdevice;
	private Session vpnSession;
	private Session jumpSession;
	private Session apdSession;
	private SystemConfiguration systemConfig;

	public APDeviceSSHSession(APDevice apdevice, SystemConfiguration systemConfig) {
		this.jsch = new JSch();
		this.apdevice = apdevice;
		this.systemConfig = systemConfig;
	}

	/**
	 * @return the jsch
	 */
	public JSch getJsch() {
		return jsch;
	}

	/**
	 * @param jsch the jsch to set
	 */
	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	/**
	 * @return the apdevice
	 */
	public APDevice getApdevice() {
		return apdevice;
	}

	/**
	 * @param apdevice the apdevice to set
	 */
	public void setApdevice(APDevice apdevice) {
		this.apdevice = apdevice;
	}

	/**
	 * @return the vpnSession
	 */
	public Session getVpnSession() {
		return vpnSession;
	}

	/**
	 * @param vpnSession the vpnSession to set
	 */
	public void setVpnSession(Session vpnSession) {
		this.vpnSession = vpnSession;
	}

	/**
	 * @return the jumpSession
	 */
	public Session getJumpSession() {
		return jumpSession;
	}

	/**
	 * @param jumpSession the jumpSession to set
	 */
	public void setJumpSession(Session jumpSession) {
		this.jumpSession = jumpSession;
	}

	/**
	 * @return the apdSession
	 */
	public Session getApdSession() {
		return apdSession;
	}

	/**
	 * @param apdSession the apdSession to set
	 */
	public void setApdSession(Session apdSession) {
		this.apdSession = apdSession;
	}

	/**
	 * @return the systemConfig
	 */
	public SystemConfiguration getSystemConfig() {
		return systemConfig;
	}

	/**
	 * @param systemConfig the systemConfig to set
	 */
	public void setSystemConfig(SystemConfiguration systemConfig) {
		this.systemConfig = systemConfig;
	}

	/**
	 * Creates a session to the APDevice VPN Server
	 * @throws JSchException
	 */
	public void createAPDeviceVPNHostSession() throws JSchException {

		vpnSession = jsch.getSession(systemConfig.getApdeviceVPNHostUser(), 
				systemConfig.getApdeviceVPNHost(), PASSTHROUGHPORT);
		vpnSession.setPassword(systemConfig.getApdeviceVPNHostPass());
		vpnSession.setConfig("StrictHostKeyChecking", "no");

		log.log(Level.INFO, "Establishing Connection to APDevice VPN host " + systemConfig.getApdeviceVPNHost() + "...");
		vpnSession.connect(40000);
		log.log(Level.INFO, "Connection established to APDevice VPN host " + systemConfig.getApdeviceVPNHost());

	}

	/**
	 * Creates a Session to the APDevice Jump Host
	 * @throws JSchException
	 */
	public void createAPDeviceJumpHostSession() throws JSchException {

		jumpSession = jsch.getSession(systemConfig.getApdeviceJumpHostUser(), 
				systemConfig.getApdeviceJumpHost(), PASSTHROUGHPORT);
		jumpSession.setPassword(systemConfig.getApdeviceJumpHostPass());
		jumpSession.setConfig("StrictHostKeyChecking", "no");

		log.log(Level.INFO, "Establishing Connection to APDevice Jump host " + systemConfig.getApdeviceJumpHost() + "...");
		jumpSession.connect(40000);
		log.log(Level.INFO, "Connection established to APDevice Jump host " + systemConfig.getApdeviceJumpHost() + "...");

	}

	/**
	 * Executes a command on an open SSH session
	 * 
	 * @param session
	 *            The session to work with
	 * @param command
	 *            The command to execute
	 * @param stdout
	 *            The STD Output StringBuffer
	 * @param stderr
	 *            The STD Error StringBuffer
	 * @return the exit status
	 * @throws IOException
	 * @throws JSchException
	 */
	public int executeCommandOnSSHSession(Session session, String command, StringBuffer stdout, StringBuffer stderr) throws IOException, JSchException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");

		if( stdout == null ) stdout = new StringBuffer();
		if( stderr == null ) stderr = new StringBuffer();
		
		try {
			channel.setCommand(command);

			InputStream in=channel.getInputStream();
			OutputStream out=channel.getOutputStream();
			InputStream err=((ChannelExec)channel).getErrStream();
			int exitStatus = 0;

			channel.connect(40000);
			log.log(Level.INFO, "SSH Channel created.");

			out.write(("\n").getBytes());
			out.flush();

			byte[] tmp=new byte[1024];

			// Writes the stdout channel
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					stdout.append(new String(tmp, 0, i));
				}
				while(err.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					stderr.append(new String(tmp, 0, i));
				}
				if(channel.isClosed()){
					exitStatus = channel.getExitStatus();
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}

			return exitStatus;
		} finally {
			if(channel != null && channel.isConnected()) {
				channel.disconnect();
				log.log(Level.INFO, "SSH Channel disconnected.");
			}
		}
	}	

	/**
	 * Disconnects the currently open sessions
	 */
	public void disconnect() {
		if( apdSession != null && apdSession.isConnected()) {
			apdSession.disconnect();
			log.log(Level.INFO, "SSH Session to " + apdevice.getHostname() + " disconnected.");
		}

		if( jumpSession != null && jumpSession.isConnected()) {
			jumpSession.disconnect();
			log.log(Level.INFO, "SSH Session to Jump Server " + systemConfig.getApdeviceJumpHost() + " disconnected.");
		}

		if( vpnSession != null && vpnSession.isConnected()) {
			vpnSession.disconnect();
			log.log(Level.INFO, "SSH Session to VPN Server " + systemConfig.getApdeviceVPNHost() + " disconnected.");
		}
	}

	/**
	 * Connects a session to the APDevice
	 */
	public void connect() throws ASException {
		try {
			try {
				// Creates a connection to the VPN Server
				createAPDeviceVPNHostSession();

				// First try to connect through the VPN Server
				int assinged_port = vpnSession.setPortForwardingL(0, apdevice.getHostname(), APDEVICEPORT);
				apdSession = jsch.getSession(systemConfig.getApdeviceUser(), LOCALHOST, assinged_port);

				apdSession.setPassword(systemConfig.getApdevicePass());
				apdSession.setConfig("StrictHostKeyChecking", "no");
				log.log(Level.INFO, "Establishing Connection to APDevice " + apdevice.getHostname() + " through VPN ...");
				apdSession.connect(40000);
				log.log(Level.INFO, "Connected to APDevice " + apdevice.getHostname() + "...");

			} catch( Exception e ) {

				// If the VPN Server did not make it, creates a connection through the Jump Server
				createAPDeviceJumpHostSession();

				// For bluemarks (bm00xxxx) use the last 4 positions as port
				// For other hosts (ashs-xxxx) use 5 + the last 4 positions as port
				int apdPort = apdevice.getHostname().startsWith("bm00")
						? Integer.parseInt(apdevice.getHostname().substring(4))
								: Integer.parseInt("5" + apdevice.getHostname().substring(5));	        	

						int assinged_port = jumpSession.setPortForwardingL(0, LOCALHOST, apdPort);
						apdSession = jsch.getSession(systemConfig.getApdeviceUser(), LOCALHOST, assinged_port);
						apdSession.setPassword(systemConfig.getApdevicePass());
						apdSession.setConfig("StrictHostKeyChecking", "no");

						try {

							log.log(Level.INFO, "Establishing Connection to APDevice " + apdevice.getHostname() + " through Jump Host...");
							apdSession.connect(40000);
							log.log(Level.INFO, "Connected to APDevice " + apdevice.getHostname() + "...");

						} catch( Exception e1 ) {

							// Try to execute a killbm to restart the connection
							log.log(Level.INFO, "Restarting socket " + apdPort + "...");
							executeCommandOnSSHSession(jumpSession, "/root/killbm " + apdPort, null, null);

							// Sleeps while the channel is recreated from the APDevice to the jump host
							try { Thread.sleep(30000); } catch( Throwable t ) {};

							// Try to connect again
							log.log(Level.INFO, "Establishing Connection to APDevice " + apdevice.getHostname() + " through Jump Host...");
							apdSession.connect(40000);
							log.log(Level.INFO, "Connected to APDevice " + apdevice.getHostname() + "...");

						}
			}

		} catch( Exception e ) {
			disconnect();
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		} 
	}


	/**
	 * Gets a remote file contents from an ssh session 
	 * @param session The SSH Session to use
	 * @param fileName The file name to get the contents from
	 * @return The file contents
	 * @throws JSchException 
	 * @throws IOException 
	 * @throws SftpException 
	 * @throws ASException
	 */
	public byte[] getFileFromSession(Session session, String fileName ) throws JSchException, IOException, SftpException {

		StringBuffer results = new StringBuffer();
		ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect(40000);
		System.out.println("SFTP Channel created.");

		try {
			InputStream out= null;
			out= sftpChannel.get(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(out));
			String line;
			while ((line = br.readLine()) != null)
				results.append(line);
			br.close();

			return results.toString().getBytes();

		} finally {
			sftpChannel.disconnect();
		}
	}
}
