package cloud.matzat.aws.mailimport.dynamodb.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;

/**
 * DynamoDB Type Converter for LocalDateTime.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

    @Override
    public String convert(final LocalDateTime time) {
        return time.toString();
    }

    @Override
    public LocalDateTime unconvert(final String stringValue) {
        return LocalDateTime.parse(stringValue);
    }
}
