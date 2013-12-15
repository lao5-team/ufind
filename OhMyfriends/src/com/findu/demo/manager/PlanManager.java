package com.findu.demo.manager;

import java.util.ArrayList;

import com.findu.demo.db.Plan;

public class PlanManager {
	private static PlanManager mInstance = null;
	private ArrayList<Plan> mPlans = null;
	public static PlanManager getInstance()
	{
		if(null==mInstance)
		{
			mInstance = new PlanManager();
		}
		return mInstance;
	}
	
	public void addPlan(Plan plan)
	{
		mPlans.add(plan);
	}
	
	public void removePlan(int index)
	{
		mPlans.remove(index);
	}
	
	public ArrayList<Plan> getPlans()
	{
		return (ArrayList<Plan>)(mPlans.clone());
	}
	
	private PlanManager()
	{
		mPlans = new ArrayList<Plan>();
	}
}
