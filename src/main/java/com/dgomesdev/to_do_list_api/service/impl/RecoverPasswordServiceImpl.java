package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.service.interfaces.RecoverPasswordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RecoverPasswordServiceImpl implements RecoverPasswordService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public RecoverPasswordServiceImpl(StringRedisTemplate redisTemplate, JavaMailSender mailSender) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
    }

    @Override
    public String generateCode(UUID userId) {
        String code = String.valueOf(UUID.randomUUID());
        storeCode(userId.toString(), code);
        return code;
    }

    @Override
    public void validateCode(UUID key, String code) {
        if (!Objects.equals(recoverCode(key.toString()), code)) throw new UnauthorizedUserException(key);
    }

    private void storeCode(String key, String code) {
        redisTemplate.opsForValue().set(key, code, 15, TimeUnit.MINUTES);
    }

    private String recoverCode(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
