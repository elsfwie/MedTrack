-- MedTrack Supabase schema — rebuilt to match the backend code 1:1.
-- Run this in the Supabase SQL editor (or psql) on a fresh project.

create table if not exists caregivers (
    id            uuid primary key references auth.users (id) on delete cascade,
    name          text not null,
    email         text not null unique,
    password_hash text,
    phone         text,
    created_at    timestamptz not null default now(),
    updated_at    timestamptz not null default now()
);

create table if not exists patients (
    id                uuid primary key default gen_random_uuid(),
    name              text not null,
    age               int  not null,
    emergency_contact text,
    device_id         text,
    created_by        uuid not null references caregivers (id),
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now()
);

create table if not exists shared_patients (
    caregiver_id uuid not null references caregivers (id) on delete cascade,
    patient_id   uuid not null references patients (id) on delete cascade,
    shared_by    uuid references caregivers (id),
    revoked_at   timestamptz,
    created_at   timestamptz not null default now(),
    primary key (caregiver_id, patient_id)
);

create table if not exists connections (
    id           uuid primary key default gen_random_uuid(),
    requester_id uuid not null references caregivers (id) on delete cascade,
    addressee_id uuid not null references caregivers (id) on delete cascade,
    status       text not null default 'pending'
                 check (status in ('pending', 'accepted')),
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now()
);

create table if not exists tokens (
    id         uuid primary key default gen_random_uuid(),
    token_hash text not null unique,
    patient_id uuid not null references patients (id) on delete cascade,
    created_by uuid not null references caregivers (id),
    used_at    timestamptz,
    revoked_at timestamptz,
    created_at timestamptz not null default now()
);

create table if not exists medications (
    id         uuid primary key default gen_random_uuid(),
    patient_id uuid not null references patients (id) on delete cascade,
    name       text not null,
    dosage     text,
    time       time,
    created_by uuid not null references caregivers (id),
    created_at timestamptz not null default now()
);

create table if not exists medications_logs (
    id           uuid primary key default gen_random_uuid(),
    medication_id uuid not null references medications (id) on delete cascade,
    status       text not null check (status in ('taken', 'skipped')),
    date         date not null default current_date,
    unique (medication_id, date)
);

create table if not exists notifications (
    id         uuid primary key default gen_random_uuid(),
    patient_id uuid not null references patients (id) on delete cascade,
    sent_by    uuid not null references caregivers (id),
    message    text not null,
    created_at timestamptz not null default now()
);
