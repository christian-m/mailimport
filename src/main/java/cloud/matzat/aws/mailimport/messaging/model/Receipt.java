package cloud.matzat.aws.mailimport.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * Wrapper Object for Mail-Metadata of the SQS-Message.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Receipt {

    private List<String> recipients;
    private Instant timestamp;
    private Long processingTimeMillis;
    private Action action;
    private Verdict spamVerdict;
    private Verdict dkimVerdict;
    private Verdict spfVerdict;
    private Verdict dmarcVerdict;
    private Verdict virusVerdict;

    /**
     * Wrapper Object for S3-Data for this SQS-Message.
     *
     * @author Christian Matzat (christian@matzat.net)
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @ToString
    @EqualsAndHashCode
    public static class Action {

        private String objectKey;
        private String bucketName;
        private String topicArn;
        private String type;
    }

    /**
     * Wrapper Object for Verdicts.
     *
     * @author Christian Matzat (christian@matzat.net)
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @ToString
    @EqualsAndHashCode
    public static class Verdict {

        private String status;
    }
}
