# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  email                     varchar(255) not null,
  user_name                 varchar(255),
  password                  varchar(255),
  birth_date                integer,
  constraint pk_user primary key (email))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

