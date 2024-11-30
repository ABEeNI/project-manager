package home.projectmanager.repository;

import home.projectmanager.entity.WorkItemComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkItemCommentRepository extends JpaRepository<WorkItemComment, Long> {
}
