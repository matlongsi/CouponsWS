package com.coupon.test.action;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.coupon.process.message.AcquireCouponMessage;
import com.coupon.process.message.OfferNotificationReceiptMessage;
import com.coupon.process.message.OfferNotificationResponseMessage;
import com.coupon.process.message.OfferSetupReceiptMessage;
import com.coupon.process.message.RedemptionNotificationMessage;
import com.coupon.process.message.RedemptionValidationRequestMessage;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProcessTest {

	private static final String REST_SERVICE_URL_BASE = "http://localhost:8080/CouponsWS/rest";
	public static List<Long> cpnIds = new ArrayList<Long>();
    
	@Test
	public void processTest() throws IOException, JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(
										OfferSetupReceiptMessage.class,
										OfferNotificationReceiptMessage.class,
										OfferNotificationResponseMessage.class,
										AcquireCouponMessage.class,
										RedemptionValidationRequestMessage.class,
										RedemptionNotificationMessage.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");

		Client client = ClientBuilder.newClient();

		Response response = client.target(REST_SERVICE_URL_BASE)
							.path("/Sourcing/setup/" + 1)
							.request()
							.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Sourcing/setup/ is not OK(200)!", (response.getStatus() == 200));

		OfferSetupReceiptMessage setupReceipt = unmarshaller.unmarshal(
												new StreamSource(new File("./TestData/OfferSetupReceipt1.json")),
												OfferSetupReceiptMessage.class).getValue();
		response = client.target(REST_SERVICE_URL_BASE)
					.path("/Sourcing/setupAcknowledge")
					.request()
					.post(Entity.entity(setupReceipt, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Sourcing/setupAcknowledge is not OK(200)!", (response.getStatus() == 200));

		response = client.target(REST_SERVICE_URL_BASE)
					.path("Sourcing/notify/" + 1)
					.request()
					.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Sourcing/notify is not OK(200)!", (response.getStatus() == 200));

		OfferNotificationReceiptMessage notifyReceipt = unmarshaller.unmarshal(
												new StreamSource(new File("./TestData/NotifyAcknowledge1.json")),
												OfferNotificationReceiptMessage.class).getValue();
		response = client.target(REST_SERVICE_URL_BASE)
					.path("/Sourcing/notifyAcknowledge")
					.request()
					.post(Entity.entity(notifyReceipt, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Sourcing/notifyAcknowledge is not OK(200)!", (response.getStatus() == 200));

		OfferNotificationResponseMessage notifyResponse = unmarshaller.unmarshal(
												new StreamSource(new File("./TestData/NotifyResponse1.json")),
												OfferNotificationResponseMessage.class).getValue();
		response = client.target(REST_SERVICE_URL_BASE)
					.path("/Sourcing/notifyRespond")
					.request()
					.post(Entity.entity(notifyResponse, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Sourcing/notifyRespond is not OK(200)!", (response.getStatus() == 200));

		AcquireCouponMessage acquireCoupon = unmarshaller.unmarshal(
												new StreamSource(new File("./TestData/AcquireCoupon1.json")),
												AcquireCouponMessage.class).getValue();
		response = client.target(REST_SERVICE_URL_BASE)
					.path("/Distribution/acquire")
					.request()
					.post(Entity.entity(acquireCoupon, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Distribution/acquire is not OK(200)!", (response.getStatus() == 200));

		RedemptionValidationRequestMessage validateRequest = unmarshaller.unmarshal(
												new StreamSource(new File("./TestData/RedemptionValidate1.json")),
												RedemptionValidationRequestMessage.class).getValue();
		response = client.target(REST_SERVICE_URL_BASE)
					.path("/Redemption/validate")
					.request()
					.post(Entity.entity(validateRequest, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Redemption/validate is not OK(200)!", (response.getStatus() == 200));

		RedemptionNotificationMessage redemptionNotify = unmarshaller.unmarshal(
												new StreamSource(new File("./TestData/RedemptionNotify1.json")),
												RedemptionNotificationMessage.class).getValue();
		response = client.target(REST_SERVICE_URL_BASE)
					.path("/Redemption/notify")
					.request()
					.post(Entity.entity(redemptionNotify, MediaType.APPLICATION_JSON));
		assertTrue("Response status for /Redemption/notify is not OK(200)!", (response.getStatus() == 200));

	}
	
}