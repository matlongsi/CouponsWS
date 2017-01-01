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

import com.coupon.common.bean.ArtworkBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.type.OfferArtworkType;
import com.coupon.resource.Resource;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArtworkTest {

	public static final String TEST_FILE_PATH = "./TestData/A1A.json";

	private static WebServiceHelper<ArtworkBean> helper =
			new WebServiceHelper<ArtworkBean>(
				ArtworkBean.class,
				Resource.PATH_ARTWORKS);
	private static WebServiceHelper<MarketingMaterialBean> mmHelper =
			new WebServiceHelper<MarketingMaterialBean>(
				MarketingMaterialBean.class,
				Resource.PATH_MARKETING_MATERIALS);

	private MarketingMaterialTest mmTest;
	private static List<ArtworkBean> abs;

	private ArtworkBean ab;
	public ArtworkBean getArtwork() { return ab; }
	
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

		ArtworkTest.abs = new JSONHelper<ArtworkBean>(ArtworkBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> mmbs = MarketingMaterialTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : mmbs) {
			for (Object ab : ArtworkTest.abs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						MarketingMaterialBean.class.cast(arr[1]),
						ab});
			}
		}

		return params;
	}

	public ArtworkTest(OfferBean ob, MarketingMaterialBean mmb, ArtworkBean ab) {

		this.mmTest = new MarketingMaterialTest(ob, mmb);
		this.ab = ab;
	}

	@Before
	public void beforeTest() {

		this.mmTest.beforeTest();

		this.ab.setParentId(
				mmHelper.postBean(mmTest.getMarketingMaterial()).getId());
	}

	@After
	public void afterTest() {

		mmHelper.deleteBean(this.ab.getParentId());

		this.mmTest.afterTest();
	}

	@Test
	public void test1_postArtwork() {

		ArtworkBean ab = helper.postBean(this.ab);
		assertTrue("New Artwork does not match import file in test1_postArtwork()",
				this.ab.equals(ab));
		
		helper.deleteBean(ab.getId());
	}
	
	@Test
	public void test2_getArtworkById() {

		long aId = helper.postBean(this.ab).getId();

		ArtworkBean ab = helper.getBean(aId);
		assertTrue("Retrieved Artwork does not match import file in test2_getArtwork()",
				this.ab.equals(ab));

		helper.deleteBean(aId);
	}

	@Test
	public void test3_putArtwork() {
		
		long aId = helper.postBean(this.ab).getId();

		ArtworkBean ab = helper.getBean(aId);
		ab.setFileName(ab.getFileName() + "UPDTD");
		ArtworkBean abUpdt = helper.putBean(ab);
		assertTrue("fileName did not update in test4_putArtwork",
				ab.equals(abUpdt));

		ab = helper.getBean(aId);
		ab.setFileFormatName(ab.getFileFormatName() + "UPDTD");
		abUpdt = helper.putBean(ab);
		assertTrue("fileFormatName did not update in test4_putArtwork",
				ab.equals(abUpdt));

		ab = helper.getBean(aId);
		ab.setFileUri(ab.getFileUri() + "UPDTD");
		abUpdt = helper.putBean(ab);
		assertTrue("fileUri did not update in test4_putArtwork",
				ab.equals(abUpdt));

		ab = helper.getBean(aId);
		ab.setArtworkType(OfferArtworkType.OFFER_VIDEO);
		abUpdt = helper.putBean(ab);
		assertTrue("artworkType did not update in test4_putArtwork",
				ab.equals(abUpdt));

		helper.deleteBean(aId);
	}
	
	@Test
	public void test4_deleteArtwork() {

		long aId = helper.postBean(this.ab).getId();
		helper.getBean(aId);
		helper.deleteBean(aId);

		try {
			helper.getBean(aId);
			fail("delete failed in test4_deleteArtwork()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}