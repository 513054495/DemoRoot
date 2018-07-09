package com.infinitus.husky;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    private static final Logger logger= LoggerFactory.getLogger(FileUtils.class);

    public static void createDir(String dirName) throws Exception {
        File dir = new File(dirName);
        if (dir.exists()) {
            logger.info("目标目录已经存在");
            return;
        }
        if (!dirName.endsWith(File.separator)) {
            dirName = dirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            logger.info("创建目录" + dirName + "成功！");
        } else {
            logger.info("创建目录" + dirName + "失败！");
            throw new Exception("创建目录失败");
        }
    }

    public static void createFile(String destFileName,String context) throws Exception {
        File file = new File(destFileName);
        if (destFileName.endsWith(File.separator)) {
            logger.error("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            throw new Exception("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            logger.info("目标文件所在目录不存在，准备创建它！");
            if(!file.getParentFile().mkdirs()) {
                logger.error("创建目标文件所在目录失败！");
                throw new Exception("创建目标文件所在目录失败！");
            }
        }
        //创建目标文件
        if(file.exists()) {
            logger.error("创建单个文件" + destFileName + "失败，目标文件已存在！");
        }else{
            file.createNewFile();
        }
        //写入内容
        if(!StringUtils.isBlank(context)){
            FileWriter fileWriter=new FileWriter(destFileName);
            fileWriter.write(context);
            fileWriter.close();
        }
    }

    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName){
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        BufferedOutputStream bos=null;
        ZipEntry zipEntry=null;
        if(sourceFile.exists() == false){
            logger.info("待压缩的文件目录："+sourceFilePath+"不存在.");
            return false;
        }
        try {
            File zipFile = new File(zipFilePath + "/" + fileName +".zip");
            if(zipFile.exists()){
                zipFile.delete();
            }
            List<File> files=new ArrayList<>();
            getAllFile(files,sourceFile);
            fos = new FileOutputStream(zipFile);
            bos=new BufferedOutputStream(fos);
            zos = new ZipOutputStream(bos);
            byte[] bufs = new byte[1024*10];
                for(int i=0;i<files.size();i++){
                    //创建ZIP实体，并添加进压缩包
                    zipEntry = new ZipEntry(files.get(i).getPath());
                    zos.putNextEntry(zipEntry);
                    //读取待压缩的文件并写进压缩包里
                    fis = new FileInputStream(files.get(i));
                    bis = new BufferedInputStream(fis, 1024*10);
                    int read = 0;
                    while((read=bis.read(bufs, 0, 1024*10)) != -1){
                        zos.write(bufs,0,read);
                    }
                    if(null != bis) bis.close();
                }
                flag = true;
                if(null != zos) zos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally{
            //关闭流
            try {
                if(null != fis) fis.close();
                if(null != bos) bos.close();
                if(null != fos) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return flag;
    }

    public static void fileDownload(String filePath,String fileName, HttpServletResponse res) throws Exception {
        BufferedInputStream bis = null;
        OutputStream os = null;
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        os = res.getOutputStream();
        bis = new BufferedInputStream(new FileInputStream(new File(filePath+"//"
                + fileName)));
        int i = bis.read(buff);
        while (i != -1) {
            os.write(buff, 0, buff.length);
            os.flush();
            i = bis.read(buff);
        }
        bis.close();
        os.close();
    }

    private static void getAllFile(List<File> files,File sourceFile){
        File[] sourceFiles = sourceFile.listFiles();
        if(null == sourceFiles || sourceFiles.length<1){
            logger.info("待压缩的文件目录里面不存在文件，无需压缩.");
        }else{
            for(int i=0;i<sourceFiles.length;i++){
                if(sourceFiles[i].isDirectory()){
                    getAllFile(files,sourceFiles[i]);
                }else{
                    files.add(sourceFiles[i]);
                }
            }
        }
    }

    public static boolean deleteDir(String path){
        File file = new File(path);
        if(!file.exists()){//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
            return false;
        }

        String[] content = file.list();//取得当前目录下所有文件和文件夹
        for(String name : content){
            File temp = new File(path, name);
            if(temp.isDirectory()){//判断是否是目录
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                temp.delete();//删除空目录
            }else{
                if(!temp.delete()){//直接删除文件
                    logger.error("Failed to delete " + name);
                }
            }
        }
        return true;
    }

}
