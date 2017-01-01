package com.coupon.resource;

import java.net.URI;
import java.util.Set;

import javax.ejb.EJB;
import javax.validation.groups.Default;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
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

import com.coupon.common.bean.ShortDescriptionBean;
import com.coupon.common.session.remote.ShortDescriptionsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_SHORT_DESCRIPTIONS)
@Produces(MediaType.APPLICATION_JSON)
public class ShortDescriptionsResource extends Resource {

	@EJB(mappedName=ShortDescriptionsRemote.MAPPED_NAME)
	ShortDescriptionsRemote sdEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public ShortDescriptionsResource() {
    }

    @GET
    @Path("/{sdId}")
	public Response get(@PathParam("sdId") Long sdId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ShortDescriptionBean>> constraintViolations = validator
				.validateValue(
	    				ShortDescriptionBean.class,
	    				"sdId",
	    				sdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ShortDescriptionBean sd = sdEJB.find(sdId);
		GenericEntity<ShortDescriptionBean> sdEntity = new GenericEntity<ShortDescriptionBean>(sd) {};

		return Response.ok(sdEntity, MediaType.APPLICATION_JSON).build();
    }

	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(ShortDescriptionBean sd) {

		Validator validator = validatorFactory.getValidator();
    		Set<ConstraintViolation<ShortDescriptionBean>> constraintViolations = validator
    				.validate(sd, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		ShortDescriptionBean sdAdd = sdEJB.create(sd);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(ShortDescriptionsResource.class, "get")
					.build(sdAdd.getId());		
		GenericEntity<ShortDescriptionBean> sdEntity = new GenericEntity<ShortDescriptionBean>(sdAdd) {};

		return Response.created(uri).entity(sdEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{sdId}")
	public Response put(@PathParam("sdId") Long sdId, ShortDescriptionBean sd) {

    		Validator validator = validatorFactory.getValidator();
    		Set<ConstraintViolation<ShortDescriptionBean>> constraintViolations = validator
    				.validateValue(
		    				ShortDescriptionBean.class,
		    				"sdId",
		    				sdId,
		    				Default.class);
    		if (constraintViolations.size() > 0) {
    			throw new ConstraintViolationException(constraintViolations);
    		}
    		sd.setId(sdId);
		constraintViolations = validator
				.validate(sd, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ShortDescriptionBean sdUpdt = sdEJB.update(sd);
		GenericEntity<ShortDescriptionBean> sdEntity = new GenericEntity<ShortDescriptionBean>(sdUpdt) {};

		return Response.ok(sdEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{sdId}")
	public Response delete(@PathParam("sdId") long sdId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ShortDescriptionBean>> constraintViolations = validator
				.validateValue(
	    				ShortDescriptionBean.class,
	    				"sdId",
	    				sdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		sdEJB.delete(sdId);
		
		return Response.noContent().build();
    }

}
