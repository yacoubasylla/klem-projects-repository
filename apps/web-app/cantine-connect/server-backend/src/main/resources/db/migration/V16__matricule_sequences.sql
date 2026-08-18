-- Compteur de séquence par année pour la génération automatique du matricule élève
-- (format E<ANNEE><RANG sur 4 chiffres>, ex. E20260001) — voir MatriculeGenerator.
CREATE TABLE matricule_sequences (
    annee         INTEGER PRIMARY KEY,
    dernier_rang  INTEGER NOT NULL DEFAULT 0
);
