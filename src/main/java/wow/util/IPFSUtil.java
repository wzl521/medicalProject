package wow.util;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IPFSUtil {
    private static IPFS ipfs = new IPFS("/ip4/192.168.163.137/tcp/5001");

    public static String add(String filePath) throws IOException {
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(filePath));
        MerkleNode addResult = ipfs.add(file).get(0);
        //System.out.println("文件上传成功,返回的hash值为：");
        return addResult.hash.toString();
    }

    public static void download(String filePathName, String hash) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hash);
        byte[] data = ipfs.cat(filePointer);
        if (data != null) {
            File file = new File(filePathName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        }
    }

//    public static void main(String[] args) {
//        try {
//            String hash = IPFSUtil.add("C:\\Users\\wzl\\Desktop\\test.docx");
//            System.out.println(hash);
//            // QmfGp2pRCs3VidFKEVh6NFdrJ3usfrRDr2KXW9k4t3EfUK
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
    }




