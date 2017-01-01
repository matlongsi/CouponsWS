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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.coupon.common.bean.AwarderDetailBean;
import com.coupon.common.session.remote.AwarderDetailsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_AWARDER_DETAILS)
@Produces(MediaType.APPLICATION_JSON)
public class AwarderDetailsResource extends Resource {

	@EJB(mappedName=AwarderDetailsRemote.MAPPED_NAME)
	AwarderDetailsRemote adEJB;

	public AwarderDetailsResource() {
    }

    @GET
    @Path("/{adId}")
	public Response get(@PathParam("adId") Long adId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderDetailBean>> constraintViolations = validator
				.validateValue(
					AwarderDetailBean.class,
	    				"adId",
	    				adId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AwarderDetailBean ad = adEJB.find(adId);
		GenericEntity<AwarderDetailBean> adEntity = new GenericEntity<AwarderDetailBean>(ad) {};

		return Response.ok(adEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(AwarderDetailBean ad) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderDetailBean>> constraintViolations = validator
				.validate(ad, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AwarderDetailBean adAdd = adEJB.create(ad);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(AwarderDetailsResource.class, "get")
					.build(adAdd.getId());
		GenericEntity<AwarderDetailBean> adEntity = new GenericEntity<AwarderDetailBean>(adAdd) {};

		return Response.created(uri).entity(adEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{adId}")
	public Response put(@PathParam("adId") long adId, AwarderDetailBean ad) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderDetailBean>> constraintViolations = validator
				.validateValue(
					AwarderDetailBean.class,
	    				"adId",
	    				adId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		ad.setId(adId);
		constraintViolations = validator
				.validate(ad, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AwarderDetailBean adUpdt = adEJB.update(ad);
		GenericEntity<AwarderDetailBean> adEntity = new GenericEntity<AwarderDetailBean>(adUpdt) {};

		return Response.ok(adEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{adId}")
	public Response delete(@PathParam("adId") Long adId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderDetailBean>> constraintViolations = validator
				.validateValue(
					AwarderDetailBean.class,
	    				"adId",
	    				adId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		adEJB.delete(adId);
		
		return Response.noContent().build();
    }

}