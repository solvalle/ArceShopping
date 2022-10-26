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
        // Html adapted from: https://codepen.io/rKalways/pen/VwwQKpV
        String body = "<!doctype html>\n" +
                "<html lang=\"en-US\">\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "</head>\n" +
                "<body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\" style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\n" +
                "    <!--100% body table-->\n" +
                "    <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\n" +
                "        style=\"@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500,700|Open+Sans:300,400,600,700); font-family: 'Open Sans', sans-serif;\">\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <table style=\"background-color: #f2f3f8; max-width:670px;  margin:0 auto;\" width=\"100%\" border=\"0\"\n" +
                "                    align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td>\n" +
                "                            <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                style=\"max-width:670px;background:#fff; border-radius:3px; text-align:center;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 35px;\">\n" +
                "                                        <h1 style=\"color:#1e1e2d; font-weight:500; margin:0;font-size:32px;font-family:'Rubik',sans-serif;\">Hola "+ name +",</h1>\n" +
                "                                        <span\n" +
                "                                            style=\"display:inline-block; vertical-align:middle; margin:29px 0 26px; border-bottom:1px solid #cecece; width:100px;\"></span>\n" +
                "                                        <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0; text-align: left;\">\n" +
                "                                           Te acabas de registrar en ArceShopping. Utiliza la siguiente clave temporal para ingresar a tu cuenta:<b> " + password + "</b>\n" +
                "                                        </p>\n" +
                "                                        <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0;  text-align: left;\">\n" +
                "                                            Por tu seguridad, no compartas esta clave con nadie. Si no pediste esta clave, por favor ignora este correo. \n" +
                "                                        </p>\n" +
                "                                        <br/>\n" +
                "                                        <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0; font-weight: bold;\">Gracias por registrate en ArceShopping! </p>\n" +
                "                                        \n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    <!--/100% body table-->\n" +
                "</body>\n" +
                "</html>";

        sendEmail(email, subject, body);
    }

    public void sendPurchaseEmail(String name, String email, ArrayList<Product> products, String precioTotal) {
        String subject = "ArceShopping: tu compra ha sido completada";
        // Html adapted from: https://codepen.io/rKalways/pen/MWWQjvm
        String body = "<!doctype html>\n" +
                "<html lang=\"en-US\">\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "</head>\n" +
                "<body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\" style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\n" +
                "    <!--100% body table-->\n" +
                "    <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\n" +
                "        style=\"@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500,700|Open+Sans:300,400,600,700); font-family: 'Open Sans', sans-serif;\">\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <table style=\"background-color: #f2f3f8; max-width:670px; margin:0 auto;\" width=\"100%\" border=\"0\"\n" +
                "                    align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                    <tr>\n" +
                "                        <td height=\"20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <!-- Email Content -->\n" +
                "                    <tr>\n" +
                "                        <td>\n" +
                "                            <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" \n" +
                "                                style=\"padding: 5%;max-width:600px; background:#fff; border-radius:3px; text-align:left;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                                <!-- Title -->\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 15px; text-align:center;\">\n" +
                "                                        <h1 style=\"color:#1e1e2d; font-weight:400; margin:0;font-size:32px;font-family:'Rubik',sans-serif;\">Hola " + name + ", </h1>\n" +
                "                                        <span style=\"display:inline-block; vertical-align:middle; margin:29px 0 26px; border-bottom:1px solid #cecece; \n" +
                "                                        width:100px;\"></span>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                  <tr>\n" +
                "                                    <td>\n" +
                "                                      <p style=\"font-size:15px; color:#455056; margin: 29px 0 26px; line-height:24px; margin:8px 0 30px;\">Gracias por visitar ArceShopping. Hemos terminado de procesar tu pedido.</p>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "\n" +
                "\n" +
                "                                <!-- Details Table -->\n" +
                "                                <tr>\n" +
                "                                    <td>\n" +
                "                                        <table cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                            style=\"width: 100%; border: 1px solid #ededed\">\n" +
                "                                            <tbody>\n" +
                "                                                <tr>\n" +
                "                                                    <td\n" +
                "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; border-right: 1px solid #ededed; width: 35%; font-weight:bold; color:rgba(0,0,0,.64)\">\n" +
                "                                                        Producto</td>\n" +
                "                                                    <td\n" +
                "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; border-right: 1px solid #ededed; width: 35%; font-weight:bold; color:rgba(0,0,0,.64)\">\n" +
                "                                                        Cantidad</td>     \n" +
                "                                                    <td\n" +
                "                                                    style=\"padding: 10px; border-bottom: 1px solid #ededed; border-right: 1px solid #ededed; width: 35%; font-weight:bold; color:rgba(0,0,0,.64)\">\n" +
                "                                                    Precio</td> " +
                "                                           </tr>\n";

        for (Product product : products) {
            body += "                                                <tr>\n" +
                    "                                                    <td\n" +
                    "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; color: #455056; border-right: 1px solid #ededed; \">\n" +
                    "                                                        "+ product.getTitle() +"</td>\n" +
                    "\n" +
                    "                                                    <td\n" +
                    "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; color: #455056; border-right: 1px solid #ededed; \">\n" +
                    "                                                        "+ product.getItemsInCart() +"</td>\n" +
                    "\n" +
                    "                                                    <td\n" +
                    "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; color: #455056; border-right: 1px solid #ededed; \">\n" +
                    "                                                         "+ product.getPrice() +"</td>\n" +
                    "                                                </tr>\n";
        }
        body += "                                                <tr>\n" +
                "                                                    <td\n" +
                "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; color: #455056;  font-weight:bold;  border-right: 1px solid #ededed; \">\n" +
                "                                                        Total</td>\n" +
                "                                                    <td\n" +
                "                                                        style=\"padding: 10px; border-bottom: 1px solid #ededed; color: #455056; font-weight:bold; \">\n" +
                "                                                        " + precioTotal + "</td>\n" +
                "                                                </tr>\n" +
                "                                            </tbody>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td height=\"20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        sendEmail(email, subject, body);
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
