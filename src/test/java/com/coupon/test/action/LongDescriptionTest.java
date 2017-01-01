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

import com.coupon.common.bean.LongDescriptionBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LongDescriptionTest {

	public static final String TEST_FILE_PATH = "./TestData/LD1A.json";

	private static WebServiceHelper<LongDescriptionBean> helper =
			new WebServiceHelper<LongDescriptionBean>(
				LongDescriptionBean.class,
				Resource.PATH_LONG_DESCRIPTIONS);
	private static WebServiceHelper<MarketingMaterialBean> mmHelper =
			new WebServiceHelper<MarketingMaterialBean>(
				MarketingMaterialBean.class,
				Resource.PATH_MARKETING_MATERIALS);

	private MarketingMaterialTest mmTest;
	private static List<LongDescriptionBean> ldbs;

	private LongDescriptionBean ldb;
	public LongDescriptionBean getLongDescription() { return ldb; }
	
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

		LongDescriptionTest.ldbs = new JSONHelper<LongDescriptionBean>(LongDescriptionBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> mmbs = MarketingMaterialTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : mmbs) {
			for (Object ldb : LongDescriptionTest.ldbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						MarketingMaterialBean.class.cast(arr[1]),
						ldb});
			}
		}

		return params;
	}

	public LongDescriptionTest(OfferBean ob, MarketingMaterialBean mmb, LongDescriptionBean ldb) {

		this.mmTest = new MarketingMaterialTest(ob, mmb);
		this.ldb = ldb;
	}

	@Before
	public void beforeTest() {

		this.mmTest.beforeTest();

		this.ldb.setParentId(
				mmHelper.postBean(mmTest.getMarketingMaterial()).getId());
	}

	@After
	public void afterTest() {

		mmHelper.deleteBean(this.ldb.getParentId());

		this.mmTest.afterTest();
	}

	@Test
	public void test1_postLongDescription() {

		LongDescriptionBean ldb = helper.postBean(this.ldb);
		assertTrue("New LongDescription does not match import file in test1_postLongDescription()",
				this.ldb.equals(ldb));
		
		helper.deleteBean(ldb.getId());
	}
	
	@Test
	public void test2_getLongDescriptionById() {

		long ldId = helper.postBean(this.ldb).getId();

		LongDescriptionBean ldb = helper.getBean(ldId);
		assertTrue("Retrieved LongDescription does not match import file in test2_getLongDescription()",
				this.ldb.equals(ldb));

		helper.deleteBean(ldId);
	}

	@Test
	public void test3_putLongDescription() {
		
		long ldId = helper.postBean(this.ldb).getId();

		LongDescriptionBean ldb = helper.getBean(ldId);
		ldb.setLongDescription(ldb.getLongDescription() + "UPDTD");
		LongDescriptionBean abUpdt = helper.putBean(ldb);
		assertTrue("LongDescription did not update in test4_putLongDescription",
				ldb.equals(abUpdt));

		helper.deleteBean(ldId);
	}
	
	@Test
	public void test4_deleteLongDescription() {

		long ldId = helper.postBean(this.ldb).getId();
		helper.getBean(ldId);
		helper.deleteBean(ldId);

		try {
			helper.getBean(ldId);
			fail("delete failed in test4_deleteLongDescription()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}