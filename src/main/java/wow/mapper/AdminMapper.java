package wow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
@Mapper
public interface AdminMapper {

	@Select("select user_count from t_usercount")
	Integer getUserCount();

}
