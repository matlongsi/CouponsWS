package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.coupon.common.bean.GlobalCouponNumberBean;
import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.OfferStatusType;
import com.coupon.common.type.OfferType;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OfferTest {

	public static final String TEST_FILE_PATH = "./TestData/OFR1A.json";

	private static WebServiceHelper<OfferBean> helper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);
	private static WebServiceHelper<GlobalLocationNumberBean> glnHelper =
			new WebServiceHelper<GlobalLocationNumberBean>(
				GlobalLocationNumberBean.class,
				Resource.PATH_GLOBAL_LOCATION_NUMBERS);

	private static List<OfferBean> obs;

	private OfferBean ob;
	public OfferBean getOffer() { return ob; }

	@BeforeClass
	public static void setup() {

		for (OfferBean ob : OfferTest.obs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", ob.getDistributorNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", ob.getDistributorNumber().getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(ob.getDistributorNumber());
			}

			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", ob.getIssuerNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", ob.getIssuerNumber().getLocationReference()));
			if (glnHelper.lookupBean(paramsMap) == null) {
				glnHelper.postBean(ob.getIssuerNumber());
			}
		}
	}
	
	@AfterClass
	public static void cleanup() {

		for (OfferBean ob : OfferTest.obs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", ob.getDistributorNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", ob.getDistributorNumber().getLocationReference()));
			GlobalLocationNumberBean glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}
	
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", ob.getIssuerNumber().getCompanyPrefix()));
			paramsMap.put("locationReference",
					String.format("%d", ob.getIssuerNumber().getLocationReference()));
			glnb = glnHelper.lookupBean(paramsMap);
			if (glnb != null) {
				glnHelper.deleteBean(glnb.getId());
			}
		}
	}

	@Parameters
	public static Collection<? extends Object> load() throws JAXBException {

		OfferTest.obs = new JSONHelper<OfferBean>(OfferBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		return OfferTest.obs;
	}

	public static Collection<? extends Object> load(String filePath) throws JAXBException {
		
		OfferTest.obs = new JSONHelper<OfferBean>(OfferBean.class)
				.arrayFromJson(filePath);

		return OfferTest.obs;
	}

	public OfferTest(OfferBean ob) {

		this.ob = ob;
	}

	@Test
	public void test1_postOffer() {

		OfferBean ob = helper.postBean(this.ob);
		assertTrue("New Offer does not match import file in test1_postOffer()",
				this.ob.equals(ob));
		
		helper.deleteBean(ob.getId());
	}

	@Test
	public void test2_getOfferById() {

		long oId = helper.postBean(this.ob).getId();

		OfferBean ob = helper.getBean(oId);
		assertTrue("Retrieved Offer does not match import file in test2_getOfferById()",
				this.ob.equals(ob));

		helper.deleteBean(oId);
	}

	@Test
	public void test3_getOfferByNumber() {

		long oId = helper.postBean(this.ob).getId();

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", this.ob.getOfferNumber().getCompanyPrefix()));
		paramsMap.put("couponReference",
				String.format("%d", this.ob.getOfferNumber().getCouponReference()));
		paramsMap.put("serialComponent",
				String.format("%d", this.ob.getOfferNumber().getSerialComponent()));
		OfferBean ob = helper.lookupBean(paramsMap);
		assertTrue("Retrieved Offer does not match import file in test3_getOfferByNumber()",
				this.ob.equals(ob));

		helper.deleteBean(oId);
	}

	@Test
	public void test4_putOffer() {

		long oId = helper.postBean(this.ob).getId();

		GlobalLocationNumberBean dnbUpdt = new GlobalLocationNumberBean()
				.init(this.ob.getDistributorNumber());
		dnbUpdt.setLocationReference((dnbUpdt.getLocationReference() + 1) % 100000);
		dnbUpdt.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						dnbUpdt.getCompanyPrefix(),
						dnbUpdt.getLocationReference().longValue()));
		dnbUpdt = glnHelper.postBean(dnbUpdt);

		GlobalLocationNumberBean inbUpdt = new GlobalLocationNumberBean()
				.init(this.ob.getIssuerNumber());
		inbUpdt.setLocationReference((inbUpdt.getLocationReference() + 1) % 100000);
		inbUpdt.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						inbUpdt.getCompanyPrefix(),
						inbUpdt.getLocationReference().longValue()));
		inbUpdt = glnHelper.postBean(inbUpdt);

		OfferBean ob = helper.getBean(oId);
		ob.getTimePeriod().getEndDateTime().setTime(
				ob.getTimePeriod().getEndDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		OfferBean obUpdt = helper.putBean(ob);
		assertTrue("timePeriod update failed in test4_putOffer",
				ob.equals(obUpdt));

		ob = helper.getBean(oId);
		GlobalCouponNumberBean onb = ob.getOfferNumber();
		onb.setCouponReference((onb.getCouponReference() + 1) % 100000);
		onb.setCheckDigit(
				ValidatorHelper.computeCheckDigit(
						onb.getCompanyPrefix(),
						onb.getCouponReference().longValue()));
		ob.setOfferNumber(onb);
		obUpdt = helper.putBean(ob);
		assertTrue("offerNumber update failed in test4_putOffer",
				ob.equals(obUpdt));

		ob = helper.getBean(oId);
		ob.setDistributorNumber(dnbUpdt);
		obUpdt = helper.putBean(ob);
		assertTrue("distributorNumber update failed in test4_putOffer",
				ob.equals(obUpdt));

		ob = helper.getBean(oId);
		ob.setIssuerNumber(inbUpdt);
		obUpdt = helper.putBean(ob);
		assertTrue("issuerNumber update failed in test4_putOffer",
				ob.equals(obUpdt));

		ob = helper.getBean(oId);
		ob.setOfferType(OfferType.NON_MANUFACTURER_COUPON);
		obUpdt = helper.putBean(ob);
		assertTrue("offerType update failed in test4_putOffer",
				ob.equals(obUpdt));

		ob = helper.getBean(oId);
		ob.setOfferStatus(OfferStatusType.SETUP);
		obUpdt = helper.putBean(ob);
		assertTrue("offerStatus update failed in test4_putOffer",
				ob.equals(obUpdt));

		ob = helper.getBean(oId);
		ob.setTimeZone("US/Pacific");
		obUpdt = helper.putBean(ob);
		assertTrue("timeZone update failed in test4_putOffer",
				ob.equals(obUpdt));

		helper.deleteBean(oId);
		glnHelper.deleteBean(dnbUpdt.getId());
		glnHelper.deleteBean(inbUpdt.getId());
	}
	
	@Test
	public void test5_deleteOffer() {

		long oId = helper.postBean(this.ob).getId();
		helper.getBean(oId);
		helper.deleteBean(oId);

		try {
			helper.getBean(oId);
			fail("delete failed in test5_deleteOffer()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}
