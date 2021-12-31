package wow.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {

	@Select("select hospital_id from t_hospital where hospital_name=#{hospitalName}")
	Integer getHospitalID(String hospitalName);

	@Select("select user_count from t_usercount")
	int getUserCount();

	@Update("update t_usercount set user_count=user_count+1")
	int updateUserCount();

	@Insert("insert into t_publickey values(#{param1}, #{param2})")
	int newUserPublicKey(Integer userID, String userPublicKey);

	@Select("select user_publickey from t_publickey where user_id =#{userID}")
	String getPublicKey(@Param("userID") Integer userID);

	@Update("update t_publickey set user_publickey=#{userPublicKey} where user_id =#{userID}")
	int updateUserPublicKey(@Param("userID") Integer userID, @Param("userPublicKey") String userPublicKey);

}
