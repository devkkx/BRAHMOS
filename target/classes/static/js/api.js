/* ==========================================================================
   BRAHMOS BHAWAN - Central API Fetch Client
   ========================================================================== */

const API_BASE_URL = '/api';

class ApiClient {
  static getToken() {
    return localStorage.getItem('brahmos_jwt_token');
  }

  static setToken(token) {
    localStorage.setItem('brahmos_jwt_token', token);
  }

  static removeToken() {
    localStorage.removeItem('brahmos_jwt_token');
    localStorage.removeItem('brahmos_user');
  }

  static getUser() {
    const userStr = localStorage.getItem('brahmos_user');
    return userStr ? JSON.parse(userStr) : null;
  }

  static setUser(user) {
    localStorage.setItem('brahmos_user', JSON.stringify(user));
  }

  static async request(endpoint, options = {}) {
    const token = this.getToken();
    
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers,
    };

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

      if (response.status === 401) {
        // Token expired or invalid
        this.removeToken();
        if (!window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('register.html') && window.location.pathname !== '/') {
          window.location.href = '/login.html';
        }
        throw new Error('Session expired. Please log in again.');
      }

      if (response.status === 204) {
        return null; // No Content
      }

      // Check if blob response (e.g. Excel download)
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('spreadsheetml')) {
        if (!response.ok) throw new Error('Export failed');
        return await response.blob();
      }

      const data = await response.json();

      if (!response.ok) {
        const errorMsg = data.message || (data.details ? Object.values(data.details).join(', ') : 'Request failed');
        throw new Error(errorMsg);
      }

      return data;
    } catch (error) {
      console.error('API Error:', error.message);
      throw error;
    }
  }

  static get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  static post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  }

  static put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  }

  static delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }

  // Toast Notification System
  static showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '❌';
    if (type === 'warning') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4000);
  }
}
