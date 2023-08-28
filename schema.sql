create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
create table company (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, arn varchar(255), arn_issue_date date, business_category varchar(255), business_description varchar(255), code varchar(255) not null, email varchar(255) not null, mobile varchar(255) not null, name varchar(255) not null, pan varchar(255) not null, sac_code int8, status varchar(255) not null, telephone varchar(255) not null, website varchar(255), primary key (id))
create table company_account (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address varchar(255) not null, branch varchar(255) not null, iban_number varchar(255) not null, ifsc_code varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_address (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, address_line_1 varchar(255) not null, address_line_2 varchar(255), city varchar(255) not null, country varchar(255) not null, post_code varchar(255) not null, state varchar(255) not null, town varchar(255), type varchar(255) not null, branch_name varchar(255), company_id uuid not null, primary key (id))
create table company_gst (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, location varchar(255) not null, number varchar(255) not null, company_id uuid not null, primary key (id))
create table company_image (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid, primary key (id))
create table company_policy (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, content bytea not null, content_type varchar(255) not null, name varchar(255) not null, size int8 not null, company_id uuid not null, primary key (id))
create table theme (id uuid not null, created_by varchar(255), created_time timestamp, last_updated_by varchar(255), last_updated_time timestamp, type varchar(255) not null, value varchar(255) not null, company_id uuid not null, primary key (id))
alter table company add constraint UK_8pha7u18i980jasccc4ekh7gg unique (code)
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5 unique (name)
alter table company add constraint UK_r698rlduouvb8wfbofi15u5td unique (pan)
alter table company_account add constraint UKjaa5h33xsaluppmy8o4gwc7fm unique (company_id, number)
alter table company_address add constraint UKdt7f4ont08bqwa4nlckwroc71 unique (company_id, type)
alter table company_gst add constraint UK42txeq3vnj1jpnovs8h1xxk81 unique (company_id, number)
alter table company_policy add constraint UKfsft6pnuhearjfe4qf3p1ermb unique (company_id, name)
alter table company_policy add constraint UK_4iwb4nwmifoddf9xpmdtqehsq unique (name)
alter table company_account add constraint FKpkhi6byf71hk9qfeuoy5ertg7 foreign key (company_id) references company
alter table company_address add constraint FKcct0cw50hai1l4eaunparc3a5 foreign key (company_id) references company
alter table company_gst add constraint FK1p0qfij71hbt46ob2njaqy5r0 foreign key (company_id) references company
alter table company_image add constraint FKo1sjbjm5jjkthg1mn9284tj5l foreign key (company_id) references company
alter table company_policy add constraint FK1aprc8xnqbycd36vce71ch83l foreign key (company_id) references company
alter table theme add constraint FK6oop0phr077nfmmmck0m4umn9 foreign key (company_id) references company
