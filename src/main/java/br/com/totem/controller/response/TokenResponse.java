package br.com.totem.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String access_token;
    private String refresh_token;
    private String socket_token;
    private String type;
    private long expiresIn;
}
