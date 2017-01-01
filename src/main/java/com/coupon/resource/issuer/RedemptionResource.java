package com.coupon.resource.issuer;

import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.coupon.process.message.RedemptionNotificationMessage;
import com.coupon.process.message.RedemptionValidationRequestMessage;
import com.coupon.process.message.RedemptionValidationResponseMessage;
import com.coupon.process.session.remote.RedemptionRemote;
import com.coupon.resource.Resource;


@Path("/Redemption")
@Produces(MediaType.APPLICATION_JSON)
public class RedemptionResource extends Resource {

	@EJB(mappedName=RedemptionRemote.MAPPED_NAME)
	RedemptionRemote redemptionEJB;

	public RedemptionResource() {
    }

    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response validate(@Valid RedemptionValidationRequestMessage requestMessage) {

    	RedemptionValidationResponseMessage responseMessage = null;
    		
   		responseMessage = redemptionEJB.validateRedemption(requestMessage);
		
		return Response.ok(responseMessage).build();
    }

    @POST
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response notify(@Valid RedemptionNotificationMessage notificationMessage) {

    	redemptionEJB.notifyRedemption(notificationMessage);
		
		return Response.ok().build();
    }

}