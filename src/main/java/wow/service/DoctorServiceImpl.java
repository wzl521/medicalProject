package wow.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.*;
import wow.mapper.DoctorMapper;
import wow.mapper.PatientMapper;
import wow.util.MyWordUtil;
import wow.util.Value;
import wow.util.downLoadIpfs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
public class DoctorServiceImpl implements DoctorService {

	@Autowired
	private PatientMapper pm;

	@Autowired
	private DoctorMapper dm;

//	@Override
//	@Transactional
//	/**
//	 * 创建病历
//	 */
//	public BackJSON createMedical(MedicalRecord cmedical) {
//		BackJSON json = new BackJSON(200);
//		Map<String, Object> data = new HashMap<>();
//		data.put("result", 1);
//		// 封装权限信息
//		Authorize authorize = new Authorize();
//		//获取系统时间
//		long nowTime = System.currentTimeMillis();
//		System.out.println(nowTime);
//		//设置系统时间
//		authorize.setApplyDate(new Timestamp(nowTime));
//		//获取医生的ID
//		Integer doctorID = cmedical.getDoctorID();
//		//设置医生的ID
//		authorize.setUserID(new UserIN(doctorID));
//		//通过病人的手机号获取病人的ID和公钥
//		Map<String, Object> ikMap = pm.getPatientIDAndPublicKey(cmedical.getPatientPhone());
//		//通过KEY值获取patientID对应的value值，将其转为Int类型
//		Integer patientID = ((Number)ikMap.get("patientID")).intValue();
//		//设置病人的ID
//		authorize.setToUserID(new UserIN(patientID));
//		//获取patientPublicKey对于的value值
//		String patientPublicKey = (String)ikMap.get("patientPublicKey");
//		// 处理病历图片
//		MultipartFile medicalFile = cmedical.getMedicalFile();
//		// 文件名字 patientID+timestamp+filename
//		System.out.println(medicalFile.getOriginalFilename());
//		String fileName = patientID+nowTime+medicalFile.getOriginalFilename();
//		System.out.println(fileName);
//		String filePath = Value.getMedicalPicturePath();
//		File file = new File(filePath+fileName);
//
//		try {
//			//传输文件到 projectPath + "/picture/medical/fileName"
//			medicalFile.transferTo(file);
//		} catch (IllegalStateException | IOException e) {
//			System.out.println("文件传输错误 file："+file.getName());
//			e.printStackTrace();
//		}
//		//获取医疗记录
//		MedicalRecord record =cmedical.getMedical();
//
//
//		//设置医疗图片地址
//		System.out.println(fileName);
//		record.setMedicalPicture(fileName);
//		System.out.println(fileName);
//
//		//设置医疗记录时间
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		record.setCreateTime(sdf.format(new Date()));
//		// 患者公钥加密，将医嘱内容进行加密
//		String encryptDescription = Value.RSAEncrypt(record.getDescription(), patientPublicKey);
//		//设置加密后的医嘱内容
//		record.setDescription(encryptDescription);
////		//将病历加入数据库中
////		int i = dm.newMedical(record);
////		if(i==1){
////			System.out.println("新增病历成功");
////		}else{
////			throw new RuntimeException("新增病历失败");
////		}
//		// 授权信息描述，这里指病情信息
//		String description = JSONObject.toJSONString(record);
//		System.out.println(description);
//		// 医生签名
//		String signDescription = Value.RSASign(description, cmedical.getDoctorPrivateKey());
//		//设置转为json数据的description
//		authorize.setDescription(description);
//		//设置医生签名
//		authorize.setSignText(signDescription);
//		if(1==dm.newAuthorize(authorize)) {
//			data.replace("result", 0);
//			data.put("authorize",authorize);
//		}
//		json.setData(data);
//		return json;
//	}
//@Override
//@Transactional
///**
// * 创建病历
// */
//public BackJSON createMedical(MedicalRecord medical) throws FileNotFoundException {
//	BackJSON json = new BackJSON(200);
//	Map<String, Object> data = new HashMap<>();
//	String templatePath = "C:\\Users\\wzl\\Desktop\\test.docx";
//	System.out.println("======开始创建病历=====");
//	String fileDir = "C:\\Users\\wzl\\Desktop";
//	String fileName = "测试文档";
//	String wordPath = WordUtil.createWord(templatePath, fileDir, fileName, medical);
//	System.out.println("生成文档路径：" + wordPath);
//	Document document =new Document();
//	document.loadFromFile(wordPath);
//
//	String path ="C:\\Users\\wzl\\Desktop\\outputToPdf.pdf";
//
//	document.saveToFile(path, FileFormat.PDF);
//	System.out.println("转换pdf成功");
//	System.out.println("=====创建病历成功=====");
//	data.put("result","1");
//	json.setData(data);
//	return json;
//}
@Override
@Transactional
/**
 * 创建病历
 */
public BackJSON createMedical(String patientPhone,String doctorName,String patientName,String description, MultipartFile[]files) throws IOException {
	BackJSON json = new BackJSON(200);
	Map<String, Object> data = new HashMap<>();
	System.out.println("======开始创建病历=====");
	Integer patientID = pm.getPatientID(patientPhone);
	MedicalRecord medical =new MedicalRecord();
	medical.setPatientID(patientID);
	medical.setPatientName(patientName);
	medical.setDoctorName(doctorName);
	medical.setDescription(description);
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
	medical.setCreateTime(df.format(new Date()));
	//创建pdf电子病历
	MyWordUtil.changeWord(medical,files);

	System.out.println("=====创建病历成功=====");
	data.put("result","1");
	json.setData(data);
	return json;
}
	@Override
	@Transactional
	/**
	 * 医生申请授权
	 */
	public BackJSON applyAuthority(Integer doctorID, String patientPhone) throws Exception {
			System.out.println("doctorID is "+doctorID+",patientPhone is"+patientPhone);
		BackJSON json = new BackJSON(200);
		Map<String, Object> data = new HashMap<>();
		//根据病人手机号获得病人ID
		Integer patientID = pm.getPatientID(patientPhone);
		if(null==patientID) {
			//患者不存在
			System.out.println("患者不存在");
			data.put("result", 1);
		} else {
			Map<String,String> mynewRecord = Value.getRecordId(patientID);
			if (mynewRecord == null) {
				System.out.println("患者不存在病历数据");
				data.put("result", 2);
			}
			System.out.println(mynewRecord);
			String patientId = mynewRecord.get("patient_id");
			String hashAddress = mynewRecord.get("hash_address");
			String createTime = mynewRecord.get("create_time");
			System.out.println("患者id："+patientId);
			System.out.println("加密后文件访问地址:"+hashAddress);

			//新建医疗记录对象
			MyAuthorize myAuthorize = new MyAuthorize();
			myAuthorize.setUserID(Integer.valueOf(patientId));
			myAuthorize.setToUserID(doctorID);
			myAuthorize.setApplyState(0);
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			myAuthorize.setApplyDate(simpleDateFormat.format(new Date()));
			myAuthorize.setHashAddress(hashAddress);

			//将信息存入授权列表中
			if(1==dm.newAuthorize(myAuthorize)) {
				System.out.println("信息插入成功");
				data.put("result", 0);
			}
//			//连接区块链网络
//			Contract contract = Value.getContract();
//			try {
//				byte[] queryResult = contract.evaluateTransaction("getRecordId", String.valueOf(patientID));
//				if(0==queryResult.length) {
//					// 患者不存在病历数据
//					data.put("result", 2);
//				} else {
//					//将获取的数据转为json格式
//					//JSONObject jsonResult = JSONObject.parseObject(new String(queryResult, StandardCharsets.UTF_8));
//					JSONObject jsonResult= (JSONObject) JSONArray.parseArray(new String(queryResult,StandardCharsets.UTF_8)).get(0);
//					System.out.println(jsonResult);
//					//新建医疗记录对象
//					MyAuthorize myAuthorize = new MyAuthorize();
//					//设置patientname
//					myAuthorize.setUserID(patientID);
//					myAuthorize.setToUserID(doctorID);
//					myAuthorize.setApplyState(0);
//					myAuthorize.setApplyDate(new Timestamp(System.currentTimeMillis()));
//					myAuthorize.setHashAddress(jsonResult.getString("hash_address"));
//
//					System.out.println(JSONObject.toJSONString(myAuthorize));
//					//将信息存入授权列表中
//					if(1==dm.newAuthorize(myAuthorize)) {
//						System.out.println("信息插入成功");
//						data.put("result", 0);
//					}
//				}
//			} catch (ContractException e) {
//				e.printStackTrace();
//			}
		}
		json.setData(data);
		return json;
	}

	@Override
	@Transactional(readOnly=true)
	/**
	 * 查看已授权列表
	 */
	public BackJSON authorizedList(Integer doctorID) {
		BackJSON json = new BackJSON(200);
		JSONObject data = new JSONObject();
		List<MyAuthorize> authList = dm.getAuthorizeList(doctorID);
		System.out.println(authList);
		data.put("list", authList);
		json.setData(data);
		return json;
	}

//	@Override
//	@Transactional(readOnly=true)
//	/**
//	 * 获取病历详细信息
//	 */
//	public BackJSON getMedicalDescription(Integer applyID, String doctorPrivateKey) {
//		BackJSON json = new BackJSON(200);
//		JSONObject data = new JSONObject();
//		//根据applyID获取详细说明desciption
//		String desciption = dm.getAuthorizeDescription(applyID);
//		System.out.println(desciption);
//		if(desciption!=null) {
//			//将desciption转化为json格式
//			MedicalRecord record = JSONObject.parseObject(desciption, MedicalRecord.class);
//			//获取图片的全路径
//			String picturePath = Value.getMedicalPicturePath();
//			//设置医疗记录图片路径
//			//record.setMedicalPicture(picturePath+record.getMedicalPicture());
//			//获取医疗记录
//			String descriptDescription = record.getDescription();
//			//使用医生的私钥对医疗记录Description进行解密
//			record.setDescription(Value.RSADecrypt(descriptDescription, doctorPrivateKey));
//			data.put("record", record);
//		} else {
//			data.put("record", null);
//		}
//		json.setData(data);
//		return json;
//	}

	@Override
	@Transactional(readOnly=true)
	/**
	 * 获取病历详细信息,将电子病历下载到本地
	 */
	public BackJSON getDownLoadMedical(String hashAddress) throws Exception {
		BackJSON json = new BackJSON(200);
		JSONObject data = new JSONObject();
		data.put("result",1);
		downLoadIpfs.downLoad(hashAddress);
		json.setData(data);
		return json;
	}

	@Override
	@Transactional(readOnly=true)
	/**
	 * 查看自己的信息
	 */
	public BackJSON getMyInfo(Integer doctorID) {
		BackJSON json = new BackJSON(200);
		Map<String, Object> data = new HashMap<>();
		try {
			Doctor myInfo = dm.getMyInfo(doctorID);
			System.out.println("myInfo是：" + myInfo);
//			List<Doctor> list = new ArrayList<>();
//			list.add(myInfo);
//            System.out.println(list);
//			data.put("Doctor",list);
			DoctorInfo doctorInfo = new DoctorInfo();
			doctorInfo.setDoctorID(doctorID);
			doctorInfo.setDoctorName(myInfo.getDoctorName());
			String hospitalName = dm.getHospitalName(doctorID);
			doctorInfo.setHospitalName(hospitalName);
			doctorInfo.setDoctorPhone(myInfo.getDoctorPhone());
			data.put("doctorInfo",doctorInfo);
			System.out.println("doctorInfo是：" + doctorInfo);

//            Doctor userdata = new Doctor();
//            userdata.setDoctorID(doctorID);
//            userdata.setDoctorName(myInfo.getDoctorName());
//            userdata.setDoctorHospital(myInfo.getDoctorHospital());
//            userdata.setDoctorPhone(myInfo.getDoctorPhone());
//            System.out.println(userdata);
//			data.put("doctorID",userdata.getDoctorID());
//            data.put("doctorName",userdata.getDoctorName());
//            data.put("hospitalName",userdata.getDoctorHospital());
//            data.put("doctorPhone",userdata.getDoctorPhone());

//			data.put("doctorID",doctorID);
//			System.out.println(data);
//			data.put("doctorName",myInfo.getDoctorName());
//			data.put("doctorPhone",myInfo.getDoctorPhone());
////			data.put("doctorPicture",myInfo.getDoctorPicture());
//			String hospitalName = dm.getHospitalName(doctorID);
//			data.put("hospitalName",hospitalName);
//			data.put("Doctor",myInfo);
			System.out.println("data是：" + data);
			json.setData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	@Override
	/**
	 * 修改个人信息
	 */
	public BackJSON alterInfo(Integer doctorID,DoctorAlter doctorAlter) {
		System.out.println("doctorAlter:"+doctorAlter.toString());
		BackJSON json = new BackJSON(200);
		Map<String, Object> data = new HashMap<>();
		Doctor myInfo = dm.getMyInfo(doctorID);
		String hospitalName = doctorAlter.getHospitalName();

		//通过doctorID获取医院编号
		Integer doctorHospitalID = dm.getDoctorHospital(doctorID);
		int i1 = dm.alterHospital(hospitalName, doctorHospitalID);

		myInfo.setDoctorName(doctorAlter.getDoctorName());
		myInfo.setDoctorPhone(doctorAlter.getDoctorPhone());
//		myInfo.setDoctorName("武武武");
//		myInfo.setDoctorPicture("C:\\Users\\wzl\\Desktop\\1.jpg");
		int i = dm.alterInfo(myInfo,doctorID);
		if (1==i||1==i1){
			//修改成功
			data.put("result",0);
			//data.put("myinfo",myInfo);
			System.out.println(myInfo.toString());
		}else {
			data.put("result",1);
		}
		json.setData(data);
		return json;
	}


}
