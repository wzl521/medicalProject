package wow.mapper;

import org.apache.ibatis.annotations.*;
import wow.entity.*;

import java.util.List;
import java.util.Map;
@Mapper
public interface PatientMapper {

	@Insert("insert into t_patient(patient_id, patient_name, patient_phone, patient_password, patient_state) values("
			+ "#{userID, jdbcType=INTEGER}, #{userName, jdbcType=VARCHAR}, #{userPhone, jdbcType=VARCHAR}, #{password, jdbcType=VARCHAR}, 0)")
	int newPatient(UserInfo userInfo);

	@Insert("insert into t_block(createTime, blockNumber,currentBlockHash,previousBlockHash,txID,timeStamp) values("
			+ "#{createTime, jdbcType=VARCHAR}, #{blockNumber, jdbcType=INTEGER}, #{currentBlockHash, jdbcType=VARCHAR}, #{previousBlockHash, jdbcType=VARCHAR}, #{txID, jdbcType=VARCHAR},#{timeStamp, jdbcType=VARCHAR})")
	int newBlock(Block block);

	@Select("select count(1) from t_patient where patient_phone=#{phone}")
	int ifPhone(String phone);

	@Select("select patient_id as patientID, patient_password as patientPassword, patient_state as patientState "
			+ "from t_patient where patient_phone=#{phone}")
	Patient patientLogin(@Param("phone") String phone);

	@Select("select p.patient_id as patientID, k.user_publickey as patientPublicKey from t_patient p, t_publickey k where p.patient_phone=#{phone} and k.user_id=p.patient_id")
	Map<String, Object> getPatientIDAndPublicKey(String phone);

	@Select("select apply_id as applyID, user_id as userID, apply_date as applyDate, description, sign_text as signText from t_authorize where to_user_id=${userID} and apply_state=0")
//	@Results({
//		@Result(property="userID", column="user_id", javaType=UserIN.class,
//				one=@One(select="getUserIN", fetchType=FetchType.EAGER))
//	})
	List<Authorize> getNeedUploadMedicalList(@Param("userID") int userID);

	@Select("select doctor_id as userID, doctor_name as userName from t_doctor where doctor_id=${_parameter}")
    UserIN getUserIN(int userID);

	@Select("select description from t_authorize where apply_id=${applyID}")
	String getMedicalDescription(@Param("applyID") int applyID);

	@Select("select user_publickey from t_publickey where uesr_id=${patientID}")
	String getPublicKey(@Param("patientID") int patientID);

	@Delete("delete from t_myauthorize where apply_id=${applyID}")
	int delNeedUploadMedical(@Param("applyID") int applyID);

	@Select("select patient_id from t_patient where patient_phone=#{phone}")
	Integer getPatientID(String phone);

//	@Select("select apply_id as applyID, to_user_id, apply_date as applyDate, description from t_authorize where user_id=${_parameter} and apply_state=0")
//	//@Select("select apply_id as applyID, to_user_id, apply_date as applyDate, description from t_authorize where user_id=${userID}")
//	@Results({
//		@Result(property="toUserID", column="to_user_id", javaType= UserIN.class,
//				one=@One(select="getUserIN", fetchType= FetchType.EAGER))
//	})
//	List<Authorize> getNeedAuthorizeList(@Param("userID") int userID);
@Select("select apply_id as applyID, to_user_id as toUserID, apply_date as applyDate, hash_address as hashAddress from t_myauthorize where user_id=${userID} and (apply_state=0 or apply_state=2) ")
//@Select("select apply_id as applyID, to_user_id, apply_date as applyDate, description from t_authorize where user_id=${userID}")

List<MyAuthorize> getNeedAuthorizeList(@Param("userID") int userID);

	@Select("select apply_id as applyID, to_user_id as toUserID, apply_date as applyDate, hash_address as hashAddress from t_myauthorize where user_id=${userID} and apply_state=1")
//@Select("select apply_id as applyID, to_user_id, apply_date as applyDate, description from t_authorize where user_id=${userID}")

	List<MyAuthorize> getNeedAlreadyAuthorizeList(@Param("userID") int userID);

	@Select("select apply_id as applyID,user_id as userID, to_user_id as toUserID, apply_date as applyDate, apply_state as applyState,hash_address as hashAddress from t_myauthorize where t_myauthorize.apply_id=${applyID}")
//@Select("select apply_id as applyID, to_user_id, apply_date as applyDate, description from t_authorize where user_id=${userID}")

	List<MyAuthorize> getAuthorizeList(@Param("applyID") int applyID);



//	@Select("select a.to_user_id, a.description, k.user_publickey from t_authorize a, t_publickey k where a.apply_id=${applyID} and k.user_id=a.to_user_id")
//	Map<String, Object> getAuthDescription(@Param("applyID") int applyID);

	@Update("update t_myauthorize set hash_address=#{aesDncode},apply_state=${applyState},apply_date=now()  where apply_id=${applyID}")
	int  confirmAuthorize(@Param("applyID") int applyID,@Param("applyState")int applyState,@Param("aesDncode") String aesDncode);





//	@Update("update t_authorize set description=#{description}, apply_state=1, apply_date=now() where apply_id=#{applyID}")
//	int confirmAuthorize(@Param("applyID") Integer applyID, @Param("description") String description);

	@Update("update t_myauthorize set apply_state=2, apply_date=now() where apply_id=${applyID}")
	int rejectAuthorize(@Param("applyID") int applyID);

	@Select("select patient_name as patientName, patient_phone as patientPhone, patient_picture as patientPicture  from t_patient where patient_id=#{patientID}")
	Patient getMyInfo(@Param("patientID") Integer patientID);

	@Update("update t_patient set patient_name=#{patient.patientName},patient_phone=#{patient.patientPhone},patient_picture=#{patient.patientPicture} where patient_id=#{patientID}")
	int alterInfo(@Param("patient") Patient patient, @Param("patientID") Integer patientID);

	@Select("select blockNumber,currentBlockHash,previousBlockHash,txID,timeStamp  from t_block where createTime=#{createTime}")
	Block inqureBlock(String createTime);
}
