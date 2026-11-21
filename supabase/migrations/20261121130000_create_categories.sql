-- F-04 Kategori: kategori bawaan/kustom dan subkategori.
-- Seed kategori bawaan per user dilakukan saat profil user dibuat.

create table public.categories (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) on delete cascade,
  name text not null check (length(trim(name)) > 0),
  type text not null check (type in ('income', 'expense')),
  parent_id uuid references public.categories(id) on delete restrict,
  icon text not null default 'category',
  color text not null default 'primary',
  is_default boolean not null default false,
  is_archived boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint categories_parent_same_owner check (parent_id is null or parent_id <> id)
);

create unique index categories_owner_name_parent_idx
  on public.categories (owner_id, lower(name), type, coalesce(parent_id, '00000000-0000-0000-0000-000000000000'::uuid));

create index categories_owner_active_idx
  on public.categories (owner_id, type, parent_id)
  where is_archived = false;

create trigger categories_set_updated_at
before update on public.categories
for each row execute function public.set_updated_at();

alter table public.categories enable row level security;

create policy "categories_owner_all" on public.categories
  for all
  to authenticated
  using ((select auth.uid()) = owner_id)
  with check ((select auth.uid()) = owner_id);
