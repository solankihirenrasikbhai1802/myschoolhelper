import React, { useState, useEffect } from 'react'
import { AlertCircle, Loader, Calendar, Download } from 'lucide-react'
import api from '@/utils/api'
import { showError, showSuccess } from '@/utils/toast'
import { formatDate, getStatusColor } from '@/utils/helpers'

/**
 * StudentAttendanceView - Display student's attendance records
 * Features:
 * - View monthly attendance history
 * - See present/absent/late records
 * - Filter by month
 * - Download attendance report
 */
function StudentAttendanceView() {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedMonth, setSelectedMonth] = useState(new Date().toISOString().slice(0, 7))
  const [monthStats, setMonthStats] = useState({
    present: 0,
    absent: 0,
    late: 0,
    total: 0
  })

  /**
   * Fetch attendance records on component mount and when month changes
   */
  useEffect(() => {
    fetchRecords()
  }, [selectedMonth])

  /**
   * Fetch student's attendance records for selected month
   */
  const fetchRecords = async () => {
    try {
      setLoading(true)
      const response = await api.get('/student/attendance/records', {
        params: {
          month: selectedMonth
        }
      })
      
      setRecords(response.data.records)
      setMonthStats(response.data.stats)
    } catch (error) {
      showError('Failed to fetch attendance records')
    } finally {
      setLoading(false)
    }
  }

  /**
   * Download attendance report for selected month
   */
  const handleDownloadReport = () => {
    if (records.length === 0) {
      showError('No records to download')
      return
    }

    let csv = 'Date,Status,Remarks\n'
    
    records.forEach(record => {
      csv += `${formatDate(record.date)},${record.status},${record.remarks || ''}\n`
    })

    csv += `\nSummary for ${selectedMonth}\n`
    csv += `Present: ${monthStats.present}\n`
    csv += `Absent: ${monthStats.absent}\n`
    csv += `Late: ${monthStats.late}\n`
    csv += `Total Days: ${monthStats.total}\n`

    const element = document.createElement('a')
    element.setAttribute('href', 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv))
    element.setAttribute('download', `attendance-${selectedMonth}.csv`)
    element.style.display = 'none'
    document.body.appendChild(element)
    element.click()
    document.body.removeChild(element)

    showSuccess('Report downloaded successfully')
  }

  return (
    <div className="space-y-6">
      {/* Heading and Filters */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Attendance History</h2>
          <p className="text-gray-600 text-sm mt-1">View your monthly attendance records</p>
        </div>

        <div className="flex gap-2">
          <input
            type="month"
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(e.target.value)}
            className="input-field max-w-xs"
          />
          <button
            onClick={handleDownloadReport}
            disabled={records.length === 0}
            className="btn-success flex items-center disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Download className="mr-2" size={18} />
            Download
          </button>
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
            <Calendar className="mx-auto mb-2 text-gray-400" size={32} />
            <p className="text-gray-600">No attendance records found for {selectedMonth}</p>
          </div>
        ) : (
          <div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b-2 border-gray-200 bg-gray-50">
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Date</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">Status</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Remarks</th>
                  </tr>
                </thead>
                <tbody>
                  {records.map(record => (
                    <tr key={record._id} className="border-b border-gray-200 hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">
                        {formatDate(record.date)}
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

            {/* Monthly Summary */}
            <div className="mt-6 pt-6 border-t border-gray-200">
              <h3 className="text-sm font-semibold text-gray-900 mb-4">Monthly Summary</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="bg-green-50 p-4 rounded-lg border border-green-200">
                  <p className="text-xs text-gray-600 mb-1">Present</p>
                  <p className="text-2xl font-bold text-green-600">{monthStats.present}</p>
                </div>
                <div className="bg-red-50 p-4 rounded-lg border border-red-200">
                  <p className="text-xs text-gray-600 mb-1">Absent</p>
                  <p className="text-2xl font-bold text-red-600">{monthStats.absent}</p>
                </div>
                <div className="bg-yellow-50 p-4 rounded-lg border border-yellow-200">
                  <p className="text-xs text-gray-600 mb-1">Late</p>
                  <p className="text-2xl font-bold text-yellow-600">{monthStats.late}</p>
                </div>
                <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
                  <p className="text-xs text-gray-600 mb-1">Total Days</p>
                  <p className="text-2xl font-bold text-blue-600">{monthStats.total}</p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Attendance Tips */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex gap-3">
          <AlertCircle className="text-blue-600 flex-shrink-0 mt-0.5" size={20} />
          <div>
            <h4 className="font-semibold text-blue-900 mb-1">Attendance Reminder</h4>
            <p className="text-sm text-blue-800">
              Maintaining good attendance is important for your academic success. 
              Current school policy requires at least 75% attendance. Contact your class teacher if you have questions about your attendance.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default StudentAttendanceView
