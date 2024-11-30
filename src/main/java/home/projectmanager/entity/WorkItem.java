package home.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class WorkItem implements ProjectObject {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String title;

    private String description;

    private Integer points;

    @Enumerated(EnumType.STRING)
    private WorkItemStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @JoinColumn(name = "board_id")
    private Long boardId;

    private Long projectId; //this is for Access control

    @ManyToOne
    @JoinColumn(name = "parent_work_item_id")
    private WorkItem parentWorkItem;

    @OneToMany(mappedBy = "parentWorkItem", cascade = CascadeType.ALL)
    private List<WorkItem> subWorkItems = new ArrayList<>();

    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkItemComment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = false)
    private BugItem bugItem;

    @Override
    public Long getParentProjectId() {
        return projectId;
    }

    public void addBugItem(BugItem bugItem) {
        this.bugItem = bugItem;
        bugItem.setWorkItem(this);
    }
}
