CREATE TABLE IF NOT EXISTS group_rule (
  id BIGSERIAL PRIMARY KEY,
  group_id BIGINT NOT NULL,
  rule_id BIGINT NOT NULL,

  CONSTRAINT fk_group_rule_group
    FOREIGN KEY (group_id)
    REFERENCES "groups" (id)
    ON DELETE CASCADE,

  CONSTRAINT fk_group_rule_rule
    FOREIGN KEY (rule_id)
    REFERENCES "rules" (id)
    ON DELETE CASCADE,

  CONSTRAINT uk_group_rule_group_rule
    UNIQUE (group_id, rule_id)
);
