import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Menu, X, LogOut, User, BarChart3 } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import { showSuccess } from '@/utils/toast'

/**
 * Header component - Navigation bar with user menu
 * Used across all dashboard pages
 */
function Header({ title = 'Dashboard' }) {
  const [menuOpen, setMenuOpen] = useState(false)
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  /**
   * Handle logout
   */
  const handleLogout = () => {
    logout()
    showSuccess('Logged out successfully')
    navigate('/login')
    setMenuOpen(false)
  }

  return (
    <header className="bg-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo/Title */}
          <div className="flex items-center">
            <div className="inline-flex items-center justify-center w-10 h-10 bg-blue-600 rounded-lg mr-3">
              <BarChart3 className="text-white" size={24} />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-900">MySchoolHelper</h1>
              <p className="text-xs text-gray-600">{title}</p>
            </div>
          </div>

          {/* User Info & Menu */}
          <div className="relative">
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="flex items-center gap-2 text-gray-700 hover:text-blue-600 transition-colors"
            >
              <div className="hidden sm:block text-right">
                <p className="text-sm font-medium">{user?.name || 'User'}</p>
                <p className="text-xs text-gray-500 capitalize">{user?.role || 'Unknown'}</p>
              </div>
              {menuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>

            {/* Dropdown Menu */}
            {menuOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl py-2">
                <div className="px-4 py-2 border-b border-gray-200">
                  <p className="text-sm font-medium text-gray-900">{user?.name}</p>
                  <p className="text-xs text-gray-500">{user?.email}</p>
                </div>

                <button
                  onClick={() => {
                    navigate(`/${user?.role}/profile`)
                    setMenuOpen(false)
                  }}
                  className="w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-100 flex items-center gap-2 transition-colors"
                >
                  <User size={18} />
                  <span>My Profile</span>
                </button>

                <button
                  onClick={handleLogout}
                  className="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 flex items-center gap-2 transition-colors border-t border-gray-200"
                >
                  <LogOut size={18} />
                  <span>Logout</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  )
}

export default Header
