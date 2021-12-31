package wow.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class downLoadIpfs {
    public static void downLoad(String hashAddress) throws Exception {
        // 下载地址
        //String downURL = "http://192.168.163.137:8080/ipfs/QmW4DixeYbFFwSMarFRekSrw7WAiaBXW6F4Yt1o2QrBEwP";
        // 地址
        System.out.println(hashAddress);

        try {
            String newHashAddress ="http://192.168.163.137:8080/ipfs/"+hashAddress;
            URL url = new URL(newHashAddress);
            System.out.println("newHashAddress is:"+newHashAddress);
            // 获取文件后缀名
            String[] split = url.getFile().split("\\.");

            // 打开地址
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // 获取流
            InputStream is = urlConnection.getInputStream();

            // 写入流
            Random random = new Random();
            FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\wzl\\Desktop\\电子病历.pdf" ));
            System.out.println("文件下载成功");

            // 写入文件
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer,0,len);
            }

            // 关闭流
            fos.close();
            is.close();
            urlConnection.disconnect(); // 断开连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }
}

