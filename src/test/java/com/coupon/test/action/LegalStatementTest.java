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

import com.coupon.common.bean.LegalStatementBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LegalStatementTest {

	public static final String TEST_FILE_PATH = "./TestData/LS1A.json";

	private static WebServiceHelper<LegalStatementBean> helper =
			new WebServiceHelper<LegalStatementBean>(
				LegalStatementBean.class,
				Resource.PATH_LEGAL_STATEMENTS);
	private static WebServiceHelper<MarketingMaterialBean> mmHelper =
			new WebServiceHelper<MarketingMaterialBean>(
				MarketingMaterialBean.class,
				Resource.PATH_MARKETING_MATERIALS);

	private MarketingMaterialTest mmTest;
	private static List<LegalStatementBean> lsbs;

	private LegalStatementBean lsb;
	public LegalStatementBean getLegalStatement() { return lsb; }
	
	@BeforeClass
	public static void setup() {
		
		MarketingMaterialTest.setup();
	}

	@AfterClass
	public static void cleanup() {
		
		MarketingMaterialTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		LegalStatementTest.lsbs = new JSONHelper<LegalStatementBean>(LegalStatementBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> mmbs = MarketingMaterialTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : mmbs) {
			for (Object lsb : LegalStatementTest.lsbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						MarketingMaterialBean.class.cast(arr[1]),
						lsb});
			}
		}

		return params;
	}

	public LegalStatementTest(OfferBean ob, MarketingMaterialBean mmb, LegalStatementBean lsb) {

		this.mmTest = new MarketingMaterialTest(ob, mmb);
		this.lsb = lsb;
	}

	@Before
	public void beforeTest() {

		this.mmTest.beforeTest();

		this.lsb.setParentId(
				mmHelper.postBean(mmTest.getMarketingMaterial()).getId());
	}

	@After
	public void afterTest() {

		mmHelper.deleteBean(this.lsb.getParentId());

		this.mmTest.afterTest();
	}

	@Test
	public void test1_postLegalStatement() {

		LegalStatementBean lsb = helper.postBean(this.lsb);
		assertTrue("New LegalStatement does not match import file in test1_postLegalStatement()",
				this.lsb.equals(lsb));
		
		helper.deleteBean(lsb.getId());
	}
	
	@Test
	public void test2_getLegalStatementById() {

		long lsId = helper.postBean(this.lsb).getId();

		LegalStatementBean lsb = helper.getBean(lsId);
		assertTrue("Retrieved LegalStatement does not match import file in test2_getLegalStatement()",
				this.lsb.equals(lsb));

		helper.deleteBean(lsId);
	}

	@Test
	public void test3_putLegalStatement() {
		
		long lsId = helper.postBean(this.lsb).getId();

		LegalStatementBean lsb = helper.getBean(lsId);
		lsb.setLegalStatement(lsb.getLegalStatement() + "UPDTD");
		LegalStatementBean abUpdt = helper.putBean(lsb);
		assertTrue("LegalStatement did not update in test4_putLegalStatement",
				lsb.equals(abUpdt));

		helper.deleteBean(lsId);
	}
	
	@Test
	public void test4_deleteLegalStatement() {

		long lsId = helper.postBean(this.lsb).getId();
		helper.getBean(lsId);
		helper.deleteBean(lsId);

		try {
			helper.getBean(lsId);
			fail("delete failed in test4_deleteLegalStatement()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}