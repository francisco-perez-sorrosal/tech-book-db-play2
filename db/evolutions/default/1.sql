# --- First database schema

# --- !Ups

set ignorecase true;

create table book (
  id                        bigint not null primary key,
  name                      varchar(255) not null,
  edition                   varchar(5),
  editorial                 varchar(255),
  year                      varchar(4),
  remark                    varchar(255),
  introduced                timestamp
);

create sequence book_seq start with 1000;
	
# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE if exists book;