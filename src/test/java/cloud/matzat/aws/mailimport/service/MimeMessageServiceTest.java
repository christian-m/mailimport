package cloud.matzat.aws.mailimport.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cloud.matzat.aws.mailimport.test.utils.TestUtils;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unittest for {@link MimeMessageService}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SpringBootTest(classes = { MimeMessageService.class })
@ActiveProfiles("test")
public class MimeMessageServiceTest {

    @Autowired
    private MimeMessageService mimeMessageService;

    private String rawMimeMessage;

    @BeforeEach
    public void setup() throws IOException {
        rawMimeMessage = TestUtils.readFile("cloud/matzat/aws/mailimport/service/mimeMessage.txt");
    }

    @Test
    public void contextLoads() {
        assertThat(mimeMessageService).isNotNull();
    }

    @Test
    public void parseMimeMessageTest() throws MessagingException {
        assertDoesNotThrow(() -> {
            mimeMessageService.parseMimeMessage(rawMimeMessage);
        });
        final MimeMessage mimeMessage = mimeMessageService.parseMimeMessage(rawMimeMessage);
        assertThat(mimeMessage.getMessageID()).isNotEmpty()
                                              .isEqualTo(
                                                  "<01020177a16827a6-ef3399ae-324a-448d-9bba-e83a94a35e4a-000000@eu-west-1.amazonses.com>");
        assertThat(mimeMessage.getFrom()).isNotEmpty().hasSize(1);
    }
}
