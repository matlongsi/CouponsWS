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

import com.coupon.common.bean.PurchaseTradeItemBean;
import com.coupon.common.session.remote.PurchaseTradeItemsRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;

@Path(Resource.PATH_PURCHASE_TRADE_ITEMS)
@Produces(MediaType.APPLICATION_JSON)
public class PurchaseTradeItemsResource extends Resource {

	@EJB(mappedName=PurchaseTradeItemsRemote.MAPPED_NAME)
	PurchaseTradeItemsRemote prtiEJB;

	public static final String DEFAULT_PAGE_SIZE = "10";

	public PurchaseTradeItemsResource() {
    }

    @GET
    @Path("/{ptiId}")
	public Response get(@PathParam("ptiId") Long ptiId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseTradeItemBean>> constraintViolations = validator
				.validateValue(
					PurchaseTradeItemBean.class,
	    				"ptiId",
	    				ptiId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		PurchaseTradeItemBean pti = prtiEJB.find(ptiId);
		GenericEntity<PurchaseTradeItemBean> ptiEntity = new GenericEntity<PurchaseTradeItemBean>(pti) {};

		return Response.ok(ptiEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(PurchaseTradeItemBean pti) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseTradeItemBean>> constraintViolations = validator
				.validate(pti, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		PurchaseTradeItemBean ptiAdd = prtiEJB.create(pti);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(PurchaseTradeItemsResource.class, "get")
					.build(ptiAdd.getId());
		GenericEntity<PurchaseTradeItemBean> ptiEntity = new GenericEntity<PurchaseTradeItemBean>(ptiAdd) {};

		return Response.created(uri).entity(ptiEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{ptiId}")
	public Response put(@PathParam("ptiId") Long ptiId, PurchaseTradeItemBean pti) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseTradeItemBean>> constraintViolations = validator
				.validateValue(
					PurchaseTradeItemBean.class,
	    				"ptiId",
	    				ptiId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		pti.setId(ptiId);
		constraintViolations = validator
				.validate(pti, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		PurchaseTradeItemBean ptiUpdt = prtiEJB.update(pti);
		GenericEntity<PurchaseTradeItemBean> ptiEntity = new GenericEntity<PurchaseTradeItemBean>(ptiUpdt) {};

		return Response.ok(ptiEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{ptiId}")
	public Response delete(@PathParam("ptiId") Long ptiId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PurchaseTradeItemBean>> constraintViolations = validator
				.validateValue(
					PurchaseTradeItemBean.class,
	    				"ptiId",
	    				ptiId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		prtiEJB.delete(ptiId);
		
		return Response.noContent().build();
    }

}
