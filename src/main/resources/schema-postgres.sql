DROP TABLE IF EXISTS auth;
DROP TABLE IF EXISTS consumer_eventsub;
DROP TABLE IF EXISTS consumer;
DROP TABLE IF EXISTS eventsub;
DROP TYPE IF EXISTS eventsub_type CASCADE;

CREATE TYPE eventsub_type as ENUM ('SUB', 'FOLLOW');
CREATE CAST (character varying as eventsub_type) WITH INOUT AS IMPLICIT;

CREATE TABLE auth (
    auth_id serial PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255),
    expires_in int NOT NULL,
    created_at int NOT NULL
);

CREATE TABLE consumer (
    consumer_id serial PRIMARY KEY,
    callback VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE eventsub (
    eventsub_id serial PRIMARY KEY,
    secret VARCHAR(255) NOT NULL,
    twitch_id VARCHAR(255) NOT NULL UNIQUE,
    broadcaster_user_id VARCHAR(255) NOT NULL,
    type eventsub_type NOT NULL
);

CREATE TABLE consumer_eventsub (
    consumer_id int REFERENCES consumer (consumer_id) ON UPDATE CASCADE,
    eventsub_id int REFERENCES eventsub (eventsub_id) ON UPDATE CASCADE,
    CONSTRAINT consumer_eventsub_pkey PRIMARY KEY (consumer_id, eventsub_id)
);
