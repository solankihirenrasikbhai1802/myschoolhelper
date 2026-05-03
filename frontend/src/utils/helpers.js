/**
 * Format date to a readable string
 * @param {Date|string} date - Date to format
 * @returns {string} Formatted date
 */
export const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

/**
 * Format date for API submission (YYYY-MM-DD)
 * @param {Date|string} date - Date to format
 * @returns {string} Formatted date for API
 */
export const formatDateForAPI = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * Calculate attendance percentage
 * @param {number} present - Number of present days
 * @param {number} total - Total number of days
 * @returns {number} Attendance percentage
 */
export const calculateAttendancePercentage = (present, total) => {
  if (total === 0) return 0
  return Math.round((present / total) * 100)
}

/**
 * Get attendance status color
 * @param {string} status - Attendance status
 * @returns {string} Tailwind color class
 */
export const getStatusColor = (status) => {
  const colors = {
    'Present': 'bg-green-100 text-green-800',
    'Absent': 'bg-red-100 text-red-800',
    'Late': 'bg-yellow-100 text-yellow-800',
    'Leave': 'bg-blue-100 text-blue-800'
  }
  return colors[status] || 'bg-gray-100 text-gray-800'
}

/**
 * Get status badge color for icons
 * @param {string} status - Attendance status
 * @returns {string} Color class
 */
export const getStatusIconColor = (status) => {
  const colors = {
    'Present': 'text-green-600',
    'Absent': 'text-red-600',
    'Late': 'text-yellow-600',
    'Leave': 'text-blue-600'
  }
  return colors[status] || 'text-gray-600'
}

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} Is valid email
 */
export const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * Validate phone number format (Indian)
 * @param {string} phone - Phone number to validate
 * @returns {boolean} Is valid phone number
 */
export const isValidPhone = (phone) => {
  const phoneRegex = /^[6-9]\d{9}$/
  return phoneRegex.test(phone.replace(/\D/g, ''))
}
