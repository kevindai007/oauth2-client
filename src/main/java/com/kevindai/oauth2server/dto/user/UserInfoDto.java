package com.kevindai.oauth2server.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Builder
public class UserInfoDto {
    private Long id;
    private String username;
    private String email;
    private Date createdTime;
    private Date updateTime;
}
