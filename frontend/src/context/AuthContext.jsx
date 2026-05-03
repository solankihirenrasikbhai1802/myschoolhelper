import React, { createContext, useState, useEffect } from 'react'
import api from '@/utils/api'

// Create Auth Context
export const AuthContext = createContext(null)

/**
 * AuthProvider component - manages authentication state
 * Provides user info, login, logout, and role-based access throughout the app
 */
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Load user from localStorage on mount
  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    const token = localStorage.getItem('token')
    
    if (storedUser && token) {
      try {
        setUser(JSON.parse(storedUser))
      } catch (err) {
        localStorage.removeItem('user')
        localStorage.removeItem('token')
      }
    }
    setLoading(false)
  }, [])

  /**
   * Login function
   * @param {string} email - User email
   * @param {string} password - User password
   * @returns {Promise} Login result
   */
  const login = async (email, password) => {
    try {
      setError(null)
      const response = await api.post('/auth/login', { email, password })
      
      const { user, token } = response.data
      
      // Normalize role to lowercase for consistency
      if (user.role) {
        user.role = user.role.toLowerCase()
      }
      
      // Store token and user info
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      
      setUser(user)
      return { success: true, user }
    } catch (err) {
      const message = err.response?.data?.message || 'Login failed'
      setError(message)
      return { success: false, error: message }
    }
  }

  /**
   * Logout function
   */
  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  /**
   * Check if user has specific role
   * @param {string|string[]} role - Role(s) to check
   * @returns {boolean} Has role
   */
  const hasRole = (role) => {
    if (!user) return false
    if (typeof role === 'string') {
      return user.role === role
    }
    return role.includes(user.role)
  }

  /**
   * Update user info
   * @param {object} updatedUser - Updated user data
   */
  const updateUser = (updatedUser) => {
    const newUser = { ...user, ...updatedUser }
    setUser(newUser)
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      error,
      login,
      logout,
      hasRole,
      updateUser,
      isAuthenticated: !!user
    }}>
      {children}
    </AuthContext.Provider>
  )
}

/**
 * Custom hook to use Auth Context
 * @returns {object} Auth context value
 */
export const useAuth = () => {
  const context = React.useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
