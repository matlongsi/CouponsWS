package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

import com.coupon.common.exception.DataValidationException;
import com.coupon.common.exception.GeneralApplicationErrorException;
import com.coupon.common.exception.ResourceNotFoundException;
import com.coupon.common.utils.ExceptionHelper;


@Provider
public class EJBTransactionRolledbackExceptionMapper implements ExceptionMapper<EJBTransactionRolledbackException> {

	@Context
	private Providers providers;
	
	@Override public Response toResponse(EJBTransactionRolledbackException ex) {

		Logger.getLogger(EJBTransactionRolledbackExceptionMapper.class.getName())
				.log(Level.SEVERE, this.getClass().getName(), ex);

		ResourceNotFoundException rnfCause = ExceptionHelper
				.unrollException(ex, ResourceNotFoundException.class);
		if (rnfCause != null) {
			return providers.getExceptionMapper(ResourceNotFoundException.class)
				.toResponse(rnfCause);
		}

		DataValidationException dvCause = ExceptionHelper
				.unrollException(ex, DataValidationException.class);
		if (dvCause != null) {
			return providers.getExceptionMapper(DataValidationException.class)
				.toResponse(dvCause);
		}
		
		return providers.getExceptionMapper(GeneralApplicationErrorException.class)
				.toResponse(new GeneralApplicationErrorException(ex.getMessage()));
	}

}