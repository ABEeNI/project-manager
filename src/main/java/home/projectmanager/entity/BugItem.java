package home.projectmanager.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

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
    private Long id;

    private String title;

    private String description;

    @ManyToOne
    private User reporter;

    @ManyToOne
    private WorkItem workItem;

    @OneToMany
    private Set<BugItemComment> comments;
}
