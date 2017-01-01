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

import com.coupon.common.LegalStatement;
import com.coupon.common.bean.LegalStatementBean;
import com.coupon.common.session.remote.LegalStatementsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_LEGAL_STATEMENTS)
@Produces(MediaType.APPLICATION_JSON)
public class LegalStatementsResource extends Resource {

	@EJB(mappedName=LegalStatementsRemote.MAPPED_NAME)
	LegalStatementsRemote lsEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public LegalStatementsResource() {
    }

    @GET
    @Path("/{lsId}")
	public Response get(@PathParam("lsId") Long lsId) {
    	
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LegalStatementBean>> constraintViolations = validator
				.validateValue(
					LegalStatementBean.class,
	    				"lsId",
	    				lsId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		LegalStatementBean ls = lsEJB.find(lsId);
		GenericEntity<LegalStatementBean> lsEntity = new GenericEntity<LegalStatementBean>(ls) {};

		return Response.ok(lsEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(LegalStatementBean ls) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LegalStatementBean>> constraintViolations = validator
				.validate(ls, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		LegalStatementBean lsAdd = lsEJB.create(ls);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(LegalStatementsResource.class, "get")
					.build(lsAdd.getId());
		GenericEntity<LegalStatement> lsEntity = new GenericEntity<LegalStatement>(lsAdd) {};

		return Response.created(uri).entity(lsEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{lsId}")
	public Response put(@PathParam("lsId") Long lsId, LegalStatementBean ls) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LegalStatementBean>> constraintViolations = validator
				.validateValue(
					LegalStatementBean.class,
	    				"lsId",
	    				lsId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		ls.setId(lsId);
		constraintViolations = validator
				.validate(ls, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		LegalStatementBean lsUpdt = lsEJB.update(ls);
		GenericEntity<LegalStatementBean> lsEntity = new GenericEntity<LegalStatementBean>(lsUpdt) {};

		return Response.ok(lsEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{lsId}")
	public Response delete(@PathParam("lsId") Long lsId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<LegalStatementBean>> constraintViolations = validator
				.validateValue(
					LegalStatementBean.class,
	    				"lsId",
	    				lsId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		lsEJB.delete(lsId);
		
		return Response.noContent().build();
    }

}
