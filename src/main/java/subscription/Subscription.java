package subscription;

import lombok.Builder;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;

@Entity
@Table(name = "subscription")
class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "repository_url", nullable = false)
    private String repositoryUrl;

    @Builder
    private Subscription(String email, String repositoryUrl) {
        Validate.notBlank(email);
        Validate.notBlank(repositoryUrl);

        this.email = email;
        this.repositoryUrl = repositoryUrl;
    }

}
