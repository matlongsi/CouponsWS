package com.coupon.resource;

import java.net.URI;
import java.util.Set;

import javax.ejb.EJB;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.coupon.common.Offer;
import com.coupon.common.bean.OfferBean;
import com.coupon.common.bean.GlobalCouponNumberBean;
import com.coupon.common.session.remote.OffersRemote;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_OFFERS)
@Produces(MediaType.APPLICATION_JSON)
public class OffersResource extends Resource {

	@EJB(mappedName=OffersRemote.MAPPED_NAME)
	OffersRemote oEJB;

	public OffersResource() {
    }

    @GET
    @Path("/{oId}")
	public Response get(@PathParam("oId") Long oId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<OfferBean>> constraintViolations = validator
				.validateValue(
					OfferBean.class,
	    				"offerId",
	    				oId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		OfferBean o = oEJB.find(oId);
		GenericEntity<OfferBean> oEntity = new GenericEntity<OfferBean>(o) {};

		return Response.ok(oEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/lookup")
	public Response lookup(@QueryParam("companyPrefix") Long companyPrefix,
							@QueryParam("couponReference") Integer couponReference,
							@QueryParam("serialComponent") Long serialComponent) {
    	
    		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean(
	    			companyPrefix,
	    			couponReference,
	    			ValidatorHelper.computeCheckDigit(companyPrefix, couponReference.longValue()),
	    			serialComponent);
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
				.validate(gcnb, Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
	    	
	    	OfferBean o = oEJB.findByCouponNumber(gcnb);
		GenericEntity<Offer> oEntity = new GenericEntity<Offer>(o) {};

		return Response.ok(oEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(OfferBean o) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<OfferBean>> constraintViolations = validator
				.validate(o, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		OfferBean oAdd = oEJB.create(o);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(OffersResource.class, "get")
					.build(oAdd.getId());
		GenericEntity<OfferBean> oEntity = new GenericEntity<OfferBean>(oAdd) {};

		return Response.created(uri).entity(oEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{oId}")
	public Response put(@PathParam("oId") Long oId, OfferBean o) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<OfferBean>> constraintViolations = validator
				.validateValue(
					OfferBean.class,
	    				"offerId",
	    				oId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		o.setId(oId);
		constraintViolations = validator
				.validate(o, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		OfferBean oUpdt = oEJB.update(o);
		GenericEntity<OfferBean> oEntity = new GenericEntity<OfferBean>(oUpdt) {};

		return Response.ok(oEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{oId}")
	public Response delete(@PathParam("oId") Long oId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<OfferBean>> constraintViolations = validator
				.validateValue(
					OfferBean.class,
	    				"offerId",
	    				oId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		oEJB.delete(oId);
		
		return Response.noContent().build();
    }

}