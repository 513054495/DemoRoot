package com.infinitus.husky.resp;

import com.infinitus.husky.enums.ResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class CommonResp {
    /** 是否成功标识 */
    @Getter
    @Setter
    private boolean success;

    /** 响应码 */
    @Getter
    @Setter
    private int code;

    /** 返回信息 */
    @Getter
    @Setter
    private String msg;


    /** 系统级错误信息 */
    @Getter
    @Setter
    private String errorMessage;

    /** 返回数据 */
    @Getter
    private Map<String,Object> data;

    public CommonResp(String msg) {
        this.success = true;
        this.code= ResponseCodeEnum.SUCCESS.getCode();
        this.msg = msg+ResponseCodeEnum.SUCCESS.getMsg();
    }

    public CommonResp(String msg,String key,Object data) {
        this.success = true;
        this.code= ResponseCodeEnum.SUCCESS.getCode();
        this.msg = msg+ResponseCodeEnum.SUCCESS.getMsg();
        this.addNewData(key,data);
    }

    public CommonResp(int code,String msg) {
        this.success = false;
        this.code= code;
        this.msg = msg;
    }

    public CommonResp( int code,String msg,Exception e) {
        this.success = false;
        this.code= code;
        this.msg = msg;
        this.errorMessage=e.getMessage();
    }

    public void addNewData(String key, Object object){
        if(data == null){
            data = new HashMap<String, Object>();
        }
        data.put(key,object);
    }

}
