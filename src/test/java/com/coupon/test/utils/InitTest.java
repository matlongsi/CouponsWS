package com.coupon.test.utils;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.coupon.test.action.AwarderDetailTest;
import com.coupon.test.action.DistributionDetailTest;
import com.coupon.test.action.FinancialSettlementDetailTest;
import com.coupon.test.action.GlobalLocationNumberTest;
import com.coupon.test.action.GlobalServiceRelationNumberTest;
import com.coupon.test.action.GlobalTradeIdentificationNumberTest;
import com.coupon.test.action.MarketingMaterialTest;
import com.coupon.test.action.OfferTest;
import com.coupon.test.action.PurchaseRequirementTest;
import com.coupon.test.action.RewardTest;
import com.coupon.test.action.UsageConditionTest;


public class InitTest {

	public static void main(String[] args) {
		
		JUnitCore core = new JUnitCore();
		
		Request test = Request.method(GlobalServiceRelationNumberTest.class, "step1_postGlobalServiceRelationNumber");
		Result result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(GlobalLocationNumberTest.class, "step1_postGlobalLocationNumber");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(GlobalTradeIdentificationNumberTest.class, "step1_postGlobalTradeIdentificationNumber");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(OfferTest.class, "step1_postOffer");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(DistributionDetailTest.class, "step1_postDistributionDetail");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(MarketingMaterialTest.class, "step1_postMarketingMaterial");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(RewardTest.class, "step1_postReward");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(AwarderDetailTest.class, "step1_postAwarderDetail");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(PurchaseRequirementTest.class, "step1_postPurchaseRequirement");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(UsageConditionTest.class, "step1_postUsageCondition");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		test = Request.method(FinancialSettlementDetailTest.class, "step1_postFinancialSettlementDetail");
		result = core.run(test);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}

		System.out.println("Completed running initialization test cases.");
	}
}