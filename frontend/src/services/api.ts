const BASE_URL = `${import.meta.env.VITE_API_URL || `${import.meta.env.VITE_API_URL}`}`;

export const googleLogin = async (payload: { idToken: string }) => {
  const response = await fetch(`${BASE_URL}/auth/google`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Google Login failed');
  }
  return response.json();
};

export const getUserProfile = async (token: string) => {
  const response = await fetch(`${BASE_URL}/users/me`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch user profile');
  return response.json();
};

export const updatePassword = async (passwordData: any, token: string) => {
  const response = await fetch(`${BASE_URL}/users/me/password`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` 
    },
    body: JSON.stringify(passwordData)
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Failed to update password');
  }
  return response.json();
};

export const submitApplication = async (payload: any) => {
  const response = await fetch(`${BASE_URL}/applications`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Application submission failed');
  }
  return response.json();
};

export const fetchApplications = async (token: string, page = 0, size = 50) => {
  const response = await fetch(`${BASE_URL}/applications?page=${page}&size=${size}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch applications');
  return response.json();
};

export const searchApplications = async (filters: any, page = 0, size = 50, sortBy = 'createdAt', sortDir = 'DESC', token: string) => {
  const response = await fetch(`${BASE_URL}/applications/search?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`, {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` 
    },
    body: JSON.stringify(filters)
  });
  if (!response.ok) throw new Error('Search failed');
  return response.json();
};

export const approveApplication = async (publicId: string, token: string) => {
  const response = await fetch(`${BASE_URL}/applications/${publicId}/approve`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Approval failed');
  return response.json();
};

export const rejectApplication = async (publicId: string, reason: string, token: string) => {
  const response = await fetch(`${BASE_URL}/applications/${publicId}/reject`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify({ reason })
  });
  if (!response.ok) throw new Error('Rejection failed');
  return response.json();
};

export const fetchDashboardStats = async (token: string, eventId?: string | null) => {
  let url = `${BASE_URL}/dashboard/summary`;
  if (eventId) {
    url += `?eventId=${eventId}`;
  }
  const response = await fetch(url, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to load stats');
  return response.json();
};



export const getGuestProfile = async (publicId: string, token: string) => {
  const response = await fetch(`${BASE_URL}/applications/${publicId}/guest-profile`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to load guest profile');
  return response.json();
};

export const bulkApproveApplications = async (publicIds: string[], token: string) => {
  const response = await fetch(`${BASE_URL}/applications/bulk-approve`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify({ publicIds })
  });
  if (!response.ok) throw new Error('Bulk approve failed');
  return response.json();
};

export const bulkRejectApplications = async (publicIds: string[], reason: string, token: string) => {
  const response = await fetch(`${BASE_URL}/applications/bulk-reject`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify({ publicIds, reason })
  });
  if (!response.ok) throw new Error('Bulk reject failed');
  return response.json();
};

export const assignAdmin = async (publicId: string, adminIdentifier: string, token: string) => {
  const response = await fetch(`${BASE_URL}/applications/${publicId}/assign`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify({ adminIdentifier })
  });
  if (!response.ok) throw new Error('Assign admin failed');
  return response.json();
};

export const fetchAdmins = async (token: string) => {
  const response = await fetch(`${BASE_URL}/users?size=100`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Fetch admins failed');
  return response.json();
};

export const fetchEvents = async (token: string, page = 0, size = 50) => {
  const response = await fetch(`${BASE_URL}/events?page=${page}&size=${size}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Fetch events failed');
  return response.json();
};

export const createEvent = async (eventData: any, token: string) => {
  const response = await fetch(`${BASE_URL}/events`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify(eventData)
  });
  if (!response.ok) throw new Error('Create event failed');
  return response.json();
};

export const updateEvent = async (publicId: string, eventData: any, token: string) => {
  const response = await fetch(`${BASE_URL}/events/${publicId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify(eventData)
  });
  if (!response.ok) throw new Error('Update event failed');
  return response.json();
};

export const deleteEvent = async (publicId: string, token: string) => {
  const response = await fetch(`${BASE_URL}/events/${publicId}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Delete event failed');
  return response.json();
};

export const changeEventStatus = async (publicId: string, action: string, token: string) => {
  const response = await fetch(`${BASE_URL}/events/${publicId}/${action}`, {
    method: 'PUT',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error(`Change status ${action} failed`);
  return response.json();
};

export const fetchUsers = async (token: string, page = 0, size = 20) => {
  const response = await fetch(`${BASE_URL}/users?page=${page}&size=${size}`, {
    method: 'GET',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch users');
  return response.json();
};

export const searchUsers = async (query: string, token: string, page = 0, size = 20) => {
  const response = await fetch(`${BASE_URL}/users/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`, {
    method: 'GET',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to search users');
  return response.json();
};

export const createUser = async (userData: any, token: string, isAdmin = false) => {
  const url = isAdmin ? `${BASE_URL}/super-admin/admins` : `${BASE_URL}/super-admin/users`;
  const response = await fetch(url, {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` 
    },
    body: JSON.stringify(userData),
  });
  if (!response.ok) throw new Error('Failed to create user');
  return response.json();
};

export const updateUser = async (id: number, userData: any, token: string) => {
  const response = await fetch(`${BASE_URL}/super-admin/users/${id}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` 
    },
    body: JSON.stringify(userData),
  });
  if (!response.ok) throw new Error('Failed to update user');
  return response.json();
};

export const deleteUser = async (id: number, token: string) => {
  const response = await fetch(`${BASE_URL}/super-admin/users/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to delete user');
  return response.json();
};

export const activateUser = async (id: number, token: string) => {
  const response = await fetch(`${BASE_URL}/super-admin/users/${id}/activate`, {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to activate user');
  return response.json();
};

export const deactivateUser = async (id: number, token: string) => {
  const response = await fetch(`${BASE_URL}/super-admin/users/${id}/deactivate`, {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to deactivate user');
  return response.json();
};

export const blockUser = async (id: number, token: string) => {
  const response = await fetch(`${BASE_URL}/super-admin/users/${id}/block`, {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to suspend/block user');
  return response.json();
};

export const fetchAuditLogs = async (token: string, page = 0, size = 50) => {
  const response = await fetch(`${BASE_URL}/audit-logs?page=${page}&size=${size}`, {
    method: 'GET',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch audit logs');
  return response.json();
};

export const fetchInvitations = async (token: string, page = 0, size = 20, eventId?: string | null) => {
  const url = eventId 
    ? `${BASE_URL}/invitations/event/${eventId}?page=${page}&size=${size}`
    : `${BASE_URL}/invitations?page=${page}&size=${size}`;
  const response = await fetch(url, {
    method: 'GET',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch invitations');
  return response.json();
};

export const resendInvitation = async (id: number, token: string) => {
  const response = await fetch(`${BASE_URL}/invitations/${id}/resend`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to resend invitation');
  return response.json();
};



export const downloadInvitationPdfBlob = async (id: number, token: string) => {
  const response = await fetch(`${BASE_URL}/invitations/${id}/download-pdf`, {
    method: 'GET',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to download PDF');
  return response.blob();
};

export const getAdminDashboardSummary = async (token: string) => {
  const response = await fetch(`${BASE_URL}/dashboard/admin-summary`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch admin dashboard summary');
  const res = await response.json();
  return res.data;
};

export const fetchUnreadNotificationCount = async (token: string) => {
  const response = await fetch(`${BASE_URL}/notifications/unread-count`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch unread notification count');
  return response.json();
};

/** Fetch all users (admin-accessible endpoint) */
export const fetchAllUsers = async (token: string, page = 0, size = 50) => {
  const response = await fetch(`${BASE_URL}/users?page=${page}&size=${size}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!response.ok) throw new Error('Failed to fetch users');
  return response.json();
};

/** Update a user's assigned events (admin-accessible PUT /users/{id}) */
export const adminAssignEventsToUser = async (userId: number, assignedEventIds: string[], token: string) => {
  const response = await fetch(`${BASE_URL}/users/${userId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify({ assignedEventIds }),
  });
  if (!response.ok) throw new Error('Failed to assign events to user');
  return response.json();
};

/** Assign a guest application to a user (reuses the existing assign endpoint) */
export const assignGuestToUser = async (applicationPublicId: string, userIdentifier: string, token: string) => {
  const response = await fetch(`${BASE_URL}/applications/${applicationPublicId}/assign`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify({ adminIdentifier: userIdentifier }),
  });
  if (!response.ok) throw new Error('Failed to assign guest to user');
  return response.json();
};

