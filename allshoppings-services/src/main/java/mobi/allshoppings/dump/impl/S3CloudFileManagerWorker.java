package mobi.allshoppings.dump.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3CloudFileManagerWorker extends Thread implements Runnable {

	private static final Logger log = Logger.getLogger(S3CloudFileManagerWorker.class.getName());
	
	private Map<String, Date> forPrefecth;
	private Map<String, S3ObjectSummary> downloaded;
	private List<String> notFound;
	private String tmpPath;
	private String bucket;
	private AmazonS3 s3;
	private ConcurrentLinkedQueue<String> controlQueue;
	private Semaphore sem;
	private boolean doneSignal;
	private S3CloudFileManager manager;

	public S3CloudFileManagerWorker(Map<String, Date> forPrefecth, Map<String, S3ObjectSummary> downloaded, List<String> notFound,
			String tmpPath, String bucket, AmazonS3 s3, ConcurrentLinkedQueue<String> controlQueue, Semaphore sem,
			S3CloudFileManager manager) {
		super();
		this.forPrefecth = forPrefecth;
		this.downloaded = downloaded;
		this.notFound = notFound;
		this.tmpPath = tmpPath;
		this.bucket = bucket;
		this.s3 = s3;
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
	public Map<String, S3ObjectSummary> getDownloaded() {
		return downloaded;
	}

	/**
	 * @param downloaded the downloaded to set
	 */
	public void setDownloaded(Map<String, S3ObjectSummary> downloaded) {
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

		// Main loop
		while(!doneSignal) {

			String file = controlQueue.poll();
			if( StringUtils.hasText(file)) {

				boolean go = true;
				try {
					sem.acquire();
					if(!forPrefecth.containsKey(file)) { 
						go = false;
					}
					if(downloaded.containsKey(file)) {
						go = false;
					}
					sem.release();
				} catch (InterruptedException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
				
				if( go ) {
					log.log(Level.INFO, "Getting file " + file);

					S3ObjectSummary sum = null;
					ObjectListing oListing = s3.listObjects(bucket, file);
					List<S3ObjectSummary> list = oListing.getObjectSummaries();
					Iterator<S3ObjectSummary> i = list.iterator();
					while(i.hasNext()) {
						S3ObjectSummary obj = i.next();
						if(obj.getKey().equals(file))
							sum = obj;
					}

					try {
						if( sum != null ) {
							try {
								manager.download(sum.getKey(), file);
							} catch( Exception e2 ) {
								manager.registerFileForPrefetch(sum.getKey());
							}

							sem.acquire();
							downloaded.put(sum.getKey(), sum);
							forPrefecth.remove(sum.getKey());
							sem.release();

						} else {
							sem.acquire();
							if(!notFound.contains(file))
								notFound.add(file);
							sem.release();
						}
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

	}

}
