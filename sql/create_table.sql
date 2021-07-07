-- Creation of product table
CREATE TABLE IF NOT EXISTS Call (
  id BIGINT NOT NULL,
  callee_number varchar(250) NOT NULL,
  caller_number varchar(250) NOT NULL,
  end_time TIMESTAMP,
  start_time TIMESTAMP,
  status varchar(250),
  type varchar(250) NOT NULL,
  PRIMARY KEY (id)
);