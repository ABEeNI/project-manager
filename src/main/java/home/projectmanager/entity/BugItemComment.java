package home.projectmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class BugItemComment {

    @Id
    private Long id;

    private String comment;

    @ManyToOne
    private BugItem bugItem;

    @ManyToOne
    private User user;
}
