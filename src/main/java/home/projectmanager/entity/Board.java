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


    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Long projectId;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "board")
    private List<WorkItem> workItems = new ArrayList<>();


    public void addWorkItem(WorkItem workItem) {
        workItems.add(workItem);
        workItem.setBoard(this);
    }

    public void removeWorkItem(WorkItem workItem) {
        workItems.remove(workItem);
        workItem.setBoard(null);
    }

    @Override
    public Long getParentProjectId() {
        return projectId;
    }
}
