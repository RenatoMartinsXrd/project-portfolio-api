CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    expected_end_date DATE NOT NULL,
    actual_end_date DATE,
    total_budget NUMERIC(19,2) NOT NULL,
    description VARCHAR(1000),
    manager_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE project_members (
    project_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_project_members_project
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);
