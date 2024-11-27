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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String boardName;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

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


}
