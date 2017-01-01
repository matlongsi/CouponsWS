package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.UsageConditionBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsageConditionTest {

	public static final String TEST_FILE_PATH = "./TestData/UC1A.json";

	private static WebServiceHelper<UsageConditionBean> helper =
			new WebServiceHelper<UsageConditionBean>(
				UsageConditionBean.class,
				Resource.PATH_USAGE_CONDITIONS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);

	private OfferTest oTest;
	private static List<UsageConditionBean> ucbs;

	private UsageConditionBean ucb;
	public UsageConditionBean getUsageCondition() { return ucb; }

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

		UsageConditionTest.ucbs = new JSONHelper<UsageConditionBean>(UsageConditionBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object ucb : UsageConditionTest.ucbs) {
				params.add(new Object[] {ob, ucb});
			}
		}

		return params;
	}

	public UsageConditionTest(OfferBean ob, UsageConditionBean ucb) {

		this.oTest = new OfferTest(ob);
		this.ucb = ucb;
	}

	@Before
	public void beforeTest() {
		
		this.ucb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.ucb.getParentId());
	}

	@Test
	public void test1_postUsageCondition() {

		UsageConditionBean ucb = helper.postBean(this.ucb);
		assertTrue("New UsageCondition does not match import file in test1_postUsageCondition()",
				this.ucb.equals(ucb));

		helper.deleteBean(ucb.getId());
	}
	
	@Test
	public void test2_getUsageConditionById() {

		long ucId = helper.postBean(this.ucb).getId();

		UsageConditionBean ucb = helper.getBean(ucId);
		assertTrue("Retrieved UsageCondition does not match import file in test2_getUsageConditionById()",
				this.ucb.equals(ucb));

		helper.deleteBean(ucId);
	}

	@Test
	public void test3_putUsageCondition() {

		UsageConditionBean ucb = helper.postBean(this.ucb);

		ucb.setMaximumCumulativeUse((short)(ucb.getMaximumCumulativeUse() + 1));
		UsageConditionBean ucbUpdt = helper.putBean(ucb);
		assertTrue("maximumCumulativeUse update failed in test3_putUsageCondition()",
				ucb.equals(ucbUpdt));

		ucb = helper.getBean(ucb.getId());
		ucb.setMaximumUsePerTransaction((short)(ucb.getMaximumUsePerTransaction() + 1));
		ucbUpdt = helper.putBean(ucb);
		assertTrue("maximumUsePerTransaction update failed in test3_putUsageCondition()",
				ucb.equals(ucbUpdt));

		helper.deleteBean(ucb.getId());
	}

	@Test
	public void test4_deleteUsageCondition() {

		long ucId = helper.postBean(this.ucb).getId();
		helper.getBean(ucId);
		helper.deleteBean(ucId);

		try {
			helper.getBean(ucId);
			fail("delete failed in test4_deleteUsageCondition()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}