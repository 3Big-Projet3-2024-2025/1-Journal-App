CREATE TABLE Role(
                     Id_Role int,
                     nomDuRole VARCHAR(20),
                     PRIMARY KEY(Id_Role)
);

CREATE TABLE Utilisateur(
                            Id_Utilisateur int,
                            nom VARCHAR(50),
                            prenom VARCHAR(50),
                            date_de_naissance DATE,
                            email VARCHAR(60),
                            motDePasse VARCHAR(250),
                            nvx_motDePasse VARCHAR(250),
                            longitude DECIMAL(10,7),
                            latitude DECIMAL(10,7),
                            IsAuthorized BOOLEAN,
                            IsRoleChange BOOLEAN,
                            Id_Role INT NOT NULL,
                            PRIMARY KEY(Id_Utilisateur),
                            UNIQUE(email),
                            FOREIGN KEY(Id_Role) REFERENCES Role(Id_Role)
);

CREATE TABLE newsletter(
                           Id_newsletter int,
                           titre VARCHAR(100),
                           sousTitre VARCHAR(150),
                           Contenu VARCHAR(500),
                           dateDePublication DATE,
                           longitude DECIMAL(10,7),
                           latitude DECIMAL(10,7),
                           IsValid BOOLEAN,
                           estLue BOOLEAN,
                           Id_Utilisateur INT NOT NULL,
                           PRIMARY KEY(Id_newsletter),
                           FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur)
);

CREATE TABLE commentaire(
                            Id_commentaire int,
                            contenue VARCHAR(350),
                            dateDePublication DATETIME,
                            Id_newsletter INT NOT NULL,
                            Id_Utilisateur INT NOT NULL,
                            PRIMARY KEY(Id_commentaire),
                            FOREIGN KEY(Id_newsletter) REFERENCES newsletter(Id_newsletter),
                            FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur)
);

CREATE TABLE image(
                      Id_image int,
                      image VARCHAR(300),
                      Id_newsletter INT NOT NULL,
                      PRIMARY KEY(Id_image),
                      FOREIGN KEY(Id_newsletter) REFERENCES newsletter(Id_newsletter)
);