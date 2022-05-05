package cloud.matzat.aws.mailimport.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Service to parse a String to get a MimeMessage.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Service
public class MimeMessageService {

    public MimeMessage parseMimeMessage(String messageContent) throws MessagingException {
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props);
        return new MimeMessage(session, new ByteArrayInputStream(messageContent.getBytes()));
    }
}
