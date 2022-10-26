package cr.ac.ucr.ecci.arceshopping.model;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This class's purpose is to send emails to the users
 */
public class EmailManager {

    private String senderEmail = "swapitecci@gmail.com"; //our email, the sender
    private String passwordSenderEmail = "azakfdtukfskysdy"; // email's password
    private String host = "smtp.gmail.com";
    private Properties properties;

    /**
     * Class's constructor
     */
    public EmailManager() {
        properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
    }

    /**
     * @Param receiverEmail The user's email. The email that will receive the message
     * @Param password The randomly generated password that will be send
     * Class's main method. Sends a randomly generated password to the user's email
     */
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
