SET NAMES latin1;

# Creer une base "subtranslator"
# Entrer "use subtranslator"
# Puis :

DROP TABLE IF EXISTS soustitres;

CREATE TABLE soustitres (
    id int(5) NOT NULL,
    duree varchar(100) NOT NULL,
    original varchar(200) NOT NULL,
    traduction varchar(200) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
