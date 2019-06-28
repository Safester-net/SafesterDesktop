--
-- PostgreSQL database dump
--

SET client_encoding = 'LATIN1';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account_prop; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE account_prop (
    account_id integer NOT NULL,
    account_comments character(254),
    max_mailbox_size integer NOT NULL,
    max_send_size integer NOT NULL,
    max_minutes_hold_for_create integer NOT NULL,
    min_seconds_hold_for_login integer NOT NULL,
    max_seconds_session_validity integer NOT NULL,
    max_recipients integer NOT NULL,
    cm_footer_1 character(254),
    cm_footer_2 character(254),
    dt_update timestamp with time zone
);


ALTER TABLE public.account_prop OWNER TO confimail;

--
-- Name: addr_book; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE addr_book (
    user_id integer,
    contact_id integer,
    contact_letter character(1),
    contact_e_mail character(254),
    contact_nickname character(254),
    contact_lastname character(254),
    contact_firstname character(254),
    is_a_list integer DEFAULT 0
);


ALTER TABLE public.addr_book OWNER TO confimail;

--
-- Name: clients; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE clients (
    cli_ref integer NOT NULL,
    cli_lastname character varying(200) NOT NULL,
    cli_firstname character varying(200) NOT NULL,
    cli_gender smallint NOT NULL,
    cli_birthdate timestamp without time zone,
    cli_email character varying(200) NOT NULL,
    cli_phone character varying(200) NOT NULL,
    cli_fax character varying(200),
    cli_addr_street_num integer,
    cli_addr_street_name character varying(200),
    cli_addr_zip integer,
    cli_addr_town character varying(200),
    cli_addr_country_code character(2)
);


ALTER TABLE public.clients OWNER TO confimail;

--
-- Name: cm_test; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE cm_test (
    user_id integer NOT NULL,
    user_value smallint NOT NULL,
    user_text character varying(255) NOT NULL
);


ALTER TABLE public.cm_test OWNER TO confimail;

--
-- Name: company; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE company (
    company_id integer NOT NULL,
    company_name character(64) NOT NULL,
    company_comments character(254) NOT NULL,
    company_client character(64) NOT NULL,
    client_email character(64) NOT NULL,
    client_phone character(40) NOT NULL,
    client_fax character(40),
    admin_contact character(64),
    billing_contact character(64),
    tech_contact character(64),
    dt_create timestamp with time zone,
    dt_update timestamp with time zone
);


ALTER TABLE public.company OWNER TO confimail;

--
-- Name: company_contact; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE company_contact (
    contact_id character(64) NOT NULL,
    contact_name character(64) NOT NULL,
    contact_comments character(254) NOT NULL,
    contact_email character(64) NOT NULL,
    contact_phone character(40) NOT NULL,
    contact_mobile character(40) NOT NULL,
    contact_fax character(40),
    dt_update timestamp with time zone
);


ALTER TABLE public.company_contact OWNER TO confimail;

--
-- Name: credit_cards; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE credit_cards (
    cli_ref integer NOT NULL,
    cb_type character(150) NOT NULL,
    cb_number character(40) NOT NULL,
    cb_expiration_date character(20) NOT NULL
);


ALTER TABLE public.credit_cards OWNER TO confimail;

--
-- Name: domain; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE "domain" (
    domain_id character(64) NOT NULL,
    domain_comments character(254),
    domain_smtp_server character(64) NOT NULL,
    domain_smtp_port integer NOT NULL,
    dt_update timestamp with time zone
);


ALTER TABLE public."domain" OWNER TO confimail;

--
-- Name: key_cert; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE key_cert (
    cert_id integer NOT NULL,
    line_num integer NOT NULL,
    cert_line character(76) NOT NULL
);


ALTER TABLE public.key_cert OWNER TO confimail;

--
-- Name: key_cert_info; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE key_cert_info (
    cert_id integer NOT NULL,
    key_id character(64) NOT NULL,
    key_type character(2) NOT NULL,
    signer_key_id character(64) NOT NULL
);


ALTER TABLE public.key_cert_info OWNER TO confimail;

--
-- Name: keypair_sign; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE keypair_sign (
    user_id integer NOT NULL,
    key_type character(2) NOT NULL,
    line_num integer NOT NULL,
    sign_hex character(254) NOT NULL
);


ALTER TABLE public.keypair_sign OWNER TO confimail;

--
-- Name: log_session; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE log_session (
    full_email character(64),
    user_ip_addr character(15),
    program character(64),
    event character(128),
    dt_create timestamp without time zone
);


ALTER TABLE public.log_session OWNER TO confimail;

--
-- Name: login_temp; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE login_temp (
    full_email character(64) NOT NULL,
    status integer NOT NULL,
    user_id integer NOT NULL,
    user_pass_salt character(40),
    dt_update timestamp with time zone
);


ALTER TABLE public.login_temp OWNER TO confimail;

--
-- Name: mailbox; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE mailbox (
    user_id integer NOT NULL,
    mailbox_id character(64) NOT NULL,
    mailbox_deletable integer NOT NULL
);


ALTER TABLE public.mailbox OWNER TO confimail;

--
-- Name: msg_attach; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE msg_attach (
    user_id integer NOT NULL,
    msg_id integer NOT NULL,
    attach_id integer NOT NULL,
    line_num integer NOT NULL,
    line_attach character(255) NOT NULL
);


ALTER TABLE public.msg_attach OWNER TO confimail;

--
-- Name: msg_attach_info; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE msg_attach_info (
    user_id integer NOT NULL,
    msg_id integer NOT NULL,
    attach_id integer NOT NULL,
    attach_size integer NOT NULL,
    content_type character(254) NOT NULL,
    content_type_charset character(64),
    content_type_name character(128),
    content_transfer_encoding character(64) NOT NULL,
    content_disposition character(254),
    content_disposition_filename character(128)
);


ALTER TABLE public.msg_attach_info OWNER TO confimail;

--
-- Name: msg_main; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE msg_main (
    user_id integer NOT NULL,
    mailbox_id character(64) NOT NULL,
    msg_id integer NOT NULL,
    msg_status character(1) NOT NULL,
    msg_date timestamp with time zone NOT NULL,
    msg_from character(128) NOT NULL,
    msg_reply_to character(128),
    msg_object character(254),
    msg_size integer NOT NULL,
    msg_is_encrypted integer NOT NULL,
    msg_is_signed integer NOT NULL,
    msg_is_to_vanish integer NOT NULL,
    vanish_date timestamp with time zone,
    msg_pass_cipher integer NOT NULL,
    msg_pass_hash character(40),
    msg_pass_salt character(40)
);


ALTER TABLE public.msg_main OWNER TO confimail;

--
-- Name: msg_signature; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE msg_signature (
    user_id integer NOT NULL,
    message_id integer NOT NULL,
    line_num integer NOT NULL,
    line_signature character(255) NOT NULL
);


ALTER TABLE public.msg_signature OWNER TO confimail;

--
-- Name: msg_to; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE msg_to (
    user_id integer NOT NULL,
    msg_id integer NOT NULL,
    to_num integer NOT NULL,
    to_type character(3) NOT NULL,
    to_name character(128) NOT NULL
);


ALTER TABLE public.msg_to OWNER TO confimail;

--
-- Name: myview; Type: VIEW; Schema: public; Owner: confimail
--

CREATE VIEW myview AS
    SELECT clients.cli_lastname, clients.cli_firstname FROM clients WHERE ((clients.cli_lastname)::text = 'lK1aD8Y='::text);


ALTER TABLE public.myview OWNER TO confimail;

--
-- Name: pop_extern; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE pop_extern (
    user_id integer NOT NULL,
    pop_server character(64) NOT NULL,
    pop_login character(40) NOT NULL,
    pop_password character(15) NOT NULL,
    pop_port integer NOT NULL,
    pop_protocol character(4) NOT NULL,
    pop_auth character(4) NOT NULL,
    mailbox_id character(64) NOT NULL,
    filter_is_activ smallint NOT NULL,
    msg_is_deleted smallint NOT NULL,
    get_only_new_msg smallint NOT NULL
);


ALTER TABLE public.pop_extern OWNER TO confimail;

--
-- Name: pop_session; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE pop_session (
    full_email character(64) NOT NULL,
    dt_update timestamp with time zone NOT NULL
);


ALTER TABLE public.pop_session OWNER TO confimail;

--
-- Name: pop_user; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE pop_user (
    full_email character(64) NOT NULL,
    user_id integer NOT NULL,
    pop_server character(64) NOT NULL,
    pop_port integer NOT NULL,
    pop_login character(40) NOT NULL,
    pop_password character(15) NOT NULL,
    pop_protocol character(4) NOT NULL,
    pop_auth character(4) NOT NULL
);


ALTER TABLE public.pop_user OWNER TO confimail;

--
-- Name: pop_validity; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE pop_validity (
    full_email character(64) NOT NULL,
    dt_begin timestamp with time zone NOT NULL,
    dt_end timestamp with time zone NOT NULL
);


ALTER TABLE public.pop_validity OWNER TO confimail;

--
-- Name: rack_directory; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE rack_directory (
    rack_id integer NOT NULL,
    rack_addr character(254) NOT NULL,
    rack_db_uri character(254) NOT NULL,
    is_rack_activ integer NOT NULL,
    user_capacity integer NOT NULL,
    load_ratio integer NOT NULL
);


ALTER TABLE public.rack_directory OWNER TO confimail;

--
-- Name: reserved_login; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE reserved_login (
    login_email character(40) NOT NULL,
    group_id integer NOT NULL,
    rack_id integer NOT NULL,
    is_pop_created integer NOT NULL,
    pop_login character(40),
    pop_password character(40),
    activation_code character(40) NOT NULL
);


ALTER TABLE public.reserved_login OWNER TO confimail;

--
-- Name: sa_download_form_data; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE sa_download_form_data (
    user_first_name character(64) NOT NULL,
    user_last_name character(64) NOT NULL,
    user_email character(64) NOT NULL,
    user_company character(64),
    user_phone character(64),
    ip_addr character(16),
    http_referer character(254),
    product character(64) NOT NULL,
    dt_update timestamp without time zone
);


ALTER TABLE public.sa_download_form_data OWNER TO confimail;

--
-- Name: sa_mailing_list; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE sa_mailing_list (
    full_email character(64) NOT NULL
);


ALTER TABLE public.sa_mailing_list OWNER TO confimail;

--
-- Name: safejdbc_iv; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE safejdbc_iv (
    database_name character varying(62) NOT NULL,
    table_name character varying(62) NOT NULL,
    column_index smallint NOT NULL,
    column_iv_hex character(16) NOT NULL
);


ALTER TABLE public.safejdbc_iv OWNER TO confimail;

--
-- Name: safejdbc_store; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE safejdbc_store (
    database_name character varying(62) NOT NULL,
    table_name character varying(62) NOT NULL,
    column_index smallint NOT NULL,
    column_name character varying(62) NOT NULL,
    column_type smallint NOT NULL,
    column_length smallint NOT NULL,
    column_precision smallint NOT NULL,
    column_scale smallint NOT NULL,
    column_is_ciphered smallint NOT NULL
);


ALTER TABLE public.safejdbc_store OWNER TO confimail;

--
-- Name: safejdbc_users; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE safejdbc_users (
    userid character varying(20) NOT NULL,
    algorithm character varying(40) NOT NULL,
    iv_hex character(16) NOT NULL,
    encrypted_challenge character varying(250) NOT NULL
);


ALTER TABLE public.safejdbc_users OWNER TO confimail;

--
-- Name: sales; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE sales (
    sa_product_id integer NOT NULL,
    cli_ref integer NOT NULL,
    cli_lastname character varying(200) NOT NULL,
    cli_firstname character varying(200) NOT NULL,
    sa_order integer NOT NULL,
    sa_buy_date timestamp without time zone NOT NULL,
    sa_buy_qty integer NOT NULL,
    sa_cli_remarks character varying(200)
);


ALTER TABLE public.sales OWNER TO confimail;

--
-- Name: save_log_session; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE save_log_session (
    full_email character(64),
    user_ip_addr character(15),
    program character(254),
    dt_create timestamp with time zone
);


ALTER TABLE public.save_log_session OWNER TO confimail;

--
-- Name: save_msg_attach_info_040510; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE save_msg_attach_info_040510 (
    user_id integer NOT NULL,
    msg_id integer NOT NULL,
    attach_id integer NOT NULL,
    attach_size integer NOT NULL,
    content_type character(64) NOT NULL,
    content_type_charset character(64),
    content_type_name character(128),
    content_transfer_encoding character(64) NOT NULL,
    content_disposition character(64),
    content_disposition_filename character(128)
);


ALTER TABLE public.save_msg_attach_info_040510 OWNER TO confimail;

--
-- Name: save_user_prng; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE save_user_prng (
    user_id integer NOT NULL,
    key_and_seed_1 character(128) NOT NULL,
    key_and_seed_2 character(128) NOT NULL,
    seed_usage integer NOT NULL,
    sign_1 character(128) NOT NULL,
    sign_2 character(128) NOT NULL
);


ALTER TABLE public.save_user_prng OWNER TO confimail;

--
-- Name: so_bill_status; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_bill_status (
    order_reference character(64) NOT NULL,
    bill_status integer NOT NULL,
    bill_comment character(254) NOT NULL,
    dt_update timestamp with time zone
);


ALTER TABLE public.so_bill_status OWNER TO confimail;

--
-- Name: so_order_log; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_order_log (
    order_reference character(64) NOT NULL,
    order_stage integer NOT NULL,
    order_log_comment character(254) NOT NULL,
    dt_update timestamp with time zone
);


ALTER TABLE public.so_order_log OWNER TO confimail;

--
-- Name: so_product_order; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_product_order (
    order_reference character(64) NOT NULL,
    user_data_id integer NOT NULL,
    mailbox_quantity integer NOT NULL,
    mailbox_duration integer NOT NULL,
    mailbox_type integer NOT NULL,
    order_currency character(65) NOT NULL,
    order_total_not double precision NOT NULL,
    order_vat_rate double precision NOT NULL,
    order_vat double precision NOT NULL,
    order_total_tti double precision NOT NULL,
    dt_create timestamp with time zone,
    dt_update timestamp with time zone
);


ALTER TABLE public.so_product_order OWNER TO confimail;

--
-- Name: so_referer_track; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_referer_track (
    ip_addr character(16) NOT NULL,
    http_referer character(254) NOT NULL,
    target_page character(254) NOT NULL,
    comments character(64),
    dt_update timestamp with time zone
);


ALTER TABLE public.so_referer_track OWNER TO confimail;

--
-- Name: so_test; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_test (
    order_reference character(64) NOT NULL,
    order_total_not double precision NOT NULL
);


ALTER TABLE public.so_test OWNER TO confimail;

--
-- Name: so_user_data; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_user_data (
    user_data_id integer NOT NULL,
    lastname character(64) NOT NULL,
    firstname character(64) NOT NULL,
    gender character(3) NOT NULL,
    company character(64),
    address_1 character(128) NOT NULL,
    address_2 character(128),
    address_3 character(128),
    city character(64) NOT NULL,
    zip_code character(20) NOT NULL,
    country_code character(2) NOT NULL,
    e_mail character(128) NOT NULL,
    phone_number character(20) NOT NULL,
    fax_number character(20),
    ip_addr character(16) NOT NULL,
    dt_create timestamp with time zone,
    dt_update timestamp with time zone
);


ALTER TABLE public.so_user_data OWNER TO confimail;

--
-- Name: so_user_send_control; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE so_user_send_control (
    full_email character(128) NOT NULL,
    mail_count integer NOT NULL
);


ALTER TABLE public.so_user_send_control OWNER TO confimail;

--
-- Name: soft_customer; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE soft_customer (
    e_mail character varying(128) NOT NULL,
    lastname character varying(64) NOT NULL,
    firstname character varying(64) NOT NULL,
    company character varying(64) NOT NULL,
    address_1 character varying(128) NOT NULL,
    address_2 character varying(128),
    city character varying(64) NOT NULL,
    zip_code character varying(20) NOT NULL,
    state character varying(20),
    country_code character varying(3) NOT NULL,
    phone_number character varying(20),
    fax_number character varying(20),
    ip_addr character varying(16) NOT NULL,
    product_hostname character varying(254) NOT NULL,
    product_name character varying(20) NOT NULL,
    product_category character varying(2) NOT NULL,
    accept_promotion integer NOT NULL,
    order_number character varying(19) NOT NULL,
    dt_create timestamp without time zone NOT NULL,
    dt_update timestamp without time zone
);


ALTER TABLE public.soft_customer OWNER TO confimail;

--
-- Name: super_user; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE super_user (
    full_email character(64) NOT NULL,
    user_data_id integer NOT NULL,
    group_id integer NOT NULL,
    mailbox_quantity_sum integer NOT NULL,
    dt_update timestamp with time zone
);


ALTER TABLE public.super_user OWNER TO confimail;

--
-- Name: temp; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE "temp" (
    user_id integer,
    rule_id integer,
    msg_object character(254),
    msg_from character(128),
    msg_to character(128),
    mailbox_id character(64),
    asset integer
);


ALTER TABLE public."temp" OWNER TO confimail;

--
-- Name: user_admin; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_admin (
    user_id integer NOT NULL,
    admin_is_root integer NOT NULL,
    dt_update timestamp with time zone
);


ALTER TABLE public.user_admin OWNER TO confimail;

--
-- Name: user_alias; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_alias (
    user_id integer NOT NULL,
    full_email character(64) NOT NULL
);


ALTER TABLE public.user_alias OWNER TO confimail;

--
-- Name: user_directory; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_directory (
    user_id integer NOT NULL,
    rack_id integer NOT NULL,
    status integer NOT NULL
);


ALTER TABLE public.user_directory OWNER TO confimail;

--
-- Name: user_footer; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_footer (
    user_id integer NOT NULL,
    footer_id character(64) NOT NULL,
    line_num integer NOT NULL,
    line_footer character(254) NOT NULL
);


ALTER TABLE public.user_footer OWNER TO confimail;

--
-- Name: user_group; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_group (
    group_id integer NOT NULL,
    company_id integer NOT NULL,
    group_name character(254) NOT NULL,
    domain_id character(64) NOT NULL,
    account_id integer NOT NULL,
    default_context_id character(64) NOT NULL,
    msg_store_root character(64),
    dt_update timestamp with time zone
);


ALTER TABLE public.user_group OWNER TO confimail;

--
-- Name: user_group_save; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_group_save (
    group_id integer NOT NULL,
    company_id integer NOT NULL,
    group_name character(254) NOT NULL,
    domain_id character(64) NOT NULL,
    account_id integer NOT NULL,
    default_context_id character(64) NOT NULL,
    dt_update timestamp with time zone
);


ALTER TABLE public.user_group_save OWNER TO confimail;

--
-- Name: user_keypair; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_keypair (
    user_id integer NOT NULL,
    key_type character(2) NOT NULL,
    line_num integer NOT NULL,
    key_hex character(254) NOT NULL
);


ALTER TABLE public.user_keypair OWNER TO confimail;

--
-- Name: user_login; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_login (
    user_id integer NOT NULL,
    group_id integer NOT NULL,
    user_lastname character(40) NOT NULL,
    user_firstname character(20) NOT NULL,
    user_sender_name character(64) NOT NULL,
    user_reply_to character(64),
    user_pass_hash character(40) NOT NULL,
    user_seed character(32) NOT NULL,
    key_pair_length integer NOT NULL,
    status integer NOT NULL,
    dt_create timestamp with time zone,
    dt_last_connection timestamp with time zone,
    host_last_connection character(128)
);


ALTER TABLE public.user_login OWNER TO confimail;

--
-- Name: user_mailing_list; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_mailing_list (
    user_id integer NOT NULL,
    list_id integer NOT NULL,
    contact_id integer NOT NULL
);


ALTER TABLE public.user_mailing_list OWNER TO confimail;

--
-- Name: user_pref; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_pref (
    user_id integer NOT NULL,
    screen_width_read integer NOT NULL,
    screen_width_write integer NOT NULL,
    max_messages_disp integer NOT NULL,
    is_getmail_on_login integer NOT NULL,
    is_stored_on_send integer NOT NULL,
    notify_email character(64),
    is_footer_activ integer NOT NULL,
    footer_id character(64)
);


ALTER TABLE public.user_pref OWNER TO confimail;

--
-- Name: user_prng; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_prng (
    user_id integer NOT NULL,
    key_and_seed_1 character(254) NOT NULL,
    key_and_seed_2 character(254) NOT NULL,
    seed_usage integer NOT NULL,
    sign_1 character(254) NOT NULL,
    sign_2 character(254) NOT NULL
);


ALTER TABLE public.user_prng OWNER TO confimail;

--
-- Name: user_rule; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_rule (
    user_id integer,
    rule_id integer,
    msg_object character(254),
    msg_from character(128),
    msg_to character(128),
    mailbox_id character(64),
    msg_is_encrypted integer,
    msg_is_signed integer,
    asset integer
);


ALTER TABLE public.user_rule OWNER TO confimail;

--
-- Name: user_session_key; Type: TABLE; Schema: public; Owner: confimail; Tablespace: 
--

CREATE TABLE user_session_key (
    user_id integer NOT NULL,
    key_id integer NOT NULL,
    key_value1 character(128) NOT NULL,
    key_value2 character(128) NOT NULL
);


ALTER TABLE public.user_session_key OWNER TO confimail;

--
-- Name: account_prop_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY account_prop
    ADD CONSTRAINT account_prop_pkey PRIMARY KEY (account_id);


--
-- Name: cm_test_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY cm_test
    ADD CONSTRAINT cm_test_pkey PRIMARY KEY (user_id);


--
-- Name: company_contact_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY company_contact
    ADD CONSTRAINT company_contact_pkey PRIMARY KEY (contact_id);


--
-- Name: company_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY company
    ADD CONSTRAINT company_pkey PRIMARY KEY (company_id);


--
-- Name: domain_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY "domain"
    ADD CONSTRAINT domain_pkey PRIMARY KEY (domain_id);


--
-- Name: key_cert_info_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY key_cert_info
    ADD CONSTRAINT key_cert_info_pkey PRIMARY KEY (key_id, key_type);


--
-- Name: key_cert_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY key_cert
    ADD CONSTRAINT key_cert_pkey PRIMARY KEY (cert_id, line_num);


--
-- Name: keypair_sign_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY keypair_sign
    ADD CONSTRAINT keypair_sign_pkey PRIMARY KEY (user_id, key_type, line_num);


--
-- Name: login_temp_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY login_temp
    ADD CONSTRAINT login_temp_pkey PRIMARY KEY (full_email, status);


--
-- Name: mailbox_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY mailbox
    ADD CONSTRAINT mailbox_pkey PRIMARY KEY (user_id, mailbox_id);


--
-- Name: msg_attach_info_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY save_msg_attach_info_040510
    ADD CONSTRAINT msg_attach_info_pkey PRIMARY KEY (user_id, msg_id, attach_id);


--
-- Name: msg_attach_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY msg_attach
    ADD CONSTRAINT msg_attach_pkey PRIMARY KEY (user_id, msg_id, attach_id, line_num);


--
-- Name: msg_main_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY msg_main
    ADD CONSTRAINT msg_main_pkey PRIMARY KEY (user_id, msg_id);


--
-- Name: msg_signature_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY msg_signature
    ADD CONSTRAINT msg_signature_pkey PRIMARY KEY (user_id, message_id, line_num);


--
-- Name: msg_to_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY msg_to
    ADD CONSTRAINT msg_to_pkey PRIMARY KEY (user_id, msg_id, to_num);


--
-- Name: new_msg_attach_info_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY msg_attach_info
    ADD CONSTRAINT new_msg_attach_info_pkey PRIMARY KEY (user_id, msg_id, attach_id);


--
-- Name: new_user_prng_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_prng
    ADD CONSTRAINT new_user_prng_pkey PRIMARY KEY (user_id);


--
-- Name: pop_extern_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY pop_extern
    ADD CONSTRAINT pop_extern_pkey PRIMARY KEY (user_id, pop_server, pop_login);


--
-- Name: pop_session_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY pop_session
    ADD CONSTRAINT pop_session_pkey PRIMARY KEY (full_email);


--
-- Name: pop_user_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY pop_user
    ADD CONSTRAINT pop_user_pkey PRIMARY KEY (full_email);


--
-- Name: pop_validity_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY pop_validity
    ADD CONSTRAINT pop_validity_pkey PRIMARY KEY (full_email);


--
-- Name: rack_directory_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY rack_directory
    ADD CONSTRAINT rack_directory_pkey PRIMARY KEY (rack_id);


--
-- Name: reserved_login_swap_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY reserved_login
    ADD CONSTRAINT reserved_login_swap_pkey PRIMARY KEY (login_email, group_id);


--
-- Name: sa_mailing_list_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY sa_mailing_list
    ADD CONSTRAINT sa_mailing_list_pkey PRIMARY KEY (full_email);


--
-- Name: safejdbc_iv_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY safejdbc_iv
    ADD CONSTRAINT safejdbc_iv_pkey PRIMARY KEY (database_name, table_name, column_index);


--
-- Name: safejdbc_store_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY safejdbc_store
    ADD CONSTRAINT safejdbc_store_pkey PRIMARY KEY (database_name, table_name, column_index);


--
-- Name: safejdbc_users_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY safejdbc_users
    ADD CONSTRAINT safejdbc_users_pkey PRIMARY KEY (userid, algorithm);


--
-- Name: so_bill_status_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY so_bill_status
    ADD CONSTRAINT so_bill_status_pkey PRIMARY KEY (order_reference);


--
-- Name: so_order_log_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY so_order_log
    ADD CONSTRAINT so_order_log_pkey PRIMARY KEY (order_reference, order_stage);


--
-- Name: so_test_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY so_test
    ADD CONSTRAINT so_test_pkey PRIMARY KEY (order_reference);


--
-- Name: so_user_data_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY so_user_data
    ADD CONSTRAINT so_user_data_pkey PRIMARY KEY (user_data_id);


--
-- Name: so_user_send_control_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY so_user_send_control
    ADD CONSTRAINT so_user_send_control_pkey PRIMARY KEY (full_email);


--
-- Name: super_user_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY super_user
    ADD CONSTRAINT super_user_pkey PRIMARY KEY (full_email);


--
-- Name: user_admin_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_admin
    ADD CONSTRAINT user_admin_pkey PRIMARY KEY (user_id);


--
-- Name: user_alias_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_alias
    ADD CONSTRAINT user_alias_pkey PRIMARY KEY (user_id, full_email);


--
-- Name: user_directory_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_directory
    ADD CONSTRAINT user_directory_pkey PRIMARY KEY (user_id);


--
-- Name: user_footer_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_footer
    ADD CONSTRAINT user_footer_pkey PRIMARY KEY (user_id, footer_id, line_num);


--
-- Name: user_group_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_group_save
    ADD CONSTRAINT user_group_pkey PRIMARY KEY (group_id);


--
-- Name: user_group_pkey2; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_group
    ADD CONSTRAINT user_group_pkey2 PRIMARY KEY (group_id);


--
-- Name: user_keypair_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_keypair
    ADD CONSTRAINT user_keypair_pkey PRIMARY KEY (user_id, key_type, line_num);


--
-- Name: user_login_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_login
    ADD CONSTRAINT user_login_pkey PRIMARY KEY (user_id);


--
-- Name: user_mailing_list_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_mailing_list
    ADD CONSTRAINT user_mailing_list_pkey PRIMARY KEY (user_id, list_id, contact_id);


--
-- Name: user_pref_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_pref
    ADD CONSTRAINT user_pref_pkey PRIMARY KEY (user_id);


--
-- Name: user_prng_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY save_user_prng
    ADD CONSTRAINT user_prng_pkey PRIMARY KEY (user_id);


--
-- Name: user_session_key_pkey; Type: CONSTRAINT; Schema: public; Owner: confimail; Tablespace: 
--

ALTER TABLE ONLY user_session_key
    ADD CONSTRAINT user_session_key_pkey PRIMARY KEY (user_id, key_id);


--
-- Name: key_cert_info_cert_id_key; Type: INDEX; Schema: public; Owner: confimail; Tablespace: 
--

CREATE UNIQUE INDEX key_cert_info_cert_id_key ON key_cert_info USING btree (cert_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

