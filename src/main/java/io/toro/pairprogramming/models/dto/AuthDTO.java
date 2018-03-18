package io.toro.pairprogramming.models.dto;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

public class AuthDTO extends ResourceSupport{

  private String accessToken;
  private String tokenType;

  public AuthDTO setAccessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  public AuthDTO bearerType() {
    this.tokenType = "Bearer";
    return this;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }
}
