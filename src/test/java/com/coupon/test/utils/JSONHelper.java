package com.coupon.test.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import com.coupon.common.bean.Bean;

public class JSONHelper<T extends Bean> {

	private Class<T> beanClass;

	public JSONHelper(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	@SuppressWarnings("unchecked")
	public List<T> arrayFromJson(String fileName) throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance("com.coupon.common.bean");
		Unmarshaller um = jaxbContext.createUnmarshaller();
		um.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		um.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		um.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);

		return (List<T>)um.unmarshal(
							new StreamSource(new File(fileName)),
							beanClass)
						.getValue();
	}

	public T objectFromJson(String fileName) throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance("com.coupon.common.bean");
		Unmarshaller um = jaxbContext.createUnmarshaller();
		um.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		um.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		um.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);

		return um.unmarshal(
						new StreamSource(new File(fileName)),
						beanClass)
					.getValue();
	}

	public String jsonFromObject(Object obj) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Marshaller m = JAXBContext
					.newInstance("com.coupon.common.bean")
					.createMarshaller();
			m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			m.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
			m.marshal(obj, baos);
		}
		catch (JAXBException ex) {
			return null;
		}
		
		return baos.toString();
	}

	public String jsonFromObjectWOValidation(Object obj) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Marshaller m = JAXBContext
					.newInstance(this.beanClass)
					.createMarshaller();
			m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			m.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, Bean.JSON_TYPE_PREFIX);
			m.setProperty(MarshallerProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE);
			m.marshal(obj, baos);
		}
		catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}
		
		return baos.toString();
	}
}
