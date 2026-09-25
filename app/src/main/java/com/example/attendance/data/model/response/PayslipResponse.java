package com.example.attendance.data.model.response;

public class PayslipResponse {
    private String employeeId;         // "10581"
    private String fullName;           // "Nguyễn Thị Ngọc Hạnh"
    private String department;         // "Ban Phát triển vùng nguyên liệu"
    private String jobTitle;           // "Nhân viên KCS trạm sữa"
    private String taxCode;            // "0305192419"
    private int dependentsCount;       // 2
    private String baseSalary;         // "12.440.000"
    private String tkqcvIncome;        // "2.270.000"
    private String extraTkqcvIncome;   // "0"
    private String paRatio;            // "1"
    private int standardWorkHours;    // 208
    private int actualWorkHours;      // 208

    public PayslipResponse() {}

    public PayslipResponse(String employeeId, String fullName, String department, String jobTitle, String taxCode, int dependentsCount, String baseSalary, String tkqcvIncome, String extraTkqcvIncome, String paRatio, int standardWorkHours, int actualWorkHours) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.department = department;
        this.jobTitle = jobTitle;
        this.taxCode = taxCode;
        this.dependentsCount = dependentsCount;
        this.baseSalary = baseSalary;
        this.tkqcvIncome = tkqcvIncome;
        this.extraTkqcvIncome = extraTkqcvIncome;
        this.paRatio = paRatio;
        this.standardWorkHours = standardWorkHours;
        this.actualWorkHours = actualWorkHours;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public int getDependentsCount() { return dependentsCount; }
    public void setDependentsCount(int dependentsCount) { this.dependentsCount = dependentsCount; }

    public String getBaseSalary() { return baseSalary; }
    public void setBaseSalary(String baseSalary) { this.baseSalary = baseSalary; }

    public String getTkqcvIncome() { return tkqcvIncome; }
    public void setTkqcvIncome(String tkqcvIncome) { this.tkqcvIncome = tkqcvIncome; }

    public String getExtraTkqcvIncome() { return extraTkqcvIncome; }
    public void setExtraTkqcvIncome(String extraTkqcvIncome) { this.extraTkqcvIncome = extraTkqcvIncome; }

    public String getPaRatio() { return paRatio; }
    public void setPaRatio(String paRatio) { this.paRatio = paRatio; }

    public int getStandardWorkHours() { return standardWorkHours; }
    public void setStandardWorkHours(int standardWorkHours) { this.standardWorkHours = standardWorkHours; }

    public int getActualWorkHours() { return actualWorkHours; }
    public void setActualWorkHours(int actualWorkHours) { this.actualWorkHours = actualWorkHours; }
}
