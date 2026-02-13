CREATE TABLE users(
    user_id INTEGER PRIMARY KEY auto_increment,
    username VARCHAR(100) UNIQUE NOT NULL,
    password CHAR(68) NOT NULL
);

CREATE TABLE role (
    role_id INTEGER NOT NULL auto_increment,
    role_name VARCHAR(100) NOT NULL,
    constraint pk_role PRIMARY KEY (role_id)
);

CREATE TABLE user_roles (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE employee_details (
    employee_id INTEGER PRIMARY KEY auto_increment,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30),
    age INTEGER,
    gender CHAR(1),
    email VARCHAR(30),
    phone_number CHAR(10),
    department VARCHAR(30),
    user_id INTEGER NOT NULL
);

CREATE TABLE login_logout_times (
    entry_id INTEGER PRIMARY KEY auto_increment,
    employee_id INTEGER NOT NULL,
    date DATE,
    time TIME,
    entry_type ENUM('login', 'logout')
);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_userRoles
        FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_role_userRoles
        FOREIGN KEY (role_id) REFERENCES role(role_id);

ALTER TABLE employee_details
    ADD CONSTRAINT fk_employee_user
        FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE login_logout_times
    ADD CONSTRAINT fk_login_logout_employee
        FOREIGN KEY (employee_id) REFERENCES employee_details(employee_id);