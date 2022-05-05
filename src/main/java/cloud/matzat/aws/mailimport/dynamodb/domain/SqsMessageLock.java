package cloud.matzat.aws.mailimport.dynamodb.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import cloud.matzat.aws.mailimport.dynamodb.converter.LocalDateTimeConverter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * SqsMessageLock Entity.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
@DynamoDBTable(tableName = "sqs_message_locks")
public class SqsMessageLock {

    @NonNull
    @DynamoDBHashKey(attributeName = "LockID")
    private String lockId;
    @DynamoDBAttribute(attributeName = "LockDate")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime lockedAt;

    public SqsMessageLock(@NonNull String lockId) {
        this.lockId = lockId;
        this.lockedAt = LocalDateTime.now();
    }

}
