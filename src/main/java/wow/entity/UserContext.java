package wow.entity;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import wow.util.UserUtils;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

/**
 * @description 用户对象
 */
public class UserContext implements User, Serializable {

    private String name;

    private Set<String> roles;

    private String account;

    private String affiliation;

    private Enrollment enrollment;

    private String mspId;
    private static final String keyFolderPath = "D:\\MyIdea\\mymedical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\users\\Admin@org1.example.com\\msp\\keystore";
    private static final String keyFileName="c29637d56864d24b1db15882160ec7b07be98ba240c26ae4334b6ecd288f23a4_sk";
    private static final String certFoldePath="D:\\MyIdea\\mymedical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\users\\Admin@org1.example.com\\msp\\admincerts";
    private static final String certFileName="Admin@org1.example.com-cert.pem";
    private static  final String tlsOrderFilePath = "D:\\MyIdea\\mymedical\\src\\main\\resources\\crypto-config\\ordererOrganizations\\example.com\\tlsca\\tlsca.example.com-cert.pem";
    private static final String txfilePath = "D:\\MyIdea\\medical\\src\\main\\resources\\mychannel.tx";
    private static  final String tlsPeerFilePath = "D:\\MyIdea\\medical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\msp\\tlscacerts\\tlsca.org1.example.com-cert.pem";
    private static  final String tlsPeerFilePathAddtion = "D:\\MyIdea\\medical\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\tlsca\\tlsca.org1.example.com-cert.pem";


    static{
        Security.addProvider(new BouncyCastleProvider());
    }
    public UserContext() throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException {
        this.name="admin";
        this.account="李伟";
        this.affiliation="Org1";
        this.mspId="Org1MSP";
        this.enrollment= UserUtils.getEnrollment(keyFolderPath,keyFileName,certFoldePath,certFileName);

    }

    public UserContext(String name, Set<String> roles, String account, String affiliation, Enrollment enrollment, String mspId) {
        this.name = name;
        this.roles = roles;
        this.account = account;
        this.affiliation = affiliation;
        this.enrollment = enrollment;
        this.mspId = mspId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }
}
