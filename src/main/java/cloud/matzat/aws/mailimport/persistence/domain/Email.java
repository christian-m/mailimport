package cloud.matzat.aws.mailimport.persistence.domain;

import org.hibernate.Hibernate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity Class for an email.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@Entity
@Table(name = "EMAILS")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAILS_SEQ")
    @SequenceGenerator(sequenceName = "EMAILS_SEQ", allocationSize = 1, name = "EMAILS_SEQ")
    @Column
    private Long id;
    @Column
    private String guid;
    @Column
    private String fromEMail;
    @Column
    private String fromName;
    @Column
    private String mailTo;
    @Column
    private String cc;
    @Column
    private String bcc;
    @Column
    private String subject;
    @Column
    private String normalizedSubject;
    @Column
    private Boolean htmlMail;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime modifiedAt;
    @Column
    private String contentMediaType;
    @Column
    private LocalDateTime dateReceived;
    @Lob
    @Column
    private String htmlContent;
    @Lob
    @Column
    private String textContent;
    @Column
    private String messageId;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modifiedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Email email = (Email) o;
        return id != null && Objects.equals(id, email.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
