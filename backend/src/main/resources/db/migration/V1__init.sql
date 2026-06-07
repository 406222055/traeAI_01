create table if not exists users (
  id varchar(64) primary key,
  username varchar(100) not null unique,
  password_hash varchar(255) not null,
  name varchar(100) not null,
  role varchar(50) not null,
  status varchar(50) not null,
  created_at bigint not null
);

create table if not exists vendors (
  id varchar(64) primary key,
  name varchar(200) not null,
  credit_code varchar(100) not null unique,
  service_type varchar(100) not null,
  contact_name varchar(100) not null,
  contact_phone varchar(50) not null,
  status varchar(50) not null,
  remark varchar(500),
  created_at bigint not null
);

create table if not exists projects (
  id varchar(64) primary key,
  code varchar(100) not null unique,
  name varchar(200) not null,
  region varchar(100) not null,
  manager_name varchar(100) not null,
  status varchar(50) not null,
  created_at bigint not null
);

create table if not exists admissions (
  id varchar(64) primary key,
  vendor_id varchar(64) not null,
  project_id varchar(64) not null,
  apply_date bigint not null,
  planned_entry_date bigint not null,
  scope_of_work varchar(500) not null,
  status varchar(50) not null,
  review_comment varchar(500),
  reviewed_by varchar(100),
  reviewed_at bigint,
  created_at bigint not null,
  constraint fk_admissions_vendor foreign key (vendor_id) references vendors(id),
  constraint fk_admissions_project foreign key (project_id) references projects(id)
);

create table if not exists compliance_items (
  id varchar(64) primary key,
  vendor_id varchar(64) not null,
  project_id varchar(64),
  type varchar(50) not null,
  name varchar(200) not null,
  issue_date bigint not null,
  expiry_date bigint not null,
  status varchar(50) not null,
  remark varchar(500),
  constraint fk_compliance_vendor foreign key (vendor_id) references vendors(id),
  constraint fk_compliance_project foreign key (project_id) references projects(id)
);

create index if not exists idx_admissions_created_at on admissions(created_at);
create index if not exists idx_compliance_expiry_date on compliance_items(expiry_date);
create index if not exists idx_vendors_created_at on vendors(created_at);
create index if not exists idx_projects_created_at on projects(created_at);
