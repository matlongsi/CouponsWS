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

import com.coupon.common.bean.AcquisitionPeriodBean;
import com.coupon.common.session.remote.AcquisitionPeriodsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_ACQUISITION_PERIODS)
@Produces(MediaType.APPLICATION_JSON)
public class AcquisitionPeriodsResource extends Resource {

	@EJB(mappedName=AcquisitionPeriodsRemote.MAPPED_NAME)
	AcquisitionPeriodsRemote apEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public AcquisitionPeriodsResource() {
    }

    @GET
    @Path("/{apId}")
	public Response get(@PathParam("apId") Long apId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AcquisitionPeriodBean>> constraintViolations = validator
				.validateValue(
					AcquisitionPeriodBean.class,
	    				"apId",
	    				apId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AcquisitionPeriodBean ap = apEJB.find(apId);
		GenericEntity<AcquisitionPeriodBean> apEntity = new GenericEntity<AcquisitionPeriodBean>(ap) {};

		return Response.ok(apEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(AcquisitionPeriodBean ap) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AcquisitionPeriodBean>> constraintViolations = validator
				.validate(ap, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AcquisitionPeriodBean apAdd = apEJB.create(ap);		
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(AcquisitionPeriodsResource.class, "get")
					.build(apAdd.getId());
		GenericEntity<AcquisitionPeriodBean> apEntity = new GenericEntity<AcquisitionPeriodBean>(apAdd) {};

		return Response.created(uri).entity(apEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{apId}")
	public Response put(@PathParam("apId") Long apId,
						AcquisitionPeriodBean ap) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AcquisitionPeriodBean>> constraintViolations = validator
				.validateValue(
					AcquisitionPeriodBean.class,
	    				"apId",
	    				apId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		ap.setId(apId);
		constraintViolations = validator
				.validate(ap, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		AcquisitionPeriodBean apUpdt = apEJB.update(ap);
		GenericEntity<AcquisitionPeriodBean> apEntity = new GenericEntity<AcquisitionPeriodBean>(apUpdt) {};

		return Response.ok(apEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{apId}")
	public Response delete(@PathParam("apId") Long apId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<AcquisitionPeriodBean>> constraintViolations = validator
				.validateValue(
					AcquisitionPeriodBean.class,
	    				"apId",
	    				apId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		apEJB.delete(apId);
		
		return Response.noContent().build();
    }

}