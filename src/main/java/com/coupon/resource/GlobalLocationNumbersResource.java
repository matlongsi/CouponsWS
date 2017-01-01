package com.coupon.resource;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import com.coupon.common.bean.GlobalLocationNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.session.remote.GlobalLocationNumbersRemote;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_GLOBAL_LOCATION_NUMBERS)
@Produces(MediaType.APPLICATION_JSON)
public class GlobalLocationNumbersResource extends Resource {

	@EJB(mappedName=GlobalLocationNumbersRemote.MAPPED_NAME)
	GlobalLocationNumbersRemote glnEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public GlobalLocationNumbersResource() {
    }

    @GET
	public Response getPage(@QueryParam("companyPrefix") Long companyPrefix,
							@DefaultValue(DEFAULT_PAGE_START) @QueryParam("start") int start,
							@DefaultValue(DEFAULT_PAGE_SIZE) @QueryParam("size") int size) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalLocationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalLocationNumberBean.class,
	    				"companyPrefix",
	    				companyPrefix,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		List<GlobalLocationNumberBean> glns = glnEJB.findPage(companyPrefix,
														curePageStart(start),
														curePageSize(size));
		if (glns == null) {
			throw new ResourceNotFoundException();
		}
		GenericEntity<List<GlobalLocationNumberBean>> glnsEntity = new GenericEntity<List<GlobalLocationNumberBean>>(glns) {};

		return Response.ok(glnsEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{glnId}")
	public Response get(@PathParam("glnId") Long glnId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalLocationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalLocationNumberBean.class,
	    				"glnId",
	    				glnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalLocationNumberBean gln = glnEJB.find(glnId);
		GenericEntity<GlobalLocationNumberBean> glnEntity = new GenericEntity<GlobalLocationNumberBean>(gln) {};

		return Response.ok(glnEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/lookup")
	public Response lookup(@QueryParam("companyPrefix") Long companyPrefix,
							@QueryParam("locationReference") Integer locationReference) {
    	
    		GlobalLocationNumberBean glnb = new GlobalLocationNumberBean(	
    				companyPrefix,
    				locationReference,
    				ValidatorHelper.computeCheckDigit(companyPrefix, locationReference.longValue()));
    		Validator validator = validatorFactory.getValidator();
    		Set<ConstraintViolation<GlobalLocationNumberBean>> constraintViolations = validator
    				.validate(glnb, Default.class, PostValidate.class);
    		if (constraintViolations.size() > 0) {
    			throw new ConstraintViolationException(constraintViolations);
    		}

    		GlobalLocationNumberBean gln = glnEJB.findByNumber(glnb);
		GenericEntity<GlobalLocationNumberBean> glnEntity = new GenericEntity<GlobalLocationNumberBean>(gln) {};

		return Response.ok(glnEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(GlobalLocationNumberBean gln) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalLocationNumberBean>> constraintViolations = validator
				.validate(gln, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		GlobalLocationNumberBean glnAdd = glnEJB.create(gln);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(GlobalLocationNumbersResource.class, "get")
					.build(glnAdd.getId());
		GenericEntity<GlobalLocationNumberBean> glnEntity = new GenericEntity<GlobalLocationNumberBean>(glnAdd) {};

		return Response.created(uri).entity(glnEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{glnId}")
	public Response put(@PathParam("glnId") Long glnId, GlobalLocationNumberBean gln) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalLocationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalLocationNumberBean.class,
	    				"glnId",
	    				glnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		gln.setId(glnId);
		constraintViolations = validator
				.validate(gln, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalLocationNumberBean glnUpdt = glnEJB.update(gln);
		GenericEntity<GlobalLocationNumberBean> glnEntity = new GenericEntity<GlobalLocationNumberBean>(glnUpdt) {};

		return Response.ok(glnEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{glnId}")
	public Response delete(@PathParam("glnId") Long glnId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalLocationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalLocationNumberBean.class,
	    				"glnId",
	    				glnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		glnEJB.delete(glnId);
		
		return Response.noContent().build();
    }

}