-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    language_code VARCHAR(10) DEFAULT 'ru',
    rating INTEGER DEFAULT 500 NOT NULL,
    rank VARCHAR(50) DEFAULT 'SILVER' NOT NULL,
    level VARCHAR(10) DEFAULT 'A2' NOT NULL,
    total_games INTEGER DEFAULT 0 NOT NULL,
    wins INTEGER DEFAULT 0 NOT NULL,
    losses INTEGER DEFAULT 0 NOT NULL,
    draws INTEGER DEFAULT 0 NOT NULL,
    energy INTEGER DEFAULT 5 NOT NULL,
    max_energy INTEGER DEFAULT 5 NOT NULL,
    last_energy_update TIMESTAMP DEFAULT NOW() NOT NULL,
    coins INTEGER DEFAULT 0 NOT NULL,
    streak_days INTEGER DEFAULT 0 NOT NULL,
    last_login_date DATE,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

-- Word categories table
CREATE TABLE IF NOT EXISTS word_categories (
    id SERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name_en VARCHAR(255),
    name_ru VARCHAR(255),
    description_ru TEXT,
    icon VARCHAR(10),
    words_count INTEGER DEFAULT 0 NOT NULL,
    min_level VARCHAR(10) DEFAULT 'A1',
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

-- Words table
CREATE TABLE IF NOT EXISTS words (
    id SERIAL PRIMARY KEY,
    english VARCHAR(255) UNIQUE NOT NULL,
    russian VARCHAR(255) NOT NULL,
    level VARCHAR(10) NOT NULL,
    category VARCHAR(100),
    frequency INTEGER,
    audio_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_level ON words(level);
CREATE INDEX IF NOT EXISTS idx_category ON words(category);

-- Games table
CREATE TABLE IF NOT EXISTS games (
    id SERIAL PRIMARY KEY,
    player1_id BIGINT REFERENCES users(id),
    player2_id BIGINT REFERENCES users(id),
    player1_score INTEGER DEFAULT 0 NOT NULL,
    player2_score INTEGER DEFAULT 0 NOT NULL,
    winner_id BIGINT REFERENCES users(id),
    game_type VARCHAR(50) NOT NULL DEFAULT 'ranked',
    rounds_count INTEGER DEFAULT 3 NOT NULL,
    status VARCHAR(50) DEFAULT 'waiting' NOT NULL,
    current_turn BIGINT,
    is_bot_game BOOLEAN DEFAULT FALSE NOT NULL,
    bot_difficulty VARCHAR(50),
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_player1 ON games(player1_id);
CREATE INDEX IF NOT EXISTS idx_player2 ON games(player2_id);
CREATE INDEX IF NOT EXISTS idx_status ON games(status);

-- Game rounds table
CREATE TABLE IF NOT EXISTS game_rounds (
    id SERIAL PRIMARY KEY,
    game_id INTEGER REFERENCES games(id) ON DELETE CASCADE NOT NULL,
    round_number INTEGER NOT NULL,
    player_id BIGINT REFERENCES users(id) NOT NULL,
    word_id INTEGER REFERENCES words(id) NOT NULL,
    direction VARCHAR(10) NOT NULL DEFAULT 'en_to_ru',
    options JSONB NOT NULL DEFAULT '[]',
    correct_answer VARCHAR(255) NOT NULL,
    user_answer VARCHAR(255),
    is_correct BOOLEAN,
    answer_time_ms INTEGER,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_game ON game_rounds(game_id);

-- User words table
CREATE TABLE IF NOT EXISTS user_words (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    word_id INTEGER REFERENCES words(id) NOT NULL,
    status VARCHAR(50) DEFAULT 'learning' NOT NULL,
    correct_count INTEGER DEFAULT 0 NOT NULL,
    incorrect_count INTEGER DEFAULT 0 NOT NULL,
    last_reviewed TIMESTAMP,
    next_review TIMESTAMP DEFAULT NOW() NOT NULL,
    ease_factor FLOAT DEFAULT 2.5 NOT NULL,
    interval_days INTEGER DEFAULT 1 NOT NULL,
    added_from VARCHAR(50) DEFAULT 'manual',
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL,
    UNIQUE(user_id, word_id)
);

CREATE INDEX IF NOT EXISTS idx_user_next_review ON user_words(user_id, next_review);

-- Achievements table
CREATE TABLE IF NOT EXISTS achievements (
    id SERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name_en VARCHAR(255),
    name_ru VARCHAR(255),
    description_en TEXT,
    description_ru TEXT,
    icon VARCHAR(255),
    reward_coins INTEGER DEFAULT 0 NOT NULL,
    condition_type VARCHAR(100),
    condition_value INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

-- User achievements table
CREATE TABLE IF NOT EXISTS user_achievements (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    achievement_id INTEGER REFERENCES achievements(id) NOT NULL,
    unlocked_at TIMESTAMP DEFAULT NOW() NOT NULL,
    UNIQUE(user_id, achievement_id)
);

CREATE INDEX IF NOT EXISTS idx_user_achievement ON user_achievements(user_id);

-- Daily tasks table
CREATE TABLE IF NOT EXISTS daily_tasks (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    task_type VARCHAR(100) NOT NULL,
    requirement INTEGER NOT NULL,
    reward_coins INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    UNIQUE(date, task_type)
);

-- User daily tasks table
CREATE TABLE IF NOT EXISTS user_daily_tasks (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    daily_task_id INTEGER REFERENCES daily_tasks(id) NOT NULL,
    progress INTEGER DEFAULT 0 NOT NULL,
    completed BOOLEAN DEFAULT FALSE NOT NULL,
    completed_at TIMESTAMP,
    UNIQUE(user_id, daily_task_id)
);

CREATE INDEX IF NOT EXISTS idx_user_date ON user_daily_tasks(user_id, completed);

-- Matchmaking queue table
CREATE TABLE IF NOT EXISTS matchmaking_queue (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    rating INTEGER NOT NULL,
    rounds_preference INTEGER DEFAULT 3 NOT NULL,
    joined_at TIMESTAMP DEFAULT NOW() NOT NULL,
    status VARCHAR(50) DEFAULT 'searching' NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rating_status ON matchmaking_queue(rating, status);
CREATE INDEX IF NOT EXISTS idx_user_status ON matchmaking_queue(user_id, status);

-- Seasons table
CREATE TABLE IF NOT EXISTS seasons (
    id SERIAL PRIMARY KEY,
    season_number INTEGER UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT FALSE NOT NULL,
    rating_decay_percent INTEGER DEFAULT 50 NOT NULL,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);
