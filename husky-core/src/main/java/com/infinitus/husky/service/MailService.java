package com.infinitus.husky.service;

import com.infinitus.husky.resp.CommonResp;

public interface MailService extends BaseService {
    CommonResp sendSimpleMail(String to, String subject, String content);

    CommonResp sendHtmlMail(String to, String subject, String content);
}
