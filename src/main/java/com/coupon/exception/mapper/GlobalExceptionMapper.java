package com.coupon.exception.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

import com.coupon.common.exception.GeneralApplicationErrorException;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

	@Context
	private Providers providers;

	@Override public Response toResponse(Throwable ex) {

		Logger.getLogger(GlobalExceptionMapper.class.getName())
				.log(Level.SEVERE, this.getClass().getName(), ex);

		return providers.getExceptionMapper(GeneralApplicationErrorException.class)
				.toResponse(new GeneralApplicationErrorException(ex.getMessage()));
	}

}