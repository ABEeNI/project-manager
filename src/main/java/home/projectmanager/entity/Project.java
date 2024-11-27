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

    @ManyToMany
    @JoinTable(
            name = "project_team",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();


    public void addTeam(Team team) {
        teams.add(team);
        team.getProjects().add(this);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        team.getProjects().remove(this);
    }

    public void addBoard(Board board) {
        boards.add(board);
        board.setProject(this);
    }

    public void removeBoard(Board board) {
        boards.remove(board);
        board.setProject(null);
    }
}
