package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

import com.coupon.common.bean.GlobalCouponNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalCouponNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GCN1A.json";

	private static WebServiceHelper<GlobalCouponNumberBean> helper =
				new WebServiceHelper<GlobalCouponNumberBean>(
						GlobalCouponNumberBean.class,
						Resource.PATH_GLOBAL_COUPON_NUMBERS);

	private GlobalCouponNumberBean gcnb;

	@BeforeClass
	public static void setup() { }

	@AfterClass
	public static void cleanup() { }

	@Parameters
	public static Collection<? extends Object> load() throws JAXBException {
		
		return new JSONHelper<GlobalCouponNumberBean>(GlobalCouponNumberBean.class)
				.arrayFromJson(TEST_FILE_PATH);
	}

	public GlobalCouponNumberTest(GlobalCouponNumberBean gcnb) {

		this.gcnb = gcnb;
	}

	@Before
	public void beforeTest() {}

	@After
	public void afterTest() {}

	@Test
	public void test1_postGlobalCouponNumber() {

		GlobalCouponNumberBean gcnb = helper.postBean(this.gcnb);
		assertTrue("New GlobalCouponNumber does not match import file in test1_postGlobalCouponNumber()",
				this.gcnb.equals(gcnb));

		helper.deleteBean(gcnb.getId());
	}
	
	@Test
	public void test2_getGlobalCouponNumberById() {

		long gcnId = helper.postBean(this.gcnb).getId();

		GlobalCouponNumberBean gcnb = helper.getBean(gcnId);
		assertTrue("Retrieved GlobalCouponNumber does not match import file test2_getGlobalCouponNumberById()",
				this.gcnb.equals(gcnb));

		helper.deleteBean(gcnId);
	}

	@Test
	public void test3_getGlobalCouponNumberByNumber() {

		long gcnId = helper.postBean(this.gcnb).getId();

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", this.gcnb.getCompanyPrefix()));
		paramsMap.put("couponReference",
				String.format("%d", this.gcnb.getCouponReference()));
		paramsMap.put("serialComponent",
				String.format("%d", this.gcnb.getSerialComponent()));
		GlobalCouponNumberBean gcnb = helper.lookupBean(paramsMap);
		assertTrue("Retrieved GlobalCouponNumber does not match import file in test3_getGlobalCouponNumberByNumber()",
				this.gcnb.equals(gcnb));

		helper.deleteBean(gcnId);
	}

	@Test
	public void test4_putGlobalCouponNumber() {

		long gcnId = helper.postBean(this.gcnb).getId();

		GlobalCouponNumberBean gcnb = helper.getBean(gcnId);
		gcnb.setCompanyPrefix((gcnb.getCompanyPrefix() + 1) % 100000000000L);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		GlobalCouponNumberBean gcnbUpdt = helper.putBean(gcnb);
		assertTrue("companyPrefix update failed in test4_putGlobalCouponNumber()",
				gcnb.equals(gcnbUpdt));

		gcnb = helper.getBean(gcnId);
		gcnb.setCouponReference((gcnb.getCouponReference() + 1) % 100000);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		gcnbUpdt = helper.putBean(gcnb);
		assertTrue("couponReference update failed in test4_putGlobalCouponNumber()",
				gcnb.equals(gcnbUpdt));

		gcnb = helper.getBean(gcnId);
		gcnb.setSerialComponent((gcnb.getSerialComponent() + 1) % 1000000000000L);
		gcnbUpdt = helper.putBean(gcnb);
		assertTrue("serialComponent update failed in test4_putGlobalCouponNumber()",
				gcnb.equals(gcnbUpdt));

		helper.deleteBean(gcnId);
	}
	
	@Test
	public void test5_deleteGlobalCouponNumber() {

		long gcnId = helper.postBean(this.gcnb).getId();
		helper.getBean(gcnId);
		helper.deleteBean(gcnId);

		try {
			helper.getBean(gcnId);
			fail("delete failed in test5_deleteGlobalLocationNumber()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}