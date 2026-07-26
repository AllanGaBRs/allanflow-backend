package com.allan.task.manager.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService implements EmailService {

    private final Resend resend;
    private final ResendProperties properties;

    public ResendEmailService(ResendProperties properties) {
        this.properties = properties;
        this.resend = new Resend(properties.apiKey());
    }

    @Override
    public void send (String to,
                      String subject,
                      String html) {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(properties.from())
                .to(to)
                .subject(subject)
                .html(html)
                .build();

        try {
            resend.emails().send(email);
        } catch (ResendException exception) {
            throw new EmailDeliveryException(
                    "Não foi possível enviar o e-mail",
                    exception
            );
        }
    }
}