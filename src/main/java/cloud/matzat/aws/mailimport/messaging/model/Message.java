package cloud.matzat.aws.mailimport.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Wrapper Object for the SQS-Message.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Message {

    private String notificationType;
    private Mail mail;
    private Receipt receipt;
}
