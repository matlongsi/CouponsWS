package com.coupon.test.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import com.coupon.common.RewardLoyaltyPoint;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.RewardBean;
import com.coupon.common.bean.RewardLoyaltyPointBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.RewardType;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RewardLoyaltyPointTest {

	public static final String TEST_FILE_PATH = "./TestData/RLP1A.json";

	private static WebServiceHelper<RewardLoyaltyPointBean> helper =
			new WebServiceHelper<RewardLoyaltyPointBean>(
				RewardLoyaltyPointBean.class,
				Resource.PATH_REWARD_LOYALTY_POINTS);
	private static WebServiceHelper<RewardBean> rHelper =
			new WebServiceHelper<RewardBean>(
				RewardBean.class,
				Resource.PATH_REWARDS);

	private static List<RewardLoyaltyPointBean> rlpbs;
	private RewardTest rTest;

	private RewardLoyaltyPointBean rlpb;
	public RewardLoyaltyPointBean getRewardLoyaltyPoint() { return rlpb; }

	@BeforeClass
	public static void setup() {

		RewardTest.setup();
	}

	@AfterClass
	public static void cleanup() {

		RewardTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		RewardLoyaltyPointTest.rlpbs = new JSONHelper<RewardLoyaltyPointBean>(RewardLoyaltyPointBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> rbs = RewardTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : rbs) {
			for (Object rlpb : RewardLoyaltyPointTest.rlpbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						RewardBean.class.cast(arr[1]),
						rlpb});
			}
		}

		return params;
	}

	public RewardLoyaltyPointTest(OfferBean ob, RewardBean rb, RewardLoyaltyPointBean rlpb) {

		this.rTest = new RewardTest(ob, rb);
		this.rlpb = rlpb;
	}

	@Before
	public void beforeTest() {

		this.rTest.beforeTest();

		this.rlpb.setParentId(
				rHelper.postBean(this.rTest.getReward()).getId());

		Assume.assumeTrue(this.rTest.getReward().getRewardType() ==
				RewardType.LOYALTY_POINTS_REWARD);
	}

	@After
	public void afterTest() {

		rHelper.deleteBean(this.rlpb.getParentId());

		this.rTest.afterTest();
	}

	@Test
	public void test1_postRewardLoyaltyPoint() {

		RewardLoyaltyPointBean rlpb = helper.postBean(this.rlpb);
		assertTrue("New RewardLoyaltyPoint does not match import file in test1_postRewardLoyaltyPoint()",
				this.rlpb.equals(rlpb));

		helper.deleteBean(rlpb.getId());
	}
	
	@Test
	public void test2_getRewardLoyaltyPoint() {

		long rlpId = helper.postBean(this.rlpb).getId();

		RewardLoyaltyPointBean rlpb = helper.getBean(rlpId);
		assertTrue("Retrieved RewardLoyaltyPoint does not match import file in test2_getRewardLoyaltyPoint()",
				this.rlpb.equals(rlpb));

		helper.deleteBean(rlpb.getId());
	}

	@Test
	public void test3_putRewardLoyaltyPoint() {

		RewardLoyaltyPointBean rlpb = helper.postBean(this.rlpb);
		rlpb.setLoyaltyPointsQuantity(rlpb.getLoyaltyPointsQuantity() + 1);
		RewardLoyaltyPoint rlpbUpdt = helper.putBean(rlpb);
		assertTrue("loyaltyPointsQuantity update failed in test3_putRewardLoyaltyPoints()",
				rlpb.equals(rlpbUpdt));

		rlpb = helper.getBean(rlpb.getId());
		rlpb.setLoyaltyProgramName(rlpb.getLoyaltyProgramName() + " UPDTD");
		rlpbUpdt = helper.putBean(rlpb);
		assertTrue("loyaltyoyaltyProgramName update failed in test3_putRewardLoyaltyPoint()",
				rlpb.equals(rlpbUpdt));

		helper.deleteBean(rlpb.getId());
	}

	@Test
	public void test4_deleteRewardLoyaltyPoint() {

		long rlpId = helper.postBean(this.rlpb).getId();
		helper.getBean(rlpId);
		helper.deleteBean(rlpId);

		try {
			helper.getBean(rlpId);
			fail("delete failed in test4_deleteRewardLoyaltyPoint()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}