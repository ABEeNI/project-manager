package home.projectmanager.repository;

import home.projectmanager.entity.BugItemComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugItemCommentRepository extends JpaRepository<BugItemComment, Long> {
}
