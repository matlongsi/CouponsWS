package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
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
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortDescriptionTest {

	public static final String TEST_FILE_PATH = "./TestData/SD1A.json";

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

	@Test
	public void test1_postShortDescription() {

		ShortDescriptionBean sdb = helper.postBean(this.sdb);
		assertTrue("New ShortDescription does not match import file in test1_postShortDescription()",
				this.sdb.equals(sdb));
		
		helper.deleteBean(sdb.getId());
	}
	
	@Test
	public void test2_getShortDescriptionById() {

		long sdId = helper.postBean(this.sdb).getId();

		ShortDescriptionBean sdb = helper.getBean(sdId);
		assertTrue("Retrieved ShortDescription does not match import file in test2_getShortDescription()",
				this.sdb.equals(sdb));

		helper.deleteBean(sdId);
	}

	@Test
	public void test3_putShortDescription() {
		
		long sdId = helper.postBean(this.sdb).getId();

		ShortDescriptionBean sdb = helper.getBean(sdId);
		sdb.setShortDescription(sdb.getShortDescription() + "UPDTD");
		ShortDescriptionBean abUpdt = helper.putBean(sdb);
		assertTrue("ShortDescription did not update in test4_putShortDescription",
				sdb.equals(abUpdt));

		helper.deleteBean(sdId);
	}
	
	@Test
	public void test4_deleteShortDescription() {

		long sdId = helper.postBean(this.sdb).getId();
		helper.getBean(sdId);
		helper.deleteBean(sdId);

		try {
			helper.getBean(sdId);
			fail("delete failed in test4_deleteShortDescription()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}