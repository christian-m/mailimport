package cloud.matzat.aws.mailimport.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * Wrapper Object for Mailcontent of the SQS-Message.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Mail {

    private Boolean headersTruncated;
    private String messageId;
    private List<Header> headers;
    private Instant timestamp;
    private String source;
    private List<String> destination;

    /**
     * Wrapper Object for Mailheader.
     *
     * @author Christian Matzat (christian@matzat.net)
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @ToString
    @EqualsAndHashCode
    public static class Header {

        private String name;
        private String value;
    }

    /**
     * Wrapper Object for Common-Mailheader.
     *
     * @author Christian Matzat (christian@matzat.net)
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @ToString
    @EqualsAndHashCode
    public static class CommonHeader {

        private String subject;
        private String messageId;
        private List<String> to;
        private String returnPath;
        private String date;
        private List<String> from;
    }
}
