

create database ibike;

use ibike;


create table bike(
  bid bigInt primary key auto_increment,
  status int default 0,
  qrcode varchar(100) default '',
  latitude double,
  longitude double
)

select * from bike;

insert into bike values (bid,1,"",0,0);

update bike set status = 1 where bid = 4;