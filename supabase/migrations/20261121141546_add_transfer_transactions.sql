-- F-03 Transaksi: income, expense, and one-record wallet transfers.

create table public.transactions (
	id uuid primary key default gen_random_uuid(),
	owner_id uuid not null references auth.users(id) on delete cascade,
	type text not null check (type in ('income', 'expense', 'transfer')),
	amount bigint not null check (amount > 0),
	wallet_id uuid not null references public.wallets(id) on delete restrict,
	to_wallet_id uuid references public.wallets(id) on delete restrict,
	category_id uuid references public.categories(id) on delete restrict,
	note text,
	occurred_at timestamptz not null default now(),
	created_at timestamptz not null default now(),
	updated_at timestamptz not null default now(),
	constraint transactions_type_fields check (
		(type = 'transfer' and to_wallet_id is not null and to_wallet_id <> wallet_id and category_id is null)
		or (type in ('income', 'expense') and to_wallet_id is null and category_id is not null)
	)
);

create index transactions_owner_occurred_idx
	on public.transactions (owner_id, occurred_at desc);

create index transactions_wallet_idx
	on public.transactions (wallet_id, occurred_at desc);

create index transactions_destination_wallet_idx
	on public.transactions (to_wallet_id, occurred_at desc)
	where to_wallet_id is not null;

create trigger transactions_set_updated_at
before update on public.transactions
for each row execute function public.set_updated_at();

alter table public.transactions enable row level security;

create policy "transactions_owner_all" on public.transactions
	for all
	to authenticated
	using ((select auth.uid()) = owner_id)
	with check ((select auth.uid()) = owner_id);
