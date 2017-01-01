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

import com.coupon.common.bean.MarketingMaterialBean;
import com.coupon.common.session.remote.MarketingMaterialsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_MARKETING_MATERIALS)
@Produces(MediaType.APPLICATION_JSON)
public class MarketingMaterialsResource extends Resource {

	@EJB(mappedName=MarketingMaterialsRemote.MAPPED_NAME)
	MarketingMaterialsRemote mmEJB;

	public MarketingMaterialsResource() {
    }

    @GET
    @Path("/{mmId}")
	public Response get(@PathParam("mmId") Long mmId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<MarketingMaterialBean>> constraintViolations = validator
				.validateValue(
					MarketingMaterialBean.class,
	    				"mmId",
	    				mmId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		MarketingMaterialBean mm = mmEJB.find(mmId);
		GenericEntity<MarketingMaterialBean> mmEntity = new GenericEntity<MarketingMaterialBean>(mm) {};

		return Response.ok(mmEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(MarketingMaterialBean mm) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<MarketingMaterialBean>> constraintViolations = validator
				.validate(mm, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		MarketingMaterialBean mmAdd = mmEJB.create(mm);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(MarketingMaterialsResource.class, "get")
					.build(mmAdd.getId());
		GenericEntity<MarketingMaterialBean> oaEntity = new GenericEntity<MarketingMaterialBean>(mmAdd) {};

		return Response.created(uri).entity(oaEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{mmId}")
	public Response put(@PathParam("mmId") Long mmId, MarketingMaterialBean mm) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<MarketingMaterialBean>> constraintViolations = validator
				.validateValue(
					MarketingMaterialBean.class,
	    				"mmId",
	    				mmId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		mm.setId(mmId);
		constraintViolations = validator
				.validate(mm, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		MarketingMaterialBean mmUpdt = mmEJB.update(mm);
		GenericEntity<MarketingMaterialBean> mmEntity = new GenericEntity<MarketingMaterialBean>(mmUpdt) {};

		return Response.ok(mmEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{mmId}")
	public Response delete(@PathParam("mmId") Long mmId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<MarketingMaterialBean>> constraintViolations = validator
				.validateValue(
					MarketingMaterialBean.class,
	    				"mmId",
	    				mmId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		mmEJB.delete(mmId);
		
		return Response.noContent().build();
    }

}