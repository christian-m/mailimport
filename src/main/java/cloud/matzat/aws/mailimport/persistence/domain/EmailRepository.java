package cloud.matzat.aws.mailimport.persistence.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link Email} entity.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
public interface EmailRepository extends JpaRepository<Email, Long> {

    Optional<Email> findById(Long id);

}
