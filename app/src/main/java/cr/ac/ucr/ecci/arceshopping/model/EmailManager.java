package cr.ac.ucr.ecci.arceshopping.model;

import java.util.ArrayList;
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
    public void sendPasswordEmail(String name, String email, String password) {
        //"¡Gracias por registrarse en ArceShopping!\n\nSu nueva clave temporal es: " + password
        String subject = "ArceShopping: clave temporal";
        String body = "<html>\n" +
                "  <head>\n" +
                "    <style type=\"text/css\" rel=\"stylesheet\" media=\"all\">\n" +
                "    \n" +
                "    @import url(\"https://fonts.googleapis.com/css?family=Nunito+Sans:400,700&display=swap\");\n" +
                "    body {\n" +
                "      width: 100% !important;\n" +
                "      height: 100%;\n" +
                "      margin: 0;\n" +
                "      background-color: #F2F4F6;\n" +
                "      color: #51545E;\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    td {\n" +
                "      word-break: break-word;\n" +
                "    }\n" +
                "    \n" +
                "    /* Type ------------------------------ */\n" +
                "    \n" +
                "    body,\n" +
                "    td,\n" +
                "    th {\n" +
                "      font-family: \"Nunito Sans\", Helvetica, Arial, sans-serif;\n" +
                "    }\n" +
                "    \n" +
                "    h1 {\n" +
                "      margin-top: 0;\n" +
                "      color: #333333;\n" +
                "      font-size: 22px;\n" +
                "      font-weight: bold;\n" +
                "      text-align: left;\n" +
                "    }\n" +
                "    \n" +
                "    td,\n" +
                "    th {\n" +
                "      font-size: 16px;\n" +
                "    }\n" +
                "    \n" +
                "    p {\n" +
                "      margin: .4em 0 1.1875em;\n" +
                "      font-size: 16px;\n" +
                "      line-height: 1.625;\n" +
                "      color: #51545E;\n" +
                "    }\n" +
                "\n" +
                "    .email-wrapper {\n" +
                "      width: 100%;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      -premailer-width: 100%;\n" +
                "      -premailer-cellpadding: 0;\n" +
                "      -premailer-cellspacing: 0;\n" +
                "      background-color: #F2F4F6;\n" +
                "    }\n" +
                "    \n" +
                "    .email-content {\n" +
                "      width: 100%;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      -premailer-width: 100%;\n" +
                "      -premailer-cellpadding: 0;\n" +
                "      -premailer-cellspacing: 0;\n" +
                "    }\n" +
                "  \n" +
                "    .email-body {\n" +
                "      width: 100%;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      -premailer-width: 100%;\n" +
                "      -premailer-cellpadding: 0;\n" +
                "      -premailer-cellspacing: 0;\n" +
                "    }\n" +
                "    \n" +
                "    .email-body_inner {\n" +
                "      width: 570px;\n" +
                "      margin: 0 auto;\n" +
                "      padding: 0;\n" +
                "      -premailer-width: 570px;\n" +
                "      -premailer-cellpadding: 0;\n" +
                "      -premailer-cellspacing: 0;\n" +
                "      background-color: #FFFFFF;\n" +
                "    }\n" +
                "  \n" +
                "    .content-cell {\n" +
                "      padding: 45px;\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    @media only screen and (max-width: 600px) {\n" +
                "      .email-body_inner {\n" +
                "        width: 100% !important;\n" +
                "      }\n" +
                "    }\n" +
                "    \n" +
                "    @media (prefers-color-scheme: dark) {\n" +
                "      body,\n" +
                "      .email-body,\n" +
                "      .email-body_inner,\n" +
                "      .email-content,\n" +
                "      .email-wrapper {\n" +
                "        background-color: #333333 !important;\n" +
                "        color: #FFF !important;\n" +
                "      }\n" +
                "      p,\n" +
                "      h1{\n" +
                "        text-shadow: none !important;\n" +
                "      }\n" +
                "    }\n" +
                "    \n" +
                "    :root {\n" +
                "      color-scheme: light dark;\n" +
                "      supported-color-schemes: light dark;\n" +
                "    }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  \n" +
                "  <body>\n" +
                "    <table class=\"email-wrapper\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n" +
                "      <tr>\n" +
                "        <td align=\"center\">\n" +
                "          <table class=\"email-content\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n" +
                "            <!-- Email Body -->\n" +
                "            <tr>\n" +
                "              <td class=\"email-body\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                <table class=\"email-body_inner\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n" +
                "                  <!-- Body content -->\n" +
                "                  <tr>\n" +
                "                    <td class=\"content-cell\">\n" +
                "                      <div class=\"f-fallback\">\n" +
                "                        <h1>Hola " + name + ",</h1>\n" +
                "                        <p> Te acabas de registrar en ArceShopping. Utiliza la siguiente clave temporal para ingresar a tu cuenta: <strong>" + password + "</strong></p>\n" +
                "                        \n" +
                "                        <p>Por tu seguridad, no compartas esta clave con nadie. Si no pediste esta clave, por favor ignora este correo. </p>\n" +
                "                        \n" +
                "                        <p>¡Gracias por registrate en ArceShopping! </p>\n" +
                "                      </div>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>";

        sendEmail(email, subject, body);
    }

    public void sendPurchaseEmail(User user, ArrayList<Product> products) {
        String subject = "";
        String body = "";

        sendEmail(user.getEmail(), subject, body);
    }

    public void sendEmail(String receiverEmail, String subject, String body) {
        try {
            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(senderEmail, passwordSenderEmail);
                        }
                    });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            mimeMessage.setSubject(subject);
            mimeMessage.setContent(body, "text/html");

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
