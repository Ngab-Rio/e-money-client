-- F-05 Anggaran: one monthly total or category budget per owner.

create table public.budgets (
	id uuid primary key default gen_random_uuid(),
	owner_id uuid not null references auth.users(id) on delete cascade,
	category_id uuid references public.categories(id) on delete restrict,
	period_month date not null,
	amount bigint not null check (amount > 0),
	created_at timestamptz not null default now(),
	updated_at timestamptz not null default now(),
	constraint budgets_period_first_day check (extract(day from period_month) = 1)
);

create unique index budgets_owner_category_period_idx
	on public.budgets (owner_id, coalesce(category_id, '00000000-0000-0000-0000-000000000000'::uuid), period_month);

create index budgets_owner_period_idx
	on public.budgets (owner_id, period_month);

create trigger budgets_set_updated_at
before update on public.budgets
for each row execute function public.set_updated_at();

alter table public.budgets enable row level security;

create policy "budgets_owner_all" on public.budgets
	for all
	to authenticated
	using ((select auth.uid()) = owner_id)
	with check ((select auth.uid()) = owner_id);
