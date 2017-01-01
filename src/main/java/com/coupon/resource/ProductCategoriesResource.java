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

import com.coupon.common.bean.ProductCategoryBean;
import com.coupon.common.session.remote.ProductCategoriesRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_PRODUCT_CATEGORIES)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCategoriesResource extends Resource {

	@EJB(mappedName=ProductCategoriesRemote.MAPPED_NAME)
	ProductCategoriesRemote pcEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public ProductCategoriesResource() {
    }

    @GET
    @Path("/{pcId}")
	public Response get(@PathParam("pcId") Long pcId) {
    	
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ProductCategoryBean>> constraintViolations = validator
				.validateValue(
					ProductCategoryBean.class,
	    				"pcId",
	    				pcId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ProductCategoryBean pc = pcEJB.find(pcId);
		GenericEntity<ProductCategoryBean> pcEntity = new GenericEntity<ProductCategoryBean>(pc) {};

		return Response.ok(pcEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(ProductCategoryBean pc) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ProductCategoryBean>> constraintViolations = validator
				.validate(pc, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		ProductCategoryBean pcAdd = pcEJB.create(pc);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(ProductCategoriesResource.class, "get")
					.build(pcAdd.getId());
		GenericEntity<ProductCategoryBean> pcEntity = new GenericEntity<ProductCategoryBean>(pcAdd) {};

		return Response.created(uri).entity(pcEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{pcId}")
	public Response put(@PathParam("pcId") Long pcId, ProductCategoryBean pc) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ProductCategoryBean>> constraintViolations = validator
				.validateValue(
					ProductCategoryBean.class,
	    				"pcId",
	    				pcId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		pc.setId(pcId);
		constraintViolations = validator
				.validate(pc, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
	
		ProductCategoryBean pcUpdt = pcEJB.update(pc);
		GenericEntity<ProductCategoryBean> pcEntity = new GenericEntity<ProductCategoryBean>(pcUpdt) {};

		return Response.ok(pcEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{pcId}")
	public Response delete(@PathParam("pcId") Long pcId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ProductCategoryBean>> constraintViolations = validator
				.validateValue(
					ProductCategoryBean.class,
	    				"pcId",
	    				pcId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		pcEJB.delete(pcId);
		
		return Response.noContent().build();
    }

}
