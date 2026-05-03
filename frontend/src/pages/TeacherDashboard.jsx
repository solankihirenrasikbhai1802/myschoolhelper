import React, { useState, useEffect } from 'react'
import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { BarChart3, ClipboardList, UserCheck } from 'lucide-react'
import Header from '@/components/Header'
import MarkAttendance from '@/components/teacher/MarkAttendance'
import ViewAttendance from '@/components/teacher/ViewAttendance'

/**
 * TeacherDashboard - Main teacher portal
 * Provides navigation between different teacher functionalities
 */
function TeacherDashboard() {
  const location = useLocation()

  // Navigation items for teacher
  const navItems = [
    {
      name: 'Mark Attendance',
      path: '/teacher/mark',
      icon: UserCheck,
      color: 'bg-blue-100 text-blue-600'
    },
    {
      name: 'View Attendance',
      path: '/teacher/view',
      icon: ClipboardList,
      color: 'bg-green-100 text-green-600'
    }
  ]

  const isActive = (path) => location.pathname === path

  return (
    <div className="min-h-screen bg-gray-50">
      <Header title="Teacher Dashboard" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Navigation Cards */}
        {location.pathname === '/teacher' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            {navItems.map((item) => {
              const IconComponent = item.icon
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className="card hover:shadow-lg transition-shadow cursor-pointer"
                >
                  <div className="flex items-start gap-4">
                    <div className={`p-3 rounded-lg ${item.color}`}>
                      <IconComponent size={28} />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900 mb-1">
                        {item.name}
                      </h3>
                      <p className="text-gray-600 text-sm">
                        {item.name === 'Mark Attendance'
                          ? 'Mark student attendance for today'
                          : 'View past attendance records'}
                      </p>
                    </div>
                  </div>
                </Link>
              )
            })}
          </div>
        )}

        {/* Routes */}
        <Routes>
          <Route path="/mark" element={<MarkAttendance />} />
          <Route path="/view" element={<ViewAttendance />} />
          <Route path="/" element={null} />
        </Routes>
      </div>
    </div>
  )
}

export default TeacherDashboard
