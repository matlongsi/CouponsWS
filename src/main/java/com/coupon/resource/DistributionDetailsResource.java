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

import com.coupon.common.DistributionDetail;
import com.coupon.common.bean.DistributionDetailBean;
import com.coupon.common.session.remote.DistributionDetailsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_DISTRIBUTION_DETAILS)
@Produces(MediaType.APPLICATION_JSON)
public class DistributionDetailsResource extends Resource {

	@EJB(mappedName=DistributionDetailsRemote.MAPPED_NAME)
	DistributionDetailsRemote ddEJB;

	public DistributionDetailsResource() {
    }

    @GET
    @Path("/{ddId}")
	public Response get(@PathParam("ddId") Long ddId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<DistributionDetailBean>> constraintViolations = validator
				.validateValue(
					DistributionDetailBean.class,
	    				"ddId",
	    				ddId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		DistributionDetailBean dd = ddEJB.find(ddId);
		GenericEntity<DistributionDetail> ddEntity = new GenericEntity<DistributionDetail>(dd) {};

		return Response.ok(ddEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(DistributionDetailBean dd) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<DistributionDetailBean>> constraintViolations = validator
				.validate(dd, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		DistributionDetailBean ddAdd = ddEJB.create(dd);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(DistributionDetailsResource.class, "get")
					.build(ddAdd.getId());
		GenericEntity<DistributionDetail> oaEntity = new GenericEntity<DistributionDetail>(ddAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{ddId}")
	public Response put(@PathParam("ddId") Long ddId, DistributionDetailBean dd) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<DistributionDetailBean>> constraintViolations = validator
				.validateValue(
					DistributionDetailBean.class,
	    				"ddId",
	    				ddId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		dd.setId(ddId);
		constraintViolations = validator
				.validate(dd, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		DistributionDetailBean ddUpdt = ddEJB.update(dd);
		GenericEntity<DistributionDetail> ddEntity = new GenericEntity<DistributionDetail>(ddUpdt) {};

		return Response.ok(ddEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{ddId}")
	public Response delete(@PathParam("ddId") Long ddId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<DistributionDetailBean>> constraintViolations = validator
				.validateValue(
					DistributionDetailBean.class,
	    				"ddId",
	    				ddId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ddEJB.delete(ddId);
		
		return Response.noContent().build();
    }

}