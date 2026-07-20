package hgraduate.course.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseTagId implements Serializable {

    private Long courseId;
    private Long tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseTagId that)) return false;
        return Objects.equals(courseId, that.courseId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, tagId);
    }
}
