package com.tus.oauthserver.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OauthController {

  @RequestMapping("/auth/user")
  public Map<String, Object> user(OAuth2Authentication user)
  {
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("user", user.getUserAuthentication().getPrincipal());
    userInfo.put("authorities", AuthorityUtils.authorityListToSet(
        user.getUserAuthentication().getAuthorities()));
    return userInfo;
  }

}
