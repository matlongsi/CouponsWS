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

import com.coupon.common.bean.RewardLoyaltyPointBean;
import com.coupon.common.session.remote.RewardLoyaltyPointsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path("/RewardLoyaltyPoints")
@Produces(MediaType.APPLICATION_JSON)
public class RewardLoyaltyPointsResource extends Resource {

	@EJB(mappedName=RewardLoyaltyPointsRemote.MAPPED_NAME)
	RewardLoyaltyPointsRemote rlpEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public RewardLoyaltyPointsResource() {
    }

    @GET
    @Path("/{rlpId}")
	public Response get(@PathParam("rlpId") long rlpId) {
    	
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardLoyaltyPointBean>> constraintViolations = validator
				.validateValue(
					RewardLoyaltyPointBean.class,
	    				"rlpId",
	    				rlpId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RewardLoyaltyPointBean rlp = rlpEJB.find(rlpId);
		GenericEntity<RewardLoyaltyPointBean> rlpEntity = new GenericEntity<RewardLoyaltyPointBean>(rlp) {};

		return Response.ok(rlpEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(RewardLoyaltyPointBean rlp) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardLoyaltyPointBean>> constraintViolations = validator
				.validate(rlp, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		RewardLoyaltyPointBean rlpAdd = rlpEJB.create(rlp);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(RewardLoyaltyPointsResource.class, "get")
					.build(rlpAdd.getId());
		GenericEntity<RewardLoyaltyPointBean> rlpEntity = new GenericEntity<RewardLoyaltyPointBean>(rlpAdd) {};

		return Response.created(uri).entity(rlpEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{rlpId}")
	public Response put(@PathParam("rlpId") Long rlpId, RewardLoyaltyPointBean rlp) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardLoyaltyPointBean>> constraintViolations = validator
				.validateValue(
					RewardLoyaltyPointBean.class,
	    				"rlpId",
	    				rlpId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		rlp.setId(rlpId);
		constraintViolations = validator
				.validate(rlp, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RewardLoyaltyPointBean rlpUpdt = rlpEJB.update(rlp);
		GenericEntity<RewardLoyaltyPointBean> rlpEntity = new GenericEntity<RewardLoyaltyPointBean>(rlpUpdt) {};

		return Response.ok(rlpEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{rlpId}")
	public Response delete(@PathParam("rlpId") Long rlpId) {

		rlpEJB.delete(rlpId);
		
		return Response.noContent().build();
    }

}
