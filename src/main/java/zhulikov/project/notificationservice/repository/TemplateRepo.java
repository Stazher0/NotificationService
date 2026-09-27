package zhulikov.project.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhulikov.project.notificationservice.entity.Template;

public interface TemplateRepo extends JpaRepository<Template,Long> {
    boolean existsByName(String name);
}
