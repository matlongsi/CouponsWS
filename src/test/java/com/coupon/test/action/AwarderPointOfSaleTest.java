package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
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
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AwarderPointOfSaleTest {

	public static final String TEST_FILE_PATH = "./TestData/APOS1A.json";

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

	@Test
	public void test1_postAwarderPointOfSale() {

		AwarderPointOfSaleBean aposb = helper.postBean(this.aposb);
		assertTrue("New AwarderPointOfSale does not match import file in test1_postAwarderPointOfSale()",
				this.aposb.equals(aposb));
		helper.deleteBean(aposb.getId());

		AwarderPointOfSaleBean aposbEdit = new AwarderPointOfSaleBean().init(this.aposb);
		aposbEdit.setStoreNumber(null);
		aposb = helper.postBean(aposbEdit);
		assertTrue("New AwarderPointOfSale w/o storeNumber does not match import file in test1_postAwarderPointOfSale()",
				aposbEdit.equals(aposb));
		helper.deleteBean(aposb.getId());

		aposbEdit = new AwarderPointOfSaleBean().init(this.aposb);
		aposbEdit.setStoreInternalId(null);
		aposb = helper.postBean(aposbEdit);
		assertTrue("New AwarderPointOfSale w/o storeInternalId does not match import file in test1_postAwarderPointOfSale()",
				aposbEdit.equals(aposb));
		helper.deleteBean(aposb.getId());
	}
	
	@Test
	public void test2_getAwarderPointOfSaleById() {

		long aposId = helper.postBean(this.aposb).getId();

		AwarderPointOfSaleBean aposb = helper.getBean(aposId);
		assertTrue("Retrieved AwarderPointOfSale does not match import file in test2_getAwarderPointOfSaleById()",
				this.aposb.equals(aposb));

		helper.deleteBean(aposId);
	}

	@Test
	public void test3_putAwarderPointOfSale() {
		
		long aposId = helper.postBean(this.aposb).getId();

		AwarderPointOfSaleBean aposb = helper.getBean(aposId);
		aposb.setStoreInternalId(aposb.getStoreInternalId() + " UPDTD");
		AwarderPointOfSaleBean aposbUpdt = helper.putBean(aposb);
		assertTrue("storeInternalId update failed in test4_putAwarderPointOfSale()",
				aposb.equals(aposbUpdt));

		helper.deleteBean(aposId);
	}
	
	@Test
	public void test4_deleteAwarderPointOfSale() {

		long aposId = helper.postBean(this.aposb).getId();
		helper.getBean(aposId);
		helper.deleteBean(aposId);

		try {
			helper.getBean(aposId);
			fail("delete failed in test4_deleteAwarderPointOfSale()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}