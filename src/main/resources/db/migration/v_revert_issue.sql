-- 1️⃣ Drop FK pointing to delivery_partners
ALTER TABLE orders
DROP CONSTRAINT IF EXISTS orders_assigned_partner_id_fkey;

-- 2️⃣ Add FK back to users(id)
ALTER TABLE orders
ADD CONSTRAINT orders_assigned_partner_id_fkey
FOREIGN KEY (assigned_partner_id)
REFERENCES users(id);