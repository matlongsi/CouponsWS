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

import com.coupon.common.bean.DistributionDetailBean;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.PublicationPeriodBean;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.resource.Resource;
import com.coupon.test.action.OfferTest;
import com.coupon.test.utils.JSONHelper;
import com.coupon.test.utils.WebServiceHelper;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DistributionDetailTest {

	public static final String TEST_FILE_PATH = "./TestData/DD1A.json";

	private static WebServiceHelper<DistributionDetailBean> helper =
			new WebServiceHelper<DistributionDetailBean>(
				DistributionDetailBean.class,
				Resource.PATH_DISTRIBUTION_DETAILS);
	private static WebServiceHelper<OfferBean> oHelper =
			new WebServiceHelper<OfferBean>(
				OfferBean.class,
				Resource.PATH_OFFERS);

	private OfferTest oTest;
	private static List<DistributionDetailBean> ddbs;

	private DistributionDetailBean ddb;
	public DistributionDetailBean getDistributionDetail() { return ddb; }
	public OfferBean getOffer() { return oTest.getOffer(); }

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

		DistributionDetailTest.ddbs = new JSONHelper<DistributionDetailBean>(DistributionDetailBean.class)
				.arrayFromJson(TEST_FILE_PATH);

		Collection<? extends Object> obs = OfferTest.load();

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object rb : DistributionDetailTest.ddbs) {
				params.add(new Object[] {ob, rb});
			}
		}

		return params;
	}

	public static Collection<Object[]> load(String oFilePath, String rFilePath) throws JAXBException {

		DistributionDetailTest.ddbs = new JSONHelper<DistributionDetailBean>(DistributionDetailBean.class)
				.arrayFromJson(rFilePath);

		Collection<? extends Object> obs = OfferTest.load(oFilePath);

		Collection<Object[]> params = new ArrayList<>();
		for (Object ob : obs) {
			for (Object ddb : DistributionDetailTest.ddbs) {
				params.add(new Object[] {ob, ddb});
			}
		}

		return params;
	}

	public DistributionDetailTest(OfferBean ob, DistributionDetailBean ddb) {

		this.oTest = new OfferTest(ob);
		this.ddb = ddb;
	}

	@Before
	public void beforeTest() {
		
		this.ddb.setParentId(
				oHelper.postBean(oTest.getOffer()).getId());
	}

	@After
	public void afterTest() {

		oHelper.deleteBean(this.ddb.getParentId());
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDInvalidOfferId() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setParentId(Long.MIN_VALUE);
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void postDDUnknownOfferId() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setParentId(Long.MAX_VALUE);
		helper.postBean(ddb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDNullPublicationPeriod() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setPublicationPeriod(null);
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDInvalidPublicationPeriod() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setPublicationPeriod(
				new PublicationPeriodBean());
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDHighTotalAcquisitionCount() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setTotalAcquisitionCount(Long.MAX_VALUE);
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDLowTotalAcquisitionCount() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setTotalAcquisitionCount(Long.MIN_VALUE);
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDHighMaximumOfferAcquisition() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setMaximumOfferAcquisition(Long.MAX_VALUE);
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceConflictException.class)
	public void postDDLowMaximumOfferAcquisition() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setMaximumOfferAcquisition(Long.MIN_VALUE);
		helper.postRaw(ddb);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getDDUnknownId() {

		helper.getBean(Long.MAX_VALUE);
	}

	@Test(expected=ResourceConflictException.class)
	public void putDDInvalidId() {

		DistributionDetailBean ddb = new DistributionDetailBean().init(this.ddb);
		ddb.setId(Long.MIN_VALUE);
		helper.putRaw(ddb);
	}

	@Test
	public void putDDUnknownId() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setId(Long.MAX_VALUE);
		try {
			helper.putBean(ddb);
		}
		catch (ResourceNotFoundException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceNotFoundException not thrown in putDDUnknownId()");
	}

	@Test
	public void putDDNullPublicationPeriod() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setPublicationPeriod(null);
		try {
			helper.putRaw(ddb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceConflictException not thrown in putDDHighTotalAcquisitionCount()");
	}
	
	@Test
	public void putDDInvalidPublicationPeriod() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setPublicationPeriod(
				new PublicationPeriodBean());
		try {
			helper.putRaw(ddb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceConflictException not thrown in putDDInvalidPublicationPeriod()");
	}

	@Test
	public void putDDHighTotalAcquisitionCount() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setTotalAcquisitionCount(Long.MAX_VALUE);
		try {
			helper.putRaw(ddb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceConflictException not thrown in putDDHighTotalAcquisitionCount()");
	}
	
	@Test
	public void putDDLowTotalAcquisitionCount() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setTotalAcquisitionCount(Long.MIN_VALUE);
		try {
			helper.putRaw(ddb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceConflictException not thrown in putDDLowTotalAcquisitionCount()");
	}
	
	@Test
	public void putDDHighMaximumOfferAcquisition() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setMaximumOfferAcquisition(Long.MAX_VALUE);
		try {
			helper.putRaw(ddb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceConflictException not thrown in putDDHighMaximumOfferAcquisition()");
	}

	@Test
	public void putDDLowMaximumOfferAcquisition() {

		long ddId = helper.postBean(this.ddb).getId();
		DistributionDetailBean ddb = helper.getBean(ddId);
		ddb.setMaximumOfferAcquisition(Long.MIN_VALUE);
		try {
			helper.putRaw(ddb);
		}
		catch (ResourceConflictException ex) {
			return;
		}
		finally {
			helper.deleteBean(ddId);
		}
		
		fail("Expected ResourceConflictException not thrown in putDDLowMaximumOfferAcquisition()");
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void deleteDDUnknownId() {

		helper.deleteBean(Long.MAX_VALUE);
	}

}
