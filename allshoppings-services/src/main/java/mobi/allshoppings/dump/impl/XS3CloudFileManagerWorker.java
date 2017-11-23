package mobi.allshoppings.dump.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import mx.getin.xs3.client.XS3Client;
import mx.getin.xs3.client.XS3ExceptionHelper;
import mx.getin.xs3.client.model.XS3Exception;
import mx.getin.xs3.client.model.XS3Object;

public class XS3CloudFileManagerWorker extends Thread implements Runnable {

	private static final Logger log = Logger.getLogger(XS3CloudFileManagerWorker.class.getName());
	
	private Map<String, Date> forPrefecth;
	private Map<String, Date> forUpload;
	private Map<String, XS3Object> downloaded;
	private List<String> notFound;
	private String tmpPath;
	private ConcurrentLinkedQueue<String> controlQueue;
	private Semaphore sem;
	private boolean doneSignal;
	private XS3CloudFileManager manager;
	
	public XS3CloudFileManagerWorker(Map<String, Date> forPrefecth, Map<String, Date> forUpload, Map<String, XS3Object> downloaded, List<String> notFound,
			String tmpPath, String bucket, XS3Client client, ConcurrentLinkedQueue<String> controlQueue, Semaphore sem,
			XS3CloudFileManager manager) {
		super();
		this.forPrefecth = forPrefecth;
		this.forUpload = forUpload;
		this.downloaded = downloaded;
		this.notFound = notFound;
		this.tmpPath = tmpPath;
		this.controlQueue = controlQueue;
		this.sem = sem;
		this.manager = manager;
	}

	/**
	 * @return the forPrefecth
	 */
	public Map<String, Date> getForPrefecth() {
		return forPrefecth;
	}

	/**
	 * @param forPrefecth the forPrefecth to set
	 */
	public void setForPrefecth(Map<String, Date> forPrefecth) {
		this.forPrefecth = forPrefecth;
	}

	/**
	 * @return the downloaded
	 */
	public Map<String, XS3Object> getDownloaded() {
		return downloaded;
	}

	/**
	 * @param downloaded the downloaded to set
	 */
	public void setDownloaded(Map<String, XS3Object> downloaded) {
		this.downloaded = downloaded;
	}

	/**
	 * @return the tmpPath
	 */
	public String getTmpPath() {
		return tmpPath;
	}

	/**
	 * @param tmpPath the tmpPath to set
	 */
	public void setTmpPath(String tmpPath) {
		this.tmpPath = tmpPath;
	}

	/**
	 * @return the controlQueue
	 */
	public ConcurrentLinkedQueue<String> getControlQueue() {
		return controlQueue;
	}

	/**
	 * @param controlQueue the controlQueue to set
	 */
	public void setControlQueue(ConcurrentLinkedQueue<String> controlQueue) {
		this.controlQueue = controlQueue;
	}

	/**
	 * @return the sem
	 */
	public Semaphore getSem() {
		return sem;
	}

	/**
	 * @param sem the sem to set
	 */
	public void setSem(Semaphore sem) {
		this.sem = sem;
	}

	/**
	 * Sets this worker as done
	 */
	public void dispose() {
		doneSignal = true;
	}

	public void run() {

		// Initializes the done signal in false
		doneSignal = false;

		String file = null;
		int action = XS3CloudFileManager.ACTION_NOP;
		
		// Main loop
		while(!doneSignal) {

			String fileConstruct = controlQueue.poll();
			if( StringUtils.hasText(fileConstruct)) {

				boolean go = true;
				try {

					String[] parts = fileConstruct.split("::");
					action = Integer.parseInt(parts[0]);
					file = parts[1];

					sem.acquire();
					if( action == XS3CloudFileManager.ACTION_DOWNLOAD) {
						if(!forPrefecth.containsKey(file) ||
								downloaded.containsKey(file)) { 
							go = false;
						}
					} else if( action == XS3CloudFileManager.ACTION_UPLOAD) {
						if(!forUpload.containsKey(file)) { 
							go = false;
						}
					}
					sem.release();
				} catch (InterruptedException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				} catch ( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
					go = false;
				}
				if( go ) {
					if( action == XS3CloudFileManager.ACTION_DOWNLOAD) {
						log.log(Level.INFO, "Getting file " + file);
						XS3Object sum = null;
						try {
							if(StringUtils.hasText(file)) {
								try {
									manager.download(file, file);
								} catch( Exception e2 ) {
									manager.registerFileForPrefetch(file);
								}
								
								sem.acquire();
								downloaded.put(file, sum);//TODO sum is NULL always
								forPrefecth.remove(file);
								sem.release();

							} else {
								sem.acquire();
								if(!notFound.contains(file))
									notFound.add(file);
								sem.release();
							}
						} catch( Exception e ) {
							e.printStackTrace();
							if( e instanceof XS3Exception &&
									((XS3Exception)e).getErrorCode() !=
									XS3ExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE )
								log.log(Level.SEVERE, e.getMessage(), e);
							else {
								try {
									sem.acquire();
									if(!notFound.contains(file))
										notFound.add(file);
									sem.release();
								} catch( Exception e1) {}
							}
						}
					} else if( action == XS3CloudFileManager.ACTION_UPLOAD) {
						
						try {

							manager.upload(file, file);
							manager.deleteLocal(file);

						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}
				
			} else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

	}

}
