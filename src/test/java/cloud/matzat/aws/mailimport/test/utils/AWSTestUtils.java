package cloud.matzat.aws.mailimport.test.utils;

import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class to build AWS test data.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
public final class AWSTestUtils {

    private static final GetQueueAttributesResult QUEUE_ATTRIBUTES_RESULT = new GetQueueAttributesResult();
    private static final List<String> queueAttributeNames = new ArrayList<>();

    static {
        final Map<String, String> queueAttributes = new HashMap<>();
        queueAttributes.put(QueueAttributeName.ApproximateNumberOfMessages.name(), "15");
        queueAttributes.put(QueueAttributeName.ApproximateNumberOfMessagesDelayed.name(), "20");
        queueAttributes.put(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.name(), "30");
        QUEUE_ATTRIBUTES_RESULT.setAttributes(queueAttributes);

        Collections.addAll(
            queueAttributeNames,
            QueueAttributeName.ApproximateNumberOfMessages.name(),
            QueueAttributeName.ApproximateNumberOfMessagesDelayed.name(),
            QueueAttributeName.ApproximateNumberOfMessagesNotVisible.name()
        );
    }

    private AWSTestUtils() {
    }

    public static GetQueueAttributesResult getQueueAttributesResult() {
        return QUEUE_ATTRIBUTES_RESULT;
    }

    public static List<String> getQueueAttributeNames() {
        return queueAttributeNames;
    }
}
