import React, { useState, useEffect } from 'react'
import { AlertCircle, CheckCircle, XCircle, Clock, Loader, Save } from 'lucide-react'
import api from '@/utils/api'
import { showSuccess, showError, showLoading, updateToastSuccess, updateToastError } from '@/utils/toast'
import { formatDateForAPI, getStatusColor } from '@/utils/helpers'

/**
 * MarkAttendance component - Allows teachers to mark student attendance
 * Features:
 * - Select class and section
 * - View student list
 * - Mark attendance as Present/Absent/Late
 * - Submit attendance with duplicate prevention
 * - Edit existing attendance
 */
function MarkAttendance() {
  const [classes, setClasses] = useState([])
  const [sections, setSections] = useState([])
  const [students, setStudents] = useState([])
  const [selectedClass, setSelectedClass] = useState('')
  const [selectedSection, setSelectedSection] = useState('')
  const [selectedDate, setSelectedDate] = useState(formatDateForAPI(new Date()))
  const [attendance, setAttendance] = useState({})
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [existingAttendance, setExistingAttendance] = useState(null)

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
   * Fetch students and check for existing attendance when class/section changes
   */
  useEffect(() => {
    if (selectedClass && selectedSection) {
      fetchStudents()
      checkExistingAttendance()
    }
  }, [selectedClass, selectedSection, selectedDate])

  /**
   * Fetch all classes from backend
   */
  const fetchClasses = async () => {
    try {
      const response = await api.get('/classes')
      setClasses(response.data)
    } catch (error) {
      showError('Failed to fetch classes')
      console.error(error)
    }
  }

  /**
   * Fetch sections for selected class
   */
  const fetchSections = async (classId) => {
    try {
      const response = await api.get(`/classes/${classId}/sections`)
      setSections(response.data)
      setStudents([])
      setAttendance({})
    } catch (error) {
      showError('Failed to fetch sections')
      console.error(error)
    }
  }

  /**
   * Fetch students for selected class and section
   */
  const fetchStudents = async () => {
    try {
      setLoading(true)
      const response = await api.get('/students', {
        params: {
          classId: selectedClass,
          sectionId: selectedSection
        }
      })
      setStudents(response.data)
      
      // Initialize attendance with empty values
      const initialAttendance = {}
      response.data.forEach(student => {
        initialAttendance[student._id] = 'Present' // Default to Present
      })
      setAttendance(initialAttendance)
    } catch (error) {
      showError('Failed to fetch students')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  /**
   * Check if attendance already exists for this date
   */
  const checkExistingAttendance = async () => {
    try {
      const response = await api.get('/attendance/check-existing', {
        params: {
          classId: selectedClass,
          sectionId: selectedSection,
          date: selectedDate
        }
      })
      
      if (response.data.exists) {
        setExistingAttendance(response.data.attendance)
        // Load existing attendance data
        const existingData = {}
        response.data.attendance.forEach(record => {
          existingData[record.student_id._id] = record.status
        })
        setAttendance(existingData)
      } else {
        setExistingAttendance(null)
      }
    } catch (error) {
      // If attendance check fails, continue with fresh form
      setExistingAttendance(null)
    }
  }

  /**
   * Handle attendance status change for a student
   */
  const handleAttendanceChange = (studentId, status) => {
    setAttendance(prev => ({
      ...prev,
      [studentId]: status
    }))
  }

  /**
   * Submit attendance records
   */
  const handleSubmit = async () => {
    if (!selectedClass || !selectedSection) {
      showError('Please select class and section')
      return
    }

    const toastId = showLoading('Submitting attendance...')

    try {
      setSubmitting(true)

      // Prepare attendance data
      const attendanceRecords = students.map(student => ({
        student_id: student._id,
        date: selectedDate,
        status: attendance[student._id] || 'Present',
        classId: selectedClass,
        sectionId: selectedSection
      }))

      // Call API to submit attendance
      const endpoint = existingAttendance 
        ? '/attendance/update' 
        : '/attendance/mark'
      
      const response = await api.post(endpoint, {
        records: attendanceRecords,
        date: selectedDate,
        classId: selectedClass,
        sectionId: selectedSection
      })

      updateToastSuccess(toastId, `Attendance submitted successfully for ${students.length} students`)
      
      // Refresh existing attendance check
      checkExistingAttendance()
    } catch (error) {
      const message = error.response?.data?.message || 'Failed to submit attendance'
      updateToastError(toastId, message)
      console.error(error)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Mark Attendance</h1>
        <p className="text-gray-600">
          {existingAttendance ? '✓ Edit existing attendance' : 'Mark new attendance for students'}
        </p>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Date Selection */}
          <div>
            <label className="label">Date</label>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="input-field"
            />
          </div>

          {/* Class Selection */}
          <div>
            <label className="label">Class</label>
            <select
              value={selectedClass}
              onChange={(e) => {
                setSelectedClass(e.target.value)
                setSelectedSection('')
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
              onChange={(e) => setSelectedSection(e.target.value)}
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

          {/* Info Alert */}
          <div className="flex items-end">
            {selectedClass && selectedSection && (
              <div className="flex items-center gap-2 text-blue-600 text-sm">
                <CheckCircle size={18} />
                <span>Ready to mark</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Students Attendance Table */}
      {selectedClass && selectedSection && (
        <div className="card">
          {loading ? (
            <div className="flex items-center justify-center py-8">
              <Loader className="animate-spin mr-2" size={20} />
              <span className="text-gray-600">Loading students...</span>
            </div>
          ) : students.length === 0 ? (
            <div className="text-center py-8">
              <AlertCircle className="mx-auto mb-2 text-yellow-600" size={32} />
              <p className="text-gray-600">No students found in this class</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b-2 border-gray-200 bg-gray-50">
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Roll No.</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Student Name</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">Attendance Status</th>
                  </tr>
                </thead>
                <tbody>
                  {students.map((student, index) => (
                    <tr key={student._id} className="border-b border-gray-200 hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 text-sm text-gray-600 font-medium">{index + 1}</td>
                      <td className="px-4 py-3 text-sm text-gray-900">{student.name}</td>
                      <td className="px-4 py-3">
                        <div className="flex justify-center gap-2 flex-wrap">
                          {['Present', 'Absent', 'Late', 'Leave'].map(status => (
                            <button
                              key={status}
                              onClick={() => handleAttendanceChange(student._id, status)}
                              className={`px-3 py-1 rounded-full text-xs font-medium transition-all ${
                                attendance[student._id] === status
                                  ? `${getStatusColor(status)} ring-2 ring-offset-2 ring-blue-500`
                                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                              }`}
                            >
                              {status === 'Present' && <CheckCircle className="inline mr-1" size={14} />}
                              {status === 'Absent' && <XCircle className="inline mr-1" size={14} />}
                              {status === 'Late' && <Clock className="inline mr-1" size={14} />}
                              {status}
                            </button>
                          ))}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Submit Button */}
          {students.length > 0 && (
            <div className="mt-6 flex gap-4 justify-end">
              <button
                onClick={handleSubmit}
                disabled={submitting || !selectedClass || !selectedSection}
                className="btn-success flex items-center disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {submitting ? (
                  <>
                    <Loader className="animate-spin mr-2" size={20} />
                    Submitting...
                  </>
                ) : (
                  <>
                    <Save className="mr-2" size={20} />
                    {existingAttendance ? 'Update Attendance' : 'Submit Attendance'}
                  </>
                )}
              </button>
            </div>
          )}

          {/* Status Message */}
          {existingAttendance && (
            <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg text-blue-800 text-sm">
              <CheckCircle className="inline mr-2" size={16} />
              Attendance already marked. Click Submit to update.
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default MarkAttendance
