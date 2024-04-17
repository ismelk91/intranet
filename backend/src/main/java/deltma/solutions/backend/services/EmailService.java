package deltma.solutions.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendInvitation(String email, String invitationLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@deltmasolutions.com");
            message.setTo(email);
            message.setSubject("Invitation to Register");
            message.setText("Hello!\n\nYou are invited to register on our platform. Use the following link to register: "
                    + invitationLink);
            javaMailSender.send(message);

            System.out.println("Invitation sent to: " + email);

        } catch (Exception e) {
            System.err.println("Error sending invitation.");
            e.printStackTrace();
        }
    }

    public void sendNewPassword(String email, String newPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@deltmasolutions.com");
            message.setTo(email);
            message.setSubject("New Password");
            message.setText("Hello!\n\nYour new password is: " + newPassword);

            javaMailSender.send(message);

            System.out.println("New password sent to: " + email);
        } catch (Exception e) {
            System.err.println("Error sending new password to: " + email);
            e.printStackTrace();
        }
    }

    public void sendSharedNewsArticle(String email, String subject, String newsArticle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@deltmasolutions.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText("Hello! A message has been shared with you. " + newsArticle);

            javaMailSender.send(message);

            System.out.println("News article sent to: " + email);
        } catch (Exception e) {
            System.err.println("Error sending news article to: " + email);
            e.printStackTrace();
            throw new RuntimeException("Error sending email");
        }
    }


}
