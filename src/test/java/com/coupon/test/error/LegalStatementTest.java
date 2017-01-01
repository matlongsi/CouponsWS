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

import com.coupon.common.LegalStatement;
import com.coupon.common.bean.LegalStatementBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.MarketingMaterialTest;
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

	@Test(expected=ResourceConflictException.class)
	public void postLSNullStatement() {

		LegalStatementBean lsb = new LegalStatementBean().init(this.lsb);
		lsb.setLegalStatement(null);
		helper.postRaw(lsb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postLSLongStatement() {

		LegalStatementBean lsb = new LegalStatementBean().init(this.lsb);
		lsb.setLegalStatement(
				String.format("%" + (LegalStatement.MAX_STATEMENT_LENGTH + 1) + "s", "*"));
		helper.postRaw(lsb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postLSNegativeArtworkId() {

		LegalStatementBean lsb = new LegalStatementBean().init(this.lsb);
		lsb.setParentId(Long.MIN_VALUE);
		helper.postRaw(lsb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postLSInvalidArtworkId() {

		LegalStatementBean lsb = new LegalStatementBean().init(this.lsb);
		lsb.setParentId(Long.MAX_VALUE);
		helper.postBean(lsb);
	}

	@Test
	public void putLSNullStatement() {

		long lsId = helper.postBean(this.lsb).getId();
		LegalStatementBean lsb = helper.getBean(lsId);
		lsb.setLegalStatement(null);
		try {
			helper.putRaw(lsb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(lsId);
		}
		
		fail("Expected ResourceConflictException not thrown in putLSNullStatement()");
	}
	
	@Test
	public void putLSLongStatement() {

		long lsId = helper.postBean(this.lsb).getId();
		LegalStatementBean lsb = helper.getBean(lsId);
		lsb.setLegalStatement(
				String.format("%" + (LegalStatement.MAX_STATEMENT_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(lsb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(lsId);
		}
		
		fail("Expected ResourceConflictException not thrown in putLSLongStatement()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getLSInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putLSInvalidId() {

		LegalStatementBean lsb = new LegalStatementBean().init(this.lsb);
		lsb.setId(Long.MAX_VALUE);
		helper.putBean(lsb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteLSInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}