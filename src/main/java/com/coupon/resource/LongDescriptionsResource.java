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

import com.coupon.common.LongDescription;
import com.coupon.common.bean.LongDescriptionBean;
import com.coupon.common.session.remote.LongDescriptionsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_LONG_DESCRIPTIONS)
@Produces(MediaType.APPLICATION_JSON)
public class LongDescriptionsResource extends Resource {

	@EJB(mappedName=LongDescriptionsRemote.MAPPED_NAME)
	LongDescriptionsRemote ldEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public LongDescriptionsResource() {
    }

    @GET
    @Path("/{ldId}")
	public Response get(@PathParam("ldId") Long ldId) {

    		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LongDescriptionBean>> constraintViolations = validator
				.validateValue(
					LongDescriptionBean.class,
	    				"ldId",
	    				ldId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		LongDescriptionBean ld = ldEJB.find(ldId);
		GenericEntity<LongDescription> ldEntity = new GenericEntity<LongDescription>(ld) {};

		return Response.ok(ldEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(LongDescriptionBean ld) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LongDescriptionBean>> constraintViolations = validator
				.validate(ld, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		LongDescriptionBean ldAdd = ldEJB.create(ld);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(LongDescriptionsResource.class, "get")
					.build(ldAdd.getId());
		GenericEntity<LongDescription> ldEntity = new GenericEntity<LongDescription>(ldAdd) {};

		return Response.created(uri).entity(ldEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{ldId}")
	public Response put(@PathParam("ldId") Long ldId, LongDescriptionBean ld) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LongDescriptionBean>> constraintViolations = validator
				.validateValue(
					LongDescriptionBean.class,
	    				"ldId",
	    				ldId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		ld.setId(ldId);
		constraintViolations = validator
				.validate(ld, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		LongDescription ldUpdt = ldEJB.update(ld);
		GenericEntity<LongDescription> ldEntity = new GenericEntity<LongDescription>(ldUpdt) {};

		return Response.ok(ldEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{ldId}")
	public Response delete(@PathParam("ldId") Long ldId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LongDescriptionBean>> constraintViolations = validator
				.validateValue(
					LongDescriptionBean.class,
	    				"ldId",
	    				ldId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ldEJB.delete(ldId);
		
		return Response.noContent().build();
    }

}
