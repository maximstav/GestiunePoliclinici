create table cheltuieli_lunare
(
    id_cheltuiala     int auto_increment
        primary key,
    luna              int not null,
    an                int not null,
    cheltuiala_totala int not null,
    constraint cheltuieli_lunare_unic
        unique (luna, an)
);

create table client
(
    id_client     int auto_increment
        primary key,
    nume          varchar(45) null,
    prenume       varchar(45) null,
    CNP           char(45)    null,
    data_nasterii date        null,
    nr_telefon    varchar(45) null
);

create table serviciu
(
    id_serviciu int auto_increment
        primary key,
    denumire    varchar(255)   not null,
    pret        int            null,
    durata      int default 10 null,
    constraint denumire
        unique (denumire)
);

create table unitate
(
    id_unitate int auto_increment
        primary key,
    nume       varchar(45)  not null,
    adresa     varchar(255) null
);

create table unitate_serviciu
(
    id_unitate  int not null,
    id_serviciu int not null,
    primary key (id_unitate, id_serviciu),
    constraint unitate_serviciu_ibfk_1
        foreign key (id_unitate) references unitate (id_unitate)
            on delete cascade,
    constraint unitate_serviciu_ibfk_2
        foreign key (id_serviciu) references serviciu (id_serviciu)
            on delete cascade
);

create index id_serviciu
    on unitate_serviciu (id_serviciu);

create table utilizator
(
    id             int auto_increment
        primary key,
    CNP            varchar(45)                                              null,
    loginID        varchar(45)                                              not null,
    parola         varchar(45)                                              not null,
    nume           varchar(45)                                              null,
    prenume        varchar(45)                                              null,
    adresa         varchar(45)                                              null,
    nrtel          varchar(45)                                              null,
    email          varchar(45)                                              null,
    IBAN           varchar(45)                                              null,
    nrcontact      varchar(45)                                              null,
    data_angajarii date                                                     null,
    functie        enum ('Administrator', 'Super-Administrator', 'Angajat') not null,
    constraint id_UNIQUE
        unique (id),
    constraint loginID
        unique (loginID)
)
    charset = utf8mb3;

create table angajat
(
    id             int                                           not null
        primary key,
    salariu_pe_ora int                                           null,
    nr_ore         int                                           null,
    tip_angajat    enum ('resurse-umane', 'economic', 'medical') null,
    constraint fk_angajat_utilizator
        foreign key (id) references utilizator (id)
            on delete cascade
);

create table concediu
(
    id_concediu  int auto_increment
        primary key,
    id_angajat   int          not null,
    data_inceput date         not null,
    data_sfarsit date         not null,
    motiv        varchar(255) null,
    constraint concediu_ibfk_1
        foreign key (id_angajat) references angajat (id)
            on delete cascade,
    constraint chk_data_validare
        check (`data_inceput` <= `data_sfarsit`)
);

create index id_angajat
    on concediu (id_angajat);

create table medical
(
    id          int                                        not null
        primary key,
    tip_medical enum ('receptionist', 'asistent', 'medic') null,
    constraint fk_medical_angajat
        foreign key (id) references angajat (id)
            on delete cascade
);

create table asistent
(
    id           int                                            not null
        primary key,
    tip_asistent enum ('generalist', 'laborator', 'radiologic') null,
    grad         enum ('secundar', 'principal')                 null,
    constraint fk_asistent_medical
        foreign key (id) references medical (id)
            on delete cascade
);

create table medic
(
    id               int                                              not null
        primary key,
    grad             enum ('specialist', 'primar')                    null,
    cod_parafa       varchar(45)                                      null,
    titlu_stiintific enum ('doctorand', 'doctor_in_stiinte_medicale') null,
    post_didactic    enum ('profesor', 'conferentiar')                null,
    procent_servicii int default 0                                    null,
    constraint cod_parafa
        unique (cod_parafa),
    constraint fk_medic_medical
        foreign key (id) references medical (id)
            on delete cascade
);

create table competente
(
    id_medical int         null,
    competenta varchar(45) null,
    constraint fk_competenta_medic
        foreign key (id_medical) references medic (id)
            on delete cascade
);

create table consultatie
(
    id_consultatie   int auto_increment
        primary key,
    id_client        int           not null,
    id_medic         int           not null,
    data_consultatie date          null,
    durata_totala    int default 0 null,
    constraint consultatie_ibfk_1
        foreign key (id_client) references client (id_client)
            on delete cascade,
    constraint consultatie_ibfk_2
        foreign key (id_medic) references medic (id)
            on delete cascade
);

create index id_client
    on consultatie (id_client);

create index id_medic
    on consultatie (id_medic);

create table consultatie_servicii
(
    id_consultatie int not null,
    id_serviciu    int not null,
    primary key (id_consultatie, id_serviciu),
    constraint consultatie_servicii_ibfk_1
        foreign key (id_consultatie) references consultatie (id_consultatie)
            on delete cascade,
    constraint consultatie_servicii_ibfk_2
        foreign key (id_serviciu) references serviciu (id_serviciu)
            on delete cascade
);

create index id_serviciu
    on consultatie_servicii (id_serviciu);

create definer = root@localhost trigger trg_update_durata_delete
    after delete
    on consultatie_servicii
    for each row
BEGIN
    UPDATE consultatie
    SET durata_totala = (
        SELECT COALESCE(SUM(s.durata), 0)
        FROM consultatie_servicii cs
        JOIN serviciu s ON cs.id_serviciu = s.id_serviciu
        WHERE cs.id_consultatie = OLD.id_consultatie
    )
    WHERE id_consultatie = OLD.id_consultatie;
END;

create definer = root@localhost trigger trg_update_durata_insert
    after insert
    on consultatie_servicii
    for each row
BEGIN
    UPDATE consultatie
    SET durata_totala = (
        SELECT COALESCE(SUM(s.durata), 0)
        FROM consultatie_servicii cs
        JOIN serviciu s ON cs.id_serviciu = s.id_serviciu
        WHERE cs.id_consultatie = NEW.id_consultatie
    )
    WHERE id_consultatie = NEW.id_consultatie;
END;

create table orar_generic
(
    id_orar_generic int auto_increment
        primary key,
    id_angajat      int                                                                        not null,
    zi_saptamana    enum ('Luni', 'Marti', 'Miercuri', 'Joi', 'Vineri', 'Sambata', 'Duminica') not null,
    ora_inceput     time                                                                       not null,
    ora_sfarsit     time                                                                       not null,
    constraint orar_generic_ibfk_1
        foreign key (id_angajat) references angajat (id)
            on delete cascade
);

create index id_angajat
    on orar_generic (id_angajat);

create table orar_specific
(
    id_orar_specific    int auto_increment
        primary key,
    id_angajat          int  not null,
    data_calendaristica date not null,
    ora_inceput         time not null,
    ora_sfarsit         time not null,
    constraint id_angajat
        unique (id_angajat, data_calendaristica),
    constraint orar_specific_ibfk_1
        foreign key (id_angajat) references angajat (id)
            on delete cascade
);

create table raport
(
    id_raport             int auto_increment
        primary key,
    id_consultatie        int          not null,
    id_pacient            int          not null,
    id_medic              int          not null,
    nume_analiza          varchar(255) null,
    rezultat_analiza      varchar(255) null,
    id_medic_recomandator int          null,
    id_asistent           int          null,
    istoric               varchar(255) null,
    simptome              varchar(255) null,
    investigatii          varchar(255) null,
    diagnostic            varchar(255) null,
    recomandari           varchar(255) null,
    constraint fk_raport_asistent
        foreign key (id_asistent) references asistent (id)
            on delete set null,
    constraint fk_raport_consultatie
        foreign key (id_consultatie) references consultatie (id_consultatie)
            on delete cascade,
    constraint fk_raport_medic
        foreign key (id_medic) references medic (id)
            on delete cascade,
    constraint fk_raport_medic_recomandator
        foreign key (id_medic_recomandator) references medic (id)
            on delete set null,
    constraint fk_raport_pacient
        foreign key (id_pacient) references client (id_client)
            on delete cascade
);

create index idx_id_consultatie
    on raport (id_consultatie);

create index idx_id_medic
    on raport (id_medic);

create index idx_id_pacient
    on raport (id_pacient);

create table salariu
(
    id_salariu         int auto_increment
        primary key,
    id_angajat         int           not null,
    luna               int           not null,
    an                 int           not null,
    salariu_total      int           not null,
    bonus_din_servicii int default 0 null,
    nr_ore_lucrate     int           not null,
    salariu_din_ore    int           null,
    constraint fk_salarii_angajat
        foreign key (id_angajat) references angajat (id)
            on delete cascade
);

create table specialitati
(
    id_medical   int         null,
    specialitate varchar(45) null,
    constraint fk_specialitate_medic
        foreign key (id_medical) references medic (id)
            on delete cascade
);

create table venituri_lunare
(
    id_venit    int auto_increment
        primary key,
    luna        int not null,
    an          int not null,
    venit_total int not null,
    constraint venituri_lunare_unic
        unique (luna, an)
);

create
    definer = root@localhost function esteAdministrator(userLoginID varchar(45)) returns tinyint(1) deterministic
BEGIN
    DECLARE isAdmin BOOLEAN;
    SELECT COUNT(*) > 0 INTO isAdmin FROM utilizator
    WHERE loginID = userLoginID AND functie = 'Administrator';
    RETURN isAdmin;
END;

create
    definer = root@localhost function esteAngajat(userLoginID varchar(45)) returns tinyint(1) deterministic
BEGIN
    DECLARE isAngajat BOOLEAN;
    SELECT COUNT(*) > 0 INTO isAngajat FROM utilizator
    WHERE loginID = userLoginID AND functie = 'Angajat';
    RETURN isAngajat;
END;

create
    definer = root@localhost function esteSuperAdministrator(userLoginID varchar(45)) returns tinyint(1) deterministic
BEGIN
    DECLARE isSuperAdmin BOOLEAN;
    SELECT COUNT(*) > 0 INTO isSuperAdmin FROM utilizator
    WHERE loginID = userLoginID AND functie = 'Super-Administrator';
    RETURN isSuperAdmin;
END;


