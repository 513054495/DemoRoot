package com.infinitus.husky.enums;

import lombok.Getter;
import lombok.Setter;

public enum ResponseCodeEnum {
    SUCCESS(0, "成功"),
    SAVE_FAIL(1, "保存失败"),
    CHANGE_FAIL(2,"修改失败"),
    SEND_FAIL(3,"发送失败"),
    CREATE_FAIL(4,"创建失败"),
    DOWNLOAD_FAIL(5,"下载失败"),
    GET_FAIL(6,"获取失败"),
    DELETE_FAIL(7,"删除失败");

    @Getter
    @Setter
    private int code;
    @Getter
    @Setter
    private String msg;

    ResponseCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

