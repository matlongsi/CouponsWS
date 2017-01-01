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

import com.coupon.common.bean.GlobalTradeIdentificationNumberBean;
import com.coupon.common.bean.RewardBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.RewardTradeItemBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.RewardType;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.resource.Resource;
import com.coupon.test.action.OfferTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RewardTest {

	public static final String TEST_FILE_PATH = "./TestData/R1.json";

	private static WebServiceHelper<RewardBean> helper =
			new WebServiceHelper<RewardBean>(
				RewardBean.class,
				Resource.PATH_REWARDS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);
	private static WebServiceHelper<GlobalTradeIdentificationNumberBean> gtinHelper =
			new WebServiceHelper<GlobalTradeIdentificationNumberBean>(
				GlobalTradeIdentificationNumberBean.class,
				Resource.PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS);

	private OfferTest oTest;
	private static List<RewardBean> rbs;

	private RewardBean rb;
	public RewardBean getReward() { return rb; }

	@BeforeClass
	public static void setup() {

		OfferTest.setup();

		for (RewardBean rb : RewardTest.rbs) {

			if (rb.getRewardType() == RewardType.TRADE_ITEM_REWARD) {

				for (RewardTradeItemBean rtib : rb.getRewardTradeItems()) {

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
		}
	}
	
	@AfterClass
	public static void cleanup() {

		for (RewardBean rb : RewardTest.rbs) {

			if (rb.getRewardType() == RewardType.TRADE_ITEM_REWARD) {
	
				for (RewardTradeItemBean rtib : rb.getRewardTradeItems()) {
	
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
			}
		}
		
		OfferTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		RewardTest.rbs = new JSONHelper<RewardBean>(RewardBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object rb : RewardTest.rbs) {
				params.add(new Object[] {ob, rb});
			}
		}

		return params;
	}

	public static Collection<Object[]> load(String oFilePath, String rFilePath) throws JAXBException {

		RewardTest.rbs = new JSONHelper<RewardBean>(RewardBean.class)
				.arrayFromJson(rFilePath);

		Collection<? extends Object> obs = OfferTest.load(oFilePath);

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object rb : RewardTest.rbs) {
				params.add(new Object[] {ob, rb});
			}
		}

		return params;
	}

	public RewardTest(OfferBean ob, RewardBean rb) {

		this.oTest = new OfferTest(ob);
		this.rb = rb;
	}

	@Before
	public void beforeTest() {
		
		this.rb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.rb.getParentId());
	}

	@Test(expected=ResourceConflictException.class)
	public void postRInvalidOfferId() {

		RewardBean rb = new RewardBean().init(this.rb);
		rb.setParentId(Long.MIN_VALUE);
		helper.postRaw(rb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postRUnknownOfferId() {

		RewardBean rb = new RewardBean().init(this.rb);
		rb.setParentId(Long.MAX_VALUE);
		helper.postBean(rb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRNullRewardType() {

		RewardBean rb = new RewardBean().init(this.rb);
		rb.setRewardType(null);
		helper.postRaw(rb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRInvalidRewardMonetaryAmount() {

		RewardBean rb = new RewardBean().init(this.rb);
		rb.setRewardMonetaryAmount(Float.MIN_VALUE);
		helper.postRaw(rb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getRUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test
	public void putRUnknownId() {

		long rId = helper.postBean(this.rb).getId();
		RewardBean rb = helper.getBean(rId);
		rb.setId(Long.MAX_VALUE);
		try {
			helper.putBean(rb);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(rId);
		}
		
		fail("Expected ResourceConflictException not thrown in putRUnknownId()");
	}

	@Test
	public void putRNullRewardType() {

		long rId = helper.postBean(this.rb).getId();
		RewardBean rb = helper.getBean(rId);
		rb.setRewardType(null);
		try {
			helper.putRaw(rb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(rId);
		}
		
		fail("Expected ResourceConflictException not thrown in putRNullRewardType()");
	}
	
	@Test
	public void putRInvalidRewardMonetaryAmount() {

		long rId = helper.postBean(this.rb).getId();
		RewardBean rb = helper.getBean(rId);
		rb.setRewardMonetaryAmount(Float.MIN_VALUE);
		try {
			helper.putRaw(rb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(rId);
		}
		
		fail("Expected ResourceConflictException not thrown in putRInvalidRewardMonetaryAmount()");
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void deleteRUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}
