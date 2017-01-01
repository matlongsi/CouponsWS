package com.coupon.test.suite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.coupon.test.error.AcquisitionPeriodTest;
import com.coupon.test.error.ArtworkTest;
import com.coupon.test.error.AwarderDetailTest;
import com.coupon.test.error.AwarderPointOfSaleTest;
import com.coupon.test.error.DistributionDetailTest;
import com.coupon.test.error.FinancialSettlementDetailTest;
import com.coupon.test.error.GlobalCouponNumberTest;
import com.coupon.test.error.GlobalLocationNumberTest;
import com.coupon.test.error.GlobalServiceRelationNumberTest;
import com.coupon.test.error.GlobalTradeIdentificationNumberTest;
import com.coupon.test.error.LegalStatementTest;
import com.coupon.test.error.LongDescriptionTest;
import com.coupon.test.error.MarketingMaterialTest;
import com.coupon.test.error.OfferTest;
import com.coupon.test.error.ProductCategoryTest;
import com.coupon.test.error.PurchaseRequirementTest;
import com.coupon.test.error.PurchaseTradeItemTest;
import com.coupon.test.error.RedemptionPeriodTest;
import com.coupon.test.error.RewardTest;
import com.coupon.test.error.RewardTradeItemTest;
import com.coupon.test.error.ShortDescriptionTest;
import com.coupon.test.error.UsageConditionTest;
import com.coupon.test.error.FileContentDescriptionTest;
import com.coupon.test.utils.TestCondition;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	GlobalLocationNumberTest.class,
	GlobalTradeIdentificationNumberTest.class,
	GlobalServiceRelationNumberTest.class,
	GlobalCouponNumberTest.class,
	OfferTest.class,
	UsageConditionTest.class,
	FinancialSettlementDetailTest.class,
	DistributionDetailTest.class,
	MarketingMaterialTest.class,
	RewardTest.class,
	PurchaseRequirementTest.class,
	AwarderDetailTest.class,
	AcquisitionPeriodTest.class,
	AwarderPointOfSaleTest.class,
	PurchaseTradeItemTest.class,
	RedemptionPeriodTest.class,
	RewardTradeItemTest.class,
	ArtworkTest.class,
	FileContentDescriptionTest.class,
	LegalStatementTest.class,
	LongDescriptionTest.class,
	ShortDescriptionTest.class,
	ProductCategoryTest.class
})

public class ErrorTestsSuite {

	@BeforeClass
	public static void initiatize() {
		
		TestCondition tc = TestCondition.getInstance();
		tc.setMethod(TestCondition.METHOD_POST);
	}

}