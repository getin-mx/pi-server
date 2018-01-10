package mobi.allshoppings.dump.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.State;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import mobi.allshoppings.dump.CloudFileManager;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.tools.CollectionFactory;

public class S3CloudFileManager implements CloudFileManager {

    private static final int BUFFER_SIZE = 1024 * 4;
	private static final Logger log = Logger.getLogger(S3CloudFileManager.class.getName());

	private Map<String, Date> forUpdate;
	private Map<String, Date> forUpload;
	private Map<String, Date> forPrefecth;
	private Map<String, S3ObjectSummary> downloaded;
	private Map<String, Date> forDisposal;
	private List<String> notFound;
	private String bucket;
	private String tmpPath;
	private int instances;
	private ConcurrentLinkedQueue<String> controlQueue;
	private List<S3CloudFileManagerWorker> workers;
	private Semaphore sem;
	private SystemConfiguration systemConfiguration;
	
	private AmazonS3 s3;
	
	public S3CloudFileManager(String localPath, SystemConfiguration systemConfiguration) {

		this.systemConfiguration = systemConfiguration;
		instances = systemConfiguration.getCFMInstances();
		
		forUpdate = CollectionFactory.createMap();
		forUpload = CollectionFactory.createMap();
		forPrefecth = CollectionFactory.createMap();
		forDisposal = CollectionFactory.createMap();
		downloaded = CollectionFactory.createMap();
		notFound = CollectionFactory.createList();
		sem = new Semaphore(1);
		
		if( !StringUtils.hasText(localPath)) {
			tmpPath = createTempDir("/tmp/s3fcm");
		} else {
			tmpPath = createLocalDir(localPath);
		}
		if( !tmpPath.endsWith(File.separator))
			tmpPath = tmpPath + File.separator;
		
	}

	private String sanitizeFileName(String filename) {
		if(filename == null ) return null;
		String myFile = new String(filename);
		if( myFile.startsWith(tmpPath)) {
			myFile = myFile.substring(tmpPath.length());
		}
		if( myFile.startsWith(File.separator)) {
			myFile = myFile.substring(1);
		}
		return myFile;
	}
	
	@Override
	public void registerFileForUpdate(String filename) throws ASException {
		forUpdate.put(sanitizeFileName(filename), new Date());
	}

	@Override
	public void registerFileForPrefetch(String filename) throws ASException {
		String file = sanitizeFileName(filename);
		forPrefecth.put(file, new Date());
		try {
			if( controlQueue != null ) {
				sem.acquire();
				controlQueue.offer("download::" + file);
				sem.release();
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@Override
	public void registerFileAsDisposable(String filename) {
		String file = sanitizeFileName(filename);
		forDisposal.put(file, new Date());
		try {
			deleteLocal(file);
		} catch( Exception e ) {}
	}

	@Override
	public void startPrefetch(boolean expectNotFound) throws ASException {

		// Check if it was already started
		if( workers != null && workers.size() > 0 ) {
			//for(S3CloudFileManagerWorker w : workers) w.setNotFoundExpected(notFoundExpected);
			return;
		}
		
		if( instances == 0 ) 
			return;
		
		// Creates the needed objects
		workers = CollectionFactory.createList();
		controlQueue = new ConcurrentLinkedQueue<String>();
		
		if(!isConnected())
			connect();
		
		// Loads the current prefetched data into the control queue
		Set<String> set = forPrefecth.keySet();
		List<String> list = CollectionFactory.createList();
		list.addAll(set);
		Collections.sort(list);
		for( String key : list ) {
			controlQueue.offer(key);
		}
		
		// Start the actual workers
		for( int i = 0; i < instances; i++ ) {
			S3CloudFileManagerWorker w = new S3CloudFileManagerWorker(forPrefecth, forUpload, downloaded, notFound,
					tmpPath, bucket, s3, controlQueue, sem, this);
			w.setName("S3 Worker " + i);
			w.start();
			workers.add(w);
		}
		
	}

	@Override
	public void flush() throws ASException {
		List<String> tmpList = CollectionFactory.createList();
		tmpList.addAll(forUpdate.keySet());

		try {
			Iterator<String> i = tmpList.iterator();
			while(i.hasNext()) {
				String key = i.next();
				// Check if it was already started
				if( workers != null && workers.size() > 0 ) {
					sem.acquire();
					forUpload.put(key, new Date());
					controlQueue.offer("upload::" + key);
					forUpdate.remove(key);
					sem.release();
				} else {
					upload(key, key);
					sem.acquire();
					forDisposal.put(key, new Date());
					forUpdate.remove(key);
					downloaded.remove(key);
					sem.release();
				}
			}

			// Waits for flushing files
			if( workers != null && workers.size() > 0 ) {
				while(controlQueue.size() > systemConfiguration.getMaxUploadQueue()) {
					log.log(Level.INFO, "waiting for files to flush... " + controlQueue.size() + " pending...");
					Thread.sleep(5000);
				}
			}
			
			forceCleanup();
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@Override
	public void forceCleanup() throws ASException {
		List<String> tmpList = CollectionFactory.createList();
		tmpList.addAll(forDisposal.keySet());

		try {
			Iterator<String> i = tmpList.iterator();
			while(i.hasNext()) {
				String key = i.next();
				deleteLocal(key);
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@Override
	public void checkAvailableSpace() throws ASException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean checkLocalCopyIntegrity(String fileName, boolean wait, boolean notFoundExpected) throws ASException {
		try {

			if( workers != null && workers.size() > 0 ) {

				boolean hasFile = false;
				do {
					sem.acquire();
					if( notFound.contains(sanitizeFileName(fileName))) {
						sem.release();
						return false;
					} else {
						if( downloaded.containsKey(sanitizeFileName(fileName))) {
							sem.release();
							return true;
						} else {
							log.log(Level.INFO, "Waiting for file " + fileName);
							sem.release();
							Thread.sleep(1000);
						}
					}
					
				} while( wait );
				
				return hasFile;
				
			} else {

				List<String> refs = getDirectoryListing(fileName);
				for( String key : refs ) {
					File check = new File(tmpPath + key);
					if(!check.exists()) {
						download(key, key);
					}
				}

				return true;
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getDirectoryListing(String directory) throws ASException {
		
		List<String> ret = CollectionFactory.createList();
		
		if(!isConnected())
			connect();
		
		ObjectListing oListing = s3.listObjects(bucket, sanitizeFileName(directory));
		List<S3ObjectSummary> list = oListing.getObjectSummaries();
		Iterator<S3ObjectSummary> i = list.iterator();
		while(i.hasNext()) {
			S3ObjectSummary obj = i.next();
			ret.add(obj.getKey());
		}

		return ret;
	}

	@Override
	public void dispose() {
		
		try {
			flush();
			
			if( workers != null ) {

				while( controlQueue.size() > 0 ) {
					log.log(Level.INFO, "waiting for all files to flush... " + controlQueue.size() + " pending...");
					Thread.sleep(5000);
				}
				
				for( S3CloudFileManagerWorker worker : workers ) {
					worker.dispose();
				}
			}

			if( workers != null ) {
				for( S3CloudFileManagerWorker worker : workers ) {
					while( worker.getState() != State.TERMINATED ) {
						Thread.sleep(100);
					}
				}
			}

			forceCleanup();

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
	}

	@Override
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	@Override
	public String getBucket() {
		return bucket;
	}

	private void connect() {
		if( s3 == null ) {
			
			ClientConfiguration cc = new ClientConfiguration(); 
			cc.setSocketTimeout(600000);
			
			s3 = new AmazonS3Client( new BasicAWSCredentials(
					// provide access key and secret key
					systemConfiguration.getAwsAccessKey(),
					systemConfiguration.getAwsSecretKey()
					), cc);

			// change to the IP and port of your Eucalyptus CLC
			s3.setEndpoint( systemConfiguration.getS3Endpoint() );

			// configure path-style S3 access if desired
			s3.setS3ClientOptions( new S3ClientOptions().withPathStyleAccess( true ));
		}
	}
	
	private boolean isConnected() {
		if( s3 == null )
			return false;
		return true;
	}
	
	public void upload(String objectKey, String fileName) throws Exception {
		if( !isConnected() )
			connect();
		
		boolean done = false;
		int retriesLeft = 4;
		
		while( !done ) {
			try {
				File file = new File(tmpPath + fileName);
				log.log(Level.FINE, "Uploading object " + objectKey + " from " + file.getAbsolutePath());
				long start = new Date().getTime();
				s3.putObject(new PutObjectRequest(bucket, objectKey, file));
				long end = new Date().getTime();
				log.log(Level.FINE, "Object " + objectKey + " uploaded in " + (end - start) + "ms" );
				done = true;
			} catch( Throwable e ) {
				if( retriesLeft <= 0 ) {
					log.log(Level.WARNING, "Error uploading " + fileName + "...");
					throw e;
				}
				retriesLeft--;
				Thread.sleep(3000);
				log.log(Level.WARNING, "Retrying upload for " + fileName + "...");
			}
		}
	}
	
	public void download(String objectKey, String fileName) throws Exception {
		if( !isConnected() )
			connect();

		File file = new File(tmpPath + fileName);
		log.log(Level.FINE, "Downloading object " + objectKey + " into " + file.getAbsolutePath());
		long start = System.currentTimeMillis();

		boolean done = false;
		int retriesLeft = 4;
		
		while(!done) {
			try {
				S3Object object = s3.getObject(new GetObjectRequest(bucket, objectKey));

				try {
					File dir = file.getParentFile();
					if( !dir.exists() ) dir.mkdirs();
					copy(object.getObjectContent(), new FileOutputStream(file));
				} finally {
					object.close();
				}

				done = true;
			} catch( Exception e ) {
				if( retriesLeft <= 0 ) {
					log.log(Level.WARNING, "Failed download for " + fileName + "...");
					throw e;
				}
				retriesLeft--;
				Thread.sleep(3000);
				log.log(Level.WARNING, "Retrying download for " + fileName + "...");
			}
		}
        long end = new Date().getTime();
        log.log(Level.FINE, "Object " + objectKey + " downloaded in " + (end - start) + "ms" );
	}

	public void deleteLocal(String fileName) throws Exception {
		File file = new File(tmpPath + fileName);
		log.log(Level.FINE, "Deleting local copy " + file.getAbsolutePath());
		long start = new Date().getTime();
		if( file.exists())
			file.delete();
		
		sem.acquire();
		forPrefecth.remove(file);
		forDisposal.remove(fileName);
		forUpdate.remove(fileName);
		downloaded.remove(fileName);
		sem.release();
		
        long end = new Date().getTime();
        log.log(Level.FINE, "Local coopy " + file.getAbsolutePath() + " deleted in " + (end - start) + "ms" );
	}
	
	private String createTempDir(String prefix) {
		UUID uuid = UUID.randomUUID();
		File tmpDir = new File(prefix + uuid.toString());
		try {
			tmpDir.mkdirs();
		} catch( Exception e ) {
			tmpDir = new File("/tmp");
		}
		return tmpDir.toString();
	}

	private String createLocalDir(String dir) {
		File tmpDir = new File(dir);
		try {
			tmpDir.mkdirs();
		} catch( Exception e ) {
		}
		return tmpDir.toString();
	}

    /**
     * Copies all bytes from the given input stream to the given output stream.
     * Caller is responsible for closing the streams.
     * 
     * @throws IOException
     *             if there is any IO exception during read or write.
     */
    public static long copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while ((n = in.read(buf)) > -1) {
            out.write(buf, 0, n);
            count += n;
        }
        return count;
    }

}
