-- F-02 Dompet: tabel, trigger, dan RLS.
-- owner_id ditambahkan sejak awal agar sinkronisasi auth tidak perlu migrasi ulang.

create table public.wallets (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) on delete cascade,
  name text not null check (length(trim(name)) > 0),
  type text not null check (type in ('cash', 'bank', 'ewallet', 'credit_card', 'savings')),
  currency char(3) not null default 'IDR',
  initial_balance bigint not null default 0,
  color text,
  icon text,
  is_archived boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  deleted_at timestamptz
);

create index wallets_owner_active_idx
  on public.wallets (owner_id, is_archived)
  where deleted_at is null;

create or replace function public.set_updated_at()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

revoke execute on function public.set_updated_at() from public;

create trigger wallets_set_updated_at
before update on public.wallets
for each row execute function public.set_updated_at();

alter table public.wallets enable row level security;

create policy "wallets_owner_all" on public.wallets
  for all
  to authenticated
  using ((select auth.uid()) = owner_id)
  with check ((select auth.uid()) = owner_id);
