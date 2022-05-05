package cloud.matzat.aws.mailimport.dynamodb.domain;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data Repository for {@link SqsMessageLock}.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@EnableScan
public interface SqsMessageLockRepository extends CrudRepository<SqsMessageLock, String> {

}
