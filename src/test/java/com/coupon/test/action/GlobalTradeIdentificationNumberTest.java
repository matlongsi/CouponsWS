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

import com.coupon.common.bean.GlobalTradeIdentificationNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalTradeIdentificationNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GTIN1A.json";

	private WebServiceHelper<GlobalTradeIdentificationNumberBean> helper =
			new WebServiceHelper<GlobalTradeIdentificationNumberBean>(
					GlobalTradeIdentificationNumberBean.class,
					Resource.PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS);

	private GlobalTradeIdentificationNumberBean gtinb;
	
	@BeforeClass
	public static void setup() { }

	@AfterClass
	public static void cleanup() { }

	@Parameters
	public static Collection<? extends Object> load() throws JAXBException {
		
		return new JSONHelper<GlobalTradeIdentificationNumberBean>(GlobalTradeIdentificationNumberBean.class)
				.arrayFromJson(TEST_FILE_PATH);
	}

	public GlobalTradeIdentificationNumberTest(GlobalTradeIdentificationNumberBean gtinb) {

		this.gtinb = gtinb;
	}

	@Before
	public void beforeTest() {}

	@After
	public void afterTest() {}

	@Test
	public void test1_postGlobalTradeIdentificationNumber() {

		GlobalTradeIdentificationNumberBean gtinb = helper.postBean(this.gtinb);
		assertTrue("New GlobalTradeIdentificationNumber does not match import file in test1_postGlobalTradeIdentificationNumber()",
				this.gtinb.equals(gtinb));

		helper.deleteBean(gtinb.getId());
	}
	
	@Test
	public void test2_getGlobalTradeIdentificationNumberById() {

		long gtinId = helper.postBean(this.gtinb).getId();

		GlobalTradeIdentificationNumberBean gtinb = helper.getBean(gtinId);
		assertTrue("Retrieved GlobalTradeIdentificationNumber does not match import file test2_getGlobalTradeIdentificationNumberById()",
				this.gtinb.equals(gtinb));
		
		helper.deleteBean(gtinId);
	}

	@Test
	public void test3_getGlobalTradeIdentificationNumberByNumber() {

		long gtinId = helper.postBean(this.gtinb).getId();

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", this.gtinb.getCompanyPrefix()));
		paramsMap.put("itemReference",
				String.format("%d", this.gtinb.getItemReference()));
		GlobalTradeIdentificationNumberBean gtinb = helper.lookupBean(paramsMap);
		assertTrue("Retrieved GlobalTradeIdentificationNumber does not match import file in test3_getGlobalTradeIdentificationNumberByNumber()",
				this.gtinb.equals(gtinb));

		helper.deleteBean(gtinId);
	}

	@Test
	public void test4_putGlobalTradeIdentificationNumber() {

		long gtinId = helper.postBean(this.gtinb).getId();

		GlobalTradeIdentificationNumberBean gtinb = helper.getBean(gtinId);
		gtinb.setCompanyPrefix((gtinb.getCompanyPrefix() + 1) % 100000000000L);
		gtinb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gtinb.getCompanyPrefix(),
						gtinb.getItemReference().longValue()));
		GlobalTradeIdentificationNumberBean gtinbUpdt = helper.putBean(gtinb);
		assertTrue("companyPrefix update failed in test4_putGlobalTradeIdentificationNumber",
				gtinb.equals(gtinbUpdt));

		gtinb = helper.getBean(gtinb.getId());
		gtinb.setItemReference((gtinb.getItemReference() + 1) % 100000);
		gtinb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gtinb.getCompanyPrefix(),
						gtinb.getItemReference().longValue()));
		gtinbUpdt = helper.putBean(gtinb);
		assertTrue("itemReference update failed in test4_putGlobalTradeIdentificationNumber",
				gtinb.equals(gtinbUpdt));

		helper.deleteBean(gtinId);
	}
	
	@Test
	public void test5_deleteGlobalTradeIdentificationNumber() {

		long gtinId = helper.postBean(this.gtinb).getId();
		helper.getBean(gtinId);
		helper.deleteBean(gtinId);

		try {
			helper.getBean(gtinId);
			fail("delete failed in test5_deleteGlobalTradeIdentificationNumber()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}