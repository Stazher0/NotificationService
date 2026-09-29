package zhulikov.project.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import zhulikov.project.notificationservice.enums.NotificationType;

import java.util.Date;

@Entity
@Data
@Table(name = "templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;

    @Column(unique = true,nullable = false)
    private String name; //уникальное название

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType; //для какого типа уведомлени шаблон

    private String theme; //тема письма --!!--

    @Column(nullable = false)
    private String content; //текст уведомления

    private String variable; //JSON с подставляемыми переменными

    @Column(nullable = false,updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }
}
