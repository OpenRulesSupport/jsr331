package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/
/**
 * This class contains constants used for CP business interface
 *
 * @author JF
 */

public final class BizConstant {

	static public final int STATUS_UNKNOWN = 0;

	static public final int STATUS_VIOLATED = 1;

	static public final int STATUS_PARTIALLY_VIOLATED = 2;

	static public final int STATUS_SATISFIED = 3;

	static public final int STATUS_OFF = 4;

	static String statuses[] = { "Unknown", "Violated", "Partially Violated",
			"Satisfied", "Off" };

	static public final int TYPE_UNKNOWN = 0;

	static public final int TYPE_INT = 1;

	static public final int TYPE_REAL = 2;

	static public final int TYPE_SET = 3;

	static public final int TYPE_BOOL = 4;

	static public final int TYPE_OBJECTIVE = 6;

	static public final int TYPE_CONSTRAINT = 7;

	static public final int ACTION_UNKNOWN = 0;

	static public final int ACTION_SOLVE = 1;

	static public final int ACTION_MINIMIZE = 2;

	static public final int ACTION_MAXIMIZE = 3;

	static public final int ACTION_SCHEDULE_ACTIVITIES = 3;

	static String actions[] = { "Unknown", "Solve", "Minimize", "Maximize", "ScheduleActivities" };
}
