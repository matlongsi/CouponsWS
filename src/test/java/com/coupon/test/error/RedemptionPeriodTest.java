package com.coupon.test.error;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import com.coupon.common.bean.RedemptionPeriodBean;
import com.coupon.common.exception.DataValidationException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.bean.AwarderDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.resource.Resource;
import com.coupon.test.action.AwarderDetailTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RedemptionPeriodTest {

	public static final String TEST_FILE_PATH = "./TestData/RP1.json";

	private static WebServiceHelper<RedemptionPeriodBean> helper =
			new WebServiceHelper<RedemptionPeriodBean>(
				RedemptionPeriodBean.class,
				Resource.PATH_REDEMPTION_PERIODS);
	private static WebServiceHelper<AwarderDetailBean> adHelper =
			new WebServiceHelper<AwarderDetailBean>(
				AwarderDetailBean.class,
				Resource.PATH_AWARDER_DETAILS);

	private AwarderDetailTest adTest;
	private static List<RedemptionPeriodBean> rpbs;

	private RedemptionPeriodBean rpb;
	public RedemptionPeriodBean getRedemptionPeriod() { return rpb; }

	@BeforeClass
	public static void setup() {

		AwarderDetailTest.setup();
	}

	@AfterClass
	public static void cleanup() {

		AwarderDetailTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		RedemptionPeriodTest.rpbs = new JSONHelper<RedemptionPeriodBean>(RedemptionPeriodBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> adbs = AwarderDetailTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : adbs) {
			for (Object rpb : RedemptionPeriodTest.rpbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						AwarderDetailBean.class.cast(arr[1]),
						rpb});
			}
		}

		return params;
	}

	public RedemptionPeriodTest(OfferBean ob, AwarderDetailBean adb, RedemptionPeriodBean rpb) {

		this.adTest = new AwarderDetailTest(ob, adb);
		this.rpb = rpb;
	}

	@Before
	public void beforeTest() {

		this.adTest.beforeTest();

		this.rpb.setParentId(
				adHelper.postBean(adTest.getAwarderDetail()).getId());
	}

	@After
	public void afterTest() {

		adHelper.deleteBean(this.rpb.getParentId());

		this.adTest.afterTest();
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getRPInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test
	public void postRPEarlyStartDateTime() {

		RedemptionPeriodBean rpb = new RedemptionPeriodBean().init(this.rpb);
		rpb.getTimePeriod().getStartDateTime().setTime(
				adTest.getOffer().getTimePeriod().getStartDateTime().getTime() - TimeUnit.DAYS.toMillis(1));
		try {
			helper.postBean(rpb);
		}
		catch (DataValidationException ex) {
			return;
		}
		
		fail("Expected DataValidationException not thrown in postRPEarlyStartDateTime()");
	}

	@Test
	public void postRPEnclosedTimePeriod() {		

		RedemptionPeriodBean rpb = new RedemptionPeriodBean().init(this.rpb);
		RedemptionPeriodBean rpb0 =
				adTest.getAwarderDetail().getRedemptionPeriods().get(0);
		rpb.getTimePeriod().getStartDateTime().setTime(
				rpb0.getTimePeriod().getStartDateTime().getTime() + TimeUnit.MINUTES.toMillis(1));
		rpb.getTimePeriod().getEndDateTime().setTime(
				rpb0.getTimePeriod().getEndDateTime().getTime() - TimeUnit.MINUTES.toMillis(1));
		try {
			helper.postBean(rpb);
		}
		catch (DataValidationException ex) {
			return;
		}
		
		fail("Expected DataValidationException not thrown in postRPEnclosedTimePeriod()");
	}

	@Test
	public void postRPLateEndDateTime() {

		RedemptionPeriodBean rpb = new RedemptionPeriodBean().init(this.rpb);
		rpb.getTimePeriod().getEndDateTime().setTime(
				adTest.getOffer().getTimePeriod().getEndDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		try {
			helper.postBean(rpb);
		}
		catch (DataValidationException ex) {
			return;
		}
		
		fail("Expected DataValidationException not thrown in postRPLateEndDateTime()");
	}

	@Test
	public void putRPInvalidEndDateTime() {

		long rpId = helper.postBean(this.rpb).getId();
		RedemptionPeriodBean rpb = helper.getBean(rpId);

		rpb.getTimePeriod().getEndDateTime().setTime(
				rpb.getTimePeriod().getStartDateTime().getTime() - TimeUnit.DAYS.toMillis(1));
		try {
			helper.putBean(rpb);
		}
		catch (DataValidationException ex) {
			return;
		}
		finally {
			helper.deleteBean(rpId);
		}

		fail("Expected DataValidationException not thrown in putRPInvalidEndDateTime()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putRPInvalidId() {

		RedemptionPeriodBean rpb = new RedemptionPeriodBean().init(this.rpb);
		rpb.setId(Long.MAX_VALUE);
		helper.putBean(rpb);
	}
	
	@Test
	public void putRPInvalidStartDateTime() {

		long rpId = helper.postBean(this.rpb).getId();
		RedemptionPeriodBean rpb = helper.getBean(rpId);

		rpb.getTimePeriod().getStartDateTime().setTime(
				rpb.getTimePeriod().getEndDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		try {
			helper.putBean(rpb);
		}
		catch (DataValidationException ex) {
			return;
		}
		finally {
			helper.deleteBean(rpId);
		}
		
		fail("Expected DataValidationException not thrown in putRPInvalidStartDateTime()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteInvalidRP() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}