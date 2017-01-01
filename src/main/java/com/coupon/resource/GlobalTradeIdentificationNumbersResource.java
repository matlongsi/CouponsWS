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

import com.coupon.common.bean.GlobalTradeIdentificationNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.session.remote.GlobalTradeIdentificationNumbersRemote;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS)
@Produces(MediaType.APPLICATION_JSON)
public class GlobalTradeIdentificationNumbersResource extends Resource {

	@EJB(mappedName=GlobalTradeIdentificationNumbersRemote.MAPPED_NAME)
	GlobalTradeIdentificationNumbersRemote gtinEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public GlobalTradeIdentificationNumbersResource() {
    }

    @GET
	public Response getPage(
						@QueryParam("companyPrefix") Long companyPrefix,
						@DefaultValue(DEFAULT_PAGE_START) @QueryParam("start") int start,
						@DefaultValue(DEFAULT_PAGE_SIZE) @QueryParam("size") int size) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalTradeIdentificationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalTradeIdentificationNumberBean.class,
	    				"companyPrefix",
	    				companyPrefix,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		List<GlobalTradeIdentificationNumberBean> gtins = gtinEJB.findPage(companyPrefix,
																curePageStart(start),
																curePageSize(size));
		if (gtins == null) {
			throw new ResourceNotFoundException();
		}
		GenericEntity<List<GlobalTradeIdentificationNumberBean>> gtinsEntity = new GenericEntity<List<GlobalTradeIdentificationNumberBean>>(gtins) {};

		return Response.ok(gtinsEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{gtinId}")
	public Response get(@PathParam("gtinId") Long gtinId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalTradeIdentificationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalTradeIdentificationNumberBean.class,
	    				"gtinId",
	    				gtinId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalTradeIdentificationNumberBean gtin = gtinEJB.find(gtinId);
		GenericEntity<GlobalTradeIdentificationNumberBean> gtinEntity = new GenericEntity<GlobalTradeIdentificationNumberBean>(gtin) {};

		return Response.ok(gtinEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/lookup")
	public Response lookup(@QueryParam("companyPrefix") Long companyPrefix,
							@QueryParam("itemReference") Integer itemReference) {
    	
    		GlobalTradeIdentificationNumberBean gtinb = new GlobalTradeIdentificationNumberBean(
				companyPrefix,
				itemReference,
				ValidatorHelper.computeCheckDigit(companyPrefix, itemReference.longValue()));
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalTradeIdentificationNumberBean>> constraintViolations = validator
				.validate(gtinb, Default.class, PostValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalTradeIdentificationNumberBean gtin = gtinEJB.findByNumber(gtinb);
		GenericEntity<GlobalTradeIdentificationNumberBean> gtinEntity = new GenericEntity<GlobalTradeIdentificationNumberBean>(gtin) {};

		return Response.ok(gtinEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(GlobalTradeIdentificationNumberBean gtin) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalTradeIdentificationNumberBean>> constraintViolations = validator
				.validate(gtin, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		GlobalTradeIdentificationNumberBean gtinAdd = gtinEJB.create(gtin);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(GlobalTradeIdentificationNumbersResource.class, "get")
					.build(gtinAdd.getId());
		GenericEntity<GlobalTradeIdentificationNumberBean> gtinEntity = new GenericEntity<GlobalTradeIdentificationNumberBean>(gtinAdd) {};

		return Response.created(uri).entity(gtinEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{gtinId}")
	public Response put(@PathParam("gtinId") Long gtinId, GlobalTradeIdentificationNumberBean gtin) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalTradeIdentificationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalTradeIdentificationNumberBean.class,
	    				"gtinId",
	    				gtinId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		gtin.setId(gtinId);
		constraintViolations = validator
				.validate(gtin, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalTradeIdentificationNumberBean gtinUpdt = gtinEJB.update(gtin);
		GenericEntity<GlobalTradeIdentificationNumberBean> gtinEntity = new GenericEntity<GlobalTradeIdentificationNumberBean>(gtinUpdt) {};

		return Response.ok(gtinEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{gtinId}")
	public Response delete(@PathParam("gtinId") Long gtinId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalTradeIdentificationNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalTradeIdentificationNumberBean.class,
	    				"gtinId",
	    				gtinId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		gtinEJB.delete(gtinId);
		
		return Response.noContent().build();
    }

}