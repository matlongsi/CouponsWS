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

import com.coupon.common.FinancialSettlementDetail;
import com.coupon.common.bean.FinancialSettlementDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.OfferTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FinancialSettlementDetailTest {

	public static final String TEST_FILE_PATH = "./TestData/FSD1.json";

	private static WebServiceHelper<FinancialSettlementDetailBean> helper =
			new WebServiceHelper<FinancialSettlementDetailBean>(
				FinancialSettlementDetailBean.class,
				Resource.PATH_FINANCIAL_SETTLEMENT_DETAILS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);

	private OfferTest oTest;
	private static List<FinancialSettlementDetailBean> fsdbs;

	private FinancialSettlementDetailBean fsdb;
	public FinancialSettlementDetailBean getFinancialSettlementDetail() { return fsdb; }

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

		FinancialSettlementDetailTest.fsdbs = new JSONHelper<FinancialSettlementDetailBean>(FinancialSettlementDetailBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object rb : FinancialSettlementDetailTest.fsdbs) {
				params.add(new Object[] {ob, rb});
			}
		}

		return params;
	}

	public FinancialSettlementDetailTest(OfferBean ob, FinancialSettlementDetailBean fsdb) {

		this.oTest = new OfferTest(ob);
		this.fsdb = fsdb;
	}

	@Before
	public void beforeTest() {
		
		this.fsdb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.fsdb.getParentId());
	}

	@Test(expected=ResourceConflictException.class)
	public void postFSDInvalidOfferId() {

		FinancialSettlementDetailBean fsdb = new FinancialSettlementDetailBean().init(this.fsdb);
		fsdb.setParentId(Long.MIN_VALUE);
		helper.postRaw(fsdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postFSDUnknownOfferId() {

		FinancialSettlementDetailBean fsdb = new FinancialSettlementDetailBean().init(this.fsdb);
		fsdb.setParentId(Long.MAX_VALUE);
		helper.postBean(fsdb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postFSDLongOfferClearingInstruction() {

		FinancialSettlementDetailBean fsdb = new FinancialSettlementDetailBean().init(this.fsdb);
		fsdb.setOfferClearingInstruction(
				String.format("%" + (FinancialSettlementDetail.MAX_CLEARING_INSTRUCTION_LENGTH + 1) + "s", "*"));
		helper.postRaw(fsdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getFSDUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putFSDInvalidId() {

		FinancialSettlementDetailBean fsdb = new FinancialSettlementDetailBean().init(this.fsdb);
		fsdb.setId(Long.MIN_VALUE);
		helper.putRaw(fsdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putFSDUnknownId() {

		FinancialSettlementDetailBean fsdb = new FinancialSettlementDetailBean().init(this.fsdb);
		fsdb.setId(Long.MAX_VALUE);
		helper.putRaw(fsdb);
	}

	@Test
	public void putFSDLongOfferClearingInstruction() {

		long fsdId = helper.postBean(this.fsdb).getId();
		FinancialSettlementDetailBean fsdb = helper.getBean(fsdId);
		fsdb.setOfferClearingInstruction(
				String.format("%" + (FinancialSettlementDetail.MAX_CLEARING_INSTRUCTION_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(fsdb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(fsdId);
		}
		
		fail("Expected ResourceConflictException not thrown in putUCLowCumulativeUse()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteFSDUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}