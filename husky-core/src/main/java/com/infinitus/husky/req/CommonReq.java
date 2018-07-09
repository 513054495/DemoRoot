package com.infinitus.husky.req;

import lombok.Getter;

public class CommonReq {
    @Getter
    private String serialNumber;

    public void setSerialNumber(String serialNumber) {
        this.serialNumber=serialNumber;
    }
}
