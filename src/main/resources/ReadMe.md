#### 1. 本项目的技术框架图如下所示
![binaryTree](../resources/系统架构图.png "系统架构图" )
#### 2. 本系统采用Hyperledger Fabric联盟链平台搭建系统，另外还使用ipfs作为存储介质，因此在运行本项目之前需要配置好区块链环境,搭建ipfs。

#### 3. 区块链网络搭建

1.创建medical目录(完整的文件见fabric网络环境文件夹) 

```shell
# cd $GOPATH/src/github.com/hyperledger/fabric 

# mkdir medical

# cd medical
```

2.获取证书生成工具,从获取的bin目录拷贝到medical目录下，并设置运行权限。Bin目录下的文件如下图所示： 

![binaryTree](../resources/102401.png "102401" )

```shell
# chmod -R 777 ./bin 
```

3.准备生成证书和区块配置文件 

   配置 crypto-config.yaml 和 [configtx.yaml](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml) [文](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml)件，拷贝到 medical目录下，具体内容见所给的区块链网络搭建文档。 

4.生成公私钥和证书 

```shell
# ./bin/cryptogen generate --config=./crypto-config.yaml 
```

5.生成创世区块 

```shell
# mkdir channel-artifacts 

#./bin/configtxgen -profile TwoOrgsOrdererGenesis -outputBlock ./channel-artifacts/genesis.block
```

6.生成通道配置区块 

```shell
# ./bin/configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./channel-artifacts/mychannel.tx -channelID mychannel 
```

7.准备docker配置文件配置docker-orderer.yaml[和](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml)[ ](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml)[docker](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml)[-](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml)[peer.yaml文](https://github.com/hyperledger/fabric/blob/v1.0.0/examples/e2e_cli/configtx.yaml)件，拷贝到 medical目录下。 

   1)    docker-orderer.yaml：启动排序（Orderer）服务配置文件 

   2)    docker-peer.yaml：启动节点（peer）服务配置文件 

8.准备部署智能合约 

   拷贝编写好的智能合约文件到medical/chaincode/go/medical目录下。 

9.启动Fabric网络 

   1)启动orderer 

```shell
# docker-compose -f docker-orderer.yaml up -d 
```

  2)启动peer 

```shell
# docker-compose -f docker-peer.yaml up -d 
```

  3)启动cli容器 

```shell
# docker exec -it cli bash
```

  4)创建Channel  

```shell
#ORDERER_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

# peer channel create -o orderer.example.com:7050 -c mychannel -f ./channel-artifacts/mychannel.tx --tls --cafile $ORDERER_CA 
```

  5)Peer加入Channel 

```shell
# peer channel join -b mychannel.block 
```

10. 安装与运行智能合约 

  1）安装智能合约 

```shell
# peer chaincode install -n medical -p github.com/hyperledger/fabric/medical/chaincode/go/medical/cmd -v 1.0
```

  2）实例化智能合约 

```shell
# peer chaincode instantiate -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C mychannel -n medical -v 1.0 -c '{"Args":["firstKey","FirstValue"]}' -P "OR ('Org1MSP.peer')" 
```

  3）Peer上查询A，显示firstKey

```shell
# peer chaincode query -C mychannel -n medical -c '{"Args":["testGet","firstKey"]}'
```

![binaryTree](../resources/102402.png "102402" )

至此，fabric区块链环境已经搭建好了。

#### 4. 关于Ipfs的搭建，请参考我写的博客https://blog.csdn.net/qq_38716929/article/details/115731898?spm=1001.2014.3001.5502

#### 5. 上述准备工作完成之后，导入sql文件至Navicat for mysql 可视化工具中，使用IDEA运行前端和后端代码即可
