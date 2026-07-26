package com.allan.task.manager.workspaceinvitation;

import com.allan.task.manager.config.app.AppProperties;
import com.allan.task.manager.email.EmailService;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceInvitationEmailService {

    private final EmailService emailService;
    private final AppProperties appProperties;

    public WorkspaceInvitationEmailService(
            EmailService emailService,
            AppProperties appProperties
    ) {
        this.emailService = emailService;
        this.appProperties = appProperties;
    }

    public void sendInvitation(
            WorkspaceInvitationModel invitation,
            String code,
            int expirationDays
    ) {
        String invitationLink = "%s/invitations/%s"
                .formatted(
                        appProperties.frontendUrl(),
                        invitation.getId()
                );

        String html = """
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>Convite para o AllanFlow</h2>

                    <p>
                        <strong>%s</strong> convidou você para participar
                        do workspace:
                    </p>

                    <h3>%s</h3>

                    <p>Seu código de convite é:</p>

                    <div style="
                        font-size: 28px;
                        font-weight: bold;
                        letter-spacing: 6px;
                        margin: 24px 0;
                    ">
                        %s
                    </div>

                    <p>
                        Este convite expira em %d dias.
                    </p>

                    <p>
                        <a href="%s">
                            Aceitar convite
                        </a>
                    </p>

                    <p>
                        Caso ainda não possua uma conta, crie uma utilizando
                        este mesmo endereço de e-mail.
                    </p>
                </div>
                """.formatted(
                invitation.getInvitedBy().getName(),
                invitation.getWorkspace().getName(),
                code,
                expirationDays,
                invitationLink
        );

        emailService.send(
                invitation.getEmail(),
                "Convite para o workspace "
                        + invitation.getWorkspace().getName(),
                html
        );
    }
}
