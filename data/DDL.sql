create table project_user
(
    mid         integer not null
        constraint user_pkey
            primary key,
    name        varchar(400) not null ,
    sex         varchar(10),
    birthday    date,
    level       integer not null ,
    sign        varchar(2000),
    identity    varchar(20) not null
);

create table project_videos
(
    BV          varchar(400)
        constraint pk_videos
            primary key ,
    title       varchar(400),
    owner_mid   integer
        references project_user (mid),
    commit_time timestamp,
    review_time timestamp,
    public_time timestamp,
    duration    integer not null ,
    description varchar(2000),
    reviewer    integer
        references project_user (mid)
);

create table project_danmu
(
    BV          varchar(400)
        references project_videos (BV),
    mid         integer
        references project_user (mid),
    time        varchar(400),
    content     varchar(400),
    primary key (BV, mid, time)
);

create table project_following
(
    user_mid    integer
        references project_user(mid),
    fans_mid    integer
        references project_user(mid),
    primary key (user_mid, fans_mid)
);

create table project_like
(
    BV          varchar(400)
        references project_videos (BV),
    mid         integer
         references project_user (mid),
    primary key (BV, mid)
);

create table project_coin
(
    BV          varchar(400)
        references project_videos (BV),
    mid         integer
         references project_user (mid),
    primary key (BV, mid)
);

create table project_favorite
(
    BV          varchar(400)
        references project_videos (BV),
    mid         integer
         references project_user (mid),
    primary key (BV, mid)
);

create table project_view
(
    BV          varchar(400)
        references project_videos (BV),
    mid         integer
        references project_user (mid),
    time        integer,
    primary key (BV, mid)
);