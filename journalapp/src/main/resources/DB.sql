CREATE TABLE Role(
                     Role_Id  INT AUTO_INCREMENT,
                     Role_Name VARCHAR(20),
                     PRIMARY KEY(Role_Id)
);

CREATE TABLE User(
                     User_Id  INT AUTO_INCREMENT,
                     Last_Name VARCHAR(50),
                     First_Name VARCHAR(50),
                     Date_Of_Birth DATE,
                     Email VARCHAR(60),
                     Password VARCHAR(250),
                     New_Password VARCHAR(250),
                     Longitude DECIMAL(10,7),
                     Latitude DECIMAL(10,7),
                     Is_Authorized BOOLEAN,
                     Is_Role_Change BOOLEAN,
                     Role_Id INT NOT NULL,
                     PRIMARY KEY(User_Id),
                     UNIQUE(Email),
                     FOREIGN KEY(Role_Id) REFERENCES Role(Role_Id)
);

CREATE TABLE Newsletter(
                           Newsletter_Id  INT AUTO_INCREMENT,
                           Title VARCHAR(100),
                           Subtitle VARCHAR(150),
                           Publication_Date DATE,
                           Is_Read BOOLEAN,
                           Created_By_User_Id INT NOT NULL,
                           Read_By_User_Id INT NOT NULL,
                           PRIMARY KEY(Newsletter_Id),
                           FOREIGN KEY(Created_By_User_Id) REFERENCES User(User_Id),
                           FOREIGN KEY(Read_By_User_Id) REFERENCES User(User_Id)
);

CREATE TABLE Comment(
                        Comment_Id  INT AUTO_INCREMENT,
                        Content VARCHAR(350),
                        Publication_Date DATETIME,
                        Newsletter_Id INT NOT NULL,
                        User_Id INT NOT NULL,
                        PRIMARY KEY(Comment_Id),
                        FOREIGN KEY(Newsletter_Id) REFERENCES Newsletter(Newsletter_Id),
                        FOREIGN KEY(User_Id) REFERENCES User(User_Id)
);

CREATE TABLE Article(
                        Article_Id  INT AUTO_INCREMENT,
                        Title VARCHAR(50) NOT NULL,
                        Content VARCHAR(50) NOT NULL,
                        Publication_Date DATE NOT NULL,
                        Longitude DOUBLE,
                        Latitude VARCHAR(50) NOT NULL,
                        Is_Valid BOOLEAN NOT NULL,
                        Newsletter_Id INT NOT NULL,
                        User_Id INT NOT NULL,
                        PRIMARY KEY(Article_Id),
                        FOREIGN KEY(Newsletter_Id) REFERENCES Newsletter(Newsletter_Id),
                        FOREIGN KEY(User_Id) REFERENCES User(User_Id)
);

CREATE TABLE Image(
                      Image_Id  INT AUTO_INCREMENT,
                      Image_Path VARCHAR(300),
                      Article_Id INT NOT NULL,
                      PRIMARY KEY(Image_Id),
                      FOREIGN KEY(Article_Id) REFERENCES Article(Article_Id)
)