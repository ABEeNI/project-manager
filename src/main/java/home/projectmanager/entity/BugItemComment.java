package home.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class BugItemComment implements ProjectObject {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_item_id")
    private BugItem bugItem;

    private Long projectId;

    @Override
    public Long getParentProjectId() {
        return projectId;
    }
}
