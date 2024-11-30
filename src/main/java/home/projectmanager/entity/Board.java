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
public class Board implements ProjectObject {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String boardName;


    @JoinColumn(name = "project_id", updatable = false)
    private Long projectId;

    @EqualsAndHashCode.Exclude //should I remove it or just pass it to the Project somehow?
    @OneToMany(mappedBy = "boardId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkItem> workItems = new ArrayList<>();

    @Override
    public Long getParentProjectId() {
        return projectId;
    }
}
