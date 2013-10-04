package com.findu.demo.service;

import com.findu.demo.db.Plan;

public class MockFindUService extends FindUService {
	@Override
	public Plan getOnTimePlan()
	{
		Plan plan = new Plan();
		plan.name = "ио╟Ю";
		plan.destLatitude = (int)(116.403909 * 1E6);
		plan.destLongitude = (int)(39.915267 * 1E6);
		return plan;
	}
}
