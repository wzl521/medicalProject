package wow.entity;

/**
 * @Description
 * @autor wzl
 * @date 2021/7/3-17:13
 */
public class PatientAlter {
    private String patientPhone;
    private  String patientName;

    public PatientAlter() {
    }

    public PatientAlter(String patientPhone, String patientName) {
        this.patientPhone = patientPhone;
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
