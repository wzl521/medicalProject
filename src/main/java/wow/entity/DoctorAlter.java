package wow.entity;

public class DoctorAlter {
    private Integer doctorID;
    private String doctorName;
    private  String doctorPhone;
    private  String hospitalName;

    public DoctorAlter() {
    }

    public DoctorAlter(Integer doctorID, String doctorName, String doctorPhone, String hospitalName) {
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.doctorPhone = doctorPhone;
        this.hospitalName = hospitalName;
    }

    public Integer getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(Integer doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    @Override
    public String toString() {
        return "DoctorAlter{" +
                "doctorID=" + doctorID +
                ", doctorName='" + doctorName + '\'' +
                ", doctorPhone='" + doctorPhone + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                '}';
    }
}
