import React, { useState, useEffect } from 'react'
import { AlertCircle, Loader, Download, Calendar } from 'lucide-react'
import api from '@/utils/api'
import { showError } from '@/utils/toast'
import { formatDate, getStatusColor } from '@/utils/helpers'

/**
 * ViewAttendance component - Display attendance records for teachers
 * Features:
 * - Filter by date range, class, and section
 * - View attendance history
 * - Export attendance reports
 */
function ViewAttendance() {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [classes, setClasses] = useState([])
  const [sections, setSections] = useState([])
  
  // Filter states
  const [selectedClass, setSelectedClass] = useState('')
  const [selectedSection, setSelectedSection] = useState('')
  const [fromDate, setFromDate] = useState('')
  const [toDate, setToDate] = useState('')

  /**
   * Fetch classes on component mount
   */
  useEffect(() => {
    fetchClasses()
  }, [])

  /**
   * Fetch sections when class changes
   */
  useEffect(() => {
    if (selectedClass) {
      fetchSections(selectedClass)
    }
  }, [selectedClass])

  /**
   * Fetch records when filters change
   */
  useEffect(() => {
    if (selectedClass && selectedSection) {
      fetchRecords()
    }
  }, [selectedClass, selectedSection, fromDate, toDate])

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
   * Fetch sections for selected class
   */
  const fetchSections = async (classId) => {
    try {
      const response = await api.get(`/classes/${classId}/sections`)
      setSections(response.data)
      setRecords([])
    } catch (error) {
      showError('Failed to fetch sections')
    }
  }

  /**
   * Fetch attendance records with filters
   */
  const fetchRecords = async () => {
    try {
      setLoading(true)
      const response = await api.get('/attendance/view', {
        params: {
          classId: selectedClass,
          sectionId: selectedSection,
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
   * Export attendance as CSV
   */
  const handleExport = () => {
    if (records.length === 0) {
      showError('No records to export')
      return
    }

    let csv = 'Date,Student Name,Status,Remarks\n'
    
    records.forEach(record => {
      csv += `${formatDate(record.date)},${record.student_id.name},${record.status},${record.remarks || ''}\n`
    })

    const element = document.createElement('a')
    element.setAttribute('href', 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv))
    element.setAttribute('download', `attendance-${new Date().toISOString()}.csv`)
    element.style.display = 'none'
    document.body.appendChild(element)
    element.click()
    document.body.removeChild(element)
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">View Attendance</h1>
        <p className="text-gray-600">Review attendance records for your classes</p>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          {/* Class Selection */}
          <div>
            <label className="label">Class</label>
            <select
              value={selectedClass}
              onChange={(e) => {
                setSelectedClass(e.target.value)
                setSelectedSection('')
                setRecords([])
              }}
              className="input-field"
            >
              <option value="">Select Class</option>
              {classes.map(cls => (
                <option key={cls._id} value={cls._id}>
                  Class {cls.name}
                </option>
              ))}
            </select>
          </div>

          {/* Section Selection */}
          <div>
            <label className="label">Section</label>
            <select
              value={selectedSection}
              onChange={(e) => {
                setSelectedSection(e.target.value)
                setRecords([])
              }}
              className="input-field"
              disabled={!selectedClass}
            >
              <option value="">Select Section</option>
              {sections.map(section => (
                <option key={section._id} value={section._id}>
                  {section.name}
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

          {/* Export Button */}
          <div className="flex items-end">
            <button
              onClick={handleExport}
              disabled={records.length === 0 || !selectedClass || !selectedSection}
              className="btn-success flex items-center disabled:opacity-50 disabled:cursor-not-allowed w-full justify-center"
            >
              <Download className="mr-2" size={18} />
              Export
            </button>
          </div>
        </div>
      </div>

      {/* Records Table */}
      {selectedClass && selectedSection && (
        <div className="card">
          {loading ? (
            <div className="flex items-center justify-center py-8">
              <Loader className="animate-spin mr-2" size={20} />
              <span className="text-gray-600">Loading records...</span>
            </div>
          ) : records.length === 0 ? (
            <div className="text-center py-8">
              <Calendar className="mx-auto mb-2 text-gray-400" size={32} />
              <p className="text-gray-600">No attendance records found</p>
              <p className="text-sm text-gray-500 mt-1">Try adjusting your filters</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b-2 border-gray-200 bg-gray-50">
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Date</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Student Name</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">Status</th>
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
                        {record.student_id.name}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex justify-center">
                          <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(record.status)}`}>
                            {record.status}
                          </span>
                        </div>
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
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
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
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default ViewAttendance
