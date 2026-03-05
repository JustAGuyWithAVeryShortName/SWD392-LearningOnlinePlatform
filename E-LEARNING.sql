use E_LEARNING
--USERS
--INSERT INTO user tự tạo do đang encode
  INSERT INTO Users (
    username,
    password,
    email,
    full_name,
    dob,
    gender,
    phone_number,
    job,
    address,
    created_at,
    updated_at,
    role,
    status,
    age_group
)
VALUES
-- MEMBER
('member02', '123456', 'member01@gmail.com', 'Member User',
 '2000-05-10', 'MALE', '0900000001', 'Student', 'Hanoi',
 GETDATE(), GETDATE(), 'MEMBER', 'ACTIVE', 'ADULT'),

-- STAFF
('staff01', '123456', 'staff01@gmail.com', 'Staff User',
 '1998-03-15', 'FEMALE', '0900000002', 'Staff', 'HCM',
 GETDATE(), GETDATE(), 'STAFF', 'ACTIVE', 'ADULT'),

-- CONSULTANT
('consultant01', '123456', 'consultant01@gmail.com', 'Consultant User',
 '1995-08-20', 'MALE', '0900000003', 'Consultant', 'Da Nang',
 GETDATE(), GETDATE(), 'CONSULTANT', 'ACTIVE', 'ADULT'),

-- MANAGER
('manager01', '123456', 'manager01@gmail.com', 'Manager User',
 '1990-01-01', 'FEMALE', '0900000004', 'Manager', 'Hanoi',
 GETDATE(), GETDATE(), 'MANAGER', 'ACTIVE', 'ADULT'),

-- ADMIN
('admin01', '123456', 'admin01@gmail.com', 'Admin User',
 '1988-12-12', 'MALE', '0900000005', 'Admin', 'HCM',
 GETDATE(), GETDATE(), 'ADMIN', 'ACTIVE', 'ADULT');


--COURSE
INSERT INTO course (
    course_id, course_name, quantity, duration,
    image, description,
    age_group, status,
    created_at, updated_at
)
VALUES
(
    NEWID(),
    N'Fullstack Web Development',
    100, 120,
    'fullstack.jpg',
    N'Khóa học Fullstack React + Spring Boot',
    'ADULT', 'AVAILABLE',
    GETUTCDATE(), GETUTCDATE()
),
(
    NEWID(),
    N'Java Spring Boot Backend',
    80, 90,
    'springboot.jpg',
    N'Xây dựng REST API với Spring Boot',
    'ADULT', 'AVAILABLE',
    GETUTCDATE(), GETUTCDATE()
);
--MODULE
DECLARE @courseId UNIQUEIDENTIFIER =
(SELECT TOP 1 course_id FROM course);

INSERT INTO module (
    module_id, module_name, status,
    created_at, updated_at, course_id
)
VALUES (
    NEWID(),
    N'Spring Boot Basics',
    'AVAILABLE',
    GETUTCDATE(), GETUTCDATE(),
    @courseId
);
--LESSON
DECLARE @moduleId UNIQUEIDENTIFIER =
(SELECT TOP 1 module_id FROM module);

INSERT INTO lesson (
    lesson_id, lesson_name, duration,
    objective, content, resource,
    status, created_at, updated_at, module_id
)
VALUES (
    NEWID(),
    N'Introduction to Spring Boot',
    45,
    N'Hiểu Spring Boot cơ bản',
    N'Nội dung bài học Spring Boot...',
    'spring_intro.pdf',
    'AVAILABLE',
    GETUTCDATE(), GETUTCDATE(),
    @moduleId
);
--qualification
INSERT INTO qualification (
    qualification_id, name, image,
    degree, institution, year,
    status, created_at, updated_at,
    consultant_id
)
VALUES (
    NEWID(),
    N'Java Backend Developer',
    'java_cert.png',
    'BACHELOR',
    N'FPT University',
    2019,
    'AVAILABLE',
    GETUTCDATE(), GETUTCDATE(),
    'consultant01'
);
--BLOG
INSERT INTO blog (
    blog_id,
    blog_name,
    image,
    description,
    content,
    reading_time,
    blog_type,
    blog_status,
    age_group,
    created_at,
    updated_at,
    member_id
)
VALUES
-- 1
(
    NEWID(),
    N'Lộ trình học Java cho người mới',
    'java_path.jpg',
    N'Lộ trình học Java từ cơ bản đến nâng cao',
    N'Nội dung chi tiết về lộ trình học Java hiệu quả...',
    7,
    'EDUCATIONAL',
    'PUBLISHED',
    'ADULT',
    GETUTCDATE(),
    GETUTCDATE(),
    'member02'
),
-- 2
(
    NEWID(),
    N'Spring Boot là gì?',
    'spring_intro.jpg',
    N'Giải thích Spring Boot dễ hiểu',
    N'Tổng quan về Spring Boot, ưu điểm và cách sử dụng...',
    6,
    'GENERAL',
    'PUBLISHED',
    'ADULT',
    GETUTCDATE(),
    GETUTCDATE(),
    'member02'
),
-- 3
(
    NEWID(),
    N'Kinh nghiệm tự học lập trình',
    'self_learning.jpg',
    N'Chia sẻ kinh nghiệm tự học lập trình',
    N'Những kinh nghiệm thực tế khi tự học lập trình tại nhà...',
    5,
    'PERSONAL',
    'PUBLISHED',
    'ADULT',
    GETUTCDATE(),
    GETUTCDATE(),
    'member02'
),
-- 4
(
    NEWID(),
    N'Xu hướng công nghệ năm 2026',
    'tech_trend.jpg',
    N'Các xu hướng công nghệ nổi bật',
    N'Trí tuệ nhân tạo, Cloud, DevOps và các xu hướng mới...',
    8,
    'NEWS',
    'PUBLISHED',
    'ADULT',
    GETUTCDATE(),
    GETUTCDATE(),
    'member02'
),
-- 5
(
    NEWID(),
    N'Một ngày của lập trình viên',
    'dev_life.jpg',
    N'Cuộc sống thường ngày của lập trình viên',
    N'Mô tả công việc, áp lực và niềm vui của lập trình viên...',
    4,
    'NICHE',
    'PUBLISHED',
    'ADULT',
    GETUTCDATE(),
    GETUTCDATE(),
    'member02'
);
