const { Client } = require('pg');

const pgclient = new Client({
    host: process.env.POSTGRES_HOST,
    port: process.env.POSTGRES_PORT,
    user: 'postgres',
    password: 'postgres',
    database: 'postgres'
});

pgclient.connect();

const table = 'CREATE TABLE public.tokens ( discord_id varchar(100) NOT NULL, "token" text NULL, expirydatetime timestamp NULL, "scope" varchar NULL, caltype varchar NULL, calid varchar NULL, cal_id varchar(255) NULL, cal_type varchar(255) NULL, code varchar(255) NULL, expires timestamp NULL, "refresh" varchar(255) NULL, CONSTRAINT tokens_pkey PRIMARY KEY (discord_id) '
pgclient.query(table, (err, res) => {
    if (err) {
        console.log(err);
        console.log(res);
        throw err;
    }
});