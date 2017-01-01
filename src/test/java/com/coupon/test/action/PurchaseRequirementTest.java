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
public class PurchaseRequirementTest {

	public static final String TEST_FILE_PATH = "./TestData/PR1A.json";

	private static WebServiceHelper<PurchaseRequirementBean> helper =
			new WebServiceHelper<PurchaseRequirementBean>(
				PurchaseRequirementBean.class,
				Resource.PATH_PURCHASE_REQUIREMENTS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);
	private static WebServiceHelper<GlobalTradeIdentificationNumberBean> gtinHelper =
			new WebServiceHelper<GlobalTradeIdentificationNumberBean>(
				GlobalTradeIdentificationNumberBean.class,
				Resource.PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS);

	private OfferTest oTest;
	private static List<PurchaseRequirementBean> prbs;

	private PurchaseRequirementBean prb;
	public PurchaseRequirementBean getPurchaseRequirement() { return prb; }

	@BeforeClass
	public static void setup() {

		OfferTest.setup();

		for (PurchaseRequirementBean prb : PurchaseRequirementTest.prbs) {
			
			if (Arrays.asList(new PurchaseRequirementType[] {
					PurchaseRequirementType.ALL_SPECIFIED_ITEMS,
					PurchaseRequirementType.ONE_OF_SPECIFIED_ITEMS,
					PurchaseRequirementType.ONE_ITEM_PER_GROUP})
				.contains(prb.getPurchaseRequirementType())) {
	
				for (PurchaseTradeItemBean ptib : prb.getPurchaseTradeItems()) {

					Map<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("companyPrefix",
							String.format("%d", ptib.getTradeItemNumber().getCompanyPrefix()));
					paramsMap.put("itemReference",
							String.format("%d", ptib.getTradeItemNumber().getItemReference()));
					if (gtinHelper.lookupBean(paramsMap) == null) {
						gtinHelper.postBean(ptib.getTradeItemNumber());
					}

					GlobalTradeIdentificationNumberBean gtinbUpdt =
							new GlobalTradeIdentificationNumberBean()
								.init(ptib.getTradeItemNumber());
					gtinbUpdt.setItemReference((gtinbUpdt.getItemReference() + 1) % 100000);
					gtinbUpdt.setCheckDigit(
							ValidatorHelper.computeCheckDigit(
									gtinbUpdt.getCompanyPrefix(),
									gtinbUpdt.getItemReference().longValue()));
					paramsMap.clear();
					paramsMap.put("companyPrefix",
							String.format("%d", gtinbUpdt.getCompanyPrefix()));
					paramsMap.put("itemReference",
							String.format("%d", gtinbUpdt.getItemReference()));
					if (gtinHelper.lookupBean(paramsMap) == null) {
						gtinHelper.postBean(gtinbUpdt);
					}
				}
			}
		}
	}

	@AfterClass
	public static void cleanup() {

		for (PurchaseRequirementBean prb : PurchaseRequirementTest.prbs) {

			if (Arrays.asList(new PurchaseRequirementType[] {
				PurchaseRequirementType.ALL_SPECIFIED_ITEMS,
				PurchaseRequirementType.ONE_OF_SPECIFIED_ITEMS,
				PurchaseRequirementType.ONE_ITEM_PER_GROUP})
			.contains(prb.getPurchaseRequirementType())) {

				for (PurchaseTradeItemBean ptib : prb.getPurchaseTradeItems()) {
	
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
			}
		}

		OfferTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		PurchaseRequirementTest.prbs = new JSONHelper<PurchaseRequirementBean>(PurchaseRequirementBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object prb : PurchaseRequirementTest.prbs) {
				params.add(new Object[] {ob, prb});
			}
		}

		return params;
	}

	public static Collection<Object[]> load(String oFilePath, String prFilePath) throws JAXBException {

		PurchaseRequirementTest.prbs = new JSONHelper<PurchaseRequirementBean>(PurchaseRequirementBean.class)
				.arrayFromJson(prFilePath);

		Collection<? extends Object> obs = OfferTest.load(oFilePath);

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object prb : PurchaseRequirementTest.prbs) {
				params.add(new Object[] {ob, prb});
			}
		}

		return params;
	}

	public PurchaseRequirementTest(OfferBean ob, PurchaseRequirementBean prb) {

		this.oTest = new OfferTest(ob);
		this.prb = prb;
	}

	@Before
	public void beforeTest() {
		
		this.prb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.prb.getParentId());
	}

	@Test
	public void test1_postPurchaseRequirement() {

		PurchaseRequirementBean prb = helper.postBean(this.prb);
		assertTrue("New PurchaseRequirement does not match import file test1_postPurchaseRequirement()",
				this.prb.equals(prb));

		helper.deleteBean(prb.getId());
	}
	
	@Test
	public void test2_getPurchaseRequirementById() {

		long prId = helper.postBean(this.prb).getId();

		PurchaseRequirementBean prb = helper.getBean(prId);
		assertTrue("Retrieved PurchaseRequirement does not match import file in test2_getPurchaseRequirementById()",
				this.prb.equals(prb));
		
		helper.deleteBean(prId);
	}

	@Test
	public void test3_putPurchaseRequirement() {

		PurchaseRequirementBean prb = helper.postBean(this.prb);

		switch (prb.getPurchaseRequirementType()) {

		case ALL_SPECIFIED_ITEMS:
		case ONE_OF_SPECIFIED_ITEMS:
			for (PurchaseTradeItemBean ptib : prb.getPurchaseTradeItems()) {

				ptib.setTradeItemQuantity((short)(ptib.getTradeItemQuantity() + 1));
			}
			break;

		case ONE_ITEM_PER_GROUP:
			for (PurchaseTradeItemBean ptib : prb.getPurchaseTradeItems()) {

				ptib.setTradeItemQuantity((short)(ptib.getTradeItemQuantity() + 1));
				ptib.setTradeItemGroup(ptib.getTradeItemGroup() + " UPDTD");
			}
			break;

		case SPECIFIED_PURCHASE_AMOUNT:
			prb.setPurchaseMonetaryAmount(prb.getPurchaseMonetaryAmount() + 1);
			break;
		}
		PurchaseRequirementBean prbUpdt = helper.putBean(prb);
		assertTrue("PurchaseRequirement update failed in test4_putPurchaseRequirement()",
				prb.equals(prbUpdt));

		helper.deleteBean(prb.getId());
	}

	@Test
	public void test4_deletePurchaseRequirement() {

		long prId = helper.postBean(this.prb).getId();
		helper.getBean(prId);
		helper.deleteBean(prId);

		try {
			helper.getBean(prId);
			fail("delete failed in test4_deletePurchaseRequirement()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}