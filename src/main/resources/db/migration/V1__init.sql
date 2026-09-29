-- Создание таблицы notifications
CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               notification_type VARCHAR(20) NOT NULL,
                               destination VARCHAR(255) NOT NULL,
                               theme VARCHAR(255),
                               content TEXT NOT NULL,
                               priority_type VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                               status_type VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                               template_id BIGINT,
                               retry_count INTEGER NOT NULL DEFAULT 0,
                               error_message VARCHAR(1000),
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               sent_at TIMESTAMP
);

-- Создание таблицы templates
CREATE TABLE templates (
                           template_id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           notification_type VARCHAR(20) NOT NULL,
                           theme VARCHAR(255),
                           content TEXT NOT NULL,
                           variable TEXT,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для быстрого поиска
CREATE INDEX idx_notifications_status_type ON notifications(status_type);
CREATE INDEX idx_notifications_destination ON notifications(destination);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);