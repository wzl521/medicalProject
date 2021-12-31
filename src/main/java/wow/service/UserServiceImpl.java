package wow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import wow.entity.BackJSON;
import wow.entity.Doctor;
import wow.entity.Patient;
import wow.entity.UserInfo;
import wow.mapper.DoctorMapper;
import wow.mapper.PatientMapper;
import wow.mapper.UserMapper;
import wow.util.JWTUtils;
import wow.util.Value;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper um;

    @Autowired
    private PatientMapper pm;

    @Autowired
    private DoctorMapper dm;

    @Override
    @Transactional
    public BackJSON userRegister(UserInfo userInfo) {
        BackJSON json = new BackJSON(200);
        Map<String, String> data = new HashMap<>();
        int userType = userInfo.getUserType();
        int userID = um.getUserCount();
        userInfo.setUserID(userID);
        int insertResult = 0;
        // 密码经过 MD5 hash
        userInfo.setPassword(Value.MD5Hash(userInfo.getPassword()));
        if (1 == userType) {
            // 患者
            if (pm.ifPhone(userInfo.getUserPhone()) > 0) {
                System.out.println(userInfo.getUserPhone());
                System.out.println("手机号注册过");
                // 手机号注册过
                data.put("result", "1");
            } else {
                insertResult = pm.newPatient(userInfo);
                if (insertResult != 1) {
                    data.put("result", "2");
                }
            }
        } else if (2 == userType) {
            // 医生
            if (dm.ifPhone(userInfo.getUserPhone()) > 0) {
                // 手机号注册过
                data.put("result", "1");
            } else {
                insertResult = dm.newDoctor(userInfo);
                if (insertResult != 1) {
                    data.put("result", "2");
                }
            }
        } else {
            json.setData(201);
            return json;
        }
        if (1 == insertResult) {
            data.put("result", "0");
            um.updateUserCount();
            // 注册成功，生成密钥对
            Map<String, String> keyMap = Value.generateRSAKeyPair();
            String publicKey = keyMap.get("publicKey");
            String privateKey = keyMap.get("privateKey");
            if (um.newUserPublicKey(userID, publicKey) == 1) {
                data.put("privateKey", privateKey);
            } else {
                data.put("result", "2");
            }
        }
        json.setData(data);
        return json;
    }

    @Override
    @Transactional(readOnly = true)
    public BackJSON getHospitalID(String hospitalName) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        Integer hospitalID = um.getHospitalID(hospitalName);
        if (hospitalID != null) {
            data.put("hospitalID", hospitalID);
        } else {
            // 不存在，返回 0
            data.put("hospitalID", 0);
        }
        json.setData(data);
        return json;
    }

    @Override
    @Transactional(readOnly = true)
    public BackJSON userLogin(UserInfo userInfo) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        int userType = userInfo.getUserType();
        //System.out.println(userType);
        String phone = userInfo.getUserPhone();
        String password = userInfo.getPassword();
        if (1 == userType) {
            // 患者
            //System.out.println(phone);
            Patient patient = pm.patientLogin(phone);

            // System.out.println(patient.getPatientID()+"/"+patient.getPatientName()+"/"+patient.getPatientPhone());
            if (null == patient) {
                // 手机号不存在
                System.out.println("用户不存在");
                data.put("result", 2);
            } else {
                if (patient.getPatientState() != 0) {
                    // 患者处于非法状态
                    data.put("result", 3);
                } else {
                    if (!Value.MD5Verify(password, patient.getPatientPassword())) {
                        // 用户名或密码错误
                        data.put("result", 1);
                    } else {
                        Map<String,String>payload =new HashMap<>();
                        payload.put("phone",userInfo.getPassword());
                        payload.put("id",patient.getPatientID().toString());
                        //生成jwt令牌
                        String token = JWTUtils.getToken(payload);
                        data.put("token",token);//响应token
                        data.put("msg","认证成功");
                        data.put("result", 0);
                        data.put("userID", patient.getPatientID());
                    }
                }
            }
        } else if (2 == userType) {
            // 医生
            Doctor doctor = dm.doctorLogin(phone);
            if (null == doctor) {
                // 手机号不存在
                data.put("result", 2);
            } else {
                if (doctor.getDoctorState() != 0) {
                    // 患者处于非法状态
                    data.put("result", 3);
                } else {
                    if (!Value.MD5Verify(password, doctor.getDoctorPassword())) {
                        // 用户名或密码错误
                        data.put("result", 1);
                    } else {
                        Map<String,String>payload =new HashMap<>();
                        payload.put("phone",userInfo.getPassword());
                        payload.put("id",doctor.getDoctorID().toString());
                        //生成jwt令牌
                        String token = JWTUtils.getToken(payload);
                        data.put("token",token);//响应token
                        data.put("msg","认证成功");
                        data.put("result", 0);
                        data.put("userID", doctor.getDoctorID());
                    }
                }
            }
        } else {
            data.put("result", 4);
        }
        json.setData(data);
        return json;
    }

    @Override
    @Transactional(readOnly = true)
    public BackJSON getPublicKey(Integer userID) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        String publicKey = um.getPublicKey(userID);
        if (publicKey != null) {
            data.put("publicKey", publicKey);
        } else {
            // 不存在，返回 0
            data.put("publicKey", 0);
        }
        json.setData(data);
        return json;
    }

	/**
	 * 生成新的密钥对
	 * @param userID  用户的id
	 * @return
	 */
	@Override
    public BackJSON generateNewKey(Integer userID) {
        BackJSON json = new BackJSON(200);
        Map<String, String> data = new HashMap<>();
        //根据userID查询到该用户的公钥
		//String myPublickey = um.getPublicKey(userID);
		//生成新的密钥对
        Map<String, String> keyMap = Value.generateRSAKeyPair();
        String publicKey = keyMap.get("publicKey");
        String privateKey = keyMap.get("privateKey");
        //将新的公钥写入到数据库中,userID是不变的，只需更新公钥的值
        if (um.updateUserPublicKey(userID, publicKey) == 1) {
			data.put("publicKey", publicKey);
            data.put("privateKey", privateKey);
        } else {
            data.put("result", "2");
        }
        json.setData(data);
        return json;
    }
}



