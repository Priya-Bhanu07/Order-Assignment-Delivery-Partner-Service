ALTER TABLE delivery_partners
ADD COLUMN service_zone_id BIGINT;

ALTER TABLE delivery_partners
ADD CONSTRAINT fk_delivery_partner_service_zone
FOREIGN KEY (service_zone_id)
REFERENCES service_zones(id);
