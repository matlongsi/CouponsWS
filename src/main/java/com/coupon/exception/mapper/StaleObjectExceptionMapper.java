package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.StaleObjectException;


@Provider
public class StaleObjectExceptionMapper implements ExceptionMapper<StaleObjectException> {

	@Override
	public Response toResponse(StaleObjectException ex) {

		ErrorMessage errorMessage = new ErrorMessage(
				ex.getErrorCode(),
				ex.getDocumentation());
		errorMessage.addErrorMessage(ex.getMessage());

		Logger.getLogger(StaleObjectExceptionMapper.class.getName())
			.log(Level.SEVERE, this.getClass().getName(), ex);

		return Response.status(Status.CONFLICT)
				.entity(errorMessage)
				.build();
	}
	
	
}
