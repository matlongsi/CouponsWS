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

import com.coupon.common.bean.ProductCategoryBean;
import com.coupon.common.ProductCategory;
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
public class ProductCategoryTest {

	public static final String TEST_FILE_PATH = "./TestData/PC1.json";

	private static WebServiceHelper<ProductCategoryBean> helper =
			new WebServiceHelper<ProductCategoryBean>(
				ProductCategoryBean.class,
				Resource.PATH_PRODUCT_CATEGORIES);
	private static WebServiceHelper<MarketingMaterialBean> mmHelper =
			new WebServiceHelper<MarketingMaterialBean>(
				MarketingMaterialBean.class,
				Resource.PATH_MARKETING_MATERIALS);

	private MarketingMaterialTest mmTest;
	private static List<ProductCategoryBean> pcbs;

	private ProductCategoryBean pcb;
	public ProductCategoryBean getProductCategory() { return pcb; }
	
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

		ProductCategoryTest.pcbs = new JSONHelper<ProductCategoryBean>(ProductCategoryBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> mmbs = MarketingMaterialTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : mmbs) {
			for (Object pcb : ProductCategoryTest.pcbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						MarketingMaterialBean.class.cast(arr[1]),
						pcb});
			}
		}

		return params;
	}

	public ProductCategoryTest(OfferBean ob, MarketingMaterialBean mmb, ProductCategoryBean pcb) {

		this.mmTest = new MarketingMaterialTest(ob, mmb);
		this.pcb = pcb;
	}

	@Before
	public void beforeTest() {

		this.mmTest.beforeTest();

		this.pcb.setParentId(
				mmHelper.postBean(mmTest.getMarketingMaterial()).getId());
	}

	@After
	public void afterTest() {

		mmHelper.deleteBean(this.pcb.getParentId());

		this.mmTest.afterTest();
	}

	@Test(expected=ResourceConflictException.class)
	public void postPCNullName() {

		ProductCategoryBean pcb = new ProductCategoryBean().init(this.pcb);
		pcb.setCategoryName(null);
		helper.postRaw(pcb);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postPCLongName() {

		ProductCategoryBean pcb = new ProductCategoryBean().init(this.pcb);
		pcb.setCategoryName(
				String.format("%" + (ProductCategory.MAX_NAME_LENGTH + 1) + "s", "*"));
		helper.postRaw(pcb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postPCNegativeMarketingMaterialId() {

		ProductCategoryBean pcb = new ProductCategoryBean().init(this.pcb);
		pcb.setParentId(Long.MIN_VALUE);
		helper.postRaw(pcb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postPCInvalidMarketingMaterialId() {

		ProductCategoryBean pcb = new ProductCategoryBean().init(this.pcb);
		pcb.setParentId(Long.MAX_VALUE);
		helper.postBean(pcb);
	}

	@Test
	public void putPCNullName() {

		long pcId = helper.postBean(this.pcb).getId();
		ProductCategoryBean pcb = helper.getBean(pcId);
		pcb.setCategoryName(null);
		try {
			helper.putRaw(pcb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(pcId);
		}
		
		fail("Expected ResourceConflictException not thrown in putPCNullName()");
	}
	
	@Test
	public void putPCLongName() {

		long pcId = helper.postBean(this.pcb).getId();
		ProductCategoryBean pcb = helper.getBean(pcId);
		pcb.setCategoryName(
				String.format("%" + (ProductCategory.MAX_NAME_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(pcb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(pcId);
		}
		
		fail("Expected ResourceConflictException not thrown in putPCLongName()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getPCInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putPCInvalidId() {

		ProductCategoryBean pcb = new ProductCategoryBean().init(this.pcb);
		pcb.setId(Long.MAX_VALUE);
		helper.putBean(pcb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deletePCInvalidId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}