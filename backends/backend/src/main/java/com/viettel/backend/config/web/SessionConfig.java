package com.viettel.backend.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.viettel.backend.session.EnableUserDeactivationSessionDestroy;

@Configuration
@EnableRedisHttpSession
@EnableUserDeactivationSessionDestroy
public class SessionConfig {

}
