package com.coupon.exception.mapper;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.ErrorStatusCode;


@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException ex) {

		ErrorMessage errorMessage = new ErrorMessage(
				ErrorStatusCode.CLIENT_ERROR_CONFLICT,
				"coupon documentation");

		Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
		for(ConstraintViolation<?> violation : violations) {
			errorMessage.addErrorMessage(violation.getMessage());
		}
		
		Logger.getLogger(ConstraintViolationExceptionMapper.class.getName())
				.log(Level.SEVERE, this.getClass().getName(), ex);

		return Response.status(Status.BAD_REQUEST)
				.entity(errorMessage)
				.build();
	}
	
}
