-- Word categories seed data
INSERT INTO word_categories (code, name_en, name_ru, description_ru, icon, min_level) VALUES
('food', 'Food & Drinks', 'Еда и напитки', 'Слова о еде, напитках и кулинарии', '🍕', 'A1'),
('travel', 'Travel', 'Путешествия', 'Слова для путешествий и туризма', '✈️', 'A1'),
('work', 'Work & Business', 'Работа и бизнес', 'Профессиональная лексика', '💼', 'B1'),
('home', 'Home & Family', 'Дом и семья', 'Слова о доме, семье и быте', '🏠', 'A1'),
('entertainment', 'Entertainment', 'Развлечения', 'Кино, музыка, спорт', '🎬', 'A2'),
('technology', 'Technology', 'Технологии', 'IT и современные технологии', '💻', 'B1'),
('health', 'Health', 'Здоровье', 'Медицина и здоровый образ жизни', '🏥', 'A2'),
('education', 'Education', 'Образование', 'Учёба и наука', '🎓', 'A2'),
('nature', 'Nature', 'Природа', 'Животные, растения и природа', '🌍', 'A1'),
('shopping', 'Shopping', 'Покупки', 'Шоппинг и финансы', '🛍️', 'A1')
ON CONFLICT (code) DO NOTHING;

-- Achievements seed data
INSERT INTO achievements (code, name_en, name_ru, description_ru, icon, reward_coins, condition_type, condition_value) VALUES
('first_win', 'First Blood', 'Первая победа', 'Выиграй свою первую игру', '🎖️', 10, 'wins_count', 1),
('warrior', 'Warrior', 'Воин', 'Выиграй 10 игр', '⚔️', 50, 'wins_count', 10),
('champion', 'Champion', 'Чемпион', 'Выиграй 50 игр', '🏆', 100, 'wins_count', 50),
('legend', 'Legend', 'Легенда', 'Выиграй 100 игр', '👑', 200, 'wins_count', 100),
('streak_3', 'Dedicated', 'Постоянство', 'Заходи 3 дня подряд', '🔥', 20, 'streak_days', 3),
('streak_7', 'Committed', 'Преданность', 'Заходи 7 дней подряд', '🔥', 50, 'streak_days', 7),
('streak_30', 'Unstoppable', 'Неудержимый', 'Заходи 30 дней подряд', '🔥', 200, 'streak_days', 30),
('student', 'Student', 'Студент', 'Добавь 50 слов в словарь', '📚', 30, 'words_learned', 50),
('polyglot', 'Polyglot', 'Полиглот', 'Добавь 200 слов в словарь', '🌍', 100, 'words_learned', 200),
('professor', 'Professor', 'Профессор', 'Добавь 500 слов в словарь', '🎓', 300, 'words_learned', 500),
('veteran', 'Veteran', 'Ветеран', 'Сыграй 100 игр', '🎯', 100, 'total_games', 100)
ON CONFLICT (code) DO NOTHING;

-- Season 1
INSERT INTO seasons (season_number, start_date, end_date, is_active, rating_decay_percent)
VALUES (1, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', TRUE, 50)
ON CONFLICT (season_number) DO NOTHING;
