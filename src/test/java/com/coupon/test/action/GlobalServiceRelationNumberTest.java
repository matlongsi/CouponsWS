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

import com.coupon.common.bean.GlobalServiceRelationNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;


@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalServiceRelationNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GSRN1A.json";

	private GlobalServiceRelationNumberBean gsrnb;

	private static WebServiceHelper<GlobalServiceRelationNumberBean> helper =
			new WebServiceHelper<GlobalServiceRelationNumberBean>(
					GlobalServiceRelationNumberBean.class,
					Resource.PATH_GLOBAL_SERVICE_RELATION_NUMBERS);
	
	@BeforeClass
	public static void setup() { }
	
	@AfterClass
	public static void cleanup() { }
	
	@Parameters
	public static Collection<? extends Object> load() throws JAXBException {
		
		return new JSONHelper<GlobalServiceRelationNumberBean>(GlobalServiceRelationNumberBean.class)
				.arrayFromJson(TEST_FILE_PATH);
	}

	public GlobalServiceRelationNumberTest(GlobalServiceRelationNumberBean gsrnb) {

		this.gsrnb = gsrnb;
	}

	@Before
	public void beforeTest() {}

	@After
	public void afterTest() {}

	@Test
	public void test1_postGlobalServiceRelationNumber() {

		GlobalServiceRelationNumberBean gsrnb = helper.postBean(this.gsrnb);
		assertTrue("New GlobalServiceRelationNumber does not match import file in test1_postGlobalServiceRelationNumber()",
				this.gsrnb.equals(gsrnb));
		
		helper.deleteBean(gsrnb.getId());
	}
	
	@Test
	public void test2_getGlobalServiceRelationNumberById() {
	
		long gsrnId = helper.postBean(this.gsrnb).getId();

		GlobalServiceRelationNumberBean gsrnb = helper.getBean(gsrnId);
		assertTrue("Retrieved GlobalServiceRelationNumber does not match import file test2_getGlobalServiceRelationNumberById()",
				this.gsrnb.equals(gsrnb));
		
		helper.deleteBean(gsrnId);
	}
	
	@Test
	public void test3_getGlobalServiceRelationNumberByNumber() {

		long gsrnId = helper.postBean(this.gsrnb).getId();

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", this.gsrnb.getCompanyPrefix()));
		paramsMap.put("serviceReference",
				String.format("%d", this.gsrnb.getServiceReference()));
		GlobalServiceRelationNumberBean gsrnb = helper.lookupBean(paramsMap);
		assertTrue("Retrieved GlobalServiceRelationNumber does not match import file in test3_getGlobalServiceRelationNumberByNumber()",
				this.gsrnb.equals(gsrnb));

		helper.deleteBean(gsrnId);
	}
	
	@Test
	public void test4_putGlobalServiceRelationNumber() {
	
		long gsrnId = helper.postBean(this.gsrnb).getId();

		GlobalServiceRelationNumberBean gsrnb = helper.getBean(gsrnId);
		gsrnb.setCompanyPrefix((gsrnb.getCompanyPrefix() + 1) % 100000000000L);
		gsrnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gsrnb.getCompanyPrefix(),
						gsrnb.getServiceReference()));
		GlobalServiceRelationNumberBean gsrnbUpdt = helper.putBean(gsrnb);
		assertTrue("companyPrefix update failed in test4_putGlobalServiceRelationNumber()",
				gsrnb.equals(gsrnbUpdt));
	
		gsrnb = helper.getBean(gsrnId);
		gsrnb.setServiceReference((gsrnb.getServiceReference() + 1) % 100000);
		gsrnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gsrnb.getCompanyPrefix(),
						gsrnb.getServiceReference()));
		gsrnbUpdt = helper.putBean(gsrnb);
		assertTrue("serviceReference update failed in test4_putGlobalServiceRelationNumber()",
				gsrnb.equals(gsrnbUpdt));

		helper.deleteBean(gsrnId);
	}
	
	@Test
	public void test5_deleteGlobalServiceRelationNumber() {

		long gsrnId = helper.postBean(this.gsrnb).getId();
		helper.getBean(gsrnId);
		helper.deleteBean(gsrnId);

		try {
			helper.getBean(gsrnId);
			fail("delete failed in test5_deleteGlobalServiceRelationNumber()");
		}
		catch (ResourceNotFoundException ex) { }
	}
}