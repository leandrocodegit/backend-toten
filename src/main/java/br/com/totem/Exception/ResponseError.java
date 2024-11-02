package br.com.totem.Exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
public class ResponseError {

    private String message;
    private String codigo;
    private List<String> erros;
    private Timestamp timestamp;
}
