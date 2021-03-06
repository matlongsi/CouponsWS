package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.ResourceConflictException;


@Provider
public class ResourceConflictExceptionMapper implements ExceptionMapper<ResourceConflictException> {

	@Override
	public Response toResponse(ResourceConflictException ex) {

		ErrorMessage errorMessage = new ErrorMessage(
				ex.getErrorCode(),
				ex.getDocumentation());
		errorMessage.addErrorMessage(ex.getMessage());

		Logger.getLogger(ResourceConflictExceptionMapper.class.getName())
			.log(Level.SEVERE, this.getClass().getName(), ex);

		return Response.status(Status.CONFLICT)
				.entity(errorMessage)
				.build();
	}

}