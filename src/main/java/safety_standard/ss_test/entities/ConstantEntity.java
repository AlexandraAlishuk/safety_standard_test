package safety_standard.ss_test.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name="constants")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConstantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "value")
    String value;

    public ConstantEntity() {
    }

    public ConstantEntity(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
