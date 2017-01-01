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

import com.coupon.common.MarketingMaterial;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.OfferTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarketingMaterialTest {

	public static final String TEST_FILE_PATH = "./TestData/MM1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postMMInvalidOfferId() {

		MarketingMaterialBean mmb = new MarketingMaterialBean().init(this.mmb);
		mmb.setParentId(Long.MIN_VALUE);
		helper.postRaw(mmb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postMMUnknownOfferId() {

		MarketingMaterialBean mmb = new MarketingMaterialBean().init(this.mmb);
		mmb.setParentId(Long.MAX_VALUE);
		helper.postBean(mmb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postMMLongBrandName() {

		MarketingMaterialBean mmb = new MarketingMaterialBean().init(this.mmb);
		mmb.setBrandName(
				String.format("%" + (MarketingMaterial.MAX_BRAND_NAME_LENGTH + 1) + "s", "*"));
		helper.postRaw(mmb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getMMUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putMMInvalidId() {

		MarketingMaterialBean mmb = new MarketingMaterialBean().init(this.mmb);
		mmb.setId(Long.MIN_VALUE);
		helper.putRaw(mmb);
	}

	@Test
	public void putMMUnknownId() {

		long mmId = helper.postBean(this.mmb).getId();
		MarketingMaterialBean mmb = helper.getBean(mmId);
		mmb.setId(Long.MAX_VALUE);
		try {
			helper.putBean(mmb);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(mmId);
		}
		
		fail("Expected ResourceNotFoundException not thrown in putMMUnknownId()");
	}

	@Test
	public void putMMLongBrandName() {

		long mmId = helper.postBean(this.mmb).getId();
		MarketingMaterialBean mmb = helper.getBean(mmId);
		mmb.setBrandName(
				String.format("%" + (MarketingMaterial.MAX_BRAND_NAME_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(mmb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(mmId);
		}
		
		fail("Expected ResourceConflictException not thrown in putMMLongBrandName()");
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void deleteMMUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}