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

import com.coupon.common.bean.RewardBean;
import com.coupon.common.session.remote.RewardsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_REWARDS)
@Produces(MediaType.APPLICATION_JSON)
public class RewardsResource extends Resource {

	@EJB(mappedName=RewardsRemote.MAPPED_NAME)
	RewardsRemote rEJB;

	public RewardsResource() {
    }

    @GET
    @Path("/{rId}")
	public Response get(@PathParam("rId") Long rId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardBean>> constraintViolations = validator
				.validateValue(
					RewardBean.class,
	    				"rId",
	    				rId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RewardBean r = rEJB.find(rId);
		GenericEntity<RewardBean> rEntity = new GenericEntity<RewardBean>(r) {};

		return Response.ok(rEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(RewardBean r) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardBean>> constraintViolations = validator
				.validate(r, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		RewardBean rAdd = rEJB.create(r);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(RewardsResource.class, "get")
					.build(rAdd.getId());
		GenericEntity<RewardBean> oaEntity = new GenericEntity<RewardBean>(rAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{rId}")
	public Response put(@PathParam("rId") Long rId, RewardBean r) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardBean>> constraintViolations = validator
				.validateValue(
					RewardBean.class,
	    				"rId",
	    				rId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		r.setId(rId);
		constraintViolations = validator
				.validate(r, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RewardBean rUpdt = rEJB.update(r);
		GenericEntity<RewardBean> rEntity = new GenericEntity<RewardBean>(rUpdt) {};

		return Response.ok(rEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{rId}")
	public Response delete(@PathParam("rId") Long rId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardBean>> constraintViolations = validator
				.validateValue(
					RewardBean.class,
	    				"rId",
	    				rId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		rEJB.delete(rId);
		
		return Response.noContent().build();
    }

}
