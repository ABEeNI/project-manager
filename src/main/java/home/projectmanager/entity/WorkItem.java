package home.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class WorkItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Integer points;

    @Enumerated(EnumType.STRING)
    private WorkItemStatus status;

    @ManyToOne
    private User assignedUser;

    @ManyToOne
    private Board board;

    @ManyToOne
    private WorkItem parentWorkItem;

    @OneToMany(mappedBy = "parentWorkItem")
    private Set<WorkItem> subWorkItems;

    @OneToMany(mappedBy = "workItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkItemComment> comments;

    @OneToMany(mappedBy = "workItem", fetch = FetchType.LAZY)
    private Set<BugItem> bugItems;
}
