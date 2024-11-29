package home.projectmanager.repository;

import home.projectmanager.entity.BugItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugItemRepository extends JpaRepository<BugItem, Long> {
}
