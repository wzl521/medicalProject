//package wow.util;
//
//import com.deepoove.poi.XWPFTemplate;
//import com.deepoove.poi.data.PictureRenderData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;
//import org.springframework.web.multipart.MultipartFile;
//import wow.entity.MedicalRecord;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//
///**
// * @Description
// * @autor wzl
// * @date 2021/4/14-20:05
// */
//
///**
// * @author wanghuyue
// * @Title: WordUtil
// * @Description: Word工具类
// * @date 2020/10/9 9:09
// */
//public class WordUtil {
//
//    private static Logger logger = LoggerFactory.getLogger(WordUtil.class);
//
//
//    /**
//     * 根据模板填充内容生成word
//     * 调用方法参考下面的main方法，详细文档参考官方文档
//     * Poi-tl模板引擎官方文档：http://deepoove.com/poi-tl/
//     *
//     * @param templatePath  word模板文件路径
//     * @param fileDir       生成的文件存放地址
//     * @param fileName      生成的文件名,不带格式。假如要生成abc.docx，则fileName传入abc即可
//     * @param medicalRecord 替换的参数集合
//     * @return 生成word成功返回生成的文件的路径，失败返回空字符串
//     */
//    public static String createWord(String templatePath, String fileDir, String fileName, MedicalRecord medicalRecord) throws FileNotFoundException {
//        Assert.notNull(templatePath, "word模板文件路径不能为空");
//        Assert.notNull(fileDir, "生成的文件存放地址不能为空");
//        Assert.notNull(fileName, "生成的文件名不能为空");
//
//        // 生成的word格式
//        String formatSuffix = ".docx";
//        // 拼接后的文件名
//        fileName = fileName + formatSuffix;
//
//        // 生成的文件的存放路径
//        if (!fileDir.endsWith("/")) {
//            fileDir = fileDir + File.separator;
//        }
//
//        File dir = new File(fileDir);
//        if (!dir.exists()) {
//            logger.info("生成word数据时存储文件目录{}不存在,为您创建文件夹!", fileDir);
//            dir.mkdirs();
//        }
//
//        String filePath = fileDir + fileName;
//        // 读取模板templatePath并将paramMap的内容填充进模板，即编辑模板+渲染数据
//        // 图片流
//
//        //medicalRecord.setImage((File) streamImg);
//        //medicalRecord.setImage(new PictureRenderData(654,1198,medicalRecord.getMedicalPicture()));
//        medicalRecord.setImage((MultipartFile) new PictureRenderData(654, 1198, medicalRecord.getMedicalPicture()));
//        XWPFTemplate template = XWPFTemplate.compile(templatePath).render(medicalRecord);
//        try {
//            // 将填充之后的模板写入filePath
//            template.writeToFile(filePath);
//            template.close();
//        } catch (Exception e) {
//            logger.error("生成word异常", e);
//            e.printStackTrace();
//            return "";
//        }
//        return filePath;
//    }
//
////    public static void main(String[] args) {
////        MedicalRecord medicalRecord = new MedicalRecord();
////        medicalRecord.setPatientID(1);
////        medicalRecord.setPatientName("钱海洋");
////        medicalRecord.setDoctorName("wzl");
////        medicalRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
////        medicalRecord.setDescription("该病人无大碍");
////        //medicalRecord.setMedicalPicture("C:\\Users\\wzl\\Desktop\\xindiantu.png");
//////        Dates dates = new Dates();
//////        dates.setChun("大河");
//////        dates.setName("asdasd");
//////        dates.setZu("aa");
//////        dates.setHouseNum("100");
//////        dates.setIdCard("45345553");
//////        dates.setPhone("1231233123");
////        medicalRecord.setImage(new PictureRenderData(654, 1198, "C:\\Users\\wzl\\Desktop\\xindiantu.png"));
//////        dates.setImageZdct(new PictureRenderData(100, 100, "D:\\face-200720DYP86CMSA8.jpg"));
//////        // 渲染图片
////        // TODO 渲染其他类型的数据请参考官方文档
////        String templatePath = "C:\\Users\\wzl\\Desktop\\test.docx";
////        String fileDir = "C:\\Users\\wzl\\Desktop";
////        String fileName = "测试文档";
////
////        System.out.println();
////        String wordPath = WordUtil.createWord(templatePath, fileDir, fileName, medicalRecord);
////        System.out.println("生成文档路径：" + wordPath);
////
////        Document document =new Document();
////        document.loadFromFile(wordPath);
////
////
////
////        String path ="C:\\Users\\wzl\\Desktop\\outputToPdf.pdf";
////
////        document.saveToFile(path, FileFormat.PDF);
////        System.out.println("转换pdf成功");
////        //上传ipfs
////        try {
////            System.out.println("上传电子病历");
////            String hash = IPFSUtil.add(path);
////
////            System.out.println(hash);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////    }
//}
//
//
