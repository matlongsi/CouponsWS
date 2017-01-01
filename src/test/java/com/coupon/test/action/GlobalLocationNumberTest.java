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

import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalLocationNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GLN1A.json";

	private static WebServiceHelper<GlobalLocationNumberBean> helper =
				new WebServiceHelper<GlobalLocationNumberBean>(
						GlobalLocationNumberBean.class,
						Resource.PATH_GLOBAL_LOCATION_NUMBERS);

	private GlobalLocationNumberBean glnb;

	@BeforeClass
	public static void setup() { }
	
	@AfterClass
	public static void cleanup() { }

	@Parameters
	public static Collection<? extends Object> load() throws JAXBException {
		
		return new JSONHelper<GlobalLocationNumberBean>(GlobalLocationNumberBean.class)
				.arrayFromJson(TEST_FILE_PATH);
	}

	public GlobalLocationNumberTest(GlobalLocationNumberBean glnb) {

		this.glnb = glnb;
	}

	@Before
	public void beforeTest() {}

	@After
	public void afterTest() {}

	@Test
	public void test1_postGlobalLocationNumber() {

		GlobalLocationNumberBean glnb = helper.postBean(this.glnb);
		assertTrue("New GlobalLocationNumber does not match import file in test1_postGlobalLocationNumber()",
				this.glnb.equals(glnb));

		helper.deleteBean(glnb.getId());
	}

	@Test
	public void test2_getGlobalLocationNumberById() {

		long glnId = helper.postBean(this.glnb).getId();

		GlobalLocationNumberBean glnb = helper.getBean(glnId);
		assertTrue("Retrieved GlobalLocationNumber does not match import file test2_getGlobalLocationNumberById()",
				this.glnb.equals(glnb));

		helper.deleteBean(glnId);
	}

	@Test
	public void test3_getGlobalLocationNumberByNumber() {

		long glnId = helper.postBean(this.glnb).getId();

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", this.glnb.getCompanyPrefix()));
		paramsMap.put("locationReference",
				String.format("%d", this.glnb.getLocationReference()));
		GlobalLocationNumberBean glnb = helper.lookupBean(paramsMap);
		assertTrue("Retrieved GlobalLocationNumber does not match import file in test3_getGlobalLocationNumberByNumber()",
				this.glnb.equals(glnb));

		helper.deleteBean(glnId);
	}

	@Test
	public void test4_putGlobalLocationNumber() {

		long glnId = helper.postBean(this.glnb).getId();

		GlobalLocationNumberBean glnb = helper.getBean(glnId);
		glnb.setCompanyPrefix((glnb.getCompanyPrefix() + 1) % 100000000000L);
		glnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						glnb.getCompanyPrefix(),
						glnb.getLocationReference().longValue()));
		GlobalLocationNumberBean glnbUpdt = helper.putBean(glnb);
		assertTrue("companyPrefix update failed in test4_putGlobalLocationNumber",
				glnb.equals(glnbUpdt));

		glnb = helper.getBean(glnId);
		glnb.setLocationReference((glnb.getLocationReference() + 1) % 100000);
		glnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						glnb.getCompanyPrefix(),
						glnb.getLocationReference().longValue()));
		glnbUpdt = helper.putBean(glnb);
		assertTrue("locationReference update failed in test4_putGlobalLocationNumber",
				glnb.equals(glnbUpdt));

		helper.deleteBean(glnId);
	}
	
	@Test
	public void test5_deleteGlobalLocationNumber() {

		long glnId = helper.postBean(this.glnb).getId();
		helper.getBean(glnId);
		helper.deleteBean(glnId);

		try {
			helper.getBean(glnId);
			fail("delete failed in test5_deleteGlobalLocationNumber()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}