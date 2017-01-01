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

import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarketingMaterialTest {

	public static final String TEST_FILE_PATH = "./TestData/MM1A.json";

	private static WebServiceHelper<MarketingMaterialBean> helper =
			new WebServiceHelper<MarketingMaterialBean>(
				MarketingMaterialBean.class,
				Resource.PATH_MARKETING_MATERIALS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);

	private OfferTest oTest;
	private static List<MarketingMaterialBean> mmbs;

	private MarketingMaterialBean mmb;
	public MarketingMaterialBean getMarketingMaterial() { return mmb; }

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

		MarketingMaterialTest.mmbs = new JSONHelper<MarketingMaterialBean>(MarketingMaterialBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object mmb : MarketingMaterialTest.mmbs) {
				params.add(new Object[] {ob, mmb});
			}
		}

		return params;
	}

	public static Collection<Object[]> load(String oFilePath, String mmFilePath) throws JAXBException {

		MarketingMaterialTest.mmbs = new JSONHelper<MarketingMaterialBean>(MarketingMaterialBean.class)
				.arrayFromJson(mmFilePath);

		Collection<? extends Object> obs = OfferTest.load(oFilePath);

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object mmb : MarketingMaterialTest.mmbs) {
				params.add(new Object[] {ob, mmb});
			}
		}

		return params;
	}

	public MarketingMaterialTest(OfferBean ob, MarketingMaterialBean mmb) {

		this.oTest = new OfferTest(ob);
		this.mmb = mmb;
	}

	@Before
	public void beforeTest() {
		
		this.mmb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.mmb.getParentId());
	}

	@Test
	public void test1_postMarketingMaterial() {

		MarketingMaterialBean mmb = helper.postBean(this.mmb);
		assertTrue("New MarketingMaterial does not match import file test1_postMarketingMaterial()",
				this.mmb.equals(mmb));

		helper.deleteBean(mmb.getId());
	}
	
	@Test
	public void test2_getMarketingMaterialById() {

		long mmId = helper.postBean(this.mmb).getId();

		MarketingMaterialBean mmb = helper.getBean(mmId);
		assertTrue("Retrieved MarketingMaterial does not match import file in test2_getMarketingMaterialById()",
				this.mmb.equals(mmb));

		helper.deleteBean(mmId);
	}

	@Test
	public void test3_putMarketingMaterial() {

		MarketingMaterialBean mmb = helper.postBean(this.mmb);

		mmb.setBrandName((mmb.getBrandName() + " UPDTD"));
		MarketingMaterialBean mmbUpdt = helper.putBean(mmb);
		assertTrue("brandName update failed in test3_putMarketingMaterial()",
				mmb.equals(mmbUpdt));

		helper.deleteBean(mmb.getId());
	}

	@Test
	public void test4_deleteMarketingMaterial() {

		long mmId = helper.postBean(this.mmb).getId();
		helper.getBean(mmId);
		helper.deleteBean(mmId);

		try {
			helper.getBean(mmId);
			fail("delete failed in test4_deleteMarketingMaterial()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}