import React, { useState, useEffect } from 'react'
import { Loader, Trash2, Edit2, Plus, Search, AlertCircle } from 'lucide-react'
import api from '@/utils/api'
import { showError, showSuccess } from '@/utils/toast'

/**
 * ManageUsers component - Admin panel for managing teachers and students
 * Features:
 * - View all users (teachers and students)
 * - Add new users
 * - Edit user information
 * - Delete users
 * - Filter by role
 */
function ManageUsers() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [filterRole, setFilterRole] = useState('')
  const [showAddForm, setShowAddForm] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'teacher'
  })

  /**
   * Fetch all users on component mount
   */
  useEffect(() => {
    fetchUsers()
  }, [])

  /**
   * Fetch users from backend
   */
  const fetchUsers = async () => {
    try {
      setLoading(true)
      const response = await api.get('/admin/users')
      setUsers(response.data)
    } catch (error) {
      showError('Failed to fetch users')
    } finally {
      setLoading(false)
    }
  }

  /**
   * Filter users based on search and role
   */
  const filteredUsers = users.filter(user => {
    const matchesSearch = user.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         user.email.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesRole = !filterRole || user.role === filterRole
    return matchesSearch && matchesRole
  })

  /**
   * Handle form input change
   */
  const handleFormChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  /**
   * Handle add/update user
   */
  const handleSubmitForm = async (e) => {
    e.preventDefault()

    // Validate form
    if (!formData.name || !formData.email || (!editingUser && !formData.password)) {
      showError('Please fill all required fields')
      return
    }

    try {
      if (editingUser) {
        // Update user
        await api.put(`/admin/users/${editingUser._id}`, {
          name: formData.name,
          email: formData.email,
          role: formData.role,
          ...(formData.password && { password: formData.password })
        })
        showSuccess('User updated successfully')
      } else {
        // Add new user
        await api.post('/admin/users', formData)
        showSuccess('User added successfully')
      }

      // Reset form and refresh list
      setFormData({ name: '', email: '', password: '', role: 'teacher' })
      setEditingUser(null)
      setShowAddForm(false)
      fetchUsers()
    } catch (error) {
      const message = error.response?.data?.message || 'Failed to save user'
      showError(message)
    }
  }

  /**
   * Handle delete user
   */
  const handleDeleteUser = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await api.delete(`/admin/users/${userId}`)
        showSuccess('User deleted successfully')
        fetchUsers()
      } catch (error) {
        showError('Failed to delete user')
      }
    }
  }

  /**
   * Handle edit user
   */
  const handleEditUser = (user) => {
    setEditingUser(user)
    setFormData({
      name: user.name,
      email: user.email,
      password: '',
      role: user.role
    })
    setShowAddForm(true)
  }

  /**
   * Reset form
   */
  const resetForm = () => {
    setFormData({ name: '', email: '', password: '', role: 'teacher' })
    setEditingUser(null)
    setShowAddForm(false)
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Manage Users</h1>
          <p className="text-gray-600">Add, edit, or remove teachers and students</p>
        </div>
        <button
          onClick={() => !showAddForm ? setShowAddForm(true) : resetForm()}
          className="btn-primary flex items-center"
        >
          <Plus className="mr-2" size={20} />
          {showAddForm ? 'Cancel' : 'Add New User'}
        </button>
      </div>

      {/* Add/Edit Form */}
      {showAddForm && (
        <div className="card bg-blue-50 border border-blue-200">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            {editingUser ? 'Edit User' : 'Add New User'}
          </h2>
          <form onSubmit={handleSubmitForm} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Name */}
            <div>
              <label className="label">Full Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleFormChange}
                className="input-field"
                placeholder="Enter full name"
                required
              />
            </div>

            {/* Email */}
            <div>
              <label className="label">Email Address *</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleFormChange}
                className="input-field"
                placeholder="Enter email"
                required
              />
            </div>

            {/* Password */}
            <div>
              <label className="label">
                Password {editingUser ? '(leave blank to keep current)' : '*'}
              </label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleFormChange}
                className="input-field"
                placeholder="Enter password"
                required={!editingUser}
              />
            </div>

            {/* Role */}
            <div>
              <label className="label">Role *</label>
              <select
                name="role"
                value={formData.role}
                onChange={handleFormChange}
                className="input-field"
              >
                <option value="teacher">Teacher</option>
                <option value="student">Student</option>
                <option value="admin">Admin</option>
              </select>
            </div>

            {/* Buttons */}
            <div className="md:col-span-2 flex gap-2 justify-end">
              <button
                type="button"
                onClick={resetForm}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn-primary"
              >
                {editingUser ? 'Update User' : 'Add User'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Filters */}
      <div className="card space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Search */}
          <div>
            <div className="relative">
              <Search className="absolute left-3 top-3 text-gray-400" size={20} />
              <input
                type="text"
                placeholder="Search by name or email..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="input-field pl-10"
              />
            </div>
          </div>

          {/* Role Filter */}
          <div>
            <select
              value={filterRole}
              onChange={(e) => setFilterRole(e.target.value)}
              className="input-field"
            >
              <option value="">All Roles</option>
              <option value="teacher">Teachers</option>
              <option value="student">Students</option>
              <option value="admin">Admins</option>
            </select>
          </div>
        </div>

        {/* Results Count */}
        <div className="text-sm text-gray-600">
          Found <span className="font-semibold text-blue-600">{filteredUsers.length}</span> users
        </div>
      </div>

      {/* Users Table */}
      <div className="card">
        {loading ? (
          <div className="flex items-center justify-center py-8">
            <Loader className="animate-spin mr-2" size={20} />
            <span className="text-gray-600">Loading users...</span>
          </div>
        ) : filteredUsers.length === 0 ? (
          <div className="text-center py-8">
            <AlertCircle className="mx-auto mb-2 text-gray-400" size={32} />
            <p className="text-gray-600">No users found</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b-2 border-gray-200 bg-gray-50">
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Name</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">Email</th>
                  <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">Role</th>
                  <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map(user => (
                  <tr key={user._id} className="border-b border-gray-200 hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 text-sm font-medium text-gray-900">{user.name}</td>
                    <td className="px-4 py-3 text-sm text-gray-600">{user.email}</td>
                    <td className="px-4 py-3">
                      <div className="flex justify-center">
                        <span className="px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800 capitalize">
                          {user.role}
                        </span>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex justify-center gap-2">
                        <button
                          onClick={() => handleEditUser(user)}
                          className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Edit user"
                        >
                          <Edit2 size={18} />
                        </button>
                        <button
                          onClick={() => handleDeleteUser(user._id)}
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          title="Delete user"
                        >
                          <Trash2 size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default ManageUsers
