package wow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

public class MyAuthorize implements Serializable {

    private Integer applyID;
    private Integer userID;
    private Integer toUserID;
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone="GMT+8")
    private String applyDate;
    private String hashAddress;
    private  Integer applyState;

    public MyAuthorize() {
    }

    public MyAuthorize(Integer applyID, Integer userID, Integer toUserID, String applyDate, String hashAddress) {
        this.applyID = applyID;
        this.userID = userID;
        this.toUserID = toUserID;
        this.applyDate = applyDate;
        this.hashAddress = hashAddress;
    }

    public Integer getApplyState() {
        return applyState;
    }

    public void setApplyState(Integer applyState) {
        this.applyState = applyState;
    }

    public Integer getApplyID() {
        return applyID;
    }

    public void setApplyID(Integer applyID) {
        this.applyID = applyID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getToUserID() {
        return toUserID;
    }

    public void setToUserID(Integer toUserID) {
        this.toUserID = toUserID;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getHashAddress() {
        return hashAddress;
    }

    public void setHashAddress(String hashAddress) {
        this.hashAddress = hashAddress;
    }
}
