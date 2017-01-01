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
import com.coupon.common.bean.FileContentDescriptionBean;
import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.ArtworkTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileContentDescriptionTest {

	public static final String TEST_FILE_PATH = "./TestData/FCD1A.json";

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

	@Test
	public void test1_postFileContentDescription() {

		FileContentDescriptionBean fcdb = helper.postBean(this.fcdb);
		assertTrue("New FileContentDescription does not match import file in test1_postFileContentDescription()",
				this.fcdb.equals(fcdb));
		
		helper.deleteBean(fcdb.getId());
	}
	
	@Test
	public void test2_getFileContentDescriptionById() {

		long fcdId = helper.postBean(this.fcdb).getId();

		FileContentDescriptionBean fcdb = helper.getBean(fcdId);
		assertTrue("Retrieved FileContentDescription does not match import file in test2_getFileContentDescriptionById()",
				this.fcdb.equals(fcdb));

		helper.deleteBean(fcdId);
	}

	@Test
	public void test3_putFileContentDescription() {
		
		long fcdId = helper.postBean(this.fcdb).getId();

		FileContentDescriptionBean fcdb = helper.getBean(fcdId);
		fcdb.setFileContentDescription(fcdb.getFileContentDescription() + "UPDTD");
		FileContentDescriptionBean fcdbUpdt = helper.putBean(fcdb);
		assertTrue("FileContentDescription did not update in test3_putFileContentDescription",
				fcdb.equals(fcdbUpdt));

		helper.deleteBean(fcdId);
	}
	
	@Test
	public void test4_deleteFileContentDescription() {

		long fcdId = helper.postBean(this.fcdb).getId();
		helper.getBean(fcdId);
		helper.deleteBean(fcdId);

		try {
			helper.getBean(fcdId);
			fail("delete failed in test4_deleteFileContentDescription()");
		}
		catch (ResourceNotFoundException ex) { }
	}

}