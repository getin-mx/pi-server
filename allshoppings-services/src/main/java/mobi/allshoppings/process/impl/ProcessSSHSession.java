package mobi.allshoppings.process.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.SystemConfiguration;

/**
 * APDevice SSH Session Descriptor
 * @author mhapanowicz
 *
 */
public class ProcessSSHSession {

	private static final Logger log = Logger.getLogger(ProcessSSHSession.class.getName());
	private static final int PASSTHROUGHPORT = 22;

	private JSch jsch;
	private Session session;
	private SystemConfiguration systemConfig;

	public ProcessSSHSession(SystemConfiguration systemConfig) {
		this.jsch = new JSch();
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
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
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
		if( session != null && session.isConnected()) {
			log.log(Level.INFO, "SSH Session to process server disconnected.");
		}
	}

	/**
	 * Connects a session to the APDevice
	 */
	public void connect() throws ASException {
		try {
			boolean passRequired = !StringUtils.hasText(systemConfig.getProcessKey()); 
			if(!passRequired)
				jsch.addIdentity(systemConfig.getProcessKey(),
						systemConfig.getProcessPass());
			session = jsch.getSession(systemConfig.getProcessUser(), 
					systemConfig.getProcessHost(), PASSTHROUGHPORT);
			if(passRequired) session.setPassword(systemConfig.getProcessPass());
			session.setConfig("StrictHostKeyChecking", "no");
			log.log(Level.INFO, "Establishing Connection to APDevice VPN host " + systemConfig.getProcessHost() + "...");
			session.connect(40000);
			log.log(Level.INFO, "Connection established to APDevice VPN host " + systemConfig.getProcessHost());

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
