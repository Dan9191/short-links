CREATE TABLE link_master (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR NOT NULL
);

CREATE TABLE links (
                       id SERIAL PRIMARY KEY,
                       link_master_id UUID REFERENCES link_master (id) ON DELETE CASCADE,
                       orig_link VARCHAR NOT NULL,
                       short_link VARCHAR NOT NULL,
                       from_date TIMESTAMP DEFAULT NOW(),
                       to_date TIMESTAMP,
                       remainder BIGINT,
                       active BOOLEAN
);