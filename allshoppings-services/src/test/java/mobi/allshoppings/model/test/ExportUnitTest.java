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
			if(!list.isEmpty()) {
				
				for( ExportUnit unit : list ) {
					System.out.println(unit);
				}
				
			} else {

				ExportUnit unit = new ExportUnit();
				unit.setDescription("Exportacion de Prada a MySQL");
				unit.setEntityIds(Arrays.asList("prada_mx"));
				unit.setEntityKind(EntityKind.KIND_BRAND);
				unit.setHideMac(true);
				unit.setName("prada_mx");
				unit.setSourceType(ExportUnit.SOURCE_VISITS);
				unit.setStatus(StatusAware.STATUS_ENABLED);
				unit.setTargetType(ExportUnit.TARGET_MYSQL);
				unit.setTargetDBName("prada_mx");
				unit.setTargetURL("jdbc:mysql://heimdall.getin.mx:3306/prada_mx");
				unit.setTargetUser("prada_mx");
				unit.setTargetPassword("pr4d401");
				unit.setKey(dao.createKey());
				dao.create(unit);
			}
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
