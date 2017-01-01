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

import com.coupon.common.bean.AwarderPointOfSaleBean;
import com.coupon.common.session.remote.AwarderPointOfSalesRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_AWARDER_POINT_OF_SALES)
@Produces(MediaType.APPLICATION_JSON)
public class AwarderPointOfSalesResource extends Resource {

	@EJB(mappedName=AwarderPointOfSalesRemote.MAPPED_NAME)
	AwarderPointOfSalesRemote aposEJB;

	public AwarderPointOfSalesResource() {
    }

    @GET
    @Path("/{aposId}")
	public Response get(@PathParam("aposId") Long aposId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderPointOfSaleBean>> constraintViolations = validator
				.validateValue(
					AwarderPointOfSaleBean.class,
	    				"aposId",
	    				aposId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AwarderPointOfSaleBean apos = aposEJB.find(aposId);
		GenericEntity<AwarderPointOfSaleBean> aposEntity = new GenericEntity<AwarderPointOfSaleBean>(apos) {};

		return Response.ok(aposEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(AwarderPointOfSaleBean apos) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderPointOfSaleBean>> constraintViolations = validator
				.validate(apos, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		AwarderPointOfSaleBean aposAdd = aposEJB.create(apos);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(AwarderPointOfSalesResource.class, "get")
					.build(aposAdd.getId());
		GenericEntity<AwarderPointOfSaleBean> oaEntity = new GenericEntity<AwarderPointOfSaleBean>(aposAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{aposId}")
	public Response put(@PathParam("aposId") Long aposId, AwarderPointOfSaleBean apos) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderPointOfSaleBean>> constraintViolations = validator
				.validateValue(
					AwarderPointOfSaleBean.class,
	    				"aposId",
	    				aposId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		apos.setId(aposId);
		constraintViolations = validator
				.validate(apos, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AwarderPointOfSaleBean aposUpdt = aposEJB.update(apos);
		GenericEntity<AwarderPointOfSaleBean> aposEntity = new GenericEntity<AwarderPointOfSaleBean>(aposUpdt) {};

		return Response.ok(aposEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{aposId}")
	public Response delete(@PathParam("aposId") Long aposId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AwarderPointOfSaleBean>> constraintViolations = validator
				.validateValue(
					AwarderPointOfSaleBean.class,
	    				"aposId",
	    				aposId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		aposEJB.delete(aposId);
		
		return Response.noContent().build();
    }

}