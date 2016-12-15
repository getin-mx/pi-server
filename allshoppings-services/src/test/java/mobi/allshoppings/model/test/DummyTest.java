package mobi.allshoppings.model.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Text;

import junit.framework.TestCase;
import mobi.allshoppings.dao.DummyDAO;
import mobi.allshoppings.dao.spi.DummyDAOJDOImpl;
import mobi.allshoppings.model.Dummy;

public class DummyTest extends TestCase {

	DummyDAO dummyDao = new DummyDAOJDOImpl();

	@Test
	public void test0001() {

		try {
			for( int i = 0; i < 10; i++ ) {
				Dummy obj = new Dummy();
				obj.setBlob(new Blob("Hola Mundo".getBytes()));
				obj.setEmail(new Email("mhapanow@hotmail.com"));
				obj.setName("my name");
				obj.setText(new Text("This is a large text"));
				List<String> arr = new ArrayList<String>();
				for(int x = 0; x < 15; x++) {
					arr.add("data"+x);
				}
				obj.setSomeArray(arr);
				obj.setKey(dummyDao.createKey());
				dummyDao.create(obj);
			}

			List<Dummy> objs = dummyDao.getAll(true);
			for(Dummy obj : objs) {
				System.out.println(obj);
			}

			for(Dummy obj : objs) {
				dummyDao.delete(obj);
			}

			for( int i = 0; i < 10; i++ ) {
				Dummy obj = new Dummy();
				obj.setBlob(new Blob("Hola Mundo".getBytes()));
				obj.setEmail(new Email("mhapanow@hotmail.com"));
				obj.setName("my name");
				obj.setText(new Text("This is a large text"));
				List<String> arr = new ArrayList<String>();
				for(int x = 0; x < 15; x++) {
					arr.add("data"+x);
				}
				obj.setSomeArray(arr);
				obj.setKey(dummyDao.createKey());
				dummyDao.create(obj);
			}

		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
