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

import com.coupon.common.bean.GlobalServiceRelationNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.session.remote.GlobalServiceRelationNumbersRemote;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_GLOBAL_SERVICE_RELATION_NUMBERS)
@Produces(MediaType.APPLICATION_JSON)
public class GlobalServiceRelationNumbersResource extends Resource {

	@EJB(mappedName=GlobalServiceRelationNumbersRemote.MAPPED_NAME)
	GlobalServiceRelationNumbersRemote gsrnEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public GlobalServiceRelationNumbersResource() {
    }

    @GET
	public Response getPage(
						@QueryParam("companyPrefix") Long companyPrefix,
						@DefaultValue(DEFAULT_PAGE_START) @QueryParam("start") int start,
						@DefaultValue(DEFAULT_PAGE_SIZE) @QueryParam("size") int size) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalServiceRelationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalServiceRelationNumberBean.class,
	    				"companyPrefix",
	    				companyPrefix,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		List<GlobalServiceRelationNumberBean> gsrns = gsrnEJB.findPage(companyPrefix,
															curePageStart(start),
															curePageSize(size));
		if (gsrns == null) {
			throw new ResourceNotFoundException();
		}
		GenericEntity<List<GlobalServiceRelationNumberBean>> gsrnsEntity = new GenericEntity<List<GlobalServiceRelationNumberBean>>(gsrns) {};

		return Response.ok(gsrnsEntity, MediaType.APPLICATION_JSON).build();
    }

	@GET
    @Path("/{gsrnId}")
	public Response get(@PathParam("gsrnId") Long gsrnId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalServiceRelationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalServiceRelationNumberBean.class,
	    				"gsrnId",
	    				gsrnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalServiceRelationNumberBean gsrn = gsrnEJB.find(gsrnId);
		GenericEntity<GlobalServiceRelationNumberBean> gsrnEntity = new GenericEntity<GlobalServiceRelationNumberBean>(gsrn) {};

		return Response.ok(gsrnEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/lookup")
	public Response lookup(@QueryParam("companyPrefix") Long companyPrefix,
							@QueryParam("serviceReference") Long serviceReference) {
    	
		GlobalServiceRelationNumberBean gsrnb = new GlobalServiceRelationNumberBean(
				companyPrefix,
				serviceReference,
				ValidatorHelper.computeCheckDigit(companyPrefix, serviceReference));
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalServiceRelationNumberBean>> constraintViolations = validator
				.validate(gsrnb, Default.class, PostValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

    		GlobalServiceRelationNumberBean gsrn = gsrnEJB.findByNumber(gsrnb);
		GenericEntity<GlobalServiceRelationNumberBean> gsrnEntity = new GenericEntity<GlobalServiceRelationNumberBean>(gsrn) {};

		return Response.ok(gsrnEntity, MediaType.APPLICATION_JSON).build();
    }

	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(GlobalServiceRelationNumberBean gsrn) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalServiceRelationNumberBean>> constraintViolations = validator
				.validate(gsrn, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
				
		GlobalServiceRelationNumberBean gsrnAdd = gsrnEJB.create(gsrn);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(GlobalServiceRelationNumbersResource.class, "get")
					.build(gsrnAdd.getId());
		GenericEntity<GlobalServiceRelationNumberBean> gsrnEntity = new GenericEntity<GlobalServiceRelationNumberBean>(gsrnAdd) {};

		return Response.created(uri).entity(gsrnEntity).build();
	}

	@PUT
    @Path("/{gsrnId}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response put(@PathParam("gsrnId") Long gsrnId, GlobalServiceRelationNumberBean gsrn) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalServiceRelationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalServiceRelationNumberBean.class,
	    				"gsrnId",
	    				gsrnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		gsrn.setId(gsrnId);
		constraintViolations = validator
				.validate(gsrn, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalServiceRelationNumberBean gsrnUpdt = gsrnEJB.update(gsrn);
		GenericEntity<GlobalServiceRelationNumberBean> gsrnEntity = new GenericEntity<GlobalServiceRelationNumberBean>(gsrnUpdt) {};

		return Response.ok(gsrnEntity, MediaType.APPLICATION_JSON).build();
    }

	@DELETE
    @Path("/{gsrnId}")
	public Response delete(@PathParam("gsrnId") Long gsrnId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalServiceRelationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalServiceRelationNumberBean.class,
	    				"gsrnId",
	    				gsrnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		gsrnEJB.delete(gsrnId);
		
		return Response.noContent().build();
	}

}