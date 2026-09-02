/* ==========================================================================
   BRAHMOS BHAWAN - Authentication & Session Logic
   ========================================================================== */

class AuthManager {
  static initLoginForm() {
    const loginForm = document.getElementById('login-form');
    if (!loginForm) return;

    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const username = document.getElementById('username').value.trim();
      const password = document.getElementById('password').value;
      const submitBtn = loginForm.querySelector('button[type="submit"]');

      if (!username || !password) {
        ApiClient.showToast('Please enter both Email/Student ID and Password', 'warning');
        return;
      }

      try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Signing In...</span>';

        const data = await ApiClient.post('/auth/login', { username, password });
        
        ApiClient.setToken(data.token);
        ApiClient.setUser(data.user);

        ApiClient.showToast(`Welcome back, ${data.user.name}!`, 'success');

        setTimeout(() => {
          if (data.user.role === 'ROLE_ADMIN') {
            window.location.href = '/admin/dashboard.html';
          } else {
            window.location.href = '/student/dashboard.html';
          }
        }, 800);
      } catch (err) {
        ApiClient.showToast(err.message || 'Login failed', 'error');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<span>Sign In</span>';
      }
    });
  }

  static initRegisterForm() {
    const registerForm = document.getElementById('register-form');
    if (!registerForm) return;

    registerForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const studentId = document.getElementById('studentId').value.trim();
      const name = document.getElementById('name').value.trim();
      const email = document.getElementById('email').value.trim();
      const roomNumber = document.getElementById('roomNumber').value.trim();
      const block = document.getElementById('block') ? document.getElementById('block').value : 'A_BLOCK';
      const password = document.getElementById('password').value;
      const role = document.getElementById('role') ? document.getElementById('role').value : 'ROLE_STUDENT';
      const submitBtn = registerForm.querySelector('button[type="submit"]');

      if (!studentId || !name || !email || !roomNumber || !password) {
        ApiClient.showToast('Please fill in all required fields', 'warning');
        return;
      }

      try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Creating Account...</span>';

        const data = await ApiClient.post('/auth/register', {
          studentId,
          name,
          email,
          roomNumber,
          block,
          password,
          role
        });

        ApiClient.setToken(data.token);
        ApiClient.setUser(data.user);

        ApiClient.showToast('Registration successful! Redirecting...', 'success');

        setTimeout(() => {
          if (data.user.role === 'ROLE_ADMIN') {
            window.location.href = '/admin/dashboard.html';
          } else {
            window.location.href = '/student/dashboard.html';
          }
        }, 800);
      } catch (err) {
        ApiClient.showToast(err.message || 'Registration failed', 'error');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<span>Register Boarder</span>';
      }
    });
  }

  static checkAuthGuard(requiredRole = null) {
    const user = ApiClient.getUser();
    const token = ApiClient.getToken();

    if (!token || !user) {
      window.location.href = '/login.html';
      return false;
    }

    if (requiredRole && user.role !== requiredRole && user.role !== 'ROLE_ADMIN') {
      ApiClient.showToast('Unauthorized access area', 'error');
      window.location.href = user.role === 'ROLE_ADMIN' ? '/admin/dashboard.html' : '/student/dashboard.html';
      return false;
    }

    // Populate user profile info in navbar/sidebar if elements exist
    document.querySelectorAll('.user-name-display').forEach(el => el.textContent = user.name);
    document.querySelectorAll('.user-room-display').forEach(el => {
      const blockStr = user.block ? user.block.replace('_', ' ') : 'A BLOCK';
      el.textContent = `${blockStr} • Room ${user.roomNumber}`;
    });
    document.querySelectorAll('.user-id-display').forEach(el => el.textContent = user.studentId);
    document.querySelectorAll('.user-block-display').forEach(el => {
      el.textContent = user.block ? user.block.replace('_', ' ') : 'A BLOCK';
    });
    document.querySelectorAll('.user-avatar-display').forEach(el => {
      el.textContent = user.name ? user.name.charAt(0).toUpperCase() : 'U';
    });

    return true;
  }

  static logout() {
    ApiClient.removeToken();
    ApiClient.showToast('Logged out successfully', 'info');
    setTimeout(() => {
      window.location.href = '/login.html';
    }, 500);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  AuthManager.initLoginForm();
  AuthManager.initRegisterForm();

  const logoutBtns = document.querySelectorAll('.btn-logout');
  logoutBtns.forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      AuthManager.logout();
    });
  });
});
