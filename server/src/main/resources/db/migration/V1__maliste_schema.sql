-- P0-02: Schema initial MaListe
-- This migration is additive and can be executed on an empty `MaListe` database.

CREATE TABLE IF NOT EXISTS Categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Produits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    categorie_id INT NOT NULL,
    CONSTRAINT fk_produit_categorie
        FOREIGN KEY (categorie_id) REFERENCES Categories(id)
);

CREATE TABLE IF NOT EXISTS Magasins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    enseigne VARCHAR(100),
    adresse TEXT
);

CREATE TABLE IF NOT EXISTS Configuration_Rayons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    magasin_id INT NOT NULL,
    categorie_id INT NOT NULL,
    position INT NOT NULL,
    CONSTRAINT fk_cfg_magasin
        FOREIGN KEY (magasin_id) REFERENCES Magasins(id),
    CONSTRAINT fk_cfg_categorie
        FOREIGN KEY (categorie_id) REFERENCES Categories(id),
    CONSTRAINT uq_cfg_magasin_categorie UNIQUE (magasin_id, categorie_id)
);

CREATE TABLE IF NOT EXISTS Listes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Listes_Produits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    liste_id INT NOT NULL,
    produit_id INT NOT NULL,
    quantite INT DEFAULT 1,
    est_achete BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_lp_liste
        FOREIGN KEY (liste_id) REFERENCES Listes(id),
    CONSTRAINT fk_lp_produit
        FOREIGN KEY (produit_id) REFERENCES Produits(id)
);

-- Indexes for P0 reads/writes (`GET /api/listes/{listId}?magasinId=...` and PATCH item state)
CREATE INDEX idx_produits_categorie_id ON Produits(categorie_id);
CREATE INDEX idx_cfg_magasin_position ON Configuration_Rayons(magasin_id, position);
CREATE INDEX idx_lp_liste_id ON Listes_Produits(liste_id);
CREATE INDEX idx_lp_produit_id ON Listes_Produits(produit_id);
CREATE INDEX idx_lp_liste_achete ON Listes_Produits(liste_id, est_achete);

