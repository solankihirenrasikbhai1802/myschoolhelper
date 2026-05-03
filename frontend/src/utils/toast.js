import toast from 'react-hot-toast'

/**
 * Display success notification
 * @param {string} message - Success message to display
 */
export const showSuccess = (message) => {
  toast.success(message, {
    duration: 3000,
    position: 'top-right',
    style: {
      background: '#10b981',
      color: '#fff'
    }
  })
}

/**
 * Display error notification
 * @param {string} message - Error message to display
 */
export const showError = (message) => {
  toast.error(message, {
    duration: 4000,
    position: 'top-right',
    style: {
      background: '#ef4444',
      color: '#fff'
    }
  })
}

/**
 * Display loading notification
 * @param {string} message - Loading message to display
 */
export const showLoading = (message) => {
  return toast.loading(message, {
    position: 'top-right'
  })
}

/**
 * Update loading toast to success
 * @param {string} toastId - ID of the toast to update
 * @param {string} message - Success message
 */
export const updateToastSuccess = (toastId, message) => {
  toast.success(message, {
    id: toastId,
    duration: 3000
  })
}

/**
 * Update loading toast to error
 * @param {string} toastId - ID of the toast to update
 * @param {string} message - Error message
 */
export const updateToastError = (toastId, message) => {
  toast.error(message, {
    id: toastId,
    duration: 4000
  })
}
