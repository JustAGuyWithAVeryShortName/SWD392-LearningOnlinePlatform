package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.ReportRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ReportResponse;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.PaymentStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import com.hsp302.shared_english_e_learning_path.domain.enums.UserStatus;
import com.hsp302.shared_english_e_learning_path.repositories.CourseRepository;
import com.hsp302.shared_english_e_learning_path.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BlogService blogService;
    private final CourseService courseService;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;

    public List<ReportResponse> getLineChartData(ReportRequest request) {
        LocalDate reportStartedAtLocal;
        LocalDate reportEndedAtLocal;

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        String filterType = request.getFilterType() == null ? "ALL" : request.getFilterType().toUpperCase(Locale.ROOT);

        if ("CUSTOM".equals(filterType)) {
            if (request.getStartedMonth() == null || request.getEndedMonth() == null) {
                return new ArrayList<>();
            }
            try {
                reportStartedAtLocal = parseFlexibleDate(request.getStartedMonth());
                reportEndedAtLocal = parseFlexibleDate(request.getEndedMonth());
            } catch (Exception e) {
                return new ArrayList<>();
            }
        } else {
            switch (filterType) {
                case "Q1":
                    reportStartedAtLocal = LocalDate.of(currentYear, Month.JANUARY, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, Month.MARCH, 31);
                    break;
                case "Q2":
                    reportStartedAtLocal = LocalDate.of(currentYear, Month.APRIL, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, Month.JUNE, 30);
                    break;
                case "Q3":
                    reportStartedAtLocal = LocalDate.of(currentYear, Month.JULY, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, Month.SEPTEMBER, 30);
                    break;
                case "Q4":
                    reportStartedAtLocal = LocalDate.of(currentYear, Month.OCTOBER, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, Month.DECEMBER, 31);
                    break;
                case "FIRST_HALF":
                    reportStartedAtLocal = LocalDate.of(currentYear, Month.JANUARY, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, Month.JUNE, 30);
                    break;
                case "LAST_HALF":
                    reportStartedAtLocal = LocalDate.of(currentYear, Month.JULY, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, Month.DECEMBER, 31);
                    break;
                case "THIS_YEAR":
                    reportStartedAtLocal = LocalDate.of(currentYear, 1, 1);
                    reportEndedAtLocal = LocalDate.of(currentYear, 12, 31);
                    break;
                case "ALL":
                default:
                    reportStartedAtLocal = LocalDate.of(2024, 1, 1);
                    reportEndedAtLocal = now;
                    break;
            }
        }

        if (reportStartedAtLocal.isAfter(reportEndedAtLocal)) {
            return new ArrayList<>();
        }

        // Lặp qua từng tháng trong khoảng thời gian đã xác định
        List<ReportResponse> reportData = new ArrayList<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy"); // Added year for clarity
        YearMonth startMonth = YearMonth.from(reportStartedAtLocal);
        YearMonth endMonth = YearMonth.from(reportEndedAtLocal);

        ZoneId appZone = ZoneId.systemDefault();

        for (YearMonth currentMonth = startMonth; !currentMonth.isAfter(endMonth); currentMonth = currentMonth
                .plusMonths(1)) {
            LocalDate monthStartLocal = currentMonth.atDay(1);
            LocalDate monthEndLocal = currentMonth.atEndOfMonth();

            Instant monthStartInstant = monthStartLocal.atStartOfDay(appZone).toInstant();
            Instant monthEndInstant = monthEndLocal.atTime(LocalTime.MAX).atZone(appZone).toInstant();

            ReportResponse dataPoint = new ReportResponse();
            dataPoint.setDate(monthStartLocal); // Still use LocalDate for response if preferred for display
            dataPoint.setMonth(monthStartLocal.format(monthFormatter));

            dataPoint.setTotalMembers(userService.getAllUsersByDateDuration(monthStartInstant, monthEndInstant).size());
            dataPoint.setStaffMembers(
                    userService.getUsersByRoleAndDateDuration(Role.STAFF, monthStartInstant, monthEndInstant).size());
            dataPoint.setConsultants(userService
                    .getUsersByRoleAndDateDuration(Role.CONSULTANT, monthStartInstant, monthEndInstant).size());
            dataPoint.setMonthlyConsultations(
                    appointmentService.getAllAppointmentsByDateDuration(monthStartInstant, monthEndInstant).size());
            dataPoint.setActiveCourses(courseService
                    .getCoursesByStatusAndDateDuration(CourseStatus.AVAILABLE, monthStartInstant, monthEndInstant)
                    .size());
            dataPoint.setBlogs(blogService
                    .getBlogsByStatusAndDateDuration(BlogStatus.PUBLISHED, monthStartInstant, monthEndInstant).size());
            dataPoint.setEvents(0);
            dataPoint.setCourses(courseService.getAllCoursesByDateDuration(monthStartInstant, monthEndInstant).size());
            dataPoint.setRevenue(paymentRepository.sumAmountByStatusAndUpdatedAtBetween(
                    PaymentStatus.SUCCESS,
                    monthStartInstant,
                    monthEndInstant));
            dataPoint.setPaidCourses((int) paymentRepository.countByStatusAndUpdatedAtBetween(
                    PaymentStatus.SUCCESS,
                    monthStartInstant,
                    monthEndInstant));

            reportData.add(dataPoint);
        }
        return reportData;
    }

    public ReportResponse getStatCardData() {
        ReportResponse statCardData = new ReportResponse();

        ZoneId appZone = ZoneId.systemDefault();
        LocalDate now = LocalDate.now();
        Instant startOfMonthInstant = now.withDayOfMonth(1).atStartOfDay(appZone).toInstant();
        Instant endOfTodayInstant = now.atTime(LocalTime.MAX).atZone(appZone).toInstant();

        statCardData.setTotalMembers(userService.getUsersByStatus(UserStatus.ACTIVE).size());
        statCardData.setStaffMembers(userService.getUsersByStatusAndRole(UserStatus.ACTIVE, Role.STAFF).size());
        statCardData.setConsultants(userService.getUsersByStatusAndRole(UserStatus.ACTIVE, Role.CONSULTANT).size());
        statCardData.setMonthlyConsultations(
                appointmentService.getAllAppointmentsByDateDuration(startOfMonthInstant, endOfTodayInstant).size());
        statCardData.setActiveCourses(courseService.getCoursesByStatus(CourseStatus.AVAILABLE).size());
        statCardData.setBlogs(blogService.getBlogsByStatus(BlogStatus.PUBLISHED).size());
        statCardData.setCourses((int) courseRepository.count());
        statCardData.setEvents(0);
        statCardData.setRevenue(paymentRepository.sumAmountByStatus(PaymentStatus.SUCCESS));
        statCardData.setPaidCourses((int) paymentRepository.countByStatus(PaymentStatus.SUCCESS));
        return statCardData;
    }

    private LocalDate parseFlexibleDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Date value must not be blank");
        }

        try {
            return ZonedDateTime.parse(value).toLocalDate();
        } catch (Exception ignored) {
            // Fall back to simple ISO date, e.g. 2026-03-01
            return LocalDate.parse(value);
        }
    }
}