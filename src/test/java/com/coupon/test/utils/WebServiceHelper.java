package com.coupon.test.utils;

import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

import com.coupon.common.bean.Bean;
import com.coupon.common.exception.DataValidationException;
import com.coupon.common.exception.ErrorMessage;
import com.coupon.common.exception.ErrorStatusCode;
import com.coupon.common.exception.GeneralApplicationErrorException;
import com.coupon.common.exception.ResourceConflictException;
import com.coupon.common.exception.ResourceNotFoundException;


public class WebServiceHelper<T extends Bean> {

	public final static String TARGET_URL = "http://localhost:8080/CouponsWS/rest/";

	private Class<T> beanClass;
	private String path;
	
	public WebServiceHelper(Class<T> beanClass, String path) {

		this.beanClass = beanClass;
		this.path = path;
	}

	public T getBean(long id) {

		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		Response response = client
				.target(TARGET_URL)
				.path(this.path + id)
				.request(MediaType.APPLICATION_JSON)
				.get();
		if (response.getStatus() == 200) {
			return response.readEntity(this.beanClass);
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}

	public T lookupBean(Map<String, String> paramsMap) {

		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		WebTarget target = client
				.target(TARGET_URL)
				.path(this.path + "lookup");
		for (String key : paramsMap.keySet()) {
			target = target.queryParam(key, paramsMap.get(key));
		}

		Response response = target.request(MediaType.APPLICATION_JSON).get();
		if (response.getStatus() == 200) {
			return response.readEntity(this.beanClass);
		}

		if (response.getStatus() == ErrorStatusCode.RESOURCE_NOT_FOUND.getValue()) {
			response.close();
			return null;
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}

	public T postBean(T bn) {

		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		Response response = client
				.target(TARGET_URL)
				.path(this.path)
				.request()
				.post(Entity.entity(bn, MediaType.APPLICATION_JSON));
		if (response.getStatus() == 201) {
			return response.readEntity(this.beanClass);
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}

	public T postRaw(T bn) {

		String json = new JSONHelper<T>(this.beanClass)
				.jsonFromObjectWOValidation(bn);
		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		Response response = client
				.target(TARGET_URL)
				.path(this.path)
				.request()
				.post(Entity.json(json));
		if (response.getStatus() == 201) {
			return response.readEntity(this.beanClass);
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}

	public T putBean(T bn) {

		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		Response response = client
				.target(TARGET_URL)
				.path(this.path + bn.getId())
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(bn, MediaType.APPLICATION_JSON));
		if (response.getStatus() == 200) {
			return response.readEntity(this.beanClass);
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}
	
	public T putRaw(T bn) {

		String json = new JSONHelper<T>(this.beanClass)
				.jsonFromObjectWOValidation(bn);
		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		Response response = client
				.target(TARGET_URL)
				.path(this.path + bn.getId())
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.json(json));
		if (response.getStatus() == 200) {
			return response.readEntity(this.beanClass);
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}

	public void deleteBean(long id) {

		Client client = ClientBuilder.newClient()
				.property(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
		Response response = client
				.target(TARGET_URL)
				.path(this.path + id)
				.request(MediaType.APPLICATION_JSON)
				.delete();
		if (response.getStatus() == 204) {
			response.close();
			return;
		}

		throw getException(response.readEntity(ErrorMessage.class));
	}

	private RuntimeException getException(ErrorMessage em) {
		
		switch(em.getErrorCode()) {

		case CLIENT_BAD_REQUEST:
			return new BadRequestException(em.getErrorMessages().get(0));
		
		case CLIENT_ERROR_CONFLICT:
			return new ResourceConflictException(em.getErrorMessages().get(0));
		
		case CLIENT_ERROR_DATA_VALIDATION:
			return new DataValidationException(em.getErrorMessages().get(0));
		
		case METHOD_NOT_ALLOWED:
			return new NotAllowedException(em.getErrorMessages().get(0));

		case RESOURCE_NOT_FOUND:
			return new ResourceNotFoundException(em.getErrorMessages().get(0));

		case INTERNAL_SERVER_ERROR:
			return new GeneralApplicationErrorException(em.getErrorMessages().get(0));
		}

		return null;
	}
}
