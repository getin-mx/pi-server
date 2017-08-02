package mx.getin.xs3.client.test;

import java.util.List;

import mx.getin.xs3.client.XS3Client;
import mx.getin.xs3.client.model.XS3Bucket;
import mx.getin.xs3.client.model.XS3Object;

public class ClientTester {

	public static void main(String args[]) {

		try {
			XS3Client client = new XS3Client("http://localhost:8085/xs3", "admin", "admin01");
			XS3Bucket bucket = client.getBucket("test1");
			System.out.println(bucket);
			
			List<XS3Object> list = client.getObjectListing("test1", null);
			for( XS3Object o : list ) {
				System.out.println(o);
			}
			
			list = client.getObjectListing("test1", "datos/archivos");
			for( XS3Object o : list ) {
				System.out.println(o);
			}
			
			list = client.getObjectListing("test1", "datos/archivos/passwd");
			for( XS3Object o : list ) {
				System.out.println(o);
				System.out.println(o.getSize());
			}
			
			client.putObject("test1", "datos/archivos/passwd", "/etc/passwd");
			client.getObject("test1", "datos/archivos/passwd", "/tmp/passwd");
			
		} catch( Exception e ) {
			e.printStackTrace();
		}

		System.exit(0);

	}

}
