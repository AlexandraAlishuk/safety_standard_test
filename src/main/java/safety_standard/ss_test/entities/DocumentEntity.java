package safety_standard.ss_test.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name="documents")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "doc_name")
    String docName;

    @Column(name = "date_create")
    Instant dateCreate;

    @Column(name = "date_add_to_db")
    Instant dateAddToDB;

    @Column(name = "date_delete")
    Instant dateDelete;

    @Column(name = "category")
    String category;

    @Column(name = "description")
    String description;

    @Column(name = "data_article_id")
    Long dataArticleId;

    public DocumentEntity() {
    }

    public DocumentEntity(String docName, Instant dateCreate, Instant dateAddToDB, Instant dateDelete, String category, String description, Long dataArticleId) {
        this.docName = docName;
        this.dateCreate = dateCreate;
        this.dateAddToDB = dateAddToDB;
        this.dateDelete = dateDelete;
        this.category = category;
        this.description = description;
        this.dataArticleId = dataArticleId;
    }
}
