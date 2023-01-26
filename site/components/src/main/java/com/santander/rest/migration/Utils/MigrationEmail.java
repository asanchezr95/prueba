package com.santander.rest.migration.Utils;

import com.santander.utils.Constants;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.hippoecm.hst.container.RequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;
import static com.santander.utils.Constants.LOCALHOST;

public class MigrationEmail {

    private static final Logger LOGGER = LoggerFactory.getLogger( MigrationEmail.class);

    public static void sendMail(String mail, String nombre_documento, String message, String productFolderPath){
        LOGGER.info("entras a sendMail");
        try {

            String host ="";
            try{
                host = RequestContextProvider.get().getBaseURL().getHostName();
                LOGGER.info("Recoges host");

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Error envio por variables de entorno", e);
                LOGGER.info("Fallo host");

            }

            if(!LOCALHOST.contains(host)){
                LOGGER.info("Entras en contains ");

                final HtmlEmail email = new HtmlEmail();
                
                email.setSSLCheckServerIdentity(true);
                email.setSSLOnConnect(true);
                LOGGER.info("set setSSLOnConnect ");
                
                LOGGER.info("Entras en getSession ");
                final Session session = getSession(host);

                LOGGER.info("sales en contains ");
                String to  = mail;

                if (session == null) {
                    LOGGER.error("Unable to send mail; no mail session available");
                }
                String html = Constants.BODY_MAIL_MIGRATION1 + nombre_documento + "<br/>" ;
                if(!productFolderPath.isEmpty ()){
                    html = html + " En la ruta: " + productFolderPath;}
                html = html + Constants.BODY_MAIL_MIGRATION2;
                
                LOGGER.info("set MailSession ");
                email.setMailSession(session);

                email.setFrom("no-reply@pagonxt.tech");
                email.setSubject(message);
                email.addTo(to);
                email.setHtmlMsg(html);
                email.setHtmlMsg(html);
                email.send();
                LOGGER.info("envias Mail");

            }

        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    protected static Session getSession(String host) {
        Properties props = new Properties();
        try {

            String mailhost = "";
            String mailport =  "";
            String username =  "";
            String ps = "";

            if(LOCALHOST.contains(host)){
                LOGGER.info("Entras en variables locales ");

                mailhost = "eu.smtp.pagonxt.corp";
                 mailport = "25";
                 username = "false";
                 ps = "false";

            }else {
                 LOGGER.info("Entras en variables entorno ");

                 mailhost = System.getenv("SMTP_HOST");
                 mailport = System.getenv("SMTP_PORT");
                 username = System.getenv("SMTP_USER");
                 ps = System.getenv("SMTP_PASSWORD");
            }

            LOGGER.info("mailhost {} ", mailhost);
            LOGGER.info("mailport {} ", mailport);
            LOGGER.info("username {} ", username);
            LOGGER.info("ps {} ", ps);

            props.put("mail.smtp.host", mailhost);
            props.put("mail.smtp.port", mailport);

            if (!"false".equals(username)) {
                LOGGER.info("Entras en validacion username != false con variables de entorno");
                props.put("mail.smtp.auth", "true");
                Authenticator authenticator = new DefaultAuthenticator(username, ps);
                return Session.getInstance(props, authenticator);
            } else {
                LOGGER.info("Entras en validacion username == false con variables de entorno");
                props.put("mail.smtp.auth", "false");
                return Session.getInstance(props);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error envio por variables de entorno", e);
            LOGGER.info("Fallas en GetSession");

        }
        return Session.getInstance(props);

    }

}

