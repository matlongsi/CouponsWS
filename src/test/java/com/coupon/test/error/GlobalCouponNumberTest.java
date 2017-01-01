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

import com.coupon.common.bean.GlobalCouponNumberBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalCouponNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GCN1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postGCNInvalidCheckDigit() {

		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean().init(this.gcnb);
		gcnb.setCheckDigit((byte)((gcnb.getCheckDigit() + 1) % 10));
		helper.postRaw(gcnb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGCNInvalidCompanyPrefix() {

		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean().init(this.gcnb);
		gcnb.setCompanyPrefix(Long.MAX_VALUE);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		helper.postRaw(gcnb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGCNInvalidCouponReference() {

		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean().init(this.gcnb);
		gcnb.setCouponReference(Integer.MAX_VALUE);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		helper.postRaw(gcnb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getGCNUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test
	public void getGCNUnknownNumber() {

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", Long.valueOf(10^12 - 1)));
		paramsMap.put("couponReference",
				String.format("%d", Integer.valueOf(10^6 - 1)));
		paramsMap.put("serialComponent",
				String.format("%d", Long.valueOf(10^11 - 1)));
		assertTrue("lookupBean() did not return null in getGCNInvalidNumber()",
				(helper.lookupBean(paramsMap) == null));
	}

	@Test(expected=ResourceConflictException.class)
	public void putGCNInvalidId() {

		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean().init(this.gcnb);
		gcnb.setId(Long.MIN_VALUE);
		helper.putRaw(gcnb);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void putGCNUnknownId() {

		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean().init(this.gcnb);
		gcnb.setId(Long.MAX_VALUE);
		helper.putBean(gcnb);
	}
	
	@Test
	public void putGCNInvalidCheckDigit() {

		long gcnId = helper.postBean(this.gcnb).getId();
		GlobalCouponNumberBean gcnb = helper.getBean(gcnId);

		gcnb.setCheckDigit((byte)((gcnb.getCheckDigit() + 1) % 10));
		try {
			helper.putRaw(gcnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gcnId);
		}
		
		fail("Expected ResourceConflictException not thrown in putGCNInvalidCheckDigit()");
	}

	@Test
	public void putGCNInvalidCompanyPrefix() {

		long gcnId = helper.postBean(this.gcnb).getId();
		GlobalCouponNumberBean gcnb = helper.getBean(gcnId);		

		gcnb.setCompanyPrefix(Long.MAX_VALUE);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		try {
			helper.putRaw(gcnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gcnId);
		}

		fail("Expected ResourceConflictException not thrown in putGCNInvalidCompanyPrefix()");
	}

	@Test
	public void putGCNInvalidCouponReference() {

		long gcnId = helper.postBean(this.gcnb).getId();
		GlobalCouponNumberBean gcnb = helper.getBean(gcnId);		

		gcnb.setCouponReference(Integer.MAX_VALUE);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		try {
			helper.putRaw(gcnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gcnId);
		}

		fail("Expected ResourceConflictException not thrown in putGCNInvalidCouponReference()");
	}

	@Test
	public void putGCNInvalidSerialComponent() {

		long gcnId = helper.postBean(this.gcnb).getId();
		GlobalCouponNumberBean gcnb = helper.getBean(gcnId);		

		gcnb.setSerialComponent(Long.MAX_VALUE);
		gcnb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gcnb.getCompanyPrefix(),
						gcnb.getCouponReference().longValue()));
		try {
			helper.putRaw(gcnb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gcnId);
		}

		fail("Expected ResourceConflictException not thrown in putGCNInvalidSerialComponent()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteGCNUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}