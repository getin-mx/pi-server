package mx.getin.xs3.client.test;

import java.util.List;

import mx.getin.xs3.client.XS3Client;
import mx.getin.xs3.client.model.XS3Bucket;
import mx.getin.xs3.client.model.XS3Object;

public class ClientTester {

	public static void main(String args[]) {

		try {
			XS3Client client = new XS3Client("http://localhost:8085/xs3", "admin", "admin01");
			client.putObject("aphotspot", "2017/11/11/APHotspot/lal.json", "/tmp/lal.json");
			client.getObject("aphotspot", "2017/11/11/APHotspot/lal.json", "/tmp/xs3-test");
			
		} catch( Exception e ) {
			e.printStackTrace();
		}

		System.exit(0);

	}

}
