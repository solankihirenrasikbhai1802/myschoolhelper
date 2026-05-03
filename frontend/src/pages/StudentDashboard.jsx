import React, { useState, useEffect } from 'react'
import { Routes, Route, useLocation } from 'react-router-dom'
import { BarChart3, Calendar, TrendingUp } from 'lucide-react'
import Header from '@/components/Header'
import StudentAttendanceView from '@/components/student/StudentAttendanceView'
import api from '@/utils/api'
import { showError } from '@/utils/toast'
import { useAuth } from '@/context/AuthContext'

/**
 * StudentDashboard - Main student portal
 * Shows personal attendance records and statistics
 */
function StudentDashboard() {
  const location = useLocation()
  const { user } = useAuth()
  const [stats, setStats] = useState({
    totalDays: 0,
    presentDays: 0,
    absentDays: 0,
    lateDays: 0,
    percentage: 0
  })
  const [loading, setLoading] = useState(true)

  /**
   * Fetch attendance statistics on component mount
   */
  useEffect(() => {
    fetchStats()
  }, [])

  /**
   * Fetch student's attendance statistics
   */
  const fetchStats = async () => {
    try {
      setLoading(true)
      const response = await api.get('/student/attendance/stats')
      setStats(response.data)
    } catch (error) {
      showError('Failed to fetch attendance statistics')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header title="Student Dashboard" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome, {user?.name}!</h1>
          <p className="text-gray-600">Your attendance information is below</p>
        </div>

        {/* Stats Cards */}
        {!loading && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
            {/* Attendance Percentage */}
            <div className="card bg-gradient-to-br from-blue-50 to-blue-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs text-gray-600 font-medium">Attendance %</p>
                  <p className="text-3xl font-bold text-blue-600 mt-2">{stats.percentage}%</p>
                </div>
                <TrendingUp className="text-blue-400" size={32} />
              </div>
            </div>

            {/* Total Days */}
            <div className="card bg-gradient-to-br from-purple-50 to-purple-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs text-gray-600 font-medium">Total Days</p>
                  <p className="text-3xl font-bold text-purple-600 mt-2">{stats.totalDays}</p>
                </div>
                <Calendar className="text-purple-400" size={32} />
              </div>
            </div>

            {/* Present Days */}
            <div className="card bg-gradient-to-br from-green-50 to-green-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs text-gray-600 font-medium">Present</p>
                  <p className="text-3xl font-bold text-green-600 mt-2">{stats.presentDays}</p>
                </div>
                <BarChart3 className="text-green-400" size={32} />
              </div>
            </div>

            {/* Absent Days */}
            <div className="card bg-gradient-to-br from-red-50 to-red-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs text-gray-600 font-medium">Absent</p>
                  <p className="text-3xl font-bold text-red-600 mt-2">{stats.absentDays}</p>
                </div>
                <BarChart3 className="text-red-400" size={32} />
              </div>
            </div>

            {/* Late Days */}
            <div className="card bg-gradient-to-br from-yellow-50 to-yellow-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs text-gray-600 font-medium">Late</p>
                  <p className="text-3xl font-bold text-yellow-600 mt-2">{stats.lateDays}</p>
                </div>
                <BarChart3 className="text-yellow-400" size={32} />
              </div>
            </div>
          </div>
        )}

        {/* Attendance Records */}
        <Routes>
          <Route path="/" element={<StudentAttendanceView />} />
        </Routes>
      </div>
    </div>
  )
}

export default StudentDashboard
