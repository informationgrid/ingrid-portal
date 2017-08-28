--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.5
-- Dumped by pg_dump version 9.5.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: admin_activity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE admin_activity (
    activity character varying(40),
    category character varying(40),
    admin character varying(80),
    user_name character varying(80),
    time_stamp timestamp(3) without time zone,
    ipaddress character varying(80),
    attr_name character varying(200),
    attr_value_before character varying(1000),
    attr_value_after character varying(1000),
    description character varying(128)
);


ALTER TABLE admin_activity OWNER TO postgres;

--
-- Name: capability; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE capability (
    capability_id integer NOT NULL,
    capability character varying(80) NOT NULL
);


ALTER TABLE capability OWNER TO postgres;

--
-- Name: client; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE client (
    client_id integer NOT NULL,
    eval_order integer NOT NULL,
    name character varying(80) NOT NULL,
    user_agent_pattern character varying(128),
    manufacturer character varying(80),
    model character varying(80),
    version character varying(40),
    preferred_mimetype_id integer NOT NULL
);


ALTER TABLE client OWNER TO postgres;

--
-- Name: client_to_capability; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE client_to_capability (
    client_id integer NOT NULL,
    capability_id integer NOT NULL
);


ALTER TABLE client_to_capability OWNER TO postgres;

--
-- Name: client_to_mimetype; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE client_to_mimetype (
    client_id integer NOT NULL,
    mimetype_id integer NOT NULL
);


ALTER TABLE client_to_mimetype OWNER TO postgres;

--
-- Name: clubs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE clubs (
    name character varying(80) NOT NULL,
    country character varying(40) NOT NULL,
    city character varying(40) NOT NULL,
    stadium character varying(80) NOT NULL,
    capacity integer,
    founded integer,
    pitch character varying(40),
    nickname character varying(40)
);


ALTER TABLE clubs OWNER TO postgres;

--
-- Name: custom_portlet_mode; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE custom_portlet_mode (
    id integer NOT NULL,
    application_id integer NOT NULL,
    custom_name character varying(150) NOT NULL,
    mapped_name character varying(150),
    portal_managed smallint NOT NULL
);


ALTER TABLE custom_portlet_mode OWNER TO postgres;

--
-- Name: custom_window_state; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE custom_window_state (
    id integer NOT NULL,
    application_id integer NOT NULL,
    custom_name character varying(150) NOT NULL,
    mapped_name character varying(150)
);


ALTER TABLE custom_window_state OWNER TO postgres;

--
-- Name: event_alias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE event_alias (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    local_part character varying(80) NOT NULL,
    namespace character varying(80),
    prefix character varying(20)
);


ALTER TABLE event_alias OWNER TO postgres;

--
-- Name: event_definition; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE event_definition (
    id integer NOT NULL,
    application_id integer NOT NULL,
    local_part character varying(80) NOT NULL,
    namespace character varying(80),
    prefix character varying(20),
    value_type character varying(255)
);


ALTER TABLE event_definition OWNER TO postgres;

--
-- Name: filter_lifecycle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE filter_lifecycle (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    name character varying(150) NOT NULL
);


ALTER TABLE filter_lifecycle OWNER TO postgres;

--
-- Name: filter_mapping; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE filter_mapping (
    id integer NOT NULL,
    application_id integer NOT NULL,
    filter_name character varying(150) NOT NULL
);


ALTER TABLE filter_mapping OWNER TO postgres;

--
-- Name: filtered_portlet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE filtered_portlet (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    name character varying(150) NOT NULL
);


ALTER TABLE filtered_portlet OWNER TO postgres;

--
-- Name: folder; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder (
    folder_id integer NOT NULL,
    parent_id integer,
    path character varying(240) NOT NULL,
    name character varying(80) NOT NULL,
    title character varying(100),
    short_title character varying(40),
    is_hidden smallint NOT NULL,
    skin character varying(80),
    default_layout_decorator character varying(80),
    default_portlet_decorator character varying(80),
    default_page_name character varying(80),
    subsite character varying(40),
    user_principal character varying(40),
    role_principal character varying(40),
    group_principal character varying(40),
    mediatype character varying(15),
    locale character varying(20),
    ext_attr_name character varying(15),
    ext_attr_value character varying(40),
    owner_principal character varying(40)
);


ALTER TABLE folder OWNER TO postgres;

--
-- Name: folder_constraint; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder_constraint (
    constraint_id integer NOT NULL,
    folder_id integer NOT NULL,
    apply_order integer NOT NULL,
    user_principals_acl character varying(120),
    role_principals_acl character varying(120),
    group_principals_acl character varying(120),
    permissions_acl character varying(120)
);


ALTER TABLE folder_constraint OWNER TO postgres;

--
-- Name: folder_constraints_ref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder_constraints_ref (
    constraints_ref_id integer NOT NULL,
    folder_id integer NOT NULL,
    apply_order integer NOT NULL,
    name character varying(40) NOT NULL
);


ALTER TABLE folder_constraints_ref OWNER TO postgres;

--
-- Name: folder_menu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder_menu (
    menu_id integer NOT NULL,
    class_name character varying(100) NOT NULL,
    parent_id integer,
    folder_id integer,
    element_order integer,
    name character varying(100),
    title character varying(100),
    short_title character varying(40),
    text character varying(100),
    options character varying(255),
    depth integer,
    is_paths smallint,
    is_regexp smallint,
    profile character varying(80),
    options_order character varying(255),
    skin character varying(80),
    is_nest smallint
);


ALTER TABLE folder_menu OWNER TO postgres;

--
-- Name: folder_menu_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder_menu_metadata (
    metadata_id integer NOT NULL,
    menu_id integer NOT NULL,
    name character varying(15) NOT NULL,
    locale character varying(20),
    value character varying(100) NOT NULL
);


ALTER TABLE folder_menu_metadata OWNER TO postgres;

--
-- Name: folder_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder_metadata (
    metadata_id integer NOT NULL,
    folder_id integer NOT NULL,
    name character varying(15) NOT NULL,
    locale character varying(20),
    value character varying(100) NOT NULL
);


ALTER TABLE folder_metadata OWNER TO postgres;

--
-- Name: folder_order; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE folder_order (
    order_id integer NOT NULL,
    folder_id integer NOT NULL,
    sort_order integer NOT NULL,
    name character varying(80) NOT NULL
);


ALTER TABLE folder_order OWNER TO postgres;

--
-- Name: fragment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE fragment (
    fragment_id integer NOT NULL,
    class_name character varying(100) NOT NULL,
    parent_id integer,
    page_id integer,
    fragment_string_id character varying(80),
    fragment_string_refid character varying(80),
    name character varying(100),
    title character varying(100),
    short_title character varying(40),
    type character varying(40),
    skin character varying(80),
    decorator character varying(80),
    state character varying(10),
    pmode character varying(10),
    layout_row integer,
    layout_column integer,
    layout_sizes character varying(20),
    layout_x double precision,
    layout_y double precision,
    layout_z double precision,
    layout_width double precision,
    layout_height double precision,
    owner_principal character varying(40)
);


ALTER TABLE fragment OWNER TO postgres;

--
-- Name: fragment_constraint; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE fragment_constraint (
    constraint_id integer NOT NULL,
    fragment_id integer NOT NULL,
    apply_order integer NOT NULL,
    user_principals_acl character varying(120),
    role_principals_acl character varying(120),
    group_principals_acl character varying(120),
    permissions_acl character varying(120)
);


ALTER TABLE fragment_constraint OWNER TO postgres;

--
-- Name: fragment_constraints_ref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE fragment_constraints_ref (
    constraints_ref_id integer NOT NULL,
    fragment_id integer NOT NULL,
    apply_order integer NOT NULL,
    name character varying(40) NOT NULL
);


ALTER TABLE fragment_constraints_ref OWNER TO postgres;

--
-- Name: fragment_pref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE fragment_pref (
    pref_id integer NOT NULL,
    fragment_id integer NOT NULL,
    name character varying(40) NOT NULL,
    is_read_only smallint NOT NULL
);


ALTER TABLE fragment_pref OWNER TO postgres;

--
-- Name: fragment_pref_value; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE fragment_pref_value (
    pref_value_id integer NOT NULL,
    pref_id integer NOT NULL,
    value_order integer NOT NULL,
    value character varying(100) NOT NULL
);


ALTER TABLE fragment_pref_value OWNER TO postgres;

--
-- Name: fragment_prop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE fragment_prop (
    prop_id integer NOT NULL,
    fragment_id integer NOT NULL,
    name character varying(40) NOT NULL,
    scope character varying(10),
    scope_value character varying(40),
    value character varying(100) NOT NULL
);


ALTER TABLE fragment_prop OWNER TO postgres;

--
-- Name: ingrid_anniversary; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_anniversary (
    id integer NOT NULL,
    topic_id character varying(255) NOT NULL,
    topic_name character varying(255),
    date_from character varying(255),
    date_from_year integer,
    date_from_month integer,
    date_from_day integer,
    date_to character varying(255),
    date_to_year integer,
    date_to_month integer,
    date_to_day integer,
    administrative_id character varying(255),
    fetched timestamp without time zone DEFAULT now() NOT NULL,
    fetched_for timestamp(3) without time zone DEFAULT '0002-11-30 00:00:00'::timestamp without time zone NOT NULL,
    language character varying(5) DEFAULT 'de'::character varying
);


ALTER TABLE ingrid_anniversary OWNER TO postgres;

--
-- Name: ingrid_anniversary_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_anniversary_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_anniversary_id_seq OWNER TO postgres;

--
-- Name: ingrid_anniversary_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_anniversary_id_seq OWNED BY ingrid_anniversary.id;


--
-- Name: ingrid_chron_eventtypes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_chron_eventtypes (
    id integer NOT NULL,
    form_value character varying(255) NOT NULL,
    query_value character varying(255) NOT NULL,
    sortkey integer DEFAULT 0 NOT NULL
);


ALTER TABLE ingrid_chron_eventtypes OWNER TO postgres;

--
-- Name: ingrid_chron_eventtypes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_chron_eventtypes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_chron_eventtypes_id_seq OWNER TO postgres;

--
-- Name: ingrid_chron_eventtypes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_chron_eventtypes_id_seq OWNED BY ingrid_chron_eventtypes.id;


--
-- Name: ingrid_cms; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_cms (
    id integer NOT NULL,
    item_key character varying(255) NOT NULL,
    item_description character varying(255),
    item_changed timestamp without time zone DEFAULT now() NOT NULL,
    item_changed_by character varying(255)
);


ALTER TABLE ingrid_cms OWNER TO postgres;

--
-- Name: ingrid_cms_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_cms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_cms_id_seq OWNER TO postgres;

--
-- Name: ingrid_cms_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_cms_id_seq OWNED BY ingrid_cms.id;


--
-- Name: ingrid_cms_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_cms_item (
    id integer NOT NULL,
    fk_ingrid_cms_id integer DEFAULT 0 NOT NULL,
    item_lang character varying(6) NOT NULL,
    item_title character varying(255),
    item_value text,
    item_changed timestamp without time zone DEFAULT now() NOT NULL,
    item_changed_by character varying(255)
);


ALTER TABLE ingrid_cms_item OWNER TO postgres;

--
-- Name: ingrid_cms_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_cms_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_cms_item_id_seq OWNER TO postgres;

--
-- Name: ingrid_cms_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_cms_item_id_seq OWNED BY ingrid_cms_item.id;


--
-- Name: ingrid_env_topic; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_env_topic (
    id integer NOT NULL,
    form_value character varying(255) NOT NULL,
    query_value character varying(255) NOT NULL,
    sortkey integer DEFAULT 0 NOT NULL
);


ALTER TABLE ingrid_env_topic OWNER TO postgres;

--
-- Name: ingrid_env_topic_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_env_topic_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_env_topic_id_seq OWNER TO postgres;

--
-- Name: ingrid_env_topic_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_env_topic_id_seq OWNED BY ingrid_env_topic.id;


--
-- Name: ingrid_lookup; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_lookup (
    id integer DEFAULT 0 NOT NULL,
    item_key character varying(255) NOT NULL,
    item_value character varying(255),
    item_date timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE ingrid_lookup OWNER TO postgres;

--
-- Name: ingrid_measures_rubric; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_measures_rubric (
    id integer NOT NULL,
    form_value character varying(255) NOT NULL,
    query_value character varying(255) NOT NULL,
    sortkey integer DEFAULT 0 NOT NULL
);


ALTER TABLE ingrid_measures_rubric OWNER TO postgres;

--
-- Name: ingrid_measures_rubric_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_measures_rubric_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_measures_rubric_id_seq OWNER TO postgres;

--
-- Name: ingrid_measures_rubric_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_measures_rubric_id_seq OWNED BY ingrid_measures_rubric.id;


--
-- Name: ingrid_newsletter_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_newsletter_data (
    id integer NOT NULL,
    firstname character varying(255),
    lastname character varying(255),
    email character varying(255),
    created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE ingrid_newsletter_data OWNER TO postgres;

--
-- Name: ingrid_newsletter_data_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_newsletter_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_newsletter_data_id_seq OWNER TO postgres;

--
-- Name: ingrid_newsletter_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_newsletter_data_id_seq OWNED BY ingrid_newsletter_data.id;


--
-- Name: ingrid_partner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_partner (
    id integer NOT NULL,
    ident character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    sortkey integer DEFAULT 0 NOT NULL
);


ALTER TABLE ingrid_partner OWNER TO postgres;

--
-- Name: ingrid_partner_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_partner_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_partner_id_seq OWNER TO postgres;

--
-- Name: ingrid_partner_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_partner_id_seq OWNED BY ingrid_partner.id;


--
-- Name: ingrid_principal_pref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_principal_pref (
    id integer NOT NULL,
    principal_name character varying(251) NOT NULL,
    pref_name character varying(251) NOT NULL,
    pref_value text,
    modified_date timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE ingrid_principal_pref OWNER TO postgres;

--
-- Name: ingrid_principal_pref_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_principal_pref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_principal_pref_id_seq OWNER TO postgres;

--
-- Name: ingrid_principal_pref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_principal_pref_id_seq OWNED BY ingrid_principal_pref.id;


--
-- Name: ingrid_provider; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_provider (
    id integer NOT NULL,
    ident character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    url character varying(255) NOT NULL,
    sortkey integer DEFAULT 0 NOT NULL,
    sortkey_partner integer DEFAULT 0 NOT NULL
);


ALTER TABLE ingrid_provider OWNER TO postgres;

--
-- Name: ingrid_provider_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_provider_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_provider_id_seq OWNER TO postgres;

--
-- Name: ingrid_provider_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_provider_id_seq OWNED BY ingrid_provider.id;


--
-- Name: ingrid_rss_source; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_rss_source (
    id integer NOT NULL,
    provider character varying(255) NOT NULL,
    description character varying(1023),
    url character varying(255) NOT NULL,
    lang character varying(255) NOT NULL,
    categories character varying(255),
    error character varying(255),
    numlastcount smallint,
    lastupdate timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE ingrid_rss_source OWNER TO postgres;

--
-- Name: ingrid_rss_source_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_rss_source_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_rss_source_id_seq OWNER TO postgres;

--
-- Name: ingrid_rss_source_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_rss_source_id_seq OWNED BY ingrid_rss_source.id;


--
-- Name: ingrid_rss_store; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_rss_store (
    link character varying(255) NOT NULL,
    author character varying(1023),
    categories character varying(255),
    copyright character varying(255),
    description text,
    language character varying(255),
    published_date timestamp without time zone DEFAULT now() NOT NULL,
    title character varying(1023)
);


ALTER TABLE ingrid_rss_store OWNER TO postgres;

--
-- Name: ingrid_service_rubric; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_service_rubric (
    id integer NOT NULL,
    form_value character varying(255) NOT NULL,
    query_value character varying(255) NOT NULL,
    sortkey integer DEFAULT 0 NOT NULL
);


ALTER TABLE ingrid_service_rubric OWNER TO postgres;

--
-- Name: ingrid_service_rubric_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_service_rubric_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_service_rubric_id_seq OWNER TO postgres;

--
-- Name: ingrid_service_rubric_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_service_rubric_id_seq OWNED BY ingrid_service_rubric.id;


--
-- Name: ingrid_tiny_url; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ingrid_tiny_url (
    id integer NOT NULL,
    user_ref character varying(254) NOT NULL,
    tiny_key character varying(254) NOT NULL,
    tiny_name character varying(254) NOT NULL,
    tiny_date timestamp without time zone DEFAULT now() NOT NULL,
    tiny_config text NOT NULL
);


ALTER TABLE ingrid_tiny_url OWNER TO postgres;

--
-- Name: ingrid_tiny_url_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ingrid_tiny_url_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ingrid_tiny_url_id_seq OWNER TO postgres;

--
-- Name: ingrid_tiny_url_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ingrid_tiny_url_id_seq OWNED BY ingrid_tiny_url.id;


--
-- Name: jetspeed_service; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE jetspeed_service (
    id integer NOT NULL,
    application_id integer NOT NULL,
    name character varying(150)
);


ALTER TABLE jetspeed_service OWNER TO postgres;

--
-- Name: language; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE language (
    id integer NOT NULL,
    portlet_id integer NOT NULL,
    locale_string character varying(50) NOT NULL,
    supported_locale smallint NOT NULL,
    title character varying(100),
    short_title character varying(100),
    keywords text
);


ALTER TABLE language OWNER TO postgres;

--
-- Name: link; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE link (
    link_id integer NOT NULL,
    parent_id integer NOT NULL,
    path character varying(240) NOT NULL,
    name character varying(80) NOT NULL,
    version character varying(40),
    title character varying(100),
    short_title character varying(40),
    is_hidden smallint NOT NULL,
    skin character varying(80),
    target character varying(80),
    url character varying(255),
    subsite character varying(40),
    user_principal character varying(40),
    role_principal character varying(40),
    group_principal character varying(40),
    mediatype character varying(15),
    locale character varying(20),
    ext_attr_name character varying(15),
    ext_attr_value character varying(40),
    owner_principal character varying(40)
);


ALTER TABLE link OWNER TO postgres;

--
-- Name: link_constraint; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE link_constraint (
    constraint_id integer NOT NULL,
    link_id integer NOT NULL,
    apply_order integer NOT NULL,
    user_principals_acl character varying(120),
    role_principals_acl character varying(120),
    group_principals_acl character varying(120),
    permissions_acl character varying(120)
);


ALTER TABLE link_constraint OWNER TO postgres;

--
-- Name: link_constraints_ref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE link_constraints_ref (
    constraints_ref_id integer NOT NULL,
    link_id integer NOT NULL,
    apply_order integer NOT NULL,
    name character varying(40) NOT NULL
);


ALTER TABLE link_constraints_ref OWNER TO postgres;

--
-- Name: link_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE link_metadata (
    metadata_id integer NOT NULL,
    link_id integer NOT NULL,
    name character varying(15) NOT NULL,
    locale character varying(20),
    value character varying(100) NOT NULL
);


ALTER TABLE link_metadata OWNER TO postgres;

--
-- Name: locale_encoding_mapping; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE locale_encoding_mapping (
    id integer NOT NULL,
    application_id integer NOT NULL,
    locale_string character varying(50) NOT NULL,
    encoding character varying(50) NOT NULL
);


ALTER TABLE locale_encoding_mapping OWNER TO postgres;

--
-- Name: localized_description; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE localized_description (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    owner_class_name character varying(255) NOT NULL,
    description text NOT NULL,
    locale_string character varying(50) NOT NULL
);


ALTER TABLE localized_description OWNER TO postgres;

--
-- Name: localized_display_name; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE localized_display_name (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    owner_class_name character varying(255),
    display_name text NOT NULL,
    locale_string character varying(50) NOT NULL
);


ALTER TABLE localized_display_name OWNER TO postgres;

--
-- Name: media_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE media_type (
    mediatype_id integer NOT NULL,
    name character varying(80) NOT NULL,
    character_set character varying(40),
    title character varying(80),
    description text
);


ALTER TABLE media_type OWNER TO postgres;

--
-- Name: mediatype_to_capability; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE mediatype_to_capability (
    mediatype_id integer NOT NULL,
    capability_id integer NOT NULL
);


ALTER TABLE mediatype_to_capability OWNER TO postgres;

--
-- Name: mediatype_to_mimetype; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE mediatype_to_mimetype (
    mediatype_id integer NOT NULL,
    mimetype_id integer NOT NULL
);


ALTER TABLE mediatype_to_mimetype OWNER TO postgres;

--
-- Name: mimetype; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE mimetype (
    mimetype_id integer NOT NULL,
    name character varying(80) NOT NULL
);


ALTER TABLE mimetype OWNER TO postgres;

--
-- Name: named_parameter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE named_parameter (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    name character varying(150) NOT NULL
);


ALTER TABLE named_parameter OWNER TO postgres;

--
-- Name: ojb_dlist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_dlist (
    id integer NOT NULL,
    size_ integer
);


ALTER TABLE ojb_dlist OWNER TO postgres;

--
-- Name: ojb_dlist_entries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_dlist_entries (
    id integer NOT NULL,
    dlist_id integer,
    position_ integer,
    oid_ bytea
);


ALTER TABLE ojb_dlist_entries OWNER TO postgres;

--
-- Name: ojb_dmap; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_dmap (
    id integer NOT NULL,
    size_ integer
);


ALTER TABLE ojb_dmap OWNER TO postgres;

--
-- Name: ojb_dset; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_dset (
    id integer NOT NULL,
    size_ integer
);


ALTER TABLE ojb_dset OWNER TO postgres;

--
-- Name: ojb_dset_entries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_dset_entries (
    id integer NOT NULL,
    dlist_id integer,
    position_ integer,
    oid_ bytea
);


ALTER TABLE ojb_dset_entries OWNER TO postgres;

--
-- Name: ojb_hl_seq; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_hl_seq (
    tablename character varying(175) NOT NULL,
    fieldname character varying(70) NOT NULL,
    max_key integer,
    grab_size integer,
    version integer
);


ALTER TABLE ojb_hl_seq OWNER TO postgres;

--
-- Name: ojb_lockentry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_lockentry (
    oid_ character varying(250) NOT NULL,
    tx_id character varying(50) NOT NULL,
    timestamp_ timestamp(3) without time zone,
    isolationlevel integer,
    locktype integer
);


ALTER TABLE ojb_lockentry OWNER TO postgres;

--
-- Name: ojb_nrm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ojb_nrm (
    name character varying(250) NOT NULL,
    oid_ bytea
);


ALTER TABLE ojb_nrm OWNER TO postgres;

--
-- Name: pa_metadata_fields; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE pa_metadata_fields (
    id integer NOT NULL,
    object_id integer NOT NULL,
    column_value text NOT NULL,
    name character varying(100) NOT NULL,
    locale_string character varying(50) NOT NULL
);


ALTER TABLE pa_metadata_fields OWNER TO postgres;

--
-- Name: pa_security_constraint; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE pa_security_constraint (
    id integer NOT NULL,
    application_id integer NOT NULL,
    transport character varying(40) NOT NULL
);


ALTER TABLE pa_security_constraint OWNER TO postgres;

--
-- Name: page; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page (
    page_id integer NOT NULL,
    class_name character varying(100) NOT NULL,
    parent_id integer NOT NULL,
    path character varying(240) NOT NULL,
    content_type character varying(40),
    is_inheritable smallint,
    name character varying(80) NOT NULL,
    version character varying(40),
    title character varying(100),
    short_title character varying(40),
    is_hidden smallint,
    skin character varying(80),
    default_layout_decorator character varying(80),
    default_portlet_decorator character varying(80),
    subsite character varying(40),
    user_principal character varying(40),
    role_principal character varying(40),
    group_principal character varying(40),
    mediatype character varying(15),
    locale character varying(20),
    ext_attr_name character varying(15),
    ext_attr_value character varying(40),
    owner_principal character varying(40)
);


ALTER TABLE page OWNER TO postgres;

--
-- Name: page_constraint; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_constraint (
    constraint_id integer NOT NULL,
    page_id integer NOT NULL,
    apply_order integer NOT NULL,
    user_principals_acl character varying(120),
    role_principals_acl character varying(120),
    group_principals_acl character varying(120),
    permissions_acl character varying(120)
);


ALTER TABLE page_constraint OWNER TO postgres;

--
-- Name: page_constraints_ref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_constraints_ref (
    constraints_ref_id integer NOT NULL,
    page_id integer NOT NULL,
    apply_order integer NOT NULL,
    name character varying(40) NOT NULL
);


ALTER TABLE page_constraints_ref OWNER TO postgres;

--
-- Name: page_menu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_menu (
    menu_id integer NOT NULL,
    class_name character varying(100) NOT NULL,
    parent_id integer,
    page_id integer,
    element_order integer,
    name character varying(100),
    title character varying(100),
    short_title character varying(40),
    text character varying(100),
    options character varying(255),
    depth integer,
    is_paths smallint,
    is_regexp smallint,
    profile character varying(80),
    options_order character varying(255),
    skin character varying(80),
    is_nest smallint
);


ALTER TABLE page_menu OWNER TO postgres;

--
-- Name: page_menu_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_menu_metadata (
    metadata_id integer NOT NULL,
    menu_id integer NOT NULL,
    name character varying(15) NOT NULL,
    locale character varying(20),
    value character varying(100) NOT NULL
);


ALTER TABLE page_menu_metadata OWNER TO postgres;

--
-- Name: page_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_metadata (
    metadata_id integer NOT NULL,
    page_id integer NOT NULL,
    name character varying(15) NOT NULL,
    locale character varying(20),
    value character varying(100) NOT NULL
);


ALTER TABLE page_metadata OWNER TO postgres;

--
-- Name: page_sec_constraint_def; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_sec_constraint_def (
    constraint_def_id integer NOT NULL,
    constraints_def_id integer NOT NULL,
    apply_order integer NOT NULL,
    user_principals_acl character varying(120),
    role_principals_acl character varying(120),
    group_principals_acl character varying(120),
    permissions_acl character varying(120)
);


ALTER TABLE page_sec_constraint_def OWNER TO postgres;

--
-- Name: page_sec_constraints_def; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_sec_constraints_def (
    constraints_def_id integer NOT NULL,
    page_security_id integer NOT NULL,
    name character varying(40) NOT NULL
);


ALTER TABLE page_sec_constraints_def OWNER TO postgres;

--
-- Name: page_sec_constraints_ref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_sec_constraints_ref (
    constraints_ref_id integer NOT NULL,
    page_security_id integer NOT NULL,
    apply_order integer NOT NULL,
    name character varying(40) NOT NULL
);


ALTER TABLE page_sec_constraints_ref OWNER TO postgres;

--
-- Name: page_security; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_security (
    page_security_id integer NOT NULL,
    parent_id integer NOT NULL,
    path character varying(240) NOT NULL,
    name character varying(80) NOT NULL,
    version character varying(40),
    subsite character varying(40),
    user_principal character varying(40),
    role_principal character varying(40),
    group_principal character varying(40),
    mediatype character varying(15),
    locale character varying(20),
    ext_attr_name character varying(15),
    ext_attr_value character varying(40)
);


ALTER TABLE page_security OWNER TO postgres;

--
-- Name: page_statistics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE page_statistics (
    ipaddress character varying(80),
    user_name character varying(80),
    time_stamp timestamp(3) without time zone,
    page character varying(80),
    status integer,
    elapsed_time bigint
);


ALTER TABLE page_statistics OWNER TO postgres;

--
-- Name: parameter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE parameter (
    parameter_id integer NOT NULL,
    owner_id integer NOT NULL,
    owner_class_name character varying(255) NOT NULL,
    name character varying(80) NOT NULL,
    parameter_value text
);


ALTER TABLE parameter OWNER TO postgres;

--
-- Name: parameter_alias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE parameter_alias (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    local_part character varying(80) NOT NULL,
    namespace character varying(80),
    prefix character varying(20)
);


ALTER TABLE parameter_alias OWNER TO postgres;

--
-- Name: pd_metadata_fields; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE pd_metadata_fields (
    id integer NOT NULL,
    object_id integer NOT NULL,
    column_value text NOT NULL,
    name character varying(100) NOT NULL,
    locale_string character varying(50) NOT NULL
);


ALTER TABLE pd_metadata_fields OWNER TO postgres;

--
-- Name: portlet_application; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_application (
    application_id integer NOT NULL,
    app_name character varying(80) NOT NULL,
    context_path character varying(255) NOT NULL,
    revision integer NOT NULL,
    version character varying(80),
    app_type integer,
    checksum character varying(80),
    security_ref character varying(40),
    default_namespace character varying(120),
    resource_bundle character varying(255)
);


ALTER TABLE portlet_application OWNER TO postgres;

--
-- Name: portlet_definition; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_definition (
    id integer NOT NULL,
    name character varying(80),
    class_name character varying(255),
    application_id integer NOT NULL,
    expiration_cache integer,
    resource_bundle character varying(255),
    preference_validator character varying(255),
    security_ref character varying(40),
    cache_scope character varying(30),
    clone_parent character varying(80)
);


ALTER TABLE portlet_definition OWNER TO postgres;

--
-- Name: portlet_filter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_filter (
    id integer NOT NULL,
    application_id integer NOT NULL,
    filter_name character varying(80) NOT NULL,
    filter_class character varying(255)
);


ALTER TABLE portlet_filter OWNER TO postgres;

--
-- Name: portlet_listener; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_listener (
    id integer NOT NULL,
    application_id integer NOT NULL,
    listener_class character varying(255)
);


ALTER TABLE portlet_listener OWNER TO postgres;

--
-- Name: portlet_preference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_preference (
    id integer NOT NULL,
    dtype character varying(10) NOT NULL,
    application_name character varying(80) NOT NULL,
    portlet_name character varying(80) NOT NULL,
    entity_id character varying(80),
    user_name character varying(80),
    name character varying(254) NOT NULL,
    readonly smallint NOT NULL
);


ALTER TABLE portlet_preference OWNER TO postgres;

--
-- Name: portlet_preference_value; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_preference_value (
    id integer NOT NULL,
    pref_id integer NOT NULL,
    idx smallint NOT NULL,
    pref_value character varying(4000)
);


ALTER TABLE portlet_preference_value OWNER TO postgres;

--
-- Name: portlet_statistics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_statistics (
    ipaddress character varying(80),
    user_name character varying(80),
    time_stamp timestamp(3) without time zone,
    page character varying(80),
    portlet character varying(255),
    status integer,
    elapsed_time bigint
);


ALTER TABLE portlet_statistics OWNER TO postgres;

--
-- Name: portlet_supports; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE portlet_supports (
    supports_id integer NOT NULL,
    portlet_id integer NOT NULL,
    mime_type character varying(30) NOT NULL,
    modes character varying(255),
    states character varying(255)
);


ALTER TABLE portlet_supports OWNER TO postgres;

--
-- Name: principal_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE principal_permission (
    principal_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE principal_permission OWNER TO postgres;

--
-- Name: principal_rule_assoc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE principal_rule_assoc (
    principal_name character varying(80) NOT NULL,
    locator_name character varying(80) NOT NULL,
    rule_id character varying(80) NOT NULL
);


ALTER TABLE principal_rule_assoc OWNER TO postgres;

--
-- Name: processing_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE processing_event (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    local_part character varying(80) NOT NULL,
    namespace character varying(80),
    prefix character varying(20)
);


ALTER TABLE processing_event OWNER TO postgres;

--
-- Name: profile_page_assoc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE profile_page_assoc (
    locator_hash character varying(40) NOT NULL,
    page_id character varying(80) NOT NULL
);


ALTER TABLE profile_page_assoc OWNER TO postgres;

--
-- Name: profiling_rule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE profiling_rule (
    rule_id character varying(80) NOT NULL,
    class_name character varying(100) NOT NULL,
    title character varying(100)
);


ALTER TABLE profiling_rule OWNER TO postgres;

--
-- Name: public_parameter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public_parameter (
    id integer NOT NULL,
    application_id integer NOT NULL,
    local_part character varying(80) NOT NULL,
    namespace character varying(80),
    prefix character varying(20),
    identifier character varying(150) NOT NULL
);


ALTER TABLE public_parameter OWNER TO postgres;

--
-- Name: publishing_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE publishing_event (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    local_part character varying(80) NOT NULL,
    namespace character varying(80),
    prefix character varying(20)
);


ALTER TABLE publishing_event OWNER TO postgres;

--
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_blob_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    blob_data bytea
);


ALTER TABLE qrtz_blob_triggers OWNER TO postgres;

--
-- Name: qrtz_calendars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_calendars (
    calendar_name character varying(80) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE qrtz_calendars OWNER TO postgres;

--
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_cron_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    cron_expression character varying(80) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE qrtz_cron_triggers OWNER TO postgres;

--
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_fired_triggers (
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    is_volatile character(1) NOT NULL,
    instance_name character varying(80) NOT NULL,
    fired_time bigint DEFAULT '0'::bigint NOT NULL,
    priority integer DEFAULT 0 NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(80),
    job_group character varying(80),
    is_stateful character(1),
    requests_recovery character(1)
);


ALTER TABLE qrtz_fired_triggers OWNER TO postgres;

--
-- Name: qrtz_job_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_job_details (
    job_name character varying(80) NOT NULL,
    job_group character varying(80) NOT NULL,
    description character varying(120),
    job_class_name character varying(128) NOT NULL,
    is_durable character(1) NOT NULL,
    is_volatile character(1) NOT NULL,
    is_stateful character(1) NOT NULL,
    requests_recovery character(1) NOT NULL,
    job_data bytea
);


ALTER TABLE qrtz_job_details OWNER TO postgres;

--
-- Name: qrtz_job_listeners; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_job_listeners (
    job_name character varying(80) NOT NULL,
    job_group character varying(80) NOT NULL,
    job_listener character varying(80) NOT NULL
);


ALTER TABLE qrtz_job_listeners OWNER TO postgres;

--
-- Name: qrtz_locks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_locks (
    lock_name character varying(40) NOT NULL
);


ALTER TABLE qrtz_locks OWNER TO postgres;

--
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_paused_trigger_grps (
    trigger_group character varying(80) NOT NULL
);


ALTER TABLE qrtz_paused_trigger_grps OWNER TO postgres;

--
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_scheduler_state (
    instance_name character varying(80) NOT NULL,
    last_checkin_time bigint DEFAULT '0'::bigint NOT NULL,
    checkin_interval bigint DEFAULT '0'::bigint NOT NULL
);


ALTER TABLE qrtz_scheduler_state OWNER TO postgres;

--
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_simple_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    repeat_count bigint DEFAULT '0'::bigint NOT NULL,
    repeat_interval bigint DEFAULT '0'::bigint NOT NULL,
    times_triggered bigint DEFAULT '0'::bigint NOT NULL
);


ALTER TABLE qrtz_simple_triggers OWNER TO postgres;

--
-- Name: qrtz_trigger_listeners; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_trigger_listeners (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    trigger_listener character varying(80) NOT NULL
);


ALTER TABLE qrtz_trigger_listeners OWNER TO postgres;

--
-- Name: qrtz_triggers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE qrtz_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    job_name character varying(80) NOT NULL,
    job_group character varying(80) NOT NULL,
    is_volatile character(1) NOT NULL,
    description character varying(120),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint DEFAULT '0'::bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(80),
    misfire_instr smallint,
    job_data bytea
);


ALTER TABLE qrtz_triggers OWNER TO postgres;

--
-- Name: rule_criterion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE rule_criterion (
    criterion_id character varying(80) NOT NULL,
    rule_id character varying(80) NOT NULL,
    fallback_order integer NOT NULL,
    request_type character varying(40) NOT NULL,
    name character varying(80) NOT NULL,
    column_value character varying(128),
    fallback_type integer DEFAULT 1
);


ALTER TABLE rule_criterion OWNER TO postgres;

--
-- Name: runtime_option; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE runtime_option (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    owner_class_name character varying(255) NOT NULL,
    name character varying(150) NOT NULL
);


ALTER TABLE runtime_option OWNER TO postgres;

--
-- Name: runtime_value; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE runtime_value (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    rvalue character varying(200) NOT NULL
);


ALTER TABLE runtime_value OWNER TO postgres;

--
-- Name: secured_portlet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE secured_portlet (
    id integer NOT NULL,
    owner_id integer NOT NULL,
    name character varying(150) NOT NULL
);


ALTER TABLE secured_portlet OWNER TO postgres;

--
-- Name: security_attribute; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_attribute (
    attr_id integer NOT NULL,
    principal_id integer NOT NULL,
    attr_name character varying(200) NOT NULL,
    attr_value character varying(1000)
);


ALTER TABLE security_attribute OWNER TO postgres;

--
-- Name: security_credential; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_credential (
    credential_id integer NOT NULL,
    principal_id integer NOT NULL,
    credential_value character varying(254),
    type smallint NOT NULL,
    update_allowed smallint NOT NULL,
    is_state_readonly smallint NOT NULL,
    update_required smallint NOT NULL,
    is_encoded smallint NOT NULL,
    is_enabled smallint NOT NULL,
    auth_failures smallint NOT NULL,
    is_expired smallint NOT NULL,
    creation_date timestamp(3) without time zone NOT NULL,
    modified_date timestamp(3) without time zone NOT NULL,
    prev_auth_date timestamp(3) without time zone,
    last_auth_date timestamp(3) without time zone,
    expiration_date date
);


ALTER TABLE security_credential OWNER TO postgres;

--
-- Name: security_domain; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_domain (
    domain_id integer NOT NULL,
    domain_name character varying(254),
    remote smallint DEFAULT '0'::smallint,
    enabled smallint DEFAULT '1'::smallint,
    owner_domain_id integer
);


ALTER TABLE security_domain OWNER TO postgres;

--
-- Name: security_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_permission (
    permission_id integer NOT NULL,
    permission_type character varying(30) NOT NULL,
    name character varying(254) NOT NULL,
    actions character varying(254) NOT NULL
);


ALTER TABLE security_permission OWNER TO postgres;

--
-- Name: security_principal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_principal (
    principal_id integer NOT NULL,
    principal_type character varying(20) NOT NULL,
    principal_name character varying(200) NOT NULL,
    is_mapped smallint NOT NULL,
    is_enabled smallint NOT NULL,
    is_readonly smallint NOT NULL,
    is_removable smallint NOT NULL,
    creation_date timestamp(3) without time zone NOT NULL,
    modified_date timestamp(3) without time zone NOT NULL,
    domain_id integer NOT NULL
);


ALTER TABLE security_principal OWNER TO postgres;

--
-- Name: security_principal_assoc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_principal_assoc (
    assoc_name character varying(30) NOT NULL,
    from_principal_id integer NOT NULL,
    to_principal_id integer NOT NULL
);


ALTER TABLE security_principal_assoc OWNER TO postgres;

--
-- Name: security_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_role (
    id integer NOT NULL,
    application_id integer NOT NULL,
    name character varying(150) NOT NULL
);


ALTER TABLE security_role OWNER TO postgres;

--
-- Name: security_role_reference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE security_role_reference (
    id integer NOT NULL,
    portlet_definition_id integer NOT NULL,
    role_name character varying(150) NOT NULL,
    role_link character varying(150)
);


ALTER TABLE security_role_reference OWNER TO postgres;

--
-- Name: sso_site; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sso_site (
    site_id integer NOT NULL,
    name character varying(254) NOT NULL,
    url character varying(254) NOT NULL,
    allow_user_set smallint DEFAULT '0'::smallint,
    requires_certificate smallint DEFAULT '0'::smallint,
    challenge_response_auth smallint DEFAULT '0'::smallint,
    form_auth smallint DEFAULT '0'::smallint,
    form_user_field character varying(128),
    form_pwd_field character varying(128),
    realm character varying(128),
    domain_id integer NOT NULL
);


ALTER TABLE sso_site OWNER TO postgres;

--
-- Name: user_activity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_activity (
    activity character varying(40),
    category character varying(40),
    user_name character varying(80),
    time_stamp timestamp(3) without time zone,
    ipaddress character varying(80),
    attr_name character varying(200),
    attr_value_before character varying(1000),
    attr_value_after character varying(1000),
    description character varying(128)
);


ALTER TABLE user_activity OWNER TO postgres;

--
-- Name: user_attribute; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_attribute (
    id integer NOT NULL,
    application_id integer NOT NULL,
    name character varying(150)
);


ALTER TABLE user_attribute OWNER TO postgres;

--
-- Name: user_attribute_ref; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_attribute_ref (
    id integer NOT NULL,
    application_id integer NOT NULL,
    name character varying(150),
    name_link character varying(150)
);


ALTER TABLE user_attribute_ref OWNER TO postgres;

--
-- Name: user_statistics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_statistics (
    ipaddress character varying(80),
    user_name character varying(80),
    time_stamp timestamp(3) without time zone,
    status integer,
    elapsed_time bigint
);


ALTER TABLE user_statistics OWNER TO postgres;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_anniversary ALTER COLUMN id SET DEFAULT nextval('ingrid_anniversary_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_chron_eventtypes ALTER COLUMN id SET DEFAULT nextval('ingrid_chron_eventtypes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_cms ALTER COLUMN id SET DEFAULT nextval('ingrid_cms_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_cms_item ALTER COLUMN id SET DEFAULT nextval('ingrid_cms_item_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_env_topic ALTER COLUMN id SET DEFAULT nextval('ingrid_env_topic_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_measures_rubric ALTER COLUMN id SET DEFAULT nextval('ingrid_measures_rubric_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_newsletter_data ALTER COLUMN id SET DEFAULT nextval('ingrid_newsletter_data_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_partner ALTER COLUMN id SET DEFAULT nextval('ingrid_partner_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_principal_pref ALTER COLUMN id SET DEFAULT nextval('ingrid_principal_pref_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_provider ALTER COLUMN id SET DEFAULT nextval('ingrid_provider_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_rss_source ALTER COLUMN id SET DEFAULT nextval('ingrid_rss_source_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_service_rubric ALTER COLUMN id SET DEFAULT nextval('ingrid_service_rubric_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_tiny_url ALTER COLUMN id SET DEFAULT nextval('ingrid_tiny_url_id_seq'::regclass);


--
-- Data for Name: admin_activity; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: capability; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO capability VALUES (1, 'HTML_3_2');
INSERT INTO capability VALUES (2, 'HTML_4_0');
INSERT INTO capability VALUES (3, 'HTML_ACTIVEX');
INSERT INTO capability VALUES (4, 'HTML_CSS1');
INSERT INTO capability VALUES (5, 'HTML_CSS2');
INSERT INTO capability VALUES (6, 'HTML_CSSP');
INSERT INTO capability VALUES (7, 'HTML_DOM');
INSERT INTO capability VALUES (8, 'HTML_DOM_1');
INSERT INTO capability VALUES (9, 'HTML_DOM_2');
INSERT INTO capability VALUES (10, 'HTML_DOM_IE');
INSERT INTO capability VALUES (11, 'HTML_DOM_NS4');
INSERT INTO capability VALUES (12, 'HTML_FORM');
INSERT INTO capability VALUES (13, 'HTML_FRAME');
INSERT INTO capability VALUES (14, 'HTML_IFRAME');
INSERT INTO capability VALUES (15, 'HTML_IMAGE');
INSERT INTO capability VALUES (16, 'HTML_JAVA');
INSERT INTO capability VALUES (17, 'HTML_JAVA1_0');
INSERT INTO capability VALUES (18, 'HTML_JAVA1_1');
INSERT INTO capability VALUES (19, 'HTML_JAVA1_2');
INSERT INTO capability VALUES (20, 'HTML_JAVASCRIPT');
INSERT INTO capability VALUES (21, 'HTML_JAVASCRIPT_1_0');
INSERT INTO capability VALUES (22, 'HTML_JAVASCRIPT_1_1');
INSERT INTO capability VALUES (23, 'HTML_JAVASCRIPT_1_2');
INSERT INTO capability VALUES (24, 'HTML_JAVA_JRE');
INSERT INTO capability VALUES (25, 'HTML_JSCRIPT');
INSERT INTO capability VALUES (26, 'HTML_JSCRIPT1_0');
INSERT INTO capability VALUES (27, 'HTML_JSCRIPT1_1');
INSERT INTO capability VALUES (28, 'HTML_JSCRIPT1_2');
INSERT INTO capability VALUES (29, 'HTML_LAYER');
INSERT INTO capability VALUES (30, 'HTML_NESTED_TABLE');
INSERT INTO capability VALUES (31, 'HTML_PLUGIN');
INSERT INTO capability VALUES (32, 'HTML_PLUGIN_');
INSERT INTO capability VALUES (33, 'HTML_TABLE');
INSERT INTO capability VALUES (34, 'HTML_XML');
INSERT INTO capability VALUES (35, 'HTML_XSL');
INSERT INTO capability VALUES (36, 'HTTP_1_1');
INSERT INTO capability VALUES (37, 'HTTP_COOKIE');
INSERT INTO capability VALUES (38, 'WML_1_0');
INSERT INTO capability VALUES (39, 'WML_1_1');
INSERT INTO capability VALUES (40, 'WML_TABLE');
INSERT INTO capability VALUES (41, 'XML_XINCLUDE');
INSERT INTO capability VALUES (42, 'XML_XPATH');
INSERT INTO capability VALUES (43, 'XML_XSLT');


--
-- Data for Name: client; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO client VALUES (1, 1, 'ie5mac', '.*MSIE 5.*Mac.*', 'Microsoft', 'None', '5.*', 2);
INSERT INTO client VALUES (2, 2, 'safari', '.*Mac.*Safari.*', 'Apple', 'None', '5.*', 2);
INSERT INTO client VALUES (3, 3, 'ie6', '.*MSIE 6.*', 'Microsoft', 'None', '6.0', 2);
INSERT INTO client VALUES (4, 4, 'ie5', '.*MSIE 5.*', 'Microsoft', 'None', '5.5', 2);
INSERT INTO client VALUES (5, 5, 'ns4', '.*Mozilla/4.*', 'Netscape', 'None', '4.75', 2);
INSERT INTO client VALUES (6, 6, 'mozilla', '.*Mozilla/5.*', 'Mozilla', 'Mozilla', '1.x', 2);
INSERT INTO client VALUES (7, 7, 'lynx', 'Lynx.*', 'GNU', 'None', '', 2);
INSERT INTO client VALUES (8, 8, 'nokia_generic', 'Nokia.*', 'Nokia', 'Generic', '', 3);
INSERT INTO client VALUES (9, 9, 'xhtml-basic', 'DoCoMo/2.0.*|KDDI-.*UP.Browser.*|J-PHONE/5.0.*|Vodafone/1.0/.*', 'WAP', 'Generic', '', 1);
INSERT INTO client VALUES (10, 10, 'up', 'UP.*|.*UP.Browser.*', 'United Planet', 'Generic', '', 3);
INSERT INTO client VALUES (11, 11, 'sonyericsson', 'Ercis.*|SonyE.*', 'SonyEricsson', 'Generic', '', 3);
INSERT INTO client VALUES (12, 12, 'wapalizer', 'Wapalizer.*', 'Wapalizer', 'Generic', '', 3);
INSERT INTO client VALUES (13, 13, 'klondike', 'Klondike.*', 'Klondike', 'Generic', '', 3);
INSERT INTO client VALUES (14, 14, 'wml_generic', '.*WML.*|.*WAP.*|.*Wap.*|.*wml.*', 'Generic', 'Generic', '', 3);
INSERT INTO client VALUES (15, 15, 'vxml_generic', '.*VoiceXML.*', 'Generic', 'Generic', '', 4);
INSERT INTO client VALUES (16, 16, 'nuance', 'Nuance.*', 'Nuance', 'Generic', '', 4);
INSERT INTO client VALUES (17, 17, 'agentxml', 'agentxml/1.0.*', 'Unknown', 'Generic', '', 6);
INSERT INTO client VALUES (18, 18, 'opera7', '.*Opera/7.*', 'Opera', 'Opera7', '7.x', 2);


--
-- Data for Name: client_to_capability; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO client_to_capability VALUES (1, 1);
INSERT INTO client_to_capability VALUES (1, 4);
INSERT INTO client_to_capability VALUES (1, 11);
INSERT INTO client_to_capability VALUES (1, 12);
INSERT INTO client_to_capability VALUES (1, 13);
INSERT INTO client_to_capability VALUES (1, 15);
INSERT INTO client_to_capability VALUES (1, 16);
INSERT INTO client_to_capability VALUES (1, 20);
INSERT INTO client_to_capability VALUES (1, 31);
INSERT INTO client_to_capability VALUES (1, 33);
INSERT INTO client_to_capability VALUES (1, 37);
INSERT INTO client_to_capability VALUES (2, 1);
INSERT INTO client_to_capability VALUES (2, 3);
INSERT INTO client_to_capability VALUES (2, 4);
INSERT INTO client_to_capability VALUES (2, 5);
INSERT INTO client_to_capability VALUES (2, 6);
INSERT INTO client_to_capability VALUES (2, 10);
INSERT INTO client_to_capability VALUES (2, 12);
INSERT INTO client_to_capability VALUES (2, 13);
INSERT INTO client_to_capability VALUES (2, 14);
INSERT INTO client_to_capability VALUES (2, 15);
INSERT INTO client_to_capability VALUES (2, 16);
INSERT INTO client_to_capability VALUES (2, 20);
INSERT INTO client_to_capability VALUES (2, 30);
INSERT INTO client_to_capability VALUES (2, 33);
INSERT INTO client_to_capability VALUES (2, 37);
INSERT INTO client_to_capability VALUES (3, 1);
INSERT INTO client_to_capability VALUES (3, 3);
INSERT INTO client_to_capability VALUES (3, 4);
INSERT INTO client_to_capability VALUES (3, 5);
INSERT INTO client_to_capability VALUES (3, 6);
INSERT INTO client_to_capability VALUES (3, 10);
INSERT INTO client_to_capability VALUES (3, 12);
INSERT INTO client_to_capability VALUES (3, 13);
INSERT INTO client_to_capability VALUES (3, 14);
INSERT INTO client_to_capability VALUES (3, 15);
INSERT INTO client_to_capability VALUES (3, 16);
INSERT INTO client_to_capability VALUES (3, 20);
INSERT INTO client_to_capability VALUES (3, 30);
INSERT INTO client_to_capability VALUES (3, 33);
INSERT INTO client_to_capability VALUES (3, 37);
INSERT INTO client_to_capability VALUES (4, 1);
INSERT INTO client_to_capability VALUES (4, 3);
INSERT INTO client_to_capability VALUES (4, 4);
INSERT INTO client_to_capability VALUES (4, 5);
INSERT INTO client_to_capability VALUES (4, 6);
INSERT INTO client_to_capability VALUES (4, 10);
INSERT INTO client_to_capability VALUES (4, 12);
INSERT INTO client_to_capability VALUES (4, 13);
INSERT INTO client_to_capability VALUES (4, 14);
INSERT INTO client_to_capability VALUES (4, 15);
INSERT INTO client_to_capability VALUES (4, 16);
INSERT INTO client_to_capability VALUES (4, 20);
INSERT INTO client_to_capability VALUES (4, 30);
INSERT INTO client_to_capability VALUES (4, 33);
INSERT INTO client_to_capability VALUES (4, 37);
INSERT INTO client_to_capability VALUES (5, 1);
INSERT INTO client_to_capability VALUES (5, 4);
INSERT INTO client_to_capability VALUES (5, 11);
INSERT INTO client_to_capability VALUES (5, 12);
INSERT INTO client_to_capability VALUES (5, 13);
INSERT INTO client_to_capability VALUES (5, 15);
INSERT INTO client_to_capability VALUES (5, 16);
INSERT INTO client_to_capability VALUES (5, 20);
INSERT INTO client_to_capability VALUES (5, 29);
INSERT INTO client_to_capability VALUES (5, 31);
INSERT INTO client_to_capability VALUES (5, 33);
INSERT INTO client_to_capability VALUES (5, 37);
INSERT INTO client_to_capability VALUES (6, 1);
INSERT INTO client_to_capability VALUES (6, 2);
INSERT INTO client_to_capability VALUES (6, 4);
INSERT INTO client_to_capability VALUES (6, 5);
INSERT INTO client_to_capability VALUES (6, 6);
INSERT INTO client_to_capability VALUES (6, 8);
INSERT INTO client_to_capability VALUES (6, 12);
INSERT INTO client_to_capability VALUES (6, 13);
INSERT INTO client_to_capability VALUES (6, 14);
INSERT INTO client_to_capability VALUES (6, 15);
INSERT INTO client_to_capability VALUES (6, 16);
INSERT INTO client_to_capability VALUES (6, 20);
INSERT INTO client_to_capability VALUES (6, 24);
INSERT INTO client_to_capability VALUES (6, 30);
INSERT INTO client_to_capability VALUES (6, 31);
INSERT INTO client_to_capability VALUES (6, 33);
INSERT INTO client_to_capability VALUES (6, 37);
INSERT INTO client_to_capability VALUES (7, 12);
INSERT INTO client_to_capability VALUES (7, 13);
INSERT INTO client_to_capability VALUES (7, 30);
INSERT INTO client_to_capability VALUES (7, 33);
INSERT INTO client_to_capability VALUES (7, 37);
INSERT INTO client_to_capability VALUES (18, 1);
INSERT INTO client_to_capability VALUES (18, 2);
INSERT INTO client_to_capability VALUES (18, 4);
INSERT INTO client_to_capability VALUES (18, 5);
INSERT INTO client_to_capability VALUES (18, 6);
INSERT INTO client_to_capability VALUES (18, 8);
INSERT INTO client_to_capability VALUES (18, 12);
INSERT INTO client_to_capability VALUES (18, 13);
INSERT INTO client_to_capability VALUES (18, 14);
INSERT INTO client_to_capability VALUES (18, 15);
INSERT INTO client_to_capability VALUES (18, 16);
INSERT INTO client_to_capability VALUES (18, 20);
INSERT INTO client_to_capability VALUES (18, 24);
INSERT INTO client_to_capability VALUES (18, 30);
INSERT INTO client_to_capability VALUES (18, 31);
INSERT INTO client_to_capability VALUES (18, 33);
INSERT INTO client_to_capability VALUES (18, 37);


--
-- Data for Name: client_to_mimetype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO client_to_mimetype VALUES (1, 2);
INSERT INTO client_to_mimetype VALUES (2, 2);
INSERT INTO client_to_mimetype VALUES (2, 5);
INSERT INTO client_to_mimetype VALUES (2, 6);
INSERT INTO client_to_mimetype VALUES (3, 2);
INSERT INTO client_to_mimetype VALUES (3, 5);
INSERT INTO client_to_mimetype VALUES (3, 6);
INSERT INTO client_to_mimetype VALUES (4, 2);
INSERT INTO client_to_mimetype VALUES (4, 6);
INSERT INTO client_to_mimetype VALUES (5, 2);
INSERT INTO client_to_mimetype VALUES (6, 2);
INSERT INTO client_to_mimetype VALUES (6, 5);
INSERT INTO client_to_mimetype VALUES (6, 6);
INSERT INTO client_to_mimetype VALUES (7, 2);
INSERT INTO client_to_mimetype VALUES (8, 3);
INSERT INTO client_to_mimetype VALUES (9, 1);
INSERT INTO client_to_mimetype VALUES (10, 3);
INSERT INTO client_to_mimetype VALUES (11, 3);
INSERT INTO client_to_mimetype VALUES (12, 3);
INSERT INTO client_to_mimetype VALUES (13, 3);
INSERT INTO client_to_mimetype VALUES (14, 3);
INSERT INTO client_to_mimetype VALUES (15, 4);
INSERT INTO client_to_mimetype VALUES (16, 4);
INSERT INTO client_to_mimetype VALUES (17, 6);
INSERT INTO client_to_mimetype VALUES (18, 2);
INSERT INTO client_to_mimetype VALUES (18, 5);
INSERT INTO client_to_mimetype VALUES (18, 6);


--
-- Data for Name: clubs; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: custom_portlet_mode; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: custom_window_state; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: event_alias; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: event_definition; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: filter_lifecycle; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: filter_mapping; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: filtered_portlet; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: folder; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO folder VALUES (1, NULL, '/', '/', 'Root Folder', 'Root Folder', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (2, 1, '/_role', '_role', 'Role', 'Role', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (3, 2, '/_role/user', 'user', 'User', 'User', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (4, 1, '/_user', '_user', 'User', 'User', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (701, 4, '/_user/template', 'template', 'Template', 'Template', 0, NULL, NULL, NULL, NULL, NULL, 'template', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (771, 1, '/administration', 'administration', 'ingrid.page.administration', 'ingrid.page.administration', 0, NULL, NULL, NULL, 'admin-cms.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (772, 1, '/application', 'application', 'ingrid.page.application', 'ingrid.page.application', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (773, 1, '/cms', 'cms', 'ingrid.page.cms', 'ingrid.page.cms', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (774, 1, '/mdek', 'mdek', 'ingrid.page.mdek', 'ingrid.page.mdek', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (775, 1, '/search-catalog', 'search-catalog', 'Search Catalog', 'Search Catalog', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder VALUES (776, 1, '/search-extended', 'search-extended', 'Search Extended', 'Search Extended', 0, NULL, NULL, NULL, 'search-ext-env-topic-terms.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


--
-- Data for Name: folder_constraint; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO folder_constraint VALUES (1, 774, 0, NULL, 'mdek', NULL, 'view');


--
-- Data for Name: folder_constraints_ref; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO folder_constraints_ref VALUES (1, 1, 0, 'public-view');
INSERT INTO folder_constraints_ref VALUES (2, 771, 0, 'admin');
INSERT INTO folder_constraints_ref VALUES (3, 771, 1, 'admin-portal');
INSERT INTO folder_constraints_ref VALUES (4, 771, 2, 'admin-partner');
INSERT INTO folder_constraints_ref VALUES (5, 771, 3, 'admin-provider');
INSERT INTO folder_constraints_ref VALUES (6, 774, 0, 'admin-portal');


--
-- Data for Name: folder_menu; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO folder_menu VALUES (1, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'footer-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (2, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 3, NULL, NULL, NULL, NULL, '/disclaimer.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (3, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 1, NULL, 1, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (5, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 1, NULL, 3, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (7, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 1, NULL, 5, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (9, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'main-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (10, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 0, NULL, NULL, NULL, NULL, '/main-search.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (12, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 1, NULL, NULL, NULL, NULL, '/main-measures.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (13, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 3, NULL, NULL, NULL, NULL, '/main-service.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (14, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 2, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (15, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 3, NULL, NULL, NULL, NULL, '/main-maps.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (16, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 4, NULL, NULL, NULL, NULL, '/main-chronicle.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (17, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 5, NULL, NULL, NULL, NULL, '/main-about.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (18, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 6, NULL, NULL, NULL, NULL, '/application/main-application.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (19, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 2, NULL, NULL, NULL, NULL, '/administration', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (20, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 10, NULL, NULL, NULL, NULL, '/Administrative', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (21, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 9, NULL, NULL, NULL, NULL, '/mdek', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (22, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'service-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (24, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 1, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (25, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 1, NULL, NULL, NULL, NULL, '/service-myportal.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (26, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 3, NULL, NULL, NULL, 'separator2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (27, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 2, NULL, NULL, NULL, NULL, '/service-sitemap.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (28, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 5, NULL, NULL, NULL, 'separator2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (29, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 0, NULL, NULL, NULL, NULL, '/help.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (30, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 7, NULL, NULL, NULL, 'separator2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (31, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 1, NULL, NULL, NULL, NULL, '/service-contact.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (32, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 9, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (33, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 0, NULL, NULL, NULL, NULL, '/language.link', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (34, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-about', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (36, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 0, NULL, NULL, NULL, NULL, '/main-about.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (37, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 1, NULL, NULL, NULL, NULL, '/main-about-partner.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (38, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 2, NULL, NULL, NULL, NULL, '/main-about-data-source.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (44, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-catalog', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (45, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 44, NULL, 0, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (47, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-search', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (51, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 7, NULL, NULL, NULL, NULL, '/cms/cms-1.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (52, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 8, NULL, NULL, NULL, NULL, '/cms/cms-2.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (53, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (54, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 0, NULL, NULL, NULL, NULL, '/administration/admin-usermanagement.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (55, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 1, NULL, NULL, NULL, NULL, '/administration/admin-homepage.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (56, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 2, NULL, NULL, NULL, NULL, '/administration/admin-content-rss.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (57, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 3, NULL, NULL, NULL, NULL, '/administration/admin-content-partner.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (58, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 4, NULL, NULL, NULL, NULL, '/administration/admin-content-provider.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (59, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 5, NULL, NULL, NULL, NULL, '/administration/admin-iplugs.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (60, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 6, NULL, NULL, NULL, NULL, '/administration/admin-cms.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (62, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 7, NULL, NULL, NULL, NULL, '/administration/admin-statistics.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (63, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 8, NULL, NULL, NULL, NULL, '/administration/admin-portal-profile.psml', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu VALUES (64, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 9, NULL, NULL, NULL, NULL, '/administration/admin-component-monitor.psml', 0, 0, 0, NULL, NULL, NULL, NULL);


--
-- Data for Name: folder_menu_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: folder_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO folder_metadata VALUES (1, 1, 'title', 'es,,', 'Carpeta raiz');
INSERT INTO folder_metadata VALUES (2, 1, 'title', 'fr,,', 'Répertoire racine');


--
-- Data for Name: folder_order; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: fragment; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fragment VALUES (1, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 1, '1', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (2, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9701', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (3, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9702', NULL, 'ingrid-portal-apps::EnvironmentTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 4, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (4, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9703', NULL, 'ingrid-portal-apps::RssNewsTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 3, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (5, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9704', NULL, 'ingrid-portal-apps::IngridInformPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (11, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 2, '9', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (12, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 11, NULL, '10', NULL, 'ingrid-portal-apps::DetectJavaScriptPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (14, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 3, '11', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (15, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 14, NULL, '12', NULL, 'ingrid-portal-apps::CMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (18, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 4, '14', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (19, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 18, NULL, '15', NULL, 'ingrid-portal-apps::HelpPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (21, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 5, '11060', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (22, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 21, NULL, '11061', NULL, 'ingrid-portal-apps::ShowDataSourcePortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (24, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 6, '16', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (25, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 24, NULL, '17', NULL, 'ingrid-portal-apps::ShowPartnerPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (27, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 7, '18', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (28, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 27, NULL, '19', NULL, 'ingrid-portal-apps::CMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (32, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 8, '22', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (33, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 32, NULL, '23', NULL, 'ingrid-portal-apps::ChronicleSearch', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (34, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 32, NULL, '24', NULL, 'ingrid-portal-apps::ChronicleResult', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (45, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 11, '30', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (46, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 45, NULL, '31', NULL, 'ingrid-portal-apps::ShowMapsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (48, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 12, '32', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (49, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 48, NULL, '33', NULL, 'ingrid-portal-apps::MeasuresSearch', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (53, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 13, '36', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (55, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 53, NULL, '38', NULL, 'ingrid-portal-apps::SearchSimpleResult', NULL, NULL, 'portlet', NULL, 'ingrid-clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (57, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 53, NULL, '40', NULL, 'ingrid-portal-apps::SearchSimilar', NULL, NULL, 'portlet', NULL, 'ingrid-clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (59, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 53, NULL, '42', NULL, 'ingrid-portal-apps::SearchResult', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (66, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 15, '47', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (67, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 66, NULL, '48', NULL, 'ingrid-portal-apps::MyPortalCreateAccountPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (70, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 16, '50', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (71, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 70, NULL, '51', NULL, 'ingrid-portal-apps::MyPortalPasswordForgottenPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (77, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 18, '55', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (78, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 77, NULL, '56', NULL, 'ingrid-portal-apps::RssNews', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (80, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 19, '57', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (81, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 80, NULL, '58', NULL, 'ingrid-portal-apps::SearchDetail', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (105, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 25, '76', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (106, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 105, NULL, '9723', NULL, 'ingrid-portal-apps::Contact', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (109, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 26, '79', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (110, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 109, NULL, '80', NULL, 'ingrid-portal-apps::MyPortalLoginPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (113, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 27, '82', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (114, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 113, NULL, '83', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (116, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 28, '84', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (117, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9709', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (118, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9710', NULL, 'ingrid-portal-apps::EnvironmentTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 4, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (119, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9711', NULL, 'ingrid-portal-apps::RssNewsTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 3, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (120, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9712', NULL, 'ingrid-portal-apps::IngridInformPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (125, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 29, '92', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (126, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 125, NULL, '9725', NULL, 'ingrid-portal-apps::MyPortalEditAccountPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (140, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 31, '105', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (141, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 140, NULL, '106', NULL, 'ingrid-portal-apps::MyPortalOverviewPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (6507, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 728, '108', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (6508, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9716', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (6509, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9717', NULL, 'ingrid-portal-apps::EnvironmentTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 4, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (6510, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9718', NULL, 'ingrid-portal-apps::RssNewsTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 3, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (6511, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9719', NULL, 'ingrid-portal-apps::IngridInformPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7138, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 798, '116', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7139, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7138, NULL, '117', NULL, 'ingrid-portal-apps::AdminCMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7141, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 799, '1278', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7142, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7141, NULL, '1279', NULL, 'ingrid-portal-apps::AdminComponentMonitorPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7144, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 800, '118', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7145, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7144, NULL, '119', NULL, 'ingrid-portal-apps::ContentPartnerPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7147, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 801, '120', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7148, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7147, NULL, '121', NULL, 'ingrid-portal-apps::ContentProviderPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7150, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 802, '122', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7151, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7150, NULL, '123', NULL, 'ingrid-portal-apps::ContentRSSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7153, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 803, '124', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7154, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7153, NULL, '125', NULL, 'ingrid-portal-apps::AdminHomepagePortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7156, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 804, '126', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7157, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7156, NULL, '127', NULL, 'ingrid-portal-apps::AdminIPlugPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7159, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 805, '128', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7160, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7159, NULL, '129', NULL, 'ingrid-portal-apps::AdminPortalProfilePortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7162, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 806, '130', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7163, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7162, NULL, '131', NULL, 'ingrid-portal-apps::AdminStatisticsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7165, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 807, '132', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7166, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7165, NULL, '133', NULL, 'ingrid-portal-apps::AdminUserPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7181, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 812, '11020', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7182, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7181, NULL, '11021', NULL, 'ingrid-portal-apps::CMSPortlet3', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7187, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 814, '11042', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7188, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7187, NULL, '11043', NULL, 'ingrid-portal-apps::CMSPortlet1', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7190, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 815, '11044', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7191, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7190, NULL, '11045', NULL, 'ingrid-portal-apps::CMSPortlet2', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7193, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 816, '2520', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7194, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7193, NULL, '2521', NULL, 'ingrid-portal-mdek::MdekPortalAdminPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7195, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7193, NULL, '2523', NULL, 'ingrid-portal-mdek::MdekEntryPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7196, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7193, NULL, '5380', NULL, 'ingrid-portal-mdek::MdekAdminLoginPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7198, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 817, '1780', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7199, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7198, NULL, '1781', NULL, 'ingrid-portal-apps::SearchCatalogHierarchy', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7300, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, NULL, NULL, 'ingrid-portal-apps::CategoryTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7301, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, NULL, NULL, 'ingrid-portal-apps::InfoDefaultPageTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 5, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7302, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 14, NULL, NULL, NULL, 'ingrid-portal-apps::PrivacyPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7303, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, NULL, NULL, 'ingrid-portal-apps::CategoryTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7304, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, NULL, NULL, 'ingrid-portal-apps::InfoDefaultPageTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 5, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7305, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, NULL, NULL, 'ingrid-portal-apps::CategoryTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL);
INSERT INTO fragment VALUES (7306, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, NULL, NULL, 'ingrid-portal-apps::InfoDefaultPageTeaser', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 5, 0, NULL, -1, -1, -1, -1, -1, NULL);


--
-- Data for Name: fragment_constraint; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fragment_constraint VALUES (1, 7195, 0, NULL, 'mdek', NULL, 'view');
INSERT INTO fragment_constraint VALUES (2, 7195, 1, 'admin', 'admin-portal', NULL, NULL);
INSERT INTO fragment_constraint VALUES (3, 7195, 2, NULL, 'mdek', NULL, 'view');


--
-- Data for Name: fragment_constraints_ref; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fragment_constraints_ref VALUES (2, 7194, 0, 'admin-portal');
INSERT INTO fragment_constraints_ref VALUES (3, 7196, 0, 'admin');


--
-- Data for Name: fragment_pref; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fragment_pref VALUES (2, 15, 'cmsKey', 0);
INSERT INTO fragment_pref VALUES (3, 15, 'infoTemplate', 0);
INSERT INTO fragment_pref VALUES (4, 15, 'titleKey', 0);
INSERT INTO fragment_pref VALUES (7, 28, 'cmsKey', 0);
INSERT INTO fragment_pref VALUES (8, 28, 'helpKey', 0);
INSERT INTO fragment_pref VALUES (9, 28, 'infoTemplate', 0);
INSERT INTO fragment_pref VALUES (14, 33, 'helpKey', 0);
INSERT INTO fragment_pref VALUES (21, 49, 'helpKey', 0);
INSERT INTO fragment_pref VALUES (24, 55, 'helpKey', 0);
INSERT INTO fragment_pref VALUES (25, 55, 'titleKey', 0);
INSERT INTO fragment_pref VALUES (38, 78, 'startWithEntry', 0);
INSERT INTO fragment_pref VALUES (59, 114, 'infoTemplate', 0);
INSERT INTO fragment_pref VALUES (60, 114, 'titleKey', 0);
INSERT INTO fragment_pref VALUES (7301, 15, 'sectionStyle', 0);
INSERT INTO fragment_pref VALUES (7302, 15, 'articleStyle', 0);
INSERT INTO fragment_pref VALUES (7303, 28, 'sectionStyle', 0);
INSERT INTO fragment_pref VALUES (7304, 114, 'sectionStyle', 0);
INSERT INTO fragment_pref VALUES (7305, 15, 'titleTag', 0);
INSERT INTO fragment_pref VALUES (7306, 28, 'titleTag', 0);
INSERT INTO fragment_pref VALUES (7307, 114, 'titleTag', 0);


--
-- Data for Name: fragment_pref_value; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO fragment_pref_value VALUES (2, 2, 0, 'ingrid.disclaimer');
INSERT INTO fragment_pref_value VALUES (3, 3, 0, '/WEB-INF/templates/default_cms.vm');
INSERT INTO fragment_pref_value VALUES (4, 4, 0, 'disclaimer.title');
INSERT INTO fragment_pref_value VALUES (7, 7, 0, 'ingrid.about');
INSERT INTO fragment_pref_value VALUES (8, 8, 0, 'about-1');
INSERT INTO fragment_pref_value VALUES (9, 9, 0, '/WEB-INF/templates/default_cms.vm');
INSERT INTO fragment_pref_value VALUES (14, 14, 0, 'search-chronicle-1');
INSERT INTO fragment_pref_value VALUES (21, 21, 0, 'search-measure-1');
INSERT INTO fragment_pref_value VALUES (24, 24, 0, 'search-1');
INSERT INTO fragment_pref_value VALUES (25, 25, 0, 'searchSimple.title.result');
INSERT INTO fragment_pref_value VALUES (38, 38, 0, '4');
INSERT INTO fragment_pref_value VALUES (59, 59, 0, '/WEB-INF/templates/sitemap.vm');
INSERT INTO fragment_pref_value VALUES (60, 60, 0, 'sitemap.title');
INSERT INTO fragment_pref_value VALUES (7301, 7301, 0, 'block--pad-top');
INSERT INTO fragment_pref_value VALUES (7302, 7302, 0, 'content ob-container ob-box-narrow ob-box-center');
INSERT INTO fragment_pref_value VALUES (7303, 7303, 0, 'block--padded');
INSERT INTO fragment_pref_value VALUES (7304, 7304, 0, 'block--padded');
INSERT INTO fragment_pref_value VALUES (7305, 7305, 0, 'h1');
INSERT INTO fragment_pref_value VALUES (7306, 7306, 0, 'h1');
INSERT INTO fragment_pref_value VALUES (7307, 7307, 0, 'h1');


--
-- Data for Name: fragment_prop; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ingrid_anniversary; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_anniversary VALUES (18114, 'calendarEvent_180', 'Greenpeace action against French atomic tests', '1992-01-21', 1992, 1, 21, '1992-01-21', 1992, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en');
INSERT INTO ingrid_anniversary VALUES (18113, 't9c22ff_11f605c4a69_-f51', 'Cabinet decides on implementation of EU Batteries Directive', '2009-01-21', 2009, 1, 21, '2009-01-21', 2009, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en');
INSERT INTO ingrid_anniversary VALUES (18112, 't2cd728_12057cca378_1295', 'The 1968 Thule air crash', '1968-01-21', 1968, 1, 21, '1968-01-21', 1968, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en');
INSERT INTO ingrid_anniversary VALUES (18111, 't53da2896_13d1685ed61_3901', 'Permanent Secretariat of Arctic Council established in Tromsø', '2013-01-21', 2013, 1, 21, '2013-01-21', 2013, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en');
INSERT INTO ingrid_anniversary VALUES (18110, 't-6dadf813_143c097fe2a_-8ae', 'Long term exposure to air pollution linked to coronary events', '2014-01-21', 2014, 1, 21, '2014-01-21', 2014, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en');
INSERT INTO ingrid_anniversary VALUES (18105, 't-6dadf813_143c097fe2a_-8ae', 'Feinstaubbelastung führt zu erhöhtem Herzinfarkt-Risiko', '2014-01-21', 2014, 1, 21, '2014-01-21', 2014, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de');
INSERT INTO ingrid_anniversary VALUES (18106, 't2cd728_12057cca378_1295', 'Der Flugzeugabsturz  bei Thule 1968', '1968-01-21', 1968, 1, 21, '1968-01-21', 1968, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de');
INSERT INTO ingrid_anniversary VALUES (18107, 't9c22ff_11f605c4a69_-f51', 'Kabinett beschließt Umsetzung der EU-Batterierichtlinie', '2009-01-21', 2009, 1, 21, '2009-01-21', 2009, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de');
INSERT INTO ingrid_anniversary VALUES (18108, 't53da2896_13d1685ed61_3901', 'Ständiges Sekretariat für den Arktischen Rat in Tromsø gegründet', '2013-01-21', 2013, 1, 21, '2013-01-21', 2013, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de');
INSERT INTO ingrid_anniversary VALUES (18109, 'calendarEvent_180', 'Greenpeace Aktion gegen französische Atomtests', '1992-01-21', 1992, 1, 21, '1992-01-21', 1992, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de');


--
-- Name: ingrid_anniversary_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_anniversary_id_seq', 1, false);


--
-- Data for Name: ingrid_chron_eventtypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_chron_eventtypes VALUES (1, 'act', 'activity', 1);
INSERT INTO ingrid_chron_eventtypes VALUES (2, 'his', 'historical', 2);
INSERT INTO ingrid_chron_eventtypes VALUES (3, 'leg', 'legal', 3);
INSERT INTO ingrid_chron_eventtypes VALUES (5, 'dis', 'disaster', 4);
INSERT INTO ingrid_chron_eventtypes VALUES (6, 'cfe', 'conference', 5);
INSERT INTO ingrid_chron_eventtypes VALUES (8, 'yea', 'natureOfTheYear', 6);
INSERT INTO ingrid_chron_eventtypes VALUES (9, 'pub', 'publication', 7);
INSERT INTO ingrid_chron_eventtypes VALUES (13, 'ann', 'anniversary', 8);
INSERT INTO ingrid_chron_eventtypes VALUES (14, 'int', 'interYear', 9);
INSERT INTO ingrid_chron_eventtypes VALUES (15, 'obs', 'observation', 10);


--
-- Name: ingrid_chron_eventtypes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_chron_eventtypes_id_seq', 1, false);


--
-- Data for Name: ingrid_cms; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_cms VALUES (1, 'ingrid.teaser.inform', 'PortalU informiert Text', '2017-03-27 14:16:22', 'kst_su');
INSERT INTO ingrid_cms VALUES (15, 'ingrid.disclaimer', 'Impressum', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms VALUES (16, 'ingrid.about', 'Über PortalU', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms VALUES (17, 'ingrid.privacy', 'Haftungsausschluss', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms VALUES (18, 'ingrid.contact.intro.postEmail', 'Adresse auf der Kontaktseite', '2017-03-27 14:16:22', 'kst_cg');
INSERT INTO ingrid_cms VALUES (19, 'ingrid.home.welcome', 'Ingrid Willkommens Portlet', '2008-07-09 00:00:00', 'kst_cg');
INSERT INTO ingrid_cms VALUES (20, 'portal.teaser.shortcut', 'Anwendungen', '2012-07-19 15:36:12', 'admin');
INSERT INTO ingrid_cms VALUES (21, 'portal.teaser.shortcut.query', 'Schnellsuche', '2012-07-19 15:36:12', 'admin');
INSERT INTO ingrid_cms VALUES (25, 'portal.cms.portlet.3', 'Anwendungen Übersicht', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms VALUES (23, 'portal.cms.portlet.1', 'Anleitungen', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms VALUES (24, 'portal.cms.portlet.2', 'Veranstaltungen', '2017-03-27 14:16:22', 'admin');


--
-- Name: ingrid_cms_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_cms_id_seq', 1, false);


--
-- Data for Name: ingrid_cms_item; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_cms_item VALUES (1, 1, 'de', '<span style="text-transform: none;">PORTALU INFORMIERT</span>', '<p style="background:url(/webstats/portalu_macht_zu.png); background-repeat: no-repeat;">
<span style="color:#008000;">Die VwV UDK/GEIN wurde gekündigt.</span><br>Demzufolge wird PortalU Ende 2014 den Betrieb einstellen:<br>
<img src="http://portalu.de/cgi-bin/count_days.pl" alt="das Ende von portalu.de naht."></p>', '2014-08-27 00:00:00', 'kst_su');
INSERT INTO ingrid_cms_item VALUES (37, 20, 'de', 'Anwendungen', '', '2012-07-19 15:36:12', 'admin');
INSERT INTO ingrid_cms_item VALUES (38, 20, 'en', 'Application', '', '2012-07-19 15:36:12', 'admin');
INSERT INTO ingrid_cms_item VALUES (39, 21, 'de', 'Schnellsuche', '', '2012-07-19 15:36:12', 'admin');
INSERT INTO ingrid_cms_item VALUES (2, 1, 'en', '<span style="text-transform: none;">PortalU informs</span>', '<p style="background:url(/webstats/portalu_macht_zu.png); background-repeat: no-repeat;">
<span style="color:#008000;">The administrative agreement VwV UDK/GEIN is terminated.</span><br>Thus the information portal PortalU will be shut down:<br>
<img src="http://portalu.de/cgi-bin/count_days.pl" alt="The end of portalu.de is near."></p>
', '2014-08-27 00:00:00', 'kst_su');
INSERT INTO ingrid_cms_item VALUES (40, 21, 'en', 'Quick Search', '', '2012-07-19 15:36:12', 'admin');
INSERT INTO ingrid_cms_item VALUES (48, 25, 'en', 'CMSPortlet3', '', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms_item VALUES (47, 25, 'de', 'CMSPortlet3', '', '2017-03-27 14:16:22', 'admin');
INSERT INTO ingrid_cms_item VALUES (43, 23, 'de', 'CMSPortlet1', '', '2012-07-19 15:36:24', 'admin');
INSERT INTO ingrid_cms_item VALUES (27, 15, 'en', 'Disclaimer', '<a name="herausgeber"></a>
<h2>Publisher</h2>
<p>PortalU is managed by the Coordination Center PortalU at the Environmental, Energy and Climate Protection Ministry of Lower Saxony, Hannover, Germany. Development and maintenance of the portal is financed by a administrative agreement between the German Federal States (Länder) and the Federal Government. </p>
<h3><a href="http://www.kst.portalu.de/" target="_new" title="Link öffnet in neuem Fenster">Coordination Center PortalU</a></h3>
<p>c/o Nieders. Ministerium für Umwelt, Energie und Klimaschutz<br>Archivstrasse 2<br>D-30169 Hannover<br>
	<a href="/ingrid-portal/portal/service-contact.psml">Contact</a>
</p>
<br>
<a name="verantwortlich"></a>
<h2>Overall Responsibility</h2>
<p>Dr. Fred Kruse</p>
<br>
<a name="realisierung"></a>
<h2>Implementation</h2>
<h3><a href="http://www.wemove.com/" target="_new" title="Link opens new window">wemove digital solutions GmbH</a></h3>
<h3><a href="http://www.chives.de/" target="_new" title="Link opens new window">chives - Büro für Webdesign Plus</a></h3>
<br>
<a name="betrieb"></a>
<h2>Operation</h2>     
<h3><a href="http://www.its-technidata.de/" target="_new" title="Link opens new window">TechniData IT Service GmbH</a></h3>
<br>
<a name="haftung"></a>
<h2>Liability Disclaimer</h2>     
<p>The Environment Ministry of Lower Saxony (Niedersächsisches Umweltministerium) does not take any responisbility for the content of web-sites that can be reached through PortalU. Web-sites that are included in the portal are evaluated only technically. A continuous evaluation of the content of the included web-pages in neither possible nor intended. The Environment Ministry of Lower Saxony explicitly rejects all content that potentially infringes upon German legislation or general morality.</p>
<p> </p>

<h2>Nutzungsbedingungen</h2>

<p class=MsoNormal>Die im PortalU-Kartenviewer eingebundenen Karten
(Geodatendienste) stammen von behördlichen Anbietern auf Bundes- und
Landesebene. Die Nutzung der kostenfrei angebotenen Geodatendienste ist hierbei
an entsprechende Bedingungen geknüpft, die zu beachten sind.</p>

<p class=MsoNormal> </p>

<p class=MsoNormal>Für Dienste aus Bayern sind die Nutzugsbedingungen der
GDI-BY zu beachten:</p>

<p class=MsoNormal><a
href="http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html">http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html</a></p>

<p class=MsoNormal> </p>
', '2013-07-09 00:00:00', 'admin');
INSERT INTO ingrid_cms_item VALUES (28, 15, 'de', 'Impressum', '<h2><a name=herausgeber></a>Herausgeber</h2>

<p>PortalU wird von der Koordinierungsstelle PortalU im Niedersächsischen
Ministerium für Umwelt, Energie und Klimaschutz auf der Grundlage der <a
href="http://www.kst.portalu.de/verwaltungskooperation/VVGEIN_endg.pdf"
target="_new" title="Link öffnet in neuem Fenster">Bund-Länder-Verwaltungsvereinbarung
UDK/GEIN</a> betrieben und weiterentwickelt.</p>

<h3><a href="http://www.kst.portalu.de/" target="_new"
title="Link 
öffnet in neuem Fenster">Koordinierungsstelle PortalU</a></h3>

<p>Niedersächsisches Ministerium für Umwelt, Energie und Klimaschutz<br>
Archivstrasse 2<br>
D-30169 Hannover<br>
<a href="http://www.portalu.de/ingrid-portal/portal/service-contact.psml">Kontakt</a>
</p>

<p class=MsoNormal><a name=verantwortlich></a> </p>

<h2>Verantwortliche Gesamtredaktion</h2>

<p>Dr. Fred Kruse</p>

<p class=MsoNormal><a name=realisierung></a> </p>

<h2>Realisierung</h2>



<h3><a href="http://www.wemove.com/" target="_new"
title="Link öffnet in neuem Fenster">wemove digital solutions GmbH</a></h3>

<h3><a href="http://www.chives.de/" target="_new"
title="Link öffnet in 
neuem Fenster">chives - Büro für Webdesign Plus
Darmstadt</a></h3>

<p class=MsoNormal><a name=betrieb></a> </p>

<h2>Technischer Betrieb</h2>

<h3><a href="http://www.its-technidata.de/" target="_new"
title="Link 
öffnet in neuem Fenster">TechniData IT Service GmbH</a></h3>

<p class=MsoNormal><a name=haftung></a> </p>

<h2>Haftungsausschluss</h2>

<p>Das Niedersächsische Ministerium für Umwelt, Energie und Klimaschutz übernimmt keine
Verantwortung für die Inhalte von Websites, die über Links erreicht werden. Die
Links werden bei der Aufnahme nur kursorisch angesehen und bewertet. Eine
kontinuierliche Prüfung der Inhalte ist weder beabsichtigt noch möglich. Das
Niedersächsische Ministerium für Umwelt und Klimaschutz distanziert sich
ausdrücklich von allen Inhalten, die möglicherweise straf- oder
haftungsrechtlich relevant sind oder gegen die guten Sitten verstoßen.</p>

<p> </p>

<h2>Nutzungsbedingungen</h2>

<p class=MsoNormal>Die im PortalU-Kartenviewer eingebundenen Karten
(Geodatendienste) stammen von behördlichen Anbietern auf Bundes- und
Landesebene. Die Nutzung der kostenfrei angebotenen Geodatendienste ist hierbei
an entsprechende Bedingungen geknüpft, die zu beachten sind.</p>

<p class=MsoNormal> </p>

<p class=MsoNormal>Für Dienste aus Bayern sind die Nutzugsbedingungen der
GDI-BY zu beachten:</p>

<p class=MsoNormal><a
href="http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html">http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html</a></p>

<p class=MsoNormal> </p>

', '2013-07-09 00:00:00', 'admin');
INSERT INTO ingrid_cms_item VALUES (29, 16, 'de', 'Porträt', '<p>Zur richtigen Einschätzung und Bewertung von Umweltsituationen werden umfassende Informationen über die Umwelt benötigt. In der öffentlichen Verwaltung wird eine Vielzahl von Umweltinformationen auf unterschiedlichster Ebene generiert. Diese Informationen sind aber zum Teil nur schwer auffindbar. Zur Verbesserung der Auffindbarkeit wurde deshalb das Umweltportal PortalU ins Leben gerufen. 
</p>
<p>PortalU bietet einen zentralen Zugriff auf umweltrelevante Webseiten, Umweltdatenkataloge und Datenbanken von über 450 
<a href="http://www.portalu.de/informationsanbieter" target="_new" title="PortalU - Informationsanbieter - Link öffnet in neuem Fenster">öffentlichen Institutionen</a> in Deutschland. Sie können im gesamten Informationsangebot oder gezielt nach einzelnen Umweltthemen, digitalen Karten, Umweltmesswerten, Presseinformationen oder historischen Ereignissen recherchieren. 
</p> 
<p>Die modular aufgebaute PortalU-Software InGrid kann man sich als Informationsnetz vorstellen, das Webseiten, Daten und Metadaten unter einem Dach bündelt. Die Software besteht aus einer flexibel konfigurierbaren Portaloberfläche, einem Web-Katalog-Service, einem Karten-Viewer sowie diversen An- und Abfrageschnittstellen zur Recherche in angeschlossenen Systemen bzw. für den Transfer von Informationen. Die PortalU-Software basiert auf Open-Source-Komponenten und Eigenentwicklungen und kann somit innerhalb der öffentlichen Verwaltung lizenzkostenfrei genutzt werden.
</p>
<p>PortalU basiert auf einer <a href="http://www.kst.portalu.de/verwaltungskooperation/VVGEIN_endg.pdf" target="_new" title="Zur Verwaltungsvereinbarung UDK/GEIN - Link öffnet in neuem Fenster">Verwaltungsvereinbarung</a> zwischen Bund und Ländern. Darin verständigen sich die Partner, dass ein gemeinsames Umweltportal zum Nachweis und zur aktiven Verbreitung von Umweltinformationen zum Einsatz kommen soll. Die Erfüllung der Anforderungen der Aarhus-Konvention und der EU-Richtlinie über den Zugang der Öffentlichkeit zu Umweltinformationen bzw. der entsprechenden Umweltinformationsgesetze von Bund und Ländern stehen hierbei im Mittelpunkt. Seit 2007 sind für die Datenkataloge zudem die Maßgaben der europäischen INSPIRE-Richtlinie ausschlaggebend. 
</p>
<p>Technisch und inhaltlich wird das Portal von der Koordinierungsstelle PortalU im Niedersächsischen Umweltministerium betreut. Für Fragen und Anregungen wenden Sie sich bitte an die <a href="mailto:kst@portalu.de" title="Schreiben Sie uns - Email Programm startet automatisch">Koordinierungsstelle PortalU</a>. Weitere Hintergrundinformationen finden Sie außerdem auf der <a href="http://www.kst.portalu.de/" target="_new" title="Zur Homepage der Bund-Länder-Kooperation PortalU - Link öffnet in neuem Fenster">Homepage der Bund-Länder-Kooperation PortalU</a>. </p>', '2013-05-28 00:00:00', 'admin');
INSERT INTO ingrid_cms_item VALUES (36, 19, 'en', 'Welcome to PortalU', '<p>Welcome to PortalU, the German Environmental Information Portal! We offer a comfortable and central access to over 1.000.000 web-pages and database entries from public agencies in Germany. We also guide you directly to up-to-date environmental news, upcoming and past environmental events, environmental monitoring data, and interesting background information on many environmental topics.</p><p>Core-component of PortalU is a powerful search-engine that you can use to look up your terms of interest in web-pages and databases. In the "Extended Search" mode, you can use an environmental thesaurus and a digital mapping tool to compose complex spatio-thematic queries.</p><p>PortalU is the result of a cooperation between the German "Länder" and the German Federal Government. The project is managed by the <a href="http://www.kst.portalu.de/" target="_new" title="Link opens new window">Coordination Center PortalU</a>, a group of environmental and IT experts attached to the Environment Ministry of Lower Saxony in Hannover, Germany. We strive to continuously improve and extend the portal. Please help us in this effort and mail your suggestions or questions to <a href="mailto:kst@portalu.de">Coordination Center PortalU</a>.</p>', '2008-07-09 00:00:00', 'kst_cg');
INSERT INTO ingrid_cms_item VALUES (44, 23, 'en', 'CMSPortlet1', '', '2012-07-19 15:36:24', 'admin');
INSERT INTO ingrid_cms_item VALUES (45, 24, 'de', 'CMSPortlet2', '', '2012-07-19 15:36:24', 'admin');
INSERT INTO ingrid_cms_item VALUES (46, 24, 'en', 'CMSPortlet2', '', '2012-07-19 15:36:24', 'admin');
INSERT INTO ingrid_cms_item VALUES (30, 16, 'en', 'Portrait', '<p>Commonly, a broad range of information about the environment is needed for estimating environmental situations accurately. A multiplicity of environmental information is generated by public administration on different hierarchical levels. Unfortunately the access to this information is mostly difficult. The environmental information portal PortalU aims to overcome this obstacle.
</p>
<p>PortalU offers central access to environmentally relevant web pages, data catalogues and databases from over 450 <a href="http://www.portalu.de/informationsanbieter" target="_new" title="PortalU - Information providers - Link opens in new window">public organisations</a> in Germany. You can search through the whole information range or search selectivly through single environmental topics, digital maps, measured data, press information or historical events. 
</p> 
<p>The PortalU software InGrid consists of several modules, which can be described as information grid. The information grid ties web pages, data and metadata under a single roof. The software consists of a portal surface, which can be configured flexibly, a web catalogue service, a map viewer and several technical interfaces to connect and transfer information. InGrid is based on open source components and internal developments. Therefore it can be used without external licence costs within the public administration.
</p>
<p>PortalU is based on an <a href="http://www.kst.portalu.de/verwaltungskooperation/VVGEIN_endg.pdf" target="_new" title="To the administrative agreement UDK/GEIN - Link opens in new window">administrative agreement</a> between the Federal Republic of Germany and the German Federal States. The agreement partners aim at improving the active dissemination of public environmental information through a common environmental information portal. The main focus is thereby set on the conformance of requirements of the Aarhus Convention and of the EU Directive 2003/4/EC which defines the public access to environmental information, respectively the German environmental information acts (Umweltinformationsgesetze). Besides this, the requirements of the EU INSPIRE Directive 2007/2/EC are crucial for the web catalogue service of PortalU (InGridCatalog).
</p>
<p> The project is managed by the Coordination Center PortalU at the Ministry of Environment of Lower Saxony in Hanover, Germany. For questions and suggestions please contact the <a href="mailto:kst@portalu.de" title="Write us - Email program starts automatically">Coordination Center PortalU</a>. For further background information please visit the <a href="http://www.kst.portalu.de/" target="_new" title="homepage of the Federal-State-Cooperation PortalU - Link opens in new window ">Homepage of the Federal-State-Cooperation PortalU </a>. 
</p>', '2013-05-28 00:00:00', 'admin');
INSERT INTO ingrid_cms_item VALUES (31, 17, 'de', 'Datenschutz', '<p>PortalU enthält sowohl Inhalte, die als Teledienst nach § 2 Teledienstgesetz (TDG) als auch Inhalte, die als Mediendienst nach § 2 Mediendienste-Staatsvertrag (MDStV) zu bewerten sind. Hierbei werden folgende Verfahrensgrundsätze gewährleistet:<br></p>
<ul>
<li>Bei jedem Zugriff eines Nutzers auf eine Seite aus dem Angebot von PortalU und bei jedem Abruf einer Datei werden Daten über diesen Vorgang in einer Protokolldatei gespeichert. Diese Daten sind nicht personenbezogen. Wir können also nicht nachvollziehen, welcher Nutzer welche Daten abgerufen hat. Die Protokolldaten werden lediglich in anonymisierter Form statistisch ausgewertet und dienen damit der inhaltlichen Verbesserung unseres Angebotes.<br><br>
<br>
<a href="http://portalu.de/piwik-config/">Deaktivierung/Aktivierung der statistischen Erfassung</a>
<br><br>
</li>
<li>Eine Ausnahme besteht innerhalb des Internetangebotes mit der Eingabe persönlicher oder geschäftlicher Daten (eMail-Adresse, Name, Anschrift) zur Anmeldung bei "Mein PortalU" oder der Bestellung des PortalU-Newsletters. Dabei erfolgt die Angabe dieser Daten durch Nutzerinnen und Nutzer ausdrücklich freiwillig. Ihre persönlichen Daten werden von uns selbstverständlich nicht an Dritte weitergegeben. Die Inanspruchnahme aller angebotenen Dienste ist, soweit dies technisch möglich und zumutbar ist, auch ohne Angabe solcher Daten beziehungsweise unter Angabe anonymisierter Daten oder eines Pseudonyms möglich.<br><br>
</li>
<li>Sie können alle allgemein zugänglichen PortalU-Seiten ohne den Einsatz von Cookies benutzen. Wenn Ihre Browser-Einstellungen das Setzen von Cookies zulassen, werden von PortalU sowohl Session-Cookies als auch permanente Cookies gesetzt. Diese dienen ausschließlich der Erhöhung des Bedienungskomforts.
</li>
</ul>', '2012-02-24 00:00:00', 'admin');
INSERT INTO ingrid_cms_item VALUES (32, 17, 'en', 'Privacy Policy', '<p>PortalU contains content that is categorized as "Teledienst" (after § 2 Teledienstgesetz (TDG)), as well as content that is categorized as "Mediendienst" (after § 2 Mediendienste-Staatsvertrag (MDStV)). The following policies do apply:<br></p>
<ul>
<li>With each user-access to a content-page in PortalU, the relevant access-data are saved in a log file. This information is not personalized. Therefore it is not possible to reason which user has had access to which content page. The purpose of the log file is purely statistical. The evaluation of the log file helps to improve PortalU.<br><br>
<a href="http://portalu.de/piwik-config/">Deactivation/activation of the statistic collection</a>
<br><br>
</li>
<li>An exeption to our general privacy policy is made when personal data (e-mail, name, address) are provided to register for the PortalU newsletter. This information is provided by the user on a voluntary basis an is saved for internal purposes (mailing of newsletter). The information is not given to third-parties. The use of specific Portal functions does not, as far as technically feasible, depend on the provision of personal data.<br><br>
</li>
<li>You can take benefit of virtually all functions of PortalU without the use of cookies. However, if you choose to allow cookies in your browser, this will increase the useability of the application. 
</li>
</ul>', '2012-02-24 00:00:00', 'admin');
INSERT INTO ingrid_cms_item VALUES (33, 18, 'de', '', '.</p><p>Unsere Postadresse:</p>

<p> Koordinierungsstelle PortalU<br />
Niedersächs. Ministerium für Umwelt, Energie und Klimaschutz<br />
Archivstrasse 2<br />
D-30169 Hannover<br /></p>

<p>Nehmen Sie online Kontakt mit uns auf! Wir werden Ihnen schnellstmöglichst per E-Mail antworten. Die eingegebenen Informationen und Daten werden nur zur Bearbeitung Ihrer Anfrage gespeichert und genutzt. Beachten Sie bitte, dass die Datenübermittlung über das Kontaktformular unverschlüsselt erfolgt. Für vertrauliche Nachrichten nutzen Sie bitte den herkömmlichen Postweg.</p>
', '2012-02-02 00:00:00', 'kst_cg');
INSERT INTO ingrid_cms_item VALUES (34, 18, 'en', '', '.</p><p>Our address:</p><p>Niedersächsisches Ministerium für Umwelt und Klimaschutz<br />Koordinierungsstelle PortalU<br />Archivstrasse 2<br />D-30169 Hannover<br /></p> <p>Please contact us! We will answer your request as soon as possible. All data you entered will be saved only to process your request.</p>', '2012-02-02 00:00:00', 'kst_cg');
INSERT INTO ingrid_cms_item VALUES (35, 19, 'de', 'Willkommen bei PortalU', '<p>Willkommen bei PortalU, dem Umweltportal Deutschland! Wir bieten Ihnen einen zentralen Zugriff auf mehrere hunderttausend Internetseiten und Datenbankeinträge von öffentlichen Institutionen und Organisationen. Zusätzlich können Sie aktuelle Nachrichten und Veranstaltungshinweise, Umweltmesswerte, Hintergrundinformationen und historische Umweltereignisse über PortalU abrufen.</p><p>Die integrierte Suchmaschine ist eine zentrale Komponente von PortalU. Mit ihrer Hilfe können Sie Webseiten und Datenbankeinträge nach Stichworten durchsuchen. Über die Option "Erweiterte Suche" können Sie zusätzlich ein differenziertes Fachvokabular und deutschlandweite Hintergrundkarten zur Zusammenstellung Ihrer Suchanfrage nutzen.</p><p>PortalU ist eine Kooperation der Umweltverwaltungen im Bund und in den Ländern. Inhaltlich und technisch wird PortalU von der <a href="http://www.kst.portalu.de/" target="_new" title="Link öffnet in neuem Fenster">Koordinierungsstelle PortalU</a> im Niedersächsischen Ministerium für Umwelt und Klimaschutz verwaltet. Wir sind darum bemüht, das System kontinuierlich zu erweitern und zu optimieren. Bei Fragen und Verbesserungsvorschlägen wenden Sie sich bitte an die <a href="mailto:kst@portalu.de">Koordinierungsstelle PortalU</a>.</p>', '2008-07-09 00:00:00', 'kst_cg');


--
-- Name: ingrid_cms_item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_cms_item_id_seq', 1, false);


--
-- Data for Name: ingrid_env_topic; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_env_topic VALUES (1, 'abf', 'abfall', 1);
INSERT INTO ingrid_env_topic VALUES (2, 'alt', 'altlasten', 2);
INSERT INTO ingrid_env_topic VALUES (3, 'bau', 'bauen', 3);
INSERT INTO ingrid_env_topic VALUES (4, 'bod', 'boden', 4);
INSERT INTO ingrid_env_topic VALUES (5, 'che', 'chemikalien', 5);
INSERT INTO ingrid_env_topic VALUES (6, 'ene', 'energie', 6);
INSERT INTO ingrid_env_topic VALUES (7, 'for', 'forstwirtschaft', 7);
INSERT INTO ingrid_env_topic VALUES (8, 'gen', 'gentechnik', 8);
INSERT INTO ingrid_env_topic VALUES (9, 'geo', 'geologie', 9);
INSERT INTO ingrid_env_topic VALUES (10, 'ges', 'gesundheit', 10);
INSERT INTO ingrid_env_topic VALUES (11, 'lae', 'laermerschuetterung', 11);
INSERT INTO ingrid_env_topic VALUES (12, 'lan', 'landwirtschaft', 12);
INSERT INTO ingrid_env_topic VALUES (13, 'luf', 'luftklima', 13);
INSERT INTO ingrid_env_topic VALUES (14, 'nac', 'nachhaltigeentwicklung', 14);
INSERT INTO ingrid_env_topic VALUES (15, 'nat', 'naturlandschaft', 15);
INSERT INTO ingrid_env_topic VALUES (16, 'str', 'strahlung', 16);
INSERT INTO ingrid_env_topic VALUES (17, 'tie', 'tierschutz', 17);
INSERT INTO ingrid_env_topic VALUES (18, 'uin', 'umweltinformation', 18);
INSERT INTO ingrid_env_topic VALUES (19, 'uwi', 'umweltwirtschaft', 19);
INSERT INTO ingrid_env_topic VALUES (20, 'ver', 'verkehr', 20);
INSERT INTO ingrid_env_topic VALUES (21, 'was', 'wasser', 21);


--
-- Name: ingrid_env_topic_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_env_topic_id_seq', 1, false);


--
-- Data for Name: ingrid_lookup; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_lookup VALUES (1, 'ingrid_db_version', '4.0.1', '2017-03-27 14:16:22');


--
-- Data for Name: ingrid_measures_rubric; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_measures_rubric VALUES (1, 'str', 'radiation', 1);
INSERT INTO ingrid_measures_rubric VALUES (2, 'luf', 'air', 2);
INSERT INTO ingrid_measures_rubric VALUES (3, 'was', 'water', 3);
INSERT INTO ingrid_measures_rubric VALUES (4, 'wei', 'misc', 4);


--
-- Name: ingrid_measures_rubric_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_measures_rubric_id_seq', 1, false);


--
-- Data for Name: ingrid_newsletter_data; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: ingrid_newsletter_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_newsletter_data_id_seq', 1, false);


--
-- Data for Name: ingrid_partner; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_partner VALUES (1, 'bund', 'Bund', 1);
INSERT INTO ingrid_partner VALUES (2, 'bw', 'Baden-Württemberg', 2);
INSERT INTO ingrid_partner VALUES (3, 'by', 'Bayern', 3);
INSERT INTO ingrid_partner VALUES (4, 'be', 'Berlin', 4);
INSERT INTO ingrid_partner VALUES (5, 'bb', 'Brandenburg', 5);
INSERT INTO ingrid_partner VALUES (6, 'hb', 'Bremen', 6);
INSERT INTO ingrid_partner VALUES (7, 'hh', 'Hamburg', 7);
INSERT INTO ingrid_partner VALUES (8, 'he', 'Hessen', 8);
INSERT INTO ingrid_partner VALUES (9, 'mv', 'Mecklenburg-Vorpommern', 9);
INSERT INTO ingrid_partner VALUES (10, 'ni', 'Niedersachsen', 10);
INSERT INTO ingrid_partner VALUES (11, 'nw', 'Nordrhein-Westfalen', 11);
INSERT INTO ingrid_partner VALUES (12, 'rp', 'Rheinland-Pfalz', 12);
INSERT INTO ingrid_partner VALUES (13, 'sl', 'Saarland', 13);
INSERT INTO ingrid_partner VALUES (14, 'sn', 'Sachsen', 14);
INSERT INTO ingrid_partner VALUES (15, 'st', 'Sachsen-Anhalt', 15);
INSERT INTO ingrid_partner VALUES (16, 'sh', 'Schleswig-Holstein', 16);
INSERT INTO ingrid_partner VALUES (17, 'th', 'Thüringen', 17);


--
-- Name: ingrid_partner_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_partner_id_seq', 1, false);


--
-- Data for Name: ingrid_principal_pref; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: ingrid_principal_pref_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_principal_pref_id_seq', 1, false);


--
-- Data for Name: ingrid_provider; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_provider VALUES (1, 'bu_bmu', 'Bundesministerium für Umwelt, Naturschutz und Reaktorsicherheit', 'http://www.bmu.de/', 1, 1);
INSERT INTO ingrid_provider VALUES (2, 'bu_uba', 'Umweltbundesamt', 'http://www.umweltbundesamt.de/', 2, 1);
INSERT INTO ingrid_provider VALUES (3, 'bu_bfn', 'Bundesamt für Naturschutz', 'http://www.bfn.de/', 3, 1);
INSERT INTO ingrid_provider VALUES (4, 'bu_bfs', 'Bundesamt für Strahlenschutz', 'http://www.bfs.de/', 4, 1);
INSERT INTO ingrid_provider VALUES (5, 'bu_bmf', 'Bundesministerium der Finanzen', 'http://www.bundesfinanzministerium.de/', 5, 1);
INSERT INTO ingrid_provider VALUES (6, 'bu_bmbf', 'Bundesministerium für Bildung und Forschung', 'http://www.bmbf.de/', 6, 1);
INSERT INTO ingrid_provider VALUES (7, 'bu_bmelv', 'Bundesministerium für Ernährung, Landwirtschaft und Verbraucherschutz', 'http://www.bmelv.de/cln_044/DE/00-Home/__Homepage__node.html__nnn=true', 7, 1);
INSERT INTO ingrid_provider VALUES (8, 'bu_bmz', 'Bundesministerium für wirtschaftliche Zusammenarbeit und Entwicklung', 'http://www.bmz.de/', 8, 1);
INSERT INTO ingrid_provider VALUES (9, 'bu_aa', 'Auswärtiges Amt', 'http://www.auswaertiges-amt.de/', 10, 1);
INSERT INTO ingrid_provider VALUES (10, 'bu_bsh', 'Bundesamt für Seeschifffahrt und Hydrographie', 'http://www.bsh.de/', 11, 1);
INSERT INTO ingrid_provider VALUES (11, 'bu_bvl', 'Bundesamt für Verbraucherschutz und Lebensmittelsicherheit', 'http://www.bvl.bund.de/', 12, 1);
INSERT INTO ingrid_provider VALUES (12, 'bu_bgr', 'Bundesanstalt für Geowissenschaften und Rohstoffe', 'http://www.bgr.bund.de/', 13, 1);
INSERT INTO ingrid_provider VALUES (13, 'bu_bfg', 'Bundesanstalt für Gewässerkunde', 'http://www.bafg.de/', 14, 1);
INSERT INTO ingrid_provider VALUES (14, 'bu_nokis', 'Bundesanstalt für Wasserbau - Dienststelle Hamburg', 'http://www.hamburg.baw.de/', 15, 1);
INSERT INTO ingrid_provider VALUES (15, 'bu_bfr', 'Bundesinstitut für Risikobewertung', 'http://www.bfr.bund.de/', 16, 1);
INSERT INTO ingrid_provider VALUES (16, 'bu_bka', 'Bundeskriminalamt', 'http://www.bka.de/', 17, 1);
INSERT INTO ingrid_provider VALUES (18, 'bu_stba', 'Statistisches Bundesamt', 'http://www.destatis.de/', 19, 1);
INSERT INTO ingrid_provider VALUES (19, 'bu_ble', 'Bundesanstalt für Landwirtschaft und Ernährung', 'http://www.ble.de', 20, 1);
INSERT INTO ingrid_provider VALUES (20, 'bu_bpb', 'Bundeszentrale für politische Bildung', 'http://www.bpb.de/', 21, 1);
INSERT INTO ingrid_provider VALUES (21, 'bu_gtz', 'Deutsche Gesellschaft für Technische Zusammenarbeit (GTZ) GmbH', 'http://www.gtz.de/', 22, 1);
INSERT INTO ingrid_provider VALUES (22, 'bu_dwd', 'Deutscher Wetterdienst', 'http://www.dwd.de/', 23, 1);
INSERT INTO ingrid_provider VALUES (23, 'bu_dlr', 'Deutsches Zentrum für Luft- und Raumfahrt DLR e.V.', 'http://www.dlr.de/', 24, 1);
INSERT INTO ingrid_provider VALUES (24, 'bu_kug', 'Koordinierungsstelle PortalU', 'http://www.kst.portalu.de/', 25, 1);
INSERT INTO ingrid_provider VALUES (25, 'bu_labo', 'Länderarbeitsgemeinschaft Boden LABO', 'http://www.labo-deutschland.de/', 26, 1);
INSERT INTO ingrid_provider VALUES (26, 'bu_lawa', 'Länderarbeitsgemeinschaft Wasser', 'http://www.lawa.de/', 27, 1);
INSERT INTO ingrid_provider VALUES (27, 'bu_laofdh', 'Leitstelle des Bundes für Abwassertechnik, Boden- und Grundwasserschutz, Liegenschaftsinformationssystem Außenanlagen LISA', 'http://www.ofd-hannover.de/la/', 28, 1);
INSERT INTO ingrid_provider VALUES (537, 'he_lbhef', 'Landesbetrieb Hessen-Forst', 'http://www.hessen-forst.de/', 0, 8);
INSERT INTO ingrid_provider VALUES (28, 'bu_bpa', 'Presse- und Informationsamt der Bundesregierung', 'http://www.bundesregierung.de/', 29, 1);
INSERT INTO ingrid_provider VALUES (29, 'bu_blauerengel', 'RAL/Umweltbundesamt Umweltzeichen "Blauer Engel"', 'http://www.blauer-engel.de/', 30, 1);
INSERT INTO ingrid_provider VALUES (30, 'bu_sru', 'Rat von Sachverständigen für Umweltfragen (SRU)', 'http://www.umweltrat.de/', 31, 1);
INSERT INTO ingrid_provider VALUES (31, 'bu_ssk', 'Strahlenschutzkommission', 'http://www.ssk.de/', 32, 1);
INSERT INTO ingrid_provider VALUES (32, 'bu_umk', 'Umweltministerkonferenz', 'http://www.umweltministerkonferenz.de/', 33, 1);
INSERT INTO ingrid_provider VALUES (33, 'bu_wbgu', 'Wissenschaftlicher Beirat der Bundesregierung Globale Umweltveränderungen - WBGU', 'http://www.wbgu.de/', 34, 1);
INSERT INTO ingrid_provider VALUES (35, 'bu_uga', 'Umweltgutachterausschuss (UGA)', 'http://www.uga.de/', 36, 1);
INSERT INTO ingrid_provider VALUES (36, 'bu_co2', 'co2online gGmbH Klimaschutzkampagne', 'http://www.co2online.de/', 37, 1);
INSERT INTO ingrid_provider VALUES (38, 'bw_um', 'Ministerium für Umwelt, Klima und Energiewirtschaft Baden-Württemberg', 'http://www.um.baden-wuerttemberg.de/', 1, 2);
INSERT INTO ingrid_provider VALUES (39, 'bw_mi', 'Innenministerium Baden-Württemberg', 'http://www.innenministerium.baden-wuerttemberg.de/', 2, 2);
INSERT INTO ingrid_provider VALUES (40, 'bw_mlr', 'Ministerium für Ländlichen Raum und Verbraucherschutz Baden-Württemberg', 'http://www.mlr.baden-wuerttemberg.de/', 3, 2);
INSERT INTO ingrid_provider VALUES (41, 'bw_mw', 'Ministerium für Finanzen und Wirtschaft Baden-Württemberg', 'http://www.mfw.baden-wuerttemberg.de/', 4, 2);
INSERT INTO ingrid_provider VALUES (42, 'bw_lu', 'Landesanstalt für Umwelt, Messungen und Naturschutz Baden-Württemberg', 'http://www.lubw.baden-wuerttemberg.de/', 5, 2);
INSERT INTO ingrid_provider VALUES (43, 'bw_lgrb', 'Regierungspräsidium Freiburg - Landesamt für Geologie, Rohstoffe und Bergbau', 'http://www.lgrb.uni-freiburg.de', 6, 2);
INSERT INTO ingrid_provider VALUES (44, 'bw_lvm', 'Landesamt für Geoinformation und Landentwicklung Baden-Württemberg', 'http://www.lv-bw.de', 7, 2);
INSERT INTO ingrid_provider VALUES (514, 'bw_vm', 'Ministerium für Verkehr und Infrastruktur Baden-Württemberg', 'http://www.mvi.baden-wuerttemberg.de/', 139, 2);
INSERT INTO ingrid_provider VALUES (47, 'bw_stla', 'Statistisches Landesamt Baden-Württemberg', 'http://www.statistik-bw.de/', 10, 2);
INSERT INTO ingrid_provider VALUES (560, 'bu_fnb', 'Fachnetzwerk Boden', '', 0, 1);
INSERT INTO ingrid_provider VALUES (49, 'by_sugv', 'Bayerisches Staatsministerium für Umwelt und Gesundheit', 'http://www.stmug.bayern.de/', 1, 3);
INSERT INTO ingrid_provider VALUES (200, 'sn_sms', 'Sächsisches Staatsministerium für Soziales', 'http://www.sms.sachsen.de/', 25, 14);
INSERT INTO ingrid_provider VALUES (51, 'by_lfstad', 'Bayerisches Landesamt für Statistik und Datenverarbeitung', 'http://www.statistik.bayern.de/', 3, 3);
INSERT INTO ingrid_provider VALUES (371, 'bu_bbsr', 'Bundesinstitut für Bau-, Stadt- und Raumforschung', 'http://www.bbsr.bund.de', 0, 1);
INSERT INTO ingrid_provider VALUES (54, 'by_brrhoen', 'Biosphärenreservat Rhön', 'http://www.biosphaerenreservat-rhoen.de/', 6, 3);
INSERT INTO ingrid_provider VALUES (55, 'by_npbayw', 'Nationalpark Bayerischer Wald', 'http://www.nationalpark-bayerischer-wald.de/', 7, 3);
INSERT INTO ingrid_provider VALUES (56, 'by_npbg', 'Nationalpark Berchtesgaden', 'http://www.nationalpark-berchtesgaden.de/', 8, 3);
INSERT INTO ingrid_provider VALUES (57, 'be_senst', 'Senatsverwaltung für Stadtentwicklung und Umwelt', 'http://www.stadtentwicklung.berlin.de/', 1, 4);
INSERT INTO ingrid_provider VALUES (58, 'be_snb', 'Stiftung Naturschutz Berlin', 'http://www.stiftung-naturschutz.de/', 2, 4);
INSERT INTO ingrid_provider VALUES (146, 'bu_portalu', 'PortalU - Das Umweltportal Deutschland', 'http://www.portalu.de', 41, 1);
INSERT INTO ingrid_provider VALUES (147, 'st_lagb', 'Landesamt für Geologie und Bergwesen Sachsen-Anhalt (LAGB)', 'http://www.sachsen-anhalt.de/LPSA/index.php?id=15849', 8, 15);
INSERT INTO ingrid_provider VALUES (60, 'bb_mluv', 'Ministerium für Umwelt, Gesundheit und Verbraucherschutz des Landes Brandenburg', 'http://www.mugv.brandenburg.de', 1, 5);
INSERT INTO ingrid_provider VALUES (61, 'hb_sbu', 'Bremer Senator für Umwelt, Bau, Verkehr und Europa', 'http://www.umwelt.bremen.de', 1, 6);
INSERT INTO ingrid_provider VALUES (62, 'hh_su', 'Behörde für Stadtentwicklung und Umwelt Hamburg', 'http://www.hamburg.de/bsu/', 1, 7);
INSERT INTO ingrid_provider VALUES (63, 'hh_wa', 'Behörde für Wirtschaft, Verkehr und Innovation', 'http://www.hamburg.de/bwvi/', 2, 7);
INSERT INTO ingrid_provider VALUES (64, 'hh_bsg', 'Behörde für Gesundheit und Verbraucherschutz', 'http://www.hamburg.de/bgv/', 3, 7);
INSERT INTO ingrid_provider VALUES (65, 'hh_lgv', 'Landesbetrieb Geoinformation und Vermessung Hamburg', 'http://www.hamburg.de/startseite-landesbetrieb-geoinformation-und-vermessung/', 4, 7);
INSERT INTO ingrid_provider VALUES (66, 'hh_npwatt', 'Nationalparkverwaltung Hamburgisches Wattenmeer', 'http://www.nationalpark-wattenmeer.de/hh', 5, 7);
INSERT INTO ingrid_provider VALUES (67, 'hh_argeelbe', 'Flussgebietsgemeinschaft Elbe', 'http://www.fgg-elbe.de/', 6, 7);
INSERT INTO ingrid_provider VALUES (68, 'hh_sth', 'Statistisches Amt für Hamburg und Schleswig-Holstein', 'http://www.statistik-nord.de/', 7, 7);
INSERT INTO ingrid_provider VALUES (69, 'he_hmulv', 'Hessisches Ministerium für Umwelt, Klimaschutz, Landwirtschaft und Verbraucherschutz', 'http://www.umwelt.hessen.de/', 1, 8);
INSERT INTO ingrid_provider VALUES (70, 'he_hlug', 'Hessisches Landesamt für Umwelt und Geologie', 'http://www.hlug.de/', 2, 8);
INSERT INTO ingrid_provider VALUES (71, 'he_umwelt', 'Umweltatlas Hessen', 'http://atlas.umwelt.hessen.de/', 3, 8);
INSERT INTO ingrid_provider VALUES (72, 'mv_um', 'Ministerium für Landwirtschaft, Umwelt und Verbraucherschutz (LU)', 'http://www.lu.mv-regierung.de/', 1, 9);
INSERT INTO ingrid_provider VALUES (73, 'mv_sm', 'Ministerium für Arbeit, Gleichstellung und Soziales', 'http://www.sozial-mv.de/', 2, 9);
INSERT INTO ingrid_provider VALUES (74, 'mv_lung', 'Landesamt für Umwelt, Naturschutz und Geologie Mecklenburg-Vorpommern (LUNG)', 'http://www.lung.mv-regierung.de/', 3, 9);
INSERT INTO ingrid_provider VALUES (75, 'mv_lfmv', 'Landesforst Mecklenburg-Vorpommern AöR', 'http://www.wald-mv.de/', 4, 9);
INSERT INTO ingrid_provider VALUES (76, 'mv_schaalsee', 'Biosphärenreservat Schaalsee', 'http://www.schaalsee.de/', 5, 9);
INSERT INTO ingrid_provider VALUES (77, 'mv_npmueritz', 'Müritz-Nationalpark', 'http://www.nationalpark-mueritz.de/', 6, 9);
INSERT INTO ingrid_provider VALUES (78, 'mv_nvblmv', 'Nationalpark Vorpommersche Boddenlandschaft', 'http://www.nationalpark-vorpommersche-boddenlandschaft.de/', 7, 9);
INSERT INTO ingrid_provider VALUES (79, 'mv_fhstr', 'Fachhochschule Stralsund', 'http://www.fh-stralsund.de/', 8, 9);
INSERT INTO ingrid_provider VALUES (80, 'mv_fhnb', 'Hochschule Neubrandenburg', 'http://www.fh-nb.de/', 9, 9);
INSERT INTO ingrid_provider VALUES (81, 'mv_hswi', 'Hochschule Wismar', 'http://www.hs-wismar.de/', 10, 9);
INSERT INTO ingrid_provider VALUES (82, 'mv_iow', 'Institut für Ostseeforschung Warnemünde', 'http://www.io-warnemuende.de/', 11, 9);
INSERT INTO ingrid_provider VALUES (83, 'mv_unigr', 'Universität Greifswald', 'http://www.uni-greifswald.de/', 12, 9);
INSERT INTO ingrid_provider VALUES (84, 'mv_uniro', 'Universität Rostock', 'http://www.uni-rostock.de/', 13, 9);
INSERT INTO ingrid_provider VALUES (85, 'ni_mu', 'Niedersächsisches Ministerium für Umwelt und Klimaschutz', 'http://www.mu.niedersachsen.de/', 1, 10);
INSERT INTO ingrid_provider VALUES (86, 'ni_mw', 'Niedersächsisches Ministerium für Wirtschaft, Arbeit und Verkehr', 'http://www.mw.niedersachsen.de/', 2, 10);
INSERT INTO ingrid_provider VALUES (87, 'ni_ms', 'Niedersächsisches Ministerium für Soziales, Frauen, Familie, Gesundheit und Integration', 'http://www.ms.niedersachsen.de/', 3, 10);
INSERT INTO ingrid_provider VALUES (88, 'ni_mi', 'Niedersächsisches Ministerium für Inneres und Sport', 'http://www.mi.niedersachsen.de/', 4, 10);
INSERT INTO ingrid_provider VALUES (89, 'ni_ml', 'Niedersächsisches Ministerium für Ernährung, Landwirtschaft, Verbraucherschutz und Landesentwicklung', 'http://www.ml.niedersachsen.de/', 5, 10);
INSERT INTO ingrid_provider VALUES (90, 'ni_nlwkn', 'Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz', 'http://www.nlwkn.niedersachsen.de/', 6, 10);
INSERT INTO ingrid_provider VALUES (91, 'ni_lbeg', 'Niedersächsisches Landesamt für Bergbau, Energie und Geologie', 'http://www.lbeg.niedersachsen.de', 7, 10);
INSERT INTO ingrid_provider VALUES (93, 'ni_laves', 'Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit', 'http://www.laves.niedersachsen.de/', 9, 10);
INSERT INTO ingrid_provider VALUES (94, 'ni_lga', 'Niedersächsisches Landesgesundheitsamt', 'http://www.nlga.niedersachsen.de/', 10, 10);
INSERT INTO ingrid_provider VALUES (95, 'ni_sbv', 'Niedersächsische Landesbehörde für Straßenbau und Verkehr', 'http://www.strassenbau.niedersachsen.de/', 11, 10);
INSERT INTO ingrid_provider VALUES (96, 'ni_nna', 'Alfred Toepfer Akademie für Naturschutz', 'http://www.nna.niedersachsen.de/', 12, 10);
INSERT INTO ingrid_provider VALUES (97, 'ni_gll', 'Behörden für Geoinformation, Landentwicklung und Liegenschaften Niedersachsen', 'http://www.gll.niedersachsen.de', 13, 10);
INSERT INTO ingrid_provider VALUES (99, 'ni_lgn', 'Landesvermessung und Geobasisinformation Niedersachsen', 'http://www.lgn.niedersachsen.de/', 15, 10);
INSERT INTO ingrid_provider VALUES (100, 'ni_lwkh', 'Landwirtschaftskammer Niedersachsen', 'http://www.lwk-niedersachsen.de/', 16, 10);
INSERT INTO ingrid_provider VALUES (103, 'ni_gaa', 'Niedersächsische Gewerbeaufsicht', 'http://www.gewerbeaufsicht.niedersachsen.de', 19, 10);
INSERT INTO ingrid_provider VALUES (105, 'nw_munlv', 'Ministerium für Umwelt und Naturschutz, Landwirtschaft und Verbraucherschutz des Landes Nordrhein-Westfalen', 'http://www.umwelt.nrw.de/', 1, 11);
INSERT INTO ingrid_provider VALUES (176, 'mv_im', 'Ministerium für Inneres und Sport', 'http://www.mv-regierung.de/im/', 14, 9);
INSERT INTO ingrid_provider VALUES (177, 'mv_am', 'Ministerium für Wirtschaft, Bau und Tourismus', 'http://www.wm.mv-regierung.de/index.htm', 15, 9);
INSERT INTO ingrid_provider VALUES (108, 'nw_gd', 'Geologischer Dienst Nordrhein-Westfalen', 'http://www.gd.nrw.de/', 4, 11);
INSERT INTO ingrid_provider VALUES (111, 'nw_ldvst', 'Landesamt für Datenverarbeitung und Statistik Nordrhein-Westfalen', 'http://www.ugrdl.de/index.html', 7, 11);
INSERT INTO ingrid_provider VALUES (112, 'rp_mufv', 'Ministerium für Umwelt, Landwirtschaft, Ernährung, Weinbau und Forsten Rheinland-Pfalz (MULEWF) ', 'http://www.mulewf.rlp.de/', 1, 12);
INSERT INTO ingrid_provider VALUES (115, 'rp_forst', 'Landesforsten Rheinland-Pfalz', 'http://www.wald-rlp.de/', 2, 12);
INSERT INTO ingrid_provider VALUES (116, 'rp_lzu', 'Landeszentrale für Umweltaufklärung Rheinland-Pfalz', 'http://www.umdenken.de/', 3, 12);
INSERT INTO ingrid_provider VALUES (117, 'sl_mfu', 'Ministerium für Umwelt und Verbraucherschutz des Saarland', 'http://www.saarland.de/ministerium_umwelt_verbraucherschutz.htm', 1, 13);
INSERT INTO ingrid_provider VALUES (118, 'sl_lua', 'Landesamt für Umwelt- und Arbeitsschutz des Saarlandes', 'http://www.lua.saarland.de/', 2, 13);
INSERT INTO ingrid_provider VALUES (119, 'sn_smul', 'Sächsisches Staatsministerium für Umwelt und Landwirtschaft', 'http://www.smul.sachsen.de/smul/index.html', 0, 14);
INSERT INTO ingrid_provider VALUES (263, 'bu_sgd', 'Staatliche Geologische Dienste Deutschlands', 'http://www.infogeo.de', 0, 1);
INSERT INTO ingrid_provider VALUES (121, 'sn_lanu', 'Sächsische Landesstiftung Natur und Umwelt', 'http://www.lanu.de/templates/intro.php', 1, 14);
INSERT INTO ingrid_provider VALUES (122, 'st_mlu', 'Ministerium für Landwirtschaft und Umwelt Sachsen-Anhalt (MLU)', 'http://www.mlu.sachsen-anhalt.de', 1, 15);
INSERT INTO ingrid_provider VALUES (496, 'rp_luwg', 'Landesamt für Umwelt, Wasserwirtschaft und Gewerbeaufsicht Rheinland-Pfalz', 'http://www.luwg.rlp.de/', 0, 12);
INSERT INTO ingrid_provider VALUES (123, 'st_lau', 'Landesamt für Umweltschutz Sachsen-Anhalt (LAU)', 'http://www.sachsen-anhalt.de/LPSA/index.php?id=lau', 7, 15);
INSERT INTO ingrid_provider VALUES (124, 'st_lue', 'Luftüberwachungssystem Sachsen-Anhalt (LÜSA)', 'http://www.mu.sachsen-anhalt.de/lau/luesa/', 22, 15);
INSERT INTO ingrid_provider VALUES (125, 'st_unimd', 'Otto-von-Guericke Universität Magdeburg', 'http://www.uni-magdeburg.de/', 23, 15);
INSERT INTO ingrid_provider VALUES (126, 'sh_munl', 'Ministerium für Landwirtschaft, Umwelt und ländliche Räume des Landes Schleswig-Holstein', 'http://www.schleswig-holstein.de/MLUR/DE/MLUR__node.html', 1, 16);
INSERT INTO ingrid_provider VALUES (127, 'sh_lanu', 'Landesamt für Landwirtschaft, Umwelt und ländliche Räume Schleswig-Holstein', 'http://www.schleswig-holstein.de/LLUR/DE/LLUR__node.html', 2, 16);
INSERT INTO ingrid_provider VALUES (128, 'sh_luesh', 'Lufthygienische Überwachung Schleswig-Holstein', 'http://www.umwelt.schleswig-holstein.de/?196', 3, 16);
INSERT INTO ingrid_provider VALUES (129, 'sh_kfue', 'Kernreaktorfernüberwachung Schleswig-Holstein', 'http://www.kfue-sh.de/', 4, 16);
INSERT INTO ingrid_provider VALUES (130, 'sh_umweltaka', 'Bildungszentrum für Natur, Umwelt und ländliche Räume der Landes Schleswig-Holstein', 'http://www.umweltakademie-sh.de/', 5, 16);
INSERT INTO ingrid_provider VALUES (131, 'sh_npa', 'Nationalpark Schleswig-Holsteinisches Wattenmeer', 'http://www.nationalpark-wattenmeer.de/sh', 6, 16);
INSERT INTO ingrid_provider VALUES (132, 'th_tmlnu', 'Thüringer Ministerium für Landwirtschaft, Forsten, Umwelt und Naturschutz', 'http://www.thueringen.de/th8/tmlfun/', 1, 17);
INSERT INTO ingrid_provider VALUES (133, 'th_tlug', 'Thüringer Landesanstalt für Umwelt und Geologie', 'http://www.tlug-jena.de/de/tlug/', 2, 17);
INSERT INTO ingrid_provider VALUES (138, 'rp_lua', 'Landesuntersuchungsamt Rheinland-Pfalz', 'http://www.lua.rlp.de/', 5, 12);
INSERT INTO ingrid_provider VALUES (139, 'bw_saa', 'SAA Sonderabfallagentur Baden-Württemberg', 'http://www.saa.de/', 13, 2);
INSERT INTO ingrid_provider VALUES (140, 'bw_rp', 'Regierungspräsidien Baden-Württemberg', 'http://www.rp.baden-wuerttemberg.de/', 14, 2);
INSERT INTO ingrid_provider VALUES (141, 'bw_rps', 'Regierungspräsidium Stuttgart', 'http://www.rp.baden-wuerttemberg.de/servlet/PB/menu/1007480/index.html', 15, 2);
INSERT INTO ingrid_provider VALUES (142, 'bu_lai', 'Bund/Länder Arbeitsgemeinschaft für Immissionsschutz - LAI', 'http://www.lai-immissionsschutz.de', 39, 1);
INSERT INTO ingrid_provider VALUES (369, 'hb_gi', 'GeoInformation Bremen', 'http://www.geo.bremen.de', 0, 6);
INSERT INTO ingrid_provider VALUES (145, 'he_hvbg', 'Hessische Verwaltung für Bodenmanagement und Geoinformation', 'http://www.hvbg.hessen.de/', 5, 8);
INSERT INTO ingrid_provider VALUES (150, 'sn_lfp', 'Staatsbetrieb Sachsenforst', 'http://www.sachsenforst.de', 5, 14);
INSERT INTO ingrid_provider VALUES (151, 'sn_np-saechsische-schweiz', 'Nationalpark Sächsische Schweiz', 'http://www.nationalpark-saechsische-schweiz.de/startseite/index.php', 6, 14);
INSERT INTO ingrid_provider VALUES (152, 'sn_ltv', 'Landestalsperrenverwaltung Sachsen', 'http://www.smul.sachsen.de/de/wu/organisation/staatsbetriebe/ltv/index_start.html', 7, 14);
INSERT INTO ingrid_provider VALUES (153, 'sn_brv', 'Biosphärenreservatsverwaltung Oberlausitzer Heide- und Teichlandschaft', 'http://www.biosphaerenreservat-oberlausitz.de/index.html', 8, 14);
INSERT INTO ingrid_provider VALUES (154, 'sn_statistik', 'Statistisches Landesamt Sachsen', 'http://www.statistik.sachsen.de/', 26, 14);
INSERT INTO ingrid_provider VALUES (155, 'st_ms', 'Ministerium für Arbeit und Soziales Sachsen-Anhalt (MS) ', 'http://www.ms.sachsen-anhalt.de', 2, 15);
INSERT INTO ingrid_provider VALUES (156, 'st_mw', 'Ministerium für Wissenschaft und Wirtschaft  Sachsen-Anhalt (MW)', 'http://www.mw.sachsen-anhalt.de', 3, 15);
INSERT INTO ingrid_provider VALUES (157, 'st_mlv', 'Ministerium für Landesentwicklung und Verkehr (MLV)', 'http://www.mlv.sachsen-anhalt.de', 4, 15);
INSERT INTO ingrid_provider VALUES (158, 'st_mi', 'Ministerium des Innern Sachsen-Anhalt (MI)', 'http://www.mi.sachsen-anhalt.de', 5, 15);
INSERT INTO ingrid_provider VALUES (159, 'st_mj', 'Ministerium der Justiz und Gleichstellung Sachsen-Anhalt (MJ)', 'http://www.mj.sachsen-anhalt.de', 6, 15);
INSERT INTO ingrid_provider VALUES (160, 'st_lvermgeo', 'Landesamt für Vermessung und Geoinformationen (LVermGeo)', 'http://www.lvermgeo.sachsen-anhalt.de', 9, 15);
INSERT INTO ingrid_provider VALUES (161, 'st_stala', 'Statistisches Landesamt Sachsen-Anhalt (StaLa)', 'http://www.stala.sachsen-anhalt.de', 10, 15);
INSERT INTO ingrid_provider VALUES (162, 'st_lav', 'Landesamt für Verbraucherschutz Sachsen-Anhalt (LAV)', 'http://www.verbraucherschutz.sachsen-anhalt.de', 11, 15);
INSERT INTO ingrid_provider VALUES (163, 'st_lfa', 'Landesamt für Denkmalpflege und Archäologie (LDA)', 'http://www.archlsa.de', 12, 15);
INSERT INTO ingrid_provider VALUES (164, 'st_laf', 'Landesanstalt für Altlastenfreistellung Sachsen-Anhalt (LAF)', 'http://www.laf-lsa.de', 13, 15);
INSERT INTO ingrid_provider VALUES (165, 'st_llfg', 'Landesanstalt für Landwirtschaft, Forsten und Gartenbau Sachsen-Anhalt (LLFG)', 'http://www.llg-lsa.de', 14, 15);
INSERT INTO ingrid_provider VALUES (166, 'st_lhw', 'Landesbetrieb für Hochwasserschutz und Wasserwirtschaft Sachsen-Anhalt (LHW)', 'http://www.sachsen-anhalt.de/LPSA/index.php?id=13427', 15, 15);
INSERT INTO ingrid_provider VALUES (167, 'st_talsperren', 'Talsperrenbetrieb Sachsen-Anhalt (TSB-LSA)', 'http://www.talsperren-lsa.de', 16, 15);
INSERT INTO ingrid_provider VALUES (168, 'st_landesforstbetrieb', 'Landesforstbetrieb Sachsen-Anhalt (LFB)', 'http://www.landesforstbetrieb.sachsen-anhalt.de', 17, 15);
INSERT INTO ingrid_provider VALUES (169, 'st_lvwa', 'Landesverwaltungsamt Sachsen-Anhalt (LVwA)', 'http://www.landesverwaltungsamt.sachsen-anhalt.de', 18, 15);
INSERT INTO ingrid_provider VALUES (170, 'st_bioreskarstsuedharz', 'Biosphärenreservat Karstlandschaft Südharz', 'http://www.bioreskarstsuedharz.de', 19, 15);
INSERT INTO ingrid_provider VALUES (171, 'st_biosphaerenreservatmittlereelbe', 'Biosphärenreservat Mittelelbe/Flusslandschaft Elbe', 'http://www.biosphaerenreservatmittlereelbe.de', 20, 15);
INSERT INTO ingrid_provider VALUES (172, 'st_nationalpark-harz', 'Nationalparkverwaltung Harz', 'http://www.nationalpark-harz.de', 21, 15);
INSERT INTO ingrid_provider VALUES (173, 'be_senges', 'Senatsverwaltung für Gesundheit, Umwelt und Verbraucherschutz Berlin', 'http://www.berlin.de/sen/umwelt/index.shtml', 3, 4);
INSERT INTO ingrid_provider VALUES (174, 'nw_lanuv', 'Landesamt für Natur, Umwelt und Verbraucherschutz NRW  (LANUV)', 'http://www.lanuv.nrw.de/', 8, 11);
INSERT INTO ingrid_provider VALUES (202, 'bu_bdj', 'Bundesministerium der Justiz', 'http://www.bmj.bund.de', 0, 1);
INSERT INTO ingrid_provider VALUES (178, 'mv_jm', 'Justizministerium Mecklenburg-Vorpommern', 'http://www.jm.mv-regierung.de/', 16, 9);
INSERT INTO ingrid_provider VALUES (179, 'mv_bm', 'Ministerium für Bildung, Wissenschaft und Kultur', 'http://www.bm.regierung-mv.de', 17, 9);
INSERT INTO ingrid_provider VALUES (180, 'mv_vm', 'Ministerium für Energie, Infrastruktur und Landesentwicklung', 'http://www.mv-regierung.de/vm/', 18, 9);
INSERT INTO ingrid_provider VALUES (181, 'mv_sta', 'Statistisches Amt Mecklenburg-Vorpommern', 'http://www.statistik-mv.de/', 19, 9);
INSERT INTO ingrid_provider VALUES (511, 'sh_masg', 'Ministerium für Arbeit, Soziales und Gesundheit', 'http://www.schleswig-holstein.de/MASG/DE/MASG_node.html', 0, 16);
INSERT INTO ingrid_provider VALUES (184, 'mv_stnb', 'Staatliches Amt für Landwirtschaft und Umwelt Mecklenburgische Seenplatte (StALU MS)', 'http://www.mv-regierung.de/staeun/neubrandenburg/', 21, 9);
INSERT INTO ingrid_provider VALUES (185, 'mv_sthst', 'Staatliches Amt für Landwirtschaft und Umwelt Vorpommern (StALU VP)', 'http://www.mv-regierung.de/staeun/stralsund_n/', 22, 9);
INSERT INTO ingrid_provider VALUES (186, 'mv_sthro', 'Staatliches Amt für Landwirtschaft und Umwelt Mittleres Mecklenburg (StALU MM)', 'http://www.mv-regierung.de/staeun/rostock/', 23, 9);
INSERT INTO ingrid_provider VALUES (187, 'mv_stsn', 'Staatliches Amt für Landwirtschaft und Umwelt Westmecklenburg (StALU WM)', 'http://www.mv-regierung.de/staeun/schwerin/', 24, 9);
INSERT INTO ingrid_provider VALUES (188, 'mv_lallf', 'Landesamt für Landwirtschaft, Lebensmittelsicherheit und Fischerei', 'http://www.lallf.de/', 25, 9);
INSERT INTO ingrid_provider VALUES (189, 'mv_afrl', 'Ämter für Raumordnung und Landesplanung Mecklenburg-Vorpommern', 'http://cms.mvnet.de/cms2/AFRL_prod/AFRL/index.jsp', 26, 9);
INSERT INTO ingrid_provider VALUES (196, 'bu_fli', 'Friedrich-Löffler-Institut', 'http://www.fli.bund.de', 42, 1);
INSERT INTO ingrid_provider VALUES (197, 'bu_nrat', 'Rat für Nachhaltige Entwicklung', 'http://www.nachhaltigkeitsrat.de', 43, 1);
INSERT INTO ingrid_provider VALUES (195, 'ni_fho', 'Fachhochschule Osnabrück', 'http://www.hs-osnabrueck.de/', 22, 10);
INSERT INTO ingrid_provider VALUES (194, 'be_npb', 'Naturpark Barnim', 'http://www.np-barnim.de/', 4, 4);
INSERT INTO ingrid_provider VALUES (198, 'bu_baw', 'Bundesanstalt für Wasserbau', 'http://www.baw.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (199, 'by_lumsch', 'Bayerisches Landesamt für Umwelt', 'http://www.lfu.bayern.de', 9, 3);
INSERT INTO ingrid_provider VALUES (201, 'sn_lus', 'Landesuntersuchungsanstalt Sachsen', 'http://www.lua.sachsen.de/', 16, 14);
INSERT INTO ingrid_provider VALUES (204, 'by_sdj', 'Bayerisches Staatsministerium der Justiz', 'http://www.justiz.bayern.de/', 0, 3);
INSERT INTO ingrid_provider VALUES (494, 'bu_dena', 'Deutsche Energie-Agentur GmbH (dena)', 'http://www.dena.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (206, 'hh_bserv', 'Hamburger Gerichte', 'http://justiz.hamburg.de/gerichte/', 0, 7);
INSERT INTO ingrid_provider VALUES (207, 'hh_justiz', 'Behörde für Justiz und Gleichstellung', 'http://www.hamburg.de/justizbehoerde/', 0, 7);
INSERT INTO ingrid_provider VALUES (208, 'he_stk', 'Hessische Staatskanzlei', 'http://www.stk.hessen.de/', 0, 8);
INSERT INTO ingrid_provider VALUES (209, 'ni_stk', 'Niedersächsische Staatskanzlei', 'http://www.stk.niedersachsen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (210, 'nw_im', 'Innenministerium des Landes Nordrhein-Westfalen', 'http://www.im.nrw.de/', 0, 11);
INSERT INTO ingrid_provider VALUES (212, 'sl_justiz', 'Ministerium der Justiz des Saarlandes', 'http://www.saarland.de/ministerium_justiz.htm', 0, 13);
INSERT INTO ingrid_provider VALUES (370, 'bu_dbu', 'Deutsche Bundesstiftung Umwelt', 'http://www.dbu.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (214, 'sh_lreg', 'Landesregierung Schleswig-Holstein', 'http://www.schleswig-holstein.de', 0, 16);
INSERT INTO ingrid_provider VALUES (215, 'th_geo', 'Thüringer Landesamt für Vermessung und Geoinformation', 'http://www.thueringen.de/th9/tlvermgeo/index.aspx', 0, 17);
INSERT INTO ingrid_provider VALUES (216, 'th_justiz', 'Justizministerium des Freistaats Thüringen', 'http://www.thueringen.de/de/justiz/', 0, 17);
INSERT INTO ingrid_provider VALUES (217, 'mv_bergamt', 'Bergamt Mecklenburg-Vorpommern', 'http://www.bergamt-mv.de/', 0, 9);
INSERT INTO ingrid_provider VALUES (218, 'mv_npjasm', 'Nationalpark Jasmund', 'http://www.nationalpark-vorpommersche-boddenlandschaft.de/jasmund/', 0, 9);
INSERT INTO ingrid_provider VALUES (219, 'mv_natnat', 'Nationale Naturlandschaften', 'http://www.natur-mv.de/', 0, 9);
INSERT INTO ingrid_provider VALUES (221, 'bu_bmwi', 'Bundesministerium für Wirtschaft und Technologie', 'http://www.bmwi.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (222, 'nw_brarn', 'Bezirksregierung Arnsberg', 'http://www.bezreg-arnsberg.nrw.de/', 0, 11);
INSERT INTO ingrid_provider VALUES (223, 'nw_brdet', 'Bezirksregierung Detmold', 'http://www.bezreg-detmold.nrw.de', 0, 11);
INSERT INTO ingrid_provider VALUES (224, 'nw_brdue', 'Bezirksregierung Düsseldorf', 'http://www.bezreg-duesseldorf.nrw.de', 0, 11);
INSERT INTO ingrid_provider VALUES (225, 'nw_brkoe', 'Bezirksregierung Köln', 'http://www.bezreg-koeln.nrw.de', 0, 11);
INSERT INTO ingrid_provider VALUES (226, 'nw_brmue', 'Bezirksregierung Münster', 'http://www.bezreg-muenster.nrw.de', 0, 11);
INSERT INTO ingrid_provider VALUES (227, 'nw_lbwuh', 'Landesbetrieb Wald und Holz NRW', 'http://www.wald-und-holz.nrw.de', 0, 11);
INSERT INTO ingrid_provider VALUES (228, 'nw_lwk', 'Landwirtschaftskammer Nordrhein-Westfalen', 'http://www.landwirtschaftskammer.de', 0, 11);
INSERT INTO ingrid_provider VALUES (229, 'rp_sgds', 'Struktur- und Genehmigungsdirektion Süd Rheinland-Pfalz', 'http://www.sgdsued.rlp.de', 6, 12);
INSERT INTO ingrid_provider VALUES (230, 'rp_sgdn', 'Struktur- und Genehmigungsdirektion Nord Rheinland-Pfalz', 'http://www.sgdnord.rlp.de', 7, 12);
INSERT INTO ingrid_provider VALUES (231, 'rp_mwvlw', 'Ministerium für Wirtschaft, Klimaschutz, Energie und Landesplanung Rheinland-Pfalz', 'http://www.mwkel.rlp.de/Startseite/', 8, 12);
INSERT INTO ingrid_provider VALUES (232, 'rp_lgb', 'Landesamt für Geologie und Bergbau Rheinland-Pfalz', 'http://www.lgb-rlp.de', 9, 12);
INSERT INTO ingrid_provider VALUES (233, 'rp_lbm', 'Landesbetrieb Mobilität Rheinland-Pfalz', 'http://www.lbm.rlp.de', 10, 12);
INSERT INTO ingrid_provider VALUES (234, 'rp_ism', 'Ministerium des Innern, für Sport und Infrastruktur Rheinland-Pfalz', 'http://www.isim.rlp.de/', 11, 12);
INSERT INTO ingrid_provider VALUES (235, 'rp_lvermgeo', 'Landesamt für Vermessung und Geobasisinformationen Rheinland-Pfalz', 'http://www.lvermgeo.rlp.de', 12, 12);
INSERT INTO ingrid_provider VALUES (236, 'rp_sla', 'Statistisches Landesamt Rheinland-Pfalz', 'http://www.statistik.rlp.de', 13, 12);
INSERT INTO ingrid_provider VALUES (237, 'rp_lfks', 'Feuerwehr- und Katastrophenschutzschule Rheinland-Pfalz', 'http://www.lfks-rlp.de', 14, 12);
INSERT INTO ingrid_provider VALUES (238, 'rp_masgff', 'Ministerium für Soziales, Arbeit, Gesundheit und Demografie Rheinland-Pfalz', 'http://msagd.rlp.de/', 15, 12);
INSERT INTO ingrid_provider VALUES (240, 'bu_baua', 'Bundesanstalt für Arbeitsschutz und Arbeitsmedizin', 'http://www.baua.bund.de', 0, 1);
INSERT INTO ingrid_provider VALUES (241, 'ni_lk_row', 'Landkreis Rotenburg-Wümme', 'http://www.lk-row.de', 0, 10);
INSERT INTO ingrid_provider VALUES (242, 'ni_lk_nh', 'Landkreis Northeim', 'http://www.landkreis-northeim.de/', 0, 10);
INSERT INTO ingrid_provider VALUES (243, 'ni_reg-hann', 'Region Hannover', 'http://www.hannover.de', 0, 10);
INSERT INTO ingrid_provider VALUES (244, 'ni_lhst-hann', 'Landeshauptstadt Hannover', 'http://www.hannover.de', 0, 10);
INSERT INTO ingrid_provider VALUES (245, 'ni_wb', 'Stadt Wolfsburg', 'http://www.wolfsburg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (246, 'ni_li', 'Stadt Lingen', 'http://www.lingen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (247, 'ni_lk_row_gb', 'Gemeinde Gnarrenburg', 'http://www.gnarrenburg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (248, 'ni_lk_row_se', 'Samtgemeinde Selsingen', 'http://www.selsingen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (249, 'ni_lk_row_si', 'Samtgemeinde Sittensen', 'http://www.sittensen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (250, 'ni_lk_row_rw', 'Stadt Rotenburg (Wümme)', 'http://www.rotenburg-wuemme.de', 0, 10);
INSERT INTO ingrid_provider VALUES (251, 'ni_lk_row_vi', 'Stadt Visselhövede', 'http://www.visselhoevede.de', 0, 10);
INSERT INTO ingrid_provider VALUES (252, 'ni_lk_row_so', 'Samtgemeinde Sottrum', 'http://www.sottrum.de', 0, 10);
INSERT INTO ingrid_provider VALUES (253, 'ni_lk_row_ze', 'Samtgemeinde Zeven', 'http://www.zeven.de', 0, 10);
INSERT INTO ingrid_provider VALUES (254, 'ni_lk_row_fi', 'Samtgemeinde Fintel', 'http://www.fintel.de', 0, 10);
INSERT INTO ingrid_provider VALUES (255, 'ni_lk_row_bo', 'Samtgemeinde Bothel', 'http://www.bothel.de', 0, 10);
INSERT INTO ingrid_provider VALUES (256, 'ni_lk_row_ta', 'Samtgemeinde Tarmstedt', 'http://www.tarmstedt.de', 0, 10);
INSERT INTO ingrid_provider VALUES (257, 'ni_lk_row_bv', 'Stadt Bremervörde', 'http://www.bremervoerde.de', 0, 10);
INSERT INTO ingrid_provider VALUES (258, 'ni_lk_row_sc', 'Gemeinde Scheeßel', 'http://www.scheessel.de', 0, 10);
INSERT INTO ingrid_provider VALUES (259, 'bw_fva', 'Forstliche Versuchs- und Forschungsanstalt Baden-Württemberg', 'http://www.fva-bw.de', 16, 2);
INSERT INTO ingrid_provider VALUES (260, 'rp_kgstgdi', 'Kompetenz- und Geschäftsstelle GDI Rheinland-Pfalz', 'http://www.geoportal.rlp.de/portal/ueber-uns/kompetenz-und-geschaeftsstelle.html', 0, 12);
INSERT INTO ingrid_provider VALUES (562, 'ni_st_rinteln', 'Stadt Rinteln', 'http://www.rinteln.de/', 0, 10);
INSERT INTO ingrid_provider VALUES (262, 'sn_lfulg', 'Sächsisches Landesamt für Umwelt, Landwirtschaft und Geologie', 'http://www.umwelt.sachsen.de/lfulg/', 3, 14);
INSERT INTO ingrid_provider VALUES (264, 'ni_st_bu', 'Stadt Buchholz i. d. N.', 'http://www.buchholz.de', 0, 10);
INSERT INTO ingrid_provider VALUES (265, 'ni_lk_ue', 'Landkreis Uelzen', 'http://www.uelzen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (266, 'ni_st_lg', 'Hansestadt Lüneburg', 'http://www.lueneburg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (267, 'ni_sg_ldf', 'Samtgemeinde Lachendorf', 'http://www.lachendorf.de', 0, 10);
INSERT INTO ingrid_provider VALUES (268, 'ni_st_ce', 'Stadt Celle', 'http://www.celle.de', 0, 10);
INSERT INTO ingrid_provider VALUES (269, 'ni_lk_ce', 'Landkreis Celle', 'http://www.landkreis-celle.de', 0, 10);
INSERT INTO ingrid_provider VALUES (270, 'ni_lk_ld', 'Landkreis Lüchow-Dannenberg', 'http://www.luechow-dannenberg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (271, 'ni_sg_oh', 'Samtgemeinde Oberharz', 'http://www.samtgemeinde-oberharz.de', 0, 10);
INSERT INTO ingrid_provider VALUES (272, 'ni_st_cux', 'Stadt Cuxhaven', 'http://www.cuxhaven.de', 0, 10);
INSERT INTO ingrid_provider VALUES (273, 'ni_st_ach', 'Stadt Achim', 'http://www.achim.de', 0, 10);
INSERT INTO ingrid_provider VALUES (274, 'ni_lk_ost', 'Landkreis Osterholz', 'http://www.landkreis-osterholz.de', 0, 10);
INSERT INTO ingrid_provider VALUES (275, 'ni_lk_ver', 'Landkreis Verden', 'http://www.landkreis-verden.de', 0, 10);
INSERT INTO ingrid_provider VALUES (276, 'ni_st_ver', 'Stadt Verden', 'http://www.verden.de', 0, 10);
INSERT INTO ingrid_provider VALUES (277, 'ni_lk_wolf', 'Landkreis Wolfenbüttel', 'http://www.lk-wf.de', 0, 10);
INSERT INTO ingrid_provider VALUES (278, 'ni_st_salz', 'Stadt Salzgitter', 'http://www.salzgitter.de', 0, 10);
INSERT INTO ingrid_provider VALUES (279, 'ni_st_koe', 'Stadt Königslutter', 'http://www.koenigslutter.de', 0, 10);
INSERT INTO ingrid_provider VALUES (280, 'ni_lk_pe', 'Landkreis Peine', 'http://www.landkreis-peine.de', 0, 10);
INSERT INTO ingrid_provider VALUES (281, 'ni_lk_helm', 'Landkreis Helmstedt', 'http://www.landkreis-helmstedt.de', 0, 10);
INSERT INTO ingrid_provider VALUES (282, 'ni_st_goe', 'Stadt Göttingen', 'http://www.goettingen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (283, 'ni_st_oster', 'Stadt Osterode', 'http://www.osterode.de', 0, 10);
INSERT INTO ingrid_provider VALUES (284, 'ni_lk_gos', 'Landkreis Goslar', 'http://www.landkreis-goslar.de', 0, 10);
INSERT INTO ingrid_provider VALUES (285, 'ni_lk_goe', 'Landkreis Göttingen', 'http://www.landkreisgoettingen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (286, 'ni_st_see', 'Stadt Seelze', 'http://www.stadt-seelze.de', 0, 10);
INSERT INTO ingrid_provider VALUES (287, 'ni_lk_oster', 'Landkreis Osterode', 'http://www.landkreis-osterode.de', 0, 10);
INSERT INTO ingrid_provider VALUES (288, 'ni_st_lang', 'Stadt Langelsheim', 'http://www.langelsheim.de', 0, 10);
INSERT INTO ingrid_provider VALUES (289, 'bu_bbr', 'Bundesamt für Bauwesen und Raumordnung', 'http://www.bbr.bund.de', 0, 1);
INSERT INTO ingrid_provider VALUES (290, 'ni_lk_hi', 'Landkreis Hildesheim', 'http://www.landkreishildesheim.de', 0, 10);
INSERT INTO ingrid_provider VALUES (291, 'ni_st_wu', 'Stadt Wunstorf', 'http://www.wunstorf.de', 0, 10);
INSERT INTO ingrid_provider VALUES (292, 'ni_st_neust', 'Stadt Neustadt a. Rbge.', 'http://www.neustadt-a-rbge.de', 0, 10);
INSERT INTO ingrid_provider VALUES (293, 'ni_lk_schaum', 'Landkreis Schaumburg', 'http://www.landkreis-schaumburg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (294, 'ni_sg_sachs', 'Samtgemeinde Sachsenhagen', 'http://www.sachsenhagen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (295, 'ni_st_ham', 'Stadt Hameln', 'http://www.hameln.de', 0, 10);
INSERT INTO ingrid_provider VALUES (296, 'ni_lk_ham', 'Landkreis Hameln-Pyrmont', 'http://www.hameln-pyrmont.de', 0, 10);
INSERT INTO ingrid_provider VALUES (297, 'ni_lk_nien', 'Landkreis Nienburg/Weser', 'http://www.kreis-ni.de', 0, 10);
INSERT INTO ingrid_provider VALUES (298, 'ni_st_bars', 'Stadt Barsinghausen', 'http://www.stadt-barsinghausen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (299, 'ni_st_spri', 'Stadt Springe', 'http://www.springe.de', 0, 10);
INSERT INTO ingrid_provider VALUES (300, 'ni_lk_weser', 'Landkreis Wesermarsch', 'http://www.lkbra.de', 0, 10);
INSERT INTO ingrid_provider VALUES (301, 'ni_lk_clop', 'Landkreis Cloppenburg', 'http://www.lkclp.de', 0, 10);
INSERT INTO ingrid_provider VALUES (302, 'ni_lk_ammer', 'Landkreis Ammerland', 'http://www.ammerland.de', 0, 10);
INSERT INTO ingrid_provider VALUES (303, 'ni_st_jever', 'Stadt Jever', 'http://www.stadt-jever.de', 0, 10);
INSERT INTO ingrid_provider VALUES (304, 'ni_lk_fries', 'Landkreis Friesland', 'http://www.landkreis-friesland.de', 0, 10);
INSERT INTO ingrid_provider VALUES (305, 'ni_lk_old', 'Landkreis Oldenburg', 'http://www.oldenburg-kreis.de', 0, 10);
INSERT INTO ingrid_provider VALUES (306, 'ni_lk_leer', 'Landkreis Leer', 'http://www.lkleer.de', 0, 10);
INSERT INTO ingrid_provider VALUES (307, 'ni_st_wil', 'Stadt Wilhelmshaven', 'http://www.wilhelmshaven.de', 0, 10);
INSERT INTO ingrid_provider VALUES (308, 'ni_lk_ems', 'Landkreis Emsland', 'http://www.emsland.de', 0, 10);
INSERT INTO ingrid_provider VALUES (309, 'ni_st_pap', 'Stadt Papenburg', 'http://www.papenburg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (310, 'ni_lk_osn', 'Landkreis Osnabrück', 'http://www.lkos.de', 0, 10);
INSERT INTO ingrid_provider VALUES (311, 'ni_st_bram', 'Stadt Bramsche', 'http://www.stadt-bramsche.de', 0, 10);
INSERT INTO ingrid_provider VALUES (312, 'ni_ge_belm', 'Gemeinde Belm', 'http://www.belm.de', 0, 10);
INSERT INTO ingrid_provider VALUES (313, 'ni_st_bers', 'Stadt Bersenbrück', 'http://www.bersenbrueck.de', 0, 10);
INSERT INTO ingrid_provider VALUES (314, 'ni_lk_vech', 'Landkreis Vechta', 'http://www.landkreis-vechta.de', 0, 10);
INSERT INTO ingrid_provider VALUES (316, 'sn_av', 'Anglerverband Sachsen e.V.', 'http://www.av-sachsen.de/data/home.htm', 49, 14);
INSERT INTO ingrid_provider VALUES (317, 'sn_bund', 'Bund für Umwelt und Naturschutz Deutschland (BUND) - Landeverband Sachsen e.V.', 'http://www.bund-sachsen.de', 44, 14);
INSERT INTO ingrid_provider VALUES (318, 'sn_gls', 'Grüne Liga Sachsen e.V.', 'http://www.grueneliga.de/sachsen', 45, 14);
INSERT INTO ingrid_provider VALUES (319, 'bu_dekade', 'UN-Dekade - Bildung für nachhaltige Entwicklung', 'http://www.bne-portal.de', 0, 1);
INSERT INTO ingrid_provider VALUES (320, 'bu_ki', 'Gemeinnützigen Kinderumwelt GmbH', 'http://www.allum.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (322, 'sn_dd', 'Stadt Dresden', 'http://www.dresden.de/', 2, 14);
INSERT INTO ingrid_provider VALUES (324, 'sn_sgv', 'Sächsische Gestütsverwaltung', 'http://www.smul.sachsen.de/sgv/index.html', 11, 14);
INSERT INTO ingrid_provider VALUES (325, 'sn_burgstaedt', 'Stadt Burgstädt', 'http://www.burgstaedt.de/', 12, 14);
INSERT INTO ingrid_provider VALUES (326, 'sn_chemnitz', 'Stadt Chemnitz', 'http://www.chemnitz.de/', 13, 14);
INSERT INTO ingrid_provider VALUES (497, 'rp_jm', 'Ministerium der Justiz und für Verbraucherschutz Rheinland Pfalz', 'http://www.mjv.rlp.de/Startseite/', 0, 12);
INSERT INTO ingrid_provider VALUES (328, 'sn_lk_mittelsachsen', 'Landkreis Mittelsachsen', 'http://www.landkreis-mittelsachsen.de/', 65, 14);
INSERT INTO ingrid_provider VALUES (330, 'sn_lk_zwickau', 'Landkreis Zwickau', 'http://www.landkreis-zwickau.de/', 18, 14);
INSERT INTO ingrid_provider VALUES (544, 'ni_bubmj', 'Bundesministerium der Justiz', '', 0, 10);
INSERT INTO ingrid_provider VALUES (333, 'sn_smwa', 'Sächsisches Staatsministerium für Wirtschaft und Arbeit', 'http://www.smwa.sachsen.de/', 21, 14);
INSERT INTO ingrid_provider VALUES (335, 'sn_ldc', 'Landesdirektion Chemnitz', 'http://www.ldc.sachsen.de/', 23, 14);
INSERT INTO ingrid_provider VALUES (336, 'sn_ldl', 'Landesdirektion Leipzig', 'http://www.ldl.sachsen.de/', 24, 14);
INSERT INTO ingrid_provider VALUES (337, 'sn_tu_dd', 'Technische Universität Dresden', 'http://www.tu-dresden.de/', 27, 14);
INSERT INTO ingrid_provider VALUES (338, 'sn_tu_fg', 'Technische Universität Bergakademie Freiberg', 'http://www.tu-freiberg.de/', 28, 14);
INSERT INTO ingrid_provider VALUES (339, 'sn_uni_l', 'Universität Leipzig', 'http://www.uni-leipzig.de/', 29, 14);
INSERT INTO ingrid_provider VALUES (340, 'sn_tu_c', 'Technische Universität Chemnitz', 'http://www.tu-chemnitz.de/', 30, 14);
INSERT INTO ingrid_provider VALUES (341, 'sn_l', 'Stadt Leipzig', 'http://www.leipzig.de/', 32, 14);
INSERT INTO ingrid_provider VALUES (342, 'sn_smi', 'Sächsisches Staatsministerium des Innern', 'http://www.smi.sachsen.de/', 33, 14);
INSERT INTO ingrid_provider VALUES (345, 'sn_aue', 'Stadt Aue', 'http://www.aue.de/', 34, 14);
INSERT INTO ingrid_provider VALUES (346, 'sn_coswig', 'Stadt Coswig', 'http://www.coswig.de/', 35, 14);
INSERT INTO ingrid_provider VALUES (347, 'sn_freital', 'Stadt Freital', 'http://www.freital.de/', 36, 14);
INSERT INTO ingrid_provider VALUES (348, 'sn_glauchau', 'Stadt Glauchau', 'http://www.glauchau.de/', 37, 14);
INSERT INTO ingrid_provider VALUES (349, 'sn_goerlitz', 'Stadt Görlitz', 'http://www.goerlitz.de/', 38, 14);
INSERT INTO ingrid_provider VALUES (350, 'sn_limbach-oberfrohna', 'Stadt Limbach-Oberfrohna', 'http://www.limbach-oberfrohna.de/', 39, 14);
INSERT INTO ingrid_provider VALUES (351, 'sn_limbach-oberfrohna', 'Stadt Limbach-Oberfrohna', 'http://www.limbach-oberfrohna.de/', 39, 14);
INSERT INTO ingrid_provider VALUES (352, 'sn_markkleeberg', 'Stadt Markkleeberg', 'http://www.markkleeberg.de', 40, 14);
INSERT INTO ingrid_provider VALUES (353, 'sn_lk_meissen', 'Landkreis Meißen', 'http://www.kreis-meissen.org/', 41, 14);
INSERT INTO ingrid_provider VALUES (354, 'sn_lk_vogtland', 'Landkreis Vogtland', 'http://www.vogtlandkreis.de/', 42, 14);
INSERT INTO ingrid_provider VALUES (355, 'sn_nabu', 'Naturschutzbund Deutschlands (NABU) - Landesverband Sachsen', 'http://www.nabu-sachsen.de/', 43, 14);
INSERT INTO ingrid_provider VALUES (356, 'sn_lsh', 'Landesverein Sächsischer Heimatschutz e.V.', 'http://www.saechsischer-heimatschutz.de/', 46, 14);
INSERT INTO ingrid_provider VALUES (357, 'sn_sdw', 'Schutzgemeinschaft Deutscher Wald - Landesverband Sachsen', 'http://www.sdw-sachsen.de', 47, 14);
INSERT INTO ingrid_provider VALUES (375, 'bw_cvua', 'Chemische und Veterinäruntersuchungsämter (CVUA)', 'http://www.untersuchungsaemter-bw.de', 17, 2);
INSERT INTO ingrid_provider VALUES (360, 'sn_grimma', 'Stadt Grimma', 'http://www.grimma.de/', 52, 14);
INSERT INTO ingrid_provider VALUES (361, 'sn_naturparke', 'Naturparke in Sachsen', 'http://www.naturparke.de', 53, 14);
INSERT INTO ingrid_provider VALUES (362, 'sn_smk', 'Sächsisches Staatsministerium für Kultus', 'http://www.sachsen-macht-schule.de/smk/', 54, 14);
INSERT INTO ingrid_provider VALUES (363, 'sn_saena', 'Sächsische Energieagentur - SAENA GmbH', 'http://www.saena.de/', 55, 14);
INSERT INTO ingrid_provider VALUES (364, 'sn_lk_bautzen', 'Landkreis Bautzen', 'http://www.landkreis-bautzen.de/', 59, 14);
INSERT INTO ingrid_provider VALUES (365, 'sn_lk_nordsachsen', 'Landkreis Nordsachsen', 'http://www.landkreis-nordsachsen.de/', 63, 14);
INSERT INTO ingrid_provider VALUES (366, 'sn_lk_leipzig', 'Landkreis Leipzig', 'http://www.landkreisleipzig.de/', 69, 14);
INSERT INTO ingrid_provider VALUES (367, 'sn_freiberg', 'Stadt Freiberg', 'http://www.freiberg.de', 62, 14);
INSERT INTO ingrid_provider VALUES (368, 'sn_lk_goerlitz', 'Landkreis Görlitz', 'http://www.kreis-goerlitz.de/', 67, 14);
INSERT INTO ingrid_provider VALUES (372, 'ni_nbue', 'Niedersächsische Bingostiftung für Umwelt und Entwicklungszusammenarbeit', 'http://www.umweltstiftung.niedersachsen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (373, 'ni_mf', 'Niedersächsisches Finanzministerium', 'http://www.mf.niedersachsen.de/', 5, 10);
INSERT INTO ingrid_provider VALUES (374, 'rp_geoportal', 'Geoportal Rheinland-Pfalz', 'http://www.geoportal.rlp.de/', 0, 12);
INSERT INTO ingrid_provider VALUES (492, 'bw_nvbw', 'Nahverkehrsgesellschaft Baden-Württemberg', 'http://www.nvbw.de/', 138, 2);
INSERT INTO ingrid_provider VALUES (376, 'bw_nsz', 'Naturschutzzentren Baden-Württemberg', 'http://www.naturschutzzentren-bw.de', 18, 2);
INSERT INTO ingrid_provider VALUES (378, 'bw_rpk', 'Regierungspräsidium Karlsruhe', 'http://www.rp-karlsruhe.de', 19, 2);
INSERT INTO ingrid_provider VALUES (379, 'bw_rpt', 'Regierungspräsidium Tübingen', 'http://www.rp-tuebingen.de ', 20, 2);
INSERT INTO ingrid_provider VALUES (513, 'hb_mbhv', 'Magistrat Bremerhaven', 'http://www.bremerhaven.de/stadt-und-politik/politik/magistrat/', 0, 6);
INSERT INTO ingrid_provider VALUES (381, 'bw_mj', 'Justizministerium Baden-Württemberg', 'http://www.jum.baden-wuerttemberg.de', 22, 2);
INSERT INTO ingrid_provider VALUES (382, 'bw_lga', 'Landesgesundheitsamt (LGA) im Regierungspräsidium Stuttgart', 'http://www.rp.baden-wuerttemberg.de', 23, 2);
INSERT INTO ingrid_provider VALUES (383, 'bw_mas', 'Ministerium für Arbeit und Sozialordnung, Familie, Frauen und Senioren Baden-Württemberg', 'http://www.sm.baden-wuerttemberg.de/', 24, 2);
INSERT INTO ingrid_provider VALUES (384, 'bw_unifreifor', 'Fakultät für Forst- und Umweltwissenschaften der UNI-Freiburg', 'http://www.ffu.uni-freiburg.de', 25, 2);
INSERT INTO ingrid_provider VALUES (385, 'bw_unifreilan', 'Institut für Landespflege der Universität Freiburg', 'http://www.landespflege-freiburg.de/', 26, 2);
INSERT INTO ingrid_provider VALUES (386, 'bw_unikarlmet', 'Institut für Meteorologie und Klimaforschung am Karlsruher Institut für Technologie (KIT)', 'http://www.imk.kit.edu/', 27, 2);
INSERT INTO ingrid_provider VALUES (388, 'bw_stadtwald', 'Stadt Waldshut-Tiengen', 'http://www.waldshut-tiengen.de/', 113, 2);
INSERT INTO ingrid_provider VALUES (495, 'bu_afee', 'Agentur für Erneuerbare Energien e. V.', 'http://www.unendlich-viel-energie.de', 0, 1);
INSERT INTO ingrid_provider VALUES (389, 'bw_unistutt', 'Universität Stuttgart', 'http://www.uni-stuttgart.de', 29, 2);
INSERT INTO ingrid_provider VALUES (390, 'bw_laal', 'Landratsamt Alb-Donau-Kreis', 'http://www.alb-donau-kreis.de', 30, 2);
INSERT INTO ingrid_provider VALUES (391, 'bw_labi', 'Landratsamt Biberach', 'http://www.biberach.de', 31, 2);
INSERT INTO ingrid_provider VALUES (392, 'bw_labo', 'Landratsamt Bodenseekreis', 'http://www.bodenseekreis.de', 32, 2);
INSERT INTO ingrid_provider VALUES (393, 'bw_laboe', 'Landratsamt Böblingen', 'http://www.landkreis-boeblingen.de', 33, 2);
INSERT INTO ingrid_provider VALUES (394, 'bw_labr', 'Landratsamt Breisgau-Hochschwarzwald', 'http://www.breisgau-hochschwarzwald.de', 34, 2);
INSERT INTO ingrid_provider VALUES (395, 'bw_laca', 'Landratsamt Calw', 'http://www.kreis-calw.de', 35, 2);
INSERT INTO ingrid_provider VALUES (396, 'bw_laem', 'Landratsamt Emmendingen', 'http://www.landkreis-emmendingen.de', 36, 2);
INSERT INTO ingrid_provider VALUES (397, 'bw_laen', 'Landratsamt Enzkreis', 'http://www.enzkreis.de', 37, 2);
INSERT INTO ingrid_provider VALUES (398, 'bw_laes', 'Landratsamt Esslingen', 'http://www.landkreis-esslingen.de', 38, 2);
INSERT INTO ingrid_provider VALUES (399, 'bw_lafr', 'Landratsamt Freudenstadt', 'http://www.landkreis-freudenstadt.de', 39, 2);
INSERT INTO ingrid_provider VALUES (400, 'bw_lago', 'Landratsamt Göppingen', 'http://www.landkreis-goeppingen.de', 40, 2);
INSERT INTO ingrid_provider VALUES (401, 'bw_laheid', 'Landratsamt Heidenheim', 'http://www.landkreis-heidenheim.de', 41, 2);
INSERT INTO ingrid_provider VALUES (402, 'bw_laheil', 'Landratsamt Heilbronn', 'http://www.landkreis-heilbronn.de', 42, 2);
INSERT INTO ingrid_provider VALUES (403, 'bw_laho', 'Landratsamt Hohenlohekreis', 'http://www.hohenlohekreis.de/', 43, 2);
INSERT INTO ingrid_provider VALUES (404, 'bw_laka', 'Landratsamt Karlsruhe', 'http://www.landkreis-karlsruhe.de', 44, 2);
INSERT INTO ingrid_provider VALUES (405, 'bw_lako', 'Landratsamt Konstanz', 'http://www.landkreis-konstanz.de/', 45, 2);
INSERT INTO ingrid_provider VALUES (406, 'bw_laloe', 'Landratsamt Lörrach', 'http://www.loerrach-landkreis.de', 46, 2);
INSERT INTO ingrid_provider VALUES (407, 'bw_lalu', 'Landratsamt Ludwigsburg', 'http://www.landkreis-ludwigsburg.de', 47, 2);
INSERT INTO ingrid_provider VALUES (408, 'bw_lama', 'Landratsamt Main-Tauber-Kreis', 'http://www.main-tauber-kreis.de', 48, 2);
INSERT INTO ingrid_provider VALUES (409, 'bw_lane', 'Landratsamt Neckar-Odenwald-Kreis', 'http://www.neckar-odenwald-kreis.de', 49, 2);
INSERT INTO ingrid_provider VALUES (410, 'bw_laor', 'Landratsamt Ortenaukreis', 'http://www.ortenaukreis.de', 50, 2);
INSERT INTO ingrid_provider VALUES (411, 'bw_laos', 'Landratsamt Ostalbkreis', 'http://www.ostalbkreis.de', 51, 2);
INSERT INTO ingrid_provider VALUES (412, 'bw_laras', 'Landratsamt Rastatt', 'http://www.landkreis-rastatt.de', 52, 2);
INSERT INTO ingrid_provider VALUES (413, 'bw_larav', 'Landratsamt Ravensburg', 'http://www.landkreis-ravensburg.de', 53, 2);
INSERT INTO ingrid_provider VALUES (414, 'bw_larem', 'Landratsamt Rems-Murr-Kreis', 'http://www.rems-murr-kreis.de', 54, 2);
INSERT INTO ingrid_provider VALUES (415, 'bw_lareu', 'Landratsamt Reutlingen', 'http://www.kreis-reutlingen.de', 55, 2);
INSERT INTO ingrid_provider VALUES (416, 'bw_larhe', 'Landratsamt Rhein-Neckar-Kreis', 'http://www.rhein-neckar-kreis.de', 56, 2);
INSERT INTO ingrid_provider VALUES (417, 'bw_laro', 'Landratsamt Rottweil', 'http://www.landkreis-rottweil.de', 57, 2);
INSERT INTO ingrid_provider VALUES (418, 'bw_laschwae', 'Landratsamt Schwäbisch Hall', 'http://www.landkreis-schwaebisch-hall.de', 58, 2);
INSERT INTO ingrid_provider VALUES (419, 'bw_laschwa', 'Landratsamt Schwarzwald-Baar-Kreis', 'http://www.schwarzwald-baar-kreis.de', 59, 2);
INSERT INTO ingrid_provider VALUES (420, 'bw_lasi', 'Landratsamt Sigmaringen', 'http://www.landkreis-sigmaringen.de', 60, 2);
INSERT INTO ingrid_provider VALUES (421, 'bw_latue', 'Landratsamt Tübingen', 'http://www.kreis-tuebingen.de', 61, 2);
INSERT INTO ingrid_provider VALUES (422, 'bw_latu', 'Landratsamt Tuttlingen', 'http://www.landkreis-tuttlingen.de', 62, 2);
INSERT INTO ingrid_provider VALUES (423, 'bw_lawa', 'Landratsamt Waldshut', 'http://www.landkreis-waldshut.de', 63, 2);
INSERT INTO ingrid_provider VALUES (424, 'bw_lazo', 'Landratsamt Zollernalbkreis', 'http://www.zollernalbkreis.de', 64, 2);
INSERT INTO ingrid_provider VALUES (425, 'bw_muell', 'Müllabfuhr-Zweckverband von Gemeinden des Landkreises Konstanz', 'http://www.mzv-hegau.de', 65, 2);
INSERT INTO ingrid_provider VALUES (426, 'bw_pro', 'Proregio Oberschwaben Gesellschaft zur Landschaftsentwicklung mbH', 'http://www.proregio-oberschwaben.de/', 66, 2);
INSERT INTO ingrid_provider VALUES (427, 'bw_zweckab', 'Zweckverband Abfallverwertung Reutlingen/Tübingen', 'http://www.zav-rt-tue.de', 67, 2);
INSERT INTO ingrid_provider VALUES (428, 'bw_azvb', 'Abwasserzweckverband Breisgauer Bucht', 'http://www.azv-breisgau.de/', 68, 2);
INSERT INTO ingrid_provider VALUES (429, 'bw_azvh', 'Abwasserzweckverband Heidelberg', 'http://www.azv-heidelberg.de', 69, 2);
INSERT INTO ingrid_provider VALUES (430, 'bw_azvo', 'Abwasserzweckverband Raum Offenburg', 'http://www.azv-offenburg.de/', 70, 2);
INSERT INTO ingrid_provider VALUES (432, 'bw_gruen', 'Geschäftsstelle Grüne Nachbarschaft', 'http://www.gruene-nachbarschaft.de/', 72, 2);
INSERT INTO ingrid_provider VALUES (433, 'bw_landess', 'Landeshauptstadt Stuttgart', 'http://www.stuttgart.de', 73, 2);
INSERT INTO ingrid_provider VALUES (434, 'bw_now', 'NOW Zweckverband Wasserversorgung Nordostwürttemberg', 'http://www.now-wasser.de/', 74, 2);
INSERT INTO ingrid_provider VALUES (435, 'bw_stadta', 'Stadt Aalen', 'http://www.sw-aalen.de/', 75, 2);
INSERT INTO ingrid_provider VALUES (436, 'bw_stadtbb', 'Stadt Baden-Baden', 'http://www.baden-baden.de', 76, 2);
INSERT INTO ingrid_provider VALUES (437, 'bw_stadtba', 'Stadt Balingen', 'http://www.balingen.de', 77, 2);
INSERT INTO ingrid_provider VALUES (438, 'bw_stadtbi', 'Stadt Biberach', 'http://www.biberach-riss.de//index.phtml?object=tx|1.300.1&org_obj=nav|383.10.1', 78, 2);
INSERT INTO ingrid_provider VALUES (439, 'bw_stadtboe', 'Stadt Böblingen', 'http://www.boeblingen.kdrs.de', 79, 2);
INSERT INTO ingrid_provider VALUES (441, 'bw_stadtem', 'Stadt Emmendingen', 'http://www.emmendingen.de', 81, 2);
INSERT INTO ingrid_provider VALUES (442, 'bw_stadtes', 'Stadt Esslingen am Neckar', 'http://www.esslingen.de', 82, 2);
INSERT INTO ingrid_provider VALUES (443, 'bw_stadtfrei', 'Stadt Freiburg', 'http://www.freiburg.de', 83, 2);
INSERT INTO ingrid_provider VALUES (444, 'bw_stadtfreu', 'Stadt Freudenstadt', 'http://www.freudenstadt.info/index.phtml?La=1&NavID=611.110', 84, 2);
INSERT INTO ingrid_provider VALUES (445, 'bw_stadtfrie', 'Stadt Friedrichshafen', 'http://www.friedrichshafen.de', 85, 2);
INSERT INTO ingrid_provider VALUES (446, 'bw_stadtgoe', 'Stadt Göppingen', 'http://www.goeppingen.de', 86, 2);
INSERT INTO ingrid_provider VALUES (447, 'bw_stadtheidel', 'Stadt Heidelberg', 'http://www.heidelberg.de', 87, 2);
INSERT INTO ingrid_provider VALUES (448, 'bw_stadtheiden', 'Stadt Heidenheim', 'http://www.heidenheim.de', 88, 2);
INSERT INTO ingrid_provider VALUES (449, 'bw_stadtheil', 'Stadt Heilbronn', 'http://www.heilbronn.de', 89, 2);
INSERT INTO ingrid_provider VALUES (450, 'bw_stadtka', 'Stadt Karlsruhe', 'http://www.karlsruhe.de', 90, 2);
INSERT INTO ingrid_provider VALUES (451, 'bw_stadtko', 'Stadt Konstanz', 'http://www.konstanz.de', 91, 2);
INSERT INTO ingrid_provider VALUES (550, 'bw_techno', 'Technologie- und Innovationszentrum Umwelttechnik und Ressourceneffizienz Baden-Württemberg GmbH', 'http://www.umwelttechnik-bw.de/', 0, 2);
INSERT INTO ingrid_provider VALUES (453, 'bw_stadtloe', 'Stadt Lörrach', 'http://www.loerrach.de', 93, 2);
INSERT INTO ingrid_provider VALUES (454, 'bw_stadtlu', 'Stadt Ludwigsburg', 'http://www.ludwigsburg.de', 94, 2);
INSERT INTO ingrid_provider VALUES (455, 'bw_stadtma', 'Stadt Mannheim', 'http://www.mannheim.de', 95, 2);
INSERT INTO ingrid_provider VALUES (456, 'bw_stadtmo', 'Stadt Mosbach', 'http://www.mosbach.de', 96, 2);
INSERT INTO ingrid_provider VALUES (457, 'bw_stadtof', 'Stadt Offenburg', 'http://www.offenburg.de', 97, 2);
INSERT INTO ingrid_provider VALUES (458, 'bw_stadtpf', 'Stadt Pforzheim', 'http://www.pforzheim.de', 98, 2);
INSERT INTO ingrid_provider VALUES (459, 'bw_stadtras', 'Stadt Rastatt', 'http://www.rastatt.de', 99, 2);
INSERT INTO ingrid_provider VALUES (460, 'bw_stadtrav', 'Stadt Ravensburg', 'http://www.ravensburg.de', 100, 2);
INSERT INTO ingrid_provider VALUES (461, 'bw_stadtreu', 'Stadt Reutlingen', 'http://www.reutlingen.de', 101, 2);
INSERT INTO ingrid_provider VALUES (462, 'bw_stadtrot', 'Stadt Rottweil', 'http://www.rottweil.de', 102, 2);
INSERT INTO ingrid_provider VALUES (463, 'bw_stadtsg', 'Stadt Schwäbisch Gmünd', 'http://www.schwaebisch-gmuend.de', 103, 2);
INSERT INTO ingrid_provider VALUES (464, 'bw_stadtsh', 'Stadt Schwäbisch Hall', 'http://www.schwaebischhall.de', 104, 2);
INSERT INTO ingrid_provider VALUES (465, 'bw_stadtsig', 'Stadt Sigmaringen', 'http://www.sigmaringen.de/', 105, 2);
INSERT INTO ingrid_provider VALUES (466, 'bw_stadtsin', 'Stadt Sindelfingen', 'http://www.sindelfingen.de', 106, 2);
INSERT INTO ingrid_provider VALUES (467, 'bw_stadttau', 'Stadt Tauberbischofsheim', 'http://www.tauberbischofsheim.de', 107, 2);
INSERT INTO ingrid_provider VALUES (468, 'bw_stadttue', 'Stadt Tübingen', 'http://www.tuebingen.de', 108, 2);
INSERT INTO ingrid_provider VALUES (469, 'bw_stadttut', 'Stadt Tuttlingen', 'http://www.tuttlingen.de', 109, 2);
INSERT INTO ingrid_provider VALUES (470, 'bw_stadtulm', 'Stadt Ulm', 'http://www.ulm.de', 110, 2);
INSERT INTO ingrid_provider VALUES (471, 'bw_stadtvill', 'Stadt Villingen-Schwenningen', 'http://www.svs-energie.de', 111, 2);
INSERT INTO ingrid_provider VALUES (472, 'bw_stadtwaib', 'Stadt Waiblingen', 'http://www.waiblingen.de', 112, 2);
INSERT INTO ingrid_provider VALUES (473, 'bw_geopark', 'Verein Geopark Schwäbische Alb e. V.', 'http://www.geopark-alb.de', 114, 2);
INSERT INTO ingrid_provider VALUES (474, 'bw_zweckba', 'Zweckverband Abwasserreinigung Balingen', 'http://www.klaeranlage-balingen.de', 115, 2);
INSERT INTO ingrid_provider VALUES (475, 'bw_zweckbo', 'Zweckverband Bodensee-Wasserversorgung (BWV)', 'http://www.zvbwv.de/', 116, 2);
INSERT INTO ingrid_provider VALUES (476, 'bw_zweckla', 'Zweckverband Landeswasserversorgung', 'http://www.lw-online.de/', 117, 2);
INSERT INTO ingrid_provider VALUES (552, 'ni_gd_ostercap', 'Ostercappeln, Gemeinde', 'http://www.ostercappeln.de/', 0, 10);
INSERT INTO ingrid_provider VALUES (553, 'ni_lk_dieph', 'Diepholz, Landkreis', 'http://www.diepholz.de/', 0, 10);
INSERT INTO ingrid_provider VALUES (479, 'bw_igkb', 'IGKB Internationale Gewässerschutzkommission Bodensee', 'http://www.igkb.org/', 120, 2);
INSERT INTO ingrid_provider VALUES (551, 'ni_buwsv', 'Wasser- und Schifffahrtsverwaltung des Bundes', '', 0, 10);
INSERT INTO ingrid_provider VALUES (481, 'bw_see', 'Internationale Bodenseekonferenz', 'http://www.bodenseekonferenz.org', 122, 2);
INSERT INTO ingrid_provider VALUES (482, 'bw_kea', 'KEA Klimaschutz- und Energieagentur Baden-Württemberg GmbH', 'http://www.keabw.de/', 123, 2);
INSERT INTO ingrid_provider VALUES (485, 'bw_museum', 'Staatliches Museum für Naturkunde in Stuttgart', 'http://www.wildbienen-kataster.de/', 126, 2);
INSERT INTO ingrid_provider VALUES (486, 'bw_rpf', 'Regierungspräsidium Freiburg', 'http://www.rp.baden-wuerttemberg.de/servlet/PB/menu/1007481/index.html', 137, 2);
INSERT INTO ingrid_provider VALUES (493, 'bu_bzga', 'Bundeszentrale für gesundheitliche Aufklärung', 'http://www.bzga.de', 44, 1);
INSERT INTO ingrid_provider VALUES (487, 'sn_geosn', 'Staatsbetrieb Geobasisinformation und Vermessung Sachsen (GeoSN)', 'http://www.landesvermessung.sachsen.de', 70, 14);
INSERT INTO ingrid_provider VALUES (498, 'bu_iksr', 'Internationale Kommission für den Schutz des Rheins', 'http://www.iksr.org/', 0, 1);
INSERT INTO ingrid_provider VALUES (499, 'bu_icpdr', 'Internationale Kommission für den Schutz der Donau', 'http://www.icpdr.org/', 0, 1);
INSERT INTO ingrid_provider VALUES (500, 'bu_moko', 'Moselkommission', 'http://www.moselkommission.org/', 0, 1);
INSERT INTO ingrid_provider VALUES (501, 'bu_ikso', 'Internationale Kommission für den Schutz der Oder gegen Verunreinigung', 'http://www.mkoo.pl/index.php?lang=DE', 0, 1);
INSERT INTO ingrid_provider VALUES (502, 'sh_lbknm', 'Landesbetrieb für Küstenschutz, Nationalpark und Meeresschutz Schleswig-Holstein', 'http://www.schleswig-holstein.de/LKN/DE/LKN_node.html', 0, 16);
INSERT INTO ingrid_provider VALUES (503, 'sh_lva', 'Landesvermessungsamt Schleswig-Holstein ', 'http://www.schleswig-holstein.de/LVERMA/DE/LVERMA_node.html', 0, 16);
INSERT INTO ingrid_provider VALUES (504, 'bu_jki', 'Julius Kühn-Institut, Bundesforschungsinstitut für Kulturpflanzen (JKI)', 'http://oekologischerlandbau.jki.bund.de/index.php?menuid=1', 0, 1);
INSERT INTO ingrid_provider VALUES (505, 'ni_geo', 'Geodatenportal Niedersachsen', 'http://www.geodaten.niedersachsen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (506, 'ni_liag', 'Leibniz-Institut für Angewandte Geophysik', 'http://www.liag-hannover.de', 0, 10);
INSERT INTO ingrid_provider VALUES (507, 'ni_nph', 'Nationalparkverwaltung Harz', 'http://www.nationalpark-harz.de/', 0, 10);
INSERT INTO ingrid_provider VALUES (508, 'ni_lskn', 'Landesbetrieb für Statistik und Kommunikationstechnologie Niedersachsen', 'http://www.lskn.niedersachsen.de/', 0, 10);
INSERT INTO ingrid_provider VALUES (509, 'ni_bre', 'Biosphärenreservat Elbtalaue', 'http://www.elbtalaue.niedersachsen.de', 0, 10);
INSERT INTO ingrid_provider VALUES (510, 'ni_npw', 'Nationalparkverwaltung Niedersächisches Wattenmeer', 'http://www.nationalpark-wattenmeer.de/nds', 0, 10);
INSERT INTO ingrid_provider VALUES (512, 'sl_lkvk', 'Landesamt für Kataster-, Vermessungs- und Kartenwesen des Saarlandes', 'http://www.saarland.de/kataster_vermessung_karten.htm', 0, 13);
INSERT INTO ingrid_provider VALUES (515, 'hh_bsb', 'Behörde für Schule und Berufsbildung Hamburg', ' http://www.hamburg.de/bsb/ ', 0, 7);
INSERT INTO ingrid_provider VALUES (516, 'hh_hu', 'Institut für Hygiene und Umwelt', 'http://www.hamburg.de/hu/ ', 0, 7);
INSERT INTO ingrid_provider VALUES (519, 'sn_landratsamt-pirna.de ', 'Landkreis Sächsische Schweiz-Osterzgebirge', 'http://www.saechsische-schweiz-osterzgebirge.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (520, 'sn_hoyerswerda.de', 'Stadt Hoyerswerda', 'http://www.hoyerswerda.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (521, 'sn_werdau.de', 'Stadt Werdau', 'http://www.werdau.de/werdau/willkommen.asp', 0, 14);
INSERT INTO ingrid_provider VALUES (522, 'sn_riesa.de', 'Stadt Riesa', 'http://www.riesa.de/deu/', 0, 14);
INSERT INTO ingrid_provider VALUES (523, 'sn_crimmitschau.de', 'Stadt Crimmitschau', 'http://www.crimmitschau.de', 0, 14);
INSERT INTO ingrid_provider VALUES (524, 'sn_doebeln.de', 'Stadt Döbeln', 'http://www.doebeln.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (525, 'sn_zittau.de', 'Stadt Zittau', 'http://www.zittau.eu', 0, 14);
INSERT INTO ingrid_provider VALUES (526, 'sn_auerbach', 'Stadt Auerbach', 'http://www.stadt-auerbach.de', 0, 14);
INSERT INTO ingrid_provider VALUES (527, 'sn_radebeul.de', 'Stadt Radebeul', 'http://www.radebeul.de', 0, 14);
INSERT INTO ingrid_provider VALUES (528, 'sn_plauen.de', 'Stadt Plauen', 'http://www.plauen.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (529, 'sn_stadt-meissen.de', 'Stadt Meißen', 'http://www.stadt-meissen.de', 0, 14);
INSERT INTO ingrid_provider VALUES (530, 'sn_bautzen', 'Stadt Bautzen', 'http://www.bautzen.de', 0, 14);
INSERT INTO ingrid_provider VALUES (531, 'sn_pirna.de', 'Stadt Pirna', 'http://www.pirna.de', 0, 14);
INSERT INTO ingrid_provider VALUES (532, 'sn_zwickau.de', 'Stadt Zwickau', 'http://www.zwickau.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (533, 'sn_delitzsch.de', 'Stadt Delitzsch', 'http://www.delitzsch.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (534, 'sn_annaberg-buchholz', 'Stadt Annaberg-Buchholz', 'http://www.annaberg-buchholz.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (535, 'sn_reichenbach-vogtland.de', 'Stadt Reichenbach i. Vogtland', 'http://www.reichenbach-vogtland.de', 0, 14);
INSERT INTO ingrid_provider VALUES (536, 'sn_erzgebirgskreis.de', 'Erzgebirgskreis', 'http://www.erzgebirgskreis.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (538, 'he_lbla', 'Landesbetrieb Landwirtschaft Hessen', 'http://www.llh.hessen.de/', 0, 8);
INSERT INTO ingrid_provider VALUES (540, 'ni_uol', 'Carl von Ossietzky Universität Oldenburg', 'http://www.uni-oldenburg.de', 0, 10);
INSERT INTO ingrid_provider VALUES (545, 'mv_flusselbe', 'Biosphärenreservat Flusslandschaft Elbe-MV ', 'http://www.elbetal-mv.de/', 0, 9);
INSERT INTO ingrid_provider VALUES (546, 'mv_soruegen', 'Biosphärenreservat Südost-Rügen', 'http://www.biosphaerenreservat-suedostruegen.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (547, 'ni_euafv', 'Amt für Veröffentlichungen der Europäischen Union', 'http://publications.europa.eu/index_de.htm', 0, 10);
INSERT INTO ingrid_provider VALUES (548, 'he_uni_kassel', 'Universität Kassel', 'http://www.uni-kassel.de/uni/', 0, 8);
INSERT INTO ingrid_provider VALUES (549, 'sn_landesdirektion.de', 'Landesdirektion Sachsen', 'http://www.lds.sachsen.de/', 0, 14);
INSERT INTO ingrid_provider VALUES (555, 'be_sejv', 'Senatsverwaltung für Justiz und Verbraucherschutz', 'www.berlin.de/sen/justiz/', 5, 4);
INSERT INTO ingrid_provider VALUES (556, 'be_segessoz', 'Senatsverwaltung für Gesundheit und Soziales ', 'www.berlin.de/sen/gessoz', 6, 1);
INSERT INTO ingrid_provider VALUES (557, 'bu_wsv', 'Wasser- und Schifffahrtsverwaltung des Bundes', 'http://www.wsv.de/', 0, 1);
INSERT INTO ingrid_provider VALUES (558, 'bw_kit', 'Karlsruher Institut für Technologie', 'http://www.kit.edu/', 0, 2);
INSERT INTO ingrid_provider VALUES (559, 'bu_vti', 'Johann Heinrich von Thünen-Institut (vTI)', 'http://www.ti.bund.de/', 0, 1);


--
-- Name: ingrid_provider_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_provider_id_seq', 1, false);


--
-- Data for Name: ingrid_rss_source; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_rss_source VALUES (1, 'bu_uba', 'Umweltbundesamt', 'http://www.umweltbundesamt.de/rss/presse', 'de', 'all', NULL, 3, '2015-01-21 11:11:33');


--
-- Name: ingrid_rss_source_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_rss_source_id_seq', 1, false);


--
-- Data for Name: ingrid_rss_store; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_rss_store VALUES ('http://www.umweltbundesamt.de/presse/presseinformationen/kaffeemaschinen-it-geraete-sparsamer-im', 'Umweltbundesamt', NULL, NULL, 'Ab Beginn des neuen Jahres gelten in der Europ&auml;ischen Union niedrigere Verbrauchswerte f&uuml;r eine Reihe von Elektroger&auml;ten des allt&auml;glichen Bedarfs, wenn sie neu auf den Markt gebracht werden. Strengere technische Anforderungen m&uuml;ssen zum Beispiel Kaffeemaschinen und IT-Ger&auml;te wie Modems und Router erf&uuml;llen. F&uuml;r elektrische Back&ouml;fen gilt zudem, dass die Informationen &uuml;ber ihren Energieverbrauch transparenter gestaltet sein m&uuml;ssen.', 'de', '2014-12-23 12:30:00', 'Kaffeemaschinen und IT-Ger&auml;te sparsamer im Stromverbrauch');
INSERT INTO ingrid_rss_store VALUES ('http://www.umweltbundesamt.de/presse/presseinformationen/gold-fuer-uba-neubau', 'Umweltbundesamt', NULL, NULL, 'Noch klingt der Name nach Zukunft: Das &#8222;Haus 2019&#8220; &#8211; doch schon heute erf&uuml;llt das B&uuml;rogeb&auml;ude des Umweltbundesamtes (UBA) die Anforderungen der europ&auml;ischen Geb&auml;uderichtlinie f&uuml;r das Jahr 2019. Auf der Bau 2015 in M&uuml;nchen &uuml;berreichte daher der Parlamentarische Staatssekret&auml;r im Bundesbauministerium, Florian Pronold, heute die Zertifizierungsurkunde f&uuml;r das neue B&uuml;rogeb&auml;ude des UBA in Berlin-Marienfelde. &#8222;Das &#8218;Haus 2019&#8216; ist das erste Bundesgeb&auml;ude, das mit den anspruchsvollen Vorgaben des &#8218;Bewertungssystems Nachhaltiges Bauen f&uuml;r Bundesgeb&auml;ude&#8216; (BNB) von Beginn an geplant und bewertet wurde&#8220;, so Florian Pronold bei der &Uuml;bergabe der Urkunde. &#8222;Darauf k&ouml;nnen wir zu Recht stolz sein, und ich danke allen Projektbeteiligten f&uuml;r Ihr besonderes Engagement.&#8220; Der gesamte Rohbau, einschlie&szlig;lich der Fassade, ist aus dem nachwachsenden Rohstoff Holz gefertigt. Zudem versorgt sich das Geb&auml;ude komplett selbst mit Energie. &#8222;Mit dem &#8218;Haus 2019&#8216; setzen wir nicht nur ein Zeichen f&uuml;r vorbildliches, nachhaltiges Bauen, sondern zeigen auch beispielhaft, wie Null-Energie-Geb&auml;ude k&uuml;nftig geplant und gebaut werden k&ouml;nnen&#8220;, betonte Maria Krautzberger, Pr&auml;sidentin des Umweltbundesamtes.', 'de', '2015-01-19 19:00:00', '&#8222;Gold&#8220; f&uuml;r UBA-Neubau');
INSERT INTO ingrid_rss_store VALUES ('http://www.umweltbundesamt.de/presse/presseinformationen/stickstoffueberschuss-ein-umweltproblem-neuem', 'Umweltbundesamt', NULL, NULL, 'In der EU sind fast zwei Drittel aller nat&uuml;rlichen Lebensr&auml;ume &uuml;berd&uuml;ngt. Verantwortlich f&uuml;r den &Uuml;berschuss an N&auml;hrstoffen ist vor allem der Stickstoff aus der Landwirtschaft, der als G&uuml;lle oder Minerald&uuml;nger auf die Felder kommt. Die EU-Kommission hat wiederholt angemahnt, die Stickstoffeintr&auml;ge zu minimieren. Maria Krautzberger, Pr&auml;sidentin des Umweltbundesamtes (UBA): &#8222;Es ist wichtig, dass die EU weiter Impulse f&uuml;r eine Reduzierung der Stickstoff&uuml;bersch&uuml;sse setzt. Gleichzeitig m&uuml;ssen wir auf nationaler Ebene handeln. Dabei ist die D&uuml;ngeverordnung ein wichtiger Ansatz, um Luft, Boden und Grundwasser besser vor zu viel Stickstoff zu sch&uuml;tzen.&#8220;', 'de', '2015-01-08 15:00:00', 'Stickstoff&uuml;berschuss &#8211; ein Umweltproblem mit neuem Ausma&szlig;');


--
-- Data for Name: ingrid_service_rubric; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ingrid_service_rubric VALUES (1, 'pre', 'press', 1);
INSERT INTO ingrid_service_rubric VALUES (2, 'pub', 'publication', 2);
INSERT INTO ingrid_service_rubric VALUES (3, 'ver', 'event', 3);


--
-- Name: ingrid_service_rubric_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_service_rubric_id_seq', 1, false);


--
-- Data for Name: ingrid_tiny_url; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: ingrid_tiny_url_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ingrid_tiny_url_id_seq', 1, false);


--
-- Data for Name: jetspeed_service; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO jetspeed_service VALUES (1, 1, 'IdGenerator');
INSERT INTO jetspeed_service VALUES (2, 1, 'PortletRegistryComponent');
INSERT INTO jetspeed_service VALUES (3, 1, 'PageManager');
INSERT INTO jetspeed_service VALUES (4, 1, 'TemplateLocator');
INSERT INTO jetspeed_service VALUES (5, 1, 'DecorationLocator');
INSERT INTO jetspeed_service VALUES (6, 1, 'Powertools');
INSERT INTO jetspeed_service VALUES (7, 1, 'DecorationFactory');
INSERT INTO jetspeed_service VALUES (8, 1, 'EntityAccessor');
INSERT INTO jetspeed_service VALUES (9, 1, 'WindowAccessor');
INSERT INTO jetspeed_service VALUES (10, 1, 'Desktop');
INSERT INTO jetspeed_service VALUES (11, 1, 'decorationContentCache');
INSERT INTO jetspeed_service VALUES (12, 1, 'portletContentCache');
INSERT INTO jetspeed_service VALUES (13, 1, 'PortalConfiguration');
INSERT INTO jetspeed_service VALUES (22, 3, 'UserManager');
INSERT INTO jetspeed_service VALUES (23, 3, 'RoleManager');
INSERT INTO jetspeed_service VALUES (24, 3, 'GroupManager');
INSERT INTO jetspeed_service VALUES (25, 3, 'Profiler');
INSERT INTO jetspeed_service VALUES (26, 3, 'PortletRegistryComponent');
INSERT INTO jetspeed_service VALUES (27, 3, 'PageManager');
INSERT INTO jetspeed_service VALUES (28, 3, 'PortalAdministration');
INSERT INTO jetspeed_service VALUES (29, 3, 'PermissionManager');
INSERT INTO jetspeed_service VALUES (41, 21, 'UserManager');
INSERT INTO jetspeed_service VALUES (42, 21, 'RoleManager');
INSERT INTO jetspeed_service VALUES (43, 21, 'GroupManager');
INSERT INTO jetspeed_service VALUES (44, 21, 'Profiler');
INSERT INTO jetspeed_service VALUES (45, 21, 'PortletRegistryComponent');
INSERT INTO jetspeed_service VALUES (46, 21, 'PageManager');
INSERT INTO jetspeed_service VALUES (47, 21, 'PortalAdministration');
INSERT INTO jetspeed_service VALUES (48, 21, 'PermissionManager');


--
-- Data for Name: language; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO language VALUES (1, 1, 'en,,', 0, 'IngridClearLayout', 'IngridClearLayout', NULL);
INSERT INTO language VALUES (2, 2, 'en,,', 0, 'IngridOneColumn', 'OneColumn', NULL);
INSERT INTO language VALUES (3, 3, 'en,,', 0, 'IngridTwoColumns', 'IngridTwoColumns', NULL);
INSERT INTO language VALUES (4, 4, 'en,,', 0, 'SimpleLayoutPortlet', 'SimpleLayout', NULL);
INSERT INTO language VALUES (5, 5, 'en,,', 0, 'One Column', 'OneColumn', NULL);
INSERT INTO language VALUES (6, 6, 'en,,', 0, 'One Column with Tables', 'OneColumnTable', NULL);
INSERT INTO language VALUES (7, 7, 'en,,', 0, 'Two Columns', 'TwoColumns', NULL);
INSERT INTO language VALUES (8, 8, 'en,,', 0, 'Two Columns (15%/85%)', 'Two Columns (15%/85%)', NULL);
INSERT INTO language VALUES (9, 9, 'en,,', 0, 'Three Columns', 'ThreeColumns', NULL);
INSERT INTO language VALUES (10, 10, 'en,,', 0, 'Three Columns with Tables', 'ThreeColumnsTable', NULL);
INSERT INTO language VALUES (11, 11, 'en,,', 0, 'One Column - No Actions', 'OneColumnNoActions', NULL);
INSERT INTO language VALUES (12, 12, 'en,,', 0, 'Two Columns - No Actions', 'TwoColumnsNoActions', NULL);
INSERT INTO language VALUES (13, 13, 'en,,', 0, 'Three Columns - No Actions', 'ThreeColumnsNoActions', NULL);
INSERT INTO language VALUES (14, 14, 'en,,', 0, 'Two Columns (25%/75%) No Actions', 'VelocityTwoColumns2575NoActions', NULL);
INSERT INTO language VALUES (15, 15, 'en,,', 0, 'Two Columns (25%/75%)', 'VelocityTwoColumns2575', NULL);
INSERT INTO language VALUES (16, 16, 'en,,', 0, 'Two Columns (15%,85%) No Actions', '2 Columns 15,85 No Actions', NULL);
INSERT INTO language VALUES (17, 17, 'en,,', 0, 'Two Columns with Tables', 'Two Columns Tables', NULL);
INSERT INTO language VALUES (18, 18, 'en,,', 0, 'Four Columns', 'FourColumns', NULL);
INSERT INTO language VALUES (121, 101, 'en,,', 0, 'mdek', 'mdek', 'mdek');
INSERT INTO language VALUES (122, 102, 'en,,', 0, 'mdek', 'mdek', 'mdek');
INSERT INTO language VALUES (123, 103, 'en,,', 0, 'mdek', 'mdek', 'mdek');
INSERT INTO language VALUES (141, 121, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (142, 122, 'en,,', 0, 'javascript detection portlet', 'javascript detection portlet', NULL);
INSERT INTO language VALUES (143, 123, 'en,,', 0, 'Help Portlet', 'Help Portlet', 'Help Portlet');
INSERT INTO language VALUES (144, 124, 'en,,', 0, 'Jobübersicht', 'Jobübersicht', NULL);
INSERT INTO language VALUES (145, 125, 'en,,', 0, 'RSS Feeds administrieren', 'RSS Feeds administrieren', NULL);
INSERT INTO language VALUES (146, 126, 'en,,', 0, 'Partner administrieren', 'Partner administrieren', NULL);
INSERT INTO language VALUES (147, 127, 'en,,', 0, 'Anbieter administrieren', 'Anbieter administrieren', NULL);
INSERT INTO language VALUES (148, 128, 'en,,', 0, 'Benutzer administrieren', 'Benutzer administrieren', NULL);
INSERT INTO language VALUES (149, 129, 'en,,', 0, 'iPlugs/iBus administrieren', 'iPlugs/iBus administrieren', NULL);
INSERT INTO language VALUES (150, 130, 'en,,', 0, 'Inhalte administrieren', 'Inhalte administrieren', NULL);
INSERT INTO language VALUES (151, 131, 'en,,', 0, 'WMS Administration', 'WMS Administration', NULL);
INSERT INTO language VALUES (152, 132, 'en,,', 0, 'Statistiken', 'Statistiken', NULL);
INSERT INTO language VALUES (153, 133, 'en,,', 0, 'Startseite administrieren', 'Startseite administrieren', NULL);
INSERT INTO language VALUES (154, 134, 'en,,', 0, 'Portal Profile', 'Portal Profile', NULL);
INSERT INTO language VALUES (155, 135, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (156, 136, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (157, 137, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (158, 138, 'en,,', 0, 'Portal User Migration', 'Portal User Migration', NULL);
INSERT INTO language VALUES (159, 139, 'en,,', 0, 'Search', 'Search', 'Search');
INSERT INTO language VALUES (160, 140, 'en,,', 0, 'Environmental Topics', 'Environmental Topics', 'Environmental Topics');
INSERT INTO language VALUES (161, 141, 'en,,', 0, 'Other News', 'RssNews', 'RssNews');
INSERT INTO language VALUES (162, 142, 'en,,', 1, 'Other News', 'RssNews', 'RssNews');
INSERT INTO language VALUES (163, 142, 'de,,', 1, 'Weitere Meldungen', 'RssNews', 'RssNews');
INSERT INTO language VALUES (164, 143, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (165, 144, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (166, 145, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information');
INSERT INTO language VALUES (167, 146, 'en,,', 0, 'Service', 'Service', 'Service');
INSERT INTO language VALUES (168, 147, 'en,,', 0, 'Environmental Monitoring Data', 'Environmental Monitoring Data', 'Environmental Monitoring Data');
INSERT INTO language VALUES (169, 148, 'en,,', 0, 'Environmental Almanac', 'Environmental Almanac', 'Environmental Almanac');
INSERT INTO language VALUES (170, 149, 'en,,', 0, 'Weather warning', 'Weather warning', 'Weather warning');
INSERT INTO language VALUES (171, 150, 'en,,', 1, 'Contact', 'Contact', 'Contact');
INSERT INTO language VALUES (172, 150, 'de,,', 1, 'Kontakt', 'Kontakt', 'Kontakt');
INSERT INTO language VALUES (173, 151, 'en,,', 1, 'Newsletter', 'Newsletter', 'Newsletter');
INSERT INTO language VALUES (174, 151, 'de,,', 1, 'Newsletter', 'Newsletter', 'Newsletter');
INSERT INTO language VALUES (175, 152, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (176, 152, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (177, 153, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (178, 153, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (179, 154, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (180, 154, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (181, 155, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (182, 155, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (183, 156, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (184, 156, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (185, 157, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (186, 157, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (187, 158, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (188, 158, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (189, 159, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (190, 159, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (191, 160, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (192, 160, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (193, 161, 'en,,', 1, 'Login', 'Login', 'Login');
INSERT INTO language VALUES (194, 161, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung');
INSERT INTO language VALUES (195, 162, 'en,,', 1, 'Information Providers', 'Information Providers', 'Information Providers');
INSERT INTO language VALUES (196, 162, 'de,,', 1, 'Informationsanbieter', 'Informationsanbieter', 'Informationsanbieter');
INSERT INTO language VALUES (197, 163, 'en,,', 1, 'Data sources', 'Data sources', 'Data sources');
INSERT INTO language VALUES (198, 163, 'de,,', 1, 'Datenquellen', 'Datenquellen', 'Datenquellen');
INSERT INTO language VALUES (199, 164, 'en,,', 1, 'Service', 'Service', 'Service');
INSERT INTO language VALUES (200, 164, 'de,,', 1, 'Service', 'Service', 'Service');
INSERT INTO language VALUES (201, 165, 'en,,', 0, 'Service', 'Service', 'Service');
INSERT INTO language VALUES (202, 166, 'en,,', 1, 'Environmental Monitoring Data', 'Environmental Monitoring Data', 'Environmental Monitoring Data');
INSERT INTO language VALUES (203, 166, 'de,,', 1, 'Messwerte', 'Messwerte', 'Messwerte');
INSERT INTO language VALUES (204, 167, 'en,,', 0, 'Environmental Monitoring Data', 'Environmental Monitoring Data', 'Environmental Monitoring Data');
INSERT INTO language VALUES (205, 168, 'en,,', 1, 'Environmental Topics', 'Environmental Topics', 'Environmental Topics');
INSERT INTO language VALUES (206, 168, 'de,,', 1, 'Themen', 'Themen', 'Themen');
INSERT INTO language VALUES (207, 169, 'en,,', 0, 'Environmental Topics', 'Environmental Topics', 'Environmental Topics');
INSERT INTO language VALUES (208, 170, 'en,,', 0, 'Kartenabschnitt als feste URLs', 'SaveMapsPortlet', NULL);
INSERT INTO language VALUES (209, 171, 'en,,', 0, 'Karten', 'ShowMapsPortlet', NULL);
INSERT INTO language VALUES (210, 172, 'en,,', 1, 'Environmental Almanac', 'Environmental Almanac', 'Environmental Almanac');
INSERT INTO language VALUES (211, 172, 'de,,', 1, 'Chronik', 'Chronik', 'Chronik');
INSERT INTO language VALUES (212, 173, 'en,,', 0, 'Environmental Almanac', 'Environmental Almanac', 'Environmental Almanac');
INSERT INTO language VALUES (213, 174, 'en,,', 0, 'Search', 'Search', 'Search');
INSERT INTO language VALUES (214, 175, 'en,,', 0, 'Search', 'Search', 'Search');
INSERT INTO language VALUES (215, 176, 'en,,', 0, 'Search', 'Search', 'Search');
INSERT INTO language VALUES (216, 177, 'en,,', 0, 'Search Result', 'Search Result', 'Search Result');
INSERT INTO language VALUES (217, 178, 'en,,', 0, 'Search Result', 'Search Result', 'Search Result');
INSERT INTO language VALUES (218, 179, 'en,,', 0, 'Search', 'Search', 'Search');
INSERT INTO language VALUES (219, 180, 'en,,', 0, 'Data Catalogues', 'Data Catalogues', 'Data Catalogues');
INSERT INTO language VALUES (220, 181, 'en,,', 0, 'Data Catalogues', 'Data Catalogues', 'Data Catalogues');
INSERT INTO language VALUES (221, 182, 'en,,', 0, 'Data Catalogues', 'Data Catalogues', 'Data Catalogues');
INSERT INTO language VALUES (222, 183, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (223, 184, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (224, 185, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (225, 186, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (226, 187, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (227, 188, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (228, 189, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (229, 190, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (230, 191, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (231, 192, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (232, 193, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (233, 194, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (234, 195, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (235, 196, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (236, 197, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (237, 198, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (238, 199, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (239, 200, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (240, 201, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced');
INSERT INTO language VALUES (241, 202, 'en,,', 1, 'Feature Overview', 'Feature', 'feature');
INSERT INTO language VALUES (242, 202, 'de,,', 1, 'Funktionen in der Übersicht', 'Feature', 'feature');


--
-- Data for Name: link; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO link VALUES (1, 1, '/language.link', 'language.link', NULL, 'ingrid.page.language', 'ingrid.page.language', 0, NULL, 'top', '/default-page.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO link VALUES (2, 1, '/webmaster.link', 'webmaster.link', NULL, 'ingrid.page.webmaster.tooltip', 'ingrid.page.webmaster', 0, NULL, 'top', 'mailto:info@informationgrid.eu', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


--
-- Data for Name: link_constraint; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: link_constraints_ref; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: link_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: locale_encoding_mapping; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: localized_description; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO localized_description VALUES (1, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The SimpleLayoutPortlet requires a ViewPage layout template set through the portlet preferences which provides its own layout algorithm', 'en,,');
INSERT INTO localized_description VALUES (2, 1, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'Jetspeed 2 Layout Portlets Applications', ',,');
INSERT INTO localized_description VALUES (177, 179, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'Entry point to the mdek application', 'en,,');
INSERT INTO localized_description VALUES (178, 180, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'Catalog administration portlet for the portal admin', 'en,,');
INSERT INTO localized_description VALUES (179, 181, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'SuperAdmin-Login to each Catalog with each User', 'en,,');
INSERT INTO localized_description VALUES (180, 3, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'InGrid-Portal Application', ',,');
INSERT INTO localized_description VALUES (181, 201, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (182, 121, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,');
INSERT INTO localized_description VALUES (183, 122, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet is called only via javascript', 'en,,');
INSERT INTO localized_description VALUES (184, 202, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (185, 123, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays help content.', 'en,,');
INSERT INTO localized_description VALUES (186, 203, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (187, 124, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the monitor component of the portlet.', 'en,,');
INSERT INTO localized_description VALUES (188, 204, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (189, 125, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the RSS feeds.', 'en,,');
INSERT INTO localized_description VALUES (190, 205, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (191, 126, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the Partners.', 'en,,');
INSERT INTO localized_description VALUES (192, 206, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (193, 127, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the Providers.', 'en,,');
INSERT INTO localized_description VALUES (194, 207, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (195, 208, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This is the template in which you setup an email to be sent after user exists', 'en,,');
INSERT INTO localized_description VALUES (196, 209, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the roles a new user will be registered with', 'en,,');
INSERT INTO localized_description VALUES (197, 210, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the groups a new user will be registered with', 'en,,');
INSERT INTO localized_description VALUES (198, 211, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule names a new user will be registered with', 'en,,');
INSERT INTO localized_description VALUES (199, 212, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule values a new user will be registered with', 'en,,');
INSERT INTO localized_description VALUES (200, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the user administration.', 'en,,');
INSERT INTO localized_description VALUES (201, 213, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (202, 129, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the iPlugs.', 'en,,');
INSERT INTO localized_description VALUES (203, 214, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (204, 130, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the free text content.', 'en,,');
INSERT INTO localized_description VALUES (205, 215, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (206, 131, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the wms admin interface links.', 'en,,');
INSERT INTO localized_description VALUES (207, 216, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (208, 132, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the links to several statistics.', 'en,,');
INSERT INTO localized_description VALUES (209, 217, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (210, 133, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the home page personalization dialog.', 'en,,');
INSERT INTO localized_description VALUES (211, 218, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (212, 134, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the portal profile admin interface.', 'en,,');
INSERT INTO localized_description VALUES (213, 219, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (214, 135, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,');
INSERT INTO localized_description VALUES (215, 220, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (216, 136, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,');
INSERT INTO localized_description VALUES (217, 221, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (218, 137, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,');
INSERT INTO localized_description VALUES (219, 222, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (220, 138, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the portal user migration interface.', 'en,,');
INSERT INTO localized_description VALUES (221, 223, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (222, 139, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a search box for the home AND main search page.', 'en,,');
INSERT INTO localized_description VALUES (223, 224, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (224, 140, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rubrics of the Environment catalogue page on the home page for quick search.', 'en,,');
INSERT INTO localized_description VALUES (225, 225, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (226, 141, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rss news list.', 'en,,');
INSERT INTO localized_description VALUES (227, 226, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (228, 142, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rss news list.', 'en,,');
INSERT INTO localized_description VALUES (229, 227, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (230, 143, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays a content entry from the cms, specified by the preference ''cmsKey''.', 'en,,');
INSERT INTO localized_description VALUES (231, 228, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (232, 144, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,');
INSERT INTO localized_description VALUES (233, 229, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (234, 145, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,');
INSERT INTO localized_description VALUES (235, 230, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (236, 146, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rubrics of the Service page on the home page for quick search.', 'en,,');
INSERT INTO localized_description VALUES (237, 231, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (238, 147, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rubrics of the Measures page on the home page for quick search.', 'en,,');
INSERT INTO localized_description VALUES (239, 232, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (240, 148, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays an anniversary event on the home page.', 'en,,');
INSERT INTO localized_description VALUES (241, 233, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (242, 149, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the weather.', 'en,,');
INSERT INTO localized_description VALUES (243, 234, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (244, 150, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes an environment search box.', 'en,,');
INSERT INTO localized_description VALUES (245, 235, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (246, 151, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'TODO: Describe the portlet.', 'en,,');
INSERT INTO localized_description VALUES (247, 236, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (248, 152, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes the login dialog.', 'en,,');
INSERT INTO localized_description VALUES (249, 237, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (250, 153, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal overview.', 'en,,');
INSERT INTO localized_description VALUES (251, 238, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (252, 239, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the url to which folks will return after they receive an email', 'en,,');
INSERT INTO localized_description VALUES (253, 240, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This is the template in which you setup an email to be sent after user exists', 'en,,');
INSERT INTO localized_description VALUES (254, 241, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule names a new user will be registered with', 'en,,');
INSERT INTO localized_description VALUES (255, 242, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule values a new user will be registered with', 'en,,');
INSERT INTO localized_description VALUES (256, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal create account dialog.', 'en,,');
INSERT INTO localized_description VALUES (257, 243, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (258, 155, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal edit account dialog.', 'en,,');
INSERT INTO localized_description VALUES (259, 244, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (260, 245, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This is the template in which you setup an email to be sent after user exists', 'en,,');
INSERT INTO localized_description VALUES (261, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal password forgotten dialog.', 'en,,');
INSERT INTO localized_description VALUES (262, 246, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (263, 157, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: OverView.', 'en,,');
INSERT INTO localized_description VALUES (264, 247, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (265, 158, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: home.', 'en,,');
INSERT INTO localized_description VALUES (266, 248, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (267, 159, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: Partner.', 'en,,');
INSERT INTO localized_description VALUES (268, 249, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (269, 160, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: Sources.', 'en,,');
INSERT INTO localized_description VALUES (270, 250, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (271, 161, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: SearchSettings.', 'en,,');
INSERT INTO localized_description VALUES (272, 251, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (273, 162, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'TODO: describe the portlet', 'en,,');
INSERT INTO localized_description VALUES (274, 252, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (275, 163, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'TODO: describe the portlet', 'en,,');
INSERT INTO localized_description VALUES (276, 253, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (277, 164, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a service search box.', 'en,,');
INSERT INTO localized_description VALUES (278, 254, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (279, 165, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the service search (service catalogue).', 'en,,');
INSERT INTO localized_description VALUES (280, 255, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (281, 166, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a service search box.', 'en,,');
INSERT INTO localized_description VALUES (282, 256, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (283, 167, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the measures search (measures catalogue).', 'en,,');
INSERT INTO localized_description VALUES (284, 257, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (285, 168, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays an environment search box.', 'en,,');
INSERT INTO localized_description VALUES (286, 258, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (287, 169, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the environment topics search (environment topics catalogue).', 'en,,');
INSERT INTO localized_description VALUES (288, 259, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (289, 170, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results.', 'en,,');
INSERT INTO localized_description VALUES (290, 260, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (291, 171, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results.', 'en,,');
INSERT INTO localized_description VALUES (292, 261, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (293, 172, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a environment search box.', 'en,,');
INSERT INTO localized_description VALUES (294, 262, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (295, 173, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the environment chronicle search.', 'en,,');
INSERT INTO localized_description VALUES (296, 263, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (297, 174, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays and handles the search settings.', 'en,,');
INSERT INTO localized_description VALUES (298, 264, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (299, 175, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays and handles the search save dialog.', 'en,,');
INSERT INTO localized_description VALUES (300, 265, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (301, 176, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays and handles the search history.', 'en,,');
INSERT INTO localized_description VALUES (302, 266, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (303, 177, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the "Similar Terms" fragment of the search result page.', 'en,,');
INSERT INTO localized_description VALUES (304, 267, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (305, 178, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results.', 'en,,');
INSERT INTO localized_description VALUES (306, 268, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (307, 179, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search detail for dsc results.', 'en,,');
INSERT INTO localized_description VALUES (308, 269, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (309, 180, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the browsing of the UDK in the catalog search.', 'en,,');
INSERT INTO localized_description VALUES (310, 270, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (311, 181, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the Search in the UDKs via Thesaurus browsing.', 'en,,');
INSERT INTO localized_description VALUES (312, 271, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (313, 182, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results found via Thesaurus Browsing.', 'en,,');
INSERT INTO localized_description VALUES (314, 272, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (315, 183, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for environment info.', 'en,,');
INSERT INTO localized_description VALUES (316, 273, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (317, 184, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the thesaurus terms in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (318, 274, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (319, 185, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the geothesaurus terms in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (320, 275, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (321, 186, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the map page in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (322, 276, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (323, 187, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the time constraint fragment in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (324, 277, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (325, 188, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the time references (chronicle) in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (326, 278, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (327, 189, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "search area"/"contents" fragment in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (328, 279, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (329, 190, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "searcharea"/"sources" fragment in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (330, 280, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (331, 191, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "envinfo"/"searcharea"/"partner" fragment in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (332, 281, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (333, 192, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for addresses.', 'en,,');
INSERT INTO localized_description VALUES (334, 282, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (335, 193, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the mode in the extended search for addresses.', 'en,,');
INSERT INTO localized_description VALUES (336, 283, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (337, 194, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the place reference in the extended search for addresses.', 'en,,');
INSERT INTO localized_description VALUES (338, 284, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (339, 195, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "addresses"/"searcharea"/"partner" fragment in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (340, 285, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (341, 196, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for research.', 'en,,');
INSERT INTO localized_description VALUES (342, 286, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (343, 197, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the attributes in the extended search for research.', 'en,,');
INSERT INTO localized_description VALUES (344, 287, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (345, 198, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for law.', 'en,,');
INSERT INTO localized_description VALUES (346, 288, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (347, 199, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the thesaurus terms in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (348, 289, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (349, 200, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "law"/"searcharea"/"partner" fragment in the extended search.', 'en,,');
INSERT INTO localized_description VALUES (350, 290, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (351, 201, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a combo box containing different languages.', 'en,,');
INSERT INTO localized_description VALUES (352, 291, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,');
INSERT INTO localized_description VALUES (353, 202, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet explains all the features the portal has to offer.', 'en,,');
INSERT INTO localized_description VALUES (354, 21, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'InGrid-Portal Application', ',,');


--
-- Data for Name: localized_display_name; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO localized_display_name VALUES (1, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Layout for Ingrid Portal Using Velocity, no layout decoration', 'en,,');
INSERT INTO localized_display_name VALUES (2, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column Layout for Ingrid Portal Using Velocity', 'en,,');
INSERT INTO localized_display_name VALUES (3, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns Layout for Ingrid Portal Using Velocity', 'en,,');
INSERT INTO localized_display_name VALUES (4, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Simple readonly fixed Layout', 'en,,');
INSERT INTO localized_display_name VALUES (5, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column', 'en,,');
INSERT INTO localized_display_name VALUES (6, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity????? 1 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (7, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,,');
INSERT INTO localized_display_name VALUES (8, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (9, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column with Tables', 'en,,');
INSERT INTO localized_display_name VALUES (10, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????? 1 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (11, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,,');
INSERT INTO localized_display_name VALUES (12, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (13, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns', 'en,,');
INSERT INTO localized_display_name VALUES (14, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ????? 2 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (15, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,,');
INSERT INTO localized_display_name VALUES (16, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (17, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (15%/85%)', 'en,,');
INSERT INTO localized_display_name VALUES (18, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????????????????????????? 2 ?', 'ja,,');
INSERT INTO localized_display_name VALUES (19, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????????', 'zh,,');
INSERT INTO localized_display_name VALUES (20, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (21, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Three Columns', 'en,,');
INSERT INTO localized_display_name VALUES (22, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ????? 3 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (23, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?3???', 'zh,,');
INSERT INTO localized_display_name VALUES (24, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?3???', 'zh,TW,');
INSERT INTO localized_display_name VALUES (25, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Three Columns with Tables', 'en,,');
INSERT INTO localized_display_name VALUES (26, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????? 3 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (27, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???3???', 'zh,,');
INSERT INTO localized_display_name VALUES (28, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???3???', 'zh,TW,');
INSERT INTO localized_display_name VALUES (29, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column - No Actions', 'en,,');
INSERT INTO localized_display_name VALUES (30, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 1 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (31, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,,');
INSERT INTO localized_display_name VALUES (32, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (33, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns Layout - No Actions', 'en,,');
INSERT INTO localized_display_name VALUES (34, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 2 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (35, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,,');
INSERT INTO localized_display_name VALUES (36, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (37, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Three Columns - No Actions', 'en,,');
INSERT INTO localized_display_name VALUES (38, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 3 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (39, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?3???', 'zh,,');
INSERT INTO localized_display_name VALUES (40, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?3???', 'zh,TW,');
INSERT INTO localized_display_name VALUES (41, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (25%/75%) No Actions', 'en,,');
INSERT INTO localized_display_name VALUES (42, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 2 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (43, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,,');
INSERT INTO localized_display_name VALUES (44, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (45, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (25%/75%)', 'en,,');
INSERT INTO localized_display_name VALUES (46, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ????? 25/75 ? 2 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (47, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,,');
INSERT INTO localized_display_name VALUES (48, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (49, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (15%,85%) No Actions', 'en,,');
INSERT INTO localized_display_name VALUES (50, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '???????? Velocity ?????????????????????????????? 2 ?', 'ja,,');
INSERT INTO localized_display_name VALUES (51, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity???????????', 'zh,,');
INSERT INTO localized_display_name VALUES (52, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity???????????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (53, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns with Tables', 'en,,');
INSERT INTO localized_display_name VALUES (54, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????? 2 ??????', 'ja,,');
INSERT INTO localized_display_name VALUES (55, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,,');
INSERT INTO localized_display_name VALUES (56, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,TW,');
INSERT INTO localized_display_name VALUES (57, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Four Columns', 'en,,');
INSERT INTO localized_display_name VALUES (58, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???4???', 'zh,,');
INSERT INTO localized_display_name VALUES (59, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???4???', 'zh,TW,');
INSERT INTO localized_display_name VALUES (60, 1, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'Jetspeed 2 Layout Portlets Application', ',,');
INSERT INTO localized_display_name VALUES (144, 101, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Entry to Mdek App', 'en,,');
INSERT INTO localized_display_name VALUES (145, 102, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Kataloge administrieren', 'en,,');
INSERT INTO localized_display_name VALUES (146, 103, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'SuperAdmin-Login', 'en,,');
INSERT INTO localized_display_name VALUES (147, 3, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'InGrid-Portal Application', ',,');
INSERT INTO localized_display_name VALUES (161, 121, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Generisches Informationsportlet', 'en,,');
INSERT INTO localized_display_name VALUES (162, 122, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'javascript detection portlet', 'en,,');
INSERT INTO localized_display_name VALUES (163, 123, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Help Portlet', 'en,,');
INSERT INTO localized_display_name VALUES (164, 124, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminComponentMonitorPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (165, 125, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Content RSS Portlet', 'en,,');
INSERT INTO localized_display_name VALUES (166, 126, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Content Partner Portlet', 'en,,');
INSERT INTO localized_display_name VALUES (167, 127, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Content Provider Portlet', 'en,,');
INSERT INTO localized_display_name VALUES (168, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminUserPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (169, 129, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminIPlugPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (170, 130, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminCMSPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (171, 131, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminWMSPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (172, 132, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminStatisticsPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (173, 133, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminHomepagePortlet', 'en,,');
INSERT INTO localized_display_name VALUES (174, 134, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminPortalProfilePortlet', 'en,,');
INSERT INTO localized_display_name VALUES (175, 135, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditAboutInfoPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (176, 136, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditNewsletterInfoPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (177, 137, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditAdvancedInfoPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (178, 138, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminUserMigrationPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (179, 139, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Einfache Suche', 'en,,');
INSERT INTO localized_display_name VALUES (180, 140, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Environment Teaser', 'en,,');
INSERT INTO localized_display_name VALUES (181, 141, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Aktuelles', 'en,,');
INSERT INTO localized_display_name VALUES (182, 142, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Weitere Meldungen', 'en,,');
INSERT INTO localized_display_name VALUES (183, 143, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'CMS portlet', 'en,,');
INSERT INTO localized_display_name VALUES (184, 144, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Generisches Informationsportlet', 'en,,');
INSERT INTO localized_display_name VALUES (185, 145, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Generisches Willkommens-Portlet', 'en,,');
INSERT INTO localized_display_name VALUES (186, 146, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Service Teaser', 'en,,');
INSERT INTO localized_display_name VALUES (187, 147, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Measures Teaser', 'en,,');
INSERT INTO localized_display_name VALUES (188, 148, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Chronik Teaser', 'en,,');
INSERT INTO localized_display_name VALUES (189, 149, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'WeatherTeaser', 'en,,');
INSERT INTO localized_display_name VALUES (190, 150, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Contact', 'en,,');
INSERT INTO localized_display_name VALUES (191, 151, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ContactNewsletterPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (192, 152, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalLoginPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (193, 153, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalOverviewPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (194, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalCreateAccountPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (195, 155, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditAccountPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (196, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPasswordForgottenPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (197, 157, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeOverviewPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (198, 158, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeHomePortlet', 'en,,');
INSERT INTO localized_display_name VALUES (199, 159, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizePartnerPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (200, 160, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeSourcesPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (201, 161, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeSearchSettingsPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (202, 162, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ShowPartnerPortlet', 'en,,');
INSERT INTO localized_display_name VALUES (203, 163, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ShowDataSourcePortlet', 'en,,');
INSERT INTO localized_display_name VALUES (204, 164, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Service', 'en,,');
INSERT INTO localized_display_name VALUES (205, 165, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Service Suchergebnis', 'en,,');
INSERT INTO localized_display_name VALUES (206, 166, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Messwerte', 'en,,');
INSERT INTO localized_display_name VALUES (207, 167, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Messwerte Suchergebnis', 'en,,');
INSERT INTO localized_display_name VALUES (208, 168, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Themen', 'en,,');
INSERT INTO localized_display_name VALUES (209, 169, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Themen Suchergebnis', 'en,,');
INSERT INTO localized_display_name VALUES (210, 170, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KartenLinkSave', 'en,,');
INSERT INTO localized_display_name VALUES (211, 171, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Karten', 'en,,');
INSERT INTO localized_display_name VALUES (212, 172, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Chronik', 'en,,');
INSERT INTO localized_display_name VALUES (213, 173, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Chronik Suchergebnis', 'en,,');
INSERT INTO localized_display_name VALUES (214, 174, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Sucheinstellungen', 'en,,');
INSERT INTO localized_display_name VALUES (215, 175, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'SearchSavePortlet', 'en,,');
INSERT INTO localized_display_name VALUES (216, 176, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Suchhistorie', 'en,,');
INSERT INTO localized_display_name VALUES (217, 177, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Similar Terms', 'en,,');
INSERT INTO localized_display_name VALUES (218, 178, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Suchergebnis', 'en,,');
INSERT INTO localized_display_name VALUES (219, 179, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Detailinformation', 'en,,');
INSERT INTO localized_display_name VALUES (220, 180, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KatalogSucheHierarchiebaum', 'en,,');
INSERT INTO localized_display_name VALUES (221, 181, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KatalogSucheThesaurus', 'en,,');
INSERT INTO localized_display_name VALUES (222, 182, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KatalogSucheThesaurusErgebnisse', 'en,,');
INSERT INTO localized_display_name VALUES (223, 183, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwThemaSuchbegriffe', 'en,,');
INSERT INTO localized_display_name VALUES (224, 184, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwThemaFachwoerterbuch', 'en,,');
INSERT INTO localized_display_name VALUES (225, 185, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwRaumGeothesaurus', 'en,,');
INSERT INTO localized_display_name VALUES (226, 186, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwRaumKarte', 'en,,');
INSERT INTO localized_display_name VALUES (227, 187, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwZeitEinschraenkung', 'en,,');
INSERT INTO localized_display_name VALUES (228, 188, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwZeitChronik', 'en,,');
INSERT INTO localized_display_name VALUES (229, 189, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwSuchbereichInhalte', 'en,,');
INSERT INTO localized_display_name VALUES (230, 190, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwSuchbereichDatenquellen', 'en,,');
INSERT INTO localized_display_name VALUES (231, 191, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwSuchbereichBundLaender', 'en,,');
INSERT INTO localized_display_name VALUES (232, 192, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrThemaSuchbegriffe', 'en,,');
INSERT INTO localized_display_name VALUES (233, 193, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrThemaSuchmodus', 'en,,');
INSERT INTO localized_display_name VALUES (234, 194, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrRaumRaumbezug', 'en,,');
INSERT INTO localized_display_name VALUES (235, 195, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrSuchbereichBundLaender', 'en,,');
INSERT INTO localized_display_name VALUES (236, 196, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheForschungThemaSuchbegriffe', 'en,,');
INSERT INTO localized_display_name VALUES (237, 197, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheForschungThemaAttribute', 'en,,');
INSERT INTO localized_display_name VALUES (238, 198, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheRechtThemaSuchbegriffe', 'en,,');
INSERT INTO localized_display_name VALUES (239, 199, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheRechtThemaFachwoerterbuch', 'en,,');
INSERT INTO localized_display_name VALUES (240, 200, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheRechtSuchbereichBundLaender', 'en,,');
INSERT INTO localized_display_name VALUES (241, 201, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Sprachauswahl', 'en,,');
INSERT INTO localized_display_name VALUES (242, 202, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Features Information', 'en,,');
INSERT INTO localized_display_name VALUES (243, 21, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'InGrid-Portal Application', ',,');


--
-- Data for Name: media_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO media_type VALUES (1, 'html', 'UTF-8', 'HTML', 'Rich HTML for HTML 4.0 compliants browsers');
INSERT INTO media_type VALUES (2, 'vxml', 'UTF-8', 'VoiceXML', 'Format suitable for use with an audio VoiceXML server');
INSERT INTO media_type VALUES (3, 'wml', 'UTF-8', 'WML', 'Format for mobile phones and PDAs compatible with WML 1.1');
INSERT INTO media_type VALUES (4, 'xhtml-basic', 'UTF-8', 'XHTML', 'XHTML Basic');
INSERT INTO media_type VALUES (5, 'xml', '', 'XML', 'XML 1.0');


--
-- Data for Name: mediatype_to_capability; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: mediatype_to_mimetype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO mediatype_to_mimetype VALUES (1, 2);
INSERT INTO mediatype_to_mimetype VALUES (2, 4);
INSERT INTO mediatype_to_mimetype VALUES (3, 3);
INSERT INTO mediatype_to_mimetype VALUES (4, 1);
INSERT INTO mediatype_to_mimetype VALUES (5, 6);


--
-- Data for Name: mimetype; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO mimetype VALUES (1, 'application/xhtml+xml');
INSERT INTO mimetype VALUES (2, 'text/html');
INSERT INTO mimetype VALUES (3, 'text/vnd.wap.wml');
INSERT INTO mimetype VALUES (4, 'text/vxml');
INSERT INTO mimetype VALUES (5, 'text/xhtml');
INSERT INTO mimetype VALUES (6, 'text/xml');


--
-- Data for Name: named_parameter; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_dlist; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_dlist_entries; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_dmap; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_dset; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_dset_entries; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_hl_seq; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ojb_hl_seq VALUES ('SEQ_CAPABILITY', 'deprecatedColumn', 60, 20, 3);
INSERT INTO ojb_hl_seq VALUES ('SEQ_CLIENT', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FOLDER', 'deprecatedColumn', 780, 20, 39);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FOLDER_CONSTRAINT', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FOLDER_CONSTRAINTS_REF', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FOLDER_MENU', 'deprecatedColumn', 80, 20, 4);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FOLDER_METADATA', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FRAGMENT', 'deprecatedColumn', 7320, 20, 366);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FRAGMENT_CONSTRAINT', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FRAGMENT_CONSTRAINTS_REF', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FRAGMENT_PREF', 'deprecatedColumn', 7320, 20, 366);
INSERT INTO ojb_hl_seq VALUES ('SEQ_FRAGMENT_PREF_VALUE', 'deprecatedColumn', 7320, 20, 366);
INSERT INTO ojb_hl_seq VALUES ('SEQ_JETSPEED_SERVICE', 'deprecatedColumn', 60, 20, 3);
INSERT INTO ojb_hl_seq VALUES ('SEQ_LANGUAGE', 'deprecatedColumn', 260, 20, 13);
INSERT INTO ojb_hl_seq VALUES ('SEQ_LINK', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_LOCALIZED_DESCRIPTION', 'deprecatedColumn', 360, 20, 18);
INSERT INTO ojb_hl_seq VALUES ('SEQ_LOCALIZED_DISPLAY_NAME', 'deprecatedColumn', 260, 20, 13);
INSERT INTO ojb_hl_seq VALUES ('SEQ_MEDIA_TYPE', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_MIMETYPE', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PA_METADATA_FIELDS', 'deprecatedColumn', 40, 20, 2);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE', 'deprecatedColumn', 840, 20, 42);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE_CONSTRAINTS_REF', 'deprecatedColumn', 860, 20, 43);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE_METADATA', 'deprecatedColumn', 780, 20, 39);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE_SEC_CONSTRAINT_DEF', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE_SEC_CONSTRAINTS_DEF', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE_SEC_CONSTRAINTS_REF', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PAGE_SECURITY', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PARAMETER', 'deprecatedColumn', 300, 20, 15);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PD_METADATA_FIELDS', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PORTLET_APPLICATION', 'deprecatedColumn', 40, 20, 2);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PORTLET_DEFINITION', 'deprecatedColumn', 220, 20, 11);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PORTLET_PREFERENCE', 'deprecatedColumn', 120, 20, 6);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PORTLET_PREFERENCE_VALUE', 'deprecatedColumn', 120, 20, 6);
INSERT INTO ojb_hl_seq VALUES ('SEQ_PORTLET_SUPPORTS', 'deprecatedColumn', 220, 20, 11);
INSERT INTO ojb_hl_seq VALUES ('SEQ_RULE_CRITERION', 'deprecatedColumn', 40, 20, 2);
INSERT INTO ojb_hl_seq VALUES ('SEQ_SECURITY_ATTRIBUTE', 'deprecatedColumn', 9960, 20, 498);
INSERT INTO ojb_hl_seq VALUES ('SEQ_SECURITY_CREDENTIAL', 'deprecatedColumn', 780, 20, 39);
INSERT INTO ojb_hl_seq VALUES ('SEQ_SECURITY_DOMAIN', 'deprecatedColumn', 20, 20, 1);
INSERT INTO ojb_hl_seq VALUES ('SEQ_SECURITY_PERMISSION', 'deprecatedColumn', 60, 20, 3);
INSERT INTO ojb_hl_seq VALUES ('SEQ_SECURITY_PRINCIPAL', 'deprecatedColumn', 800, 20, 40);


--
-- Data for Name: ojb_lockentry; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ojb_nrm; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: pa_metadata_fields; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO pa_metadata_fields VALUES (1, 1, 'Jetspeed Layout Portlets', 'title', 'en,,');
INSERT INTO pa_metadata_fields VALUES (2, 1, 'Layout Portlets', 'title', 'en,,');
INSERT INTO pa_metadata_fields VALUES (3, 1, 'J2 Team', 'creator', 'en,,');
INSERT INTO pa_metadata_fields VALUES (4, 1, 'true', 'layout-app', 'en,,');
INSERT INTO pa_metadata_fields VALUES (5, 1, '2.2', 'pa-version', 'en,,');
INSERT INTO pa_metadata_fields VALUES (8, 3, 'ingrid portal mdek', 'title', 'en,,');
INSERT INTO pa_metadata_fields VALUES (9, 3, 'ingrid portal mdek', 'title', 'en,,');
INSERT INTO pa_metadata_fields VALUES (21, 21, 'ingrid portal applications', 'title', 'en,,');
INSERT INTO pa_metadata_fields VALUES (22, 21, 'ingrid portal applications', 'title', 'en,,');


--
-- Data for Name: pa_security_constraint; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: page; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page VALUES (1, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/default-page.psml', NULL, NULL, 'default-page.psml', NULL, 'ingrid.page.home.tooltip', 'ingrid.page.home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (2, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/detect-js.psml', NULL, NULL, 'detect-js.psml', NULL, 'Detect JavaScript', 'Detect JavaScript', 1, 'orange', 'ingrid-clear', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (3, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/disclaimer.psml', NULL, NULL, 'disclaimer.psml', NULL, 'ingrid.page.disclaimer.tooltip', 'ingrid.page.disclaimer', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (4, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/help.psml', NULL, NULL, 'help.psml', NULL, 'ingrid.page.help.link.tooltip', 'ingrid.page.help', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (5, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-about-data-source.psml', NULL, NULL, 'main-about-data-source.psml', NULL, 'ingrid.page.data.source.tooltip', 'ingrid.page.data.source', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (6, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-about-partner.psml', NULL, NULL, 'main-about-partner.psml', NULL, 'ingrid.page.partner.tooltip', 'ingrid.page.partner', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (7, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-about.psml', NULL, NULL, 'main-about.psml', NULL, 'ingrid.page.about.tooltip', 'ingrid.page.about', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (8, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-chronicle.psml', NULL, NULL, 'main-chronicle.psml', NULL, 'ingrid.page.chronicle.tooltip', 'ingrid.page.chronicle', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (11, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-maps.psml', NULL, NULL, 'main-maps.psml', NULL, 'ingrid.page.maps.tooltip', 'ingrid.page.maps', 0, 'orange', 'ingrid', 'ingrid-clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (12, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-measures.psml', NULL, NULL, 'main-measures.psml', NULL, 'ingrid.page.measures.tooltip', 'ingrid.page.measures', 0, 'orange', 'ingrid', 'ingrid-clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (13, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-search.psml', NULL, NULL, 'main-search.psml', NULL, 'ingrid.page.search.tooltip', 'ingrid.page.search.free', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (15, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/myportal-create-account.psml', NULL, NULL, 'myportal-create-account.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (16, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/myportal-password-forgotten.psml', NULL, NULL, 'myportal-password-forgotten.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (18, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/rss-news.psml', NULL, NULL, 'rss-news.psml', NULL, 'Home', 'Home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (19, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/search-detail.psml', NULL, NULL, 'search-detail.psml', NULL, 'ingrid.page.search.detail.tooltip', 'ingrid.page.search.detail', 0, 'orange', 'ingrid-clear', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (25, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-contact.psml', NULL, NULL, 'service-contact.psml', NULL, 'ingrid.page.contact.tooltip', 'ingrid.page.contact', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (26, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-myportal.psml', NULL, NULL, 'service-myportal.psml', NULL, 'ingrid.page.myportal.tooltip', 'ingrid.page.myportal', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (27, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-sitemap.psml', NULL, NULL, 'service-sitemap.psml', NULL, 'ingrid.page.sitemap.tooltip', 'ingrid.page.sitemap', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (28, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/default-page.psml', NULL, NULL, 'default-page.psml', NULL, 'ingrid.page.home.tooltip', 'ingrid.page.home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (29, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/myportal-edit-account.psml', NULL, NULL, 'myportal-edit-account.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (31, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/service-myportal.psml', NULL, NULL, 'service-myportal.psml', NULL, 'ingrid.page.myportal', 'ingrid.page.myportal', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (728, 'org.apache.jetspeed.om.page.impl.PageImpl', 701, '/_user/template/default-page.psml', NULL, NULL, 'default-page.psml', NULL, 'ingrid.page.home.tooltip', 'ingrid.page.home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, 'template', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (798, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-cms.psml', NULL, NULL, 'admin-cms.psml', NULL, 'ingrid.page.admin.cms', 'ingrid.page.admin.cms', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (799, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-component-monitor.psml', NULL, NULL, 'admin-component-monitor.psml', NULL, 'ingrid.page.admin.monitor', 'ingrid.page.admin.monitor', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (800, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-content-partner.psml', NULL, NULL, 'admin-content-partner.psml', NULL, 'ingrid.page.admin.partner', 'ingrid.page.admin.partner', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (801, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-content-provider.psml', NULL, NULL, 'admin-content-provider.psml', NULL, 'ingrid.page.admin.provider', 'ingrid.page.admin.provider', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (802, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-content-rss.psml', NULL, NULL, 'admin-content-rss.psml', NULL, 'ingrid.page.admin.rss', 'ingrid.page.admin.rss', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (803, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-homepage.psml', NULL, NULL, 'admin-homepage.psml', NULL, 'ingrid.page.admin.homepage', 'ingrid.page.admin.homepage', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (804, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-iplugs.psml', NULL, NULL, 'admin-iplugs.psml', NULL, 'ingrid.page.admin.iplugs', 'ingrid.page.admin.iplugs', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (805, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-portal-profile.psml', NULL, NULL, 'admin-portal-profile.psml', NULL, 'ingrid.page.admin.profile', 'ingrid.page.admin.profile', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (806, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-statistics.psml', NULL, NULL, 'admin-statistics.psml', NULL, 'ingrid.page.admin.statistics', 'ingrid.page.admin.statistics', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (807, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-usermanagement.psml', NULL, NULL, 'admin-usermanagement.psml', NULL, 'ingrid.page.admin.usermanagement', 'ingrid.page.admin.usermanagement', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (812, 'org.apache.jetspeed.om.page.impl.PageImpl', 772, '/application/main-application.psml', NULL, NULL, 'main-application.psml', NULL, 'ingrid.page.application.tooltip', 'ingrid.page.application', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (814, 'org.apache.jetspeed.om.page.impl.PageImpl', 773, '/cms/cms-1.psml', NULL, NULL, 'cms-1.psml', NULL, 'ingrid.page.cms.1.tooltip', 'ingrid.page.cms.1', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (815, 'org.apache.jetspeed.om.page.impl.PageImpl', 773, '/cms/cms-2.psml', NULL, NULL, 'cms-2.psml', NULL, 'ingrid.page.cms.2.tooltip', 'ingrid.page.cms.2', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (816, 'org.apache.jetspeed.om.page.impl.PageImpl', 774, '/mdek/mdek_portal_admin.psml', NULL, NULL, 'mdek_portal_admin.psml', NULL, 'ingrid.page.mdek.catadmin', 'ingrid.page.mdek.catadmin', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page VALUES (817, 'org.apache.jetspeed.om.page.impl.PageImpl', 775, '/search-catalog/search-catalog-hierarchy.psml', NULL, NULL, 'search-catalog-hierarchy.psml', NULL, 'ingrid.page.search.catalog.tooltip', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


--
-- Data for Name: page_constraint; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: page_constraints_ref; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page_constraints_ref VALUES (1, 1, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (2, 1, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (3, 2, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (4, 3, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (5, 4, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (6, 5, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (7, 6, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (8, 7, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (9, 8, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (11, 11, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (12, 12, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (13, 13, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (15, 15, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (16, 16, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (18, 18, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (19, 19, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (25, 25, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (26, 26, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (27, 27, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (28, 28, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (29, 29, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (31, 31, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (728, 728, 0, 'public-edit');
INSERT INTO page_constraints_ref VALUES (798, 798, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (799, 798, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (800, 799, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (801, 799, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (802, 800, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (803, 800, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (804, 801, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (805, 801, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (806, 802, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (807, 802, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (808, 803, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (809, 803, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (810, 804, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (811, 804, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (812, 804, 2, 'admin-partner');
INSERT INTO page_constraints_ref VALUES (813, 804, 3, 'admin-provider');
INSERT INTO page_constraints_ref VALUES (814, 805, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (815, 805, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (816, 806, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (817, 806, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (818, 806, 2, 'admin-partner');
INSERT INTO page_constraints_ref VALUES (819, 806, 3, 'admin-provider');
INSERT INTO page_constraints_ref VALUES (820, 807, 0, 'admin');
INSERT INTO page_constraints_ref VALUES (821, 807, 1, 'admin-partner');
INSERT INTO page_constraints_ref VALUES (822, 807, 2, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (834, 812, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (835, 812, 1, 'admin-portal');
INSERT INTO page_constraints_ref VALUES (837, 814, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (838, 815, 0, 'public-view');
INSERT INTO page_constraints_ref VALUES (839, 817, 0, 'public-view');


--
-- Data for Name: page_menu; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: page_menu_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: page_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page_metadata VALUES (1, 1, 'meta_descr', 'de,,', 'ingrid.page.home.meta.description');
INSERT INTO page_metadata VALUES (2, 1, 'meta_keywords', 'de,,', 'ingrid.page.home.meta.keywords');
INSERT INTO page_metadata VALUES (3, 1, 'meta_title', 'de,,', 'ingrid.page.home.meta.title');
INSERT INTO page_metadata VALUES (4, 3, 'meta_descr', 'de,,', 'ingrid.page.disclaimer.meta.description');
INSERT INTO page_metadata VALUES (5, 3, 'meta_keywords', 'de,,', 'ingrid.page.disclaimer.meta.keywords');
INSERT INTO page_metadata VALUES (6, 3, 'meta_title', 'de,,', 'ingrid.page.disclaimer.meta.title');
INSERT INTO page_metadata VALUES (7, 4, 'meta_descr', 'de,,', 'ingrid.page.help.meta.description');
INSERT INTO page_metadata VALUES (8, 4, 'meta_keywords', 'de,,', 'ingrid.page.help.meta.keywords');
INSERT INTO page_metadata VALUES (9, 4, 'meta_title', 'de,,', 'ingrid.page.help.meta.title');
INSERT INTO page_metadata VALUES (10, 5, 'meta_descr', 'de,,', 'ingrid.page.data.source.meta.description');
INSERT INTO page_metadata VALUES (11, 5, 'meta_keywords', 'de,,', 'ingrid.page.data.source.meta.keywords');
INSERT INTO page_metadata VALUES (12, 5, 'meta_title', 'de,,', 'ingrid.page.data.source.meta.title');
INSERT INTO page_metadata VALUES (13, 6, 'meta_descr', 'de,,', 'ingrid.page.about.partner.meta.description');
INSERT INTO page_metadata VALUES (14, 6, 'meta_keywords', 'de,,', 'ingrid.page.about.partner.meta.keywords');
INSERT INTO page_metadata VALUES (15, 6, 'meta_title', 'de,,', 'ingrid.page.about.partner.meta.title');
INSERT INTO page_metadata VALUES (16, 7, 'meta_descr', 'de,,', 'ingrid.page.about.meta.description');
INSERT INTO page_metadata VALUES (17, 7, 'meta_keywords', 'de,,', 'ingrid.page.about.meta.keywords');
INSERT INTO page_metadata VALUES (18, 7, 'meta_title', 'de,,', 'ingrid.page.about.meta.title');
INSERT INTO page_metadata VALUES (19, 8, 'meta_descr', 'de,,', 'ingrid.page.chronicle.meta.description');
INSERT INTO page_metadata VALUES (20, 8, 'meta_keywords', 'de,,', 'ingrid.page.chronicle.meta.keywords');
INSERT INTO page_metadata VALUES (21, 8, 'meta_title', 'de,,', 'ingrid.page.chronicle.meta.title');
INSERT INTO page_metadata VALUES (28, 11, 'meta_descr', 'de,,', 'ingrid.page.maps.meta.description');
INSERT INTO page_metadata VALUES (29, 11, 'meta_keywords', 'de,,', 'ingrid.page.maps.meta.keywords');
INSERT INTO page_metadata VALUES (30, 11, 'meta_title', 'de,,', 'ingrid.page.maps.meta.title');
INSERT INTO page_metadata VALUES (31, 12, 'meta_descr', 'de,,', 'ingrid.page.measures.meta.description');
INSERT INTO page_metadata VALUES (32, 12, 'meta_keywords', 'de,,', 'ingrid.page.measures.meta.keywords');
INSERT INTO page_metadata VALUES (33, 12, 'meta_title', 'de,,', 'ingrid.page.measures.meta.title');
INSERT INTO page_metadata VALUES (34, 13, 'meta_descr', 'de,,', 'ingrid.page.search.meta.description');
INSERT INTO page_metadata VALUES (35, 13, 'meta_keywords', 'de,,', 'ingrid.page.search.meta.keywords');
INSERT INTO page_metadata VALUES (36, 13, 'meta_title', 'de,,', 'ingrid.page.search.meta.title');
INSERT INTO page_metadata VALUES (43, 18, 'meta_descr', 'de,,', 'ingrid.page.rss.meta.description');
INSERT INTO page_metadata VALUES (44, 18, 'meta_keywords', 'de,,', 'ingrid.page.rss.meta.keywords');
INSERT INTO page_metadata VALUES (45, 18, 'meta_title', 'de,,', 'ingrid.page.rss.meta.title');
INSERT INTO page_metadata VALUES (46, 19, 'meta_descr', 'de,,', 'ingrid.page.detail.meta.description');
INSERT INTO page_metadata VALUES (47, 19, 'meta_keywords', 'de,,', 'ingrid.page.detail.meta.keywords');
INSERT INTO page_metadata VALUES (48, 19, 'meta_title', 'de,,', 'ingrid.page.detail.meta.title');
INSERT INTO page_metadata VALUES (58, 25, 'meta_descr', 'de,,', 'ingrid.page.contact.meta.description');
INSERT INTO page_metadata VALUES (59, 25, 'meta_keywords', 'de,,', 'ingrid.page.contact.meta.keywords');
INSERT INTO page_metadata VALUES (60, 25, 'meta_title', 'de,,', 'ingrid.page.contact.meta.title');
INSERT INTO page_metadata VALUES (61, 26, 'meta_descr', 'de,,', 'ingrid.page.myportal.meta.description');
INSERT INTO page_metadata VALUES (62, 26, 'meta_keywords', 'de,,', 'ingrid.page.myportal.meta.keywords');
INSERT INTO page_metadata VALUES (63, 26, 'meta_title', 'de,,', 'ingrid.page.myportal.meta.title');
INSERT INTO page_metadata VALUES (64, 27, 'meta_descr', 'de,,', 'ingrid.page.sitemap.meta.description');
INSERT INTO page_metadata VALUES (65, 27, 'meta_keywords', 'de,,', 'ingrid.page.sitemap.meta.keywords');
INSERT INTO page_metadata VALUES (66, 27, 'meta_title', 'de,,', 'ingrid.page.sitemap.meta.title');
INSERT INTO page_metadata VALUES (67, 28, 'meta_descr', 'de,,', 'ingrid.page.home.meta.description');
INSERT INTO page_metadata VALUES (68, 28, 'meta_keywords', 'de,,', 'ingrid.page.home.meta.keywords');
INSERT INTO page_metadata VALUES (69, 28, 'meta_title', 'de,,', 'ingrid.page.home.meta.title');
INSERT INTO page_metadata VALUES (697, 728, 'meta_descr', 'de,,', 'ingrid.page.home.meta.description');
INSERT INTO page_metadata VALUES (698, 728, 'meta_keywords', 'de,,', 'ingrid.page.home.meta.keywords');
INSERT INTO page_metadata VALUES (699, 728, 'meta_title', 'de,,', 'ingrid.page.home.meta.title');
INSERT INTO page_metadata VALUES (758, 812, 'meta_descr', 'de,,', 'ingrid.page.application.meta.description');
INSERT INTO page_metadata VALUES (759, 812, 'meta_keywords', 'de,,', 'ingrid.page.application.meta.keywords');
INSERT INTO page_metadata VALUES (757, 812, 'meta_title', 'de,,', 'ingrid.page.application.meta.title');
INSERT INTO page_metadata VALUES (764, 814, 'meta_descr', 'de,,', 'ingrid.page.cms.1.meta.description');
INSERT INTO page_metadata VALUES (765, 814, 'meta_keywords', 'de,,', 'ingrid.page.cms.1.meta.keywords');
INSERT INTO page_metadata VALUES (763, 814, 'meta_title', 'de,,', 'ingrid.page.cms.1.meta.title');
INSERT INTO page_metadata VALUES (767, 815, 'meta_descr', 'de,,', 'ingrid.page.cms.2.meta.description');
INSERT INTO page_metadata VALUES (768, 815, 'meta_keywords', 'de,,', 'ingrid.page.cms.2.meta.keywords');
INSERT INTO page_metadata VALUES (766, 815, 'meta_title', 'de,,', 'ingrid.page.cms.2.meta.title');
INSERT INTO page_metadata VALUES (769, 817, 'meta_descr', 'de,,', 'ingrid.page.hierarchy.meta.description');
INSERT INTO page_metadata VALUES (770, 817, 'meta_keywords', 'de,,', 'ingrid.page.hierarchy.meta.keywords');
INSERT INTO page_metadata VALUES (771, 817, 'meta_title', 'de,,', 'ingrid.page.hierarchy.meta.title');


--
-- Data for Name: page_sec_constraint_def; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page_sec_constraint_def VALUES (1, 1, 0, NULL, 'admin', NULL, 'view,edit');
INSERT INTO page_sec_constraint_def VALUES (2, 2, 0, NULL, 'admin-partner', NULL, 'view,edit');
INSERT INTO page_sec_constraint_def VALUES (3, 3, 0, NULL, 'admin-portal', NULL, 'view,edit');
INSERT INTO page_sec_constraint_def VALUES (4, 4, 0, NULL, 'admin-provider', NULL, 'view,edit');
INSERT INTO page_sec_constraint_def VALUES (5, 5, 0, NULL, 'manager', NULL, 'view');
INSERT INTO page_sec_constraint_def VALUES (6, 6, 0, '*', NULL, NULL, 'view,edit');
INSERT INTO page_sec_constraint_def VALUES (7, 7, 0, '*', NULL, NULL, 'view');
INSERT INTO page_sec_constraint_def VALUES (8, 8, 0, NULL, 'user,manager', NULL, 'view');


--
-- Data for Name: page_sec_constraints_def; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page_sec_constraints_def VALUES (1, 1, 'admin');
INSERT INTO page_sec_constraints_def VALUES (2, 1, 'admin-partner');
INSERT INTO page_sec_constraints_def VALUES (3, 1, 'admin-portal');
INSERT INTO page_sec_constraints_def VALUES (4, 1, 'admin-provider');
INSERT INTO page_sec_constraints_def VALUES (5, 1, 'manager');
INSERT INTO page_sec_constraints_def VALUES (6, 1, 'public-edit');
INSERT INTO page_sec_constraints_def VALUES (7, 1, 'public-view');
INSERT INTO page_sec_constraints_def VALUES (8, 1, 'users');


--
-- Data for Name: page_sec_constraints_ref; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page_sec_constraints_ref VALUES (1, 1, 0, 'admin');


--
-- Data for Name: page_security; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO page_security VALUES (1, 1, '/page.security', 'page.security', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


--
-- Data for Name: page_statistics; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: parameter; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO parameter VALUES (1, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'ingrid-clear-layout');
INSERT INTO parameter VALUES (2, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (3, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1');
INSERT INTO parameter VALUES (4, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%');
INSERT INTO parameter VALUES (5, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn');
INSERT INTO parameter VALUES (6, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'ingrid-one-column');
INSERT INTO parameter VALUES (7, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (8, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1');
INSERT INTO parameter VALUES (9, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%');
INSERT INTO parameter VALUES (10, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn');
INSERT INTO parameter VALUES (11, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'ingrid-two-columns');
INSERT INTO parameter VALUES (12, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (13, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (14, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%');
INSERT INTO parameter VALUES (15, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (16, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (17, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (18, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (19, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (20, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1');
INSERT INTO parameter VALUES (21, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%');
INSERT INTO parameter VALUES (22, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn');
INSERT INTO parameter VALUES (23, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'tcolumns');
INSERT INTO parameter VALUES (24, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (25, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1');
INSERT INTO parameter VALUES (26, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%');
INSERT INTO parameter VALUES (27, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn');
INSERT INTO parameter VALUES (28, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (29, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (30, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (31, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%');
INSERT INTO parameter VALUES (32, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (33, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (34, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (35, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (36, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '15%,85%');
INSERT INTO parameter VALUES (37, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (38, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (39, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (40, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '3');
INSERT INTO parameter VALUES (41, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '33%,33%,33%');
INSERT INTO parameter VALUES (42, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'ThreeColumns');
INSERT INTO parameter VALUES (43, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'tcolumns');
INSERT INTO parameter VALUES (44, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (45, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '3');
INSERT INTO parameter VALUES (46, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '33%,33%,33%');
INSERT INTO parameter VALUES (47, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'ThreeColumns');
INSERT INTO parameter VALUES (48, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (49, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (50, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1');
INSERT INTO parameter VALUES (51, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%');
INSERT INTO parameter VALUES (52, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn');
INSERT INTO parameter VALUES (53, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (54, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (55, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (56, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%');
INSERT INTO parameter VALUES (57, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (58, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (59, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (60, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '3');
INSERT INTO parameter VALUES (61, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '33%,33%,33%');
INSERT INTO parameter VALUES (62, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'ThreeColumns');
INSERT INTO parameter VALUES (63, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (64, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (65, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (66, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '25%,75%');
INSERT INTO parameter VALUES (67, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (68, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (69, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (70, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (71, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '25%,75%');
INSERT INTO parameter VALUES (72, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (73, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (74, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (75, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (76, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '15%,85%');
INSERT INTO parameter VALUES (77, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (78, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'tcolumns');
INSERT INTO parameter VALUES (79, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (80, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2');
INSERT INTO parameter VALUES (81, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%');
INSERT INTO parameter VALUES (82, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns');
INSERT INTO parameter VALUES (83, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns');
INSERT INTO parameter VALUES (84, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized');
INSERT INTO parameter VALUES (85, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '4');
INSERT INTO parameter VALUES (86, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '20%,30%,30%,20%');
INSERT INTO parameter VALUES (87, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'FourColumns');
INSERT INTO parameter VALUES (179, 101, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/mdek/mdek_entry.vm');
INSERT INTO parameter VALUES (180, 102, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/mdek/mdek_portal_admin.vm');
INSERT INTO parameter VALUES (181, 103, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/mdek/mdek_admin_login.vm');
INSERT INTO parameter VALUES (201, 121, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_info.vm');
INSERT INTO parameter VALUES (202, 123, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/help.vm');
INSERT INTO parameter VALUES (203, 124, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/component_monitor.vm');
INSERT INTO parameter VALUES (204, 125, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/content_rss.vm');
INSERT INTO parameter VALUES (205, 126, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/content_partner.vm');
INSERT INTO parameter VALUES (206, 127, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/content_provider.vm');
INSERT INTO parameter VALUES (207, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_user_browser.vm');
INSERT INTO parameter VALUES (208, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'emailTemplate', '/WEB-INF/templates/administration/userCreatedConfirmationEmail.vm');
INSERT INTO parameter VALUES (209, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'roles', 'user');
INSERT INTO parameter VALUES (210, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'groups', '');
INSERT INTO parameter VALUES (211, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesNames', 'user-role-fallback');
INSERT INTO parameter VALUES (212, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesValues', 'page');
INSERT INTO parameter VALUES (213, 129, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_iplug.vm');
INSERT INTO parameter VALUES (214, 130, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_cms_browser.vm');
INSERT INTO parameter VALUES (215, 131, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_wms.vm');
INSERT INTO parameter VALUES (216, 132, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_statistics.vm');
INSERT INTO parameter VALUES (217, 133, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_homepage.vm');
INSERT INTO parameter VALUES (218, 134, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_portal_profile.vm');
INSERT INTO parameter VALUES (219, 135, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/about_portalu_partner.vm');
INSERT INTO parameter VALUES (220, 136, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/newsletter_teaser.vm');
INSERT INTO parameter VALUES (221, 137, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/login_teaser.vm');
INSERT INTO parameter VALUES (222, 138, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_user_migration.vm');
INSERT INTO parameter VALUES (223, 139, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_simple.vm');
INSERT INTO parameter VALUES (224, 140, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/environment_teaser.vm');
INSERT INTO parameter VALUES (225, 141, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/rss_news_teaser.vm');
INSERT INTO parameter VALUES (226, 142, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/rss_news.vm');
INSERT INTO parameter VALUES (227, 143, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_cms.vm');
INSERT INTO parameter VALUES (228, 144, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_cms.vm');
INSERT INTO parameter VALUES (229, 145, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_cms.vm');
INSERT INTO parameter VALUES (230, 146, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/service_teaser_new.vm');
INSERT INTO parameter VALUES (231, 147, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/measures_teaser_new.vm');
INSERT INTO parameter VALUES (232, 148, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/chronicle_teaser_new.vm');
INSERT INTO parameter VALUES (233, 149, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/weather_teaser.vm');
INSERT INTO parameter VALUES (234, 150, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/contact.vm');
INSERT INTO parameter VALUES (235, 151, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/contact_newsletter.vm');
INSERT INTO parameter VALUES (236, 152, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/login.vm');
INSERT INTO parameter VALUES (237, 153, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_overview.vm');
INSERT INTO parameter VALUES (238, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_create_account.vm');
INSERT INTO parameter VALUES (239, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'returnURL', '/myportal-create-account.psml');
INSERT INTO parameter VALUES (240, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'emailTemplate', '/WEB-INF/templates/myportal/userRegistrationEmail.vm');
INSERT INTO parameter VALUES (241, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesNames', 'user-role-fallback');
INSERT INTO parameter VALUES (242, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesValues', 'page');
INSERT INTO parameter VALUES (243, 155, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_edit_account.vm');
INSERT INTO parameter VALUES (244, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_password_forgotten.vm');
INSERT INTO parameter VALUES (245, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'emailTemplate', '/WEB-INF/templates/myportal/forgottenPasswdEmail.vm');
INSERT INTO parameter VALUES (246, 157, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_overview.vm');
INSERT INTO parameter VALUES (247, 158, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_home.vm');
INSERT INTO parameter VALUES (248, 159, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_partner.vm');
INSERT INTO parameter VALUES (249, 160, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_sources.vm');
INSERT INTO parameter VALUES (250, 161, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_search_settings.vm');
INSERT INTO parameter VALUES (251, 162, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/show_partner.vm');
INSERT INTO parameter VALUES (252, 163, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/show_data_source.vm');
INSERT INTO parameter VALUES (253, 164, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/service_search.vm');
INSERT INTO parameter VALUES (254, 165, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm');
INSERT INTO parameter VALUES (255, 166, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/measures_search.vm');
INSERT INTO parameter VALUES (256, 167, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm');
INSERT INTO parameter VALUES (257, 168, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/environment_search.vm');
INSERT INTO parameter VALUES (258, 169, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm');
INSERT INTO parameter VALUES (259, 170, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/save_maps.vm');
INSERT INTO parameter VALUES (260, 171, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/show_maps.vm');
INSERT INTO parameter VALUES (261, 172, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/chronicle_search.vm');
INSERT INTO parameter VALUES (262, 173, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm');
INSERT INTO parameter VALUES (263, 174, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_settings.vm');
INSERT INTO parameter VALUES (264, 175, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_save.vm');
INSERT INTO parameter VALUES (265, 176, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_history.vm');
INSERT INTO parameter VALUES (266, 177, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_similar.vm');
INSERT INTO parameter VALUES (267, 178, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_result.vm');
INSERT INTO parameter VALUES (268, 179, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_detail.vm');
INSERT INTO parameter VALUES (269, 180, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_catalog/search_cat_hierarchy.vm');
INSERT INTO parameter VALUES (270, 181, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_catalog/search_cat_thesaurus.vm');
INSERT INTO parameter VALUES (271, 182, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_catalog/search_cat_thesaurus_result.vm');
INSERT INTO parameter VALUES (272, 183, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_topic_terms.vm');
INSERT INTO parameter VALUES (273, 184, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus.vm');
INSERT INTO parameter VALUES (274, 185, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus.vm');
INSERT INTO parameter VALUES (275, 186, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_place_map.vm');
INSERT INTO parameter VALUES (276, 187, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_time_constraint.vm');
INSERT INTO parameter VALUES (277, 188, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_time_chronicle.vm');
INSERT INTO parameter VALUES (278, 189, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_area_contents.vm');
INSERT INTO parameter VALUES (279, 190, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_area_sources.vm');
INSERT INTO parameter VALUES (280, 191, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_area_partner.vm');
INSERT INTO parameter VALUES (281, 192, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_topic_terms.vm');
INSERT INTO parameter VALUES (282, 193, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_topic_mode.vm');
INSERT INTO parameter VALUES (283, 194, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_place_reference.vm');
INSERT INTO parameter VALUES (284, 195, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_area_partner.vm');
INSERT INTO parameter VALUES (285, 196, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_res_topic_terms.vm');
INSERT INTO parameter VALUES (286, 197, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_res_topic_attributes.vm');
INSERT INTO parameter VALUES (287, 198, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_law_topic_terms.vm');
INSERT INTO parameter VALUES (288, 199, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_law_topic_thesaurus.vm');
INSERT INTO parameter VALUES (289, 200, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_law_area_partner.vm');
INSERT INTO parameter VALUES (290, 201, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/language.vm');
INSERT INTO parameter VALUES (291, 202, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/features.vm');


--
-- Data for Name: parameter_alias; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: pd_metadata_fields; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO pd_metadata_fields VALUES (1, 4, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (2, 5, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (3, 6, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (4, 7, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (5, 8, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (6, 9, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (7, 10, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (8, 11, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (9, 12, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (10, 13, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (11, 14, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (12, 15, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (13, 16, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (14, 17, '*', 'selector.conditional.role', 'en,,');
INSERT INTO pd_metadata_fields VALUES (15, 18, '*', 'selector.conditional.role', 'en,,');


--
-- Data for Name: portlet_application; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portlet_application VALUES (1, 'jetspeed-layouts', '<portal>', 1, '1.0', 1, '982190297', NULL, '', NULL);
INSERT INTO portlet_application VALUES (3, 'ingrid-portal-mdek', '/ingrid-portal-mdek', 1, '1.0', 0, '382981415', NULL, '', NULL);
INSERT INTO portlet_application VALUES (21, 'ingrid-portal-apps', '/ingrid-portal-apps', 2, '1.0', 0, '3223520607', NULL, '', NULL);


--
-- Data for Name: portlet_definition; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portlet_definition VALUES (1, 'IngridClearLayout', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'de.ingrid.portal.resources.PortalLayoutResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (2, 'IngridOneColumn', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'de.ingrid.portal.resources.PortalLayoutResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (3, 'IngridTwoColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'de.ingrid.portal.resources.PortalLayoutResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (4, 'SimpleLayout', 'org.apache.jetspeed.portlets.layout.LayoutPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (5, 'VelocityOneColumn', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (6, 'VelocityOneColumnTable', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (7, 'VelocityTwoColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (8, 'VelocityTwoColumnsSmallLeft', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (9, 'VelocityThreeColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (10, 'VelocityThreeColumnsTable', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (11, 'VelocityOneColumnNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (12, 'VelocityTwoColumnsNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (13, 'VelocityThreeColumnsNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (14, 'VelocityTwoColumns2575NoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (15, 'VelocityTwoColumns2575', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (16, 'VelocityTwoColumnsSmallLeftNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (17, 'VelocityTwoColumnsTable', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (18, 'VelocityFourColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (101, 'MdekEntryPortlet', 'de.ingrid.portal.portlets.mdek.MdekEntryPortlet', 3, 0, 'de.ingrid.portal.resources.MdekResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (102, 'MdekPortalAdminPortlet', 'de.ingrid.portal.portlets.mdek.MdekPortalAdminPortlet', 3, 0, 'de.ingrid.portal.resources.MdekResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (103, 'MdekAdminLoginPortlet', 'de.ingrid.portal.portlets.mdek.MdekAdminLoginPortlet', 3, 0, 'de.ingrid.portal.resources.MdekResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (121, 'InfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (122, 'DetectJavaScriptPortlet', 'de.ingrid.portal.portlets.DetectJavaScriptPortlet', 21, 0, NULL, NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (123, 'HelpPortlet', 'de.ingrid.portal.portlets.HelpPortlet', 21, 0, 'de.ingrid.portal.resources.HelpPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (124, 'AdminComponentMonitorPortlet', 'de.ingrid.portal.portlets.admin.AdminComponentMonitorPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (125, 'ContentRSSPortlet', 'de.ingrid.portal.portlets.admin.ContentRSSPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (126, 'ContentPartnerPortlet', 'de.ingrid.portal.portlets.admin.ContentPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (127, 'ContentProviderPortlet', 'de.ingrid.portal.portlets.admin.ContentProviderPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (128, 'AdminUserPortlet', 'de.ingrid.portal.portlets.admin.AdminUserPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (129, 'AdminIPlugPortlet', 'de.ingrid.portal.portlets.admin.AdminIPlugPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (130, 'AdminCMSPortlet', 'de.ingrid.portal.portlets.admin.AdminCMSPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (131, 'AdminWMSPortlet', 'de.ingrid.portal.portlets.admin.AdminWMSPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (132, 'AdminStatisticsPortlet', 'de.ingrid.portal.portlets.admin.AdminStatisticsPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (133, 'AdminHomepagePortlet', 'de.ingrid.portal.portlets.admin.AdminHomepagePortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (134, 'AdminPortalProfilePortlet', 'de.ingrid.portal.portlets.admin.AdminPortalProfilePortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (135, 'MyPortalEditAboutInfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (136, 'MyPortalEditNewsletterInfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (137, 'MyPortalEditAdvancedInfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (138, 'AdminUserMigrationPortlet', 'de.ingrid.portal.portlets.admin.AdminUserMigrationPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (139, 'SearchSimple', 'de.ingrid.portal.portlets.SearchSimplePortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (140, 'EnvironmentTeaser', 'de.ingrid.portal.portlets.EnvironmentTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.EnvironmentSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (141, 'RssNewsTeaser', 'de.ingrid.portal.portlets.RssNewsTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.RssNewsResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (142, 'RssNews', 'de.ingrid.portal.portlets.RssNewsPortlet', 21, 0, 'de.ingrid.portal.resources.RssNewsResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (143, 'CMSPortlet', 'de.ingrid.portal.portlets.CMSPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (144, 'IngridInformPortlet', 'de.ingrid.portal.portlets.CMSPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (145, 'IngridWelcomePortlet', 'de.ingrid.portal.portlets.CMSPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (146, 'ServiceTeaser', 'de.ingrid.portal.portlets.ServiceTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.ServiceSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (147, 'MeasuresTeaser', 'de.ingrid.portal.portlets.MeasuresTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.MeasuresSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (148, 'ChronicleTeaser', 'de.ingrid.portal.portlets.ChronicleTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.ChronicleSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (149, 'WeatherTeaser', 'de.ingrid.portal.portlets.WeatherTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.WeatherTeaserResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (150, 'Contact', 'de.ingrid.portal.portlets.ContactPortlet', 21, 0, 'de.ingrid.portal.resources.ContactResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (151, 'ContactNewsletterPortlet', 'de.ingrid.portal.portlets.ContactNewsletterPortlet', 21, 0, 'de.ingrid.portal.resources.ContactNewsletterResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (152, 'MyPortalLoginPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalLoginPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (153, 'MyPortalOverviewPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalOverviewPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (154, 'MyPortalCreateAccountPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalCreateAccountPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (155, 'MyPortalEditAccountPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalEditAccountPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (156, 'MyPortalPasswordForgottenPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPasswordForgottenPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (157, 'MyPortalPersonalizeOverviewPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeOverviewPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (158, 'MyPortalPersonalizeHomePortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeHomePortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (159, 'MyPortalPersonalizePartnerPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizePartnerPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (160, 'MyPortalPersonalizeSourcesPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeSourcesPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (161, 'MyPortalPersonalizeSearchSettingsPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeSearchSettingsPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (162, 'ShowPartnerPortlet', 'de.ingrid.portal.portlets.ShowPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.ShowPartnerPortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (163, 'ShowDataSourcePortlet', 'de.ingrid.portal.portlets.ShowDataSourcePortlet', 21, 0, 'de.ingrid.portal.resources.ShowDataSourcePortletResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (164, 'ServiceSearch', 'de.ingrid.portal.portlets.ServiceSearchPortlet', 21, 0, 'de.ingrid.portal.resources.ServiceSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (165, 'ServiceResult', 'de.ingrid.portal.portlets.ServiceResultPortlet', 21, 0, 'de.ingrid.portal.resources.ServiceSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (166, 'MeasuresSearch', 'de.ingrid.portal.portlets.MeasuresSearchPortlet', 21, 0, 'de.ingrid.portal.resources.MeasuresSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (167, 'MeasuresResult', 'de.ingrid.portal.portlets.MeasuresResultPortlet', 21, 0, 'de.ingrid.portal.resources.MeasuresSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (168, 'EnvironmentSearch', 'de.ingrid.portal.portlets.EnvironmentSearchPortlet', 21, 0, 'de.ingrid.portal.resources.EnvironmentSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (169, 'EnvironmentResult', 'de.ingrid.portal.portlets.EnvironmentResultPortlet', 21, 0, 'de.ingrid.portal.resources.EnvironmentSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (170, 'SaveMapsPortlet', 'de.ingrid.portal.portlets.SaveMapsPortlet', 21, 0, 'de.ingrid.portal.resources.CommonResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (171, 'ShowMapsPortlet', 'de.ingrid.portal.portlets.ShowMapsPortlet', 21, 0, 'de.ingrid.portal.resources.CommonResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (172, 'ChronicleSearch', 'de.ingrid.portal.portlets.ChronicleSearchPortlet', 21, 0, 'de.ingrid.portal.resources.ChronicleSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (173, 'ChronicleResult', 'de.ingrid.portal.portlets.ChronicleResultPortlet', 21, 0, 'de.ingrid.portal.resources.ChronicleSearchResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (174, 'SearchSettings', 'de.ingrid.portal.portlets.SearchSettingsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (175, 'SearchSavePortlet', 'de.ingrid.portal.portlets.SearchSavePortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (176, 'SearchHistory', 'de.ingrid.portal.portlets.SearchHistoryPortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (177, 'SearchSimilar', 'de.ingrid.portal.portlets.SearchSimilarPortlet', 21, 0, 'de.ingrid.portal.resources.SearchResultResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (178, 'SearchResult', 'de.ingrid.portal.portlets.SearchResultPortlet', 21, 0, 'de.ingrid.portal.resources.SearchResultResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (179, 'SearchDetail', 'de.ingrid.portal.portlets.SearchDetailPortlet', 21, 0, 'de.ingrid.portal.resources.SearchDetailResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (180, 'SearchCatalogHierarchy', 'de.ingrid.portal.portlets.searchcatalog.SearchCatalogHierarchyPortlet', 21, 0, 'de.ingrid.portal.resources.SearchCatalogResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (181, 'SearchCatalogThesaurus', 'de.ingrid.portal.portlets.searchcatalog.SearchCatalogThesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchCatalogResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (182, 'SearchCatalogThesaurusResult', 'de.ingrid.portal.portlets.searchcatalog.SearchCatalogThesaurusResultPortlet', 21, 0, 'de.ingrid.portal.resources.SearchCatalogResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (183, 'SearchExtEnvTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (184, 'SearchExtEnvTopicThesaurus', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTopicThesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (185, 'SearchExtEnvPlaceGeothesaurus', 'de.ingrid.portal.portlets.searchext.SearchExtEnvPlaceGeothesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (186, 'SearchExtEnvPlaceMap', 'de.ingrid.portal.portlets.searchext.SearchExtEnvPlaceMapPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (187, 'SearchExtEnvTimeConstraint', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTimeConstraintPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (188, 'SearchExtEnvTimeChronicle', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTimeChroniclePortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (189, 'SearchExtEnvAreaContents', 'de.ingrid.portal.portlets.searchext.SearchExtEnvAreaContentsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (190, 'SearchExtEnvAreaSources', 'de.ingrid.portal.portlets.searchext.SearchExtEnvAreaSourcesPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (191, 'SearchExtEnvAreaPartner', 'de.ingrid.portal.portlets.searchext.SearchExtEnvAreaPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (192, 'SearchExtAdrTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtAdrTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (193, 'SearchExtAdrTopicMode', 'de.ingrid.portal.portlets.searchext.SearchExtAdrTopicModePortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (194, 'SearchExtAdrPlaceReference', 'de.ingrid.portal.portlets.searchext.SearchExtAdrPlaceReferencePortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (195, 'SearchExtAdrAreaPartner', 'de.ingrid.portal.portlets.searchext.SearchExtAdrAreaPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (196, 'SearchExtResTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtResTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (197, 'SearchExtResTopicAttributes', 'de.ingrid.portal.portlets.searchext.SearchExtResTopicAttributesPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (198, 'SearchExtLawTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtLawTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (199, 'SearchExtLawTopicThesaurus', 'de.ingrid.portal.portlets.searchext.SearchExtLawTopicThesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (200, 'SearchExtLawAreaPartner', 'de.ingrid.portal.portlets.searchext.SearchExtLawAreaPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (201, 'LanguageSwitch', 'de.ingrid.portal.portlets.LanguageSwitchPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL);
INSERT INTO portlet_definition VALUES (202, 'ShowFeaturesPortlet', 'de.ingrid.portal.portlets.ShowFeaturesPortlet', 21, 0, 'de.ingrid.portal.resources.FeaturesResources', NULL, NULL, 'private', NULL);


--
-- Data for Name: portlet_filter; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: portlet_listener; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: portlet_preference; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portlet_preference VALUES (49, 'portlet', 'ingrid-portal-mdek', 'MdekEntryPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (50, 'portlet', 'ingrid-portal-mdek', 'MdekPortalAdminPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (51, 'portlet', 'ingrid-portal-mdek', 'MdekAdminLoginPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (61, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAboutInfoPortlet', NULL, NULL, 'infoTemplate', 0);
INSERT INTO portlet_preference VALUES (62, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAboutInfoPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (63, 'portlet', 'ingrid-portal-apps', 'MyPortalEditNewsletterInfoPortlet', NULL, NULL, 'infoTemplate', 0);
INSERT INTO portlet_preference VALUES (64, 'portlet', 'ingrid-portal-apps', 'MyPortalEditNewsletterInfoPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (65, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAdvancedInfoPortlet', NULL, NULL, 'infoTemplate', 0);
INSERT INTO portlet_preference VALUES (66, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAdvancedInfoPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (67, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (68, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (69, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (70, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (71, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (72, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (73, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (74, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (75, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (76, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (77, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (78, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (79, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'noOfEntriesDisplayed', 0);
INSERT INTO portlet_preference VALUES (80, 'portlet', 'ingrid-portal-apps', 'CMSPortlet', NULL, NULL, 'cmsKey', 0);
INSERT INTO portlet_preference VALUES (81, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (82, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'cmsKey', 0);
INSERT INTO portlet_preference VALUES (83, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (84, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (85, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'infoTemplate', 0);
INSERT INTO portlet_preference VALUES (86, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (87, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (88, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'cmsKey', 0);
INSERT INTO portlet_preference VALUES (89, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (90, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'infoTemplate', 0);
INSERT INTO portlet_preference VALUES (91, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (92, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (93, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (94, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (95, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (96, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (97, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (98, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (99, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (100, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (101, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (102, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (103, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (104, 'portlet', 'ingrid-portal-apps', 'WeatherTeaser', NULL, NULL, 'portlet-type', 0);
INSERT INTO portlet_preference VALUES (105, 'portlet', 'ingrid-portal-apps', 'WeatherTeaser', NULL, NULL, 'default-vertical-position', 0);
INSERT INTO portlet_preference VALUES (106, 'portlet', 'ingrid-portal-apps', 'WeatherTeaser', NULL, NULL, 'titleKey', 0);
INSERT INTO portlet_preference VALUES (107, 'portlet', 'ingrid-portal-apps', 'SearchCatalogHierarchy', NULL, NULL, 'helpKey', 0);
INSERT INTO portlet_preference VALUES (108, 'portlet', 'ingrid-portal-apps', 'SearchCatalogThesaurus', NULL, NULL, 'helpKey', 0);


--
-- Data for Name: portlet_preference_value; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portlet_preference_value VALUES (49, 49, 0, 'mdek.title.entry');
INSERT INTO portlet_preference_value VALUES (50, 50, 0, 'mdek.title.portaladmin');
INSERT INTO portlet_preference_value VALUES (51, 51, 0, 'mdek.title.adminlogin');
INSERT INTO portlet_preference_value VALUES (61, 61, 0, '/WEB-INF/templates/myportal/myportal_navigation.vm');
INSERT INTO portlet_preference_value VALUES (62, 62, 0, 'myPortal.info.navigation.title');
INSERT INTO portlet_preference_value VALUES (63, 63, 0, '/WEB-INF/templates/newsletter_teaser.vm');
INSERT INTO portlet_preference_value VALUES (64, 64, 0, 'teaser.newsletter.title');
INSERT INTO portlet_preference_value VALUES (65, 65, 0, '/WEB-INF/templates/myportal/login_teaser.vm');
INSERT INTO portlet_preference_value VALUES (66, 66, 0, 'teaser.login.title');
INSERT INTO portlet_preference_value VALUES (67, 67, 0, 'ingrid-home');
INSERT INTO portlet_preference_value VALUES (68, 68, 0, '0');
INSERT INTO portlet_preference_value VALUES (69, 69, 0, 'search-1');
INSERT INTO portlet_preference_value VALUES (70, 70, 0, 'searchSimple.title.search');
INSERT INTO portlet_preference_value VALUES (71, 71, 0, 'ingrid-home');
INSERT INTO portlet_preference_value VALUES (72, 72, 0, '1');
INSERT INTO portlet_preference_value VALUES (73, 73, 0, 'search-topics-1');
INSERT INTO portlet_preference_value VALUES (74, 74, 0, 'teaser.environment.title');
INSERT INTO portlet_preference_value VALUES (75, 75, 0, 'ingrid-home');
INSERT INTO portlet_preference_value VALUES (76, 76, 0, '2');
INSERT INTO portlet_preference_value VALUES (77, 77, 0, 'rss-news-1');
INSERT INTO portlet_preference_value VALUES (78, 78, 0, 'news.teaser.title');
INSERT INTO portlet_preference_value VALUES (79, 79, 0, '4');
INSERT INTO portlet_preference_value VALUES (80, 80, 0, 'ingrid.cms.default');
INSERT INTO portlet_preference_value VALUES (81, 81, 0, 'ingrid-home-marginal');
INSERT INTO portlet_preference_value VALUES (82, 82, 0, 'ingrid.teaser.inform');
INSERT INTO portlet_preference_value VALUES (83, 83, 0, '0');
INSERT INTO portlet_preference_value VALUES (84, 84, 0, 'ingrid-inform-1');
INSERT INTO portlet_preference_value VALUES (85, 85, 0, '/WEB-INF/templates/default_cms.vm');
INSERT INTO portlet_preference_value VALUES (86, 86, 0, 'teaser.ingridInform.title');
INSERT INTO portlet_preference_value VALUES (87, 87, 0, 'ingrid-home');
INSERT INTO portlet_preference_value VALUES (88, 88, 0, 'ingrid.home.welcome');
INSERT INTO portlet_preference_value VALUES (89, 89, 0, '3');
INSERT INTO portlet_preference_value VALUES (90, 90, 0, '/WEB-INF/templates/default_cms.vm');
INSERT INTO portlet_preference_value VALUES (91, 91, 0, 'ingrid.home.welcome.title');
INSERT INTO portlet_preference_value VALUES (92, 92, 0, 'ingrid-home-marginal');
INSERT INTO portlet_preference_value VALUES (93, 93, 0, '1');
INSERT INTO portlet_preference_value VALUES (94, 94, 0, 'search-service-1');
INSERT INTO portlet_preference_value VALUES (95, 95, 0, 'teaser.service.title');
INSERT INTO portlet_preference_value VALUES (96, 96, 0, 'ingrid-home-marginal');
INSERT INTO portlet_preference_value VALUES (97, 97, 0, '2');
INSERT INTO portlet_preference_value VALUES (98, 98, 0, 'search-measure-1');
INSERT INTO portlet_preference_value VALUES (99, 99, 0, 'teaser.measures.title');
INSERT INTO portlet_preference_value VALUES (100, 100, 0, 'ingrid-home-marginal');
INSERT INTO portlet_preference_value VALUES (101, 101, 0, '3');
INSERT INTO portlet_preference_value VALUES (102, 102, 0, 'search-chronicle-1');
INSERT INTO portlet_preference_value VALUES (103, 103, 0, 'chronicle.teaser.title');
INSERT INTO portlet_preference_value VALUES (104, 104, 0, 'ingrid-home-marginal');
INSERT INTO portlet_preference_value VALUES (105, 105, 0, '4');
INSERT INTO portlet_preference_value VALUES (106, 106, 0, 'teaser.weather.title');
INSERT INTO portlet_preference_value VALUES (107, 107, 0, 'search-catalog-1');
INSERT INTO portlet_preference_value VALUES (108, 108, 0, 'search-catalog-2');


--
-- Data for Name: portlet_statistics; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: portlet_supports; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO portlet_supports VALUES (1, 1, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (2, 2, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (3, 3, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (4, 4, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (5, 5, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (6, 5, 'text/vnd.wap.wml', '"view"', '');
INSERT INTO portlet_supports VALUES (7, 6, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (8, 7, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (9, 8, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (10, 9, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (11, 10, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (12, 11, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (13, 11, 'text/vnd.wap.wml', '"view"', '');
INSERT INTO portlet_supports VALUES (14, 12, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (15, 13, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (16, 14, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (17, 15, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (18, 16, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (19, 17, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (20, 18, 'text/html', '"view","edit","help"', '');
INSERT INTO portlet_supports VALUES (103, 101, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (104, 102, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (105, 103, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (121, 121, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (122, 122, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (123, 123, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (124, 124, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (125, 125, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (126, 126, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (127, 127, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (128, 128, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (129, 129, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (130, 130, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (131, 131, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (132, 132, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (133, 133, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (134, 134, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (135, 135, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (136, 136, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (137, 137, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (138, 138, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (139, 139, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (140, 140, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (141, 141, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (142, 142, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (143, 143, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (144, 144, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (145, 145, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (146, 146, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (147, 147, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (148, 148, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (149, 149, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (150, 150, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (151, 151, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (152, 152, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (153, 153, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (154, 154, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (155, 155, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (156, 156, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (157, 157, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (158, 158, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (159, 159, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (160, 160, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (161, 161, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (162, 162, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (163, 163, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (164, 164, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (165, 165, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (166, 166, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (167, 167, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (168, 168, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (169, 169, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (170, 170, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (171, 171, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (172, 172, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (173, 173, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (174, 174, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (175, 175, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (176, 176, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (177, 177, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (178, 178, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (179, 179, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (180, 180, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (181, 181, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (182, 182, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (183, 183, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (184, 184, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (185, 185, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (186, 186, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (187, 187, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (188, 188, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (189, 189, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (190, 190, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (191, 191, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (192, 192, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (193, 193, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (194, 194, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (195, 195, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (196, 196, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (197, 197, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (198, 198, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (199, 199, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (200, 200, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (201, 201, 'text/html', '"view"', '');
INSERT INTO portlet_supports VALUES (202, 202, 'text/html', '"view"', '');


--
-- Data for Name: principal_permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO principal_permission VALUES (8, 1);
INSERT INTO principal_permission VALUES (11, 1);
INSERT INTO principal_permission VALUES (12, 1);
INSERT INTO principal_permission VALUES (13, 1);
INSERT INTO principal_permission VALUES (13, 2);
INSERT INTO principal_permission VALUES (13, 3);
INSERT INTO principal_permission VALUES (13, 4);
INSERT INTO principal_permission VALUES (8, 5);
INSERT INTO principal_permission VALUES (8, 6);
INSERT INTO principal_permission VALUES (13, 6);
INSERT INTO principal_permission VALUES (13, 7);
INSERT INTO principal_permission VALUES (13, 8);
INSERT INTO principal_permission VALUES (11, 11);
INSERT INTO principal_permission VALUES (12, 11);
INSERT INTO principal_permission VALUES (11, 12);
INSERT INTO principal_permission VALUES (12, 12);
INSERT INTO principal_permission VALUES (4, 17);
INSERT INTO principal_permission VALUES (9, 18);
INSERT INTO principal_permission VALUES (8, 19);
INSERT INTO principal_permission VALUES (8, 20);
INSERT INTO principal_permission VALUES (13, 22);
INSERT INTO principal_permission VALUES (4, 23);
INSERT INTO principal_permission VALUES (13, 24);
INSERT INTO principal_permission VALUES (13, 41);


--
-- Data for Name: principal_rule_assoc; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO principal_rule_assoc VALUES ('admin', 'page', 'role-fallback');
INSERT INTO principal_rule_assoc VALUES ('subsite', 'page', 'subsite-role-fallback-home');
INSERT INTO principal_rule_assoc VALUES ('subsite2', 'page', 'subsite2-role-fallback-home');


--
-- Data for Name: processing_event; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: profile_page_assoc; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: profiling_rule; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO profiling_rule VALUES ('group-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 group-based fallback');
INSERT INTO profiling_rule VALUES ('j1', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'The default profiling rule following the Jetspeed-1 hard-coded profiler fallback algorithm.');
INSERT INTO profiling_rule VALUES ('j2', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'The default profiling rule for users and mediatype minus language and country.');
INSERT INTO profiling_rule VALUES ('path', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'use a path to locate.');
INSERT INTO profiling_rule VALUES ('role-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
INSERT INTO profiling_rule VALUES ('role-group', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm that searches all groups and roles for a user');
INSERT INTO profiling_rule VALUES ('security', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'The security profiling rule needed for credential change requirements.');
INSERT INTO profiling_rule VALUES ('subsite-role-fallback-home', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A rule based on role fallback algorithm with specified subsite and home page');
INSERT INTO profiling_rule VALUES ('subsite2-role-fallback-home', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A rule based on role fallback algorithm with specified subsite and home page');
INSERT INTO profiling_rule VALUES ('user-role-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
INSERT INTO profiling_rule VALUES ('user-rolecombo-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 role-based fallback');


--
-- Data for Name: public_parameter; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: publishing_event; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_blob_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_job_listeners; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_locks; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO qrtz_locks VALUES ('CALENDAR_ACCESS');
INSERT INTO qrtz_locks VALUES ('JOB_ACCESS');
INSERT INTO qrtz_locks VALUES ('MISFIRE_ACCESS');
INSERT INTO qrtz_locks VALUES ('STATE_ACCESS');
INSERT INTO qrtz_locks VALUES ('TRIGGER_ACCESS');


--
-- Data for Name: qrtz_paused_trigger_grps; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_scheduler_state; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_trigger_listeners; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: rule_criterion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO rule_criterion VALUES ('1', 'j1', 0, 'path.session', 'page', 'default-page', 0);
INSERT INTO rule_criterion VALUES ('10', 'role-group', 1, 'navigation', 'navigation', '/', 2);
INSERT INTO rule_criterion VALUES ('11', 'role-group', 2, 'group', 'group', NULL, 2);
INSERT INTO rule_criterion VALUES ('12', 'group-fallback', 0, 'group', 'group', NULL, 2);
INSERT INTO rule_criterion VALUES ('13', 'group-fallback', 1, 'path.session', 'page', 'default-page', 0);
INSERT INTO rule_criterion VALUES ('14', 'security', 0, 'hard.coded', 'page', '/my-account.psml', 0);
INSERT INTO rule_criterion VALUES ('15', 'j2', 0, 'path.session', 'page', 'default-page', 0);
INSERT INTO rule_criterion VALUES ('16', 'j2', 1, 'group.role.user', 'user', NULL, 0);
INSERT INTO rule_criterion VALUES ('17', 'j2', 2, 'mediatype', 'mediatype', NULL, 1);
INSERT INTO rule_criterion VALUES ('18', 'user-role-fallback', 0, 'user', 'user', NULL, 2);
INSERT INTO rule_criterion VALUES ('19', 'user-role-fallback', 1, 'navigation', 'navigation', '/', 2);
INSERT INTO rule_criterion VALUES ('2', 'j1', 1, 'group.role.user', 'user', NULL, 0);
INSERT INTO rule_criterion VALUES ('20', 'user-role-fallback', 2, 'role', 'role', NULL, 2);
INSERT INTO rule_criterion VALUES ('21', 'user-role-fallback', 3, 'path.session', 'page', 'default-page', 1);
INSERT INTO rule_criterion VALUES ('22', 'user-rolecombo-fallback', 0, 'user', 'user', NULL, 2);
INSERT INTO rule_criterion VALUES ('23', 'user-rolecombo-fallback', 1, 'navigation', 'navigation', '/', 2);
INSERT INTO rule_criterion VALUES ('24', 'user-rolecombo-fallback', 2, 'rolecombo', 'role', NULL, 2);
INSERT INTO rule_criterion VALUES ('25', 'user-rolecombo-fallback', 3, 'path.session', 'page', 'default-page', 1);
INSERT INTO rule_criterion VALUES ('26', 'subsite-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2);
INSERT INTO rule_criterion VALUES ('27', 'subsite-role-fallback-home', 1, 'role', 'role', NULL, 2);
INSERT INTO rule_criterion VALUES ('28', 'subsite-role-fallback-home', 2, 'path', 'path', 'subsite-default-page', 0);
INSERT INTO rule_criterion VALUES ('29', 'subsite2-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2);
INSERT INTO rule_criterion VALUES ('3', 'j1', 2, 'mediatype', 'mediatype', NULL, 1);
INSERT INTO rule_criterion VALUES ('30', 'subsite2-role-fallback-home', 1, 'role', 'role', NULL, 2);
INSERT INTO rule_criterion VALUES ('31', 'subsite2-role-fallback-home', 2, 'path', 'path', 'subsite2-default-page', 0);
INSERT INTO rule_criterion VALUES ('4', 'j1', 3, 'language', 'language', NULL, 1);
INSERT INTO rule_criterion VALUES ('5', 'j1', 4, 'country', 'country', NULL, 1);
INSERT INTO rule_criterion VALUES ('6', 'role-fallback', 0, 'role', 'role', NULL, 2);
INSERT INTO rule_criterion VALUES ('7', 'role-fallback', 1, 'path.session', 'page', 'default-page', 0);
INSERT INTO rule_criterion VALUES ('8', 'path', 0, 'path', 'path', '/', 0);
INSERT INTO rule_criterion VALUES ('9', 'role-group', 0, 'role', 'role', NULL, 2);


--
-- Data for Name: runtime_option; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: runtime_value; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: secured_portlet; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: security_attribute; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO security_attribute VALUES (27, 16, 'user.business-info.online.email', 'yrmail@yrmail.yrmail');
INSERT INTO security_attribute VALUES (28, 16, 'user.business-info.postal.city', '');
INSERT INTO security_attribute VALUES (29, 16, 'user.business-info.postal.postalcode', '');
INSERT INTO security_attribute VALUES (30, 16, 'user.business-info.postal.street', '');
INSERT INTO security_attribute VALUES (31, 16, 'user.custom.ingrid.user.age.group', '0');
INSERT INTO security_attribute VALUES (32, 16, 'user.custom.ingrid.user.attention.from', '');
INSERT INTO security_attribute VALUES (33, 16, 'user.custom.ingrid.user.interest', '0');
INSERT INTO security_attribute VALUES (34, 16, 'user.custom.ingrid.user.profession', '0');
INSERT INTO security_attribute VALUES (35, 16, 'user.custom.ingrid.user.subscribe.newsletter', '');
INSERT INTO security_attribute VALUES (36, 16, 'user.name.family', 'Administrator');
INSERT INTO security_attribute VALUES (37, 16, 'user.name.given', 'System');
INSERT INTO security_attribute VALUES (38, 16, 'user.name.prefix', '0');


--
-- Data for Name: security_credential; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO security_credential VALUES (3, 16, 'liiHgKcA1sEBisdWUN9fLEc2gBo=', 0, 1, 0, 0, 1, 1, 0, 0, '2014-12-10 14:50:41', '2015-01-21 11:16:21', '2015-01-21 11:06:08', '2015-01-21 11:16:21', NULL);


--
-- Data for Name: security_domain; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO security_domain VALUES (1, '[default]', 0, 1, NULL);
INSERT INTO security_domain VALUES (2, '[system]', 0, 1, NULL);


--
-- Data for Name: security_permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO security_permission VALUES (1, 'folder', '/', 'view');
INSERT INTO security_permission VALUES (11, 'folder', '/__subsite-root', 'view');
INSERT INTO security_permission VALUES (13, 'folder', '/__subsite-root/_role/subsite', 'view, edit');
INSERT INTO security_permission VALUES (14, 'folder', '/__subsite-root/_role/subsite/-', 'view, edit');
INSERT INTO security_permission VALUES (15, 'folder', '/__subsite-root/_role/subsite2', 'view, edit');
INSERT INTO security_permission VALUES (16, 'folder', '/__subsite-root/_role/subsite2/-', 'view, edit');
INSERT INTO security_permission VALUES (12, 'folder', '/__subsite-root/-', 'view');
INSERT INTO security_permission VALUES (9, 'folder', '/_user/user', 'view, edit');
INSERT INTO security_permission VALUES (10, 'folder', '/_user/user/-', 'view, edit');
INSERT INTO security_permission VALUES (2, 'folder', '/*', 'view');
INSERT INTO security_permission VALUES (3, 'folder', '/anotherdir/-', 'view');
INSERT INTO security_permission VALUES (4, 'folder', '/non-java/-', 'view');
INSERT INTO security_permission VALUES (5, 'folder', '/Public', 'view, edit');
INSERT INTO security_permission VALUES (6, 'folder', '/Public/-', 'view, edit');
INSERT INTO security_permission VALUES (7, 'folder', '/third-party/-', 'view');
INSERT INTO security_permission VALUES (8, 'folder', '/top-links/-', 'view');
INSERT INTO security_permission VALUES (18, 'folder', '<<ALL FILES>>', 'view');
INSERT INTO security_permission VALUES (17, 'folder', '<<ALL FILES>>', 'view, edit');
INSERT INTO security_permission VALUES (19, 'page', '/default-page.psml', 'view');
INSERT INTO security_permission VALUES (20, 'page', '/rss.psml', 'view');
INSERT INTO security_permission VALUES (41, 'portlet', 'ingrid-portal-apps::*', 'view, edit');
INSERT INTO security_permission VALUES (22, 'portlet', 'ingrid-portal-mdek::*', 'view, edit');
INSERT INTO security_permission VALUES (23, 'portlet', 'j2-admin::*', 'view, edit');
INSERT INTO security_permission VALUES (24, 'portlet', 'jetspeed-layouts::*', 'view, edit');


--
-- Data for Name: security_principal; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO security_principal VALUES (1, 'group', 'accounting', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1);
INSERT INTO security_principal VALUES (2, 'group', 'engineering', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1);
INSERT INTO security_principal VALUES (3, 'group', 'marketing', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1);
INSERT INTO security_principal VALUES (4, 'role', 'admin', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1);
INSERT INTO security_principal VALUES (5, 'role', 'admin-partner', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (6, 'role', 'admin-portal', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (7, 'role', 'admin-provider', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (8, 'role', 'guest', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (9, 'role', 'manager', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (10, 'role', 'mdek', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (11, 'role', 'subsite', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (12, 'role', 'subsite2', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (13, 'role', 'user', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1);
INSERT INTO security_principal VALUES (16, 'user', 'admin', 1, 1, 0, 1, '2014-12-10 14:50:41', '2015-01-21 10:50:26', 1);
INSERT INTO security_principal VALUES (20, 'user', 'guest', 1, 1, 0, 1, '2014-12-10 14:50:41', '2014-12-10 14:50:44', 1);


--
-- Data for Name: security_principal_assoc; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO security_principal_assoc VALUES ('isMemberOf', 16, 4);
INSERT INTO security_principal_assoc VALUES ('isMemberOf', 16, 9);
INSERT INTO security_principal_assoc VALUES ('isMemberOf', 16, 13);
INSERT INTO security_principal_assoc VALUES ('isMemberOf', 20, 8);


--
-- Data for Name: security_role; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: security_role_reference; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: sso_site; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: user_activity; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: user_attribute; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: user_attribute_ref; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: user_statistics; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: capability_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY capability
    ADD CONSTRAINT capability_pkey PRIMARY KEY (capability_id);


--
-- Name: client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY client
    ADD CONSTRAINT client_pkey PRIMARY KEY (client_id);


--
-- Name: client_to_capability_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY client_to_capability
    ADD CONSTRAINT client_to_capability_pkey PRIMARY KEY (capability_id, client_id);


--
-- Name: client_to_mimetype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY client_to_mimetype
    ADD CONSTRAINT client_to_mimetype_pkey PRIMARY KEY (client_id, mimetype_id);


--
-- Name: clubs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY clubs
    ADD CONSTRAINT clubs_pkey PRIMARY KEY (name);


--
-- Name: custom_portlet_mode_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY custom_portlet_mode
    ADD CONSTRAINT custom_portlet_mode_pkey PRIMARY KEY (id);


--
-- Name: custom_window_state_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY custom_window_state
    ADD CONSTRAINT custom_window_state_pkey PRIMARY KEY (id);


--
-- Name: event_alias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY event_alias
    ADD CONSTRAINT event_alias_pkey PRIMARY KEY (id);


--
-- Name: event_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY event_definition
    ADD CONSTRAINT event_definition_pkey PRIMARY KEY (id);


--
-- Name: filter_lifecycle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY filter_lifecycle
    ADD CONSTRAINT filter_lifecycle_pkey PRIMARY KEY (id);


--
-- Name: filter_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY filter_mapping
    ADD CONSTRAINT filter_mapping_pkey PRIMARY KEY (id);


--
-- Name: filtered_portlet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY filtered_portlet
    ADD CONSTRAINT filtered_portlet_pkey PRIMARY KEY (id);


--
-- Name: folder_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_constraint
    ADD CONSTRAINT folder_constraint_pkey PRIMARY KEY (constraint_id);


--
-- Name: folder_constraints_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_constraints_ref
    ADD CONSTRAINT folder_constraints_ref_pkey PRIMARY KEY (constraints_ref_id);


--
-- Name: folder_menu_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_menu_metadata
    ADD CONSTRAINT folder_menu_metadata_pkey PRIMARY KEY (metadata_id);


--
-- Name: folder_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_menu
    ADD CONSTRAINT folder_menu_pkey PRIMARY KEY (menu_id);


--
-- Name: folder_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_metadata
    ADD CONSTRAINT folder_metadata_pkey PRIMARY KEY (metadata_id);


--
-- Name: folder_order_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_order
    ADD CONSTRAINT folder_order_pkey PRIMARY KEY (order_id);


--
-- Name: folder_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder
    ADD CONSTRAINT folder_pkey PRIMARY KEY (folder_id);


--
-- Name: fragment_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_constraint
    ADD CONSTRAINT fragment_constraint_pkey PRIMARY KEY (constraint_id);


--
-- Name: fragment_constraints_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_constraints_ref
    ADD CONSTRAINT fragment_constraints_ref_pkey PRIMARY KEY (constraints_ref_id);


--
-- Name: fragment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment
    ADD CONSTRAINT fragment_pkey PRIMARY KEY (fragment_id);


--
-- Name: fragment_pref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_pref
    ADD CONSTRAINT fragment_pref_pkey PRIMARY KEY (pref_id);


--
-- Name: fragment_pref_value_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_pref_value
    ADD CONSTRAINT fragment_pref_value_pkey PRIMARY KEY (pref_value_id);


--
-- Name: fragment_prop_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_prop
    ADD CONSTRAINT fragment_prop_pkey PRIMARY KEY (prop_id);


--
-- Name: ingrid_anniversary_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_anniversary
    ADD CONSTRAINT ingrid_anniversary_pkey PRIMARY KEY (id);


--
-- Name: ingrid_chron_eventtypes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_chron_eventtypes
    ADD CONSTRAINT ingrid_chron_eventtypes_pkey PRIMARY KEY (id);


--
-- Name: ingrid_cms_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_cms_item
    ADD CONSTRAINT ingrid_cms_item_pkey PRIMARY KEY (id);


--
-- Name: ingrid_cms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_cms
    ADD CONSTRAINT ingrid_cms_pkey PRIMARY KEY (id);


--
-- Name: ingrid_env_topic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_env_topic
    ADD CONSTRAINT ingrid_env_topic_pkey PRIMARY KEY (id);


--
-- Name: ingrid_lookup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_lookup
    ADD CONSTRAINT ingrid_lookup_pkey PRIMARY KEY (id);


--
-- Name: ingrid_measures_rubric_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_measures_rubric
    ADD CONSTRAINT ingrid_measures_rubric_pkey PRIMARY KEY (id);


--
-- Name: ingrid_newsletter_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_newsletter_data
    ADD CONSTRAINT ingrid_newsletter_data_pkey PRIMARY KEY (id);


--
-- Name: ingrid_partner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_partner
    ADD CONSTRAINT ingrid_partner_pkey PRIMARY KEY (id);


--
-- Name: ingrid_principal_pref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_principal_pref
    ADD CONSTRAINT ingrid_principal_pref_pkey PRIMARY KEY (id);


--
-- Name: ingrid_provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_provider
    ADD CONSTRAINT ingrid_provider_pkey PRIMARY KEY (id);


--
-- Name: ingrid_rss_source_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_rss_source
    ADD CONSTRAINT ingrid_rss_source_pkey PRIMARY KEY (id);


--
-- Name: ingrid_rss_store_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_rss_store
    ADD CONSTRAINT ingrid_rss_store_pkey PRIMARY KEY (link);


--
-- Name: ingrid_service_rubric_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_service_rubric
    ADD CONSTRAINT ingrid_service_rubric_pkey PRIMARY KEY (id);


--
-- Name: ingrid_tiny_url_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ingrid_tiny_url
    ADD CONSTRAINT ingrid_tiny_url_pkey PRIMARY KEY (id);


--
-- Name: jetspeed_service_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY jetspeed_service
    ADD CONSTRAINT jetspeed_service_pkey PRIMARY KEY (id);


--
-- Name: language_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY language
    ADD CONSTRAINT language_pkey PRIMARY KEY (id);


--
-- Name: link_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_constraint
    ADD CONSTRAINT link_constraint_pkey PRIMARY KEY (constraint_id);


--
-- Name: link_constraints_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_constraints_ref
    ADD CONSTRAINT link_constraints_ref_pkey PRIMARY KEY (constraints_ref_id);


--
-- Name: link_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_metadata
    ADD CONSTRAINT link_metadata_pkey PRIMARY KEY (metadata_id);


--
-- Name: link_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link
    ADD CONSTRAINT link_pkey PRIMARY KEY (link_id);


--
-- Name: locale_encoding_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY locale_encoding_mapping
    ADD CONSTRAINT locale_encoding_mapping_pkey PRIMARY KEY (id);


--
-- Name: localized_description_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY localized_description
    ADD CONSTRAINT localized_description_pkey PRIMARY KEY (id);


--
-- Name: localized_display_name_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY localized_display_name
    ADD CONSTRAINT localized_display_name_pkey PRIMARY KEY (id);


--
-- Name: media_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY media_type
    ADD CONSTRAINT media_type_pkey PRIMARY KEY (mediatype_id);


--
-- Name: mediatype_to_capability_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mediatype_to_capability
    ADD CONSTRAINT mediatype_to_capability_pkey PRIMARY KEY (capability_id, mediatype_id);


--
-- Name: mediatype_to_mimetype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mediatype_to_mimetype
    ADD CONSTRAINT mediatype_to_mimetype_pkey PRIMARY KEY (mediatype_id, mimetype_id);


--
-- Name: mimetype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mimetype
    ADD CONSTRAINT mimetype_pkey PRIMARY KEY (mimetype_id);


--
-- Name: named_parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY named_parameter
    ADD CONSTRAINT named_parameter_pkey PRIMARY KEY (id);


--
-- Name: ojb_dlist_entries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_dlist_entries
    ADD CONSTRAINT ojb_dlist_entries_pkey PRIMARY KEY (id);


--
-- Name: ojb_dlist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_dlist
    ADD CONSTRAINT ojb_dlist_pkey PRIMARY KEY (id);


--
-- Name: ojb_dmap_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_dmap
    ADD CONSTRAINT ojb_dmap_pkey PRIMARY KEY (id);


--
-- Name: ojb_dset_entries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_dset_entries
    ADD CONSTRAINT ojb_dset_entries_pkey PRIMARY KEY (id);


--
-- Name: ojb_dset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_dset
    ADD CONSTRAINT ojb_dset_pkey PRIMARY KEY (id);


--
-- Name: ojb_hl_seq_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_hl_seq
    ADD CONSTRAINT ojb_hl_seq_pkey PRIMARY KEY (fieldname, tablename);


--
-- Name: ojb_lockentry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_lockentry
    ADD CONSTRAINT ojb_lockentry_pkey PRIMARY KEY (oid_, tx_id);


--
-- Name: ojb_nrm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ojb_nrm
    ADD CONSTRAINT ojb_nrm_pkey PRIMARY KEY (name);


--
-- Name: pa_metadata_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pa_metadata_fields
    ADD CONSTRAINT pa_metadata_fields_pkey PRIMARY KEY (id);


--
-- Name: pa_security_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pa_security_constraint
    ADD CONSTRAINT pa_security_constraint_pkey PRIMARY KEY (id);


--
-- Name: page_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_constraint
    ADD CONSTRAINT page_constraint_pkey PRIMARY KEY (constraint_id);


--
-- Name: page_constraints_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_constraints_ref
    ADD CONSTRAINT page_constraints_ref_pkey PRIMARY KEY (constraints_ref_id);


--
-- Name: page_menu_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_menu_metadata
    ADD CONSTRAINT page_menu_metadata_pkey PRIMARY KEY (metadata_id);


--
-- Name: page_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_menu
    ADD CONSTRAINT page_menu_pkey PRIMARY KEY (menu_id);


--
-- Name: page_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_metadata
    ADD CONSTRAINT page_metadata_pkey PRIMARY KEY (metadata_id);


--
-- Name: page_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page
    ADD CONSTRAINT page_pkey PRIMARY KEY (page_id);


--
-- Name: page_sec_constraint_def_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraint_def
    ADD CONSTRAINT page_sec_constraint_def_pkey PRIMARY KEY (constraint_def_id);


--
-- Name: page_sec_constraints_def_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraints_def
    ADD CONSTRAINT page_sec_constraints_def_pkey PRIMARY KEY (constraints_def_id);


--
-- Name: page_sec_constraints_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraints_ref
    ADD CONSTRAINT page_sec_constraints_ref_pkey PRIMARY KEY (constraints_ref_id);


--
-- Name: page_security_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_security
    ADD CONSTRAINT page_security_pkey PRIMARY KEY (page_security_id);


--
-- Name: parameter_alias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter_alias
    ADD CONSTRAINT parameter_alias_pkey PRIMARY KEY (id);


--
-- Name: parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter
    ADD CONSTRAINT parameter_pkey PRIMARY KEY (parameter_id);


--
-- Name: pd_metadata_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pd_metadata_fields
    ADD CONSTRAINT pd_metadata_fields_pkey PRIMARY KEY (id);


--
-- Name: portlet_application_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_application
    ADD CONSTRAINT portlet_application_pkey PRIMARY KEY (application_id);


--
-- Name: portlet_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_definition
    ADD CONSTRAINT portlet_definition_pkey PRIMARY KEY (id);


--
-- Name: portlet_filter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_filter
    ADD CONSTRAINT portlet_filter_pkey PRIMARY KEY (id);


--
-- Name: portlet_listener_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_listener
    ADD CONSTRAINT portlet_listener_pkey PRIMARY KEY (id);


--
-- Name: portlet_preference_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_preference
    ADD CONSTRAINT portlet_preference_pkey PRIMARY KEY (id);


--
-- Name: portlet_preference_value_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_preference_value
    ADD CONSTRAINT portlet_preference_value_pkey PRIMARY KEY (id, idx, pref_id);


--
-- Name: portlet_supports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_supports
    ADD CONSTRAINT portlet_supports_pkey PRIMARY KEY (supports_id);


--
-- Name: principal_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY principal_permission
    ADD CONSTRAINT principal_permission_pkey PRIMARY KEY (permission_id, principal_id);


--
-- Name: principal_rule_assoc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY principal_rule_assoc
    ADD CONSTRAINT principal_rule_assoc_pkey PRIMARY KEY (locator_name, principal_name);


--
-- Name: processing_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY processing_event
    ADD CONSTRAINT processing_event_pkey PRIMARY KEY (id);


--
-- Name: profile_page_assoc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY profile_page_assoc
    ADD CONSTRAINT profile_page_assoc_pkey PRIMARY KEY (locator_hash, page_id);


--
-- Name: profiling_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY profiling_rule
    ADD CONSTRAINT profiling_rule_pkey PRIMARY KEY (rule_id);


--
-- Name: public_parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public_parameter
    ADD CONSTRAINT public_parameter_pkey PRIMARY KEY (id);


--
-- Name: publishing_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY publishing_event
    ADD CONSTRAINT publishing_event_pkey PRIMARY KEY (id);


--
-- Name: qrtz_blob_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (trigger_group, trigger_name);


--
-- Name: qrtz_calendars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_calendars
    ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (calendar_name);


--
-- Name: qrtz_cron_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (trigger_group, trigger_name);


--
-- Name: qrtz_fired_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_fired_triggers
    ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (entry_id);


--
-- Name: qrtz_job_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_job_details
    ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (job_group, job_name);


--
-- Name: qrtz_job_listeners_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_job_listeners
    ADD CONSTRAINT qrtz_job_listeners_pkey PRIMARY KEY (job_group, job_listener, job_name);


--
-- Name: qrtz_locks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_locks
    ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (lock_name);


--
-- Name: qrtz_paused_trigger_grps_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_paused_trigger_grps
    ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (trigger_group);


--
-- Name: qrtz_scheduler_state_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_scheduler_state
    ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (instance_name);


--
-- Name: qrtz_simple_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (trigger_group, trigger_name);


--
-- Name: qrtz_trigger_listeners_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_trigger_listeners
    ADD CONSTRAINT qrtz_trigger_listeners_pkey PRIMARY KEY (trigger_group, trigger_listener, trigger_name);


--
-- Name: qrtz_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (trigger_group, trigger_name);


--
-- Name: rule_criterion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rule_criterion
    ADD CONSTRAINT rule_criterion_pkey PRIMARY KEY (criterion_id);


--
-- Name: runtime_option_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY runtime_option
    ADD CONSTRAINT runtime_option_pkey PRIMARY KEY (id);


--
-- Name: runtime_value_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY runtime_value
    ADD CONSTRAINT runtime_value_pkey PRIMARY KEY (id);


--
-- Name: secured_portlet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY secured_portlet
    ADD CONSTRAINT secured_portlet_pkey PRIMARY KEY (id);


--
-- Name: security_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_attribute
    ADD CONSTRAINT security_attribute_pkey PRIMARY KEY (attr_id, attr_name, principal_id);


--
-- Name: security_credential_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_credential
    ADD CONSTRAINT security_credential_pkey PRIMARY KEY (credential_id);


--
-- Name: security_domain_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_domain
    ADD CONSTRAINT security_domain_pkey PRIMARY KEY (domain_id);


--
-- Name: security_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_permission
    ADD CONSTRAINT security_permission_pkey PRIMARY KEY (permission_id);


--
-- Name: security_principal_assoc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_principal_assoc
    ADD CONSTRAINT security_principal_assoc_pkey PRIMARY KEY (assoc_name, from_principal_id, to_principal_id);


--
-- Name: security_principal_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_principal
    ADD CONSTRAINT security_principal_pkey PRIMARY KEY (principal_id);


--
-- Name: security_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_role
    ADD CONSTRAINT security_role_pkey PRIMARY KEY (id);


--
-- Name: security_role_reference_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_role_reference
    ADD CONSTRAINT security_role_reference_pkey PRIMARY KEY (id);


--
-- Name: sso_site_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sso_site
    ADD CONSTRAINT sso_site_pkey PRIMARY KEY (site_id);


--
-- Name: uix_domain_name; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_domain
    ADD CONSTRAINT uix_domain_name UNIQUE (domain_name);


--
-- Name: uix_portlet_preference; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_preference
    ADD CONSTRAINT uix_portlet_preference UNIQUE (dtype, application_name, portlet_name, entity_id, user_name, name);


--
-- Name: uix_security_permission; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_permission
    ADD CONSTRAINT uix_security_permission UNIQUE (permission_type, name, actions);


--
-- Name: uix_security_principal; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_principal
    ADD CONSTRAINT uix_security_principal UNIQUE (principal_type, principal_name, domain_id);


--
-- Name: uix_site_name; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sso_site
    ADD CONSTRAINT uix_site_name UNIQUE (name);


--
-- Name: uix_site_url; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sso_site
    ADD CONSTRAINT uix_site_url UNIQUE (url);


--
-- Name: uk_application; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_application
    ADD CONSTRAINT uk_application UNIQUE (app_name);


--
-- Name: uk_supports; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_supports
    ADD CONSTRAINT uk_supports UNIQUE (portlet_id, mime_type);


--
-- Name: un_folder_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder
    ADD CONSTRAINT un_folder_1 UNIQUE (path);


--
-- Name: un_folder_constraints_ref_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_constraints_ref
    ADD CONSTRAINT un_folder_constraints_ref_1 UNIQUE (folder_id, name);


--
-- Name: un_folder_menu_metadata_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_menu_metadata
    ADD CONSTRAINT un_folder_menu_metadata_1 UNIQUE (menu_id, name, locale, value);


--
-- Name: un_folder_metadata_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_metadata
    ADD CONSTRAINT un_folder_metadata_1 UNIQUE (folder_id, name, locale, value);


--
-- Name: un_folder_order_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_order
    ADD CONSTRAINT un_folder_order_1 UNIQUE (folder_id, name);


--
-- Name: un_fragment_constraints_ref_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_constraints_ref
    ADD CONSTRAINT un_fragment_constraints_ref_1 UNIQUE (fragment_id, name);


--
-- Name: un_fragment_pref_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_pref
    ADD CONSTRAINT un_fragment_pref_1 UNIQUE (fragment_id, name);


--
-- Name: un_fragment_prop_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_prop
    ADD CONSTRAINT un_fragment_prop_1 UNIQUE (fragment_id, name, scope, scope_value);


--
-- Name: un_link_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link
    ADD CONSTRAINT un_link_1 UNIQUE (path);


--
-- Name: un_link_constraints_ref_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_constraints_ref
    ADD CONSTRAINT un_link_constraints_ref_1 UNIQUE (link_id, name);


--
-- Name: un_link_metadata_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_metadata
    ADD CONSTRAINT un_link_metadata_1 UNIQUE (link_id, name, locale, value);


--
-- Name: un_page_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page
    ADD CONSTRAINT un_page_1 UNIQUE (path);


--
-- Name: un_page_constraints_ref_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_constraints_ref
    ADD CONSTRAINT un_page_constraints_ref_1 UNIQUE (page_id, name);


--
-- Name: un_page_menu_metadata_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_menu_metadata
    ADD CONSTRAINT un_page_menu_metadata_1 UNIQUE (menu_id, name, locale, value);


--
-- Name: un_page_metadata_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_metadata
    ADD CONSTRAINT un_page_metadata_1 UNIQUE (page_id, name, locale, value);


--
-- Name: un_page_sec_constraints_def_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraints_def
    ADD CONSTRAINT un_page_sec_constraints_def_1 UNIQUE (page_security_id, name);


--
-- Name: un_page_sec_constraints_ref_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraints_ref
    ADD CONSTRAINT un_page_sec_constraints_ref_1 UNIQUE (page_security_id, name);


--
-- Name: un_page_security_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_security
    ADD CONSTRAINT un_page_security_1 UNIQUE (parent_id);


--
-- Name: un_page_security_2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_security
    ADD CONSTRAINT un_page_security_2 UNIQUE (path);


--
-- Name: un_qrtz_blob_triggers_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT un_qrtz_blob_triggers_1 UNIQUE (trigger_name, trigger_group);


--
-- Name: user_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_attribute
    ADD CONSTRAINT user_attribute_pkey PRIMARY KEY (id);


--
-- Name: user_attribute_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_attribute_ref
    ADD CONSTRAINT user_attribute_ref_pkey PRIMARY KEY (id);


--
-- Name: idx_qrtz_ft_job_group; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_job_group ON qrtz_fired_triggers USING btree (job_group);


--
-- Name: idx_qrtz_ft_job_name; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_job_name ON qrtz_fired_triggers USING btree (job_name);


--
-- Name: idx_qrtz_ft_job_req_recovery; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_job_req_recovery ON qrtz_fired_triggers USING btree (requests_recovery);


--
-- Name: idx_qrtz_ft_job_stateful; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_job_stateful ON qrtz_fired_triggers USING btree (is_stateful);


--
-- Name: idx_qrtz_ft_trig_group; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_trig_group ON qrtz_fired_triggers USING btree (trigger_group);


--
-- Name: idx_qrtz_ft_trig_inst_name; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers USING btree (instance_name);


--
-- Name: idx_qrtz_ft_trig_name; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_trig_name ON qrtz_fired_triggers USING btree (trigger_name);


--
-- Name: idx_qrtz_ft_trig_nm_gp; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_trig_nm_gp ON qrtz_fired_triggers USING btree (trigger_name, trigger_group);


--
-- Name: idx_qrtz_ft_trig_volatile; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_ft_trig_volatile ON qrtz_fired_triggers USING btree (is_volatile);


--
-- Name: idx_qrtz_j_req_recovery; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details USING btree (requests_recovery);


--
-- Name: idx_qrtz_t_next_fire_time; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_t_next_fire_time ON qrtz_triggers USING btree (next_fire_time);


--
-- Name: idx_qrtz_t_nft_st; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_t_nft_st ON qrtz_triggers USING btree (next_fire_time, trigger_state);


--
-- Name: idx_qrtz_t_state; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_t_state ON qrtz_triggers USING btree (trigger_state);


--
-- Name: idx_qrtz_t_volatile; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_qrtz_t_volatile ON qrtz_triggers USING btree (is_volatile);


--
-- Name: ix_email_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_email_1 ON ingrid_newsletter_data USING btree (email);


--
-- Name: ix_folder_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_1 ON folder USING btree (parent_id);


--
-- Name: ix_folder_constraint_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_constraint_1 ON folder_constraint USING btree (folder_id);


--
-- Name: ix_folder_constraints_ref_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_constraints_ref_1 ON folder_constraints_ref USING btree (folder_id);


--
-- Name: ix_folder_menu_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_menu_1 ON folder_menu USING btree (parent_id);


--
-- Name: ix_folder_menu_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_menu_2 ON folder_menu USING btree (folder_id);


--
-- Name: ix_folder_menu_metadata_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_menu_metadata_1 ON folder_menu_metadata USING btree (menu_id);


--
-- Name: ix_folder_metadata_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_metadata_1 ON folder_metadata USING btree (folder_id);


--
-- Name: ix_folder_order_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_folder_order_1 ON folder_order USING btree (folder_id);


--
-- Name: ix_fragment_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_1 ON fragment USING btree (parent_id);


--
-- Name: ix_fragment_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_2 ON fragment USING btree (fragment_string_refid);


--
-- Name: ix_fragment_3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_3 ON fragment USING btree (fragment_string_id);


--
-- Name: ix_fragment_constraint_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_constraint_1 ON fragment_constraint USING btree (fragment_id);


--
-- Name: ix_fragment_constraints_ref_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_constraints_ref_1 ON fragment_constraints_ref USING btree (fragment_id);


--
-- Name: ix_fragment_pref_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_pref_1 ON fragment_pref USING btree (fragment_id);


--
-- Name: ix_fragment_pref_value_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_pref_value_1 ON fragment_pref_value USING btree (pref_id);


--
-- Name: ix_fragment_prop_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_fragment_prop_1 ON fragment_prop USING btree (fragment_id);


--
-- Name: ix_from_principal_assoc; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_from_principal_assoc ON security_principal_assoc USING btree (from_principal_id);


--
-- Name: ix_ingrid_anniversary_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_anniversary_1 ON ingrid_anniversary USING btree (fetched_for);


--
-- Name: ix_ingrid_anniversary_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_anniversary_2 ON ingrid_anniversary USING btree (language);


--
-- Name: ix_ingrid_partner_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_partner_1 ON ingrid_partner USING btree (sortkey);


--
-- Name: ix_ingrid_provider_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_provider_1 ON ingrid_provider USING btree (sortkey_partner);


--
-- Name: ix_ingrid_provider_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_provider_2 ON ingrid_provider USING btree (sortkey);


--
-- Name: ix_ingrid_provider_3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_provider_3 ON ingrid_provider USING btree (ident);


--
-- Name: ix_ingrid_rss_store_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_rss_store_1 ON ingrid_rss_store USING btree (published_date);


--
-- Name: ix_ingrid_rss_store_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ingrid_rss_store_2 ON ingrid_rss_store USING btree (language);


--
-- Name: ix_link_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_link_1 ON link USING btree (parent_id);


--
-- Name: ix_link_constraint_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_link_constraint_1 ON link_constraint USING btree (link_id);


--
-- Name: ix_link_constraints_ref_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_link_constraints_ref_1 ON link_constraints_ref USING btree (link_id);


--
-- Name: ix_link_metadata_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_link_metadata_1 ON link_metadata USING btree (link_id);


--
-- Name: ix_name_lookup; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_name_lookup ON security_attribute USING btree (attr_name);


--
-- Name: ix_page_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_1 ON page USING btree (parent_id);


--
-- Name: ix_page_constraint_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_constraint_1 ON page_constraint USING btree (page_id);


--
-- Name: ix_page_constraints_ref_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_constraints_ref_1 ON page_constraints_ref USING btree (page_id);


--
-- Name: ix_page_menu_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_menu_1 ON page_menu USING btree (parent_id);


--
-- Name: ix_page_menu_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_menu_2 ON page_menu USING btree (page_id);


--
-- Name: ix_page_menu_metadata_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_menu_metadata_1 ON page_menu_metadata USING btree (menu_id);


--
-- Name: ix_page_metadata_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_metadata_1 ON page_metadata USING btree (page_id);


--
-- Name: ix_page_sec_constraint_def_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_sec_constraint_def_1 ON page_sec_constraint_def USING btree (constraints_def_id);


--
-- Name: ix_page_sec_constraints_def_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_sec_constraints_def_1 ON page_sec_constraints_def USING btree (page_security_id);


--
-- Name: ix_page_sec_constraints_ref_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_page_sec_constraints_ref_1 ON page_sec_constraints_ref USING btree (page_security_id);


--
-- Name: ix_portlet_preference; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_portlet_preference ON portlet_preference_value USING btree (pref_id);


--
-- Name: ix_principal_attr; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_principal_attr ON security_attribute USING btree (principal_id);


--
-- Name: ix_principal_permission_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_principal_permission_1 ON principal_permission USING btree (permission_id);


--
-- Name: ix_principal_permission_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_principal_permission_2 ON principal_permission USING btree (principal_id);


--
-- Name: ix_rule_criterion_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_rule_criterion_1 ON rule_criterion USING btree (rule_id);


--
-- Name: ix_rule_criterion_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_rule_criterion_2 ON rule_criterion USING btree (rule_id, fallback_order);


--
-- Name: ix_security_credential_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_security_credential_1 ON security_credential USING btree (principal_id);


--
-- Name: ix_security_domain_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_security_domain_1 ON security_principal USING btree (domain_id);


--
-- Name: ix_security_domain_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_security_domain_2 ON sso_site USING btree (domain_id);


--
-- Name: ix_to_principal_assoc; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_to_principal_assoc ON security_principal_assoc USING btree (to_principal_id);


--
-- Name: ix_to_principal_assoc_lookup; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_to_principal_assoc_lookup ON security_principal_assoc USING btree (assoc_name, to_principal_id);


--
-- Name: un_folder_menu_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX un_folder_menu_1 ON folder_menu USING btree (folder_id, name);


--
-- Name: un_fragment_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX un_fragment_1 ON fragment USING btree (page_id);


--
-- Name: un_page_menu_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX un_page_menu_1 ON page_menu USING btree (page_id, name);


--
-- Name: fk_folder_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder
    ADD CONSTRAINT fk_folder_1 FOREIGN KEY (parent_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_folder_constraint_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_constraint
    ADD CONSTRAINT fk_folder_constraint_1 FOREIGN KEY (folder_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_folder_constraints_ref_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_constraints_ref
    ADD CONSTRAINT fk_folder_constraints_ref_1 FOREIGN KEY (folder_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_folder_menu_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_menu
    ADD CONSTRAINT fk_folder_menu_1 FOREIGN KEY (parent_id) REFERENCES folder_menu(menu_id) ON DELETE CASCADE;


--
-- Name: fk_folder_menu_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_menu
    ADD CONSTRAINT fk_folder_menu_2 FOREIGN KEY (folder_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_folder_menu_metadata_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_menu_metadata
    ADD CONSTRAINT fk_folder_menu_metadata_1 FOREIGN KEY (menu_id) REFERENCES folder_menu(menu_id) ON DELETE CASCADE;


--
-- Name: fk_folder_metadata_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_metadata
    ADD CONSTRAINT fk_folder_metadata_1 FOREIGN KEY (folder_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_folder_order_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY folder_order
    ADD CONSTRAINT fk_folder_order_1 FOREIGN KEY (folder_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment
    ADD CONSTRAINT fk_fragment_1 FOREIGN KEY (parent_id) REFERENCES fragment(fragment_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment
    ADD CONSTRAINT fk_fragment_2 FOREIGN KEY (page_id) REFERENCES page(page_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_constraint_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_constraint
    ADD CONSTRAINT fk_fragment_constraint_1 FOREIGN KEY (fragment_id) REFERENCES fragment(fragment_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_constraints_ref_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_constraints_ref
    ADD CONSTRAINT fk_fragment_constraints_ref_1 FOREIGN KEY (fragment_id) REFERENCES fragment(fragment_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_pref_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_pref
    ADD CONSTRAINT fk_fragment_pref_1 FOREIGN KEY (fragment_id) REFERENCES fragment(fragment_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_pref_value_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_pref_value
    ADD CONSTRAINT fk_fragment_pref_value_1 FOREIGN KEY (pref_id) REFERENCES fragment_pref(pref_id) ON DELETE CASCADE;


--
-- Name: fk_fragment_prop_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fragment_prop
    ADD CONSTRAINT fk_fragment_prop_1 FOREIGN KEY (fragment_id) REFERENCES fragment(fragment_id) ON DELETE CASCADE;


--
-- Name: fk_from_principal_assoc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_principal_assoc
    ADD CONSTRAINT fk_from_principal_assoc FOREIGN KEY (from_principal_id) REFERENCES security_principal(principal_id) ON DELETE CASCADE;


--
-- Name: fk_link_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link
    ADD CONSTRAINT fk_link_1 FOREIGN KEY (parent_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_link_constraint_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_constraint
    ADD CONSTRAINT fk_link_constraint_1 FOREIGN KEY (link_id) REFERENCES link(link_id) ON DELETE CASCADE;


--
-- Name: fk_link_constraints_ref_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_constraints_ref
    ADD CONSTRAINT fk_link_constraints_ref_1 FOREIGN KEY (link_id) REFERENCES link(link_id) ON DELETE CASCADE;


--
-- Name: fk_link_metadata_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY link_metadata
    ADD CONSTRAINT fk_link_metadata_1 FOREIGN KEY (link_id) REFERENCES link(link_id) ON DELETE CASCADE;


--
-- Name: fk_page_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page
    ADD CONSTRAINT fk_page_1 FOREIGN KEY (parent_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_page_constraint_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_constraint
    ADD CONSTRAINT fk_page_constraint_1 FOREIGN KEY (page_id) REFERENCES page(page_id) ON DELETE CASCADE;


--
-- Name: fk_page_constraints_ref_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_constraints_ref
    ADD CONSTRAINT fk_page_constraints_ref_1 FOREIGN KEY (page_id) REFERENCES page(page_id) ON DELETE CASCADE;


--
-- Name: fk_page_menu_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_menu
    ADD CONSTRAINT fk_page_menu_1 FOREIGN KEY (parent_id) REFERENCES page_menu(menu_id) ON DELETE CASCADE;


--
-- Name: fk_page_menu_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_menu
    ADD CONSTRAINT fk_page_menu_2 FOREIGN KEY (page_id) REFERENCES page(page_id) ON DELETE CASCADE;


--
-- Name: fk_page_menu_metadata_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_menu_metadata
    ADD CONSTRAINT fk_page_menu_metadata_1 FOREIGN KEY (menu_id) REFERENCES page_menu(menu_id) ON DELETE CASCADE;


--
-- Name: fk_page_metadata_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_metadata
    ADD CONSTRAINT fk_page_metadata_1 FOREIGN KEY (page_id) REFERENCES page(page_id) ON DELETE CASCADE;


--
-- Name: fk_page_sec_constraint_def_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraint_def
    ADD CONSTRAINT fk_page_sec_constraint_def_1 FOREIGN KEY (constraints_def_id) REFERENCES page_sec_constraints_def(constraints_def_id) ON DELETE CASCADE;


--
-- Name: fk_page_sec_constraints_def_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraints_def
    ADD CONSTRAINT fk_page_sec_constraints_def_1 FOREIGN KEY (page_security_id) REFERENCES page_security(page_security_id) ON DELETE CASCADE;


--
-- Name: fk_page_sec_constraints_ref_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_sec_constraints_ref
    ADD CONSTRAINT fk_page_sec_constraints_ref_1 FOREIGN KEY (page_security_id) REFERENCES page_security(page_security_id) ON DELETE CASCADE;


--
-- Name: fk_page_security_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY page_security
    ADD CONSTRAINT fk_page_security_1 FOREIGN KEY (parent_id) REFERENCES folder(folder_id) ON DELETE CASCADE;


--
-- Name: fk_portlet_preference; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY portlet_preference_value
    ADD CONSTRAINT fk_portlet_preference FOREIGN KEY (pref_id) REFERENCES portlet_preference(id) ON DELETE CASCADE;


--
-- Name: fk_principal_attr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_attribute
    ADD CONSTRAINT fk_principal_attr FOREIGN KEY (principal_id) REFERENCES security_principal(principal_id) ON DELETE CASCADE;


--
-- Name: fk_principal_permission_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY principal_permission
    ADD CONSTRAINT fk_principal_permission_1 FOREIGN KEY (permission_id) REFERENCES security_permission(permission_id) ON DELETE CASCADE;


--
-- Name: fk_principal_permission_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY principal_permission
    ADD CONSTRAINT fk_principal_permission_2 FOREIGN KEY (principal_id) REFERENCES security_principal(principal_id) ON DELETE CASCADE;


--
-- Name: fk_qrtz_blob_triggers_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT fk_qrtz_blob_triggers_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers(trigger_name, trigger_group);


--
-- Name: fk_qrtz_cron_triggers_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT fk_qrtz_cron_triggers_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers(trigger_name, trigger_group);


--
-- Name: fk_qrtz_job_listeners_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_job_listeners
    ADD CONSTRAINT fk_qrtz_job_listeners_1 FOREIGN KEY (job_name, job_group) REFERENCES qrtz_job_details(job_name, job_group);


--
-- Name: fk_qrtz_simple_triggers_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT fk_qrtz_simple_triggers_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers(trigger_name, trigger_group);


--
-- Name: fk_qrtz_trigger_listeners_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_trigger_listeners
    ADD CONSTRAINT fk_qrtz_trigger_listeners_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers(trigger_name, trigger_group);


--
-- Name: fk_qrtz_triggers_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT fk_qrtz_triggers_1 FOREIGN KEY (job_name, job_group) REFERENCES qrtz_job_details(job_name, job_group);


--
-- Name: fk_rule_criterion_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rule_criterion
    ADD CONSTRAINT fk_rule_criterion_1 FOREIGN KEY (rule_id) REFERENCES profiling_rule(rule_id) ON DELETE CASCADE;


--
-- Name: fk_security_credential_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_credential
    ADD CONSTRAINT fk_security_credential_1 FOREIGN KEY (principal_id) REFERENCES security_principal(principal_id) ON DELETE CASCADE;


--
-- Name: fk_security_domain_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_principal
    ADD CONSTRAINT fk_security_domain_1 FOREIGN KEY (domain_id) REFERENCES security_domain(domain_id) ON DELETE CASCADE;


--
-- Name: fk_security_domain_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sso_site
    ADD CONSTRAINT fk_security_domain_2 FOREIGN KEY (domain_id) REFERENCES security_domain(domain_id) ON DELETE CASCADE;


--
-- Name: fk_to_principal_assoc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY security_principal_assoc
    ADD CONSTRAINT fk_to_principal_assoc FOREIGN KEY (to_principal_id) REFERENCES security_principal(principal_id) ON DELETE CASCADE;


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

