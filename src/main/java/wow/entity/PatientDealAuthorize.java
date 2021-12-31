package wow.entity;

/**
 * @Description
 * @autor wzl
 * @date 2021/7/2-21:12
 */
public class PatientDealAuthorize {
    private Integer applyID;
    private Integer dealType;

    public PatientDealAuthorize() {
    }

    public PatientDealAuthorize(Integer applyID, Integer dealType) {
        this.applyID = applyID;
        this.dealType = dealType;
    }

    public Integer getApplyID() {
        return applyID;
    }

    public void setApplyID(Integer applyID) {
        this.applyID = applyID;
    }

    public Integer getDealType() {
        return dealType;
    }

    public void setDealType(Integer dealType) {
        this.dealType = dealType;
    }
}
