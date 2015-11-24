alter table board drop constraint FK_46bxi72hjgqovkfee5tsg41ms
alter table board drop constraint FK_egmpx4ejx9jcxsrco924l0rld
alter table board_file_infos drop constraint FK_qph2bxv0crvq7iqdsqj062ci2
alter table board_file_infos drop constraint FK_lr7q498jorrdx0efe06gdo13h
alter table comment drop constraint FK_1fib2gxtvwexknprie08dwtkb

drop table board if exists
drop table board_file_infos if exists
drop table comment if exists
drop table file_info if exists
drop table kind_board if exists

create table board (
    board_id bigint generated by default as identity (start with 1),
    content clob(255) not null,
    create_date timestamp,
    read_count integer not null,
    title varchar(50) not null,
    kind_board_kindBoardId bigint,
    user_userId bigint,
    primary key (board_id)
)
create table board_file_infos (
    board_boardId bigint not null,
    file_infos_fileId bigint not null
)
create table comment (
    comment_id bigint generated by default as identity (start with 1),
    content varchar(255),
    create_date timestamp,
    depth integer not null,
    board_boardId bigint,
    primary key (comment_id)
)
create table file_info (
    file_id bigint generated by default as identity (start with 1),
    daily_folder boolean not null,
    daily_folder_path varchar(255),
    file_name varchar(255),
    file_size bigint,
    primary key (file_id)
)
create table kind_board (
    kind_board_id bigint generated by default as identity (start with 1),
    create_date timestamp,
    enable boolean not null,
    kind_board_name varchar(255) not null,
    primary key (kind_board_id)
)
create table user (
    user_id bigint generated by default as identity (start with 1),
    active boolean not null,
    authority_type varchar(255),
    first_name varchar(255) not null,
    join_date timestamp,
    last_name varchar(255) not null,
    nick_name varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (user_id)
)

alter table board_file_infos add constraint UK_qph2bxv0crvq7iqdsqj062ci2 unique (file_infos_fileId)
alter table file_info add constraint UK_gxex4d48dqfdx5q50ygox3ofh unique (file_name)
alter table kind_board add constraint UK_od3rm5c7436wpgr7q4xyntqe3 unique (kind_board_name)
alter table user add constraint UK_d2ia11oqhsynodbsi46m80vfc unique (nick_name)
alter table user add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username)
alter table board add constraint FK_46bxi72hjgqovkfee5tsg41ms foreign key (kind_board_kindBoardId) references kind_board
alter table board add constraint FK_egmpx4ejx9jcxsrco924l0rld foreign key (user_userId) references user
alter table board_file_infos add constraint FK_qph2bxv0crvq7iqdsqj062ci2 foreign key (file_infos_fileId) references file_info
alter table board_file_infos add constraint FK_lr7q498jorrdx0efe06gdo13h foreign key (board_boardId) references board
alter table comment add constraint FK_1fib2gxtvwexknprie08dwtkb foreign key (board_boardId) references board