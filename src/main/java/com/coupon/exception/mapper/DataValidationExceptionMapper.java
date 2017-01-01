package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

import com.coupon.common.exception.DataValidationException;
import com.coupon.common.exception.ErrorMessage;


@Provider
public class DataValidationExceptionMapper implements ExceptionMapper<DataValidationException> {

	@Override
	public Response toResponse(DataValidationException ex) {

		ErrorMessage errorMessage = new ErrorMessage(
				ex.getErrorCode(),
				ex.getDocumentation());
		errorMessage.addErrorMessage(ex.getMessage());

		Logger.getLogger(DataValidationExceptionMapper.class.getName())
				.log(Level.SEVERE, this.getClass().getName(), ex);

		return Response.status(ex.getErrorCode().getValue())
				.entity(errorMessage)
				.build();
	}
	
	
}
