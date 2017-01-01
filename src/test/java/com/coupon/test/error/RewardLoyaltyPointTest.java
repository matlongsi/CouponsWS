package com.coupon.test.error;

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
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.RewardType;
import com.coupon.resource.Resource;
import com.coupon.test.action.RewardTest;
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

	@Test(expected=ResourceNotFoundException.class)
	public void deleteRLPInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getRLPInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRLPNullLoyaltyProgramName() {

		RewardLoyaltyPointBean rlpb = new RewardLoyaltyPointBean().init(this.rlpb);
		rlpb.setLoyaltyProgramName(null);
		helper.postRaw(rlpb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRLPLongLoyaltyProgramName() {

		RewardLoyaltyPointBean rlpb = new RewardLoyaltyPointBean().init(this.rlpb);
		rlpb.setLoyaltyProgramName(
				String.format("%" + (RewardLoyaltyPoint.MAX_LOYALTY_PROGRAM_NAME_LENGTH + 1) + "s", "*"));
		helper.postRaw(rlpb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postRLPNegativeRewardId() {

		RewardLoyaltyPointBean rlpb = new RewardLoyaltyPointBean().init(this.rlpb);
		rlpb.setParentId(Long.MIN_VALUE);
		helper.postRaw(rlpb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postRLPInvalidRewardId() {

		RewardLoyaltyPointBean rlpb = new RewardLoyaltyPointBean().init(this.rlpb);
		rlpb.setParentId(Long.MAX_VALUE);
		helper.postBean(rlpb);
	}

	@Test
	public void putRLPNullLoyaltyProgramName() {

		long rlpId = helper.postBean(this.rlpb).getId();
		RewardLoyaltyPointBean rlpb = helper.getBean(rlpId);
		rlpb.setLoyaltyProgramName(null);
		try {
			helper.putRaw(rlpb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(rlpId);
		}
		
		fail("Expected ResourceConflictException not thrown in putRLPNullLoyaltyProgramName()");
	}

	@Test
	public void putRLPLongLoyaltyProgramName() {

		long rlpId = helper.postBean(this.rlpb).getId();
		RewardLoyaltyPointBean rlpb = helper.getBean(rlpId);
		rlpb.setLoyaltyProgramName(
				String.format("%" + (RewardLoyaltyPoint.MAX_LOYALTY_PROGRAM_NAME_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(rlpb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(rlpId);
		}
		
		fail("Expected ResourceConflictException not thrown in putRLPLongLoyaltyProgramName()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putRLPInvalidId() {

		RewardLoyaltyPointBean rlpb = new RewardLoyaltyPointBean().init(this.rlpb);
		rlpb.setId(Long.MAX_VALUE);
		helper.putBean(rlpb);
	}

}