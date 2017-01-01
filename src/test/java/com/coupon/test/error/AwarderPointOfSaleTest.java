package com.coupon.test.error;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

import com.coupon.common.AwarderPointOfSale;
import com.coupon.common.bean.AwarderDetailBean;
import com.coupon.common.bean.AwarderPointOfSaleBean;
import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.action.AwarderDetailTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AwarderPointOfSaleTest {

	public static final String TEST_FILE_PATH = "./TestData/APOS1.json";

	private static WebServiceHelper<AwarderPointOfSaleBean> helper =
			new WebServiceHelper<AwarderPointOfSaleBean>(
				AwarderPointOfSaleBean.class,
				Resource.PATH_AWARDER_POINT_OF_SALES);
	private static WebServiceHelper<AwarderDetailBean> adHelper =
			new WebServiceHelper<AwarderDetailBean>(
				AwarderDetailBean.class,
				Resource.PATH_AWARDER_DETAILS);
	private static WebServiceHelper<GlobalLocationNumberBean> glnHelper =
			new WebServiceHelper<GlobalLocationNumberBean>(
				GlobalLocationNumberBean.class,
				Resource.PATH_GLOBAL_LOCATION_NUMBERS);

	private AwarderDetailTest adTest;
	private static List<AwarderPointOfSaleBean> aposbs;

	private AwarderPointOfSaleBean aposb;
	public AwarderPointOfSaleBean getAwarderPointOfSale() { return aposb; }
	
	@BeforeClass
	public static void setup() {

		AwarderDetailTest.setup();

		for (AwarderPointOfSaleBean aposb : AwarderPointOfSaleTest.aposbs) {
			
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", aposb.getStoreNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", aposb.getStoreNumber().getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(aposb.getStoreNumber());
			}

			GlobalLocationNumberBean snbUpdt = new GlobalLocationNumberBean()
					.init(aposb.getStoreNumber());
			snbUpdt.setCompanyPrefix((snbUpdt.getCompanyPrefix() + 1) % 100000000000L);
			snbUpdt.setCheckDigit(
					ValidatorHelper.computeCheckDigit(
							snbUpdt.getCompanyPrefix(),
							snbUpdt.getLocationReference().longValue()));
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", snbUpdt.getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", snbUpdt.getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(snbUpdt);
			}
		}
	}

	@AfterClass
	public static void cleanup() {

		for (AwarderPointOfSaleBean aposb : AwarderPointOfSaleTest.aposbs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", aposb.getStoreNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", aposb.getStoreNumber().getLocationReference()));
			GlobalLocationNumberBean glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}

			GlobalLocationNumberBean snbUpdt = new GlobalLocationNumberBean()
					.init(aposb.getStoreNumber());
			snbUpdt.setCompanyPrefix((snbUpdt.getCompanyPrefix() + 1) % 100000000000L);
			snbUpdt.setCheckDigit(
					ValidatorHelper.computeCheckDigit(
							snbUpdt.getCompanyPrefix(),
							snbUpdt.getLocationReference().longValue()));
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", snbUpdt.getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", snbUpdt.getLocationReference()));
			glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}
		}

		AwarderDetailTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		AwarderPointOfSaleTest.aposbs = new JSONHelper<AwarderPointOfSaleBean>(AwarderPointOfSaleBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> adbs = AwarderDetailTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : adbs) {
			for (Object aposb : AwarderPointOfSaleTest.aposbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						AwarderDetailBean.class.cast(arr[1]),
						aposb});
			}
		}

		return params;
	}

	public AwarderPointOfSaleTest(OfferBean ob, AwarderDetailBean adb, AwarderPointOfSaleBean aposb) {

		this.adTest = new AwarderDetailTest(ob, adb);
		this.aposb = aposb;
	}

	@Before
	public void beforeTest() {

		this.adTest.beforeTest();

		this.aposb.setParentId(
				adHelper.postBean(adTest.getAwarderDetail()).getId());
	}

	@After
	public void afterTest() {

		adHelper.deleteBean(this.aposb.getParentId());

		this.adTest.afterTest();
	}

	@Test(expected=ResourceConflictException.class)
	public void postAPOSInvalidOfferId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setParentId(Long.MIN_VALUE);
		helper.postRaw(aposb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postAPOSUnknownOfferId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setParentId(Long.MAX_VALUE);
		helper.postBean(aposb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postAPOSLongStoreInternalId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setStoreInternalId(
				String.format("%" + (AwarderPointOfSale.MAX_STORE_INTERNAL_ID_LENGTH + 1) + "s", "*"));
		helper.postRaw(aposb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postAPOSInvalidAwarderDetailId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setParentId(Long.MIN_VALUE);
		helper.postRaw(aposb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postAPOSUnknownAwarderDetailId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setParentId(Long.MAX_VALUE);
		helper.postBean(aposb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getAPOSUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putAPOSInvalidId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setId(Long.MIN_VALUE);
		helper.putRaw(aposb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putAPOSUnknownId() {

		AwarderPointOfSaleBean aposb = new AwarderPointOfSaleBean().init(this.aposb);
		aposb.setId(Long.MAX_VALUE);
		helper.putBean(aposb);
	}

	@Test
	public void putAPOSLongStoreInternalId() {

		long sdId = helper.postBean(this.aposb).getId();
		AwarderPointOfSaleBean aposb = helper.getBean(sdId);
		aposb.setStoreInternalId(
				String.format("%" + (AwarderPointOfSale.MAX_STORE_INTERNAL_ID_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(aposb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		
		fail("Expected ResourceConflictException not thrown in putAPOSLongStoreInternalId()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteAPOSUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}