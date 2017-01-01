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

import com.coupon.common.bean.AcquisitionPeriodBean;
import com.coupon.common.bean.DistributionDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.DataValidationException;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.DistributionDetailTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AcquisitionPeriodTest {

	public static final String TEST_FILE_PATH = "./TestData/AP1.json";

	private static WebServiceHelper<AcquisitionPeriodBean> helper =
			new WebServiceHelper<AcquisitionPeriodBean>(
				AcquisitionPeriodBean.class,
				Resource.PATH_ACQUISITION_PERIODS);
	private static WebServiceHelper<DistributionDetailBean> ddHelper =
			new WebServiceHelper<DistributionDetailBean>(
				DistributionDetailBean.class,
				Resource.PATH_DISTRIBUTION_DETAILS);

	private DistributionDetailTest ddTest;
	private static List<AcquisitionPeriodBean> apbs;

	private AcquisitionPeriodBean apb;
	public AcquisitionPeriodBean getAcquisitionPeriod() { return apb; }

	@BeforeClass
	public static void setup() {

		DistributionDetailTest.setup();
	}

	@AfterClass
	public static void cleanup() {

		DistributionDetailTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		AcquisitionPeriodTest.apbs = new JSONHelper<AcquisitionPeriodBean>(AcquisitionPeriodBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> ddbs = DistributionDetailTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : ddbs) {
			for (Object apb : AcquisitionPeriodTest.apbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						DistributionDetailBean.class.cast(arr[1]),
						apb});
			}
		}

		return params;
	}

	public AcquisitionPeriodTest(OfferBean ob, DistributionDetailBean ddb, AcquisitionPeriodBean apb) {

		this.ddTest = new DistributionDetailTest(ob, ddb);
		this.apb = apb;
	}

	@Before
	public void beforeTest() {

		this.ddTest.beforeTest();

		this.apb.setParentId(
				ddHelper.postBean(ddTest.getDistributionDetail()).getId());
	}

	@After
	public void afterTest() {

		ddHelper.deleteBean(this.apb.getParentId());

		this.ddTest.afterTest();
	}

	@Test(expected=ResourceConflictException.class)
	public void postAPInvalidDistributionDetailId() {

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		apb.setParentId(Long.MIN_VALUE);
		helper.postRaw(apb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postAPUnknownDistributionDetailId() {

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		apb.setParentId(Long.MAX_VALUE);
		helper.postBean(apb);
	}

	@Test(expected=DataValidationException.class)
	public void postAPEarlyStartDateTime() {

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		apb.getTimePeriod().getStartDateTime().setTime(
				ddTest.getOffer().getTimePeriod().getStartDateTime().getTime() - TimeUnit.DAYS.toMillis(1));
		helper.postBean(apb);
	}

	@Test(expected=DataValidationException.class)
	public void postAPEnclosedTimePeriod() {		

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		AcquisitionPeriodBean apb0 =
				ddTest.getDistributionDetail().getAcquisitionPeriods().get(0);
		apb.getTimePeriod().getStartDateTime().setTime(
				apb0.getTimePeriod().getStartDateTime().getTime() + TimeUnit.MINUTES.toMillis(1));
		apb.getTimePeriod().getEndDateTime().setTime(
				apb0.getTimePeriod().getEndDateTime().getTime() - TimeUnit.MINUTES.toMillis(1));
		helper.postBean(apb);
	}

	@Test(expected=DataValidationException.class)
	public void postAPLateEndDateTime() {

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		apb.getTimePeriod().getEndDateTime().setTime(
				ddTest.getOffer().getTimePeriod().getEndDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		helper.postBean(apb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getAPUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putAPInvalidId() {

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		apb.setId(Long.MIN_VALUE);
		helper.putRaw(apb);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void putAPUnknownId() {

		AcquisitionPeriodBean apb = new AcquisitionPeriodBean().init(this.apb);
		apb.setId(Long.MAX_VALUE);
		helper.putBean(apb);
	}
	
	@Test
	public void putAPInvalidEndDateTime() {

		long apId = helper.postBean(this.apb).getId();
		AcquisitionPeriodBean apb = helper.getBean(apId);
		apb.getTimePeriod().getEndDateTime().setTime(
				apb.getTimePeriod().getStartDateTime().getTime() - TimeUnit.DAYS.toMillis(1));
		try {
			helper.putBean(apb);
		}
		catch (DataValidationException ex) {
			return;
		}
		finally {
			helper.deleteBean(apId);
		}

		fail("Expected DataValidationException not thrown in putAPInvalidEndDateTime()");
	}

	@Test
	public void putAPInvalidStartDateTime() {

		long apId = helper.postBean(this.apb).getId();
		AcquisitionPeriodBean apb = helper.getBean(apId);

		apb.getTimePeriod().getStartDateTime().setTime(
				apb.getTimePeriod().getEndDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		try {
			helper.putBean(apb);
		}
		catch (DataValidationException ex) {
			return;
		}
		finally {
			helper.deleteBean(apId);
		}
		
		fail("Expected DataValidationException not thrown in putAPInvalidStartDateTime()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteAPUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}