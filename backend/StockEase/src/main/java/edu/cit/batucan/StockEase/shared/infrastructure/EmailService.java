package edu.cit.batucan.StockEase.shared.infrastructure;

import edu.cit.batucan.StockEase.feature.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(User user) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Welcome to StockEase!");
                message.setText("Hello " + user.getFirstname() + ",\n\n" +
                        "Welcome to StockEase! Your account has been created successfully.\n" +
                        "You can now login with your credentials.\n\n" +
                        "Best regards,\nStockEase Team");
                mailSender.send(message);
            } else {
                System.out.println("Mail sender not configured. Skipping email to: " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }

    public void sendReceiptEmail(String email, String subject, String body) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            } else {
                System.out.println("Mail sender not configured. Skipping email to: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error sending receipt email: " + e.getMessage());
        }
    }
}
