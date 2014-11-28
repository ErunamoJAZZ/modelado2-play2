# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "album" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"owner" INTEGER,"name" VARCHAR(254) NOT NULL,"creation" TIMESTAMP NOT NULL,constraint "fk_album__user" foreign key("owner") references "user"("id") on update CASCADE on delete CASCADE);
create table "album_picture" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"album" INTEGER,"picture" INTEGER,"isPrivate" INTEGER NOT NULL,"creation" TIMESTAMP NOT NULL,constraint "fk_ap__album" foreign key("album") references "album"("id") on update CASCADE on delete SET NULL,constraint "fk_ap__picture" foreign key("picture") references "picture"("id") on update CASCADE on delete SET NULL);
create unique index "idx_album_picture" on "album_picture" ("album","picture");
create table "picture" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"path" VARCHAR(254) NOT NULL,"description" VARCHAR(254) NOT NULL,"last_edition" TIMESTAMP NOT NULL,"album" INTEGER NOT NULL);
create table "user" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"name" VARCHAR(254) NOT NULL,"password" VARCHAR(254) NOT NULL);
create unique index "idx_name" on "user" ("name");

# --- !Downs

drop table "user";
drop table "picture";
drop table "album_picture";
drop table "album";

