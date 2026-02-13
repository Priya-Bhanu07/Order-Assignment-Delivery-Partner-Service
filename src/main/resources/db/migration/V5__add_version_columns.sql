-- NEW: for @Version in DeliveryPartner
ALTER TABLE delivery_partners
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- NEW: for @Version in Order (only if you added it in the entity)
ALTER TABLE orders
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;