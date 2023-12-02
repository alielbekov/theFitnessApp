-- This script creates all of the tables for the database.

create table Trainer (
    id integer primary key,
    name varchar(128),
    phone varchar(22) not null constraint TRAINER_PHONE_CHECK check ( regexp_like(phone, '^(\+\d\s?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$') )
);

-- Level was renamed to Levels to avoid oracle db error.
create table Levels (
    name varchar(7) not null constraint LEVEL_CHECK check ( name in ('Regular', 'Gold', 'Diamond') ) primary key,
    discount integer,
    minSpendingAmount numeric
);

create table Member (
    id integer not null primary key,
    name varchar(128) not null,
    phone varchar(22) not null constraint MEMBER_PHONE_CHECK check ( regexp_like(phone, '^(\+\d\s?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$') ),
    membershipLevel varchar(7) not null constraint MEMBERSHIP_CHECK check ( membershipLevel in ('Regular', 'Gold', 'Diamond') ),
    totalSpending numeric,
    foreign key (membershipLevel) references Levels(name)
);

create table Course (
    name varchar(128) primary key,
    trainerID integer,
    weeklyClassTime varchar(9) constraint WEEKDAY_CHECK check ( weeklyClassTime in ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday') ),
    startDate date,
    endDate date,
    currentParticipants integer,
    maxParticipants integer,
    foreign key (trainerID) references Trainer(id)
);

create table Package (
    name varchar(128) primary key,
    price numeric
);

create table PackageCourse (
    packageName varchar(128),
    courseName varchar(128),
    primary key (packageName, courseName),
    foreign key (packageName) references Package(name),
    foreign key (courseName) references Course(name)
);

create table PackageMembers (
    packageName varchar(128),
    memberId integer,
    primary key (packageName, memberId),
    foreign key (packageName) references Package(name),
    foreign key (memberId) references Member(id)
);

create table Equipment (
    name varchar(128) primary key,
    available integer,
    quantity integer
);

create table Borrow (
    memberId integer,
    equipmentName varchar(128),
    checkoutTime date,
    returnTime date,
    primary key (memberId, equipmentName, checkoutTime),
    foreign key (memberId) references Member(id),
    foreign key (equipmentName) references Equipment(name)
);

create table Transaction (
    id integer primary key,
    memberID integer,
    amount numeric,
    transactionDate date, -- renamed date to transactionDate to avoid oracle db error.
    transactionStatus varchar(10) constraint TRANS_STATUS check ( transactionStatus in ('DUE', 'PROCESSING', 'FINISHED')),
    transactionType varchar(5) constraint TRANS_TYPE check ( transactionType in ('CASH', 'CARD', 'CHECK')),
    foreign key (memberID) references Member(id)
);