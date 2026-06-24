-- users
CREATE TABLE users (
	id varchar(255) PRIMARY KEY,
	username varchar(255) not null UNIQUE
);

INSERT INTO users (id, username) VALUES ('81150016-8501-4b97-9168-01113e21d8a5', 'User 001');
INSERT INTO users (id, username) VALUES ('d891323f-a3ad-4a95-b340-2e1c8aa8d1bd', 'User 002');

-- favorite_locations
CREATE TABLE favorite_locations (
	id varchar(255) PRIMARY KEY,
	user_id varchar(255) not null,
	given_name varchar(255) not null,
	given_location varchar(255) not null,
	longitude double precision not null,
	latitude double precision not null,
	elevation real not null,
	nearest_airport varchar(255) not null,
	nearest_airport_longitude double precision not null,
	nearest_airport_latitude double precision not null,
	nearest_airport_elevation real not null
);

INSERT INTO favorite_locations (
	id,
	user_id,
	given_name,
	given_location,
	longitude,
	latitude,
	elevation,
	nearest_airport,
	nearest_airport_longitude,
	nearest_airport_latitude,
	nearest_airport_elevation
) VALUES (
	'c5b38625-7eed-4705-858d-c685f18ed47d',
	'81150016-8501-4b97-9168-01113e21d8a5',
	'Linz',
	'Vienna, Austria',
	16.37208,
	48.20849,
	171.0,
	'LOWW',
	16.5697,
	48.110298,
	183.0
);
