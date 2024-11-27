package home.projectmanager.repository;

import home.projectmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectName(String name);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN p.teams t " +
            "JOIN t.users u " +
            "WHERE u.id = :userId")
    List<Project> findAllByUserId(@Param("userId") Long userId);

}
