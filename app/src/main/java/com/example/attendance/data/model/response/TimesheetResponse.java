package com.example.attendance.data.model.response;

public class TimesheetResponse {
    private String year;
    private String month;
    private String shiftName;         // e.g. "Ca 1 (06g00-14g00)"
    private String checkTime;         // e.g. "06:00 - 14:00"
    private String overtimeStandard;   // e.g. "8.00 -"
    private int standardWorkHours;    // 208
    private int startAnnualLeave;     // 68
    private int actualWorkHours;      // 192
    private int leaveHours;           // 0
    private int policyLeaveHours;     // 16
    private int businessTripHours;    // 0
    private int stopWorkHours;        // 0
    private int paidLeaveHours;       // 16
    private int totalPaidHours;       // 208
    private int totalTnTkqcvHours;    // 208
    private int unpaidLeaveHours;     // 0
    private int mealCount;            // 24
    private int nightShiftHours;      // 0
    private int endAnnualLeave;       // 68

    public TimesheetResponse() {}

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }

    public String getCheckTime() { return checkTime; }
    public void setCheckTime(String checkTime) { this.checkTime = checkTime; }

    public String getOvertimeStandard() { return overtimeStandard; }
    public void setOvertimeStandard(String overtimeStandard) { this.overtimeStandard = overtimeStandard; }

    public int getStandardWorkHours() { return standardWorkHours; }
    public void setStandardWorkHours(int standardWorkHours) { this.standardWorkHours = standardWorkHours; }

    public int getStartAnnualLeave() { return startAnnualLeave; }
    public void setStartAnnualLeave(int startAnnualLeave) { this.startAnnualLeave = startAnnualLeave; }

    public int getActualWorkHours() { return actualWorkHours; }
    public void setActualWorkHours(int actualWorkHours) { this.actualWorkHours = actualWorkHours; }

    public int getLeaveHours() { return leaveHours; }
    public void setLeaveHours(int leaveHours) { this.leaveHours = leaveHours; }

    public int getPolicyLeaveHours() { return policyLeaveHours; }
    public void setPolicyLeaveHours(int policyLeaveHours) { this.policyLeaveHours = policyLeaveHours; }

    public int getBusinessTripHours() { return businessTripHours; }
    public void setBusinessTripHours(int businessTripHours) { this.businessTripHours = businessTripHours; }

    public int getStopWorkHours() { return stopWorkHours; }
    public void setStopWorkHours(int stopWorkHours) { this.stopWorkHours = stopWorkHours; }

    public int getPaidLeaveHours() { return paidLeaveHours; }
    public void setPaidLeaveHours(int paidLeaveHours) { this.paidLeaveHours = paidLeaveHours; }

    public int getTotalPaidHours() { return totalPaidHours; }
    public void setTotalPaidHours(int totalPaidHours) { this.totalPaidHours = totalPaidHours; }

    public int getTotalTnTkqcvHours() { return totalTnTkqcvHours; }
    public void setTotalTnTkqcvHours(int totalTnTkqcvHours) { this.totalTnTkqcvHours = totalTnTkqcvHours; }

    public int getUnpaidLeaveHours() { return unpaidLeaveHours; }
    public void setUnpaidLeaveHours(int unpaidLeaveHours) { this.unpaidLeaveHours = unpaidLeaveHours; }

    public int getMealCount() { return mealCount; }
    public void setMealCount(int mealCount) { this.mealCount = mealCount; }

    public int getNightShiftHours() { return nightShiftHours; }
    public void setNightShiftHours(int nightShiftHours) { this.nightShiftHours = nightShiftHours; }

    public int getEndAnnualLeave() { return endAnnualLeave; }
    public void setEndAnnualLeave(int endAnnualLeave) { this.endAnnualLeave = endAnnualLeave; }
}
