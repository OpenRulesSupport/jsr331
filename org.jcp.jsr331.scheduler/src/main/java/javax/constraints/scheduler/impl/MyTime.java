//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

/**
 * This class represents time that can be presented as natural as year/month/day
 * hour:min and that can be converted toTime integers relative toTime some
 * initial time (origin) and timestep that defines a number of minutes
 *
 */

public class MyTime {

	int day; // 1-31

	int month; // 1-12

	int year; // 0-2007 and more

	int hour; // 0 - 23

	int min; // 0 - 59

	public MyTime(int year, int month, int day, int hour, int min) {
		setDay(day);
		setMonth(month);
		setYear(year);
		setHour(hour);
		setMin(min);
	}

	public MyTime(MyTime ct) {
		setDay(ct.day);
		setMonth(ct.month);
		setYear(ct.year);
		setHour(ct.hour);
		setMin(ct.min);
	}

	public MyTime(int year, int month, int day) {
		this(year, month, day, 0, 0);
	}

	public MyTime(int julian) {
		int a, b, c, d, e, z, alpha;
		z = julian + 1;

		// dealing with Gregorian calendar reform

		if (z < 2299161L)
			a = z;
		else {
			alpha = (int) ((z - 1867216.25) / 36524.25);
			a = z + 1 + alpha - alpha / 4;
		}

		b = (a > 1721423 ? a + 1524 : a + 1158);
		c = (int) ((b - 122.1) / 365.25);
		d = (int) (365.25 * c);
		e = (int) ((b - d) / 30.6001);

		day = (int) (b - d - (long) (30.6001 * e));
		month = ((e < 13.5) ? e - 1 : e - 13);
		year = ((month > 2.5) ? (c - 4716) : c - 4715);
	}

	public int julian() {
		int a, b = 0;
		int work_month = month, work_day = day, work_year = year;
		int julian;

		// correct for negative year

		if (work_year < 0)
			work_year++;

		if (work_month <= 2) {
			work_year--;
			work_month += 12;
		}

		// deal with Gregorian calendar

		if (work_year * 10000. + work_month * 100. + work_day >= 15821015.) {
			a = (int) (work_year / 100.);
			b = 2 - a + a / 4;
		}

		julian = (int) (365.25 * work_year)
				+ (int) (30.6001 * (work_month + 1)) + work_day + 1720994 + b;
		return julian;
	}

	// next day
	public MyTime next() {
		return new MyTime(julian() + 1);
	}

	public MyTime add(int plusMinutes) {
		if (plusMinutes > 1440)
			throw new RuntimeException("Unhandled limit in CpTime.add(minutes) - too many minutes");

		MyTime result = new MyTime(this);
		int pureHours = plusMinutes / 60;
		int pureMinutes = plusMinutes % 60;
		int newMinutes = result.min + pureMinutes;
		if (newMinutes > 59) {
			pureHours++;
			result.min= newMinutes - 60;
		}
		else {
			result.min = newMinutes;
		}
		if (pureHours > 0) {
			int newHour = result.hour + pureHours;
			if (newHour > 23) {
				new MyTime(julian() + 1);
				result.hour = newHour - 24;
			}
			else {
				result.hour = newHour;
			}
		}
		return result;
	}

	public boolean eq(MyTime dmy) {
		return this.day == dmy.day && this.month == dmy.month
				&& this.year == dmy.year;
	}

	public boolean lessThan(MyTime dmy) {
		if (this.year > dmy.year)
			return false;
		if (this.year < dmy.year)
			return true;
		if (this.month > dmy.month)
			return false;
		if (this.month < dmy.month)
			return true;
		if (this.day > dmy.day)
			return false;
		if (this.day < dmy.day)
			return true;
		if (this.day > dmy.day)
			return false;
		if (this.hour > dmy.hour)
			return false;
		if (this.hour < dmy.hour)
			return true;
		if (this.min > dmy.min)
			return false;
		if (this.min < dmy.min)
			return true;
		return false; // they are equal
	}

	public boolean lessThanOrEqual(MyTime dmy) {
		if (this.year > dmy.year)
			return false;
		if (this.year < dmy.year)
			return true;
		if (this.month > dmy.month)
			return false;
		if (this.month < dmy.month)
			return true;
		if (this.day > dmy.day)
			return false;
		if (this.day < dmy.day)
			return true;
		if (this.day > dmy.day)
			return false;
		if (this.hour > dmy.hour)
			return false;
		if (this.hour < dmy.hour)
			return true;
		if (this.min > dmy.min)
			return false;
		if (this.min < dmy.min)
			return true;
		return true; // they are equal
	}

	static final int SUN = 0;

	static final int MON = 1;

	static final int TUE = 2;

	static final int WED = 3;

	static final int THU = 4;

	static final int FRI = 5;

	static final int SAT = 6;

	public int weekday() {
		return (julian() + 2) % 7;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int toInt(MyTime origin, int timestep) {
		int numberOfDaySteps = (julian() - origin.julian()) * (1440 / timestep);
		int numberOfFullTimeSteps = (hour * 60 + min) / timestep;
		return numberOfDaySteps + numberOfFullTimeSteps;

	}

	static public MyTime fromInt(int time, MyTime origin, int timestep) {
		int numberOfTimestepsInDay = 1440 / timestep;
		int numberOfFullDaysInTimesteps = (time / numberOfTimestepsInDay)
				* numberOfTimestepsInDay;
		int numberOfMinutesInTimesteps = time - numberOfFullDaysInTimesteps;
		int julianTime = (numberOfFullDaysInTimesteps / numberOfTimestepsInDay)
				+ origin.julian();
		MyTime ct = new MyTime(julianTime);
		// System.out.println(ct);
		int numberOfMinutes = numberOfMinutesInTimesteps * timestep;
		ct.setHour(numberOfMinutes / 60);
		ct.setMin(numberOfMinutes % 60);
		return ct;

	}

	public String toString() {

		String date = String.valueOf(year);

		if (month <= 9)
			date += ".0" + String.valueOf(month);
		else
			date += "." + String.valueOf(month);

		if (day <= 9)
			date += ".0" + String.valueOf(day);
		else
			date += "." + String.valueOf(day);

		if (hour <= 9)
			date += " 0" + String.valueOf(hour);
		else
			date += " " + String.valueOf(hour);

		if (min <= 9)
			date += ":0" + String.valueOf(min);
		else
			date += ":" + String.valueOf(min);

		return date;
	}

	public static void main(String[] args) {

		MyTime origin = new MyTime(2000, 2, 28);
		int timestep = 480;
		System.out.println("Origin: " + origin + " timestep: " + timestep);
		System.out.println("Origin's next: " + origin.next());
		MyTime t = new MyTime(2000, 3, 1);
		t.setHour(11);
		t.setMin(25);
		System.out.println(t);
		int it = t.toInt(origin, timestep);
		System.out.println("toInt: " + it);
		System.out
				.println("fromInt: " + MyTime.fromInt(it, origin, timestep));

		System.out.println(origin.next());
	}

}
