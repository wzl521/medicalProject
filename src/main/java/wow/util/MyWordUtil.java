package wow.util;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import wow.entity.MedicalRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Description
 * @autor wzl
 * @date 2021/5/5-21:35
 */
public class MyWordUtil {

    public static void changeWord(MedicalRecord medicalRecord, MultipartFile[] files) throws IOException {
        //        //模板路径
        XWPFTemplate template = XWPFTemplate.compile("C:\\Users\\wzl\\Desktop\\test.docx").render(
                new HashMap<String, Object>() {{
                    put("patientID", medicalRecord.getPatientID());
                    put("patientName", medicalRecord.getPatientName());
                    put("doctorName", medicalRecord.getDoctorName());
                    put("createTime", medicalRecord.getCreateTime());
                    put("description", medicalRecord.getDescription());
                    //File file =new File("C:\\Users\\wzl\\Desktop\\7.png");
                    //创建一个file对象
                    File file = new File("C:\\Users\\wzl\\Desktop\\1.png");
//将multipartFile的输入流，拷贝到file对象，此时file对象的内容就是mutipartFile中的内容
                    FileUtils.copyInputStreamToFile(files[0].getInputStream(), file);
                    System.out.println(file.getAbsolutePath());
                    put("image", Pictures.ofStream(new FileInputStream(file), PictureType.PNG)
                            .size(428, 720).create());


                    System.out.println("word文件生成成功");
                    System.out.println();


                }});
        //生成word文件路径
        template.writeAndClose(new FileOutputStream("C:\\Users\\wzl\\Desktop\\wordTest.doc"));
        System.out.println("开始转换为pdf文件");
        Document document = new Document();
        document.loadFromFile("C:\\Users\\wzl\\Desktop\\wordTest.doc");
        System.out.println("加载文件成功");
        String path = "C:\\Users\\wzl\\Desktop\\outputToPdf.pdf";

        document.saveToFile(path, FileFormat.PDF);
        System.out.println("转换pdf成功");

    }
}
