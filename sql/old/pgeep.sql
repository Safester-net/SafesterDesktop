
--
-- pgeep.sql
-- 
-- pgeep sql database
--
-- ndp - 12/07/05 - 19:50 - v1.00 - creation
-- ndp - 21/07/05 - 15:30 - v1.02 - creation
-- ndp - 09/08/05 - 11:25 - v1.03 - user_name is 128 chars & add index on it
-- ndp - 05/09/05 - 12:55 - v1.04 -	add the user_prereg table (for preregister from buzz page)
-- ndp - 07/10/05 - 16:45 - v1.04 - add columns to user_token
-- ndp - 08/10/05 - 17:20 - v1.04 - remove the columns from user_token & add  hash_id on user_prereg
-- ndp - 21/10/05 - 14:05 - v1.05 - add the language to user_token
-- ndp - 24/12/05 - 19:20 - v1.06 - add the receive_infos in pgp_user
-- ndp - 26/01/06 - 18:05 - v1.07 - add key_download_log
-- ab  - 13/04/06 - 15:12 - v1.08 - add inscription_date in user_prereg
-- ab  - 12/05/06 - 10:39 - v1.09 - add new table user_prereg_backup
-- ab  - 17/05/06 - 10:50 - v1.10 - add user_lang in tables user_prereg & user_ptereg_backup
-- ndp - 17/05/06 - 20:15 - v1.11 - add weblog
-- ndp - 23/05/06 - 14:50 - v1.11 - comments
-- ndp - 09/06/06 - 11:15 - v1.12 - Admin Login
-- ndp - 10/11/06 - 17:10 - v1.12 - Comments
-- ndp - 19/03/07 - 21:25 - v1.13 - Add license_register table
-- ndp - 01/12/07 - 15:30 - v1.14 - add email_invitation table
-- ndp - 14/12/07 - 16:05 - v1.15 - alter table email_invitation add dt_update timestamp
-- ndp - 20/02/09 - 17:15 - v1.16 - add eval_licence
-- abe - 11/03/08 - 14:54 - v1.17 - add column ip_addr varchar(16) in license_buy_register & in table eval_licence
-- abe - 14/03/08 - 14:54 - v1.17 - Comments
-- abe - 24/04/08 - 17:52 - v1.17 - Add public_key_buffer
-- ndp - 02/05/08 - 13:30 - v1.17 - Add   not_cgeep_origin  to (CACHE_)PGP_KEY
-- ndp - 06/05/08 - 20:35 - v1.17 - Comments (remove some tab character)
-- ndp - 29/05/08 - 12:25 - v1.17 - CREATE INDEX weblog_index_ip_addr   ON weblog (ip_addr);
-- ndp - 06/03/09 - 16:10 - v1.18 - remove license_eval_register & license_eval_token

CREATE TABLE eval_licence_stop
(
  key_id            varchar(64) NOT NULL,
  stop_reason       varchar(64),
  dt_update         timestamp without time zone NOT NULL,  
    primary key (key_id)
);

CREATE TABLE eval_licence
(
  key_id            varchar(64) NOT NULL,
  expiration_date   timestamp without time zone NOT NULL,
  licence_file      varchar(255) NOT NULL,
  ip_addr           varchar(16), 
    primary key (key_id)
);

--
-- Table of pGeep Pro registration. To be used for Buy (Perpetual license)
--
create table license_buy_register
(
	order_reference				varchar(64)		not null,
	product_code				varchar(20)		not null,
	firstname 					varchar(64) 	not null,
	lastname   					varchar(64) 	not null,
	company_name    			varchar(64), 	
	phone_number    			varchar(32),
	street_address_1    		varchar(64),
	street_address_2    		varchar(64),
	city    					varchar(64),
	state_province   			varchar(32),
	postal_code    				varchar(32),
	country 					varchar(64)		not null,
	vat_number   				varchar(32),		
	email   					varchar(64)		not null,
	alternative_email			varchar(64),		
	receive_infos				integer,
	buy_quantity				integer			not null,
	dt_update 					timestamp		not null,
	ip_addr						varchar(16),
			primary key (order_reference)
);

  
create table user_prereg
(
  user_email 	varchar(64) 	not null,
  user_name 	varchar(64) 	not null,
  user_os		integer			not null,
  os_other		varchar(16),
  user_mailer	integer			not null,
  mailer_other	varchar(16)				,  	
  hash_id 		varchar(40)				,	
  inscription_date timestamp			,
  user_lang		varchar(2)				,
	primary key (user_email)
);


CREATE TABLE user_prereg_backup
(
  user_email 		varchar(64) 	NOT NULL,
  user_name 		varchar(64) 	NOT NULL,
  user_os 			integer 		NOT NULL,
  os_other 			varchar(16)				,
  user_mailer 		integer 		NOT NULL,
  mailer_other 		varchar(16)				,
  hash_id 			varchar(40)				,
  inscription_date 	timestamp 				,
  user_lang		varchar(2)					,
	 PRIMARY KEY (user_email)
);

CREATE INDEX user_prereg_hash_id ON user_prereg (hash_id);

create table pgp_user
(
  user_email 	varchar(64) 	not null,
  user_name 	varchar(128) 	not null,
  key_id	 	varchar(64) 	not null,
  receive_infos integer				    ,
  stealth_mode	integer 		default 0,
  	primary key (user_email)
);

CREATE INDEX pgp_user_user_name_index ON pgp_user (user_name);

create table  user_token
(
  hash_id 			varchar(40) 	not null,
  user_email 		varchar(64) 	not null,
  user_name	 		varchar(128) 	not null,  
  user_language	 	varchar(2) 		not null,  
  receive_infos 	integer				    ,
	primary key (hash_id)
);

create table  pgp_key
(
  key_id 			varchar(64) not null,
  user_pass_hash 	varchar(40)			,
  fingerprint 		varchar(40) not null,
  key_type		    varchar(16) not null,
  key_length 		varchar(16) not null,
  sym_algorithm		varchar(16) not null,
  dt_create			date 		not null,
  dt_expire			date 				,
  public_key_block	text 		not null,
  private_key_block	text 				,
  not_cgeep_origin  integer     DEFAULT 0,  
  	primary key (key_id)
);

create table key_download_log
(
  key_id 			varchar(64) 					not null,
  dt_log			timestamp without time zone 	not null
);

CREATE INDEX key_download_log_index ON key_download_log (key_id);

create table user_pref
(
  user_email 		varchar(64) 	not null,
  user_name 		varchar(128) 	not null,  
  key_directory 	varchar(256) 	not null,
  encrypt_to_user	smallint	 	not null,
  cache_passphrase 	smallint	 	not null,
  cache_duration	integer		 	not null,
  encrypt_on_send	smallint 		not null,
  decrypt_on_recv	smallint 		not null,
  		primary key (user_email)
);


create table weblog
(
  ip_addr       		varchar(16),             
  http_referer  		varchar(254),            
  program		   	    varchar(254),           
  comments      		varchar(64),            
  dt_update     		timestamp 
);

CREATE INDEX weblog_index_ip_addr   ON weblog (ip_addr);
CREATE INDEX weblog_index_dt_update ON weblog (dt_update);
CREATE INDEX weblog_index_program   ON weblog (program);


create table admin_login
(
  login       			varchar(64)		not null,             
  password	  			varchar(64)		not null,
  	  	primary key (login)            
);


--
-- email_invitation table
--

create table email_invitation 
(
  user_email 		varchar(64) 	not null,
  invited_email 	varchar(64) 	not null,
  dt_update     	timestamp 
);


CREATE INDEX email_invitation_index_1 ON email_invitation(user_email); 
CREATE INDEX email_invitation_index_2 ON email_invitation(invited_email); 

CREATE TABLE public_key_buffer
(
  user_id character varying(64) NOT NULL,
  public_key_block text,
   primary key (user_id)
) ;

CREATE TABLE cache_pgp_user
(
  user_email 	varchar(64) 	not null,
  user_name 	varchar(128) 	not null,
  key_id	 	varchar(64) 	not null,
  receive_infos integer				    ,
  stealth_mode  integer 		default 0, 
	primary key (user_email)
);


create table  cache_pgp_key
(
  key_id 			 varchar(64) not null,
  user_pass_hash 	 varchar(40)			,
  fingerprint 		 varchar(40) not null,
  key_type		     varchar(16) not null,
  key_length 		 varchar(16) not null,
  sym_algorithm      varchar(16) not null,
  dt_create			 date        not null,
  dt_expire			 date 				,
  public_key_block	 text 		not null,
  private_key_block	 text 				,
  not_cgeep_origin   integer      DEFAULT 0,
  	primary key (key_id)
);

-- end --
















