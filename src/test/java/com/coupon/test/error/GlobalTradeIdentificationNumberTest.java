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

import com.coupon.common.bean.GlobalTradeIdentificationNumberBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalTradeIdentificationNumberTest {

	public static final String TEST_FILE_PATH = "./TestData/GTIN1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postGTINInvalidCheckDigit() {

		GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean().init(this.gtinb);
		gtinb.setCheckDigit((byte)((gtinb.getCheckDigit() + 1) % 10));
		helper.postRaw(gtinb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGTINInvalidCompanyPrefix() {

		GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean().init(this.gtinb);
		gtinb.setCompanyPrefix(Long.MAX_VALUE);
		gtinb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gtinb.getCompanyPrefix(),
						gtinb.getItemReference().longValue()));
		helper.postRaw(gtinb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postGTINInvalidItemReference() {

		GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean().init(this.gtinb);
		gtinb.setItemReference(Integer.MAX_VALUE);
		gtinb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gtinb.getCompanyPrefix(),
						gtinb.getItemReference().longValue()));
		helper.postRaw(gtinb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getGTINInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test
	public void getGTINUnknownNumber() {

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", Long.valueOf(10^12 - 1)));
		paramsMap.put("itemReference",
				String.format("%d", Integer.valueOf(10^6 - 1)));
		assertTrue("lookupBean() did not return null in getGTINInvalidNumber()",
				(helper.lookupBean(paramsMap) == null));
	}

	@Test(expected=ResourceConflictException.class)
	public void putGTINInvalidId() {

		GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean().init(this.gtinb);
		gtinb.setId(Long.MIN_VALUE);
		helper.putRaw(gtinb);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void putGTINUnknownId() {

		GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean().init(this.gtinb);
		gtinb.setId(Long.MAX_VALUE);
		helper.putBean(gtinb);
	}
	
	@Test
	public void putGTINInvalidCheckDigit() {

		long gtinId = helper.postBean(this.gtinb).getId();
		GlobalTradeIdentificationNumberBean gtinb = helper.getBean(gtinId);

		gtinb.setCheckDigit((byte)((gtinb.getCheckDigit() + 1) % 10));
		try {
			helper.putRaw(gtinb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gtinId);
		}
		
		fail("Expected ResourceConflictException not thrown in putGTINInvalidCheckDigit()");
	}

	@Test
	public void putGTINInvalidCompanyPrefix() {

		long gtinId = helper.postBean(this.gtinb).getId();
		GlobalTradeIdentificationNumberBean gtinb = helper.getBean(gtinId);		

		gtinb.setCompanyPrefix(Long.MAX_VALUE);
		gtinb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gtinb.getCompanyPrefix(),
						gtinb.getItemReference().longValue()));
		try {
			helper.putRaw(gtinb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gtinId);
		}

		fail("Expected ResourceConflictException not thrown in putGTINInvalidCompanyPrefix()");
	}

	@Test
	public void putGTINInvalidItemReference() {

		long gtinId = helper.postBean(this.gtinb).getId();
		GlobalTradeIdentificationNumberBean gtinb = helper.getBean(gtinId);		

		gtinb.setItemReference(Integer.MAX_VALUE);
		gtinb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						gtinb.getCompanyPrefix(),
						gtinb.getItemReference().longValue()));
		try {
			helper.putRaw(gtinb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(gtinId);
		}

		fail("Expected ResourceConflictException not thrown in putGTINInvalidItemReference()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteGTINUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}