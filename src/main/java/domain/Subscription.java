package domain;

import javax.persistence.*;

@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;



}
