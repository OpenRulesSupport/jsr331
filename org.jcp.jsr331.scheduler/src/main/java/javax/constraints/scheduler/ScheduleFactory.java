package javax.constraints.scheduler;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.scheduler.impl.ScheduleImpl;

public class ScheduleFactory {

	public static Schedule newSchedule(String name, int start, int end) {
		try {
			Problem problem = ProblemFactory.newProblem(name);
			Schedule schedule = new ScheduleImpl(problem, name, start, end);
			return schedule;
		} catch (Exception e) {
			throw new RuntimeException(
					"ScheduleFactory: Can not create an instance of the class ScheduleImpl",
					e);
		}
	}
	
	public static Schedule newSchedule(Problem problem) {
		try {
			Schedule schedule = new ScheduleImpl(problem, problem.getName());
			return schedule;
		} catch (Exception e) {
			throw new RuntimeException(
					"ScheduleFactory: Can not create an instance of the class ScheduleImpl",
					e);
		}
	}

}
