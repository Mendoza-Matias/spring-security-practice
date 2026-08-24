INSERT
IGNORE INTO USERS (username, password, role)
VALUES ('admin', '$2a$12$ovTjKZiP/hvU6ByprblgS.JTbs.DIQG0TxViWhRcfZBXz8tp9yQ3C', 'ROLE_ADMIN');

INSERT
IGNORE INTO USERS (username, password, role)
VALUES ('employee', '$2a$12$IpEFk9vVE5.1KInME0Zb3uNC0WoO5uaiPlKz3krRVeiTfFqZ3l5m.', 'ROLE_EMPLOYEE');
