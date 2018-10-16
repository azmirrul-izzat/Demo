package com.apddemo.core.services;

public interface CaptchaService {
    Boolean validateCaptcha(String recaptchaResponse);
}
