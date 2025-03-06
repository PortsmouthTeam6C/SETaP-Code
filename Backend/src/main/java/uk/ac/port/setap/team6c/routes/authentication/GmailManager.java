package uk.ac.port.setap.team6c.routes.authentication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GmailManager {

    private static final String EMAIL_ADDRESS = "synergyverif@gmail.com";
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_COMPOSE, GmailScopes.MAIL_GOOGLE_COM);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final Gmail SERVICE;

    static {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            SERVICE = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName("Team6C")
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an authorized credentials object
     * @param HTTP_TRANSPORT The network HTTP transport
     * @return An authorized credentials object
     * @throws IOException If the credentials.json file cannot be found
     * @author Adapted from <a href="https://developers.google.com/gmail/api/quickstart/java#set_up_the_sample">Google's Gmail API Quickstart</a>
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GmailManager.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: resources/credentials.json");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Creates an email MimeMessage object
     * @param recipient The recipient's email address
     * @param subject The email subject
     * @param body The email body
     * @return The email message object
     * @throws MessagingException If the email cannot be created
     * @author Adapted from <a href="https://developers.google.com/gmail/api/guides/sending#creating_messages">Google's Gmail Documentation</a>
     */
    private static @NotNull MimeMessage createEmail(String recipient, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(EMAIL_ADDRESS));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
        email.setSubject(subject);
        email.setText(body);
        return email;
    }

    /**
     *
     * @param email The email to be sent
     * @return The message object
     * @throws MessagingException If the email cannot be created
     * @throws IOException If the email cannot be created
     * @author Adapted from <a href="https://developers.google.com/gmail/api/guides/sending#creating_messages">Google's Gmail Documentation</a>
     */
    private static @NotNull Message createMessageWithEmail(@NotNull MimeMessage email) throws MessagingException, IOException {
        // Encode email as Base64
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        // Create the message
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email
     * @param recipient The recipient's email address
     * @param subject The email subject
     * @param body The email body
     * @return The message object
     */
    public static Message sendEmail(String recipient, String subject, String body) {
        try {
            MimeMessage mimeMessage = createEmail(recipient, subject, body);
            Message message = createMessageWithEmail(mimeMessage);
            return SERVICE.users().messages().send(EMAIL_ADDRESS, message).execute();
        } catch (MessagingException | IOException e) {
            // Consolidate all exceptions into a single runtime exception
            throw new RuntimeException(e);
        }
    }

}
