package com.coupon.test.suite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.coupon.test.action.AcquisitionPeriodTest;
import com.coupon.test.action.ArtworkTest;
import com.coupon.test.action.AwarderDetailTest;
import com.coupon.test.action.AwarderPointOfSaleTest;
import com.coupon.test.action.DistributionDetailTest;
import com.coupon.test.action.FinancialSettlementDetailTest;
import com.coupon.test.action.GlobalCouponNumberTest;
import com.coupon.test.action.GlobalLocationNumberTest;
import com.coupon.test.action.GlobalServiceRelationNumberTest;
import com.coupon.test.action.GlobalTradeIdentificationNumberTest;
import com.coupon.test.action.LegalStatementTest;
import com.coupon.test.action.LongDescriptionTest;
import com.coupon.test.action.MarketingMaterialTest;
import com.coupon.test.action.OfferTest;
import com.coupon.test.action.ProductCategoryTest;
import com.coupon.test.action.PurchaseRequirementTest;
import com.coupon.test.action.PurchaseTradeItemTest;
import com.coupon.test.action.RedemptionPeriodTest;
import com.coupon.test.action.RewardTest;
import com.coupon.test.action.RewardTradeItemTest;
import com.coupon.test.action.ShortDescriptionTest;
import com.coupon.test.action.UsageConditionTest;
import com.coupon.test.action.FileContentDescriptionTest;
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

public class ActionTestsSuite {

	@BeforeClass
	public static void initiatize() {
		
		TestCondition tc = TestCondition.getInstance();
		tc.setMethod(TestCondition.METHOD_POST);
	}

}