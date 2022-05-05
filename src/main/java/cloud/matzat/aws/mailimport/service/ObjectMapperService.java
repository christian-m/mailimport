package cloud.matzat.aws.mailimport.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * ObjectMapper configuration to handle timestamps from JSON.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Service
public class ObjectMapperService {

    @Bean
    public ObjectMapper getObjectMapper() {
        final JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .registerModule(module);
    }

}
