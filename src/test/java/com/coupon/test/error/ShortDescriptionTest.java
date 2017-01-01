package com.coupon.test.error;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.coupon.common.bean.ShortDescriptionBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.ShortDescription;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.resource.Resource;
import com.coupon.test.action.MarketingMaterialTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortDescriptionTest {

	public static final String TEST_FILE_PATH = "./TestData/SD1.json";

	private static WebServiceHelper<ShortDescriptionBean> helper =
			new WebServiceHelper<ShortDescriptionBean>(
				ShortDescriptionBean.class,
				Resource.PATH_SHORT_DESCRIPTIONS);
	private static WebServiceHelper<MarketingMaterialBean> mmHelper =
			new WebServiceHelper<MarketingMaterialBean>(
				MarketingMaterialBean.class,
				Resource.PATH_MARKETING_MATERIALS);

	private MarketingMaterialTest mmTest;
	private static List<ShortDescriptionBean> sdbs;

	private ShortDescriptionBean sdb;
	public ShortDescriptionBean getShortDescription() { return sdb; }
	
	@BeforeClass
	public static void setup() {

		MarketingMaterialTest.setup();
	}

	@AfterClass
	public static void cleanup() {
		
		MarketingMaterialTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		ShortDescriptionTest.sdbs = new JSONHelper<ShortDescriptionBean>(ShortDescriptionBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> mmbs = MarketingMaterialTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : mmbs) {
			for (Object sdb : ShortDescriptionTest.sdbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						MarketingMaterialBean.class.cast(arr[1]),
						sdb});
			}
		}

		return params;
	}

	public ShortDescriptionTest(OfferBean ob, MarketingMaterialBean mmb, ShortDescriptionBean sdb) {

		this.mmTest = new MarketingMaterialTest(ob, mmb);
		this.sdb = sdb;
	}

	@Before
	public void beforeTest() {

		this.mmTest.beforeTest();

		this.sdb.setParentId(
				mmHelper.postBean(mmTest.getMarketingMaterial()).getId());
	}

	@After
	public void afterTest() {

		mmHelper.deleteBean(this.sdb.getParentId());

		this.mmTest.afterTest();
	}

	@Test(expected=ResourceConflictException.class)
	public void postSDNullDescription() {

		ShortDescriptionBean sdb = new ShortDescriptionBean().init(this.sdb);
		sdb.setShortDescription(null);
		helper.postRaw(sdb);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postSDLongDescription() {

		ShortDescriptionBean sdb = new ShortDescriptionBean().init(this.sdb);
		sdb.setShortDescription(
				String.format("%" + (ShortDescription.MAX_DESCRIPTION_LENGTH + 1) + "s", "*"));
		helper.postRaw(sdb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postSDNegativeMarketingMaterialId() {

		ShortDescriptionBean sdb = new ShortDescriptionBean().init(this.sdb);
		sdb.setParentId(Long.MIN_VALUE);
		helper.postRaw(sdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postSDInvalidMarketingMaterialId() {

		ShortDescriptionBean sdb = new ShortDescriptionBean().init(this.sdb);
		sdb.setParentId(Long.MAX_VALUE);
		helper.postBean(sdb);
	}

	@Test
	public void putSDNullDescription() {

		long sdId = helper.postBean(this.sdb).getId();
		ShortDescriptionBean sdb = helper.getBean(sdId);
		sdb.setShortDescription(null);
		try {
			helper.putRaw(sdb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(sdId);
		}
		
		fail("Expected ResourceConflictException not thrown in putSDNullDescription()");
	}
	
	@Test
	public void putSDLongDescription() {

		long sdId = helper.postBean(this.sdb).getId();
		ShortDescriptionBean sdb = helper.getBean(sdId);
		sdb.setShortDescription(
				String.format("%" + (ShortDescription.MAX_DESCRIPTION_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(sdb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(sdId);
		}
		
		fail("Expected ResourceConflictException not thrown in putSDLongDescription()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getSDUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putSDUnknownId() {

		ShortDescriptionBean sdb = new ShortDescriptionBean().init(this.sdb);
		sdb.setId(Long.MAX_VALUE);
		helper.putBean(sdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteSDInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}