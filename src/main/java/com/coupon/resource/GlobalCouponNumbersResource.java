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

import com.coupon.common.bean.GlobalCouponNumberBean;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.session.remote.GlobalCouponNumbersRemote;
import com.coupon.common.utils.ValidatorHelper;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_GLOBAL_COUPON_NUMBERS)
@Produces(MediaType.APPLICATION_JSON)
public class GlobalCouponNumbersResource extends Resource {

	@EJB(mappedName=GlobalCouponNumbersRemote.MAPPED_NAME)
	GlobalCouponNumbersRemote gcnEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public GlobalCouponNumbersResource() {
    }

    @GET
	public Response getPage(@QueryParam("companyPrefix") Long companyPrefix,
							@DefaultValue(DEFAULT_PAGE_START) @QueryParam("start") Integer start,
							@DefaultValue(DEFAULT_PAGE_SIZE) @QueryParam("size") Integer size) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalCouponNumberBean.class,
	    				"companyPrefix",
	    				companyPrefix,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		List<GlobalCouponNumberBean> gcns = gcnEJB.findPage(companyPrefix,
													curePageStart(start),
													curePageSize(size));
		if (gcns == null) {
			throw new ResourceNotFoundException();
		}
		GenericEntity<List<GlobalCouponNumberBean>> gcnsEntity = new GenericEntity<List<GlobalCouponNumberBean>>(gcns) {};

		return Response.ok(gcnsEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{gcnId}")
	public Response get(@PathParam("gcnId") Long gcnId) {
    	
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalCouponNumberBean.class,
	    				"gcnId",
	    				gcnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalCouponNumberBean gcn = gcnEJB.find(gcnId);
		GenericEntity<GlobalCouponNumberBean> gcnEntity = new GenericEntity<GlobalCouponNumberBean>(gcn) {};

		return Response.ok(gcnEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/lookup")
	public Response lookup(@QueryParam("companyPrefix") Long companyPrefix,
							@QueryParam("couponReference") Integer couponReference,
							@QueryParam("serialComponent") Long serialComponent) {

    		GlobalCouponNumberBean gcnb = new GlobalCouponNumberBean(
				companyPrefix,
				couponReference,
				ValidatorHelper.computeCheckDigit(companyPrefix, couponReference.longValue()),
				serialComponent);
    		Validator validator = validatorFactory.getValidator();
    		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
    				.validate(gcnb, Default.class, PostValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalCouponNumberBean gcn = gcnEJB.findByNumber(gcnb);
		GenericEntity<GlobalCouponNumberBean> gcnEntity = new GenericEntity<GlobalCouponNumberBean>(gcn) {};

		return Response.ok(gcnEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(GlobalCouponNumberBean gcn) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
				.validate(gcn, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalCouponNumberBean gcnAdd = gcnEJB.create(gcn);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(GlobalCouponNumbersResource.class, "get")
					.build(gcnAdd.getId());
		GenericEntity<GlobalCouponNumberBean> gcnEntity = new GenericEntity<GlobalCouponNumberBean>(gcnAdd) {};

		return Response.created(uri).entity(gcnEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{gcnId}")
	public Response put(@PathParam("gcnId") Long gcnId, GlobalCouponNumberBean gcn) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalCouponNumberBean.class,
	    				"gcnId",
	    				gcnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		gcn.setId(gcnId);
		constraintViolations = validator
				.validate(gcn, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		GlobalCouponNumberBean gcnUpdt = gcnEJB.update(gcn);
		GenericEntity<GlobalCouponNumberBean> gcnEntity = new GenericEntity<GlobalCouponNumberBean>(gcnUpdt) {};

		return Response.ok(gcnEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{gcnId}")
	public Response delete(@PathParam("gcnId") Long gcnId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<GlobalCouponNumberBean>> constraintViolations = validator
				.validateValue(
					GlobalCouponNumberBean.class,
	    				"gcnId",
	    				gcnId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		gcnEJB.delete(gcnId);
		
		return Response.noContent().build();
    }

}
