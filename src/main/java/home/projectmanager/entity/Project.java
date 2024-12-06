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
public class Project {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String projectName;

    private String projectDescription;

    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "project_team",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();

    //@EqualsAndHashCode.Exclude ?? Where to put it
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BugItem> bugItems = new ArrayList<>();

    public void addTeam(Team team) {
        teams.add(team);
        team.getProjects().add(this);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        team.getProjects().remove(this);
    }
}
