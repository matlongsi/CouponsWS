package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
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
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.TimePeriodHelper;
import com.coupon.common.bean.AwarderDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RedemptionPeriodTest {

	public static final String TEST_FILE_PATH = "./TestData/RP1A.json";

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

		rpb.setTimePeriod(TimePeriodHelper.toDefault(
				rpb.getTimePeriod(),
				TimeZone.getTimeZone(ob.getTimeZone())));
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

	@Test
	public void test1_postRedemptionPeriod() {

		RedemptionPeriodBean rpb = helper.postBean(this.rpb);
		assertTrue("New RedemptionPeriod does not match import file in test1_postRedemptionPeriod()",
				this.rpb.equals(rpb));

		helper.deleteBean(rpb.getId());
	}
	
	@Test
	public void test2_getRedemptionPeriodById() {

		long rpId = helper.postBean(this.rpb).getId();

		RedemptionPeriodBean rpb = helper.getBean(rpId);
		assertTrue("Retrieved RedemptionPeriod does not match import file in test2_getRedemptionPeriod()",
				this.rpb.equals(rpb));
		
		helper.deleteBean(rpId);
	}

	@Test
	public void test3_putRedemptionPeriod() {

		RedemptionPeriodBean rpb = helper.postBean(this.rpb);

		rpb.getTimePeriod().getStartDateTime().setTime(
				rpb.getTimePeriod().getStartDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		RedemptionPeriodBean rpUpdt = helper.putBean(rpb);
		assertTrue("startDateTime did not update successfully in test3_putRedemptionPeriod",
				rpb.equals(rpUpdt));

		helper.deleteBean(rpb.getId());
	}
	
	@Test
	public void test4_deleteRedemptionPeriod() {

		long rpId = helper.postBean(this.rpb).getId();
		helper.getBean(rpId);
		helper.deleteBean(rpId);

		try {
			helper.getBean(rpId);
			fail("delete failed in test4_deleteRedemptionPeriod()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}