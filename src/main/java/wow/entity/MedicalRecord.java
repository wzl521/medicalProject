package wow.entity;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class MedicalRecord implements Serializable {
	private  String patientPhone;
	private Integer patientID;
	private String patientName;
	private String doctorName;
	private String createTime;
	//private String medicalPicture;
	private String description;
	private MultipartFile image;

	public String getPatientPhone() {
		return patientPhone;
	}

	public void setPatientPhone(String patientPhone) {
		this.patientPhone = patientPhone;
	}

	public MedicalRecord(String patientPhone, Integer patientID, String patientName, String doctorName, String createTime, String description, MultipartFile image) {
		this.patientPhone = patientPhone;
		this.patientID = patientID;
		this.patientName = patientName;
		this.doctorName = doctorName;
		this.createTime = createTime;
		//this.medicalPicture = medicalPicture;
		this.description = description;
		this.image = image;
	}

	public MedicalRecord(Integer patientID, String patientName, String doctorName, String createTime,  String description, MultipartFile image) {
		this.patientID = patientID;
		this.patientName = patientName;
		this.doctorName = doctorName;
		this.createTime = createTime;
		this.description = description;
		this.image = image;
	}
	public MedicalRecord() {
		super();
	}

	public String getPatientName() {
		return patientName;
	}

	public Integer getPatientID() {
		return patientID;
	}

	public void setPatientID(Integer patientID) {
		this.patientID = patientID;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getImage() {
		return  image;
	}

//	public void setImage(MultipartFile image){
////		Object streamImg = put("streamImg", Pictures.ofStream(new FileInputStream(image.getName()), PictureType.PNG)
////				.size(554, 1098).create());
//
//
//
//	}

	public void setImage(MultipartFile pictureRenderData) {
	  image = pictureRenderData;

	}
}
