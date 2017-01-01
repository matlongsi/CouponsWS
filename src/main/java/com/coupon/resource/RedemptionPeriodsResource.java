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

import com.coupon.common.RedemptionPeriod;
import com.coupon.common.bean.RedemptionPeriodBean;
import com.coupon.common.session.remote.RedemptionPeriodsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_REDEMPTION_PERIODS)
@Produces(MediaType.APPLICATION_JSON)
public class RedemptionPeriodsResource extends Resource {

	@EJB(mappedName=RedemptionPeriodsRemote.MAPPED_NAME)
	RedemptionPeriodsRemote rpEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public RedemptionPeriodsResource() {
    }

    @GET
    @Path("/{rpId}")
	public Response get(@PathParam("rpId") Long rpId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RedemptionPeriodBean>> constraintViolations = validator
				.validateValue(
					RedemptionPeriodBean.class,
	    				"rpId",
	    				rpId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		RedemptionPeriodBean rp = rpEJB.find(rpId);
		GenericEntity<RedemptionPeriod> rpEntity = new GenericEntity<RedemptionPeriod>(rp) {};

		return Response.ok(rpEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(RedemptionPeriodBean rp) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RedemptionPeriodBean>> constraintViolations = validator
				.validate(rp, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		RedemptionPeriodBean rpAdd = rpEJB.create(rp);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(RedemptionPeriodsResource.class, "get")
					.build(rpAdd.getId());
		GenericEntity<RedemptionPeriodBean> rpEntity = new GenericEntity<RedemptionPeriodBean>(rpAdd) {};

		return Response.created(uri).entity(rpEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{rpId}")
	public Response put(@PathParam("rpId") Long rpId, RedemptionPeriodBean rp) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RedemptionPeriodBean>> constraintViolations = validator
				.validateValue(
					RedemptionPeriodBean.class,
	    				"rpId",
	    				rpId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		rp.setId(rpId);
		constraintViolations = validator
				.validate(rp, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
	
		RedemptionPeriodBean rpUpdt = rpEJB.update(rp);
		GenericEntity<RedemptionPeriod> rpEntity = new GenericEntity<RedemptionPeriod>(rpUpdt) {};

		return Response.ok(rpEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{rpId}")
	public Response delete(@PathParam("rpId") Long rpId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<RedemptionPeriodBean>> constraintViolations = validator
				.validateValue(
					RedemptionPeriodBean.class,
	    				"rpId",
	    				rpId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		rpEJB.delete(rpId);
		
		return Response.noContent().build();
    }

}
