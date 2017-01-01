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

import com.coupon.common.bean.AwarderDetailBean;
import com.coupon.common.bean.AwarderPointOfSaleBean;
import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.action.OfferTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AwarderDetailTest {

	public static final String TEST_FILE_PATH = "./TestData/AD1.json";

	private static WebServiceHelper<AwarderDetailBean> helper =
			new WebServiceHelper<AwarderDetailBean>(
				AwarderDetailBean.class,
				Resource.PATH_AWARDER_DETAILS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);
	private static WebServiceHelper<GlobalLocationNumberBean> glnHelper =
			new WebServiceHelper<GlobalLocationNumberBean>(
				GlobalLocationNumberBean.class,
				Resource.PATH_GLOBAL_LOCATION_NUMBERS);

	private OfferTest oTest;
	private static List<AwarderDetailBean> adbs;

	private AwarderDetailBean adb;
	public AwarderDetailBean getAwarderDetail() { return adb; }
	public OfferBean getOffer() { return oTest.getOffer(); }

	@BeforeClass
	public static void setup() {

		OfferTest.setup();

		for (AwarderDetailBean adb : AwarderDetailTest.adbs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", adb.getAwarderNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", adb.getAwarderNumber().getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(adb.getAwarderNumber());
			}
			GlobalLocationNumberBean anbUpdt = new GlobalLocationNumberBean()
					.init(adb.getAwarderNumber());
			anbUpdt.setCompanyPrefix((anbUpdt.getCompanyPrefix() + 1) % 100000000000L);
			anbUpdt.setCheckDigit(
					ValidatorHelper.computeCheckDigit(
							anbUpdt.getCompanyPrefix(),
							anbUpdt.getLocationReference().longValue()));
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", anbUpdt.getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", anbUpdt.getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(anbUpdt);
			}

			if (adb.getAwarderClearingAgentNumber() != null) {

				paramsMap.clear();
				paramsMap.put("companyPrefix",
						String.format("%d", adb.getAwarderClearingAgentNumber().getCompanyPrefix()));
				paramsMap.put("locationReference",
						String.format("%d", adb.getAwarderClearingAgentNumber().getLocationReference()));
				if (glnHelper.lookupBean(paramsMap) == null) {
					glnHelper.postBean(adb.getAwarderClearingAgentNumber());
				}
			}
			GlobalLocationNumberBean acanbUpdt = new GlobalLocationNumberBean()
					.init(adb.getAwarderNumber());
			acanbUpdt.setCompanyPrefix((acanbUpdt.getCompanyPrefix() + 2) % 100000000000L);
			acanbUpdt.setCheckDigit(
					ValidatorHelper.computeCheckDigit(
							acanbUpdt.getCompanyPrefix(),
							acanbUpdt.getLocationReference().longValue()));
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", acanbUpdt.getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", acanbUpdt.getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(acanbUpdt);
			}

			for (AwarderPointOfSaleBean aposb : adb.getPointOfSales()) {

				paramsMap.clear();
				paramsMap.put("companyPrefix",
						String.format("%d", aposb.getStoreNumber().getCompanyPrefix()));
				paramsMap.put("locationReference",
						String.format("%d", aposb.getStoreNumber().getLocationReference()));
				if (glnHelper.lookupBean(paramsMap) == null) {
					glnHelper.postBean(aposb.getStoreNumber());
				}

				GlobalLocationNumberBean aposbUpdt = new GlobalLocationNumberBean()
						.init(aposb.getStoreNumber());
				aposbUpdt.setLocationReference((aposbUpdt.getLocationReference() + 1) % 100000);
				aposbUpdt.setCheckDigit(
						ValidatorHelper.computeCheckDigit(
								aposbUpdt.getCompanyPrefix(),
								aposbUpdt.getLocationReference().longValue()));
				paramsMap.clear();
				paramsMap.put("companyPrefix",
						String.format("%d", aposbUpdt.getCompanyPrefix()));
				paramsMap.put("locationReference",
						String.format("%d", aposbUpdt.getLocationReference()));
				if (glnHelper.lookupBean(paramsMap) == null) {
					glnHelper.postBean(aposbUpdt);
				}
			}
		}
	}

	@AfterClass
	public static void cleanup() {

		for (AwarderDetailBean adb : AwarderDetailTest.adbs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", adb.getAwarderNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", adb.getAwarderNumber().getLocationReference()));
			GlobalLocationNumberBean glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", (adb.getAwarderNumber().getCompanyPrefix() + 1) % 100000000000L));
			paramsMap.put("locationReference",
					String.format("%d", adb.getAwarderNumber().getLocationReference()));
			glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}
	
			if (adb.getAwarderClearingAgentNumber() != null) {
	
				paramsMap.clear();
				paramsMap.put("companyPrefix",
						String.format("%d", adb.getAwarderClearingAgentNumber().getCompanyPrefix()));
				paramsMap.put("locationReference",
						String.format("%d", adb.getAwarderClearingAgentNumber().getLocationReference()));
				glnb = glnHelper.lookupBean(paramsMap);
				if (glnb != null) {
					glnHelper.deleteBean(glnb.getId());
				}
			}
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", (adb.getAwarderNumber().getCompanyPrefix() + 2) % 100000000000L));
			paramsMap.put("locationReference",
					String.format("%d", adb.getAwarderNumber().getLocationReference()));
			glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}
	
			for (AwarderPointOfSaleBean aposb : adb.getPointOfSales()) {
	
				paramsMap.clear();
				paramsMap.put("companyPrefix",
						String.format("%d", aposb.getStoreNumber().getCompanyPrefix()));
				paramsMap.put("locationReference",
						String.format("%d", aposb.getStoreNumber().getLocationReference()));
				glnb = glnHelper.lookupBean(paramsMap);
				if (glnb != null) {
					glnHelper.deleteBean(glnb.getId());
				}

				paramsMap.clear();
				paramsMap.put("companyPrefix",
						String.format("%d", aposb.getStoreNumber().getCompanyPrefix()));
				paramsMap.put("locationReference",
						String.format("%d", (aposb.getStoreNumber().getLocationReference() + 1) % 100000));
				glnb = glnHelper.lookupBean(paramsMap);
				if (glnb != null) {
					glnHelper.deleteBean(glnb.getId());
				}
			}
		}

		OfferTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		AwarderDetailTest.adbs = new JSONHelper<AwarderDetailBean>(AwarderDetailBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object adb : AwarderDetailTest.adbs) {
				params.add(new Object[] {ob, adb});
			}
		}

		return params;
	}

	public static Collection<Object[]> load(String oFilePath, String adFilePath) throws JAXBException {

		AwarderDetailTest.adbs = new JSONHelper<AwarderDetailBean>(AwarderDetailBean.class)
				.arrayFromJson(adFilePath);

		Collection<? extends Object> obs = OfferTest.load(oFilePath);

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object adb : AwarderDetailTest.adbs) {
				params.add(new Object[] {ob, adb});
			}
		}

		return params;
	}

	public AwarderDetailTest(OfferBean ob, AwarderDetailBean rb) {

		this.oTest = new OfferTest(ob);
		this.adb = rb;
	}

	@Before
	public void beforeTest() {
		
		this.adb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.adb.getParentId());
	}

	@Test(expected=ResourceConflictException.class)
	public void postADInvalidOfferId() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setParentId(Long.MIN_VALUE);
		helper.postRaw(adb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postADUnknownOfferId() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setParentId(Long.MAX_VALUE);
		helper.postBean(adb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postADNullAwarderNumber() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setAwarderNumber(null);
		helper.postRaw(adb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postADUnknownAwarderNumber() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setAwarderNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)0));
		helper.postBean(adb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postADInvalidAwarderNumber() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setAwarderNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)1));
		helper.postRaw(adb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postADUnknownAwarderClearingAgentNumber() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setAwarderClearingAgentNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)0));
		helper.postBean(adb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postADInvalidAwarderClearingAgentNumber() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setAwarderClearingAgentNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)1));
		helper.postRaw(adb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getADUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putADInvalidId() {

		AwarderDetailBean adb = new AwarderDetailBean().init(this.adb);
		adb.setId(Long.MIN_VALUE);
		helper.putRaw(adb);
	}

	@Test
	public void putADUnknownId() {

		long adId = helper.postBean(this.adb).getId();
		AwarderDetailBean adb = helper.getBean(adId);
		adb.setId(Long.MAX_VALUE);
		try {
			helper.putBean(adb);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(adId);
		}
		
		fail("Expected ResourceNotFoundException not thrown in putADUnknownId()");
	}

	@Test
	public void putADNullAwarderNumber() {

		long adId = helper.postBean(this.adb).getId();
		AwarderDetailBean adb = helper.getBean(adId);
		adb.setAwarderNumber(null);
		try {
			helper.putRaw(adb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(adId);
		}
		
		fail("Expected ResourceConflictException not thrown in putADNullAwarderNumber()");
	}
	
	@Test
	public void putADUnknownAwarderNumber() {

		long adId = helper.postBean(this.adb).getId();
		AwarderDetailBean adb = helper.getBean(adId);
		adb.setAwarderNumber(
				new GlobalLocationNumberBean(Long.valueOf(0),
						Integer.valueOf(0),
						Byte.valueOf((byte) 0)));
		try {
			helper.putBean(adb);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(adId);
		}
		
		fail("Expected ResourceNotFoundException not thrown in putADUnknownAwarderNumber()");
	}
	
	@Test
	public void putADInvalidAwarderNumber() {

		long adId = helper.postBean(this.adb).getId();
		AwarderDetailBean adb = helper.getBean(adId);
		adb.setAwarderNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)1));
		try {
			helper.putRaw(adb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(adId);
		}
		
		fail("Expected ResourceConflictException not thrown in putADInvalidAwarderNumber()");
	}

	@Test
	public void putADUnknownAwarderClearingAgentNumber() {

		long adId = helper.postBean(this.adb).getId();
		AwarderDetailBean adb = helper.getBean(adId);
		adb.setAwarderClearingAgentNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)0));
		try {
			helper.putRaw(adb);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(adId);
		}

		fail("Expected ResourceNotFoundException not thrown in putADUnknownAwarderClearingAgentNumber()");
	}

	@Test
	public void putADInvalidAwarderClearingAgentNumber() {

		long adId = helper.postBean(this.adb).getId();
		AwarderDetailBean adb = helper.getBean(adId);
		adb.setAwarderClearingAgentNumber(
				new GlobalLocationNumberBean(0L, 0, (byte)1));
		try {
			helper.putRaw(adb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(adId);
		}

		fail("Expected ResourceConflictException not thrown in putADInvalidAwarderClearingAgentNumber()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteADUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}