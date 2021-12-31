package wow.service;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.BackJSON;
import wow.entity.DoctorAlter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

public interface DoctorService {

//	public BackJSON createMedical(MedicalRecord medical, MultipartFile multipartFile) throws IOException;
public BackJSON createMedical(String patientPhone,String doctorName,String patientName,String description, MultipartFile[]files) throws IOException;
	public BackJSON applyAuthority(Integer doctorID, String patientPhone) throws IllegalAccessException, InstantiationException, InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, CryptoException, ProposalException, ExecutionException, TransactionException, NoSuchMethodException, org.bouncycastle.crypto.CryptoException, IOException, InterruptedException, InvocationTargetException, InvalidArgumentException, Exception;

	public BackJSON authorizedList(Integer doctorID);

	//public BackJSON getMedicalDescription(Integer applyID, String doctorPrivateKey);
	public BackJSON getDownLoadMedical(String hashAddress) throws Exception;


    public BackJSON getMyInfo(Integer doctorID);

	public BackJSON alterInfo(Integer doctorID, DoctorAlter doctorAlter);
}
