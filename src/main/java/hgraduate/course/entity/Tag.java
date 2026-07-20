package hgraduate.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name", nullable = false, unique = true)
    private String tagName;

    @Column(name = "description")
    private String description;
}
