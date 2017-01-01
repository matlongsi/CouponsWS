package com.coupon.resource;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.ResourceConfig;


@ApplicationPath("/")
public class Resource extends ResourceConfig {

	@Context
	protected UriInfo uriInfo;

	protected static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

	public static final String PATH_OFFERS = "Offers/";
	public static final String PATH_GLOBAL_LOCATION_NUMBERS = "GlobalLocationNumbers/";
	public static final String PATH_GLOBAL_COUPON_NUMBERS = "GlobalCouponNumbers/";
	public static final String PATH_GLOBAL_TRADE_IDENTIFICATION_NUMBERS = "GlobalTradeIdentificationNumbers/";
	public static final String PATH_GLOBAL_SERVICE_RELATION_NUMBERS = "GlobalServiceRelationNumbers/";
	public static final String PATH_AWARDER_DETAILS = "AwarderDetails/";
	public static final String PATH_DISTRIBUTION_DETAILS = "DistributionDetails/";
	public static final String PATH_MARKETING_MATERIALS = "MarketingMaterials/";
	public static final String PATH_FINANCIAL_SETTLEMENT_DETAILS = "FinancialSettlementDetails/";
	public static final String PATH_USAGE_CONDITIONS = "UsageConditions/";
	public static final String PATH_REWARDS = "Rewards/";
	public static final String PATH_PURCHASE_REQUIREMENTS = "PurchaseRequirements/";
	public static final String PATH_AWARDER_POINT_OF_SALES = "AwarderPointOfSales/";
	public static final String PATH_REDEMPTION_PERIODS = "RedemptionPeriods/";
	public static final String PATH_ACQUISITION_PERIODS = "AcquisitionPeriods/";
	public static final String PATH_ARTWORKS = "Artworks/";
	public static final String PATH_FILE_CONTENT_DESCRIPTIONS = "FileContentDescriptions/";
	public static final String PATH_LEGAL_STATEMENTS = "LegalStatements/";
	public static final String PATH_LONG_DESCRIPTIONS = "LongDescriptions/";
	public static final String PATH_PRODUCT_CATEGORIES = "ProductCategories/";
	public static final String PATH_SHORT_DESCRIPTIONS = "ShortDescriptions/";
	public static final String PATH_PURCHASE_TRADE_ITEMS = "PurchaseTradeItems/";
	public static final String PATH_REWARD_TRADE_ITEMS = "RewardTradeItems/";
	public static final String PATH_REWARD_LOYALTY_POINTS = "RewardLoyaltyPoints/";

	public static final String DEFAULT_PAGE_START = "1";
	public static Integer MAX_PAGE_SIZE = 25;
	public static Integer STANDARD_PAGE_SIZE = 10;

	protected int curePageStart(int start) {

		int pageStart = 0;				//JPA uses 0 for starting position of record
		
		if (start > 0) {
			pageStart = start - 1;		//subtract 1 since JPA uses 0 for starting position of record
		}

		return pageStart;
	}
	
	protected int curePageSize(int size) {

		int pageSize = STANDARD_PAGE_SIZE;
			
	    	if (size > MAX_PAGE_SIZE) {
	    		pageSize = MAX_PAGE_SIZE;
	    	}
			
		return pageSize;
	}

	public Resource() {
	}
}
