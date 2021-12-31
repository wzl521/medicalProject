package wow.service;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.BackJSON;
import wow.entity.PatientAlter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

public interface PatientService {

	public BackJSON needConfirmMedical(Integer patientID, String patientPrivateKey);

	//public BackJSON uploadMedical(Integer applyID, Integer patientID, String patientPrvateKey);
//	public BackJSON uploadMedical(Integer patientID, MultipartFile multipartFile, String createTime) throws IOException;
	public BackJSON uploadMedical(Integer patientID, MultipartFile[] files, String createTime) throws IOException;

	//public BackJSON inquireMedical(Integer type, Integer patientID, String patientPrivateKey);
	public BackJSON inquireMedical(Integer patientID,Integer type) throws IllegalAccessException, InstantiationException, InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, CryptoException, ProposalException, ExecutionException, TransactionException, NoSuchMethodException, org.bouncycastle.crypto.CryptoException, IOException, InterruptedException, InvocationTargetException, InvalidArgumentException;

	//public BackJSON authorizeList(Integer patientID, String patientPrivateKey);
	public BackJSON authorizeList(Integer patientID);

	//public BackJSON dealAuthorize(Integer applyID, String patientPrivateKey, Integer dealType);
	public BackJSON dealAuthorize(Integer applyID, Integer dealType) throws Exception;

	public BackJSON getMyInfo(Integer patientID);

	public BackJSON alterInfo(Integer patientID, PatientAlter patientAlter);

	public  BackJSON getDownLoadMedical(String s) throws Exception;

	public BackJSON inquireApplyRecord(Integer i1);

	public BackJSON authorizeAlreadyList(Integer patientID);

	public BackJSON inquireBlock(String createTime);
}
