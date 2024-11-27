package home.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "parent_work_item_id")
    private WorkItem parentWorkItem;

    @OneToMany(mappedBy = "parentWorkItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkItem> subWorkItems = new ArrayList<>();

    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkItemComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "workItem")
    private List<BugItem> bugItems = new ArrayList<>();
}
