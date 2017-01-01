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

import com.coupon.common.FileContentDescription;
import com.coupon.common.bean.ArtworkBean;
import com.coupon.common.bean.FileContentDescriptionBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.ArtworkTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileContentDescriptionTest {

	public static final String TEST_FILE_PATH = "./TestData/FCD1.json";

	private static WebServiceHelper<FileContentDescriptionBean> helper =
			new WebServiceHelper<FileContentDescriptionBean>(
				FileContentDescriptionBean.class,
				Resource.PATH_FILE_CONTENT_DESCRIPTIONS);
	private static WebServiceHelper<ArtworkBean> aHelper =
			new WebServiceHelper<ArtworkBean>(
				ArtworkBean.class,
				Resource.PATH_ARTWORKS);

	private ArtworkTest aTest;
	private static List<FileContentDescriptionBean> fcdbs;

	private FileContentDescriptionBean fcdb;
	public FileContentDescriptionBean getFileContentDescription() { return fcdb; }
	
	@BeforeClass
	public static void setup() {

		ArtworkTest.setup();
	}

	@AfterClass
	public static void cleanup() {
		
		ArtworkTest.cleanup();
	}

	@Parameters
	public static Collection<Object[]> load() throws JAXBException {

		FileContentDescriptionTest.fcdbs = new JSONHelper<FileContentDescriptionBean>(FileContentDescriptionBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object[]> mmbs = ArtworkTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object[] arr : mmbs) {
			for (Object fcdb : FileContentDescriptionTest.fcdbs) {
				params.add(new Object[] {
						OfferBean.class.cast(arr[0]),
						MarketingMaterialBean.class.cast(arr[1]),
						ArtworkBean.class.cast(arr[2]),
						fcdb});
			}
		}

		return params;
	}

	public FileContentDescriptionTest(OfferBean ob, MarketingMaterialBean mmb, ArtworkBean ab, FileContentDescriptionBean fcdb) {

		this.aTest = new ArtworkTest(ob, mmb, ab);
		this.fcdb = fcdb;
	}

	@Before
	public void beforeTest() {

		this.aTest.beforeTest();

		this.fcdb.setParentId(
				aHelper.postBean(aTest.getArtwork()).getId());
	}

	@After
	public void afterTest() {

		aHelper.deleteBean(this.fcdb.getParentId());

		this.aTest.afterTest();
	}

	@Test(expected=ResourceConflictException.class)
	public void postFCDInvalidArtworkId() {

		FileContentDescriptionBean fcdb = new FileContentDescriptionBean().init(this.fcdb);
		fcdb.setParentId(Long.MIN_VALUE);
		helper.postRaw(fcdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postFCDUnknownArtworkId() {

		FileContentDescriptionBean fcdb = new FileContentDescriptionBean().init(this.fcdb);
		fcdb.setParentId(Long.MAX_VALUE);
		helper.postBean(fcdb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postFCDNullName() {

		FileContentDescriptionBean fcdb = new FileContentDescriptionBean().init(this.fcdb);
		fcdb.setFileContentDescription(null);
		helper.postRaw(fcdb);
	}
	
	@Test(expected=ResourceConflictException.class)
	public void postFCDLongName() {

		FileContentDescriptionBean fcdb = new FileContentDescriptionBean().init(this.fcdb);
		fcdb.setFileContentDescription(
				String.format("%" + (FileContentDescription.MAX_DESCRIPTION_LENGTH + 1) + "s", "*"));
		helper.postRaw(fcdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getFCDInvalidId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putFCDInvalidId() {

		FileContentDescriptionBean fcdb = new FileContentDescriptionBean().init(this.fcdb);
		fcdb.setId(Long.MIN_VALUE);
		helper.putRaw(fcdb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void putFCDUnknownId() {

		FileContentDescriptionBean fcdb = new FileContentDescriptionBean().init(this.fcdb);
		fcdb.setId(Long.MAX_VALUE);
		helper.putBean(fcdb);
	}

	@Test
	public void putFCDNullName() {

		long fcdId = helper.postBean(this.fcdb).getId();
		FileContentDescriptionBean fcdb = helper.getBean(fcdId);
		fcdb.setFileContentDescription(null);
		try {
			helper.putRaw(fcdb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(fcdId);
		}
		
		fail("Expected ResourceConflictException not thrown in putFCDNullName()");
	}
	
	@Test
	public void putFCDLongName() {

		long fcdId = helper.postBean(this.fcdb).getId();
		FileContentDescriptionBean fcdb = helper.getBean(fcdId);
		fcdb.setFileContentDescription(
				String.format("%" + (FileContentDescription.MAX_DESCRIPTION_LENGTH + 1) + "s", "*"));
		try {
			helper.putRaw(fcdb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(fcdId);
		}

		fail("Expected ResourceConflictException not thrown in putFCDLongName()");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void deleteFCDUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}