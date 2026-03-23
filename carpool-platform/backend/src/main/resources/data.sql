-- 插入测试用户
INSERT INTO users (username, phone, password, real_name, avatar, gender, age, status)
VALUES 
('admin', '13800138001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeG3vJ.7g6Hn7.7p6J7p6J7p6J7p6J7p6J', '管理员', '', '男', 25, 'ACTIVE'),
('test1', '13800138002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeG3vJ.7g6Hn7.7p6J7p6J7p6J7p6J7p6J', '测试用户1', '', '男', 22, 'ACTIVE'),
('test2', '13800138003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeG3vJ.7g6Hn7.7p6J7p6J7p6J7p6J7p6J', '测试用户2', '', '女', 20, 'ACTIVE');

-- 插入测试行程
INSERT INTO trips (user_id, trip_type, departure_province, departure_city, departure_detail, destination_province, destination_city, destination_detail, departure_time, seats_available, seats_total, price_per_person, status)
VALUES 
(1, 'driver', '北京市', '北京市', '天安门', '上海市', '上海市', '外滩', '2026-04-01 08:00:00', 3, 4, 300.00, 'pending'),
(2, 'passenger', '广州市', '广州市', '广州塔', '深圳市', '深圳市', '深圳湾', '2026-04-02 10:00:00', 1, 1, 100.00, 'pending');

-- 插入测试建议
INSERT INTO comments (user_id, username, avatar, content, rating, likes)
VALUES 
(1, 'admin', '', '这个拼车平台真的很棒，让我找到了回家的同伴，节省了不少费用，而且认识了新朋友！', 5, 12),
(2, 'test1', '', '车主很准时，车也很干净，一路上聊得很开心，下次还会使用这个平台。', 4, 8),
(3, 'test2', '', '平台操作简单，界面友好，客服响应也很及时，推荐给大家！', 5, 15);