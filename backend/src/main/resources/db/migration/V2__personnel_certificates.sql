create table if not exists personnel_certificates (
  id varchar(64) primary key,
  vendor_id varchar(64) not null,
  project_id varchar(64),
  personnel_name varchar(100) not null,
  id_card_no varchar(50),
  certificate_type varchar(50) not null,
  certificate_no varchar(100) not null,
  issue_date bigint not null,
  expiry_date bigint not null,
  status varchar(50) not null,
  remark varchar(500),
  created_at bigint not null,
  constraint fk_personnel_cert_vendor foreign key (vendor_id) references vendors(id),
  constraint fk_personnel_cert_project foreign key (project_id) references projects(id)
);

create index if not exists idx_personnel_cert_expiry_date on personnel_certificates(expiry_date);
create index if not exists idx_personnel_cert_vendor_id on personnel_certificates(vendor_id);
