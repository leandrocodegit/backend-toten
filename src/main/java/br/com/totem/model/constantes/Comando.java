package br.com.totem.model.constantes;

public enum Comando {

    ACEITO("Comando aceito pelo dispositivo"),
    ENVIADO("Comando enviado"),
    ENVIAR("Comando pendente"),
    SISTEMA("Comando enviado pelo sistema"),
    NENHUM_DEVICE("Comando não enviado, dispositivo inválido"),
    ONLINE("Dispositivo %S online"),
    OFFLINE("Dispositivo %S offline"),
    SINCRONIZAR("Sincronização enviada"),
    CONFIGURACAO("Solicitação de paramentros configuração"),
    TESTE("Teste de cor"),
    CONCLUIDO("Teste concluido");

    private String value;

    Comando(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
