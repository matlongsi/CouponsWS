package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.GeneralApplicationErrorException;


@Provider
public class GeneralApplicationErrorExceptionMapper implements ExceptionMapper<GeneralApplicationErrorException> {

	@Override public Response toResponse(GeneralApplicationErrorException ex) {

		Logger.getLogger(GeneralApplicationErrorExceptionMapper.class.getName())
				.log(Level.SEVERE, this.getClass().getName(), ex);

		ErrorMessage errorMessage = new ErrorMessage(
				ex.getErrorCode(),
				ex.getDocumentation());
		errorMessage.addErrorMessage(GeneralApplicationErrorException.DEFAULT_MESSAGE);

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(errorMessage)
				.build();
	}

}
