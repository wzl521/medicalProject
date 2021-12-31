package wow.mapper;

import org.apache.ibatis.annotations.*;
import wow.entity.Doctor;
import wow.entity.MyAuthorize;
import wow.entity.UserIN;
import wow.entity.UserInfo;

import java.util.List;

@Mapper
public interface DoctorMapper {

	@Select("select t_hospital.hospital_name from t_hospital,t_doctor where t_hospital.hospital_id=t_doctor.doctor_hospital and t_doctor.doctor_id=#{doctorID}")
	String  getHospitalName(@Param("doctorID") Integer doctorID);

	@Select("select t_doctor.doctor_hospital from t_doctor where  t_doctor.doctor_id=#{doctorID}")
	Integer  getDoctorHospital(@Param("doctorID") Integer doctorID);

	@Insert("insert into t_doctor(doctor_id, doctor_name, doctor_phone, doctor_password, doctor_hospital, doctor_state) values("
			+ "#{userID, jdbcType=INTEGER}, #{userName, jdbcType=VARCHAR}, #{userPhone, jdbcType=VARCHAR}, #{password, jdbcType=VARCHAR}, #{hospitalID, jdbcType=INTEGER}, 0)")
	int newDoctor(UserInfo userInfo);

	@Select("select count(1) from t_doctor where doctor_phone=#{phone}")
	int ifPhone(String phone);

	@Select("select doctor_id as doctorID, doctor_password as doctorPassword, doctor_state as doctorState "
			+ "from t_doctor where doctor_phone=#{phone}")
    Doctor doctorLogin(String phone);

	@Insert("insert into t_myauthorize(user_id, to_user_id, apply_state, apply_date, hash_address) "
			+ "values(#{userID, jdbcType=INTEGER}, #{toUserID, jdbcType=INTEGER}, 0, #{applyDate, jdbcType=TIMESTAMP}, #{hashAddress,jdbcType=VARCHAR})")
	int newAuthorize(MyAuthorize myAuthorize);

//	@Insert("insert into t_medical(patient_name, doctor_name, create_time, medical_picture, description) "
//			+ "values(#{medicalRecord.patientName, jdbcType=VARCHAR}, #{medicalRecord.doctorName, jdbcType=VARCHAR}, #{medicalRecord.createTime, jdbcType=TIMESTAMP}, #{medicalRecord.medicalPicture,jdbcType=VARCHAR}, #{medicalRecord.description, jdbcType=VARCHAR})")
//	int newMedical(@Param("medicalRecord") MedicalRecord medicalRecord);

	@Select("select user_publickey from t_publickey where user_id=${userID}")
	String getPublicKey(@Param("userID") Integer userID);

	@Select("select apply_id as applyID,apply_date as applyDate ,hash_address as hashAddress,apply_state as applyState from t_myauthorize where to_user_id=${userID} and apply_state=1")
	List<MyAuthorize> getAuthorizeList(@Param("userID") Integer userID);

	@Select("select patient_id as userID, patient_name as userName from t_patient where patient_id=${userID}")
    UserIN getUserIN(@Param("userID") Integer userID);

	@Select("select description from t_authorize where apply_id=${applyID}")
	String getAuthorizeDescription(@Param("applyID") Integer applyID);

	@Select("select doctor_name as doctorName, doctor_phone as doctorPhone, doctor_picture as doctorPicture ,doctor_hospital as doctorHospital from t_doctor where doctor_id=#{doctorID}")
    Doctor getMyInfo(@Param("doctorID") Integer doctorID);

	@Update("update t_doctor set doctor_name=#{doctor.doctorName},doctor_phone=#{doctor.doctorPhone},doctor_picture=#{doctor.doctorPicture} where doctor_id=#{doctorID}")
	int alterInfo(@Param("doctor") Doctor doctor, @Param("doctorID") Integer doctorID);

	@Update("update t_hospital h, t_doctor d set hospital_name= #{hospitalName} where d.doctor_hospital = h.hospital_id and d.doctor_hospital =#{doctorHospitalID}")
	int alterHospital(@Param("hospitalName") String hospitalName, @Param("doctorHospitalID") Integer doctorHospitalID);


}
