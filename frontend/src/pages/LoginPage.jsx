import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { LogIn, AlertCircle, Loader } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import { showError, showSuccess } from '@/utils/toast'
import { isValidEmail } from '@/utils/helpers'

/**
 * LoginPage - Secure login page for all users
 * Supports teacher, student, and admin login
 */
function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState({})
  const { login } = useAuth()
  const navigate = useNavigate()

  /**
   * Validate login form
   * @returns {boolean} Is form valid
   */
  const validateForm = () => {
    const newErrors = {}

    if (!email.trim()) {
      newErrors.email = 'Email is required'
    } else if (!isValidEmail(email)) {
      newErrors.email = 'Please enter a valid email'
    }

    if (!password) {
      newErrors.password = 'Password is required'
    } else if (password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  /**
   * Handle login form submission
   */
  const handleSubmit = async (e) => {
    e.preventDefault()

    // Validate form first
    if (!validateForm()) {
      showError('Please fix the errors below')
      return
    }

    setLoading(true)
    try {
      const result = await login(email, password)

      if (result.success) {
        showSuccess(`Welcome, ${result.user.name}!`)
        // Navigation will be handled by RootRedirect component
        setTimeout(() => {
          navigate('/')
        }, 1000)
      } else {
        showError(result.error || 'Login failed')
      }
    } catch (err) {
      showError('An unexpected error occurred')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-600 to-blue-800 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-white rounded-full mb-4">
            <LogIn className="text-blue-600" size={32} />
          </div>
          <h1 className="text-4xl font-bold text-white mb-2">MySchoolHelper</h1>
          <p className="text-blue-100">Attendance Management System</p>
        </div>

        {/* Login Form Card */}
        <div className="card shadow-2xl">
          <form onSubmit={handleSubmit} noValidate>
            {/* Email Field */}
            <div className="mb-6">
              <label className="label">Email Address</label>
              <input
                type="email"
                className="input-field"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value)
                  if (errors.email) setErrors({ ...errors, email: '' })
                }}
                disabled={loading}
              />
              {errors.email && (
                <div className="error-text flex items-center mt-2">
                  <AlertCircle size={16} className="mr-1" />
                  {errors.email}
                </div>
              )}
            </div>

            {/* Password Field */}
            <div className="mb-6">
              <label className="label">Password</label>
              <input
                type="password"
                className="input-field"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value)
                  if (errors.password) setErrors({ ...errors, password: '' })
                }}
                disabled={loading}
              />
              {errors.password && (
                <div className="error-text flex items-center mt-2">
                  <AlertCircle size={16} className="mr-1" />
                  {errors.password}
                </div>
              )}
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full btn-primary flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <>
                  <Loader className="animate-spin mr-2" size={20} />
                  Logging in...
                </>
              ) : (
                <>
                  <LogIn className="mr-2" size={20} />
                  Login
                </>
              )}
            </button>
          </form>

          {/* Demo Credentials */}
          <div className="mt-8 pt-6 border-t border-gray-200">
            <p className="text-sm font-medium text-gray-600 mb-3">Demo Credentials:</p>
            <div className="space-y-2 text-xs text-gray-600">
              <p>
                <span className="font-semibold text-blue-600">Teacher:</span> teacher@school.com / password123
              </p>
              <p>
                <span className="font-semibold text-blue-600">Student:</span> student@school.com / password123
              </p>
              <p>
                <span className="font-semibold text-blue-600">Admin:</span> admin@school.com / password123
              </p>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="text-center mt-6 text-blue-100 text-sm">
          <p>&copy; 2024 MySchoolHelper. All rights reserved.</p>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
