package mx.getin.xs3.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;

import mx.getin.xs3.client.model.XS3Bucket;
import mx.getin.xs3.client.model.XS3Exception;
import mx.getin.xs3.client.model.XS3Object;

public class XS3Client {

	private static final Logger log = Logger.getLogger(XS3Client.class.getName());
	private static final Gson gson = GsonFactory.getInstance();

	private static final int BUFFER_SIZE = 1024 * 4;

	private String endpoint;
	private String user;
	private String password;
	private String token;
	private Date tokenValidity;

	public XS3Client(String endpoint, String user, String password ) {
		this.endpoint = endpoint;
		this.user = user;
		this.password = password;

		try {
			updateToken();
		} catch( Exception e ) {
			log.log(Level.WARNING, e.getMessage(), e);
		}

	}

	private void checkTockenValidity() throws XS3Exception {
		if( tokenValidity == null || tokenValidity.before(new Date()))
			updateToken();
	}

	private void updateToken() throws XS3Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			final Map<String, Integer> respCode = new HashMap<String, Integer>();

			HttpRequestBase request = null;
			request = new HttpPost(endpoint + "/auth");
			request.addHeader("Content-Type", "application/json");

			JSONObject json = new JSONObject();
			json.put("identifier", user);
			json.put("password", password);
			String body = json.toString();

			HttpEntity reqEntity = new StringEntity(body);
			((HttpPost)request).setEntity(reqEntity);

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					respCode.put("status", status);
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};

			// Execute the service
			String responseBody = httpclient.execute(request, responseHandler);
			int status = respCode.get("status");

			if( status != 200 )
				throw XS3ExceptionHelper.defaultException(null, null);

			JSONObject responseJson = new JSONObject(responseBody);
			if( responseJson.has("error_code") ) {
				int errorCode = responseJson.getInt("error_code");
				if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_FORBIDDEN_CODE)
					throw XS3ExceptionHelper.forbiddenException();
				else
					throw XS3ExceptionHelper.defaultException(null, null);
			}

			if( responseJson.has("token")) {
				token = responseJson.getString("token");
				tokenValidity = obtainDateValue(responseJson.getString("tokenValidity"), null);
			}

		} catch( XS3Exception e ) {
			throw e;
		} catch( Exception e ) {
			throw XS3ExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}

	}

	public XS3Bucket getBucket(String bucketName) throws XS3Exception {

		checkTockenValidity();
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			final Map<String, Integer> respCode = new HashMap<String, Integer>();

			HttpRequestBase request = null;
			request = new HttpGet(new URL(endpoint + "/bucket/" + bucketName + "?authToken=" + token).toURI());
			request.addHeader("Content-Type", "application/json");

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					respCode.put("status", status);
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};

			// Execute the service
			log.log(Level.INFO, "Executing request " + request.getRequestLine());
			String responseBody = httpclient.execute(request, responseHandler);
			int status = respCode.get("status");

			if( status != 200 )
				throw XS3ExceptionHelper.defaultException(null, null);

			JSONObject responseJson = new JSONObject(responseBody);
			if( responseJson.has("error_code") ) {
				int errorCode = responseJson.getInt("error_code");
				if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_FORBIDDEN_CODE) {
					throw XS3ExceptionHelper.forbiddenException();
				} else if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE ) { 
					updateToken();
					return getBucket(bucketName);
				} else if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					throw XS3ExceptionHelper.notFoundException(bucketName);
				} else {
					throw XS3ExceptionHelper.defaultException(null, null);
				}
			}

			XS3Bucket bucket = null;
			if( responseJson.has("identifier")) {
				bucket = gson.fromJson(responseBody, XS3Bucket.class); 
			} else if( responseJson.has("data")) {
				JSONArray arr = responseJson.getJSONArray("data");
				if( arr.length() > 0 ) {
					bucket = gson.fromJson(arr.getJSONObject(0).toString(), XS3Bucket.class);
				}
			}

			return bucket;

		} catch( XS3Exception e ) {
			throw e;
		} catch( Exception e ) {
			throw XS3ExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	public List<XS3Object> getObjectListing(String bucketName, String path) throws XS3Exception {

		checkTockenValidity();
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			final Map<String, Integer> respCode = new HashMap<String, Integer>();

			HttpRequestBase request = null;
			request = new HttpGet(new URL(endpoint + "/object/" + bucketName
					+ "/" + (path == null ? "" : path) + "?authToken=" + token).toURI());
			request.addHeader("Content-Type", "application/json");

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					respCode.put("status", status);
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};

			// Execute the service
			log.log(Level.INFO, "Executing request " + request.getRequestLine());
			String responseBody = httpclient.execute(request, responseHandler);
			int status = respCode.get("status");

			if( status != 200 )
				throw XS3ExceptionHelper.defaultException(null, null);

			JSONObject responseJson = new JSONObject(responseBody);
			if( responseJson.has("error_code") ) {
				int errorCode = responseJson.getInt("error_code");
				if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_FORBIDDEN_CODE) {
					throw XS3ExceptionHelper.forbiddenException();
				} else if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE ) { 
					updateToken();
					return getObjectListing(bucketName, path);
				} else if( errorCode == XS3ExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					throw XS3ExceptionHelper.notFoundException(bucketName + "/" + path);
				} else {
					throw XS3ExceptionHelper.defaultException(null, null);
				}
			}

			List<XS3Object> ret = new ArrayList<XS3Object>();

			if( responseJson.has("identifier")) {
				XS3Object object = gson.fromJson(responseBody, XS3Object.class);
				ret.add(object);
			} else if( responseJson.has("data")) {
				JSONArray arr = responseJson.getJSONArray("data");
				for( int i = 0; i < arr.length(); i++ ) {
					XS3Object object = gson.fromJson(arr.getJSONObject(i).toString(), XS3Object.class);
					ret.add(object);
				}
			}

			return ret;

		} catch( XS3Exception e ) {
			throw e;
		} catch( Exception e ) {
			throw XS3ExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	public void getObject(String bucketName, String path, String toFile) throws XS3Exception {

		checkTockenValidity();
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			final Map<String, Integer> respCode = new HashMap<String, Integer>();

			HttpRequestBase request = null;
			request = new HttpGet(new URL(endpoint + "/files/" + bucketName
					+ "/" + (path == null ? "" : path) + "?authToken=" + token).toURI());
			request.addHeader("Content-Type", "application/json");

			final File file = new File(toFile);
			File parent = file.getParentFile();
			if( !parent.exists() ) {
				boolean res = parent.mkdirs();
				if( !res )
					throw XS3ExceptionHelper.invalidArgumentsException(parent.getAbsolutePath());
			}

			if( !parent.isDirectory() )
				throw XS3ExceptionHelper.invalidArgumentsException(parent.getAbsolutePath());

			if( file.exists() && file.isDirectory())
				throw XS3ExceptionHelper.invalidArgumentsException(file.getAbsolutePath());

			if( !file.exists()) {
				boolean res = file.createNewFile();
				if( !res )
					throw XS3ExceptionHelper.invalidArgumentsException(file.getAbsolutePath());
			}

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					respCode.put("status", status);
					if (status >= 200 && status < 300) {
						InputStream is = response.getEntity().getContent();
						FileOutputStream fos = new FileOutputStream(file);

						copy(is, fos);

						fos.flush();
						fos.close();
						is.close();

						return null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};

			// Execute the service
			log.log(Level.INFO, "Executing request " + request.getRequestLine());
			httpclient.execute(request, responseHandler);
			int status = respCode.get("status");

			if( status != 200 )
				throw XS3ExceptionHelper.defaultException(null, null);

		} catch( XS3Exception e ) {
			throw e;
		} catch( Exception e ) {
			throw XS3ExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	public void putObject(String bucketName, String path, String fromFile) throws XS3Exception {

		checkTockenValidity();
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			String uri = new URL(endpoint + "/files/" + bucketName
					+ "/" + (path == null ? "" : path) + "?authToken=" + token).toURI().toString();

			final File file = new File(fromFile);
			if( !file.exists()) {
				throw XS3ExceptionHelper.invalidArgumentsException(file.getAbsolutePath());
			}

			// Calling-code manages thread-pool
			ExecutorService es = Executors.newCachedThreadPool(
					new ThreadFactoryBuilder()
					.setNameFormat("apache-client-executor-thread-%d")
					.build());


			// Build configuration
			PipedApacheClientOutputStreamConfig config = new PipedApacheClientOutputStreamConfig();
			config.setUrl(uri);
			config.setPipeBufferSizeBytes(1024);
			config.setThreadPool(es);
			config.setHttpClient(HttpClientBuilder.create().build());

			// Instantiate OutputStream
			PipedApacheClientOutputStream os = new PipedApacheClientOutputStream(config);

			// Write to OutputStream
			FileInputStream fis = new FileInputStream(file);
			copy(fis, os);

			try {
				fis.close();
				os.close();
			} catch (IOException e) {
				log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}

			// Close the HTTP response
			os.getResponse().close();

			// Finally, shut down thread pool
			// This must occur after retrieving response (after is) if interested   
			// in POST result
			es.shutdown();

		} catch( XS3Exception e ) {
			throw e;
		} catch( Exception e ) {
			throw XS3ExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue. The date must be in
	 * ISO_DATETIME_TIME_ZONE_FORMAT format.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Date obtainDateValue(String value, Date defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		String pattern = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
		Date date = null;
		try {
			date = DateUtils.parseDate((String)value, new String[] { pattern });
		} catch (ParseException e) {
		}
		if (date == null) {
			return defaultValue;
		}
		return date;
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
