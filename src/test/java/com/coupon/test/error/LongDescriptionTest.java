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

import com.coupon.common.LongDescription;
import com.coupon.common.bean.LongDescriptionBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.MarketingMaterialTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LongDescriptionTest {

	public static final String TEST_FILE_PATH = "./TestData/LD1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postLDNullDescription() {

		LongDescriptionBean ldb = new LongDescriptionBean().init(this.ldb);
		ldb.setLongDescription(null);
		helper.postRaw(ldb);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postLDLongDescription() {

		LongDescriptionBean ldb = new LongDescriptionBean().init(this.ldb);
		ldb.setLongDescription(
				String.format("%" + (LongDescription.MAX_DESCRIPTION_LENGTH + 1) + "s", "*"));
		helper.postRaw(ldb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postLDNegativeMarketingMaterialId() {

		LongDescriptionBean ldb = new LongDescriptionBean().init(this.ldb);
		ldb.setParentId(Long.MIN_VALUE);
		helper.postRaw(ldb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postLDInvalidMarketingMaterialId() {

		LongDescriptionBean ldb = new LongDescriptionBean().init(this.ldb);
		ldb.setParentId(Long.MAX_VALUE);
		helper.postBean(ldb);
	}

	@Test
	public void putLDNullDescription() {

		long ldId = helper.postBean(this.ldb).getId();
		LongDescriptionBean ldb = helper.getBean(ldId);
		ldb.setLongDescription(null);
		try {
			helper.putRaw(ldb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ldId);
		}
		
		fail("Expected ResourceConflictException not thrown in putLDNullDescription()");
	}
	
	@Test
	public void putLDLongDescription() {

		long ldId = helper.postBean(this.ldb).getId();
		LongDescriptionBean ldb = helper.getBean(ldId);
		ldb.setLongDescription(
				String.format("%" + (LongDescription.MAX_DESCRIPTION_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(ldb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ldId);
		}
		
		fail("Expected ResourceConflictException not thrown in putLDLongDescription()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getLDInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putLDInvalidId() {

		LongDescriptionBean ldb = new LongDescriptionBean().init(this.ldb);
		ldb.setId(Long.MAX_VALUE);
		helper.putBean(ldb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteLDInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}