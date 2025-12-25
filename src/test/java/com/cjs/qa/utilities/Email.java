package com.cjs.qa.utilities;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;

public final class Email {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Email.class));

  // linkFilePath = "C:" + Constants.DELIMETER_PATH + "Program Files" +
  // Constants.DELIMETER_PATH + "File Name" + IExtension.TXT
  // linkFile = "linkFile [file: " + resultsFile.replace(" ", "%20")" + "]"

  private Email() {
    // Empty
  }

  public static final String SESSION_DEBUG = "false";
  public static final String LABEL_FROM = "from";
  public static final String LABEL_PASSWORD = "password";
  public static final String LABEL_EMAIL_TO = "emailTO";
  public static final String LABEL_EMAIL_CC = "emailCC";
  public static final String LABEL_EMAIL_BC = "emailBCC";
  public static final String LABEL_SUBJECT = "subject";
  public static final String LABEL_BODY = "body";
  public static final String LABEL_ATTACHMENT = "attachment";
  public static final String LABEL_MAIL_DOMAIN = "mailDomain";
  public static final String LABEL_HOST = "host";
  public static final String LABEL_USERID = "userID";

  private static MailcapCommandMap addMailcapCommandMap() throws Exception {
    final MailcapCommandMap mailcapCommandMap =
        (MailcapCommandMap) CommandMap.getDefaultCommandMap();
    mailcapCommandMap.addMailcap(
        "text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
    mailcapCommandMap.addMailcap(
        "text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
    mailcapCommandMap.addMailcap(
        "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    mailcapCommandMap.addMailcap(
        "multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
    mailcapCommandMap.addMailcap(
        "message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
    return mailcapCommandMap;
  }

  private static Map<String, String> eMailSetParameters(
      String from,
      String password,
      String emailTO,
      String emailCC,
      String emailBCC,
      String subject,
      String body,
      String attachment)
      throws Exception {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put(LABEL_FROM, from);
    parameters.put(LABEL_PASSWORD, password);
    parameters.put(LABEL_EMAIL_TO, emailTO);
    parameters.put(LABEL_EMAIL_CC, emailCC);
    parameters.put(LABEL_EMAIL_BC, emailBCC);
    parameters.put(LABEL_SUBJECT, subject);
    parameters.put(LABEL_BODY, body);
    parameters.put(LABEL_ATTACHMENT, attachment);
    from = from.toLowerCase(Locale.ENGLISH);
    parameters.put(LABEL_MAIL_DOMAIN, from.substring(from.indexOf('@') + 1));
    switch (parameters.get(LABEL_MAIL_DOMAIN)) {
      case "aol" + IExtension.COM:
        parameters.put(LABEL_HOST, EmailHost.AOL.getValue());
        parameters.put(LABEL_USERID, from);
        break;
      case "gmail" + IExtension.COM:
        parameters.put(LABEL_HOST, EmailHost.GMAIL.getValue());
        parameters.put(LABEL_USERID, from.substring(0, from.indexOf('@')));
        break;
      case "msn" + IExtension.COM:
        parameters.put(LABEL_HOST, EmailHost.MSN.getValue());
        parameters.put(LABEL_USERID, from);
        break;
      case "vivit-worldwide" + IExtension.ORG:
        parameters.put(LABEL_HOST, EmailHost.VIVIT.getValue());
        // This does not work with Office 365 as there may be
        // multiple
        // users with the same User Name at different companies.
        parameters.put(LABEL_USERID, from);
        break;
      case "smtp.wrberkley" + IExtension.COM:
        parameters.put(LABEL_HOST, EmailHost.BTS.getValue());
        parameters.put(LABEL_USERID, from);
        break;
      default:
        break;
    }
    return parameters;
  }

  private static Properties eMailSetProperties(Map<String, String> parameters) throws Exception {
    final Properties properties = System.getProperties();
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", parameters.get(LABEL_HOST));
    properties.put("mail.smtp.user", parameters.get(LABEL_FROM));
    properties.put("mail.smtp.password", parameters.get(LABEL_PASSWORD));
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth", "true");
    return properties;
  }

  private static void eMailSetRecipients(MimeMessage message, Map<String, String> parameters)
      throws Exception {
    for (int recipientIndex = 1; recipientIndex < 4; recipientIndex++) {
      String eMailAddresses = "";
      RecipientType recipientType = null;
      switch (recipientIndex) {
        case 1:
          eMailAddresses = parameters.get(LABEL_EMAIL_TO);
          recipientType = Message.RecipientType.TO;
          break;
        case 2:
          eMailAddresses = parameters.get(LABEL_EMAIL_CC);
          recipientType = Message.RecipientType.CC;
          break;
        case 3:
          recipientType = Message.RecipientType.BCC;
          eMailAddresses = parameters.get(LABEL_EMAIL_BC);
          break;
        default:
          break;
      }
      if (!eMailAddresses.isEmpty()) {
        final String[] eMailAddress = eMailAddresses.split(",");
        final InternetAddress[] addressTO = new InternetAddress[eMailAddress.length];
        for (int index = 0; index < eMailAddress.length; index++) {
          if (!eMailAddress[index].isEmpty()) {
            addressTO[index] = new InternetAddress(eMailAddress[index]);
          }
        }
        // Modified to use the recipientType variable.
        // message.addRecipients(Message.RecipientType.TO, addressTO)
        message.addRecipients(recipientType, addressTO);
      }
    }
  }

  public static boolean sendEmail(
      String from,
      String password,
      String emailTO,
      String emailCC,
      String emailBCC,
      String subject,
      String body,
      List<String> attachments)
      throws QAException {
    String attachment = "";
    if (!attachments.isEmpty()) {
      attachment = attachments.toString();
      attachment = attachment.substring(1, attachment.length() - 1);
      attachment = attachment.replace(", ", Constants.DELIMETER_LIST);
    }
    return sendEmail(from, password, emailTO, emailCC, emailBCC, subject, body, attachment);
  }

  public static boolean sendEmail(
      String from,
      String password,
      String emailTO,
      String emailCC,
      String emailBCC,
      String subject,
      String body,
      String attachment)
      throws QAException {
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      attempt++;
      try {
        // add handlers for main MIME types
        final MailcapCommandMap mailcapCommandMap = addMailcapCommandMap();
        CommandMap.setDefaultCommandMap(mailcapCommandMap);
        Map<String, String> parameters;
        parameters =
            eMailSetParameters(
                from, password, emailTO, emailCC, emailBCC, subject, body, attachment);
        parameters.put("sessionDebug", SESSION_DEBUG);
        Map<String, String> parametersTemp = new HashMap<>();
        parametersTemp.putAll(parameters);
        parametersTemp.put(LABEL_PASSWORD, "********");
        LOG.debug("parameters: [{}]", parameters);
        final Properties properties = eMailSetProperties(parameters);
        final Session session = Session.getDefaultInstance(properties);
        // Set Session debug for debugging of course.
        session.setDebug(Boolean.valueOf(parameters.get("sessionDebug")));
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(parameters.get(LABEL_USERID)));
        eMailSetRecipients(message, parameters);
        message.setSubject(parameters.get(LABEL_SUBJECT));
        message.setSentDate(new Date());
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        // messageBodyPart.setText(parameters.get(LABEL_BODY))
        messageBodyPart.setText(
            parameters.get(LABEL_BODY), StandardCharsets.UTF_8.toString(), "html");
        final Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        if (!attachment.isEmpty()) {
          // Part two is attachment
          // No delimited list passed in.
          if (attachment.indexOf(Constants.DELIMETER_LIST) == -1) {
            messageBodyPart = new MimeBodyPart();
            final DataSource source = new FileDataSource(attachment);
            messageBodyPart.setDataHandler(new DataHandler(source));
            String resultsFileName = attachment;
            final int index = attachment.lastIndexOf(Constants.DELIMETER_PATH);
            if (index > 0) {
              resultsFileName = attachment.substring(index + 1);
            }
            messageBodyPart.setFileName(resultsFileName);
            multipart.addBodyPart(messageBodyPart);
          } else {
            final List<String> attachments =
                Arrays.asList(attachment.split(Constants.DELIMETER_LIST));
            for (final String pathFileAttachment : attachments) {
              messageBodyPart = new MimeBodyPart();
              final DataSource source = new FileDataSource(pathFileAttachment);
              messageBodyPart.setDataHandler(new DataHandler(source));
              String resultsFileName = pathFileAttachment;
              final int index = pathFileAttachment.lastIndexOf(Constants.DELIMETER_PATH);
              if (index > 0) {
                resultsFileName = pathFileAttachment.substring(index + 1);
              }
              messageBodyPart.setFileName(resultsFileName);
              multipart.addBodyPart(messageBodyPart);
            }
          }
        }
        // Put parts in message
        message.setContent(multipart);
        // Send the message
        try (Transport transport = session.getTransport("smtp")) {
          transport.connect(parameters.get(LABEL_HOST), from, password);
          transport.sendMessage(message, message.getAllRecipients());
        }
        success = true;
      } catch (final Exception e) {
        LOG.error(
            "{} {}: Error Sending Email.",
            QAException.ERROR,
            JavaHelpers.getCurrentClassMethodName());
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    LOG.info(
        "Successfully sent email From ["
            + from
            + "], To:["
            + emailTO
            + "] CC:["
            + emailCC
            + "], BC:["
            + emailBCC
            + "], Subject:["
            + subject
            + "]");
    return success;
  }
}
