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

import com.coupon.common.bean.FinancialSettlementDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FinancialSettlementDetailTest {

	public static final String TEST_FILE_PATH = "./TestData/FSD1A.json";

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

	@Test
	public void test1_postFinancialSettlementDetail() {
		
		FinancialSettlementDetailBean fsdb = helper.postBean(this.fsdb);
		assertTrue("New FinancialSettlementDetail does not match import file in test1_postFinancialSettlementDetail()",
				this.fsdb.equals(fsdb));

		helper.deleteBean(fsdb.getId());
	}

	@Test
	public void test2_getFinancialSettlementDetailById() {

		long fsdId = helper.postBean(this.fsdb).getId();

		FinancialSettlementDetailBean fsdb = helper.getBean(fsdId);
		assertTrue("Retrieved FinancialSettlementDetail does not match import file in test2_getFinancialSettlementDetailById",
				this.fsdb.equals(fsdb));

		helper.deleteBean(fsdId);
	}

	@Test
	public void test3_putFinancialSettlementDetail() {

		FinancialSettlementDetailBean fsdb = helper.postBean(this.fsdb);
		fsdb.setOfferClearingInstruction(fsdb.getOfferClearingInstruction() + " UPDTD");
		FinancialSettlementDetailBean fsdbUpdt = helper.putBean(fsdb);
		assertTrue("offerClearingInstruction update failed in test3_putFinancialSettlementDetail()",
				fsdb.equals(fsdbUpdt));

		helper.deleteBean(fsdb.getId());
	}

	@Test
	public void test4_deleteFinancialSettlementDetail() {

		long fsdId = helper.postBean(this.fsdb).getId();
		helper.getBean(fsdId);
		helper.deleteBean(fsdId);

		try {
			helper.getBean(fsdId);
			fail("delete failed in test4_deleteFinancialSettlementDetail()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}