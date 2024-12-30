package com.dgomesdev.to_do_list_api.service.interfaces;

import java.util.UUID;

public interface RecoverPasswordService {

    String generateCode(UUID userId);
    void validateCode(UUID key, String code);
    void sendMail(String to, String subject, String body);
}
