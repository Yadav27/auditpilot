INSERT INTO tenant (id, slug, name) VALUES
  ('11111111-1111-1111-1111-111111111111', 'acme-bank', 'Acme Bank'),
  ('22222222-2222-2222-2222-222222222222', 'birch-insurance', 'Birch Insurance')
ON CONFLICT DO NOTHING;