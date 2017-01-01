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

import com.coupon.common.FileContentDescription;
import com.coupon.common.bean.FileContentDescriptionBean;
import com.coupon.common.session.remote.FileContentDescriptionsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path("/FileContentDescriptions")
@Produces(MediaType.APPLICATION_JSON)
public class FileContentDescriptionsResource extends Resource {

	@EJB(mappedName=FileContentDescriptionsRemote.MAPPED_NAME)
	FileContentDescriptionsRemote fcdEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public FileContentDescriptionsResource() {
    }

    @GET
    @Path("/{fcdId}")
	public Response get(@PathParam("fcdId") Long fcdId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FileContentDescriptionBean>> constraintViolations = validator
				.validateValue(
					FileContentDescriptionBean.class,
	    				"fcdId",
	    				fcdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		FileContentDescription fcd = fcdEJB.find(fcdId);
		GenericEntity<FileContentDescription> fcdEntity = new GenericEntity<FileContentDescription>(fcd) {};

		return Response.ok(fcdEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(FileContentDescriptionBean fcd) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FileContentDescriptionBean>> constraintViolations = validator
				.validate(fcd, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		FileContentDescriptionBean fcdAdd = fcdEJB.create(fcd);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(FileContentDescriptionsResource.class, "get")
					.build(fcdAdd.getId());
		GenericEntity<FileContentDescription> fcdEntity = new GenericEntity<FileContentDescription>(fcdAdd) {};

		return Response.created(uri).entity(fcdEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{fcdId}")
	public Response put(@PathParam("fcdId") Long fcdId, FileContentDescriptionBean fcd) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FileContentDescriptionBean>> constraintViolations = validator
				.validateValue(
					FileContentDescriptionBean.class,
	    				"fcdId",
	    				fcdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		fcd.setId(fcdId);
		constraintViolations = validator
				.validate(fcd, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		FileContentDescriptionBean fcdUpdt = fcdEJB.update(fcd);
		GenericEntity<FileContentDescription> fcdEntity = new GenericEntity<FileContentDescription>(fcdUpdt) {};

		return Response.ok(fcdEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{fcdId}")
	public Response delete(@PathParam("fcdId") Long fcdId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FileContentDescriptionBean>> constraintViolations = validator
				.validateValue(
					FileContentDescriptionBean.class,
	    				"fcdId",
	    				fcdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		fcdEJB.delete(fcdId);
		
		return Response.noContent().build();
    }

}