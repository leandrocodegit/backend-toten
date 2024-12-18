package br.com.totem.model.constantes;

public enum TipoToken {

    ACCESS("access_token"),
    REFRESH("refresh_token"),
    SOCKET("socket_token");

    private String value;

    TipoToken(String value) {
        this.value = value;
    }

    public String value(){
        return value;
    }

}
