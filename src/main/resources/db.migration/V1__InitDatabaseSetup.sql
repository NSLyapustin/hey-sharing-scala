CREATE TABLE USERS
(
    ID            BIGSERIAL PRIMARY KEY,
    USER_NAME     VARCHAR NOT NULL UNIQUE,
    EMAIL         VARCHAR NOT NULL,
    HASH_PASSWORD VARCHAR NOT NULL,
    ROLE          VARCHAR NOT NULL DEFAULT 'Default',
);

CREATE TABLE PRODUCTS
(
    ID             BIGSERIAL PRIMARY KEY,
    NAME           VARCHAR NOT NULL UNIQUE,
    PRICE          NUMERIC NOT NULL,
    DURATION       VARCHAR NOT NULL,
    IMAGE          VARCHAR NOT NULL,
    COUNT_OF_VIEWS BIGINT  NOT NULL DEFAULT 0,
    DESCRIPTION    VARCHAR NOT NULL,
    CATEGORY       VARCHAR NOT NULL DEFAULT 'Other',
    STATUS         VARCHAR NOT NULL DEFAULT,
    ADDRESS        VARCHAR NOT NULL,
    USER_ID        BIGINT  NOT NULL REFERENCES USERS (ID) ON DELETE CASCADE
);

CREATE TABLE RENTS
(
    ID              BIGSERIAL PRIMARY KEY,
    FROM_DATE       VARCHAR NOT NULL,
    TO_DATE         VARCHAR NOT NULL,
    PERIOD_TYPE     VARCHAR NOT NULL,
    COUNT_OF_PERIOD BIGINT NOTNULL,
    PRODUCT_ID      BIGINT  NOT NULL REFERENCES PRODUCTS (ID) ON DELETE CASCADE
)