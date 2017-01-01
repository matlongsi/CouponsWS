package com.coupon.resource;


import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

import com.coupon.common.bean.Bean;


@Provider
public class MOXyJsonContextResolver implements ContextResolver<MoxyJsonConfig> {

	private final MoxyJsonConfig config;
    
    public MOXyJsonContextResolver() {

	    	this.config = new MoxyJsonConfig()
	    			.setAttributePrefix(Bean.JSON_TYPE_PREFIX);
    }
 
    @Override
    public MoxyJsonConfig getContext(Class<?> objectType) {

        return config;
    }

}
