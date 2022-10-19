package cr.ac.ucr.ecci.arceshopping.model;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailManager {

    private String senderEmail = "swapitecci@gmail.com";
    private String passwordSenderEmail = "azakfdtukfskysdy";
    private String host = "smtp.gmail.com";
    private Properties properties;


    public EmailManager() {
        properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
    }

    public void sendPasswordEmail(String receiverEmail, String password) {
        try {
            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(senderEmail, passwordSenderEmail);
                        }
                    });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            mimeMessage.setSubject("ArceShopping: clave temporal");
            mimeMessage.setText("¡Gracias por registrarse en ArceShopping!\n\nSu nueva clave temporal es: " + password);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
