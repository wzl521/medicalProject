package wow.util;

import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import wow.entity.Block;
import wow.entity.FabricClient;
import wow.entity.UserContext;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Value {


    private final static String projectPath = "D:\\MyIdea";
    private static final String keyFolderPath = "D:\\MyIdea\\mymedical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\users\\Admin@org1.example.com\\msp\\keystore";
    private static final String keyFileName="c29637d56864d24b1db15882160ec7b07be98ba240c26ae4334b6ecd288f23a4_sk";
    private static final String certFoldePath="D:\\MyIdea\\mymedical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\users\\Admin@org1.example.com\\msp\\admincerts";
    private static final String certFileName="Admin@org1.example.com-cert.pem";
    private static  final String tlsOrderFilePath = "D:\\MyIdea\\mymedical\\src\\main\\resources\\crypto-config\\ordererOrganizations\\example.com\\tlsca\\tlsca.example.com-cert.pem";
    private static final String txfilePath = "D:\\MyIdea\\medical\\src\\main\\resources\\mychannel.tx";
    private static  final String tlsPeerFilePath = "D:\\MyIdea\\medical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\msp\\tlscacerts\\tlsca.org1.example.com-cert.pem";
    private static  final String tlsPeerFilePathAddtion = "D:\\MyIdea\\medical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\tlsca\\tlsca.org1.example.com-cert.pem";


    /* fabric相关证书
     * 相对路径找不到，先使用绝对路径  */
    private static final Path localPath = Paths.get(projectPath + "/medical/src/main/resources/");
    private static final Path NETWORK_CONFIG_PATH = localPath.resolve(Paths.get("connection.json"));
    private static final Path credentialOrg1Path = localPath.resolve(Paths.get("crypto-config", "peerOrganizations", "org1.example.com", "users", "User1@org1.example.com", "msp"));
    private static final Path credentialOrg1PemPath = credentialOrg1Path.resolve(Paths.get("signcerts", "User1@org1.example.com-cert.pem"));
    private static final String orgMSP = "Org1MSP";
    private static final String channelName = "mychannel";
    private static final String chaincode = "medical";

    // RSA 密钥长度
    private final static int KEY_SIZE = 1024;

    // 医疗图片地址
    private final static String medicalPicturePath = projectPath + "/medical/src/main/resources/picture/medical/";


    //获取合约连接
    public static Contract getContract() {
        Contract contract = null;
//        HFClient hfClient = HFClient.createNewInstance();
        try {
            Wallet wallet = Wallets.newInMemoryWallet();
            Path certificatePath = credentialOrg1Path.resolve(credentialOrg1PemPath);
            X509Certificate certificate = readX509Certificate(certificatePath);
            Path privateKeyPath = credentialOrg1Path.resolve(Paths.get("keystore", "0e2832f1c08f2b0235d8bb32be2bf47493a3fedbd7125155ff94e99224c8c7e3_sk"));//0e2832f1c08f2b0235d8bb32be2bf47493a3fedbd7125155ff94e99224c8c7e3
            PrivateKey privateKey = getPrivateKey(privateKeyPath);
            wallet.put("user", Identities.newX509Identity(orgMSP, certificate, privateKey));
            GatewayImpl.Builder builder = (GatewayImpl.Builder) Gateway.createBuilder();
            builder.identity(wallet, "user").networkConfig(NETWORK_CONFIG_PATH);
            Gateway gateway = builder.connect();
            Network network = gateway.getNetwork(channelName);
            contract = network.getContract(chaincode);
        } catch (Exception e) {
            System.out.println("something wrong!!!");
            e.printStackTrace();
        }
        return contract;
    }
    //上传病历智能合约
    public static Block uploadMedicalRecord(Integer patientID, String hashAddress, String createTime) throws Exception {
        UserContext userContext =new UserContext();
        FabricClient fabricClient =new FabricClient(userContext);
        List<Peer> peers =new ArrayList<>();
        Peer peer = fabricClient.getPeer("peer0.org1.example.com", "grpcs://peer0.org1.example.com:7051", tlsPeerFilePath);
        peers.add(peer);
        Orderer orderer = fabricClient.getOrderer("orderer.example.com", "grpcs://orderer.example.com:7050", tlsOrderFilePath);
        String  initargs[]={patientID.toString(),hashAddress,createTime};
        Block block = fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG, "medical", orderer, peers, "uploadMedicalRecord", initargs);
        return block;
    }

    //查询最新病历合约

    public static Map getRecordId(Integer patinetID) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, ProposalException, TransactionException, org.bouncycastle.crypto.CryptoException, ExecutionException, InterruptedException {
        UserContext userContext = new UserContext();
        FabricClient fabricClient = new FabricClient(userContext);
        Peer peer0 = fabricClient.getPeer("peer0.org1.example.com","grpcs://peer0.org1.example.com:7051",tlsPeerFilePath);
        //Peer peer1 = fabricClient.getPeer("peer0.org2.example.com","grpcs://peer0.org2.example.com:9051",tlsPeerFilePathAddtion);
        List<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        //peers.add(peer1);
        String initArgs[] = {String.valueOf(patinetID)};
        Map map =  fabricClient.queryChaincode(peers,"mychannel", TransactionRequest.Type.GO_LANG,"medical","getRecordId",initArgs);
        System.out.println(map);
        return map;
    }
    //查询历史病历合约

    public static Map<String,String>[] getAllRecordById(Integer patinetID) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, ProposalException, TransactionException, org.bouncycastle.crypto.CryptoException, ExecutionException, InterruptedException {
        UserContext userContext = new UserContext();
        FabricClient fabricClient = new FabricClient(userContext);
        Peer peer0 = fabricClient.getPeer("peer0.org1.example.com","grpcs://peer0.org1.example.com:7051",tlsPeerFilePath);
        //Peer peer1 = fabricClient.getPeer("peer0.org2.example.com","grpcs://peer0.org2.example.com:9051",tlsPeerFilePathAddtion);
        List<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        //peers.add(peer1);
        String initArgs[] = {String.valueOf(patinetID)};
        Map<String,String>[] map =  fabricClient.queryChaincodeAll(peers,"mychannel", TransactionRequest.Type.GO_LANG,"medical","getAllHistoryRecordId",initArgs);
        System.out.println(map);
        return map;
    }

    //上传授权访问记录智能合约
    public static Block uploadapplyRecord(Integer patientID,Integer doctorID,Integer ApplyID,String ApplyTime,Integer ApplyState) throws Exception {
        UserContext userContext =new UserContext();
        FabricClient fabricClient =new FabricClient(userContext);
        List<Peer> peers =new ArrayList<>();
        Peer peer = fabricClient.getPeer("peer0.org1.example.com", "grpcs://peer0.org1.example.com:7051", tlsPeerFilePath);
        peers.add(peer);
        Orderer orderer = fabricClient.getOrderer("orderer.example.com", "grpcs://orderer.example.com:7050", tlsOrderFilePath);
        String  initargs[]={patientID.toString(),doctorID.toString(),ApplyID.toString(),ApplyTime,ApplyState.toString()};
        Block block = fabricClient.myInvoke("mychannel", TransactionRequest.Type.GO_LANG, "medical", orderer, peers, "uploadapplyRecord", initargs);
        return block;
    }

    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }

    // MD5 hash 运算
    public static String MD5Hash(String text) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] digest = md.digest(text.getBytes());
            char[] charset = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(charset[(b >> 4) & 15]);
                sb.append(charset[b & 15]);
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    // MD5 hash 验证
    public static boolean MD5Verify(String text, String digest) {
        if (digest.equals(MD5Hash(text))) {
            return true;
        }
        return false;
    }

    // 随机生成字符串
    public static void randString(int length) {
        int size;
        String str = "";
        for (int i = 0; i < length; i++) {
            size = ((int) (Math.random() * 2)) == 0 ? 65 : 97;
            str += (char) ((int) (Math.random() * 26) + size);
        }
        System.out.println(str);
    }

    // 生成 RSA 密钥对
    public static Map<String, String> generateRSAKeyPair() {
        Map<String, String> result = new HashMap<>();
        try {
            //KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            //初始化密钥对生成器，密钥大小为96-1024位
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            //生成一个密钥对，保存到keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            //得到公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            //得到私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            //得到公钥字符串
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            //得到私钥字符串
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            //将公钥字符串写入集合中
            result.put("publicKey", publicKeyString);
            //将私钥字符串写入集合中
            result.put("privateKey", privateKeyString);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("生成公钥私钥出错");
            e.printStackTrace();
        }
        return result;
    }

    // RSA 公钥加密

    /**
     *
     *
     * @param text 明文数据
     * @param publicKey 公钥
     * @return
     */
    public static String RSAEncrypt(String text, String publicKey) {
        String outStr = null;
        byte[] decodedPublicKey = Base64.getDecoder().decode(publicKey);
        try {
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
            //使用默认RSA
            Cipher cipher = Cipher.getInstance("RSA");
            //初始化算法：公钥加密
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            //公钥加密后的字节数组
            outStr = Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("加密过程出错");
            e.printStackTrace();
        }
        return outStr;
    }

    // RSA 私钥解密

    /**
     *
     * @param text 密文：已加密的数据
     * @param privateKey 使用自己的私钥
     * @return
     */
    public static String RSADecrypt(String text, String privateKey) {
        String outStr = null;
        byte[] inputByte = Base64.getDecoder().decode(text);
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        try {
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //使用RSA
            Cipher cipher = Cipher.getInstance("RSA");
            //初始化解密算法：私钥解密
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            //解密，生成以字符数组形式存在的明文数据
            outStr = new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            System.out.println("解密过程出错");
            e.printStackTrace();
        }
        return outStr;
    }

    // RSA 私钥签名

    /**
     *
     * @param text  签名的内容
     * @param privateKey  个人的私钥
     * @return
     */
    public static String RSASign(String text, String privateKey) {
        String outStr = null;
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decoded);
            PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance("SHA256withRSA");
            //以私钥字符串初始化签名
            signature.initSign(priKey);
            //将原始字符串初始化签名
            signature.update(text.getBytes("UTF-8"));
            //签名后的结果，以字符数组形式存在
            byte[] signed = signature.sign();
            //签名后的结果，以Base64编码形式存在
            outStr = Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            System.out.println("签名过程出错");
            e.printStackTrace();
        }
        return outStr;
    }

    // RSA 公钥验证签名

    /**
     *
     * @param text 加密的内容
     * @param signText 签名
     * @param publicKey 公钥
     * @return
     */
    public static boolean RSACheckSign(String text, String signText, String publicKey) {
        boolean verifyResult = false;
        byte[] decodedKey = Base64.getDecoder().decode(publicKey);
        byte[] decodeSign = Base64.getDecoder().decode(signText);
        try {
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedKey));
            Signature signature = Signature.getInstance("SHA256withRSA");
            //使用公钥初始化签名校验
            signature.initVerify(pubKey);
            //原始字符串注入到签名校验中
            signature.update(text.getBytes("UTF-8"));
            //验证签名结果是否成功
            verifyResult = signature.verify(decodeSign);
        } catch (Exception e) {
            System.out.println("验证签名过程出错");
            e.printStackTrace();
        }
        return verifyResult;
    }

    public static String getMedicalPicturePath() {
        return medicalPicturePath;
    }


}
