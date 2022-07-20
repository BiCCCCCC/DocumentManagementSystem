package cn.edu.swu.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;

public class UploadUtil {


    public static Boolean checkImg(List<MultipartFile> fileList) {
        if (fileList.isEmpty()) {
            return false;
        } else {
            for (MultipartFile file : fileList) {
                try {
                    //通过ImageIO检查是否是 bmp/gif/jpg/png图片，是则有东西返回（具体是啥，我也不清楚）
                    Image image = ImageIO.read(file.getInputStream());
                    return image != null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }


    public static String upload(List<MultipartFile> fileList, HttpServletRequest request, String dirPath) {
        //定义URI地址
        String fileDownloadUri = "";
        for (MultipartFile file : fileList) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            //获取年份，月份，天
            Calendar date = Calendar.getInstance();
            String year = String.valueOf(date.get(Calendar.YEAR));
            String month = String.valueOf(date.get(Calendar.MONTH) + 1);
            String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
            // 新文件名
            fileName = year + "/" + month + "/" + day + "/" + System.currentTimeMillis() + fileName;
            //创建文件
            File dest = new File(dirPath + fileName);
            //判断文件父目录是否存在
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs(); //这里因为创建的是多级目录，所以需要使用mkdirs()方法。使用mkdir()方法则文件夹创建不成功，会报找不到路径错误。
            }
            try {
                //将文件内容写入创建的文件中
                file.transferTo(dest);
                //获得本机Ip（获取的是服务器的Ip）
                InetAddress inetAddress = InetAddress.getLocalHost();
                String ip = inetAddress.getHostAddress();
                //URL地址的格式：http://ip:port/文件路径
                if (fileDownloadUri == "") {
                    fileDownloadUri = request.getScheme() + "://" + ip + ":" + request.getServerPort() + "/upload/" + fileName;
                } else {
                    fileDownloadUri = fileDownloadUri + "," + request.getScheme() + "://" + ip + ":" + request.getServerPort() + "/upload/" + fileName;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        //返回Url地址，以逗号分隔
        return fileDownloadUri;
    }


    public static Boolean deleteFile(String dirPath, String url) {
        //截取第四个“/”后面的字符串
        int i1 = url.indexOf('/');
        int i2 = url.indexOf('/', i1 + 1);
        int i3 = url.indexOf('/', i2 + 1);
        int i4 = url.indexOf("/", i3 + 1);
        String substring = url.substring(i4 + 1, url.length());
        String relPath = dirPath + substring;
        Boolean flag = false;
        File file = new File(relPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
}

