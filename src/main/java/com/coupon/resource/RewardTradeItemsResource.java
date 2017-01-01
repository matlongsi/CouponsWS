package com.coupon.resource;

import java.net.URI;
import java.util.Set;

import javax.ejb.EJB;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
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

import com.coupon.common.bean.RewardTradeItemBean;
import com.coupon.common.session.remote.RewardTradeItemsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_REWARD_TRADE_ITEMS)
@Produces(MediaType.APPLICATION_JSON)
public class RewardTradeItemsResource extends Resource {

	@EJB(mappedName=RewardTradeItemsRemote.MAPPED_NAME)
	RewardTradeItemsRemote rtiEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public RewardTradeItemsResource() {
    }

    @GET
    @Path("/{rtiId}")
	public Response get(@PathParam("rtiId") long rtiId) {
    	
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardTradeItemBean>> constraintViolations = validator
				.validateValue(
					RewardTradeItemBean.class,
	    				"rtiId",
	    				rtiId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RewardTradeItemBean rti = rtiEJB.find(rtiId);
		GenericEntity<RewardTradeItemBean> rtiEntity = new GenericEntity<RewardTradeItemBean>(rti) {};

		return Response.ok(rtiEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Valid RewardTradeItemBean rti) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardTradeItemBean>> constraintViolations = validator
				.validate(rti, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		RewardTradeItemBean rtiAdd = rtiEJB.create(rti);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(RewardTradeItemsResource.class, "get")
					.build(rtiAdd.getId());
		GenericEntity<RewardTradeItemBean> rtiEntity = new GenericEntity<RewardTradeItemBean>(rtiAdd) {};

		return Response.created(uri).entity(rtiEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{rtiId}")
	public Response put(@PathParam("rtiId") Long rtiId, RewardTradeItemBean rti) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardTradeItemBean>> constraintViolations = validator
				.validateValue(
					RewardTradeItemBean.class,
	    				"rtiId",
	    				rtiId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		rti.setId(rtiId);
		constraintViolations = validator
				.validate(rti, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RewardTradeItemBean rtiUpdt = rtiEJB.update(rti);
		GenericEntity<RewardTradeItemBean> rtiEntity = new GenericEntity<RewardTradeItemBean>(rtiUpdt) {};

		return Response.ok(rtiEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{rtiId}")
	public Response delete(@PathParam("rtiId") long rtiId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RewardTradeItemBean>> constraintViolations = validator
				.validateValue(
					RewardTradeItemBean.class,
	    				"rtiId",
	    				rtiId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		rtiEJB.delete(rtiId);
		
		return Response.noContent().build();
    }

}
