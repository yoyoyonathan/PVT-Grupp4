<<<<<<< HEAD
# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

=======
# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

>>>>>>> origin/Login
create table user (
  email                     varchar(255) not null,
  user_name                 varchar(255),
  password                  varchar(255),
  birth_date                integer,
  constraint pk_user primary key (email))
;

create sequence user_seq;



<<<<<<< HEAD

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;
=======
<<<<<<< HEAD

# --- !Downs

=======

# --- !Downs

>>>>>>> origin/Login
SET FOREIGN_KEY_CHECKS=0;
>>>>>>> origin/Login

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

