package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.coupon.common.bean.GlobalTradeIdentificationNumberBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.PurchaseRequirementBean;
import com.coupon.common.bean.PurchaseTradeItemBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.PurchaseRequirementType;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PurchaseTradeItemTest {

	public static final String TEST_FILE_PATH = "./TestData/PTI1A.json";

	private static WebServiceHelper<PurchaseTradeItemBean> helper =
			new WebServiceHelper<PurchaseTradeItemBean>(
				PurchaseTradeItemBean.class,
				Resource.PATH_PURCHASE_TRADE_ITEMS);
	private static WebServiceHelper<PurchaseRequirementBean> prHelper =
			new WebServiceHelper<PurchaseRequirementBean>(
				PurchaseRequirementBean.class,
				Resource.PATH_PURCHASE_REQUIREMENTS);
	private static WebServiceHelper<GlobalTradeIdentificationNumberBean> gtinHelper =
			new WebServiceHelper<GlobalTradeIdentificationNumberBean>(
				GlobalTradeIdentificationNumberBean.class,
				Resource.PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS);

	private static List<PurchaseTradeItemBean> ptibs;
	private PurchaseRequirementTest prTest;

	private PurchaseTradeItemBean ptib;
	public PurchaseTradeItemBean getPurchaseTradeItemBean() { return ptib; }

	@BeforeClass
	public static void setup() {

		PurchaseRequirementTest.setup();

		for (PurchaseTradeItemBean ptib : PurchaseTradeItemTest.ptibs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", ptib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", ptib.getTradeItemNumber().getItemReference()));
			if (gtinHelper.lookupBean(paramsMap) == null) {
				gtinHelper.postBean(ptib.getTradeItemNumber());
			}

			GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean()
					.init(ptib.getTradeItemNumber());
			gtinb.setItemReference((gtinb.getItemReference() + 1) % 100000);
			gtinb.setCheckDigit(
					ValidatorHelper.computeCheckDigit(
							gtinb.getCompanyPrefix(),
							gtinb.getItemReference().longValue()));
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", gtinb.getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", gtinb.getItemReference()));
			if (gtinHelper.lookupBean(paramsMap) == null) {
				gtinHelper.postBean(gtinb);
			}
		}
	}

	@AfterClass
	public static void cleanup() {

		for (PurchaseTradeItemBean ptib : PurchaseTradeItemTest.ptibs) {
			
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", ptib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", ptib.getTradeItemNumber().getItemReference()));
			GlobalTradeIdentificationNumberBean gtinb = gtinHelper.lookupBean(paramsMap);
			if (gtinb != null) {
				gtinHelper.deleteBean(gtinb.getId());
			}
	
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", ptib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", (ptib.getTradeItemNumber().getItemReference() + 1) % 100000));
			gtinb = gtinHelper.lookupBean(paramsMap);
			if (gtinb != null) {
				gtinHelper.deleteBean(gtinb.getId());
			}
		}

		PurchaseRequirementTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		PurchaseTradeItemTest.ptibs = new JSONHelper<PurchaseTradeItemBean>(PurchaseTradeItemBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> prbs = PurchaseRequirementTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : prbs) {
			for (Object ptib : PurchaseTradeItemTest.ptibs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						PurchaseRequirementBean.class.cast(arr[1]),
						ptib});
			}
		}

		return params;
	}

	public PurchaseTradeItemTest(OfferBean ob, PurchaseRequirementBean prb, PurchaseTradeItemBean ptib) {

		this.prTest = new PurchaseRequirementTest(ob, prb);
		this.ptib = ptib;
	}

	@Before
	public void beforeTest() {

		prTest.beforeTest();

		this.ptib.setParentId(
				prHelper.postBean(this.prTest.getPurchaseRequirement()).getId());

		Assume.assumeTrue(Arrays.asList(new PurchaseRequirementType[] {
						PurchaseRequirementType.ALL_SPECIFIED_ITEMS,
						PurchaseRequirementType.ONE_OF_SPECIFIED_ITEMS,
						PurchaseRequirementType.ONE_ITEM_PER_GROUP})
					.contains(this.prTest.getPurchaseRequirement().getPurchaseRequirementType()));
	}

	@After
	public void afterTest() {

		prHelper.deleteBean(this.ptib.getParentId());

		prTest.afterTest();
	}

	@Test
	public void test1_postPurchaseTradeItem() {

		PurchaseTradeItemBean ptib = helper.postBean(this.ptib);
		assertTrue("New PurchaseTradeItem does not match import file in test1_postPurchaseTradeItem()",
				this.ptib.equals(ptib));

		helper.deleteBean(ptib.getId());
	}
	
	@Test
	public void test2_getPurchaseTradeItem() {

		long ptiId = helper.postBean(this.ptib).getId();

		PurchaseTradeItemBean ptib = helper.getBean(ptiId);
		assertTrue("Retrieved PurchaseTradeItem does not match import file in test2_getPurchaseTradeItem()",
				this.ptib.equals(ptib));

		helper.deleteBean(ptiId);
	}

	@Test
	public void test3_putPurchaseTradeItem() {

		PurchaseTradeItemBean ptib = helper.postBean(this.ptib);

		ptib.setTradeItemQuantity((short)(ptib.getTradeItemQuantity() + 1));
		PurchaseTradeItemBean ptibUpdt = helper.putBean(ptib);
		assertTrue("tradeItemQuantity update failed in test3_putPurchaseTradeItem()",
				ptib.equals(ptibUpdt));

		if (prTest.getPurchaseRequirement().getPurchaseRequirementType() ==
				PurchaseRequirementType.ONE_ITEM_PER_GROUP) {

			ptib = helper.getBean(ptib.getId());
			ptib.setTradeItemGroup(ptib.getTradeItemGroup() + " UPDTD");
			ptibUpdt = helper.putBean(ptib);
			assertTrue("tradeItemGroup update failed in test3_putPurchaseTradeItem()",
					ptib.equals(ptibUpdt));
		}

		helper.deleteBean(ptib.getId());
	}
	
	@Test
	public void test4_deletePurchaseTradeItem() {

		long ptiId = helper.postBean(this.ptib).getId();
		helper.getBean(ptiId);
		helper.deleteBean(ptiId);

		try {
			helper.getBean(ptiId);
			fail("delete failed in test4_deletePurchaseTradeItem()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}