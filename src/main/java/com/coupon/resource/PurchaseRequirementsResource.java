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

import com.coupon.common.bean.PurchaseRequirementBean;
import com.coupon.common.session.remote.PurchaseRequirementsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_PURCHASE_REQUIREMENTS)
@Produces(MediaType.APPLICATION_JSON)
public class PurchaseRequirementsResource extends Resource {

	@EJB(mappedName=PurchaseRequirementsRemote.MAPPED_NAME)
	PurchaseRequirementsRemote prEJB;

	public PurchaseRequirementsResource() {
    }

    @GET
    @Path("/{prId}")
	public Response get(@PathParam("prId") Long prId) {
 
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseRequirementBean>> constraintViolations = validator
				.validateValue(
					PurchaseRequirementBean.class,
	    				"prId",
	    				prId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		PurchaseRequirementBean pr = prEJB.find(prId);
		GenericEntity<PurchaseRequirementBean> prEntity = new GenericEntity<PurchaseRequirementBean>(pr) {};

		return Response.ok(prEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(PurchaseRequirementBean pr) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseRequirementBean>> constraintViolations = validator
				.validate(pr, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		PurchaseRequirementBean prAdd = prEJB.create(pr);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(PurchaseRequirementsResource.class, "get")
					.build(prAdd.getId());
		GenericEntity<PurchaseRequirementBean> oaEntity = new GenericEntity<PurchaseRequirementBean>(prAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{prId}")
	public Response put(@PathParam("prId") Long prId, PurchaseRequirementBean pr) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseRequirementBean>> constraintViolations = validator
				.validateValue(
					PurchaseRequirementBean.class,
	    				"prId",
	    				prId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		pr.setId(prId);
		constraintViolations = validator
				.validate(pr, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		PurchaseRequirementBean prUpdt = prEJB.update(pr);
		GenericEntity<PurchaseRequirementBean> prEntity = new GenericEntity<PurchaseRequirementBean>(prUpdt) {};

		return Response.ok(prEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{prId}")
	public Response delete(@PathParam("prId") Long prId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseRequirementBean>> constraintViolations = validator
				.validateValue(
					PurchaseRequirementBean.class,
	    				"prId",
	    				prId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		prEJB.delete(prId);
		
		return Response.noContent().build();
    }

}
