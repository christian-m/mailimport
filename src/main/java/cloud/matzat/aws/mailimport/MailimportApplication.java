package cloud.matzat.aws.mailimport;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot Main Class.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@SuppressWarnings({ "checkstyle:FinalClass", "checkstyle:HideUtilityClassConstructor" })
@SpringBootApplication
@EnableJpaRepositories(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = { EnableScan.class })
})
public class MailimportApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailimportApplication.class, args);
    }

}
