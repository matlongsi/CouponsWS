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

import com.coupon.common.FinancialSettlementDetail;
import com.coupon.common.bean.FinancialSettlementDetailBean;
import com.coupon.common.session.remote.FinancialSettlementDetailsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_FINANCIAL_SETTLEMENT_DETAILS)
@Produces(MediaType.APPLICATION_JSON)
public class FinancialSettlementDetailsResource extends Resource {

	@EJB(mappedName=FinancialSettlementDetailsRemote.MAPPED_NAME)
	FinancialSettlementDetailsRemote fsdEJB;

	public FinancialSettlementDetailsResource() {
    }

    @GET
    @Path("/{fsdId}")
	public Response get(@PathParam("fsdId") Long fsdId) {
    	
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FinancialSettlementDetailBean>> constraintViolations = validator
				.validateValue(
					FinancialSettlementDetailBean.class,
	    				"fsdId",
	    				fsdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		FinancialSettlementDetailBean fsd = fsdEJB.find(fsdId);
		GenericEntity<FinancialSettlementDetail> fsdEntity = new GenericEntity<FinancialSettlementDetail>(fsd) {};

		return Response.ok(fsdEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(FinancialSettlementDetailBean fsd) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FinancialSettlementDetailBean>> constraintViolations = validator
				.validate(fsd, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		FinancialSettlementDetailBean fsdAdd = fsdEJB.create(fsd);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(FinancialSettlementDetailsResource.class, "get")
					.build(fsdAdd.getId());
		GenericEntity<FinancialSettlementDetail> oaEntity = new GenericEntity<FinancialSettlementDetail>(fsdAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{fsdId}")
	public Response put(@PathParam("fsdId") Long fsdId, FinancialSettlementDetailBean fsd) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FinancialSettlementDetailBean>> constraintViolations = validator
				.validateValue(
					FinancialSettlementDetailBean.class,
	    				"fsdId",
	    				fsdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		fsd.setId(fsdId);
		constraintViolations = validator
				.validate(fsd, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		FinancialSettlementDetailBean fsdUpdt = fsdEJB.update(fsd);
		GenericEntity<FinancialSettlementDetail> fsdEntity = new GenericEntity<FinancialSettlementDetail>(fsdUpdt) {};

		return Response.ok(fsdEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{fsdId}")
	public Response delete(@PathParam("fsdId") Long fsdId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<FinancialSettlementDetailBean>> constraintViolations = validator
				.validateValue(
					FinancialSettlementDetailBean.class,
	    				"fsdId",
	    				fsdId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		fsdEJB.delete(fsdId);
		
		return Response.noContent().build();
    }

}