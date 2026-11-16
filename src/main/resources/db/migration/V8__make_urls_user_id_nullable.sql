-- Permite criar URLs sem usuário logado (URLs anônimas)
ALTER TABLE urls ALTER COLUMN user_id DROP NOT NULL;
