package safety_standard.ss_test.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    String nameCategory;
    Long docCount;
    Long added;
    Long deleted;

    public Category(String nameCategory, Long docCount) {
        this.docCount = docCount;
        this.nameCategory = nameCategory;
    }
}
