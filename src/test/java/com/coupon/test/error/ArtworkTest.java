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

import com.coupon.common.Artwork;
import com.coupon.common.bean.ArtworkBean;
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
public class ArtworkTest {

	public static final String TEST_FILE_PATH = "./TestData/A1.json";

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

	@Test(expected=ResourceConflictException.class)
	public void postAInvalidMarketingMaterialId() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setParentId(Long.MIN_VALUE);
		helper.postRaw(ab);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postAUnknownMarketingMaterialId() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setParentId(Long.MAX_VALUE);
		helper.postBean(ab);
	}

	@Test(expected=ResourceConflictException.class)
	public void postANullArtworkType() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setArtworkType(null);
		helper.postRaw(ab);
	}
	
	
	@Test(expected=ResourceConflictException.class)
	public void postANullFileName() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setFileName(null);
		helper.postRaw(ab);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postALongFileName() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setFileName(
				String.format("%" + (Artwork.MAX_FILE_NAME_LENGTH + 1) + "s", "*"));
		helper.postRaw(ab);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postANullFileFormatName() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setFileFormatName(null);
		helper.postRaw(ab);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postALongFileFormatName() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setFileFormatName(
				String.format("%" + (Artwork.MAX_FILE_FORMAT_NAME_LENGTH + 1) + "s", "*"));
		helper.postRaw(ab);
	}

	@Test(expected=ResourceConflictException.class)
	public void postALongFileUri() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setFileUri(
				String.format("%" + (Artwork.MAX_FILE_URI_LENGTH + 1) + "s", "*"));
		helper.postRaw(ab);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void getAUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putAInvalidId() {

		ArtworkBean ab = new ArtworkBean().init(this.ab);
		ab.setId(Long.MIN_VALUE);
		helper.putRaw(ab);
	}

	@Test
	public void putAUnknownId() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setId(Long.MAX_VALUE);
		try {
			helper.putBean(ab);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putAUnknownId()");
	}

	@Test
	public void putANullType() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setArtworkType(null);
		try {
			helper.putRaw(ab);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putANullType()");
	}
	
	@Test
	public void putANullFileName() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setFileName(null);
		try {
			helper.postRaw(ab);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putANullFileName()");
	}
	
	@Test
	public void putALongFileName() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setFileName(
				String.format("%" + (Artwork.MAX_FILE_NAME_LENGTH + 1) + "s", "*"));
		try {
			helper.postRaw(ab);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putALongFileName()");
	}
	
	@Test
	public void putANullFileFormatName() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setFileFormatName(null);
		try {
			helper.putRaw(ab);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putANullFileFormatName()");
	}
	
	@Test
	public void putALongFileFormatName() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setFileFormatName(
				String.format("%" + (Artwork.MAX_FILE_FORMAT_NAME_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(ab);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putALongFileFormatName()");
	}
	
	@Test
	public void putALongFileUri() {

		long aId = helper.postBean(this.ab).getId();
		ArtworkBean ab = helper.getBean(aId);
		ab.setFileUri(
				String.format("%" + (Artwork.MAX_FILE_URI_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(ab);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(aId);
		}
		
		fail("Expected ResourceConflictException not thrown in putALongFileUri()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteAUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}