package br.com.totem.util;

import br.com.totem.controller.request.MensagemEmailRequest;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EnviarEmailHTML {
    public static void enviar(MensagemEmailRequest request) {
        String host = "smtp.zoho.com"; // Servidor SMTP do Zoho
        String from = request.getEmail(); // Seu e-mail no Zoho
        String to = "comercial@sincroled.com.br"; // E-mail do destinatário
        String password = "Sincroled@1520";

        // Configurações das propriedades do servidor
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Recomendado para Gmail
        properties.put("mail.smtp.ssl.trust", host); // Trust SSL

        // Criando a sessão de autenticação
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Criando o objeto MimeMessage
            MimeMessage message = new MimeMessage(session);

            // Definindo o remetente
            message.setFrom(new InternetAddress(from));

            // Definindo o destinatário
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Definindo o assunto
            message.setSubject(request.getAssunto());

            // Definindo o conteúdo HTML
            String htmlContent = "<html><body>"
                    + "<p>" + request.getMensagem() + "</p>"
                    + "</body></html>";
            message.setContent(htmlContent, "text/html");

            // Enviando o e-mail
            Transport.send(message);

            System.out.println("E-mail enviado com sucesso!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
