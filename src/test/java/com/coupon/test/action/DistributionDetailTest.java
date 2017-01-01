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

import com.coupon.common.bean.DistributionDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DistributionDetailTest {

	public static final String TEST_FILE_PATH = "./TestData/DD1A.json";

	private static WebServiceHelper<DistributionDetailBean> helper =
			new WebServiceHelper<DistributionDetailBean>(
				DistributionDetailBean.class,
				Resource.PATH_DISTRIBUTION_DETAILS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);

	private OfferTest oTest;
	private static List<DistributionDetailBean> ddbs;

	private DistributionDetailBean ddb;
	public DistributionDetailBean getDistributionDetail() { return ddb; }
	public OfferBean getOffer() { return oTest.getOffer(); }

	@BeforeClass
	public static void setup() {

		OfferTest.setup();
	}

	@AfterClass
	public static void cleanup() {

		OfferTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		DistributionDetailTest.ddbs = new JSONHelper<DistributionDetailBean>(DistributionDetailBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object rb : DistributionDetailTest.ddbs) {
				params.add(new Object[] {ob, rb});
			}
		}

		return params;
	}

	public static Collection<Object[]> load(String oFilePath, String rFilePath) throws JAXBException {

		DistributionDetailTest.ddbs = new JSONHelper<DistributionDetailBean>(DistributionDetailBean.class)
				.arrayFromJson(rFilePath);

		Collection<? extends Object> obs = OfferTest.load(oFilePath);

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object ddb : DistributionDetailTest.ddbs) {
				params.add(new Object[] {ob, ddb});
			}
		}

		return params;
	}

	public DistributionDetailTest(OfferBean ob, DistributionDetailBean ddb) {

		this.oTest = new OfferTest(ob);
		this.ddb = ddb;
	}

	@Before
	public void beforeTest() {
		
		this.ddb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.ddb.getParentId());
	}

	@Test
	public void test1_postDistributionDetail() {

		DistributionDetailBean ddb = helper.postBean(this.ddb);
		assertTrue("New DistributionDetail does not match import file in test1_postDistributionDetail()",
				this.ddb.equals(ddb));

		helper.deleteBean(ddb.getId());
	}
	
	@Test
	public void test2_getDistributionDetailById() {

		long ddId = helper.postBean(this.ddb).getId();

		DistributionDetailBean ddb = helper.getBean(ddId);
		assertTrue("Retrieved DistributionDetail does not match import file in test2_getDistributionDetailById()",
				this.ddb.equals(ddb));

		helper.deleteBean(ddId);
	}

/*	@Test
	public void test3_getDistributionDetailByCouponNumber() {

		long ddId = helper.postBean(this.ddb).getId();

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("companyPrefix",
				String.format("%d", oTest.getOffer().getOfferNumber().getCompanyPrefix()));
		paramsMap.put("couponReference",
				String.format("%d", oTest.getOffer().getOfferNumber().getCouponReference()));
		paramsMap.put("serialComponent",
				String.format("%d", oTest.getOffer().getOfferNumber().getSerialComponent()));
		DistributionDetailBean ddb = helper.lookupBean(paramsMap);
		assertTrue("Retrieved DistributionDetail does not match import file in test3_getDistributionDetailByCouponNumber()",
				this.ddb.equals(ddb));

		helper.deleteBean(ddId);
	}
*/

	@Test
	public void test4_putDistributionDetail() {
		
		DistributionDetailBean ddb = helper.postBean(this.ddb);
		ddb.setMaximumOfferAcquisition(ddb.getMaximumOfferAcquisition() + 1);
		DistributionDetailBean ddbUpdt = helper.putBean(ddb);
		assertTrue("maximumOfferAcquisition update failed in test4_putDistributionDetail()",
				ddb.equals(ddbUpdt));

		ddb = helper.getBean(ddb.getId());
		ddb.getPublicationPeriod().getTimePeriod().getEndDateTime().setTime(
				ddb.getPublicationPeriod().getTimePeriod().getEndDateTime().getTime() + TimeUnit.DAYS.toMillis(1));
		ddbUpdt = helper.putBean(ddb);
		assertTrue("timePeriod update failed in test3_putDistributionDetail()",
				ddb.equals(ddbUpdt));
	}

	@Test
	public void test5_deleteDistributionDetail() {

		long ddId = helper.postBean(this.ddb).getId();
		helper.getBean(ddId);
		helper.deleteBean(ddId);

		try {
			helper.getBean(ddId);
			fail("delete failed in test5_deleteDistributionDetail()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}
