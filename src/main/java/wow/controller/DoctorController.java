package wow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.BackJSON;
import wow.entity.DoctorAlter;
import wow.entity.DoctorApply;
import wow.service.DoctorService;

import java.io.IOException;


/**
 * 医生功能模块
 * @author wow
 * @date 2020年6月6日
 */

@RestController
@RequestMapping("/doctor/")
public class DoctorController {

	@Autowired
	private DoctorService ds;

	/* 医生查看自己的信息*/
	@RequestMapping(value = "/getMyInfo/{doctorID}",method = RequestMethod.GET)
	public BackJSON getMyInfo(@PathVariable("doctorID") Integer doctorID){
		return ds.getMyInfo(doctorID);
	}
	/* 医生修改自己的信息*/
	@RequestMapping(value = "/alterInfo/{doctorID}",method = RequestMethod.POST)
	public BackJSON alterInfo(@PathVariable("doctorID") Integer doctorID, @RequestBody DoctorAlter doctorAlter){
		System.out.println("doctorID:"+doctorID);
		return ds.alterInfo(doctorID,doctorAlter);
	}
	/* 医生为患者建立病历 */
//	@PostMapping("createMedical")
//	public BackJSON createMedical(@ModelAttribute CreateMedical medical) {
//
//		return ds.createMedical(medical);
//	}

//	@RequestMapping("createMedical")
//	public BackJSON createMedical(@ModelAttribute MedicalRecord medical, @RequestParam MultipartFile multipartFile) throws IOException {
//
//		return ds.createMedical(medical,multipartFile);
//	}
@RequestMapping("createMedical")
public BackJSON createMedical(@RequestParam(value = "patientPhone", required = false) String patientPhone,
							  @RequestParam(value = "doctorName", required = false) String doctorName,
							  @RequestParam(value = "patientName", required = false) String patientName,
							  @RequestParam(value = "description", required = false) String description,
//							  @RequestParam(value = "createTime", required = false) String createTime,
							  @RequestParam(value = "file",required = false) MultipartFile[] files) throws IOException {

//	for(int x = 0; x<filex.length; x++){
//		MultipartFile file = filex[x];
//	}
	return ds.createMedical(patientPhone,doctorName,patientName,description,files);
}
	/* 医生申请授权 */
	@PostMapping("applyAuthorize")
	public BackJSON applyAuthorze(@RequestBody DoctorApply doctorApply) throws Exception {
		System.out.println(doctorApply.getDoctorID()+"  "+doctorApply.getPatientPhone());
		return ds.applyAuthority(doctorApply.getDoctorID(), doctorApply.getPatientPhone());
	}

	/* 医生查看已获得授权列表  */
	@RequestMapping(value = "/authorizeList/{doctorID}",method = RequestMethod.GET)
	public BackJSON authorizedList(@PathVariable("doctorID") Integer doctorID) {
		return ds.authorizedList(doctorID);
	}

	/* 医生查看详细病历,从ipfs中下载电子病历
	 * is: Integer applyID, String doctorPrivateKey
	 * */
	@RequestMapping("getDownLoadMedical/{hashAddress}")
	public BackJSON getDownLoadMedical(@PathVariable("hashAddress") String hashAddress) throws Exception {
		System.out.println("下载地址为:"+hashAddress);
		return ds.getDownLoadMedical(hashAddress);
	}





}
