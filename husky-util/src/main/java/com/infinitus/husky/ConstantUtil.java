package com.infinitus.husky;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wenshan on 16/7/10.
 */
public class ConstantUtil {

    //创建线程池，线程初始化4修改为40
    public static ExecutorService threadPool = Executors.newFixedThreadPool(40);

    public static final String  YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String  YYYYMMDDHHMMSS_withbias = "yyyy/MM/dd HH:mm:ss";

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String EXCEL_TYPE = "application/vnd.ms-excel";
    public static final String HTML_TYPE = "text/html";
    public static final String JS_TYPE = "text/javascript";
    public static final String JSON_TYPE = "application/json";
    public static final String XML_TYPE = "text/xml";
    public static final String TEXT_TYPE = "text/plain";

    public static String concat(String ...values){
        StringBuilder sb = new StringBuilder();
        for(String value: values){
            if(StringUtils.isNotEmpty(value)){
                sb.append(value);
            }
        }
        return sb.toString();
    }

    public static String urlConcat(String urlPri,String append ){
        if( !urlPri.endsWith("/")){
            urlPri= urlPri+ "/";
        }
        if(append.startsWith("/")){
            append = StringUtils.removeStart(append,"/");
        }
        return urlPri+append;
    }

}
