package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.ResourceNotFoundException;


@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

	@Override
	public Response toResponse(ResourceNotFoundException ex) {

		ErrorMessage errorMessage = new ErrorMessage(
				ex.getErrorCode(),
				ex.getDocumentation());
		errorMessage.addErrorMessage(ex.getMessage());

		Logger.getLogger(ResourceNotFoundExceptionMapper.class.getName())
			.log(Level.SEVERE, this.getClass().getName(), ex);

		return Response.status(Status.NOT_FOUND)
				.entity(errorMessage)
				.build();
	}

}