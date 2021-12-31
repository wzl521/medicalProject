package wow.controller;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.BackJSON;
import wow.entity.IIS;
import wow.entity.PatientAlter;
import wow.service.PatientService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

/**
 * 患者功能模块
 *
 * @author wow
 * @date 2020年6月6日
 */

@RestController
@RequestMapping("/patient/")
public class PatientController {

    @Autowired
    private PatientService ps;

    /* 确认要提交到区块链上的病历
     * is: patientID, patientPrivateKey
     *  */
    @PostMapping("needConfirmMedical")
    public BackJSON needConfirmMedical(@ModelAttribute IIS is) {
        System.out.println(is.getI1());
        System.out.println(is.getS());

        return ps.needConfirmMedical(is.getI1(), is.getS());
    }

    /* 上传病历
     * iis: Integer applyID, Integer patientID, String patientPrivateKey
     * */
    @RequestMapping("uploadMedical")
    public BackJSON uploadMedical(@RequestParam(value = "file",required = false) MultipartFile[] files, @RequestParam("patientID") Integer patientID,@RequestParam("createTime") String createTime) throws IOException {
        return ps.uploadMedical(patientID,files,createTime);
    }

    /* 查询区块
     * iis ：Integer type, Integer patientID
     * type:1 最新病历  2 历史病历
     * */
    @RequestMapping("inquireBlock/{createTime}")
    public BackJSON inquireBlock(@PathVariable("createTime")String createTime) throws IllegalAccessException, InstantiationException, InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, CryptoException, ProposalException, InvalidArgumentException, ExecutionException, NoSuchMethodException, org.bouncycastle.crypto.CryptoException, IOException, InterruptedException, InvocationTargetException, TransactionException {
        System.out.println("创建时间为"+createTime);
        return ps.inquireBlock(createTime);

    }
    /* 查询病历
     * iis ：Integer type, Integer patientID
     * type:1 最新病历  2 历史病历
     * */
    @RequestMapping("inquireMedical/{patientID}/{type}")
    public BackJSON inquireMedical(@PathVariable("patientID")Integer patientID,@PathVariable("type")Integer type) throws IllegalAccessException, InstantiationException, InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, CryptoException, ProposalException, InvalidArgumentException, ExecutionException, NoSuchMethodException, org.bouncycastle.crypto.CryptoException, IOException, InterruptedException, InvocationTargetException, TransactionException {
        return ps.inquireMedical(patientID,type);
    }

    /* 查询授权访问记录
     * iis ：Integer type, Integer patientID
     * type:1 最新病历  2 历史病历
     * */
    @PostMapping("inquireApplyRecord")
    public BackJSON inquireApplyRecord(@ModelAttribute IIS iis) {
        return ps.inquireApplyRecord(iis.getI1());
    }

    /**
     * 查询病历详情,将电子病历下载到本地
     * @param hashAddress  电子病历地址
     * @return
     * @throws Exception
     */
    @RequestMapping("getDownLoadMedical/{hashAddress}")
    public BackJSON getDownLoadMedical(@PathVariable("hashAddress") String hashAddress) throws Exception {
        return ps.getDownLoadMedical(hashAddress);
    }

    /* 查看待授权列表
     * is： Integer patientID
     * */
    @RequestMapping(value = "authorizeList/{patientID}",method = RequestMethod.GET)
    public BackJSON authorizeList(@PathVariable("patientID") Integer patientID) {
        return ps.authorizeList(patientID);
    }

    /* 查看已授权列表
     * is： Integer patientID
     * */
    @RequestMapping(value = "authorizeAlreadyList/{patientID}",method = RequestMethod.GET)
    public BackJSON authorizeAlreadyList(@PathVariable("patientID") Integer patientID) {
        return ps.authorizeAlreadyList(patientID);
    }

    /* 处理授权
     * iis: Integer applyID,Integer dealType
     * 1-同意 2-拒绝 3-删除
     * */
    @RequestMapping(value = "dealAuthorize/{applyID}/{dealType}",method = RequestMethod.POST)
    public BackJSON dealAuthorize(@PathVariable("applyID")Integer applyID,@PathVariable("dealType")Integer dealType) throws Exception {
        return ps.dealAuthorize(applyID,dealType);
    }

    /* 病人查看自己的信息*/
    @RequestMapping(value = "/getMyInfo/{patientID}", method = RequestMethod.GET)
    public BackJSON getMyInfo(@PathVariable("patientID") Integer patientID) {
        return ps.getMyInfo(patientID);
    }

    /* 病人修改自己的信息*/
    @RequestMapping(value = "/alterInfo/{patientID}", method = RequestMethod.POST)
    public BackJSON alterInfo(@PathVariable("patientID") Integer patientID, @RequestBody PatientAlter patientAlter) {
        return ps.alterInfo(patientID,patientAlter);
    }


}
