import React, { useState, useEffect } from 'react'
import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { BarChart3, Users, FileText, Settings } from 'lucide-react'
import Header from '@/components/Header'
import AdminAttendanceView from '@/components/admin/AdminAttendanceView'
import ManageUsers from '@/components/admin/ManageUsers'

/**
 * AdminDashboard - Main admin portal
 * Provides access to all school management features
 */
function AdminDashboard() {
  const location = useLocation()

  // Navigation items for admin
  const navItems = [
    {
      name: 'Attendance Reports',
      path: '/admin/attendance',
      icon: FileText,
      color: 'bg-blue-100 text-blue-600',
      description: 'View and export attendance reports'
    },
    {
      name: 'Manage Users',
      path: '/admin/users',
      icon: Users,
      color: 'bg-purple-100 text-purple-600',
      description: 'Manage teachers and students'
    }
  ]

  const isActive = (path) => location.pathname === path

  return (
    <div className="min-h-screen bg-gray-50">
      <Header title="Admin Dashboard" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Navigation Cards */}
        {location.pathname === '/admin' && (
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
                        {item.description}
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
          <Route path="/attendance" element={<AdminAttendanceView />} />
          <Route path="/users" element={<ManageUsers />} />
          <Route path="/" element={null} />
        </Routes>
      </div>
    </div>
  )
}

export default AdminDashboard
