-- ============================================================
-- DEMO SEED DATA — E-Learning Platform
-- SQL Server (run AFTER Spring Boot creates the schema via ddl-auto=update)
-- All passwords are BCrypt hash of: Test@1234
-- BCrypt hash: $2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW
-- !! Replace the hash below if your BCrypt rounds differ !!
-- ============================================================

DECLARE @pwd NVARCHAR(100) = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW';
DECLARE @now DATETIME2 = GETUTCDATE();

-- ============================================================
-- 1. USERS
-- ============================================================

-- Admin
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('admin', @pwd, 'admin@elearning.com', N'System Admin', '1990-01-01', 'MALE', '0900000001', N'Administrator', N'Ho Chi Minh City', @now, @now, 'ADMIN', 'ACTIVE', 'ADULT');

-- Manager
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'manager01')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('manager01', @pwd, 'manager@elearning.com', N'Nguyen Van Manager', '1988-05-15', 'MALE', '0900000002', N'Course Manager', N'Ha Noi', @now, @now, 'MANAGER', 'ACTIVE', 'ADULT');

-- Staff members
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff01')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('staff01', @pwd, 'staff01@elearning.com', N'Tran Thi Staff', '1995-03-20', 'FEMALE', '0900000003', N'Content Creator', N'Da Nang', @now, @now, 'STAFF', 'ACTIVE', 'ADULT');

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff02')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('staff02', @pwd, 'staff02@elearning.com', N'Le Van Staff', '1993-07-10', 'MALE', '0900000004', N'Content Creator', N'Can Tho', @now, @now, 'STAFF', 'ACTIVE', 'ADULT');

-- Consultant
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'consultant01')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('consultant01', @pwd, 'consult01@elearning.com', N'Pham Thi Lan', '1985-11-25', 'FEMALE', '0900000005', N'English Consultant', N'Ho Chi Minh City', @now, @now, 'CONSULTANT', 'ACTIVE', 'ADULT');

-- Members
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'member01')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('member01', @pwd, 'member01@gmail.com', N'Hoang Van An', '2000-06-15', 'MALE', '0911000001', N'Student', N'Ho Chi Minh City', @now, @now, 'MEMBER', 'ACTIVE', 'ADULT');

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'member02')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('member02', @pwd, 'member02@gmail.com', N'Nguyen Thi Bich', '2002-09-30', 'FEMALE', '0911000002', N'Student', N'Ha Noi', @now, @now, 'MEMBER', 'ACTIVE', 'ADULT');

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'member03')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('member03', @pwd, 'member03@gmail.com', N'Tran Minh Duc', '1998-01-05', 'MALE', '0911000003', N'Office Worker', N'Da Nang', @now, @now, 'MEMBER', 'ACTIVE', 'ADULT');

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'member04')
INSERT INTO users (username, password, email, full_name, dob, gender, phone_number, job, [address], created_at, updated_at, role, status, age_group)
VALUES ('member04', @pwd, 'member04@gmail.com', N'Le Thi My', '2006-04-18', 'FEMALE', '0911000004', N'High School Student', N'Hue', @now, @now, 'MEMBER', 'ACTIVE', 'ADOLESCENT');

-- ============================================================
-- 2. COURSES
-- ============================================================

-- Course GUIDs
DECLARE @c1 UNIQUEIDENTIFIER = '11111111-0000-0000-0000-000000000001';
DECLARE @c2 UNIQUEIDENTIFIER = '11111111-0000-0000-0000-000000000002';
DECLARE @c3 UNIQUEIDENTIFIER = '11111111-0000-0000-0000-000000000003';
DECLARE @c4 UNIQUEIDENTIFIER = '11111111-0000-0000-0000-000000000004';
DECLARE @c5 UNIQUEIDENTIFIER = '11111111-0000-0000-0000-000000000005';

-- Free course — Adults
IF NOT EXISTS (SELECT 1 FROM course WHERE course_id = @c1)
INSERT INTO course (course_id, course_name, quantity, duration, image, price, description, age_group, status, created_at, updated_at, staff_id)
VALUES (@c1, N'English for Beginners', 50, 0, 'https://res.cloudinary.com/demo/image/upload/sample.jpg', 0,
        N'Khóa học tiếng Anh cơ bản dành cho người mới bắt đầu. Bao gồm ngữ pháp, từ vựng và phát âm.',
        'ADULT', 'AVAILABLE', @now, @now, 'staff01');

-- Paid course — Adults (299,000 VND)
IF NOT EXISTS (SELECT 1 FROM course WHERE course_id = @c2)
INSERT INTO course (course_id, course_name, quantity, duration, image, price, description, age_group, status, created_at, updated_at, staff_id)
VALUES (@c2, N'IELTS Foundation', 30, 0, 'https://res.cloudinary.com/demo/image/upload/sample.jpg', 299000,
        N'Lộ trình luyện thi IELTS từ nền tảng. Giúp học viên đạt band 5.5 - 6.0 trong 3 tháng.',
        'ADULT', 'AVAILABLE', @now, @now, 'staff01');

-- Paid course — Adults (499,000 VND)
IF NOT EXISTS (SELECT 1 FROM course WHERE course_id = @c3)
INSERT INTO course (course_id, course_name, quantity, duration, image, price, description, age_group, status, created_at, updated_at, staff_id)
VALUES (@c3, N'Business English Advanced', 20, 0, 'https://res.cloudinary.com/demo/image/upload/sample.jpg', 499000,
        N'Tiếng Anh thương mại nâng cao — email, thuyết trình, đàm phán chuyên nghiệp.',
        'ADULT', 'AVAILABLE', @now, @now, 'staff02');

-- Free course — Teens
IF NOT EXISTS (SELECT 1 FROM course WHERE course_id = @c4)
INSERT INTO course (course_id, course_name, quantity, duration, image, price, description, age_group, status, created_at, updated_at, staff_id)
VALUES (@c4, N'English for Teens', 40, 0, 'https://res.cloudinary.com/demo/image/upload/sample.jpg', 0,
        N'Khóa học tiếng Anh thú vị và sinh động dành riêng cho học sinh THPT.',
        'ADOLESCENT', 'AVAILABLE', @now, @now, 'staff02');

-- Paid course — Everyone (199,000 VND) — Pending approval
IF NOT EXISTS (SELECT 1 FROM course WHERE course_id = @c5)
INSERT INTO course (course_id, course_name, quantity, duration, image, price, description, age_group, status, created_at, updated_at, staff_id)
VALUES (@c5, N'TOEIC 600+ Crash Course', 100, 0, 'https://res.cloudinary.com/demo/image/upload/sample.jpg', 199000,
        N'Luyện thi TOEIC tăng tốc — chiến lược làm bài, nghe đọc toàn diện. Cam kết 600+ sau 6 tuần.',
        'EVERYONE', 'PENDING', @now, @now, 'staff01');

-- ============================================================
-- 3. MODULES
-- ============================================================

DECLARE @m1  UNIQUEIDENTIFIER = '22222222-0000-0000-0000-000000000001';
DECLARE @m2  UNIQUEIDENTIFIER = '22222222-0000-0000-0000-000000000002';
DECLARE @m3  UNIQUEIDENTIFIER = '22222222-0000-0000-0000-000000000003';
DECLARE @m4  UNIQUEIDENTIFIER = '22222222-0000-0000-0000-000000000004';
DECLARE @m5  UNIQUEIDENTIFIER = '22222222-0000-0000-0000-000000000005';
DECLARE @m6  UNIQUEIDENTIFIER = '22222222-0000-0000-0000-000000000006';

-- Modules for Course 1 (English for Beginners)
IF NOT EXISTS (SELECT 1 FROM module WHERE module_id = @m1)
INSERT INTO module (module_id, module_name, status, created_at, updated_at, course_id)
VALUES (@m1, N'Unit 1: Greetings & Introductions', 'AVAILABLE', @now, @now, @c1);

IF NOT EXISTS (SELECT 1 FROM module WHERE module_id = @m2)
INSERT INTO module (module_id, module_name, status, created_at, updated_at, course_id)
VALUES (@m2, N'Unit 2: Numbers & Alphabet', 'AVAILABLE', @now, @now, @c1);

-- Modules for Course 2 (IELTS Foundation)
IF NOT EXISTS (SELECT 1 FROM module WHERE module_id = @m3)
INSERT INTO module (module_id, module_name, status, created_at, updated_at, course_id)
VALUES (@m3, N'Module 1: IELTS Listening Strategies', 'AVAILABLE', @now, @now, @c2);

IF NOT EXISTS (SELECT 1 FROM module WHERE module_id = @m4)
INSERT INTO module (module_id, module_name, status, created_at, updated_at, course_id)
VALUES (@m4, N'Module 2: IELTS Reading Techniques', 'AVAILABLE', @now, @now, @c2);

-- Modules for Course 3 (Business English)
IF NOT EXISTS (SELECT 1 FROM module WHERE module_id = @m5)
INSERT INTO module (module_id, module_name, status, created_at, updated_at, course_id)
VALUES (@m5, N'Module 1: Professional Emails', 'AVAILABLE', @now, @now, @c3);

IF NOT EXISTS (SELECT 1 FROM module WHERE module_id = @m6)
INSERT INTO module (module_id, module_name, status, created_at, updated_at, course_id)
VALUES (@m6, N'Module 2: Presentations & Public Speaking', 'AVAILABLE', @now, @now, @c3);

-- ============================================================
-- 4. LESSONS
-- ============================================================

DECLARE @l1  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000001';
DECLARE @l2  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000002';
DECLARE @l3  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000003';
DECLARE @l4  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000004';
DECLARE @l5  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000005';
DECLARE @l6  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000006';
DECLARE @l7  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000007';
DECLARE @l8  UNIQUEIDENTIFIER = '33333333-0000-0000-0000-000000000008';

-- Lessons for Module 1 (Greetings)
IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l1)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l1, N'Lesson 1: Hello & Goodbye', 15, 'AVAILABLE', @now, @now, @m1);

IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l2)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l2, N'Lesson 2: Introducing Yourself', 20, 'AVAILABLE', @now, @now, @m1);

-- Lessons for Module 2 (Numbers)
IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l3)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l3, N'Lesson 1: Numbers 1-100', 15, 'AVAILABLE', @now, @now, @m2);

IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l4)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l4, N'Lesson 2: The Alphabet & Spelling', 20, 'AVAILABLE', @now, @now, @m2);

-- Lessons for Module 3 (IELTS Listening)
IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l5)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l5, N'Lesson 1: Section 1 & 2 Tactics', 30, 'AVAILABLE', @now, @now, @m3);

IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l6)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l6, N'Lesson 2: Section 3 & 4 Academic Listening', 35, 'AVAILABLE', @now, @now, @m3);

-- Lessons for Module 5 (Business Emails)
IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l7)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l7, N'Lesson 1: Formal vs Informal Emails', 25, 'AVAILABLE', @now, @now, @m5);

IF NOT EXISTS (SELECT 1 FROM lesson WHERE lesson_id = @l8)
INSERT INTO lesson (lesson_id, lesson_name, duration, status, created_at, updated_at, module_id)
VALUES (@l8, N'Lesson 2: Writing a Complaint Email', 25, 'AVAILABLE', @now, @now, @m5);

-- ============================================================
-- 5. ENROLLMENTS
-- ============================================================
-- member01 enrolled in Course 1 (free) — LEARNING
IF NOT EXISTS (SELECT 1 FROM enrollment WHERE member_id = 'member01' AND course_id = @c1)
INSERT INTO enrollment (enrollment_id, status, started_at, ended_at, member_id, course_id)
VALUES (NEWID(), 'LEARNING', @now, DATEADD(DAY, 14, @now), 'member01', @c1);

-- member02 enrolled in Course 1 (free) — COMPLETED
IF NOT EXISTS (SELECT 1 FROM enrollment WHERE member_id = 'member02' AND course_id = @c1)
INSERT INTO enrollment (enrollment_id, status, started_at, ended_at, member_id, course_id)
VALUES (NEWID(), 'COMPLETED', DATEADD(DAY, -30, @now), DATEADD(DAY, -16, @now), 'member02', @c1);

-- member04 enrolled in Course 4 (free, teens) — LEARNING
IF NOT EXISTS (SELECT 1 FROM enrollment WHERE member_id = 'member04' AND course_id = @c4)
INSERT INTO enrollment (enrollment_id, status, started_at, ended_at, member_id, course_id)
VALUES (NEWID(), 'LEARNING', @now, DATEADD(DAY, 14, @now), 'member04', @c4);

-- member03 enrolled in Course 2 (paid, post-payment) — LEARNING
IF NOT EXISTS (SELECT 1 FROM enrollment WHERE member_id = 'member03' AND course_id = @c2)
INSERT INTO enrollment (enrollment_id, status, started_at, ended_at, member_id, course_id)
VALUES (NEWID(), 'LEARNING', @now, DATEADD(DAY, 14, @now), 'member03', @c2);

-- ============================================================
-- 6. PAYMENTS
-- ============================================================

-- member03 paid for Course 2 (IELTS Foundation, 299,000đ) — SUCCESS
IF NOT EXISTS (SELECT 1 FROM payment WHERE order_id = 'DEMO-ORDER-001')
INSERT INTO payment (payment_id, order_id, request_id, amount, order_info, status, momo_trans_id, result_code, message, created_at, updated_at, member_id, course_id)
VALUES (NEWID(), 'DEMO-ORDER-001', 'DEMO-ORDER-001', 299000,
        N'Thanh toan khoa hoc: IELTS Foundation',
        'SUCCESS', '3290000001', 0, 'Successful.', DATEADD(DAY, -1, @now), DATEADD(DAY, -1, @now),
        'member03', @c2);

-- member01 paid for Course 3 (Business English, 499,000đ) — SUCCESS
IF NOT EXISTS (SELECT 1 FROM payment WHERE order_id = 'DEMO-ORDER-002')
INSERT INTO payment (payment_id, order_id, request_id, amount, order_info, status, momo_trans_id, result_code, message, created_at, updated_at, member_id, course_id)
VALUES (NEWID(), 'DEMO-ORDER-002', 'DEMO-ORDER-002', 499000,
        N'Thanh toan khoa hoc: Business English Advanced',
        'SUCCESS', '3290000002', 0, 'Successful.', DATEADD(DAY, -3, @now), DATEADD(DAY, -3, @now),
        'member01', @c3);

-- member02 attempted Course 2 payment but FAILED
IF NOT EXISTS (SELECT 1 FROM payment WHERE order_id = 'DEMO-ORDER-003')
INSERT INTO payment (payment_id, order_id, request_id, amount, order_info, status, momo_trans_id, result_code, message, created_at, updated_at, member_id, course_id)
VALUES (NEWID(), 'DEMO-ORDER-003', 'DEMO-ORDER-003', 299000,
        N'Thanh toan khoa hoc: IELTS Foundation',
        'FAILED', NULL, 1006, 'Transaction declined.', DATEADD(DAY, -2, @now), DATEADD(DAY, -2, @now),
        'member02', @c2);

-- member01 payment for TOEIC course is PENDING (not yet paid)
IF NOT EXISTS (SELECT 1 FROM payment WHERE order_id = 'DEMO-ORDER-004')
INSERT INTO payment (payment_id, order_id, request_id, amount, order_info, status, momo_trans_id, result_code, message, created_at, updated_at, member_id, course_id)
VALUES (NEWID(), 'DEMO-ORDER-004', 'DEMO-ORDER-004', 199000,
        N'Thanh toan khoa hoc: TOEIC 600+ Crash Course',
        'PENDING', NULL, NULL, NULL, @now, @now,
        'member01', @c5);

-- ============================================================
-- Summary
-- ============================================================
PRINT '=== Seed complete ===';
PRINT 'Users    : admin, manager01, staff01, staff02, consultant01, member01-04';
PRINT 'Courses  : 5 (2 free, 2 paid AVAILABLE, 1 paid PENDING)';
PRINT 'Modules  : 6';
PRINT 'Lessons  : 8';
PRINT 'Enrollments: 4';
PRINT 'Payments : 4 (2 SUCCESS, 1 FAILED, 1 PENDING)';
PRINT 'Password for all accounts: Test@1234';
