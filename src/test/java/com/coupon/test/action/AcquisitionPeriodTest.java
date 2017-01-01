package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
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
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AcquisitionPeriodTest {

	public static final String TEST_FILE_PATH = "./TestData/AP1A.json";

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

	@Test
	public void test1_postAcquisitionPeriod() {

		AcquisitionPeriodBean apb = helper.postBean(this.apb);
		assertTrue("New AcquisitionPeriod does not match import file in test1_postAcquisitionPeriod()",
				this.apb.equals(apb));
		
		helper.deleteBean(apb.getId());
	}

	@Test
	public void test2_getAcquisitionPeriodById() {

		long apId = helper.postBean(this.apb).getId();

		AcquisitionPeriodBean apb = helper.getBean(apId);
		assertTrue("AcquisitionPeriod is null in test2_getAcquisitionPeriod()",
				this.apb.equals(apb));
		
		helper.deleteBean(apId);
	}

	@Test
	public void test3_putAcquisitionPeriod() {

		AcquisitionPeriodBean apb = helper.postBean(this.apb);

		apb.getTimePeriod().getStartDateTime().setTime(
				apb.getTimePeriod().getStartDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		AcquisitionPeriodBean apbUpdt = helper.putBean(apb);
		assertTrue("startDateTime update failed in test3_putAcquisitionPeriod()",
				apb.equals(apbUpdt));

		helper.deleteBean(apb.getId());
	}
	
	@Test
	public void test4_deleteAcquisitionPeriod() {

		long apId = helper.postBean(this.apb).getId();
		helper.getBean(apId);
		helper.deleteBean(apId);

		try {
			helper.getBean(apId);
			fail("delete failed in test4_deleteAcquisitionPeriod()");
		}
		catch (ResourceNotFoundException ex) { }
	}
}