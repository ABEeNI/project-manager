package home.projectmanager.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class BugItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private BugItemStatus status;

    @ManyToOne
    private User reporter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_item_id")
    private WorkItem workItem;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "bugItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BugItemComment> comments = new ArrayList<>();
}
