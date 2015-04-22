# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  folder                    varchar(255),
  constraint pk_project primary key (id))
;

create table task (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  done                      tinyint(1) default 0,
  due_date                  datetime,
  assigned_to_email         varchar(255),
  folder                    varchar(255),
  project_id                bigint,
  constraint pk_task primary key (id))
;

create table user (
  email                     varchar(255) not null,
  user_name                 varchar(255),
  password                  varchar(255),
  birth_date                integer,
  constraint pk_user primary key (email))
;


create table project_user (
  project_id                     bigint not null,
  user_email                     varchar(255) not null,
  constraint pk_project_user primary key (project_id, user_email))
;
alter table task add constraint fk_task_assignedTo_1 foreign key (assigned_to_email) references user (email) on delete restrict on update restrict;
create index ix_task_assignedTo_1 on task (assigned_to_email);
alter table task add constraint fk_task_project_2 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_task_project_2 on task (project_id);



alter table project_user add constraint fk_project_user_project_01 foreign key (project_id) references project (id) on delete restrict on update restrict;

alter table project_user add constraint fk_project_user_user_02 foreign key (user_email) references user (email) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table project;

drop table project_user;

drop table task;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

