package com.coupon.test.error;

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
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;


@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalServiceRelationNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GSRN1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postGSRNInvalidCheckDigit() {

		GlobalServiceRelationNumberBean gsrnb = new GlobalServiceRelationNumberBean().init(this.gsrnb);
		gsrnb.setCheckDigit((byte)((gsrnb.getCheckDigit() + 1) % 10));
		helper.postRaw(gsrnb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGSRNInvalidCompanyPrefix() {

		GlobalServiceRelationNumberBean gsrnb = new GlobalServiceRelationNumberBean().init(this.gsrnb);
		gsrnb.setCompanyPrefix(Long.MAX_VALUE);
		gsrnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gsrnb.getCompanyPrefix(),
						gsrnb.getServiceReference()));
		helper.postRaw(gsrnb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGSRNInvalidServiceReference() {

		GlobalServiceRelationNumberBean gsrnb = new GlobalServiceRelationNumberBean().init(this.gsrnb);
		gsrnb.setServiceReference(Long.MAX_VALUE);
		gsrnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gsrnb.getCompanyPrefix(),
						gsrnb.getServiceReference()));
		helper.postRaw(gsrnb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getGSRNUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test
	public void getGSRNUnknownNumber() {

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", Long.valueOf(10^12 - 1)));
		paramsMap.put("serviceReference",
				String.format("%d", Long.valueOf(10^11 - 1)));
		assertTrue("lookupBean() did not return null in getGSRNInvalidNumber()",
				(helper.lookupBean(paramsMap) == null));
	}

	@Test(expected=ResourceConflictException.class)
	public void putGSRNInvalidId() {

		GlobalServiceRelationNumberBean gsrnb = new GlobalServiceRelationNumberBean().init(this.gsrnb);
		gsrnb.setId(Long.MIN_VALUE);
		helper.putRaw(gsrnb);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void putGSRNUnknownId() {

		GlobalServiceRelationNumberBean gsrnb = new GlobalServiceRelationNumberBean().init(this.gsrnb);
		gsrnb.setId(Long.MAX_VALUE);
		helper.putBean(gsrnb);
	}
	
	@Test
	public void putGSRNInvalidCheckDigit() {

		long glnId = helper.postBean(this.gsrnb).getId();
		GlobalServiceRelationNumberBean gsrnb = helper.getBean(glnId);

		gsrnb.setCheckDigit((byte)((gsrnb.getCheckDigit() + 1) % 10));
		try {
			helper.putRaw(gsrnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(glnId);
		}

		fail("Expected ResourceConflictException not thrown in putGSRNInvalidCheckDigit()");
	}

	@Test
	public void putGSRNInvalidCompanyPrefix() {

		long glnId = helper.postBean(this.gsrnb).getId();
		GlobalServiceRelationNumberBean gsrnb = helper.getBean(glnId);		

		gsrnb.setCompanyPrefix(Long.MAX_VALUE);
		gsrnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gsrnb.getCompanyPrefix(),
						gsrnb.getServiceReference()));
		try {
			helper.putRaw(gsrnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(glnId);
		}

		fail("Expected ResourceConflictException not thrown in putGSRNInvalidCompanyPrefix()");
	}

	@Test
	public void putGSRNInvalidServiceReference() {

		long glnId = helper.postBean(this.gsrnb).getId();
		GlobalServiceRelationNumberBean gsrnb = helper.getBean(glnId);		

		gsrnb.setServiceReference(Long.MAX_VALUE);
		gsrnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gsrnb.getCompanyPrefix(),
						gsrnb.getServiceReference()));
		try {
			helper.putRaw(gsrnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(glnId);
		}

		fail("Expected ResourceConflictException not thrown in putGSRNInvalidServiceReference()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteGSRNUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}