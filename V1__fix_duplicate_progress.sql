-- Bước 1: Xem duplicates
SELECT enrollment_id, lesson_id, COUNT(*) as count
FROM Progress
GROUP BY enrollment_id, lesson_id
HAVING COUNT(*) > 1;

-- Bước 2: Xóa duplicates (giữ lại 1 record)
WITH CTE AS (
    SELECT progress_id,
           ROW_NUMBER() OVER (
               PARTITION BY enrollment_id, lesson_id
               ORDER BY completed_at DESC
           ) AS row_num
    FROM Progress
)
DELETE FROM CTE WHERE row_num > 1;

-- Bước 3: Kiểm tra lại (phải trả về 0 rows)
SELECT enrollment_id, lesson_id, COUNT(*) as count
FROM Progress
GROUP BY enrollment_id, lesson_id
HAVING COUNT(*) > 1;