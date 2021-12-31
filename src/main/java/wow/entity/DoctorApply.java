package wow.entity;

/**
 * @Description
 * @autor wzl
 * @date 2021/7/2-19:20
 */
public class DoctorApply {
    private Integer doctorID;
    private String patientPhone;

    public DoctorApply() {
    }

    public DoctorApply(Integer doctorID, String patientPhone) {
        this.doctorID = doctorID;
        this.patientPhone = patientPhone;
    }

    public Integer getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(Integer doctorID) {
        this.doctorID = doctorID;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
}
