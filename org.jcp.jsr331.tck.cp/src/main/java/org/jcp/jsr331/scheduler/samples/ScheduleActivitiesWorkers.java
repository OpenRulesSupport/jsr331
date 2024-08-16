package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public final class ScheduleActivitiesWorkers {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleActivitiesWorkers",0,40);
	
	public void define() throws Exception {

		// defining jobs
		Activity masonry =   s.activity("masonry  ",7);
		Activity carpentry = s.activity("carpentry",3);
		Activity plumbing =  s.activity("plumbing ",8);
		Activity ceiling =   s.activity("ceiling  ",3);
		Activity roofing =   s.activity("roofing  ",1);
		Activity painting =  s.activity("painting ",2);
		Activity windows =   s.activity("windows  ",1);
		Activity facade =    s.activity("facade   ",2);
		Activity garden =    s.activity("garden   ",1);
		Activity movingIn =  s.activity("moving in",1);
		
		Activity[] activities = {
			masonry,carpentry,roofing,plumbing,ceiling,windows,	
			facade,garden,painting,movingIn
		};

		// Posting "startsAfterEnd" constraints
		s.post(carpentry,">",masonry);
		s.post(roofing,">",carpentry);
		s.post(plumbing,">",masonry);
		s.post(ceiling,">",masonry);
		s.post(windows,">",roofing);
		s.post(facade,">",roofing);
		s.post(facade,">",plumbing);
		s.post(garden,">",roofing);
		s.post(garden,">",plumbing);
		s.post(painting,">",ceiling);
		s.post(movingIn,">",windows);
		s.post(movingIn,">",facade);
		s.post(movingIn,">",garden);
		s.post(movingIn,">",painting);
		
		// Adding Discrete Resource
		int numberOfWorkers = 2;
		Resource worker = s.resource("Workers",numberOfWorkers);
		for (int i = 0; i < activities.length; ++i) {
			activities[i].requires(worker,1);
		}
		s.logActivities();
		s.logResources();
	}
	
	public void solve() {

		Solution solution = s.scheduleActivities();
		if (solution == null)
			s.log("No solutions");
		else {
			s.log(solution);
		}
	}
	
	public static void main(String args[]) throws Exception {
		ScheduleActivitiesWorkers p = new ScheduleActivitiesWorkers();
		p.define();
		p.solve();		
	}
}
