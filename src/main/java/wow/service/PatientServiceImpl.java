package wow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.*;
import wow.mapper.DoctorMapper;
import wow.mapper.PatientMapper;
import wow.util.AESUtil;
import wow.util.IPFSUtil;
import wow.util.Value;
import wow.util.downLoadIpfs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
public class PatientServiceImpl implements PatientService {

    //    private static final  int n=6;
//    private static final  int m=5;
    private int length = 0;
    @Autowired
    private PatientMapper pm;

    @Autowired
    private DoctorMapper dm;

    @Override
    public BackJSON needConfirmMedical(Integer patientID, String patientPrivateKey) {
        return null;
    }

    //    @Override
//    @Transactional
//    /**
//     * 待确认病历
//     */
//    public BackJSON needConfirmMedical(Integer patientID, String patientPrivateKey) {
//        BackJSON json = new BackJSON(200);
//        Map<String, Object> data = new HashMap<>();
//        //通过病人ID查询需要上传的病历列表
//        List<Authorize> authorizeList = pm.getNeedUploadMedicalList(patientID);
//        //遍历病历列表
//        for (Authorize auth : authorizeList) {
//            // 获取病历
//            String medicalDescription = auth.getDescription();
//            System.out.println(medicalDescription);
//            //根据user_id查询医生的公钥
////            System.out.println(auth.getUserID().getUserID());
////            String doctorPublicKey = dm.getPublicKey(auth.getUserID().getUserID());
//            String doctorPublicKey=dm.getPublicKey(10);
//            // 使用医生的公钥验证医生签名
//            System.out.println(auth.getSignText());
//            System.out.println(auth);
//            boolean verifyResult = Value.RSACheckSign(medicalDescription, auth.getSignText(), doctorPublicKey);
//            if (verifyResult) {
//                System.out.println("验证医生签名成功");
//                //验证成功之后将签名置为空
//                auth.setSignText(null);
//                //将病历转为json格式，并保存下来
//                MedicalRecord record = JSONObject.parseObject(medicalDescription, MedicalRecord.class);
////				System.out.println(patientPrivateKey);
//                // 患者私钥解密，获取到明文病历内容
//                String DescryptMedicalDescription = Value.RSADecrypt(record.getDescription(), patientPrivateKey);
//                System.out.println(DescryptMedicalDescription);
//                //将明文病历内容存到Description中
//                record.setDescription(DescryptMedicalDescription);
//                //获取图片的绝对地址
//                String picturePath = Value.getMedicalPicturePath();
//                //设置图片的地址
//                record.setMedicalPicture(picturePath + record.getMedicalPicture());
//                //将病历内容保存到MedicalRecord中
//                auth.setRecord(record);
//                //将Description中的内容置为空
//                auth.setDescription(null);
//            } else {
//                //验证签名失败，不往MedicalRecord中保存内容
//                auth.setRecord(null);
//            }
//        }
//        data.put("list", authorizeList);
//        json.setData(data);
//        return json;
//    }
    @Override
    @Transactional
    /**
     * 上传病历到ipfs,然后将获取到的文件hash进行加密，将加密后的hash值上链
     */
    public BackJSON uploadMedical(Integer patientID, MultipartFile[] files, String createTime) throws IOException {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        data.put("result", 3);
        try {
            String dirPath = "C:\\Users\\wzl\\Desktop\\";
            String multipartFileName = files[0].getOriginalFilename();
            String fileName = dirPath + multipartFileName;
            System.out.println(fileName);
            String hash = IPFSUtil.add(fileName);
            System.out.println("生成的文件hash值为:" + "\n" + hash);//http://192.168.163.137:8080/ipfs/QmW4DixeYbFFwSMarFRekSrw7WAiaBXW6F4Yt1o2QrBEwP
            String ipfsDirPath = "http://192.168.163.137:8080/ipfs/";
            String hashContent = ipfsDirPath + hash;
            System.out.println("文件的完整地址为" + hashContent);
            System.out.println("加密");
            //使用AES算法加密
            String hashAddress = AESUtil.AESEncode(hashContent);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            createTime = df.format(new Date());
            System.out.println("上传时间为:" + df.format(new Date()));// new Date()为获取当前系统时间
            //将文件hash值加密后上链，key(patientID)===>value(aesHashEncode);

            //调用uploadMedicalRecord 上传病历至区块链网络
            Block block = Value.uploadMedicalRecord(patientID, hashAddress, createTime);
            block.setCreateTime(createTime);

            //将区块高度、区块hash等内容存储至mysql数据库中,其中将createTime作为主键
            int i = pm.newBlock(block);
            if (i == 1) {
                System.out.println("插入区块数据成功");
                data.replace("result", 0);
            } else {
                data.replace("result", 2);
            }


            //连接到区块链网络
//            Contract contract = Value.getContract();
//                try {
//                System.out.println("调用智能合约");
//                //调用智能合约，上传病历
//
//                    byte[] queryResult = contract.createTransaction("uploadMedicalRecord").submit(String.valueOf(patientID), hashAddress, createTime);
//                String queryStr = new String(queryResult, StandardCharsets.UTF_8);
//                if (queryStr.equals("success")) {
//                    data.replace("result", 0);
//                    //如果上传病历成功，则吧t_authorize表中的内容删去
//                    System.out.println("病历上传成功");
//                } else {
//                    data.replace("result", 2);
//                }
//            } catch (ContractException | TimeoutException | InterruptedException e) {
//                e.printStackTrace();
//            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.put("result", 0);
        json.setData(data);
        return json;
    }

//    @Override
//    @Transactional
//    /**
//     * 上传病历
//     */
//    public BackJSON uploadMedical(Integer applyID, Integer patientID, String patientPrivateKey) {
//        BackJSON json = new BackJSON(200);
//        Map<String, Object> data = new HashMap<>();
//        data.put("result", 3);
//        //根据applyID查询病历描述{"createTime":"2021/03/29",
//        // "description":"W3lDEPWFvAm2fJDsGKpFEfBzPl4aZzR0q/TXDxxLTcVk2LuujLHGhwCY25LlmGi7k84ovwY+ApJlKkVAtP+20zBpyAfFS4jR2z2aXg8C5k8jStTw6p1/YXaYIY24X4VCjhO0UfNR64T7u/lzK3YLZPBV1YbKN/h25vrg4aCSfTo=",
//        // "doctorName":"武志立","medicalPicture":"16170084494421.jpg","patientName":"岳鹏帆"}
//        String medicalDescription = pm.getMedicalDescription(applyID);
//        if (medicalDescription != null) {
//            //获取其中的医疗记录，将json格式其解析为一个对象
//            MedicalRecord record = JSONObject.parseObject(medicalDescription, MedicalRecord.class);
//            System.out.println(record.toString());
//            // 加密病历描述
//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                System.out.println("调用智能合约");
//                //调用智能合约，上传病历
//                byte[] queryResult = contract.createTransaction("uploadMedicalRecord").submit(String.valueOf(patientID), record.getPatientName(), record.getDoctorName(), record.getCreateTime(), record.getMedicalPicture(), record.getDescription());
//                String queryStr = new String(queryResult, StandardCharsets.UTF_8);
//                if (queryStr.equals("success")) {
//                    data.replace("result", 0);
//                    //如果上传病历成功，则吧t_authorize表中的内容删去
//                    System.out.println("病历上传成功");
//                    pm.delNeedUploadMedical(applyID);
//                } else {
//                    data.replace("result", 2);
//                }
//            } catch (ContractException | TimeoutException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        json.setData(data);
//        return json;
//    }

    //    @Override
//    /**
//     * 查询病历
//     * type：查询的类型：1.查询最新病历 ，2.查询历史病历
//     * patientID:病人的ID
//     * patientPrivateKey:病人的私钥
//     */
//    public BackJSON inquireMedical(Integer type, Integer patientID, String patientPrivateKey) {
//        BackJSON json = new BackJSON(200);
//        Map<String, Object> data = new HashMap<>();
//        if (1 == type) {
//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                byte[] queryResult = contract.evaluateTransaction("getRecordId", String.valueOf(patientID));
//                JSONObject jsonResult= (JSONObject) JSONArray.parseArray(new String(queryResult,StandardCharsets.UTF_8)).get(0);
//                //JSONObject[] jsonResult = new JSONObject[]{JSONObject.parseObject(new String(queryResult, StandardCharsets.UTF_8))};
//                MedicalRecord medicalRecord = new MedicalRecord();
//                medicalRecord.setPatientName(jsonResult.getString("patient_name"));
//                medicalRecord.setDoctorName(jsonResult.getString("doctor_name"));
//                medicalRecord.setCreateTime(jsonResult.getString("create_time"));
//                medicalRecord.setMedicalPicture(Value.getMedicalPicturePath() + jsonResult.getString("medical_picture"));
//                //使用自己的私钥解密
//                medicalRecord.setDescription(Value.RSADecrypt(jsonResult.getString("description"), patientPrivateKey));
//                data.put("result", 0);
//                data.put("record", medicalRecord);
//            } catch (ContractException e) {
//                data.put("result", 2);
//                e.printStackTrace();
//            }
//        } else {
//            // TODO：历史病历
//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                byte[] queryResult = contract.evaluateTransaction("getAllHistoryRecordId", String.valueOf(patientID));
//                for (int length = queryResult.length; length > 0; length--) {
//                    JSONObject jsonResult = (JSONObject) JSONArray.parseArray(new String(queryResult, StandardCharsets.UTF_8)).get(length);
//                    //JSONObject[] jsonResult = new JSONObject[]{JSONObject.parseObject(new String(queryResult, StandardCharsets.UTF_8))};
//                    MedicalRecord medicalRecord = new MedicalRecord();
//                    medicalRecord.setPatientName(jsonResult.getString("patient_name"));
//                    medicalRecord.setDoctorName(jsonResult.getString("doctor_name"));
//                    medicalRecord.setCreateTime(jsonResult.getString("create_time"));
//                    medicalRecord.setMedicalPicture(Value.getMedicalPicturePath() + jsonResult.getString("medical_picture"));
//                    medicalRecord.setDescription(Value.RSADecrypt(jsonResult.getString("description"), patientPrivateKey));
//                    data.put("result", 0);
//                    data.put("record", medicalRecord);
//                }
//            } catch (ContractException e) {
//                data.put("result", 2);
//                e.printStackTrace();
//            }
//        }
//
//
//        json.setData(data);
//        return json;
//    }

    public BackJSON inquireBlock(String createTime) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();

        String newData = createTime.substring(0, 10);
        System.out.println(newData);
        String substring = createTime.substring(10);
        System.out.println(substring);
        String s = newData + " " + substring;
        System.out.println(s);

        Block block = pm.inqureBlock(s);
        System.out.println("block为：" + block);
        data.put("block", block);
        json.setData(data);
        return json;
    }

    @Override
/**
 * 查询病历
 * type：查询的类型：1.查询最新病历 ，2.查询历史病历
 * patientID:病人的ID
 *
 */
    public BackJSON inquireMedical(Integer patientID, Integer type) throws IllegalAccessException, InstantiationException, InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, CryptoException, ProposalException, ExecutionException, TransactionException, NoSuchMethodException, org.bouncycastle.crypto.CryptoException, IOException, InterruptedException, InvocationTargetException, InvalidArgumentException {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        if (1 == type) {
            //调用智能合约查询最新病历

            Map<String, String> mynewRecord = Value.getRecordId(patientID);
            System.out.println(mynewRecord);
            String patientId = mynewRecord.get("patient_id");
            String hashAddress = mynewRecord.get("hash_address");
            String createTime = mynewRecord.get("create_time");
            System.out.println("患者id：" + patientId);
            System.out.println("加密后文件访问地址:" + hashAddress);

            String aesDncode = AESUtil.AESDncode(hashAddress);
            System.out.println("解密后文件访问地址为：" + aesDncode);

            Map<String, Object> medical = new HashMap<>();
            medical.put("patientId", patientId);
            medical.put("createTime", createTime);
            medical.put("hashAddress", aesDncode);
            data.put("medical", medical);
            data.put("result", 0);

            //连接到区块链网络
            // Contract contract = Value.getContract();
            //                byte[] queryResult = contract.evaluateTransaction("getRecordId", String.valueOf(patientID));
//                JSONObject jsonResult = (JSONObject) JSONArray.parseArray(new String(queryResult, StandardCharsets.UTF_8)).get(0);
//                //JSONObject[] jsonResult = new JSONObject[]{JSONObject.parseObject(new String(queryResult, StandardCharsets.UTF_8))};
//                //患者id
//                String patientId = jsonResult.getString("patient_id");
//                System.out.println("患者id为:" + patientId);
//                //创建电子病历的时间
//                String createTime = jsonResult.getString("create_time");
//                //加密后的文件访问地址
//                String hashAddress = jsonResult.getString("hash_address");
//                System.out.println("加密后的文件访问地址为：" + hashAddress);
//                System.out.println("解密后的文件访问地址为:");
//                String aesDncode = AESUtil.AESDncode(hashAddress);
//                Map<String, Object> medical = new HashMap<>();
//                medical.put("patientId", patientId);
//                medical.put("createTime", createTime);
//                medical.put("hashAddress", aesDncode);
//                data.put("medical", medical);
//                data.put("result", 0);

//            for (int i = 0; i < newRecord.size(); i++) {
//                Object o = newRecord.get(i);
//                System.out.println("值为："+o);
//            }
//            for (Object entry : newRecord.entrySet()) {
//                System.out.println("键值对："+entry );
//                System.out.println("value:"+newRecord.get(entry));
//
//            }

//            for (String key : newRecord.keySet()) {
//                String patientId = newRecord.get("patient_id").toString();
//                System.out.println("患者id为:" + patientId);
//                String createTime = newRecord.get("create_time").toString();
//                String hashAddress = newRecord.get("hash_address").toString();
//                System.out.println("加密后的文件访问地址为：" + hashAddress);
//                System.out.println("解密后的文件访问地址为:");
//                String aesDncode = AESUtil.AESDncode(hashAddress);
//                Map<String, Object> medical = new HashMap<>();
//               // medical.put("patientId", patientId);
//                medical.put("createTime", createTime);
//                medical.put("hashAddress", aesDncode);
//                data.put("medical", medical);
//                data.put("result", 0);
//            }

        } else {
            // TODO：历史病历
            //调用查询历史病历智能合约
            Map<String, String>[] allRecordById = Value.getAllRecordById(patientID);
            Map<String, String>[] mydata = new HashMap[allRecordById.length];

            for (int i = 0; i < allRecordById.length; i++) {
                String patientId = allRecordById[i].get("patient_id");
                String hashAddress = allRecordById[i].get("hash_address");
                String createTime = allRecordById[i].get("create_time");
                String aesDncode = AESUtil.AESDncode(hashAddress);

                mydata[i] = new HashMap<>();
                mydata[i].put("patientId", patientId);
                mydata[i].put("hashAddress", aesDncode);
                mydata[i].put("createTime", createTime);
                System.out.println("患者id：" + patientId);
                System.out.println("加密后文件访问地址:" + hashAddress);
                System.out.println("解密后文件访问地址为：" + aesDncode);
            }
            data.put("result", 0);
            data.put("mydata", mydata);
            data.put("size", allRecordById.length);


//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                byte[] queryResult = contract.evaluateTransaction("getAllHistoryRecordId", String.valueOf(patientID));
//                String s = new String(queryResult);
//                System.out.println("s的长度为" + s.length());
//                System.out.println("长度为" + queryResult.length);
////                Map<String, Object> medical = new HashMap<>();
//                JSONArray jsonResult1 = JSONArray.parseArray(new String(queryResult, StandardCharsets.UTF_8));
//                int size = jsonResult1.size();
//                System.out.println("size长度为，历史病历一共"+size+"条");
//                Map<String,String>[] mydata = new HashMap[size];
//                for (length = 0; length < size; length++) {
//                    JSONObject jsonResult = (JSONObject) JSONArray.parseArray(new String(queryResult, StandardCharsets.UTF_8)).get(length);
//                    //患者id
//                    String patientId = jsonResult.getString("patient_id");
//
//                    mydata[length]=new HashMap<>();
//                    mydata[length].put("patientId",patientId);
////                    medical.put("patientId", patientId);
//                    System.out.println("患者id为:" + patientId);
//                    //创建电子病历的时间
//                    String createTime = jsonResult.getString("create_time");
//                    mydata[length].put("createTime",createTime);
////                    medical.put("createTime", createTime);
//                    System.out.println("时间为" + createTime);
//                    //加密后的文件访问地址
//                    String hashAddress = jsonResult.getString("hash_address");
//                    System.out.println("解密后的文件访问地址为:");
//                    String aesDncode = AESUtil.AESDncode(hashAddress);
//
//                    mydata[length].put("hashAddress",aesDncode);
////                    medical.put("hashAddress", aesDncode);
////                    mydata[length] = (HashMap) medical;
//                    System.out.println(mydata[length]);
//                }
//                data.put("result", 0);
//                data.put("mydata", mydata);
//                data.put("size",size);
//            } catch (ContractException e) {
//                data.put("result", 2);
//                e.printStackTrace();
//            }
        }


        json.setData(data);
        return json;
    }

    //    @Override
//    @Transactional(readOnly = true)
//    /**
//     * 获取授权列表
//     */
//    public BackJSON authorizeList(Integer patientID) {
//        BackJSON json = new BackJSON(200);
//        JSONObject data = new JSONObject();
//        //获取需要待授权的列表
//        List<Authorize> authList = pm.getNeedAuthorizeList(patientID);
//        //循环遍历每个待授权的医疗记录
//        for (Authorize auth : authList) {
//            //获取医疗记录说明
//            String description = auth.getDescription();
//            //将医疗记录说明转为json格式，并将其转为一条医疗记录
//            MedicalRecord record = JSONObject.parseObject(description, MedicalRecord.class);
//            //设置该医疗记录中的图片地址
//            record.setMedicalPicture(Value.getMedicalPicturePath() + record.getMedicalPicture());
//            //对医疗记录中描述使用病人的私钥进行解密，设置该医疗记录中的描述
//            record.setDescription(Value.RSADecrypt(record.getDescription(), patientPrivateKey));
//            //将整条记录存储到medicalRecord对象中
//            auth.setRecord(record);
//            //将授权列表中的Description置为空
//            auth.setDescription(null);
//        }
//        data.put("list", authList);
//        json.setData(data);
//        return json;
//    }
    @Override
    @Transactional(readOnly = true)
/**
 * 查看待授权列表
 */
    public BackJSON authorizeList(Integer patientID) {
        BackJSON json = new BackJSON(200);
        JSONObject data = new JSONObject();
        //获取需要待授权的列表
        List<MyAuthorize> authList = pm.getNeedAuthorizeList(patientID);
        if(authList.size()==0){
            data.put("authList", null);
            json.setData(data);
            return json;
        }
        System.out.println(authList);
        System.out.println(authList.size());
        Map<String,String> map[] =new HashMap[authList.size()];
        for (int i =0;i<authList.size();i++) {

            map[i]=new HashMap<>();
            map[i].put("applyID", authList.get(i).getApplyID().toString());
            map[i].put("toUserID",authList.get(i).getToUserID().toString());
            map[i].put("applyDate",authList.get(i).getApplyDate());
            map[i].put("hashAddress",authList.get(i).getHashAddress());
        }
        data.put("authList", map);
        json.setData(data);
        return json;
    }

    /**
     * 查看已授权列表
     */
    public BackJSON authorizeAlreadyList(Integer patientID) {
        BackJSON json = new BackJSON(200);
        JSONObject data = new JSONObject();
        //获取需要待授权的列表
        List<MyAuthorize> authList = pm.getNeedAlreadyAuthorizeList(patientID);
        System.out.println(authList);
//        Map<String,String> map[] =new HashMap[authList.size()];
//        for (int i =0;i<authList.size();i++) {
//
//            map[i].put("applyID", authList.get(i).getApplyID().toString());
//            map[i].put("toUserID",authList.get(i).getToUserID().toString());
//            map[i].put("applyDate",authList.get(i).getApplyDate());
//            map[i].put("hashAddress",authList.get(i).getHashAddress());
//        }
        data.put("list", authList);
        json.setData(data);
        return json;
    }

    //    @Override
//    @Transactional
//    /**
//     * 处理授权
//     * 1-同意 2-拒绝 3-删除
//     */
//    public BackJSON dealAuthorize(Integer applyID, String patientPrivateKey, Integer dealType) {
//        BackJSON json = new BackJSON(200);
//        JSONObject data = new JSONObject();
//        data.put("result", 1);
//        if (1 == dealType) {
//            // 同意
//            //根据applyID获取授权的内容,to_user_id:要授权的医生编号,user_publickey：医生的公钥,description：病历的描述
//            Map<String, Object> dkMap = pm.getAuthDescription(applyID);
//            //将病历内容存储到MedicalRecord中
//            MedicalRecord record = JSONObject.parseObject((String) dkMap.get("description"), MedicalRecord.class);
//            //使用患者私钥解密
//            String decryptDescription = Value.RSADecrypt(record.getDescription(), patientPrivateKey);
//            //使用医生公钥加密
//            record.setDescription(Value.RSAEncrypt(decryptDescription, (String) dkMap.get("user_publickey")));
//            //将MedicalRecord转化为string类型
//            String description = JSONObject.toJSONString(record);
//            if (1 == pm.confirmAuthorize(applyID, description)) {
//                data.replace("result", 0);
//            }
//        } else if (2 == dealType) {
//            // 拒绝
//            if (1 == pm.rejectAuthorize(applyID)) {
//                data.replace("result", 0);
//            }
//        } else if (3 == dealType) {
//            // 删除
//            if (1 == pm.delNeedUploadMedical(applyID)) {
//                data.replace("result", 0);
//            }
//        } else {
//            // 未知处理
//            data.replace("result", 2);
//        }
//        json.setData(data);
//        return json;
//    }
    @Override
    @Transactional
/**
 * 处理授权
 * 1-同意 2-拒绝 3-删除
 */
    public BackJSON dealAuthorize(Integer applyID, Integer dealType) throws Exception {
        BackJSON json = new BackJSON(200);
        JSONObject data = new JSONObject();
        data.put("result", 1);

        if (1 == dealType) {
            List<MyAuthorize> authorizeList = pm.getAuthorizeList(applyID);
            //获取加密的hash地址
            String hashAddress = authorizeList.get(0).getHashAddress();
            System.out.println(hashAddress);
            //解密hash地址
            String aesDncode = AESUtil.AESDncode(hashAddress);

            if (1 == pm.confirmAuthorize(applyID, 1, aesDncode)) {
                System.out.println("更新成功");
                data.replace("result", 0);
            }

            List<MyAuthorize> myAuthorizeList = pm.getAuthorizeList(applyID);
            Integer apply_id = applyID;
            Integer doctor_id = myAuthorizeList.get(0).getToUserID();
            Integer patient_id = myAuthorizeList.get(0).getUserID();
            String apply_time = myAuthorizeList.get(0).getApplyDate();
            Integer applyState = myAuthorizeList.get(0).getApplyState();
            System.out.println(apply_id + "\t" + doctor_id + "\t" + patient_id + "\t" + apply_time + "\t" + applyState);

            Block block = Value.uploadapplyRecord(patient_id, doctor_id, applyID, apply_time, applyState);
            block.setCreateTime(apply_time);
            int i = pm.newBlock(block);
            if(i==1){
                System.out.println("授权记录上传成功");
                System.out.println("插入区块数据成功");
                data.replace("result",0);
            }else {
                data.replace("result",2);
            }
//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                System.out.println("调用智能合约");
////                List<MyAuthorize> myAuthorizeList = pm.getAuthorizeList(applyID);
////                String apply_id = String.valueOf(applyID);
////                String doctor_id = String.valueOf(myAuthorizeList.get(0).getToUserID());
////                String patient_id = String.valueOf(myAuthorizeList.get(0).getUserID());
////                String apply_time = myAuthorizeList.get(0).getApplyDate().toString();
////                Integer applyState = myAuthorizeList.get(0).getApplyState();
////                System.out.println(apply_id + "\t" + doctor_id + "\t" + patient_id + "\t" + apply_time + "\t" + applyState);
////                //调用智能合约，上传病历
//                byte[] queryResult = contract.createTransaction("uploadapplyRecord").submit(patient_id, doctor_id, apply_id, apply_time, "同意");
//                String queryStr = new String(queryResult, StandardCharsets.UTF_8);
//                if (queryStr.equals("success")) {
//                    data.replace("result", 0);
//                    //如果上传病历成功，则吧t_authorize表中的内容删去
//                    System.out.println("授权记录上传成功");
//                } else {
//                    data.replace("result", 2);
//                }
//            } catch (ContractException | TimeoutException | InterruptedException e) {
//                e.printStackTrace();
//            }
        } else if (2 == dealType) {
            // 拒绝
            if (1 == pm.rejectAuthorize(applyID)) {
                data.replace("result", 0);
            }
            List<MyAuthorize> myAuthorizeList = pm.getAuthorizeList(applyID);
            Integer apply_id = applyID;
            Integer doctor_id = myAuthorizeList.get(0).getToUserID();
            Integer patient_id = myAuthorizeList.get(0).getUserID();
            String apply_time = myAuthorizeList.get(0).getApplyDate().toString();
            Integer applyState = myAuthorizeList.get(0).getApplyState();
            System.out.println(apply_id + "\t" + doctor_id + "\t" + patient_id + "\t" + apply_time + "\t" + applyState);

            Block block = Value.uploadapplyRecord(patient_id, doctor_id, applyID, apply_time, applyState);
            block.setCreateTime(apply_time);
            int i = pm.newBlock(block);
            if(i==1){
                System.out.println("授权记录上传成功");
                System.out.println("插入区块数据成功");
                data.replace("result",0);
            }else {
                data.replace("result",2);
            }
//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                System.out.println("调用智能合约");
//                List<MyAuthorize> myAuthorizeList = pm.getAuthorizeList(applyID);
//                //调用智能合约，上传病历
//                byte[] queryResult = contract.createTransaction("uploadapplyRecord").submit(String.valueOf(myAuthorizeList.get(0).getUserID()), String.valueOf(myAuthorizeList.get(0).getToUserID()), String.valueOf(applyID), myAuthorizeList.get(0).getApplyDate().toString(), "拒绝");
//                String queryStr = new String(queryResult, StandardCharsets.UTF_8);
//                if (queryStr.equals("success")) {
//                    data.replace("result", 0);
//                    //如果上传病历成功，则吧t_authorize表中的内容删去
//                    System.out.println("授权记录上传成功");
//                } else {
//                    data.replace("result", 2);
//                }
//            } catch (ContractException | TimeoutException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else if (3 == dealType) {
//
//            //连接到区块链网络
//            Contract contract = Value.getContract();
//            try {
//                System.out.println("调用智能合约");
//                List<MyAuthorize> myAuthorizeList = pm.getAuthorizeList(applyID);
//                //调用智能合约，上传病历
//                byte[] queryResult = contract.createTransaction("uploadapplyRecord").submit(String.valueOf(myAuthorizeList.get(0).getUserID()), String.valueOf(myAuthorizeList.get(0).getToUserID()), String.valueOf(applyID), myAuthorizeList.get(0).getApplyDate().toString(), "已删除");
//                String queryStr = new String(queryResult, StandardCharsets.UTF_8);
//                if (queryStr.equals("success")) {
//                    data.replace("result", 0);
//                    //如果上传病历成功，则吧t_authorize表中的内容删去
//                    System.out.println("授权记录上传成功");
//                } else {
//                    data.replace("result", 2);
//                }
//            } catch (ContractException | TimeoutException | InterruptedException e) {
//                e.printStackTrace();
//            }
//            // 删除
//            if (1 == pm.delNeedUploadMedical(applyID)) {
//                System.out.println("删除成功");
//                data.replace("result", 0);
//            }
        } else {
            // 未知处理
            data.replace("result", 2);
        }
        json.setData(data);
        return json;
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * 查看自己的信息
     */
    public BackJSON getMyInfo(Integer patientID) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        try {
            Patient myInfo = pm.getMyInfo(patientID);
            System.out.println(myInfo);
            PatientInfo patientInfo = new PatientInfo();
            patientInfo.setPatientID(patientID);
            patientInfo.setPatientName(myInfo.getPatientName());
            patientInfo.setPatientPhone(myInfo.getPatientPhone());
            data.put("patientInfo", patientInfo);
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
    public BackJSON alterInfo(Integer patientID, PatientAlter patientAlter) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        Patient myinfo = pm.getMyInfo(patientID);
        myinfo.setPatientName(patientAlter.getPatientName());
        myinfo.setPatientPhone(patientAlter.getPatientPhone());
        int i = pm.alterInfo(myinfo, patientID);
        if (1 == i) {
            //修改成功
            data.put("result", 0);
            //data.put("myinfo",myInfo);
            System.out.println(myinfo.toString());
        } else {
            data.put("result", 1);
        }
        json.setData(data);
        return json;
    }

    /**
     * 查询病历详情，下载电子病历到本地
     *
     * @param
     * @return
     */
    @Override
    public BackJSON getDownLoadMedical(String hashAddress) throws Exception {
        BackJSON json = new BackJSON(200);
        JSONObject data = new JSONObject();
        data.put("result", 1);
        downLoadIpfs.downLoad(hashAddress);
        json.setData(data);
        return json;
    }

    /**
     * 查询授权访问记录
     *
     * @param patientID 患者的id号
     * @return
     */
    @Override
    public BackJSON inquireApplyRecord(Integer patientID) {
        BackJSON json = new BackJSON(200);
        Map<String, Object> data = new HashMap<>();
        // TODO：历史病历
        //连接到区块链网络
        Contract contract = Value.getContract();
        try {
            byte[] queryResult = contract.evaluateTransaction("getAllHistoryapplyRecord", String.valueOf(patientID));
            String s = new String(queryResult);
            System.out.println("s的长度为" + s.length());
            System.out.println("长度为" + queryResult.length);
            //Map<String, String> medical = new HashMap<>();
            JSONArray jsonResult1 = JSONArray.parseArray(new String(queryResult, StandardCharsets.UTF_8));
            int size = jsonResult1.size();
            Map<String, String>[] mydata = new HashMap[size];
            for (length = 0; length < size; length++) {
                JSONObject jsonResult = (JSONObject) JSONArray.parseArray(new String(queryResult, StandardCharsets.UTF_8)).get(length);
                //患者id
                String patientId = jsonResult.getString("patient_id");
                mydata[length] = new HashMap();
                mydata[length].put("patientId", patientId);
//                medical.put("patientId", patientId);
                System.out.println("患者id为:" + patientId);
                //创建电子病历的时间
                String doctorId = jsonResult.getString("doctor_id");
                mydata[length].put("doctorId", doctorId);
//                medical.put("doctorId", doctorId);
                System.out.println("医生id为：" + doctorId);
                //加密后的文件访问地址
                String applyId = jsonResult.getString("apply_id");
                mydata[length].put("applyId", applyId);
//                medical.put("applyId", applyId);
                String applyTime = jsonResult.getString("apply_time");
                mydata[length].put("applyTime", applyTime);
                System.out.println("申请时间为:" + applyTime);

                String applyState = jsonResult.getString("apply_state");
                System.out.println("申请的状态为" + applyState);
                mydata[length].put("applyState", applyState);
//                medical.put("applyState",applyState);
//                data.put("medical",medical);
//                mydata[length] = (HashMap) medical;
                System.out.println(mydata[length]);

            }
            data.put("mydata", mydata);
            System.out.println(mydata);
            for (int i = 0; i < mydata.length; i++) {
                System.out.println(mydata[i]);
            }
            data.put("result", 0);

        } catch (ContractException e) {
            data.put("result", 2);
            e.printStackTrace();
        }
        json.setData(data);
        return json;
    }

}


