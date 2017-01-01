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
import com.coupon.common.bean.RewardTradeItemBean;
import com.coupon.common.bean.RewardBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.RewardType;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.action.RewardTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RewardTradeItemTest {

	public static final String TEST_FILE_PATH = "./TestData/RTI1.json";

	private static WebServiceHelper<RewardTradeItemBean> helper =
			new WebServiceHelper<RewardTradeItemBean>(
				RewardTradeItemBean.class,
				Resource.PATH_REWARD_TRADE_ITEMS);
	private static WebServiceHelper<RewardBean> rHelper =
			new WebServiceHelper<RewardBean>(
				RewardBean.class,
				Resource.PATH_REWARDS);
	private static WebServiceHelper<GlobalTradeIdentificationNumberBean> gtinHelper =
			new WebServiceHelper<GlobalTradeIdentificationNumberBean>(
				GlobalTradeIdentificationNumberBean.class,
				Resource.PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS);

	private static List<RewardTradeItemBean> rtibs;
	private RewardTest rTest;
	
	private RewardTradeItemBean rtib;
	public RewardTradeItemBean getRewardTradeItem() { return rtib; }

	@BeforeClass
	public static void setup() {

		RewardTest.setup();

		for (RewardTradeItemBean rtib : RewardTradeItemTest.rtibs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", rtib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", rtib.getTradeItemNumber().getItemReference()));
			if (gtinHelper.lookupBean(paramsMap) == null) {
				gtinHelper.postBean(rtib.getTradeItemNumber());
			}

			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", rtib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", (rtib.getTradeItemNumber().getItemReference() + 1) % 100000));
			if (gtinHelper.lookupBean(paramsMap) == null) {
				GlobalTradeIdentificationNumberBean gtinbUpdt =
						new GlobalTradeIdentificationNumberBean()
							.init(rtib.getTradeItemNumber());
				gtinbUpdt.setItemReference((gtinbUpdt.getItemReference() + 1) % 100000);
				gtinbUpdt.setCheckDigit(
						ValidatorHelper.computeCheckDigit(
								gtinbUpdt.getCompanyPrefix(),
								gtinbUpdt.getItemReference().longValue()));
				gtinHelper.postBean(gtinbUpdt);
			}
		}
	}

	@AfterClass
	public static void cleanup() {

		for (RewardTradeItemBean rtib : RewardTradeItemTest.rtibs) {

			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("companyPrefix",
					String.format("%d", rtib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", rtib.getTradeItemNumber().getItemReference()));
			GlobalTradeIdentificationNumberBean gtinb = gtinHelper.lookupBean(paramsMap);
			if (gtinb != null) {
				gtinHelper.deleteBean(gtinb.getId());
			}
	
			paramsMap.clear();
			paramsMap.put("companyPrefix",
					String.format("%d", rtib.getTradeItemNumber().getCompanyPrefix()));
			paramsMap.put("itemReference",
					String.format("%d", (rtib.getTradeItemNumber().getItemReference() + 1) % 100000));
			gtinb = gtinHelper.lookupBean(paramsMap);
			if (gtinb != null) {
				gtinHelper.deleteBean(gtinb.getId());
			}
		}
		
		RewardTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		RewardTradeItemTest.rtibs = new JSONHelper<RewardTradeItemBean>(RewardTradeItemBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> rbs = RewardTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : rbs) {
			for (Object rtib : RewardTradeItemTest.rtibs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						RewardBean.class.cast(arr[1]),
						rtib});
			}
		}

		return params;
	}

	public RewardTradeItemTest(OfferBean ob, RewardBean rb, RewardTradeItemBean rtib) {

		this.rTest = new RewardTest(ob, rb);
		this.rtib = rtib;
	}

	@Before
	public void beforeTest() {

		this.rTest.beforeTest();

		this.rtib.setParentId(
				rHelper.postBean(this.rTest.getReward()).getId());

		Assume.assumeTrue(this.rTest.getReward().getRewardType() ==
				RewardType.TRADE_ITEM_REWARD);
	}

	@After
	public void afterTest() {

		rHelper.deleteBean(this.rtib.getParentId());

		this.rTest.afterTest();
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteRTIInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getRTIInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRTINullTradeItemNumber() {

		RewardTradeItemBean rtib = new RewardTradeItemBean().init(this.rtib);
		rtib.setTradeItemNumber(null);
		helper.postRaw(rtib);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRTINegativeRewardId() {

		RewardTradeItemBean rtib = new RewardTradeItemBean().init(this.rtib);
		rtib.setParentId(Long.MIN_VALUE);
		helper.postRaw(rtib);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postRTIInvalidRewardId() {

		RewardTradeItemBean rtib = new RewardTradeItemBean().init(this.rtib);
		rtib.setParentId(Long.MAX_VALUE);
		helper.postBean(rtib);
	}

	@Test
	public void putRTINullTradeItemNumber() {

		long rtiId = helper.postBean(this.rtib).getId();
		RewardTradeItemBean rtib = helper.getBean(rtiId);
		rtib.setTradeItemNumber(null);
		try {
			helper.putRaw(rtib);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(rtiId);
		}
		
		fail("Expected ResourceConflictException not thrown in putRTINullTradeItemNumber()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putRTIInvalidId() {

		RewardTradeItemBean rtib = new RewardTradeItemBean().init(this.rtib);
		rtib.setId(Long.MAX_VALUE);
		helper.putBean(rtib);
	}

}