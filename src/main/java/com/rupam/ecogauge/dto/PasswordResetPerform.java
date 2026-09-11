package com.rupam.ecogauge.dto;
import lombok.Data;
@Data
public class PasswordResetPerform {
    private String token;
    private String newPassword;
}