--
-- Safester.sql
-- 
-- Safester Sql Database
--
-- ndp - 07/12/09 - 18:05 - v1.00 - creation!
-- ndp - 07/12/09 - 18:40 - v1.00 - update after ABE remarks
-- ndp - 08/12/09 - 11:55 - v1.00 - new folder & child_folder table
-- ndp - 05/01/10 - 11:20 - v1.00 - Add hash_passphrase to user_login
-- abe - 05/01/10 - 16:55 - v1.00 - Add table temp_pgp_key (based on pgp_key)
-- abe - 06/01/10 - 14:39 - v1.00 - Add key_id & user_name to user_token (to bind with temp_pgp_key)
-- ndp - 11/01/10 - 19:35 - v1.00 - remove unused parameters from pgp_key & temp_pgp_key
-- ndp - 12/01/10 - 12:10 - v1.00 - Merge ABE & NDP
-- ndp - 13/01/10 - 17:30 - v1.00 - Add hash_passphrase to user_token
-- ndp - 13/01/10 - 12:20 - v1.00 - create unique index key_id_index on user_login(key_id);
-- ndp - 14/01/10 - 12:20 - v1.00 - Complete new organisation for Messages which are now unique 
-- ndp - 15/01/10 - 15:20 - v1.00 - Change type typeRecipient from varchar to int & remove header_encrypted
-- ndp - 20/01/10 - 15:40 - v1.00 - new key for message_user: primary key (user_number, message_id, folder_id)
-- abe - 04/02/10 - 15:22 - v1.00  - change recipient primary key (add type_recipient to key)
-- abe - 10/03/10 - 16:56 - v1.00 - Add signature to table user_settings
-- ndp - 28/04/10 - 17:15 - v1.00 - Add user_number_increment & update message
-- ndp - 07/05/10 - 14:55 - v1.00 - alter table user_settings add notification_on boolean default true;
-- ndp - 17/05/10 - 18:15 - v1.00 - Add pending_user_id_increment & message_id_increment increment tables
-- abe - 18/05/10 - 15:29 - v1.00 - Add temp_email_user & email_user
-- abe - 27/05/10 - 12:15 - v1.00 - Remove temp_email_user
-- abe - 31/05/10 - 11:57 - v1.00 - Add connection_log
-- abe - 01/06/10 - 15:25 - v1.00 - pending_message_user add is_draft
--
-- ndp - 07/08/10 - 13:20 - v1.00 - user_token.user_email is now varchar(255)
-- ndp - 07/08/10 - 13:25 - v1.00 - add table discarded_ip
-- ndp - 24/09/10 - 23:10 - v1.00 - add table sql_statement
--
-- ndp - 11/10/10 - 19:30 - v1.00 - ALTER TABLE attachment ALTER COLUMN file_name TYPE varchar(280);
-- ndp - 15/10/10 - 16:30 - v1.00 - Add passprase_recovery table
-- ndp - 20/10/10 - 16:00 - v1.00 - message.size_message is now BIGINT
-- ndp - 20/10/10 - 16:45 - v1.00 - passprase_recovery new encrypted fields
-- ndp - 31/12/10 - 17:15 - v1.00 - ALTER TABLE folder ALTER COLUMN name TYPE varchar(256);
-- I18n:
-- ndp - 14/01/11 - 12:55 - v1.00 
-- 
-- ALTER TABLE folder          ALTER COLUMN name            TYPE VARCHAR(2560);
-- ALTER TABLE recipient       ALTER COLUMN name_recipient  TYPE VARCHAR(2560);
-- ALTER TABLE user_settings   ALTER COLUMN user_name       TYPE VARCHAR(1280);
-- ALTER TABLE attachment      ALTER COLUMN file_name       TYPE VARCHAR(2800);
--
-- ndp - 02/02/11 - 20:00 - v1.00 - Add responder table
-- abe - 07/02/11 - 17:31 - v1.00 - CREATE TABLE user_login_email_group
--                                  CREATE TABLE email_group;
-- ndp - 09/02/11 - 19:00 - v1.00 - Add and update groups table
-- abe - 10/02/11 - 09:36 - v1.00 - ALTER TABLE user_settings ADD COLUMN use_otp boolean DEFAULT false 
-- ndp - 21/02/11 - 15:10 - v1.00 - ALTER TABLE attachment ADD COLUMN file_size BIGINT default 0;

-- ndp - 23/02/11 - 12:20 - v1.00 - ALTER TABLE subscription RENAME COLUMN code             TO type_subscription;
-- ndp - 23/02/11 - 12:20 - v1.00 - ALTER TABLE voucher      RENAME COLUMN type_suscritpion TO type_subscription;
-- ndp - 23/02/11 - 12:20 - v1.00 - ALTER TABLE voucher      RENAME COLUMN voucher          TO voucher_code;
-- ndp - 23/02/11 - 12:20 - v1.00 - ALTER TABLE voucher      RENAME COLUMN voucher          TO voucher_code;
-- ndp - 23/02/11 - 12:20 - v1.00 - new definitions for voucher & subscription tables
-- abe - 18/02/11 - 14:04 - v1.00 - create table group_member & DROP TABLE user_login_email_group
-- abe - 02/03/11 - 15:39 - v1.00 - create table group_id_increment
-- abe - 10/03/11 - 15:30 - v1.00 - Comments
-- ndp - 14/04/11 - 19:20 - v1.00 - CREATE TABLE key_created_from_pending
-- ndp - 18/07/11 - 17:35 - v1.00 - CREATE TABLE login_log
-- ndp - 29/07/11 - 17:10 - v1.00 - ALTER TABLE sql_statement ALTER COLUMN sql_order TYPE varchar(1024);
-- ndp - 03/09/13 - 18:50 - v1.00 - add table user_photo
-- ndp - 23/03/18 - 19:43 - v1.00 - add table address_book_new
-- ndp - 27/03/18 - 19:43 - v1.00 - add address_book_key
-- ndp - 27/03/18 - 14:00 - v1.00 - add address_book_key

-- ndp - 06/04/18 - 13:51 - v1.00 - add bcc_notarization
-- ndp - 06/04/18 - 18:53 - v1.00 - add attach_storage_dir
-- ndp - 20/04/18 - 19:57 - v1.00 - ALTER TABLE recipient drop column name_recipient
-- ndp - 14/07/18 - 13:38 - v1.00 - DROP TABLE attach_storage_dir -- not used anymore, is replaced by ini file
-- ndp - 16/03/19 - 21:36 - v1.00 - emailAddress is now a key in address_book_new
-- ndp - 04/03/19 - 21:01 - v1.00 - CREATE TABLE company_coupon & user_coupon
-- 			        insert into partner values('KawanSoft', 'ndepomereu@kawansoft.com', 'N. de Pomereu', 'CEO', NULL, NULL, 'KAW-190406');
-- ndp - 04/03/19 - 21:01 - v1.00 -
-- ndp - 11/04/19 - 14:57 - v1.00 - alter table login_log add column device varchar(20);
-- ndp - 14/05/19 - 12:33 - v1.00 - create user_2fa

--
-- Stores encrypted secret
--

CREATE TABLE user_2fa (
  user_email        		  varchar(254)  not null,  
  base32_secret_encrypted     text	        not null,
  activity_status      		  boolean      default false,
        primary key (user_email)  
);

--
-- Define company for partners
--

CREATE TABLE company (
  company_name	        varchar(512)    not null,
  address_1				varchar(512)    not null, 
  address_2				varchar(512)    		, 
  city					varchar(128)    not null, 
  zip_code				varchar(20)     not null,
  country_code		    varchar(2)      not null,
  company_phone         varchar(40)    		,
  company_url           varchar(512)    		,
  dt_update             timestamp       default current_timestamp,
        primary key (company_name)  
);


drop table partner;
CREATE TABLE partner (
  company_name	        varchar(512)    not null,
  rep_email             varchar(254)    not null,
  rep_firstname         varchar(254)    not null,  
  rep_lastname          varchar(254)    not null,
  rep_pro_function		varchar(254)            , 
  rep_office_phone      varchar(40)    		   ,                      
  rep_cell_phone        varchar(40)            ,
  info			        text				   , 
  partner_coupon        varchar(20)     not null,
  dt_update             timestamp       default current_timestamp,
        primary key (company_name, rep_email)  
);
CREATE INDEX idx_partner_coupon ON partner(partner_coupon);


CREATE TABLE user_coupon (
  user_email        	varchar(254)    not null,  
  partner_coupon        varchar(20)     not null,
        primary key (user_email)  
);
CREATE INDEX idx_user_coupon_partner_coupon ON user_coupon(partner_coupon);


-- 
-- Says for each email domain if client side has to sent a BCC 
-- for silent notarization
-- 

create table bcc_notarization (
  domain         varchar(128)       not null,
  email_address  varchar(128)       not null,
        primary key (domain)
);

--
-- 
-- The lass inbox message displayed (for notifications)
-- 
-- 

create table last_message_in_display (
  user_number      integer         not null,
  last_message_id  integer         not null,
        primary key (user_number)
);

-- 
-- The new address book encryption key
-- 

CREATE TABLE address_book_key (
  user_number           integer         not null,
  encrypted_key         text            not null,                 
        primary key (user_number)  
);

-- 
-- The new address book
-- 

CREATE TABLE address_book_new (
  user_number           integer         not null,
  address_book_id       integer         not null,
  email                 varchar(512)    not null,
  name                  varchar(512)    not null,                 
  company               varchar(512)            ,         
  cell_phone            varchar(512)             ,
        primary key (user_number, address_book_id, email)  
);


CREATE TABLE user_photo (
  user_email        	varchar(254)    not null,  
  thumbnail	           	text			not null,
  photo	           		text			not null,
        primary key (user_email)  
);


-- Reminder : delete user ==> delete instance of pgp_key & user_login & user_settings
--

CREATE TABLE key_created_from_pending (
    email           varchar(254)  not null,
    dt_create       timestamp     not null,
        primary key (email)
);


-- drop table voucher;

create table voucher (
 voucher_code       varchar(255)       not null,
 swreg_order_id     varchar(64)                ,
 label              varchar(255)               ,
 email              varchar(255)       not null,
 date               timestamp          not null,
 type_subscription  integer            not null,
 nb_subscription    integer            not null,
 used_subscription  integer            not null default 0,
         primary key (voucher_code)
);  

-- drop table subscription;

create table subscription (
 user_number        integer             not null,
 voucher_code       varchar(255)        not null,
 startdate          timestamp           not null,
 enddate            timestamp           not null,
 active             boolean                     ,
         primary key (user_number, voucher_code)
);  
  
         
--
-- Store the pending_user_id increment
-- Must be in independent table for integrity reasons
--

create table pending_user_id_increment (
    pending_user_id      integer        not null,
     primary key (pending_user_id)
);  
    
--
-- Store the message_id increment
-- Must be in independent table for integrity reasons
--

create table message_id_increment (
  message_id            integer         not null,
     primary key (message_id)
);  
  
--
-- Store the user_number increment
-- Must be in independent table for integrity reasons
--

create table user_number_increment
(               
  user_number       integer         not null,   
      primary key (user_number)
);


--
-- Token for registration - The hash_id will identify the correct email before account creation
--

create table  user_token
(
  hash_id           varchar(40)     not null,
  user_email        varchar(254)    not null,     
  key_id            varchar(254)    not null,
  user_name         varchar(128)    not null,
  hash_passphrase   varchar(40)     not null,
      primary key (hash_id)
);

--
-- Store  the login info - The login can be an email
--

create table user_login
(               
  user_number           integer         not null,   
  login                 varchar(255)    not null,     
  key_id                varchar(254)    not null, 
  hash_passphrase       varchar(40)     not null,
        primary key (user_number)
);

-- the login & key_id must be unique in the system, of course!
create unique index user_login_index on user_login(login);
create unique index key_id_index on user_login(key_id);

--
-- The user base info & settings main table
--

-- drop table user_settings;

create table user_settings
(
  user_number           integer         not null,     
  user_name             varchar(1280)   not null, 
  notification_email    varchar(255)            ,
  receive_infos         boolean         default false,
  stealth_mode          boolean         default false,
  signature             text,
  notification_on       boolean         default true,
  send_anonymous_notification_on boolean default false,
  use_otp				boolean 		default false,
        primary key (user_number)
);

create table responder
(
  user_number           integer         not null,     
  responder_on          boolean         default false,
  dt_begin              date            not null,
  dt_expire             date,     
  responder_msg         text,
        primary key (user_number)
);

--
-- PGP keys (private and public) stored by email address (key_id)
--
  
create table  pgp_key
(
  key_id                varchar(254)    not null,
  fingerprint           varchar(40)     not null,
  key_type              varchar(16)     not null,
  key_length            varchar(16)     not null,
  dt_create             date            not null,
  dt_expire             date                    ,
  public_key_block      text            not null,
  private_key_block     text            not null,
  not_intern_origin     boolean         default false,  
        primary key (key_id)
);

---
--- The temporary table for pgp_key
---

create table  temp_pgp_key
(
  key_id                varchar(254)    not null,
  fingerprint           varchar(40)     not null,
  key_type              varchar(16)     not null,
  key_length            varchar(16)     not null,
  dt_create             date            not null,
  dt_expire             date                    ,
  public_key_block      text            not null,
  private_key_block     text            not null,
  not_intern_origin     boolean         default false,  
        primary key (key_id)
);

--
-- The email folders for each user. The Id is incremented++ per user_number  
--

create table folder
(
  user_number         integer         not null,
  folder_id           integer         not null, 
  name                varchar(2560)   not null,
        primary key (user_number, folder_id)
);

--
-- The list of children folders per (user, folder)
-- 

create table child_folder
(
  user_number         integer         not null,
  folder_id           integer         not null, 
  child_id            integer         not null,
        primary key (user_number, folder_id, child_id)
);

-- the index of children folders
create index child_folder_child_id on child_folder(child_id);

--
-- The messages - main with content & base info.
-- Each message is identified by a unique incremented message_id
-- Warning: This contains only the message sent, 
-- not the received messages instances. Table instances must never been deleted 
-- (only references in message_user can be deleted.
-- 

-- drop table message;

create table message (
  message_id            integer         not null,
  sender_user_number    integer         not null,
  priority              char(1)         ,
  is_with_attachment    boolean         default false,
  is_encrypted          boolean         default false,
  is_signed             boolean         default false,  
  date_message          timestamp       not null,
  subject               text            ,
  reply_to              varchar(254)            ,
  size_message          bigint          not null, 
  body                  text            not null,
  printable             boolean         default true,
  fowardable            boolean         default true,
        primary key (message_id)
);

-- Help-full indexes for Message Table
create index sender_user_number_id on message(sender_user_number);
create index date_message_id on message(date_message);

--
-- The Recipients references per Message
--

CREATE TABLE recipient (
  message_id            integer         not null,
  recipient_position    integer         not null,
  type_recipient        integer         not null,    
  user_number           integer         not null,          
  name_recipient        varchar(2560)   not null,
        primary key (message_id, recipient_position, type_recipient)
);

--
-- The Messages references per User: contains all messages info
-- per (user, message, folder_id) 
-- 

create table message_user (
  user_number           integer         not null,
  message_id            integer         not null,
  folder_id             integer         not null,
  is_read               boolean         default false,
        primary key (user_number, message_id, folder_id)
);

-- the index of messages per (user, folder)
create index user_number_folder_id on message_user(user_number, folder_id);


--
-- The Attachment references per Message
--

CREATE TABLE attachment (
  message_id            integer         not null,
  attach_position       integer         not null,
  file_name             varchar(2800)   not null,
  file_size             bigint          default 0,  
        primary key (message_id, attach_position)  
);


-- 
-- The address book
-- 

CREATE TABLE address_book (
  user_number           integer         not null,
  address_book_id       integer         not null,
  name                  varchar(128)    not null,                 
  email                 varchar(254)    not null,
        primary key (user_number, address_book_id)  
);

CREATE TABLE pending_user (
    pending_user_id integer not null,
    email           varchar(254) not null,
    UNIQUE(email),
        primary key (pending_user_id)
);
create index pending_user_email on pending_user(email);


CREATE TABLE pending_message_user (
    pending_user_id integer not null,
    message_id      integer not null,
    type_recipient  integer not null,
    is_draft        integer not null default 0,
        primary key(pending_user_id, message_id, type_recipient)
);
create index pending_message_user_user_id on pending_message_user(pending_user_id);



-- 
-- The list of aliases of users
-- 

CREATE TABLE email_user (
    user_number integer not null,
    email varchar(255) not null,
    primary key(user_number, email)
);

create index index_email_user_email on email_user(email);

--
-- The connection log
--

CREATE TABLE connection_log (
	ip_address varchar(15) not null,	
	connection_time timestamp not null,
	primary key (ip_address, connection_time)
);


--
-- The discarded IP
--

CREATE TABLE discarded_ip (
    ip_address      varchar(15)     not null,
    login           varchar(255)    not null,      
    date_time       timestamp       not null
);

create index index_discarded_ip_ip_address on discarded_ip(ip_address);
   
--
-- The table that contain all executed statement called from Awake on PC side
--

CREATE TABLE sql_statement (
    sql_order      varchar(512)     not null,     
    date_time      timestamp        not null,
    primary key (sql_order)
);

--
-- passprase_recovery table: allow user to recover passphrase with hint or email
--

-- drop table passprase_recovery;
create table passprase_recovery
(               
    user_number              integer   not null      ,      
    use_hint                 boolean   default false ,
    use_passphrase_recovery  boolean   default false ,               
    hint_encrypted           text                    ,
    question_encrypted       text                    ,
    answer_encrypted         text                    ,    
    passphrase_encrypted     text                    ,         
        primary key (user_number)
);

--
-- Groups Tables
--

create table email_group( 
    id  integer                     not null, 
    user_number     integer         not null, 
    name            varchar(255)    not null, 
        primary key(id) 
); 

create index email_group_name on email_group(name);
 
--create table user_login_email_group(
--    id_email_group  integer          not null,
--    user_number     integer          not null,
--        primary key(id_email_group, user_number)
--);

create table group_id_increment (
     group_id     integer        not null,
        primary key (group_id)
);  


create table group_member(
	id_email_group         int4 not null,
	email                  varchar(255) not null,
	   primary key(id_email_group, email)
)


--
-- The login log
--

CREATE TABLE login_log (
    user_number             integer         not null,
    date_time               timestamp       not null,
    ip_address              varchar(15)     not null,    
    hostname                varchar(255)    not null,
    device				    varchar(20)             , 
        primary key (user_number, date_time)
);


--
-- end
--

