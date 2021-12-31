package wow.entity;

/**
 * @Description
 * @autor wzl
 * @date 2021/7/3-17:09
 */
public class PatientInfo {
    private Integer patientID;
    private String patientName;
    private String patientPhone;

    public PatientInfo() {
    }

    public PatientInfo(Integer patientID, String patientName, String patientPhone) {
        this.patientID = patientID;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
    }

    public Integer getPatientID() {
        return patientID;
    }

    public void setPatientID(Integer patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
}
