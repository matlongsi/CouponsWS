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

import com.coupon.common.bean.ArtworkBean;
import com.coupon.common.session.remote.ArtworksRemote;
import com.coupon.common.validator.PostValidate;
import com.coupon.common.validator.PutValidate;
import com.coupon.common.validator.RootValidate;


@Path(Resource.PATH_ARTWORKS)
@Produces(MediaType.APPLICATION_JSON)
public class ArtworksResource extends Resource {

	@EJB(mappedName=ArtworksRemote.MAPPED_NAME)
	ArtworksRemote aEJB;

	public ArtworksResource() {
    }

    @GET
    @Path("/{aId}")
	public Response get(@PathParam("aId") Long aId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ArtworkBean>> constraintViolations = validator
				.validateValue(
					ArtworkBean.class,
	    				"aId",
	    				aId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ArtworkBean a = aEJB.find(aId);
		GenericEntity<ArtworkBean> oaEntity = new GenericEntity<ArtworkBean>(a) {};

		return Response.ok(oaEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response post(ArtworkBean a) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ArtworkBean>> constraintViolations = validator
				.validate(a, Default.class, PostValidate.class, RootValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		ArtworkBean aAdd = aEJB.create(a);
		URI uri = uriInfo.getAbsolutePathBuilder()
					.path(ArtworksResource.class, "get")
					.build(aAdd.getId());
		GenericEntity<ArtworkBean> aEntity = new GenericEntity<ArtworkBean>(aAdd) {};

		return Response.created(uri).entity(aEntity).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{aId}")
	public Response put(@PathParam("aId") Long aId, ArtworkBean a) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ArtworkBean>> constraintViolations = validator
				.validateValue(
					ArtworkBean.class,
	    				"aId",
	    				aId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}
		a.setId(aId);
		constraintViolations = validator
				.validate(a, Default.class, PutValidate.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		ArtworkBean aUpdt = aEJB.update(a);
		GenericEntity<ArtworkBean> aEntity = new GenericEntity<ArtworkBean>(aUpdt) {};

		return Response.ok(aEntity, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{aId}")
	public Response delete(@PathParam("aId") Long aId) {

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ArtworkBean>> constraintViolations = validator
				.validateValue(
					ArtworkBean.class,
	    				"aId",
	    				aId,
	    				Default.class);
		if (constraintViolations.size() > 0) {
			throw new ConstraintViolationException(constraintViolations);
		}

		aEJB.delete(aId);
		
		return Response.noContent().build();
    }

}
