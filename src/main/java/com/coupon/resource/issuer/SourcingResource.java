package com.coupon.resource.issuer;

import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.coupon.process.message.OfferNotificationReceiptMessage;
import com.coupon.process.message.OfferNotificationResponseMessage;
import com.coupon.process.message.OfferSetupReceiptMessage;
import com.coupon.process.session.remote.SourcingRemote;
import com.coupon.resource.Resource;


@Path("/Sourcing")
@Produces(MediaType.APPLICATION_JSON)
public class SourcingResource extends Resource {

	@EJB(mappedName=SourcingRemote.MAPPED_NAME)
	SourcingRemote sourcingEJB;

	public SourcingResource() {
    }

    @POST
    @Path("/setup/{cpnId}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response setup(@PathParam("cpnId") long cpnId) {

		sourcingEJB.setup(cpnId);
		
		return Response.ok().build();
    }

    @POST
    @Path("/setupAcknowledge")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response setupAcknowledge(@Valid OfferSetupReceiptMessage osrm) {

		sourcingEJB.setupAcknowledge(osrm);

		return Response.ok().build();
    }
    
    @POST
    @Path("/notify/{cpnId}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response notify(@PathParam("cpnId") long cpnId) {

		sourcingEJB.notify(cpnId);

		return Response.ok().build();
    }
   
    @POST
    @Path("/notifyAcknowledge")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response notifyAcknowledge(@Valid OfferNotificationReceiptMessage onrm) {

		sourcingEJB.notifyAcknowledge(onrm);

		return Response.ok().build();
    }
    
    @POST
    @Path("/notifyRespond")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response notifyRespond(@Valid OfferNotificationResponseMessage onrm) {

		sourcingEJB.notifyRespond(onrm);;

		return Response.ok().build();
    }

}
