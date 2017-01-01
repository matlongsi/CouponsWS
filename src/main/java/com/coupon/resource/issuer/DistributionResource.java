package com.coupon.resource.issuer;

import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.coupon.process.message.AcquireCouponMessage;
import com.coupon.process.message.AcquisitionConfirmationMessage;
import com.coupon.process.session.remote.DistributionRemote;
import com.coupon.resource.Resource;


@Path("/Distribution")
@Produces(MediaType.APPLICATION_JSON)
public class DistributionResource extends Resource {

	@EJB(mappedName=DistributionRemote.MAPPED_NAME)
	DistributionRemote distributionEJB;

	public DistributionResource() {
    }

    @POST
    @Path("/acquire")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response setup(@Valid AcquireCouponMessage couponMessage) {

    	AcquisitionConfirmationMessage confirmationMessage = distributionEJB.acquire(couponMessage);
		
		//TODO send and receive message from awarder

		return Response.ok(confirmationMessage).build();
    }

}
