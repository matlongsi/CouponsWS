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

import com.coupon.common.bean.UsageConditionBean;
import com.coupon.common.session.remote.UsageConditionsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_USAGE_CONDITIONS)
@Produces(MediaType.APPLICATION_JSON)
public class UsageConditionsResource extends Resource {

	@EJB(mappedName=UsageConditionsRemote.MAPPED_NAME)
	UsageConditionsRemote ucEJB;

	public UsageConditionsResource() {
    }

    @GET
    @Path("/{ucId}")
	public Response get(@PathParam("ucId") Long ucId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<UsageConditionBean>> constraintViolations = validator
				.validateValue(
					UsageConditionBean.class,
	    				"ucId",
	    				ucId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		UsageConditionBean uc = ucEJB.find(ucId);
		GenericEntity<UsageConditionBean> ucEntity = new GenericEntity<UsageConditionBean>(uc) {};

		return Response.ok(ucEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(UsageConditionBean uc) {
		
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<UsageConditionBean>> constraintViolations = validator
				.validate(uc, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		UsageConditionBean ucAdd = ucEJB.create(uc);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(UsageConditionsResource.class, "get")
					.build(ucAdd.getId());
		GenericEntity<UsageConditionBean> oaEntity = new GenericEntity<UsageConditionBean>(ucAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{ucId}")
	public Response put(@PathParam("ucId") Long ucId, UsageConditionBean uc) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<UsageConditionBean>> constraintViolations = validator
				.validateValue(
					UsageConditionBean.class,
	    				"ucId",
	    				ucId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		uc.setId(ucId);
		constraintViolations = validator
				.validate(uc, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		UsageConditionBean ucUpdt = ucEJB.update(uc);
		GenericEntity<UsageConditionBean> ucEntity = new GenericEntity<UsageConditionBean>(ucUpdt) {};

		return Response.ok(ucEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{ucId}")
	public Response delete(@PathParam("ucId") Long ucId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<UsageConditionBean>> constraintViolations = validator
				.validateValue(
					UsageConditionBean.class,
	    				"ucId",
	    				ucId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ucEJB.delete(ucId);
		
		return Response.noContent().build();
    }

}