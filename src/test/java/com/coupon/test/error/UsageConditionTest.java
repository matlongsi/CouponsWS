package com.coupon.test.error;

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
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.OfferTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsageConditionTest {

	public static final String TEST_FILE_PATH = "./TestData/UC1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postUCInvalidOfferId() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setParentId(Long.MIN_VALUE);
		helper.postRaw(ucb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postUCUnknownOfferId() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setParentId(Long.MAX_VALUE);
		helper.postBean(ucb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postUCHighCumulativeUse() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setMaximumCumulativeUse(Short.MAX_VALUE);
		helper.postRaw(ucb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postUCLowCumulativeUse() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setMaximumCumulativeUse(Short.MIN_VALUE);
		helper.postRaw(ucb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postUCHighUsePerTransaction() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setMaximumUsePerTransaction(Short.MAX_VALUE);
		helper.postRaw(ucb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postUCLowUsePerTransaction() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setMaximumUsePerTransaction(Short.MIN_VALUE);
		helper.postRaw(ucb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getUCUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putUCUnknownId() {

		UsageConditionBean ucb = new UsageConditionBean().init(this.ucb);
		ucb.setId(Long.MAX_VALUE);
		helper.putBean(ucb);
	}

	@Test
	public void putUCHighCumulativeUse() {

		long ucId = helper.postBean(this.ucb).getId();
		UsageConditionBean ucb = helper.getBean(ucId);
		ucb.setMaximumCumulativeUse(Short.MAX_VALUE);
		try {
			helper.putRaw(ucb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ucId);
		}
		
		fail("Expected ResourceConflictException not thrown in putUCHighCumulativeUse()");
	}
	
	@Test
	public void putUCLowCumulativeUse() {

		long ucId = helper.postBean(this.ucb).getId();
		UsageConditionBean ucb = helper.getBean(ucId);
		ucb.setMaximumCumulativeUse(Short.MIN_VALUE);
		try {
			helper.putRaw(ucb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ucId);
		}
		
		fail("Expected ResourceConflictException not thrown in putUCLowCumulativeUse()");
	}
	
	@Test
	public void putUCHighUsePerTransaction() {

		long ucId = helper.postBean(this.ucb).getId();
		UsageConditionBean ucb = helper.getBean(ucId);
		ucb.setMaximumUsePerTransaction(Short.MAX_VALUE);
		try {
			helper.putRaw(ucb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ucId);
		}
		
		fail("Expected ResourceConflictException not thrown in putUCHighUsePerTransaction()");
	}

	@Test
	public void putUCLowUsePerTransaction() {

		long ucId = helper.postBean(this.ucb).getId();
		UsageConditionBean ucb = helper.getBean(ucId);
		ucb.setMaximumUsePerTransaction(Short.MIN_VALUE);
		try {
			helper.putRaw(ucb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ucId);
		}
		
		fail("Expected ResourceConflictException not thrown in putUCLowUsePerTransaction()");
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void deleteUCUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}