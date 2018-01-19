package mobi.allshoppings.model.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.ExportUnitDAO;
import mobi.allshoppings.dao.spi.ExportUnitDAOJDOImpl;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExportUnit;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;

public class ExportUnitTest extends TestCase {

	ExportUnitDAO dao = new ExportUnitDAOJDOImpl();
	KeyHelper keyHelper = new KeyHelperGaeImpl();

	@Test
	public void test0001() {

		try {

			List<ExportUnit> list = dao.getUsingStatusAndRange(StatusHelper.statusActive(), null, null);
			if(list.size() > 1 ) {
				
				for( ExportUnit unit : list ) {
					System.out.println(unit);
				}
				
			} else {

				ExportUnit unit = new ExportUnit();
				unit.setDescription("Exportacion de Test a MySQL");
				unit.setEntityIds(Arrays.asList("agasys_mx"));
				unit.setEntityKind(EntityKind.KIND_BRAND);
				unit.setHideMac(true);
				unit.setName("test_mx");
				unit.setSourceType(ExportUnit.SOURCE_VISITS);
				unit.setStatus(StatusAware.STATUS_ENABLED);
				unit.setTargetType(ExportUnit.TARGET_MYSQL);
				unit.setTargetDBName("test_mx");
				unit.setTargetURL("jdbc:mysql://heimdall.getin.mx:3306/test_mx");
				unit.setTargetUser("test_mx");
				unit.setTargetPassword("test01");
				unit.setKey(dao.createKey());
				dao.create(unit);
			}
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
