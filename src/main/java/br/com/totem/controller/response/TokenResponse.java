package br.com.totem.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenResponse {

    private String access_token;
    private String refresh_token;
    private String comando_token;
    private String type;
    private long expiresIn;

    public TokenResponse(String comando_token, String type, long expiresIn) {
        this.comando_token = comando_token;
        this.type = type;
        this.expiresIn = expiresIn;
    }

    public TokenResponse(String access_token, String refresh_token, String comando_token, String type, long expiresIn) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.comando_token = comando_token;
        this.type = type;
        this.expiresIn = expiresIn;
    }
}
