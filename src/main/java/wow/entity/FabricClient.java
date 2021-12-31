package wow.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FabricClient {

    private static final Logger log = LoggerFactory.getLogger(FabricClient.class);

    private HFClient hfClient;

    public FabricClient(UserContext userContext) throws IllegalAccessException, InvocationTargetException, InvalidArgumentException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfClient.setCryptoSuite(cryptoSuite);
        hfClient.setUserContext(userContext);
    }

    /**
     * @param channelName channel的名字
     * @param order       order的信息
     * @param txPath      创建channel所需的tx文件
     * @return Channel
     * @throws IOException
     * @throws InvalidArgumentException
     * @throws TransactionException
     * @description 创建channel
     */
    public Channel createChannel(String channelName, Orderer order, String txPath) throws IOException, InvalidArgumentException, TransactionException {
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(txPath));
        return hfClient.newChannel(channelName, order, channelConfiguration, hfClient.getChannelConfigurationSignature(channelConfiguration, hfClient.getUserContext()));
    }

    /**
     * @param lang              合约开发语言
     * @param chaincodeName     合约名称
     * @param chaincodeVersion  合约版本
     * @param chaincodeLocation 合约的目录路径
     * @param chaincodePath     合约的文件夹
     * @param peers             安装的peers 节点
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @description 安装合约
     */
    public void installChaincode(TransactionRequest.Type lang, String chaincodeName, String chaincodeVersion, String chaincodeLocation, String chaincodePath, List<Peer> peers) throws InvalidArgumentException, ProposalException, TransactionException {
        InstallProposalRequest installProposalRequest = hfClient.newInstallProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion);
        installProposalRequest.setChaincodeLanguage(lang);
        installProposalRequest.setChaincodeID(builder.build());
        installProposalRequest.setChaincodeSourceLocation(new File(chaincodeLocation));
        installProposalRequest.setChaincodePath(chaincodePath);
        Collection<ProposalResponse> responses = hfClient.sendInstallProposal(installProposalRequest, peers);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {

                System.out.println("安装合约成功");
                ChaincodeID chaincodeID = response.getChaincodeID();
                System.out.println("链码ID号为：" + chaincodeID);
                String transactionID = response.getTransactionID();
                System.out.println("交易ID号为:" + transactionID);

                Peer peer = response.getPeer();

                FabricProposalResponse.ProposalResponse proposalResponse = response.getProposalResponse();
                System.out.println("交易提案响应为:" + proposalResponse);
                String message = response.getMessage();
                System.out.println("交易message为：" + message);
                log.info("{} installed sucess", response.getPeer().getName());
            } else {
                log.error("{} installed fail", response.getMessage());
            }
        }
    }

    /**
     * @param channelName
     * @param lang
     * @param chaincodeName
     * @param chaincodeVersion
     * @param order
     * @param peer
     * @param funcName         合约实例化执行的函数
     * @param args             合约实例化执行的参数
     * @throws TransactionException
     * @throws ProposalException
     * @throws InvalidArgumentException
     * @description 合约的实例化
     */
    public void initChaincode(String channelName, TransactionRequest.Type lang, String chaincodeName, String chaincodeVersion, Orderer order, Peer peer, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException {
        Channel channel = getChannel(channelName);
        channel.addPeer(peer);
        channel.addOrderer(order);
        channel.initialize();
        InstantiateProposalRequest instantiateProposalRequest = hfClient.newInstantiationProposalRequest();
        instantiateProposalRequest.setArgs(args);
        instantiateProposalRequest.setFcn(funcName);
        instantiateProposalRequest.setChaincodeLanguage(lang);
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("D:\\MyIdea\\fabric-sdk\\fabric\\src\\main\\resources\\chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion);
        instantiateProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                System.out.println("合约实例化成功");
                log.info("{} init sucess", response.getPeer().getName());
            } else {
                log.error("{} init fail", response.getMessage());
            }
        }
        channel.sendTransaction(responses);
    }

    /**
     * @param channelName
     * @param lang
     * @param chaincodeName
     * @param chaincodeVersion
     * @param order
     * @param peer
     * @param funcName
     * @param args
     * @throws TransactionException
     * @throws ProposalException
     * @throws InvalidArgumentException
     * @throws IOException
     * @throws ChaincodeEndorsementPolicyParseException
     * @description 合约的升级
     */
    public void upgradeChaincode(String channelName, TransactionRequest.Type lang, String chaincodeName, String chaincodeVersion, Orderer order, Peer peer, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException {
        Channel channel = getChannel(channelName);
        channel.addPeer(peer);
        channel.addOrderer(order);
        channel.initialize();
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer);
        System.out.println("blockchainInfo为:" + blockchainInfo);
        long height = blockchainInfo.getHeight();
        byte[] blockhash1 = "zph90vkM12rXIHDgjVTSNkLDVm5sowLj46p6U4Gz3bY=".getBytes();
        System.out.println("区块hash为：" + Hex.encodeHexString(blockhash1));
        System.out.println("区块前置hash为:" + Hex.encodeHexString("HDPUpocbVxEQsfnmkysvMYNAYIjV6lQVNRITH2Po8Wc=".getBytes()));
        System.out.println("区块高度为:" + height);

        byte[] currentBlockHash = blockchainInfo.getCurrentBlockHash();
        System.out.println("当前区块hash为：" + org.apache.commons.codec.binary.Hex.encodeHexString(currentBlockHash));


        byte[] previousBlockHash = blockchainInfo.getPreviousBlockHash();
        System.out.println("前置区块hash为：" + Hex.encodeHexString(previousBlockHash));


//        byte[] dataHash = blockchainInfo.getDataHash();
//        System.out.println("数据hash为："+dataHash);
//
//        byte[] previousBlockHash = blockchainInfo.getPreviousBlockHash();
//        System.out.println("前置区块hash为："+previousBlockHash);
//
//        byte[] currentBlockHash = blockchainInfo.getCurrentBlockHash();
//        System.out.println("当前区块hash为："+currentBlockHash);

        UpgradeProposalRequest upgradeProposalRequest = hfClient.newUpgradeProposalRequest();
        upgradeProposalRequest.setArgs(args);
        upgradeProposalRequest.setFcn(funcName);
        upgradeProposalRequest.setChaincodeLanguage(lang);
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("D:\\MyIdea\\fabric-sdk\\fabric\\src\\main\\resources\\chaincodeendorsementpolicy.yaml"));
        upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion);
        upgradeProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses = channel.sendUpgradeProposal(upgradeProposalRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                log.info("{} upgrade sucess", response.getPeer().getName());
            } else {
                log.error("{} upgrade fail", response.getMessage());
            }
        }
        channel.sendTransaction(responses);

        System.out.println("-------------------------------------");
//        BlockchainInfo blockchainInfo1 = channel.queryBlockchainInfo(peer);
//        System.out.println("blockchainInfo1为:"+blockchainInfo);
//        long height1 = blockchainInfo.getHeight();
//        System.out.println("区块高度为:"+height1);
//
//        BlockInfo blockInfo1 = channel.queryBlockByNumber(height);
//        long blockNumber1 = blockInfo.getBlockNumber();
//        System.out.println("区块blockNumber为："+blockNumber1);
//
//        byte[] dataHash1 = blockInfo.getDataHash();
//        System.out.println("数据hash为："+dataHash1);

        byte[] previousBlockHash1 = blockchainInfo.getPreviousBlockHash();
        System.out.println("前置区块hash为：" + previousBlockHash1);

        byte[] currentBlockHash1 = blockchainInfo.getCurrentBlockHash();
        System.out.println("当前区块hash为：" + currentBlockHash1);

    }

    /**
     * @param channelName
     * @param lang
     * @param chaincodeName
     * @param order
     * @param peers
     * @param funcName      合约调用执行的函数名称
     * @param args          合约调用执行的参数
     * @throws TransactionException
     * @throws ProposalException
     * @throws InvalidArgumentException
     * @description 合约的调用
     */
    public Block myInvoke(String channelName, TransactionRequest.Type lang, String chaincodeName, Orderer order, List<Peer> peers, String funcName, String args[]) throws Exception {
        Channel channel = getChannel(channelName);
        channel.addOrderer(order);
        for (Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
//        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
//        long height = blockchainInfo.getHeight();
//        System.out.println("区块高度为："+height);
//
//
//        byte[] currentBlockHash = blockchainInfo.getCurrentBlockHash();
//        System.out.println("当前区块hash为："+ org.apache.commons.codec.binary.Hex.encodeHexString(currentBlockHash));
//
//        byte[] previousBlockHash = blockchainInfo.getPreviousBlockHash();
//        System.out.println("前置区块hash为："+ Hex.encodeHexString(previousBlockHash));

        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeLanguage(lang);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setFcn(funcName);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        transactionProposalRequest.setChaincodeID(builder.build());


        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest, peers);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                String transactionID = response.getTransactionID();
                System.out.println("交易ID为：" + transactionID);
                System.out.println("授权记录上链成功");

                log.info("{} invoke proposal {} sucess", response.getPeer().getName(), funcName);
            } else {
                String logArgs[] = {response.getMessage(), funcName, response.getPeer().getName()};
                log.error("{} invoke proposal {} fail on {}", logArgs);
            }
        }
        BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(responses).get();

        //提交链码交易
//        TransactionProposalRequest req2 = hfClient.newTransactionProposalRequest();
//        req2.setChaincodeID(builder.build());
//        req2.setFcn(funcName);
//        req2.setArgs(args);
//        Collection<ProposalResponse> rsp2 = channel.sendTransactionProposal(req2);
//        BlockEvent.TransactionEvent event = channel.sendTransaction(rsp2).get();
        Block block = new Block();
        String transactionID = transactionEvent.getTransactionID();
        System.out.format("txid: %s\n", transactionID);
        block.setTxID(transactionID);

        System.out.format("valid: %b\n", transactionEvent.isValid());

        BlockInfo blockInfo = channel.queryBlockByTransactionID(transactionID);
        System.out.println("----------------------------------------");
        System.out.println("以下内容为解析区块内容");
        System.out.println("区块高度为；" + blockInfo.getBlockNumber());
        block.setBlockNumber((int) blockInfo.getBlockNumber());

        String previousHash = Hex.encodeHexString(blockInfo.getPreviousHash());
        System.out.println("前置hash为：" + previousHash);
        block.setPreviousBlockHash(previousHash);

        System.out.println("当前数据hash为：" + Hex.encodeHexString(blockInfo.getDataHash()));

        String currentHash = Hex.encodeHexString(SDKUtils.calculateBlockHash(hfClient, blockInfo.getBlockNumber(), blockInfo.getPreviousHash(), blockInfo.getDataHash()));
        System.out.println("当前区块hash为：" + currentHash);
        block.setCurrentBlockHash(currentHash);

        System.out.println("当前区块交易数量为：" + blockInfo.getTransactionCount());
        Iterable<BlockInfo.EnvelopeInfo> envelopeInfos = blockInfo.getEnvelopeInfos();

//        BlockInfo blockInfo1 = channel.queryBlockByHash(Hex.decodeHex("f2c246d3bc4ac59e4e42e094973e5997121f7b8703dc6567120a28af505ebdcb".toCharArray()));
//        System.out.println("区块的具体内容为：" + blockInfo1.getBlock().toBuilder().toString());
        List list = new ArrayList();
        for (BlockInfo.EnvelopeInfo envelopeInfo : envelopeInfos) {
            //从区块中获取channelid
            String channelId = envelopeInfo.getChannelId();
            System.out.println("通道id为：" + channelId);
            //获取落快时间
            Date timestamp = envelopeInfo.getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(timestamp);
            System.out.println("落快时间为：" + format);
            block.setTimeStamp(format);
            //定位是组织的那个节点
            System.out.println("提交事务的身份：" + envelopeInfo.getCreator().getMspid());


        }
//        Common.Block block = blockInfo.getBlock();
//        System.out.println("区块内容为:"+block.toString());
//        System.out.println("区块头为:"+block.getHeader());
//        String s = blockInfo.getTransActionsMetaData().toString();
//        System.out.println("交易元数据为："+s);
        return block;

    }

    /**
     * @description 最新病历的查询
     * @param peers
     * @param channelName
     * @param lang
     * @param chaincodeName
     * @param funcName
     * @param args
     * @return
     * @throws TransactionException
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public Map queryChaincode(List<Peer> peers, String channelName, TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, InvalidArgumentException, ProposalException, ExecutionException, InterruptedException {
       Channel channel = getChannel(channelName);
        for(Peer p : peers) {
            channel.addPeer(p);
        }
       channel.initialize();
       QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
       ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
       queryByChaincodeRequest.setChaincodeID(builder.build());
       queryByChaincodeRequest.setArgs(args);
       queryByChaincodeRequest.setFcn(funcName);
       queryByChaincodeRequest.setChaincodeLanguage(lang);

        Map map = new HashMap();
        Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
       for (ProposalResponse response : responses) {
               if (response.getStatus().getStatus() == 200) {
                   System.out.println("这里是:"+response.getChaincodeActionResponsePayload());
                   log.info("data is {}", response.getProposalResponse().getResponse().getPayload());
                   byte[] queryResult = response.getProposalResponse().getResponse().getPayload().toByteArray();
                   String bytes = new String(response.getProposalResponse().getResponse().getPayload().toByteArray(), UTF_8) ;
                   System.out.println(bytes);
                   JSONObject jsonObject = (JSONObject) JSONArray.parseArray(bytes).get(0);
                   String patientId = jsonObject.getString("patient_id");
                   String hashAddress = jsonObject.getString("hash_address");
                   String createTime = jsonObject.getString("create_time");

                   map.put("patient_id",patientId);
                   map.put("hash_address",hashAddress);
                   map.put("create_time",createTime);
                   System.out.println("patientId:"+patientId);
                   System.out.println("create_time:"+createTime);
                   System.out.println("hash_address:"+hashAddress);
                   return map;
               } else {
                   log.error("data get error {}", response.getMessage());
                   map.put(response.getStatus().getStatus(),response.getMessage());
                   return map;
               }
           }
       map.put("code","404");
       return map;
    }
    /**
     * @description 历史病历的查询
     * @param peers
     * @param channelName
     * @param lang
     * @param chaincodeName
     * @param funcName
     * @param args
     * @return
     * @throws TransactionException
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public Map<String,String> [] queryChaincodeAll(List<Peer> peers, String channelName, TransactionRequest.Type lang, String chaincodeName, String funcName, String args[]) throws TransactionException, InvalidArgumentException, ProposalException, ExecutionException, InterruptedException {
        Channel channel = getChannel(channelName);
        for(Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        queryByChaincodeRequest.setChaincodeID(builder.build());
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(funcName);
        queryByChaincodeRequest.setChaincodeLanguage(lang);

        Map map = new HashMap();
        Map<String,String> []mydata =null;
        Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                System.out.println("这里是:"+response.getChaincodeActionResponsePayload());
                log.info("data is {}", response.getProposalResponse().getResponse().getPayload());
                byte[] queryResult = response.getProposalResponse().getResponse().getPayload().toByteArray();
                String bytes = new String(queryResult, UTF_8);
                System.out.println("queryResult.length"+bytes.length());
                JSONArray jsonArray = JSONArray.parseArray(bytes);
                int size = jsonArray.size();
                System.out.println("size="+size+"条历史病历");
                 mydata =new HashMap[size];

                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = (JSONObject) JSONArray.parseArray(new String(bytes)).get(i);
                    String patientId = jsonObject.getString("patient_id");
                    mydata[i]=new HashMap<>();
                    mydata[i].put("patient_id",patientId);
                    System.out.println("patient_id:"+patientId);

                    String hashAddress = jsonObject.getString("hash_address");
                    System.out.println("hash_address:"+hashAddress);
//                    String aesDncode = AESUtil.AESDncode(hashAddress);
//                    System.out.println("解密后的hashAddress："+aesDncode);
                    mydata[i].put("hash_address",hashAddress);


                    String createTime = jsonObject.getString("create_time");
                    mydata[i].put("create_time",createTime);
                    System.out.println("create_time:"+createTime);

                    System.out.println(mydata[i]);

                }
                return mydata;
            } else {
                log.error("data get error {}", response.getMessage());
                map.put(response.getStatus().getStatus(),response.getMessage());
                return mydata;
            }
        }
        map.put("code","404");
        return mydata;
    }

    /**
     * @param channelName
     * @param lang
     * @param chaincodeName
     * @param order
     * @param peers
     * @param funcName      合约调用执行的函数名称
     * @param args          合约调用执行的参数
     * @throws TransactionException
     * @throws ProposalException
     * @throws InvalidArgumentException
     * @description 合约的调用
     */
    public Block invoke(String channelName, TransactionRequest.Type lang, String chaincodeName, Orderer order, List<Peer> peers, String funcName, String args[]) throws Exception {
        Channel channel = getChannel(channelName);
        channel.addOrderer(order);
        for (Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
//        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
//        long height = blockchainInfo.getHeight();
//        System.out.println("区块高度为："+height);
//
//
//        byte[] currentBlockHash = blockchainInfo.getCurrentBlockHash();
//        System.out.println("当前区块hash为："+ org.apache.commons.codec.binary.Hex.encodeHexString(currentBlockHash));
//
//        byte[] previousBlockHash = blockchainInfo.getPreviousBlockHash();
//        System.out.println("前置区块hash为："+ Hex.encodeHexString(previousBlockHash));

        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeLanguage(lang);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setFcn(funcName);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        transactionProposalRequest.setChaincodeID(builder.build());


        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest, peers);
        for (ProposalResponse response : responses) {
            if (response.getStatus().getStatus() == 200) {
                String transactionID = response.getTransactionID();
                System.out.println("交易ID为：" + transactionID);
                System.out.println("病历上链成功");

                log.info("{} invoke proposal {} sucess", response.getPeer().getName(), funcName);
            } else {
                String logArgs[] = {response.getMessage(), funcName, response.getPeer().getName()};
                log.error("{} invoke proposal {} fail on {}", logArgs);
            }
        }
        BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(responses).get();

        //提交链码交易
//        TransactionProposalRequest req2 = hfClient.newTransactionProposalRequest();
//        req2.setChaincodeID(builder.build());
//        req2.setFcn(funcName);
//        req2.setArgs(args);
//        Collection<ProposalResponse> rsp2 = channel.sendTransactionProposal(req2);
//        BlockEvent.TransactionEvent event = channel.sendTransaction(rsp2).get();
        Block block = new Block();
        String transactionID = transactionEvent.getTransactionID();
        System.out.format("txid: %s\n", transactionID);
        block.setTxID(transactionID);

        System.out.format("valid: %b\n", transactionEvent.isValid());

        BlockInfo blockInfo = channel.queryBlockByTransactionID(transactionID);
        System.out.println("----------------------------------------");
        System.out.println("以下内容为解析区块内容");
        System.out.println("区块高度为；" + blockInfo.getBlockNumber());
        block.setBlockNumber((int) blockInfo.getBlockNumber());

        String previousHash = Hex.encodeHexString(blockInfo.getPreviousHash());
        System.out.println("前置hash为：" + previousHash);
        block.setPreviousBlockHash(previousHash);

        System.out.println("当前数据hash为：" + Hex.encodeHexString(blockInfo.getDataHash()));

        String currentHash = Hex.encodeHexString(SDKUtils.calculateBlockHash(hfClient, blockInfo.getBlockNumber(), blockInfo.getPreviousHash(), blockInfo.getDataHash()));
        System.out.println("当前区块hash为：" + currentHash);
        block.setCurrentBlockHash(currentHash);

        System.out.println("当前区块交易数量为：" + blockInfo.getTransactionCount());
        Iterable<BlockInfo.EnvelopeInfo> envelopeInfos = blockInfo.getEnvelopeInfos();

//        BlockInfo blockInfo1 = channel.queryBlockByHash(Hex.decodeHex("f2c246d3bc4ac59e4e42e094973e5997121f7b8703dc6567120a28af505ebdcb".toCharArray()));
//        System.out.println("区块的具体内容为：" + blockInfo1.getBlock().toBuilder().toString());
        List list = new ArrayList();
        for (BlockInfo.EnvelopeInfo envelopeInfo : envelopeInfos) {
            //从区块中获取channelid
            String channelId = envelopeInfo.getChannelId();
            System.out.println("通道id为：" + channelId);
            //获取落快时间
            Date timestamp = envelopeInfo.getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(timestamp);
            System.out.println("落快时间为：" + format);
            block.setTimeStamp(format);
            //定位是组织的那个节点
            System.out.println("提交事务的身份：" + envelopeInfo.getCreator().getMspid());


        }
//        Common.Block block = blockInfo.getBlock();
//        System.out.println("区块内容为:"+block.toString());
//        System.out.println("区块头为:"+block.getHeader());
//        String s = blockInfo.getTransActionsMetaData().toString();
//        System.out.println("交易元数据为："+s);
        return block;

    }

    /**
     * @description 获取orderer节点
     * @param name
     * @param grpcUrl
     * @param tlsFilePath
     * @return
     * @throws InvalidArgumentException
     */
    public Orderer getOrderer(String name, String grpcUrl, String tlsFilePath) throws InvalidArgumentException {
        Properties properties = new Properties();
        properties.setProperty("pemFile",tlsFilePath);
        Orderer orderer = hfClient.newOrderer(name,grpcUrl,properties);
        return orderer;
   }

    /**
     * @description 获取peer节点
     * @param name
     * @param grpcUrl
     * @param tlsFilePath
     * @return
     * @throws InvalidArgumentException
     */
   public Peer getPeer(String name, String grpcUrl, String tlsFilePath) throws InvalidArgumentException {
       Properties properties = new Properties();
       properties.setProperty("pemFile",tlsFilePath);
       Peer peer = hfClient.newPeer(name,grpcUrl,properties);
       return peer;
   }

    /**
     * @description 获取已有的channel
     * @param channelName
     * @return
     * @throws InvalidArgumentException
     * @throws TransactionException
     * @throws ProposalException
     */
   public Channel getChannel(String channelName) throws InvalidArgumentException, TransactionException, ProposalException {
       Channel channel =  hfClient.newChannel(channelName);
       return channel;
   }
}
