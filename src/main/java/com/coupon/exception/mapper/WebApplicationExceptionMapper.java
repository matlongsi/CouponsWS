package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.UnmarshalException;

import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.ErrorStatusCode;
import com.coupon.common.exception.GeneralApplicationErrorException;
import com.coupon.common.utils.ExceptionHelper;


@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Context
	private Providers providers;
	
	@Override public Response toResponse(WebApplicationException ex) {

		Logger.getLogger(WebApplicationExceptionMapper.class.getName())
				.log(Level.SEVERE, this.getClass().getName(), ex);

		UnmarshalException ux = ExceptionHelper.unrollException(ex, UnmarshalException.class);
		if (ux != null) {

			ErrorMessage errorMessage = new ErrorMessage(
					ErrorStatusCode.CLIENT_BAD_REQUEST,
					"coupon documentation");
			errorMessage.addErrorMessage("An error occurred unmarshalling the document");

			return Response.status(Status.BAD_REQUEST)
					.entity(errorMessage)
					.build();
		}

		//number format exception are likely to occur at this stage
		//due to invalid query parameters
		NumberFormatException nfx = ExceptionHelper.unrollException(ex, NumberFormatException.class);
		if (nfx != null) {

			ErrorMessage errorMessage = new ErrorMessage(
					ErrorStatusCode.CLIENT_BAD_REQUEST,
					"coupon documentation");
			errorMessage.addErrorMessage("An error occured processing the query parameters");

			return Response.status(Status.BAD_REQUEST)
					.entity(errorMessage)
					.build();
		}

		NotAllowedException nax = ExceptionHelper.unrollException(ex, NotAllowedException.class);
		if (nax != null) {

			ErrorMessage errorMessage = new ErrorMessage(
					ErrorStatusCode.METHOD_NOT_ALLOWED,
					"coupon documentation");
			errorMessage.addErrorMessage("Method not allowed on resource");

			return Response.status(Status.METHOD_NOT_ALLOWED)
					.entity(errorMessage)
					.build();
		}
		
		return providers.getExceptionMapper(GeneralApplicationErrorException.class)
				.toResponse(new GeneralApplicationErrorException(ex.getMessage()));
	}

}