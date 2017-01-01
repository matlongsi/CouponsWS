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

import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalLocationNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GLN1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postGLNInvalidCheckDigit() {

		GlobalLocationNumberBean glnb = new GlobalLocationNumberBean().init(this.glnb);
		glnb.setCheckDigit((byte)((glnb.getCheckDigit() + 1) % 10));
		helper.postRaw(glnb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGLNInvalidCompanyPrefix() {

		GlobalLocationNumberBean glnb = new GlobalLocationNumberBean().init(this.glnb);
		glnb.setCompanyPrefix(Long.MAX_VALUE);
		glnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						glnb.getCompanyPrefix(),
						glnb.getLocationReference().longValue()));
		helper.postRaw(glnb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGLNInvalidLocationReference() {

		GlobalLocationNumberBean glnb = new GlobalLocationNumberBean().init(this.glnb);
		glnb.setLocationReference(Integer.MAX_VALUE);
		glnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						glnb.getCompanyPrefix(),
						glnb.getLocationReference().longValue()));
		helper.postRaw(glnb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getGLNUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test
	public void getGLNUnknownNumber() {

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", Long.valueOf(10^12 - 1)));
		paramsMap.put("locationReference",
				String.format("%d", Integer.valueOf(10^6 - 1)));
		assertTrue("lookupBean() did not return null in getGLNInvalidNumber()",
				(helper.lookupBean(paramsMap) == null));
	}

	@Test(expected=ResourceConflictException.class)
	public void putGLNInvalidId() {

		GlobalLocationNumberBean glnb = new GlobalLocationNumberBean().init(this.glnb);
		glnb.setId(Long.MIN_VALUE);
		helper.putRaw(glnb);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void putGLNUnknownId() {

		GlobalLocationNumberBean glnb = new GlobalLocationNumberBean().init(this.glnb);
		glnb.setId(Long.MAX_VALUE);
		helper.putBean(glnb);
	}
	
	@Test
	public void putGLNInvalidCheckDigit() {

		long glnId = helper.postBean(this.glnb).getId();
		GlobalLocationNumberBean glnb = helper.getBean(glnId);

		glnb.setCheckDigit((byte)((glnb.getCheckDigit() + 1) % 10));
		try {
			helper.putRaw(glnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(glnId);
		}
		
		fail("Expected ResourceConflictException not thrown in putGLNInvalidCheckDigit()");
	}

	@Test
	public void putGLNInvalidCompanyPrefix() {

		long glnId = helper.postBean(this.glnb).getId();
		GlobalLocationNumberBean glnb = helper.getBean(glnId);		

		glnb.setCompanyPrefix(Long.MAX_VALUE);
		glnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						glnb.getCompanyPrefix(),
						glnb.getLocationReference().longValue()));
		try {
			helper.putRaw(glnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(glnId);
		}

		fail("Expected ResourceConflictException not thrown in putGLNInvalidCompanyPrefix()");
	}

	@Test
	public void putGLNInvalidLocationReference() {

		long glnId = helper.postBean(this.glnb).getId();
		GlobalLocationNumberBean glnb = helper.getBean(glnId);		

		glnb.setLocationReference(Integer.MAX_VALUE);
		glnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						glnb.getCompanyPrefix(),
						glnb.getLocationReference().longValue()));
		try {
			helper.putRaw(glnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(glnId);
		}

		fail("Expected ResourceConflictException not thrown in putGLNInvalidLocationReference()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteGLNUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}