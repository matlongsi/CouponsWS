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

import com.coupon.common.bean.ProductCategoryBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductCategoryTest {

	public static final String TEST_FILE_PATH = "./TestData/PC1A.json";

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

	@Test
	public void test1_postProductCategory() {

		ProductCategoryBean pcb = helper.postBean(this.pcb);
		assertTrue("New ProductCategory does not match import file in test1_postProductCategory()",
				this.pcb.equals(pcb));
		
		helper.deleteBean(pcb.getId());
	}
	
	@Test
	public void test2_getProductCategoryById() {

		long pcId = helper.postBean(this.pcb).getId();

		ProductCategoryBean pcb = helper.getBean(pcId);
		assertTrue("Retrieved ProductCategory does not match import file in test2_getProductCategory()",
				this.pcb.equals(pcb));

		helper.deleteBean(pcId);
	}

	@Test
	public void test3_putProductCategory() {
		
		long pcId = helper.postBean(this.pcb).getId();

		ProductCategoryBean pcb = helper.getBean(pcId);
		pcb.setCategoryName(pcb.getCategoryName() + "UPDTD");
		ProductCategoryBean abUpdt = helper.putBean(pcb);
		assertTrue("ProductCategory did not update in test4_putProductCategory",
				pcb.equals(abUpdt));

		helper.deleteBean(pcId);
	}
	
	@Test
	public void test4_deleteProductCategory() {

		long pcId = helper.postBean(this.pcb).getId();
		helper.getBean(pcId);
		helper.deleteBean(pcId);

		try {
			helper.getBean(pcId);
			fail("delete failed in test4_deleteProductCategory()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}