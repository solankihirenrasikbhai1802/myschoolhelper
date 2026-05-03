import React, { useState, useEffect } from 'react'
import { AlertCircle, Loader, Download, Search } from 'lucide-react'
import api from '@/utils/api'
import { showError, showSuccess } from '@/utils/toast'
import { formatDate, getStatusColor } from '@/utils/helpers'

/**
 * AdminAttendanceView - Admin panel for viewing and exporting attendance reports
 * Features:
 * - Search by student, class, date
 * - View all attendance records
 * - Export comprehensive reports
 * - Filter by date range and class
 */
function AdminAttendanceView() {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [classes, setClasses] = useState([])
  
  // Filter states
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedClass, setSelectedClass] = useState('')
  const [fromDate, setFromDate] = useState('')
  const [toDate, setToDate] = useState('')

  /**
   * Fetch classes and records on component mount
   */
  useEffect(() => {
    fetchClasses()
    fetchRecords()
  }, [])

  /**
   * Re-fetch records when filters change
   */
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchRecords()
    }, 500)
    return () => clearTimeout(timer)
  }, [searchQuery, selectedClass, fromDate, toDate])

  /**
   * Fetch all classes
   */
  const fetchClasses = async () => {
    try {
      const response = await api.get('/classes')
      setClasses(response.data)
    } catch (error) {
      showError('Failed to fetch classes')
    }
  }

  /**
   * Fetch attendance records with filters
   */
  const fetchRecords = async () => {
    try {
      setLoading(true)
      const response = await api.get('/admin/attendance/reports', {
        params: {
          search: searchQuery,
          classId: selectedClass,
          fromDate,
          toDate
        }
      })
      setRecords(response.data)
    } catch (error) {
      showError('Failed to fetch attendance records')
    } finally {
      setLoading(false)
    }
  }

  /**
   * Export attendance report as CSV
   */
  const handleExportReport = () => {
    if (records.length === 0) {
      showError('No records to export')
      return
    }

    let csv = 'Date,Student Name,Class,Status,Teacher,Remarks\n'
    
    records.forEach(record => {
      csv += `${formatDate(record.date)},${record.student_id.name},${record.student_id.class_id?.name || 'N/A'},${record.status},${record.marked_by?.name || 'N/A'},${record.remarks || ''}\n`
    })

    const element = document.createElement('a')
    element.setAttribute('href', 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv))
    element.setAttribute('download', `attendance-report-${new Date().toISOString()}.csv`)
    element.style.display = 'none'
    document.body.appendChild(element)
    element.click()
    document.body.removeChild(element)

    showSuccess('Report exported successfully')
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Attendance Reports</h1>
        <p className="text-gray-600">View and export school-wide attendance records</p>
      </div>

      {/* Search and Filters */}
      <div className="card space-y-4">
        {/* Search Bar */}
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-3 text-gray-400" size={20} />
            <input
              type="text"
              placeholder="Search by student name or email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input-field pl-10"
            />
          </div>
          <button
            onClick={handleExportReport}
            disabled={records.length === 0}
            className="btn-success flex items-center disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Download className="mr-2" size={18} />
            Export
          </button>
        </div>

        {/* Advanced Filters */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Class Selection */}
          <div>
            <label className="label">Class</label>
            <select
              value={selectedClass}
              onChange={(e) => setSelectedClass(e.target.value)}
              className="input-field"
            >
              <option value="">All Classes</option>
              {classes.map(cls => (
                <option key={cls._id} value={cls._id}>
                  Class {cls.name}
                </option>
              ))}
            </select>
          </div>

          {/* From Date */}
          <div>
            <label className="label">From Date</label>
            <input
              type="date"
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
              className="input-field"
            />
          </div>

          {/* To Date */}
          <div>
            <label className="label">To Date</label>
            <input
              type="date"
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
              className="input-field"
            />
          </div>

          {/* Results Count */}
          <div className="flex items-end">
            <div className="text-sm text-gray-600">
              Found <span className="font-semibold text-blue-600">{records.length}</span> records
            </div>
          </div>
        </div>
      </div>

      {/* Records Table */}
      <div className="card">
        {loading ? (
          <div className="flex items-center justify-center py-8">
            <Loader className="animate-spin mr-2" size={20} />
            <span className="text-gray-600">Loading records...</span>
          </div>
        ) : records.length === 0 ? (
          <div className="text-center py-8">
            <AlertCircle className="mx-auto mb-2 text-gray-400" size={32} />
            <p className="text-gray-600">No attendance records found</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b-2 border-gray-200 bg-gray-50">
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Date</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Student Name</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Class</th>
                  <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">Status</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Teacher</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Remarks</th>
                </tr>
              </thead>
              <tbody>
                {records.map(record => (
                  <tr key={record._id} className="border-b border-gray-200 hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 text-sm text-gray-900 font-medium">
                      {formatDate(record.date)}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900">
                      {record.student_id?.name || 'N/A'}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">
                      Class {record.student_id?.class_id?.name || 'N/A'}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex justify-center">
                        <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(record.status)}`}>
                          {record.status}
                        </span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">
                      {record.marked_by?.name || 'N/A'}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">
                      {record.remarks || '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Summary Stats */}
        {records.length > 0 && (
          <div className="mt-6 pt-6 border-t border-gray-200">
            <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
              <div className="bg-blue-50 p-4 rounded-lg">
                <p className="text-xs text-gray-600 mb-1">Total Records</p>
                <p className="text-2xl font-bold text-blue-600">{records.length}</p>
              </div>
              <div className="bg-green-50 p-4 rounded-lg">
                <p className="text-xs text-gray-600 mb-1">Present</p>
                <p className="text-2xl font-bold text-green-600">
                  {records.filter(r => r.status === 'Present').length}
                </p>
              </div>
              <div className="bg-red-50 p-4 rounded-lg">
                <p className="text-xs text-gray-600 mb-1">Absent</p>
                <p className="text-2xl font-bold text-red-600">
                  {records.filter(r => r.status === 'Absent').length}
                </p>
              </div>
              <div className="bg-yellow-50 p-4 rounded-lg">
                <p className="text-xs text-gray-600 mb-1">Late</p>
                <p className="text-2xl font-bold text-yellow-600">
                  {records.filter(r => r.status === 'Late').length}
                </p>
              </div>
              <div className="bg-purple-50 p-4 rounded-lg">
                <p className="text-xs text-gray-600 mb-1">Leave</p>
                <p className="text-2xl font-bold text-purple-600">
                  {records.filter(r => r.status === 'Leave').length}
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminAttendanceView
