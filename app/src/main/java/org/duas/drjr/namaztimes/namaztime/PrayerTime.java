package org.duas.drjr.namaztimes.namaztime;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by JafarRizvi on 12-Aug-2016.
 */


public class PrayerTime {

    private CalculationMethod calcMethod; // caculation method
    private JuristicMethod asrJuristic;   // Juristic method for Asr
    private TimeFormat timeFormat;
    private HighLatitudeAdjustment adjustHighLats;

    private double lat; // latitude
    private double lng; // longitude
    private double timeZone; // time-zone
    private double JDate; // Julian date

    private String InvalidTime; // The string used for invalid times

    // return prayer times for a given date
    public ArrayList<String> getPrayerTimes(Calendar date, double latitude,
                                            double longitude, double tZone) {

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);

        return getDatePrayerTimes(year, month + 1, day, latitude, longitude, tZone);
    }

    // return prayer times for a given date
    private ArrayList<String> getDatePrayerTimes(int year, int month, int day,
                                                 double latitude, double longitude, double tZone) {

        this.calcMethod = CalculationMethod.JAFARI;
        this.asrJuristic = JuristicMethod.JAFARI;
        this.timeFormat = TimeFormat.Hour12xx;
        this.adjustHighLats = HighLatitudeAdjustment.MidNight;

        this.InvalidTime = "??:?? xx";
        this.lat = latitude;
        this.lng = longitude;
        this.timeZone = tZone;
        this.JDate = julianDate(year, month, day);
        double lonDiff = longitude / (15.0 * 24.0);
        this.JDate = this.JDate - lonDiff;
        return computeDayTimes();
    }

    public void setCalcMethod(CalculationMethod calculationMethod) {
        this.calcMethod = calculationMethod;
    }

    public void setAsrJuristic(JuristicMethod juristicMethod) {
        this.asrJuristic = juristicMethod;
    }

    public void setTimeFormat(TimeFormat format) {
        this.timeFormat = format;
    }

    public void setAdjustHighLats(HighLatitudeAdjustment adjustment) {
        this.adjustHighLats = adjustment;
    }

    // compute prayer times at given julian date
    private ArrayList<String> computeDayTimes() {
        double[] times = {5, 6, 12, 13, 18, 18, 18}; // default times

        times = computeTimes();
        times = adjustTimes(times);
        //times = tuneTimes(times);

        return adjustTimesFormat(times);
    }

    // ---------------------- Compute Prayer Times -----------------------
    // compute prayer times at given julian date
    private double[] computeTimes() {

        double[] DayTimes = new double[DayPoint.values().length];

        DayTimes[DayPoint.FastingStart.ordinal()] = this.computeFastingStart();
        DayTimes[DayPoint.Fajr.ordinal()] = this.computeFajar();
        DayTimes[DayPoint.AstroTwilight.ordinal()] = this.computeAstroTwilight();
        DayTimes[DayPoint.Sunrise.ordinal()] = this.computeSunRise();
        DayTimes[DayPoint.Dhuhr.ordinal()] = this.computeZohar();
        DayTimes[DayPoint.Asr.ordinal()] = this.computeAsr();
        DayTimes[DayPoint.Sunset.ordinal()] = this.computeSunSet();
        DayTimes[DayPoint.Maghrib.ordinal()] = this.computeMaghrib();
        DayTimes[DayPoint.Isha.ordinal()] = this.computeIsha();
        DayTimes[DayPoint.Midnight.ordinal()] = (DayTimes[DayPoint.Fajr.ordinal()] + DayTimes[DayPoint.Maghrib.ordinal()] + 24) / 2.0;

        return DayTimes;
    }

    // ---------------------- Calculation Functions -----------------------
    // References:
    // http://praytimes.org/calculation/
    // https://en.wikipedia.org/wiki/Salah_times
    // http://aa.usno.navy.mil/faq/docs/SunApprox.html
    // compute Declination Angle of Sun
    // The declination of the Sun is the angle between the rays of the sun and the plane of the earth equator.
    // The declination of the Sun changes continuously throughout the year.
    // This is a consequence of the Earth's tilt, i.e. the difference in its rotational and revolutionary axes.
    private double computeSunDeclination(double julian_date) {

        double D = julian_date - 2451545;
        double g = fixAngle(357.529 + 0.98560028 * D);
        double q = fixAngle(280.459 + 0.98564736 * D);
        double L = fixAngle(q + (1.915 * dsin(g)) + (0.020 * dsin(2 * g)));

        double e = 23.439 - (0.00000036 * D);
        double SunDeclination = darcsin(dsin(e) * dsin(L));

        return SunDeclination;
    }

    // ---------------------- Calculation Functions -----------------------
    // References:
    // http://praytimes.org/calculation/
    // https://en.wikipedia.org/wiki/Salah_times
    // http://aa.usno.navy.mil/faq/docs/SunApprox.html
    // compute Equation of Time
    // The equation of time is the difference between time as read from a sundial and a clock.
    // It results from an apparent irregular movement of the Sun caused by a combination of the
    // obliquity of the Earth's rotation axis and the eccentricity of its orbit.
    // The sundial can be ahead (fast) by as much as 16 min 33 s (around November 3)
    // or fall behind by as much as 14 min 6 s (around February 12)
    private double computeEquationOfTime(double julian_date) {

        double D = julian_date - 2451545;
        double g = fixAngle(357.529 + 0.98560028 * D);
        double q = fixAngle(280.459 + 0.98564736 * D);
        double L = fixAngle(q + (1.915 * dsin(g)) + (0.020 * dsin(2 * g)));

        // double R = 1.00014 - 0.01671 * [self dcos:g] - 0.00014 * [self dcos:
        // (2*g)];
        double e = 23.439 - (0.00000036 * D);

        double RA = (darctan2((dcos(e) * dsin(L)), (dcos(L)))) / 15.0;
        RA = fixHour(RA);
        double EquationOfTime = q / 15.0 - RA;

        return EquationOfTime;
    }

    // compute mid-day (Dhuhr, Zawal) time
    private double computeMidDay(double init_time) {
        double T = computeEquationOfTime(this.JDate + init_time);
        double Z = fixHour(12 - T);
        return Z;
    }

    // compute the time of Asr
    // Shafii: step=1, Hanafi: step=2
    private double computeAsr() {
        double step = 0;
        double init_time = 13.0 / 24.0;
        if (asrJuristic == JuristicMethod.HANAFI)
            step = 2;
        else if (asrJuristic == JuristicMethod.SHAFAI)
            step = 1;
        else if (asrJuristic == JuristicMethod.JAFARI)
            step = 1;

        double D = computeSunDeclination(this.JDate + init_time);
        double Angle = -darccot(step + dtan(Math.abs(this.lat - D)));
        return computeTime(Angle, init_time);
    }

    private double computeFajar() {

        // Jafari   double[] Jvalues = {16, 0, 4, 0, 14};
        // Karachi  double[] Kvalues = {18, 1, 0, 0, 18};
        // ISNA     double[] Ivalues = {15, 1, 0, 0, 15};
        // MWL      double[] MWvalues = {18, 1, 0, 0, 17};
        // Makkah   double[] MKvalues = {18.5, 1, 0, 1, 90};
        // Egypt    double[] Evalues = {19.5, 1, 0, 0, 17.5};
        // Tehran   double[] Tvalues = {17.7, 0, 4.5, 0, 14};
        // Custom   double[] Cvalues = {18, 1, 0, 0, 17};

        double init_time = 5.0 / 24.0;
        double FajarAngle = 180.0;

        switch (calcMethod) {
            case JAFARI:
                FajarAngle -= 16;
                break;
            case KARACHI:
                FajarAngle -= 18;
                break;
            case ISNA:
                FajarAngle -= 15;
                break;
            case MWL:
                FajarAngle -= 18;
                break;
            case MAKKAH:
                FajarAngle -= 18.5;
                break;
            case EGYPT:
                FajarAngle -= 19.5;
                break;
            case TEHRAN:
                FajarAngle -= 17.7;
                break;
            case CUSTOM:
            default:
                FajarAngle -= 18;
                break;
        }

        return computeTime(FajarAngle, init_time);
    }

    private double computeFastingStart() {
        double init_time = 5.0 / 24.0;
        double ImsakAngle = 180.0 - 19;
        return computeTime(ImsakAngle, init_time);
    }

    private double computeAstroTwilight() {
        double t = 5.0 / 24.0;
        double AstroTwilightAngle = 180.0 - 18;
        return computeTime(AstroTwilightAngle, t);
    }

    private double computeSunRise() {
        double G = 180 - 0.833;
        double t = 6.0 / 24.0;
        double sun_rise = computeTime(G, t);
        return sun_rise;
    }

    private double computeZohar() {
        return this.computeMidDay(12.0 / 24.0);
    }

    private double computeSunSet() {
        double G = 0.833;
        double init_time = 18.0 / 24.0;
        double sun_set = computeTime(G, init_time);
        return sun_set;
    }

    private double computeMaghrib() {
        double initTime = 18.0 / 24.0;
        double Angle = 0.0;
        if (calcMethod == CalculationMethod.JAFARI)
            Angle = 4.0;
        else if (calcMethod == CalculationMethod.TEHRAN)
            Angle = 4.5;
        return computeTime(Angle, initTime);
    }

    private double computeIsha() {
        double time = 18.0 / 24.0;
        double IshaAngle = 14.0;

        // Jafari   double[] Jvalues = {16, 0, 4, 0, 14};
        // Karachi  double[] Kvalues = {18, 1, 0, 0, 18};
        // ISNA     double[] Ivalues = {15, 1, 0, 0, 15};
        // MWL      double[] MWvalues = {18, 1, 0, 0, 17};
        // Makkah   double[] MKvalues = {18.5, 1, 0, 1, 90};
        // Egypt    double[] Evalues = {19.5, 1, 0, 0, 17.5};
        // Tehran   double[] Tvalues = {17.7, 0, 4.5, 0, 14};
        // Custom   double[] Cvalues = {18, 1, 0, 0, 17};

        switch (calcMethod) {
            case JAFARI:
                IshaAngle = 14;
                break;
            case KARACHI:
                IshaAngle = 18;
                break;
            case ISNA:
                IshaAngle = 15;
                break;
            case MWL:
                IshaAngle = 17;
                break;
            case MAKKAH:
                IshaAngle = 90;
                break;
            case EGYPT:
                IshaAngle = 17.5;
                break;
            case TEHRAN:
                IshaAngle = 14;
                break;
            case CUSTOM:
            default:
                IshaAngle = 17;
                break;
        }

        return computeTime(IshaAngle, time);

    }

    // compute time for a given angle G
    private double computeTime(double G, double t) {
        double D = computeSunDeclination(this.JDate + t);
        double Z = computeMidDay(t);
        double Beg = -dsin(G) - dsin(D) * dsin(this.lat);
        double Mid = dcos(D) * dcos(this.lat);
        double V = darccos(Beg / Mid) / 15.0;

        return Z + (G > 90 ? -V : V);
    }

    // ---------------------- Trigonometric Functions -----------------------
    // Reduce Angle in Rangle 0 to 359 degrees.
    private double fixAngle(double angle) {
        //  Method 1
        //  angle = angle - (360 * (Math.floor(angle / 360.0)));
        //  angle = angle < 0 ? (angle + 360) : angle;

        //  Method 2
        while (angle >= 360)
            angle -= 360;

        while (angle < 0)
            angle += 360;

        return angle;
    }

    // Reduce hours in Range 0 to 23 hours
    private double fixHour(double hour) {
        //   Method 1
        //   hour = hour - 24.0 * Math.floor(hour / 24.0);
        //   hour = hour < 0 ? (hour + 24) : hour;

        //  Method 2
        while (hour >= 24)
            hour -= 24;

        while (hour < 0)
            hour += 24;

        return hour;
    }

    // radian to degree
    private double radiansToDegrees(double alpha) {
        return ((alpha * 180.0) / Math.PI);
    }

    // degree to radian
    private double DegreesToRadians(double alpha) {
        return ((alpha * Math.PI) / 180.0);
    }

    // degree sin
    private double dsin(double d) {
        return (Math.sin(DegreesToRadians(d)));
    }

    // degree cos
    private double dcos(double d) {
        return (Math.cos(DegreesToRadians(d)));
    }

    // degree tan
    private double dtan(double d) {
        return (Math.tan(DegreesToRadians(d)));
    }

    // degree arcsin
    private double darcsin(double x) {
        double val = Math.asin(x);
        return radiansToDegrees(val);
    }

    // degree arccos
    private double darccos(double x) {
        double val = Math.acos(x);
        return radiansToDegrees(val);
    }

    // degree arctan
    private double darctan(double x) {
        double val = Math.atan(x);
        return radiansToDegrees(val);
    }

    // degree arctan2
    private double darctan2(double y, double x) {
        double val = Math.atan2(y, x);
        return radiansToDegrees(val);
    }

    // degree arccot
    private double darccot(double x) {
        double val = Math.atan2(1.0, x);
        return radiansToDegrees(val);
    }

    // ---------------------- Julian Date Functions -----------------------
    // calculate julian date from a calendar date
    private double julianDate(int year, int month, int day) {

        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor(year / 100.0);

        double B = 2 - A + Math.floor(A / 4.0);

        double JD = Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;

        return JD;
    }


    // adjust times in a prayer time array
    private double[] adjustTimes(double[] times) {

        for (int i = 0; i < times.length; i++) {
            times[i] += this.timeZone - this.lng / 15;
        }

        double DhuhrMinutes = 0;
        times[DayPoint.Dhuhr.ordinal()] += DhuhrMinutes / 60; // Dhuhr


        // Jafari   double[] Jvalues = {16, 0, 4, 0, 14};
        // Karachi  double[] Kvalues = {18, 1, 0, 0, 18};
        // ISNA     double[] Ivalues = {15, 1, 0, 0, 15};
        // MWL      double[] MWvalues = {18, 1, 0, 0, 17};
        // Makkah   double[] MKvalues = {18.5, 1, 0, 1, 90};
        // Egypt    double[] Evalues = {19.5, 1, 0, 0, 17.5};
        // Tehran   double[] Tvalues = {17.7, 0, 4.5, 0, 14};
        // Custom   double[] Cvalues = {18, 1, 0, 0, 17};


//        if (calcMethod == CalculationMethod.JAFARI) // Maghrib
//        {
//            times[DayPoint.Maghrib.ordinal()] = times[DayPoint.Sunset.ordinal()] + 4.0 / 60;  // 4 minutes after sunset
//        } else if (calcMethod == CalculationMethod.TEHRAN) // Maghrib
//        {
//            times[DayPoint.Maghrib.ordinal()] = times[DayPoint.Sunset.ordinal()] + 4.5 / 60;  // 4.5 minutes after sunset
//        }

        if (calcMethod == CalculationMethod.MAKKAH) // Isha
        {
            times[DayPoint.Isha.ordinal()] = times[DayPoint.Maghrib.ordinal()] + 90.0 / 60; // 90 minutes after maghrib
        }

        if (this.adjustHighLats != HighLatitudeAdjustment.None) {
            times = adjustHighLatTimes(times);
        }

        return times;
    }

    // convert times array to given time format
    private ArrayList<String> adjustTimesFormat(double[] times) {

        ArrayList<String> result = new ArrayList<String>();

        if (timeFormat == TimeFormat.Floating) {
            for (double time : times) {
                result.add(String.valueOf(time));
            }
            return result;
        }

        int count = DayPoint.values().length;
        for (int i = 0; i < count; i++) {
            if (this.timeFormat == TimeFormat.Hour24) {
                result.add(floatToTime24(times[i]));
            } else if (timeFormat == TimeFormat.Hour12) {
                result.add(floatToTime12(times[i], true));
            } else {
                result.add(floatToTime12(times[i], false));
            }
        }
        return result;
    }

    // convert double hours to 24h format
    public String floatToTime24(double time) {

        String result;

        if (Double.isNaN(time)) {
            return InvalidTime;
        }

        time = fixHour(time + 0.5 / 60.0); // add 0.5 minutes to round
        int hours = (int) Math.floor(time);
        double minutes = Math.floor((time - hours) * 60.0);

        if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
            result = "0" + hours + ":0" + Math.round(minutes);
        } else if ((hours >= 0 && hours <= 9)) {
            result = "0" + hours + ":" + Math.round(minutes);
        } else if ((minutes >= 0 && minutes <= 9)) {
            result = hours + ":0" + Math.round(minutes);
        } else {
            result = hours + ":" + Math.round(minutes);
        }
        return result;
    }

    // convert double hours to 12h format
    public String floatToTime12(double time, boolean noSuffix) {

        if (Double.isNaN(time)) {
            return InvalidTime;
        }

        time = fixHour(time + 0.5 / 60); // add 0.5 minutes to round
        int hours = (int) Math.floor(time);
        double minutes = Math.floor((time - hours) * 60);
        String suffix, result;
        if (hours >= 12) {
            suffix = "pm";
        } else {
            suffix = "am";
        }
        hours = ((((hours + 12) - 1) % (12)) + 1);
        /*hours = (hours + 12) - 1;
        int hrs = (int) hours % 12;
        hrs += 1;*/
        if (noSuffix == false) {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes) + " "
                        + suffix;
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes) + " " + suffix;
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes) + " " + suffix;
            } else {
                result = hours + ":" + Math.round(minutes) + " " + suffix;
            }

        } else {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes);
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes);
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes);
            } else {
                result = hours + ":" + Math.round(minutes);
            }
        }
        return result;

    }

    // convert double hours to 12h format with no suffix
    public String floatToTime12NS(double time) {
        return floatToTime12(time, true);
    }

    // adjust Fajr, Isha and Maghrib for locations in higher latitudes
    private double[] adjustHighLatTimes(double[] times) {
        double nightTime = timeDiff(times[DayPoint.Sunset.ordinal()], times[DayPoint.Sunrise.ordinal()]); // sunset to sunrise

        double FajarAngle = 16;
        switch (calcMethod) {
            case JAFARI:
                FajarAngle = 16;
                break;
            case KARACHI:
                FajarAngle = 18;
                break;
            case ISNA:
                FajarAngle = 15;
                break;
            case MWL:
                FajarAngle = 18;
                break;
            case MAKKAH:
                FajarAngle = 18.5;
                break;
            case EGYPT:
                FajarAngle = 19.5;
                break;
            case TEHRAN:
                FajarAngle = 17.7;
                break;
            case CUSTOM:
            default:
                FajarAngle = 18;
                break;
        }

        // Adjust Fajr
        double FajrDiff = nightPortion(FajarAngle) * nightTime;

        if (Double.isNaN(times[DayPoint.Fajr.ordinal()]) || timeDiff(times[DayPoint.Fajr.ordinal()], times[DayPoint.Sunrise.ordinal()]) > FajrDiff) {
            times[DayPoint.Fajr.ordinal()] = times[DayPoint.Sunrise.ordinal()] - FajrDiff;
        }

        // Jafari   double[] Jvalues = {16, 0, 4, 0, 14};
        // Karachi  double[] Kvalues = {18, 1, 0, 0, 18};
        // ISNA     double[] Ivalues = {15, 1, 0, 0, 15};
        // MWL      double[] MWvalues = {18, 1, 0, 0, 17};
        // Makkah   double[] MKvalues = {18.5, 1, 0, 1, 90};
        // Egypt    double[] Evalues = {19.5, 1, 0, 0, 17.5};
        // Tehran   double[] Tvalues = {17.7, 0, 4.5, 0, 14};
        // Custom   double[] Cvalues = {18, 1, 0, 0, 17};

        // Adjust Isha
        double IshaAngle = 18;

        switch (calcMethod) {
            case JAFARI:
                IshaAngle = 14;
                break;
            case KARACHI:
                IshaAngle = 18;
                break;
            case ISNA:
                IshaAngle = 15;
                break;
            case MWL:
                IshaAngle = 17;
                break;
            case MAKKAH:
                IshaAngle = 90;
                break;
            case EGYPT:
                IshaAngle = 17.5;
                break;
            case TEHRAN:
                IshaAngle = 14;
                break;
            case CUSTOM:
            default:
                IshaAngle = 17;
                break;
        }

        double IshaDiff = this.nightPortion(IshaAngle) * nightTime;
        if (Double.isNaN(times[DayPoint.Isha.ordinal()]) || this.timeDiff(times[DayPoint.Sunset.ordinal()], times[DayPoint.Isha.ordinal()]) > IshaDiff) {
            times[DayPoint.Isha.ordinal()] = times[DayPoint.Sunset.ordinal()] + IshaDiff;
        }

        // Adjust Maghrib
        double MaghribAngle = 0;
        if (calcMethod == CalculationMethod.JAFARI)
            MaghribAngle = 4;
        else if (calcMethod == CalculationMethod.TEHRAN)
            MaghribAngle = 4.5;
        else
            MaghribAngle = 0;

        double MaghribDiff = nightPortion(MaghribAngle) * nightTime;
        if (Double.isNaN(times[DayPoint.Maghrib.ordinal()]) || this.timeDiff(times[DayPoint.Sunset.ordinal()], times[DayPoint.Maghrib.ordinal()]) > MaghribDiff) {
            times[DayPoint.Maghrib.ordinal()] = times[DayPoint.Sunset.ordinal()] + MaghribDiff;
        }

        return times;
    }

    // the night portion used for adjusting times in higher latitudes
    private double nightPortion(double angle) {
        double calc = 0;

        if (adjustHighLats == HighLatitudeAdjustment.AngleBased)
            calc = (angle) / 60.0;
        else if (adjustHighLats == HighLatitudeAdjustment.MidNight)
            calc = 0.5;
        else if (adjustHighLats == HighLatitudeAdjustment.OneSeventh)
            calc = 0.14286;

        return calc;
    }

    // convert hours to day portions
    private double[] dayPortion(double[] times) {
        for (int i = 0; i < 7; i++) {
            times[i] /= 24;
        }
        return times;
    }

    // compute the difference between two times
    private double timeDiff(double time1, double time2) {
        return fixHour(time2 - time1);
    }


}
