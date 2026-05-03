import React from 'react'
import { useNavigate } from 'react-router-dom'
import { AlertCircle, ArrowLeft } from 'lucide-react'

/**
 * NotFound - 404 page for invalid routes
 */
function NotFound() {
  const navigate = useNavigate()

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="text-center">
        <AlertCircle className="mx-auto text-red-600 mb-4" size={64} />
        <h1 className="text-4xl font-bold text-gray-900 mb-2">404</h1>
        <p className="text-xl text-gray-600 mb-6">Page Not Found</p>
        <p className="text-gray-500 mb-8">The page you're looking for doesn't exist.</p>
        
        <button
          onClick={() => navigate('/')}
          className="btn-primary inline-flex items-center"
        >
          <ArrowLeft className="mr-2" size={20} />
          Go Back Home
        </button>
      </div>
    </div>
  )
}

export default NotFound
