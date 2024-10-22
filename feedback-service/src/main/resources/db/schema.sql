CREATE schema if not exists feedback;

CREATE TABLE if not exists feedback.t_favourite (
    id uuid NOT NULL,
	c_productid int NOT NULL,
	CONSTRAINT t_favourite_pk PRIMARY KEY (id)
    );

CREATE TABLE if not exists feedback.t_review (
	id uuid NOT NULL,
	c_productid int NOT NULL,
	c_rating int NOT NULL check (c_rating >= 1 AND c_rating <= 5),
	c_review varchar(1000) NULL,
	CONSTRAINT t_review_pk PRIMARY KEY (id)
);