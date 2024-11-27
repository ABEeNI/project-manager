package home.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.Array2DHashSet;

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
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String teamName;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "teams")
    private List<User> users = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "teams")
    private List<Project> projects = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        user.getTeams().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getTeams().remove(this);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.getTeams().add(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.getTeams().remove(this);
    }
}
