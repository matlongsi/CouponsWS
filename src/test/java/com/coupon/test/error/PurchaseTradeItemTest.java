package com.coupon.test.error;

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

import com.coupon.common.PurchaseTradeItem;
import com.coupon.common.bean.PurchaseTradeItemBean;
import com.coupon.common.bean.GlobalTradeIdentificationNumberBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.PurchaseRequirementBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.PurchaseRequirementType;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.action.PurchaseRequirementTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PurchaseTradeItemTest {

	public static final String TEST_FILE_PATH = "./TestData/PTI1.json";

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

	@Test(expected=ResourceNotFoundException.class)
	public void deletePTIInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getPTIInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void postPTINullTradeItemNumber() {

		PurchaseTradeItemBean ptib = new PurchaseTradeItemBean().init(this.ptib);
		ptib.setTradeItemNumber(null);
		helper.postRaw(ptib);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postPTILongTradeItemGroup() {

		PurchaseTradeItemBean ptib = new PurchaseTradeItemBean().init(this.ptib);
		ptib.setTradeItemGroup(
				String.format("%" + (PurchaseTradeItem.MAX_TRADE_ITEM_GROUP_NAME_LENGTH + 1) + "s", "*"));
		helper.postRaw(ptib);
	}

	@Test(expected=ResourceConflictException.class)
	public void postPTINegativePurchaseRequirementId() {

		PurchaseTradeItemBean ptib = new PurchaseTradeItemBean().init(this.ptib);
		ptib.setParentId(Long.MIN_VALUE);
		helper.postRaw(ptib);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postPTIInvalidPurchaseRequirementId() {

		PurchaseTradeItemBean ptib = new PurchaseTradeItemBean().init(this.ptib);
		ptib.setParentId(Long.MAX_VALUE);
		helper.postBean(ptib);
	}

	@Test
	public void putPTINullTradeItemNumber() {

		long ptiId = helper.postBean(this.ptib).getId();
		PurchaseTradeItemBean ptib = helper.getBean(ptiId);
		ptib.setTradeItemNumber(null);
		try {
			helper.putRaw(ptib);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ptiId);
		}
		
		fail("Expected ResourceConflictException not thrown in putPTINullTradeItemNumber()");
	}

	@Test
	public void putPTILongTradeItemGroup() {

		long ptiId = helper.postBean(this.ptib).getId();
		PurchaseTradeItemBean ptib = helper.getBean(ptiId);
		ptib.setTradeItemGroup(
				String.format("%" + (PurchaseTradeItem.MAX_TRADE_ITEM_GROUP_NAME_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(ptib);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ptiId);
		}
		
		fail("Expected ResourceConflictException not thrown in putPTILongTradeItemGroup()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putPTIInvalidId() {

		PurchaseTradeItemBean ptib = new PurchaseTradeItemBean().init(this.ptib);
		ptib.setId(Long.MAX_VALUE);
		helper.putBean(ptib);
	}

}