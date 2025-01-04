CREATE TABLE "user" (
                        id VARCHAR PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR NOT NULL
);

CREATE TABLE links (
                       id SERIAL PRIMARY KEY,
                       user_id VARCHAR REFERENCES "user" (id) ON DELETE CASCADE,
                       orig_link VARCHAR NOT NULL,
                       short_link VARCHAR NOT NULL,
                       from_date TIMESTAMP DEFAULT NOW(),
                       to_date TIMESTAMP,
                       remainder BIGINT
);