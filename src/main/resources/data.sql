INSERT INTO period (period, period_name, start_time, end_time) VALUES
    (1, '1限', '09:00:00', '10:30:00'),
    (2, '2限', '10:40:00', '12:10:00'),
    (3, '3限', '13:00:00', '14:30:00'),
    (4, '4限', '14:40:00', '16:10:00')
ON DUPLICATE KEY UPDATE period_name=VALUES(period_name), start_time=VALUES(start_time), end_time=VALUES(end_time);

INSERT INTO pc (serial_number, created_at, updated_at) VALUES
    ('PC-001', NOW(), NOW()),
    ('PC-002', NOW(), NOW()),
    ('PC-003', NOW(), NOW()),
    ('PC-004', NOW(), NOW()),
    ('PC-005', NOW(), NOW());
