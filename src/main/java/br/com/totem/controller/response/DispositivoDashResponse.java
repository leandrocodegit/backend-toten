package br.com.totem.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoDashResponse {

    private int total;
    private int online;
    private int offline;

    @Override
    public String toString() {
        return "DispositivoDashResponse{" +
                "total=" + total +
                ", online=" + online +
                ", offline=" + offline +
                '}';
    }
}
