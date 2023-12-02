create domain Phone varchar(22) not null constraint PHONE_CHECK check ( regexp_like(VALUE, '/^(\+\d\s?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$/gm') );

create domain Membership varchar(7) not null constraint MEMBERSHIP_CHECK check ( VALUE in ('Regular', 'Gold', 'Diamond') );

create domain Money_Type numeric; -- todo: maybe add a check for positive numerics.

create domain Weekday varchar(9) constraint WEEKDAY_CHECK check ( VALUE in ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday') );

create table Trainer (
    id integer primary key,
    name varchar(128),
    phone Phone
);

create table Level (
    name Membership primary key,
    discount integer,
    minSpendingAmount Money_Type
);

create table Member (
    id integer not null primary key,
    name varchar(128) not null,
    phone Phone,
    membershipLevel Membership,
    totalSpending Money_Type,
    foreign key (membershipLevel) references Level(name)
);

create table Course (
    name varchar(128) primary key,
    trainerID integer,
    weeklyClassTime Weekday,
    startDate date,
    endDate date,
    currentParticipants integer,
    maxParticipants integer,
    foreign key (trainerID) references Trainer(id)
);

create table Package (
    name varchar(128) primary key,
    price Money_Type
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
    amount Money_Type,
    date date,
    transactionStatus varchar(10) constraint TRANS_STATUS check ( transactionStatus in ('DUE', 'PROCESSING', 'FINISHED')),
    transactionType varchar(5) constraint TRANS_TYPE check ( transactionType in ('CASH', 'CARD', 'CHECK')),
    foreign key (memberID) references Member(id)
);