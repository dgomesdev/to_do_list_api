package com.dgomesdev.to_do_list_api.service.interfaces;

public interface EmailService {
    void sendWelcomeMail(String to, String username);
    void sendResetPasswordMail(String to, String username, String code);
}
