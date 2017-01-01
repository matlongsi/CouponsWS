package com.coupon.test.error;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OfferTest {

	public static final String TEST_FILE_PATH = "./TestData/OFR1.json";

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

	@Test(expected=ResourceNotFoundException.class)
	public void deleteOfferInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getOfferInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void postOfferNullIssuerNumber() {

		OfferBean ob = new OfferBean().init(this.ob);
		ob.setIssuerNumber(null);
		helper.postRaw(ob);
	}

	@Test(expected=ResourceConflictException.class)
	public void postOfferNullDistributorNumber() {

		OfferBean ob = new OfferBean().init(this.ob);
		ob.setDistributorNumber(null);
		helper.postRaw(ob);
	}

	@Test
	public void putOfferNullIssuerNumber() {

		long oId = helper.postBean(this.ob).getId();
		OfferBean ob = helper.getBean(oId);
		ob.setIssuerNumber(null);
		try {
			helper.putRaw(ob);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(oId);
		}
		
		fail("Expected ResourceConflictException not thrown in putOfferNullIssuerNumber()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putOfferInvalidId() {

		OfferBean ob = new OfferBean().init(this.ob);
		ob.setId(Long.MAX_VALUE);
		helper.putBean(ob);
	}

}
