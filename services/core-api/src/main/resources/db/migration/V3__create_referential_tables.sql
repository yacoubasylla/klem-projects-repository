CREATE TABLE referential_list (
    id         UUID PRIMARY KEY,
    code       VARCHAR(50) NOT NULL,
    label      VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX ux_referential_list_code ON referential_list (code);

CREATE TABLE referential_entry (
    id         UUID PRIMARY KEY,
    list_id    UUID NOT NULL REFERENCES referential_list (id),
    entry_code VARCHAR(50) NOT NULL,
    label      VARCHAR(200) NOT NULL,
    sort_order INT NOT NULL
);

CREATE UNIQUE INDEX ux_referential_entry_list_code ON referential_entry (list_id, entry_code);

-- Seed minimal, cohérent avec le périmètre réel du portefeuille (voir knowledge/02-product-portfolio.md) :
-- pays des corridors Hinterland-Track (Abidjan-Bamako, Abidjan-Ouagadougou) et devise BCEAO.
-- Pas d'endpoint d'écriture dans ce Sprint (README.md §5) — seed par migration, à faire évoluer
-- vers un endpoint d'administration si un besoin réel de mise à jour dynamique apparaît.
INSERT INTO referential_list (id, code, label, created_at) VALUES
    ('a3f1c1e0-0000-4000-8000-000000000001', 'countries', 'Pays', now()),
    ('a3f1c1e0-0000-4000-8000-000000000002', 'currencies', 'Devises', now());

INSERT INTO referential_entry (id, list_id, entry_code, label, sort_order) VALUES
    ('a3f1c1e0-0000-4000-8000-000000000011', 'a3f1c1e0-0000-4000-8000-000000000001', 'CI', 'Côte d''Ivoire', 1),
    ('a3f1c1e0-0000-4000-8000-000000000012', 'a3f1c1e0-0000-4000-8000-000000000001', 'ML', 'Mali', 2),
    ('a3f1c1e0-0000-4000-8000-000000000013', 'a3f1c1e0-0000-4000-8000-000000000001', 'BF', 'Burkina Faso', 3),
    ('a3f1c1e0-0000-4000-8000-000000000021', 'a3f1c1e0-0000-4000-8000-000000000002', 'XOF', 'Franc CFA (BCEAO)', 1);
