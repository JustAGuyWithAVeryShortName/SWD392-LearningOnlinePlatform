-- ============================================================
-- DEMO SEED DATA (LARGE) - E-Learning Platform
-- SQL Server (run AFTER Spring Boot creates the schema)
-- This script will:
--   1) Wipe all existing data in all tables
--   2) Seed fresh demo data for reporting
-- Default password for ALL users: 123456
-- NOTE: Current AuthenticationService compares plain text password directly.
-- So this seed stores plain text '123456' (NOT bcrypt) for compatibility.
-- ============================================================

SET NOCOUNT ON;

DECLARE @pwd NVARCHAR(100) = '123456';
DECLARE @now DATETIME2 = GETUTCDATE();

-- ============================================================
-- 0) FULL CLEANUP (all tables)
-- ============================================================
DECLARE @sql NVARCHAR(MAX) = N'';

SELECT @sql = @sql + N'ALTER TABLE [' + s.name + N'].[' + t.name + N'] NOCHECK CONSTRAINT ALL;' + CHAR(10)
FROM sys.tables t
JOIN sys.schemas s ON s.schema_id = t.schema_id;
EXEC sp_executesql @sql;

SET @sql = N'';
SELECT @sql = @sql + N'DELETE FROM [' + s.name + N'].[' + t.name + N'];' + CHAR(10)
FROM sys.tables t
JOIN sys.schemas s ON s.schema_id = t.schema_id;
EXEC sp_executesql @sql;

SET @sql = N'';
SELECT @sql = @sql + N'ALTER TABLE [' + s.name + N'].[' + t.name + N'] WITH CHECK CHECK CONSTRAINT ALL;' + CHAR(10)
FROM sys.tables t
JOIN sys.schemas s ON s.schema_id = t.schema_id;
EXEC sp_executesql @sql;

-- ============================================================
-- 1) USERS
--    1 admin + 1 manager + 2 consultants + 10 lecturers (STAFF) + 60 members
-- ============================================================
INSERT INTO users (
        username, password, email, full_name, dob, gender, phone_number, job, [address],
        created_at, updated_at, role, status, age_group
)
VALUES
('admin', @pwd, 'admin@elearning.com', N'System Admin', '1990-01-01', 'MALE', '0900000001', N'Administrator', N'Ho Chi Minh City', @now, @now, 'ADMIN', 'ACTIVE', 'ADULT'),
('manager01', @pwd, 'manager@elearning.com', N'Demo Manager', '1988-05-15', 'MALE', '0900000002', N'Course Manager', N'Ha Noi', @now, @now, 'MANAGER', 'ACTIVE', 'ADULT'),
('consultant01', @pwd, 'consultant01@elearning.com', N'Consultant Demo 1', '1987-03-10', 'FEMALE', '0900000011', N'English Consultant', N'Ho Chi Minh City', @now, @now, 'CONSULTANT', 'ACTIVE', 'ADULT'),
('consultant02', @pwd, 'consultant02@elearning.com', N'Consultant Demo 2', '1991-09-22', 'MALE', '0900000012', N'English Consultant', N'Ha Noi', @now, @now, 'CONSULTANT', 'ACTIVE', 'ADULT');

DECLARE @i INT = 1;
WHILE @i <= 10
BEGIN
        INSERT INTO users (
                username, password, email, full_name, dob, gender, phone_number, job, [address],
                created_at, updated_at, role, status, age_group
        )
        VALUES (
                'staff' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
                @pwd,
                'staff' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2) + '@elearning.com',
                N'English Lecturer ' + CAST(@i AS NVARCHAR(10)),
                DATEADD(DAY, -(@i * 400), CAST(@now AS DATE)),
                CASE WHEN @i % 2 = 0 THEN 'FEMALE' ELSE 'MALE' END,
                '0901' + RIGHT('000000' + CAST(@i AS VARCHAR(6)), 6),
                N'English Lecturer',
                CASE WHEN @i % 3 = 0 THEN N'Da Nang' WHEN @i % 3 = 1 THEN N'Ha Noi' ELSE N'Ho Chi Minh City' END,
                @now,
                @now,
                'STAFF',
                'ACTIVE',
                CASE WHEN @i % 4 = 0 THEN 'EVERYONE' ELSE 'ADULT' END
        );

        SET @i = @i + 1;
END;

SET @i = 1;
WHILE @i <= 60
BEGIN
        INSERT INTO users (
                username, password, email, full_name, dob, gender, phone_number, job, [address],
                created_at, updated_at, role, status, age_group
        )
        VALUES (
                'member' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
                @pwd,
                'member' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2) + '@gmail.com',
                N'Member Demo ' + CAST(@i AS NVARCHAR(10)),
                DATEADD(DAY, -(@i * 220), CAST(@now AS DATE)),
                CASE WHEN @i % 2 = 0 THEN 'FEMALE' ELSE 'MALE' END,
                '0911' + RIGHT('000000' + CAST(@i AS VARCHAR(6)), 6),
                N'Learner',
                CASE WHEN @i % 4 = 0 THEN N'Can Tho' WHEN @i % 4 = 1 THEN N'Ha Noi' WHEN @i % 4 = 2 THEN N'Da Nang' ELSE N'Ho Chi Minh City' END,
                DATEADD(DAY, -(@i * 8), @now),
                DATEADD(DAY, -(@i * 8), @now),
                'MEMBER',
                'ACTIVE',
                CASE WHEN @i % 5 = 0 THEN 'ADOLESCENT' ELSE 'ADULT' END
        );

        SET @i = @i + 1;
END;

-- ============================================================
-- 2) COURSES (50 courses, mostly paid)
-- ============================================================
DECLARE @courses TABLE (
        course_idx INT PRIMARY KEY,
        course_id UNIQUEIDENTIFIER,
        course_name NVARCHAR(255),
        price BIGINT,
        status VARCHAR(20),
        created_at DATETIME2
);

SET @i = 1;
WHILE @i <= 50
BEGIN
        DECLARE @courseId UNIQUEIDENTIFIER = NEWID();
        DECLARE @courseName NVARCHAR(255) = N'English Course ' + RIGHT('00' + CAST(@i AS NVARCHAR(2)), 2);
        DECLARE @price BIGINT = CASE
                WHEN @i % 7 = 0 THEN 0
                WHEN @i % 5 = 0 THEN 199000
                WHEN @i % 3 = 0 THEN 299000
                WHEN @i % 2 = 0 THEN 399000
                ELSE 499000
        END;
        DECLARE @status VARCHAR(20) = CASE WHEN @i % 10 = 0 THEN 'PENDING' ELSE 'AVAILABLE' END;
        DECLARE @courseCreated DATETIME2 = DATEADD(DAY, (@i - 1) * 12, CAST('2025-01-01' AS DATETIME2));
        DECLARE @staffId VARCHAR(255) = 'staff' + RIGHT('00' + CAST(((@i - 1) % 10) + 1 AS VARCHAR(2)), 2);

        INSERT INTO course (
                course_id, course_name, quantity, duration, image, price, description,
                age_group, status, created_at, updated_at, staff_id
        )
        VALUES (
                @courseId,
                @courseName,
                30 + (@i % 70),
                0,
                'https://res.cloudinary.com/demo/image/upload/sample.jpg',
                @price,
                N'Demo course for reporting and dashboard analytics.',
                CASE WHEN @i % 6 = 0 THEN 'ADOLESCENT' WHEN @i % 4 = 0 THEN 'EVERYONE' ELSE 'ADULT' END,
                @status,
                @courseCreated,
                DATEADD(HOUR, 3, @courseCreated),
                @staffId
        );

        INSERT INTO @courses (course_idx, course_id, course_name, price, status, created_at)
        VALUES (@i, @courseId, @courseName, @price, @status, @courseCreated);

        SET @i = @i + 1;
END;

-- ============================================================
-- 3) PAYMENTS + ENROLLMENTS
--    Revenue timeline from 2025-01-01 to current month
-- ============================================================
DECLARE @paidCourses TABLE (
        row_no INT PRIMARY KEY,
        course_id UNIQUEIDENTIFIER,
        course_name NVARCHAR(255),
        amount BIGINT
);

INSERT INTO @paidCourses (row_no, course_id, course_name, amount)
SELECT
        ROW_NUMBER() OVER (ORDER BY course_idx),
        course_id,
        course_name,
        price
FROM @courses
WHERE price > 0 AND status = 'AVAILABLE';

DECLARE @paidCount INT = (SELECT COUNT(*) FROM @paidCourses);
DECLARE @monthStart DATE = '2025-01-01';
DECLARE @monthEnd DATE = DATEFROMPARTS(YEAR(@now), MONTH(@now), 1);
DECLARE @paymentSeq INT = 1;

WHILE @monthStart <= @monthEnd
BEGIN
        DECLARE @k INT = 1;
        WHILE @k <= 6
        BEGIN
                DECLARE @pick INT = ((@paymentSeq + @k) % @paidCount) + 1;
                DECLARE @courseIdPaid UNIQUEIDENTIFIER;
                DECLARE @courseNamePaid NVARCHAR(255);
                DECLARE @amountPaid BIGINT;
                DECLARE @memberId VARCHAR(255) = 'member' + RIGHT('00' + CAST(((@paymentSeq + @k - 1) % 60) + 1 AS VARCHAR(2)), 2);
                DECLARE @createdAt DATETIME2 = DATEADD(DAY, @k * 4, CAST(@monthStart AS DATETIME2));
                DECLARE @updatedAt DATETIME2 = DATEADD(HOUR, 2, @createdAt);
                DECLARE @paymentStatus VARCHAR(20);
                DECLARE @orderId VARCHAR(100) =
                        'DEMO-' + FORMAT(@monthStart, 'yyyyMM') + '-' + RIGHT('0000' + CAST(@paymentSeq * 10 + @k AS VARCHAR(10)), 4);

                SELECT @courseIdPaid = course_id, @courseNamePaid = course_name, @amountPaid = amount
                FROM @paidCourses
                WHERE row_no = @pick;

                SET @paymentStatus = CASE WHEN @k <= 4 THEN 'SUCCESS' WHEN @k = 5 THEN 'FAILED' ELSE 'PENDING' END;

                INSERT INTO payment (
                        payment_id, order_id, request_id, amount, order_info, status,
                        momo_trans_id, result_code, message, created_at, updated_at, member_id, course_id
                )
                VALUES (
                        NEWID(),
                        @orderId,
                        @orderId,
                        @amountPaid,
                        N'Thanh toan khoa hoc: ' + @courseNamePaid,
                        @paymentStatus,
                        CASE WHEN @paymentStatus = 'SUCCESS' THEN CAST(3000000000 + @paymentSeq * 10 + @k AS VARCHAR(20)) ELSE NULL END,
                        CASE WHEN @paymentStatus = 'SUCCESS' THEN 0 WHEN @paymentStatus = 'FAILED' THEN 1006 ELSE NULL END,
                        CASE WHEN @paymentStatus = 'SUCCESS' THEN 'Successful.' WHEN @paymentStatus = 'FAILED' THEN 'Transaction declined.' ELSE NULL END,
                        @createdAt,
                        @updatedAt,
                        @memberId,
                        @courseIdPaid
                );

                IF @paymentStatus = 'SUCCESS'
                   AND NOT EXISTS (
                                SELECT 1
                                FROM enrollment
                                WHERE member_id = @memberId AND course_id = @courseIdPaid
                   )
                BEGIN
                        INSERT INTO enrollment (enrollment_id, status, started_at, ended_at, member_id, course_id)
                        VALUES (NEWID(), 'LEARNING', @updatedAt, DATEADD(DAY, 30, @updatedAt), @memberId, @courseIdPaid);
                END;

                SET @k = @k + 1;
        END;

        SET @paymentSeq = @paymentSeq + 1;
        SET @monthStart = DATEADD(MONTH, 1, @monthStart);
END;

-- Some free-course enrollments for variety
INSERT INTO enrollment (enrollment_id, status, started_at, ended_at, member_id, course_id)
SELECT TOP 40
        NEWID(),
        CASE WHEN ROW_NUMBER() OVER (ORDER BY c.course_id) % 3 = 0 THEN 'COMPLETED' ELSE 'LEARNING' END,
        DATEADD(DAY, -20, @now),
        DATEADD(DAY, 10, @now),
        'member' + RIGHT('00' + CAST((ROW_NUMBER() OVER (ORDER BY c.course_id)) AS VARCHAR(2)), 2),
        c.course_id
FROM course c
WHERE c.price = 0;

-- ============================================================
-- SUMMARY
-- ============================================================
DECLARE @totalUsers INT = (SELECT COUNT(*) FROM users);
DECLARE @totalStaff INT = (SELECT COUNT(*) FROM users WHERE role = 'STAFF');
DECLARE @totalConsultants INT = (SELECT COUNT(*) FROM users WHERE role = 'CONSULTANT');
DECLARE @totalMembers INT = (SELECT COUNT(*) FROM users WHERE role = 'MEMBER');
DECLARE @totalCourses INT = (SELECT COUNT(*) FROM course);
DECLARE @totalPayments INT = (SELECT COUNT(*) FROM payment);
DECLARE @totalRevenue BIGINT = (SELECT COALESCE(SUM(amount), 0) FROM payment WHERE status = 'SUCCESS');

PRINT '=== Seed complete ===';
PRINT 'Default password for all users: 123456';
PRINT 'Users      : ' + CAST(@totalUsers AS VARCHAR(20));
PRINT 'Staff      : ' + CAST(@totalStaff AS VARCHAR(20));
PRINT 'Consultants: ' + CAST(@totalConsultants AS VARCHAR(20));
PRINT 'Members    : ' + CAST(@totalMembers AS VARCHAR(20));
PRINT 'Courses    : ' + CAST(@totalCourses AS VARCHAR(20));
PRINT 'Payments   : ' + CAST(@totalPayments AS VARCHAR(20));
PRINT 'Revenue    : ' + CAST(@totalRevenue AS VARCHAR(30));
PRINT 'Range      : 2025-01-01 -> now';
