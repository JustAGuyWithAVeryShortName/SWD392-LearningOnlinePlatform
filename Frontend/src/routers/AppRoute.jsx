import React from "react";
import { Routes, Route } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "../App.css";
import AppLayout from "../layouts/AppLayout";
import Home from "../pages/home/Home";
import Login from "../pages/login/Login";
import OAuth2RedirectHandler from "../components/OAuth2RedirectHandler";

import NotFound from "../pages/not-found/NotFound";
import MyProfile from "../pages/my-profiles/MyProfile";

import BlogList from "../pages/blog/BlogList";
import BlogDetails from "../pages/blog/BlogDetails";
import BlogCreation from "../pages/blog/BlogCreation";

import CourseList from "../pages/course/CourseList";
import CourseDetails from "../pages/course/CourseDetails";
import CourseLesson from "../pages/course/CourseLesson";
import CourseCreation from "../pages/course/CourseCreation";
import ModuleCreation from "../pages/module/ModuleCreation";
import LessonCreation from "../pages/lesson/LessonCreation";

import AppointmentBooking from "../pages/appointment/AppointmentBooking";
import AvailabilityBooking from "../pages/availability/AvailabilityBooking";

import HomeStaff from "../pages/home/HomeStaff";
import HomeManager from "../pages/home/HomeManager";
import HomeConsultant from "../pages/home/HomeConsultant";
import ForgotResetPassword from "../pages/password/ForgotResetPassword";

import BlogManagement from "../pages/blog/BlogManagement";
import CourseManagement from "../pages/course/CourseManagement";
import ChatPage from "../pages/chat/ChatPage";

export default function AppRoute() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<Home />} />
        <Route path="/staff" element={<HomeStaff />} />
        <Route path="/manager" element={<HomeManager />} />
        <Route path="/consultant" element={<HomeConsultant />} />
        <Route path="/oauth2/success" element={<OAuth2RedirectHandler />} />

        <Route path="/blogs" element={<BlogList />} />
        <Route path="/blog-management" element={<BlogManagement />} />
        <Route path="/blogs/:id" element={<BlogDetails />} />
        {/* Tạo blog mới tại /blogs/create */}
        <Route path="/blogs/create" element={<BlogCreation />} />
        {/* Chỉnh sửa blog tại /blogs/create/:id */}
        <Route path="/blogs/create/:id" element={<BlogCreation />} />

        <Route path="/courses" element={<CourseList />} />
        <Route path="/course-management" element={<CourseManagement />} />
        <Route path="/courses/:id" element={<CourseDetails />} />
        
        {/* Tạo khóa học mới tại /courses/create */}
        <Route path="/courses/create" element={<CourseCreation />} />
        <Route
          path="/courses/:courseID/module/create"
          element={<ModuleCreation />}
        />
        <Route
          path="/courses/:courseID/module/:moduleID/lesson/create"
          element={<LessonCreation />}
        />

        {/* Cập nhật khóa học tại /courses/:courseID/update */}
        <Route path="/courses/:courseID/update" element={<CourseCreation />} />
        <Route
          path="/courses/:courseID/module/:moduleID/update"
          element={<ModuleCreation />}
        />
        <Route
          path="/courses/:courseID/module/:moduleID/lesson/:lessonID/update"
          element={<LessonCreation />}
        />

        <Route path="/appointment" element={<AppointmentBooking />} />
        <Route path="/availability" element={<AvailabilityBooking />} />

        <Route path="*" element={<NotFound />} />
      </Route>
      <Route path="/chat" element={<ChatPage />} />
      <Route path="/login" element={<Login />} />
      <Route path="/profile" element={<MyProfile />} />
      <Route path="/courses/lesson/:id" element={<CourseLesson />} />
      <Route path="/forgot-password" element={<ForgotResetPassword />} />
    </Routes>
  );
}
