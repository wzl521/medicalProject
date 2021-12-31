package wow.entity;

/**
 * @Description
 * @autor wzl
 * @date 2021/8/28-11:20
 */
public class Block   {

    private String createTime;
    private Integer blockNumber;
    private String currentBlockHash;
    private String previousBlockHash;
    private String txID;
    private  String timeStamp;

    public Block() {
    }

    public Block(String createTime, Integer blockNumber, String currentBlockHash, String previousBlockHash, String txID, String timeStamp) {
        this.createTime = createTime;
        this.blockNumber = blockNumber;
        this.currentBlockHash = currentBlockHash;
        this.previousBlockHash = previousBlockHash;
        this.txID = txID;
        this.timeStamp = timeStamp;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getCurrentBlockHash() {
        return currentBlockHash;
    }

    public void setCurrentBlockHash(String currentBlockHash) {
        this.currentBlockHash = currentBlockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
