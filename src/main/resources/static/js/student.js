/* ==========================================================================
   BRAHMOS BHAWAN - Student Portal JS Logic
   ========================================================================== */

class StudentApp {
  static async loadDashboardStats() {
    try {
      const dateEl = document.getElementById('current-date-display');
      if (dateEl) {
        const today = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        dateEl.textContent = today.toLocaleDateString('en-US', options);
      }

      const preferences = await ApiClient.get('/student/food-preference');
      const complaints = await ApiClient.get('/student/complaints');

      const todayStr = new Date().toISOString().split('T')[0];
      const todayLunch = preferences.find(p => p.date === todayStr && p.mealType === 'LUNCH');
      const todayDinner = preferences.find(p => p.date === todayStr && p.mealType === 'DINNER');

      const lunchBadge = document.getElementById('today-lunch-pref');
      if (lunchBadge) {
        if (todayLunch) {
          lunchBadge.className = `badge ${todayLunch.foodPreference === 'VEG' ? 'badge-veg' : 'badge-nonveg'}`;
          lunchBadge.textContent = todayLunch.foodPreference === 'VEG' ? '🥗 VEG' : '🍗 NON-VEG';
        } else {
          lunchBadge.className = 'badge badge-pending';
          lunchBadge.textContent = 'NOT SELECTED';
        }
      }

      const dinnerBadge = document.getElementById('today-dinner-pref');
      if (dinnerBadge) {
        if (todayDinner) {
          dinnerBadge.className = `badge ${todayDinner.foodPreference === 'VEG' ? 'badge-veg' : 'badge-nonveg'}`;
          dinnerBadge.textContent = todayDinner.foodPreference === 'VEG' ? '🥗 VEG' : '🍗 NON-VEG';
        } else {
          dinnerBadge.className = 'badge badge-pending';
          dinnerBadge.textContent = 'NOT SELECTED';
        }
      }

      const totalCompEl = document.getElementById('total-complaints-count');
      if (totalCompEl) totalCompEl.textContent = complaints.length;

      const pendingCompEl = document.getElementById('pending-complaints-count');
      if (pendingCompEl) {
        const pendingCount = complaints.filter(c => c.status === 'PENDING' || c.status === 'IN_PROGRESS').length;
        pendingCompEl.textContent = pendingCount;
      }

      this.renderRecentComplaintsWidget(complaints.slice(0, 3));
      this.loadStudentNotices();

    } catch (err) {
      console.error('Error loading dashboard stats:', err);
    }
  }

  static async loadStudentNotices() {
    const dashContainer = document.getElementById('dashboard-notices-container');
    const pageContainer = document.getElementById('student-notices-container');

    if (!dashContainer && !pageContainer) return;

    try {
      const notices = await ApiClient.get('/notices');

      if (dashContainer) {
        if (notices.length === 0) {
          dashContainer.innerHTML = `<p style="color: var(--text-muted);">No active notices posted.</p>`;
        } else {
          let html = '';
          notices.slice(0, 2).forEach(n => {
            let badgeClass = 'badge-pending';
            if (n.priority === 'IMPORTANT') badgeClass = 'badge-progress';
            if (n.priority === 'URGENT') badgeClass = 'badge-rejected';

            html += `
              <div style="padding: 14px; background: rgba(15, 23, 42, 0.5); border-radius: var(--radius-sm); border-left: 3px solid var(--accent-primary);">
                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px;">
                  <strong style="font-size: 0.98rem; color: var(--text-primary);">${n.title}</strong>
                  <span class="badge ${badgeClass}">${n.priority}</span>
                </div>
                <div style="font-size: 0.85rem; color: var(--text-secondary); line-height: 1.4;">${n.content}</div>
                <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 8px;">
                  📌 Posted by ${n.postedBy} • ${new Date(n.createdAt).toLocaleDateString()}
                </div>
              </div>
            `;
          });
          dashContainer.innerHTML = html;
        }
      }

      if (pageContainer) {
        if (notices.length === 0) {
          pageContainer.innerHTML = `
            <div class="glass-panel" style="padding: 40px; text-align: center; color: var(--text-muted);">
              No notices published on the Notice Board currently.
            </div>`;
          return;
        }

        let pageHtml = '';
        notices.forEach(n => {
          let badgeClass = 'badge-pending';
          if (n.priority === 'IMPORTANT') badgeClass = 'badge-progress';
          if (n.priority === 'URGENT') badgeClass = 'badge-rejected';

          const imageHtml = n.imageUrl 
            ? `<div style="margin-top: 14px;"><img src="${n.imageUrl}" alt="Notice Attachment" style="max-width: 100%; max-height: 300px; border-radius: var(--radius-sm); border: 1px solid var(--border-color);"></div>`
            : '';

          pageHtml += `
            <div class="glass-panel" style="padding: 24px; border-left: 4px solid ${n.priority === 'URGENT' ? '#f43f5e' : 'var(--accent-primary)'};">
              <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px; margin-bottom: 12px;">
                <h3 style="font-size: 1.15rem; color: var(--text-primary);">${n.title}</h3>
                <div style="display: flex; gap: 8px; align-items: center;">
                  <span class="badge" style="background: rgba(255,255,255,0.06);">${n.category}</span>
                  <span class="badge ${badgeClass}">${n.priority}</span>
                </div>
              </div>
              <div style="font-size: 0.92rem; color: var(--text-secondary); line-height: 1.6; white-space: pre-line;">${n.content}</div>
              ${imageHtml}
              <div style="font-size: 0.78rem; color: var(--text-muted); margin-top: 14px; border-top: 1px solid var(--border-color); padding-top: 10px;">
                📌 Posted by <strong>${n.postedBy}</strong> on ${new Date(n.createdAt).toLocaleString()}
              </div>
            </div>
          `;
        });
        pageContainer.innerHTML = pageHtml;
      }

    } catch (err) {
      console.error('Error loading notices:', err);
    }
  }

  static renderRecentComplaintsWidget(recentComplaints) {
    const container = document.getElementById('recent-complaints-container');
    if (!container) return;

    if (recentComplaints.length === 0) {
      container.innerHTML = `<p style="text-align: center; padding: 20px; color: var(--text-muted);">No complaints submitted yet.</p>`;
      return;
    }

    let html = '<div style="display: flex; flex-direction: column; gap: 12px;">';
    recentComplaints.forEach(c => {
      let statusClass = 'badge-pending';
      let statusText = '🔴 Pending';
      if (c.status === 'IN_PROGRESS') { statusClass = 'badge-progress'; statusText = '🟡 In Progress'; }
      if (c.status === 'RESOLVED') { statusClass = 'badge-resolved'; statusText = '🟢 Resolved'; }
      if (c.status === 'REJECTED') { statusClass = 'badge-rejected'; statusText = '⚪ Rejected'; }

      html += `
        <div style="padding: 14px; background: rgba(15, 23, 42, 0.4); border-radius: var(--radius-sm); border: 1px solid var(--border-color); display: flex; align-items: center; justify-content: space-between;">
          <div>
            <div style="font-weight: 700; font-size: 0.95rem;">${c.title}</div>
            <div style="font-size: 0.8rem; color: var(--text-muted);">${c.category} • Submitted ${new Date(c.createdAt).toLocaleDateString()}</div>
          </div>
          <span class="badge ${statusClass}">${statusText}</span>
        </div>
      `;
    });
    html += '</div>';
    container.innerHTML = html;
  }

  static async initWeeklyMealSelectionGrid() {
    const gridContainer = document.getElementById('weekly-meal-grid');
    if (!gridContainer) return;

    try {
      const preferences = await ApiClient.get('/student/food-preference');
      const prefMap = {};
      preferences.forEach(p => {
        prefMap[`${p.date}_${p.mealType}`] = p;
      });

      const now = new Date();
      const dayOfWeek = now.getDay();
      const diffToMon = (dayOfWeek === 0 ? -6 : 1) - dayOfWeek;
      
      const mondayDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() + diffToMon);

      let gridHtml = '';
      const dayNames = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

      for (let i = 0; i < 7; i++) {
        const currentDate = new Date(mondayDate.getFullYear(), mondayDate.getMonth(), mondayDate.getDate() + i);

        const year = currentDate.getFullYear();
        const month = String(currentDate.getMonth() + 1).padStart(2, '0');
        const day = String(currentDate.getDate()).padStart(2, '0');
        const dateStr = `${year}-${month}-${day}`;

        const dayName = dayNames[i];
        const isMonday = dayName === 'Monday';
        const formattedDate = currentDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

        const lunchObj = prefMap[`${dateStr}_LUNCH`];
        const dinnerObj = prefMap[`${dateStr}_DINNER`];

        const lunchPref = lunchObj ? lunchObj.foodPreference : null;
        const dinnerPref = dinnerObj ? dinnerObj.foodPreference : null;

        const isLunchLocked = lunchObj ? (lunchObj.locked || lunchObj.editCount >= 1) : false;
        const isDinnerLocked = dinnerObj ? (dinnerObj.locked || dinnerObj.editCount >= 1) : false;

        gridHtml += `
          <div class="glass-panel day-card ${isMonday ? 'monday-locked' : ''}">
            <div class="day-card-header">
              <div>
                <div class="day-name">${dayName}</div>
                <div class="day-date">${formattedDate} (${dateStr})</div>
              </div>
              ${isMonday ? '<span class="badge badge-pending">🔒 Fixed Menu</span>' : ''}
            </div>

            ${isMonday ? `
              <div class="monday-notice-banner">
                <span>ℹ️</span>
                <span>Monday meal is fixed & same for all boarders. Selection disabled.</span>
              </div>
            ` : `
              <div class="meal-option-row">
                <div class="meal-type-label">
                  <span>☀️</span>
                  <span>Lunch ${isLunchLocked ? '<span style="font-size:0.75rem; color:var(--status-pending);">(Locked)</span>' : ''}</span>
                </div>
                <div class="pref-btn-group">
                  <button class="pref-toggle ${lunchPref === 'VEG' ? 'active-veg' : ''}" 
                          ${isLunchLocked ? 'disabled' : ''}
                          onclick="StudentApp.setPreference('${dateStr}', 'LUNCH', 'VEG', this)">
                    🥗 Veg
                  </button>
                  <button class="pref-toggle ${lunchPref === 'NON_VEG' ? 'active-nonveg' : ''}" 
                          ${isLunchLocked ? 'disabled' : ''}
                          onclick="StudentApp.setPreference('${dateStr}', 'LUNCH', 'NON_VEG', this)">
                    🍗 Non-Veg
                  </button>
                </div>
              </div>

              <div class="meal-option-row">
                <div class="meal-type-label">
                  <span>🌙</span>
                  <span>Dinner ${isDinnerLocked ? '<span style="font-size:0.75rem; color:var(--status-pending);">(Locked)</span>' : ''}</span>
                </div>
                <div class="pref-btn-group">
                  <button class="pref-toggle ${dinnerPref === 'VEG' ? 'active-veg' : ''}" 
                          ${isDinnerLocked ? 'disabled' : ''}
                          onclick="StudentApp.setPreference('${dateStr}', 'DINNER', 'VEG', this)">
                    🥗 Veg
                  </button>
                  <button class="pref-toggle ${dinnerPref === 'NON_VEG' ? 'active-nonveg' : ''}" 
                          ${isDinnerLocked ? 'disabled' : ''}
                          onclick="StudentApp.setPreference('${dateStr}', 'DINNER', 'NON_VEG', this)">
                    🍗 Non-Veg
                  </button>
                </div>
              </div>
            `}
          </div>
        `;
      }

      gridContainer.innerHTML = gridHtml;

    } catch (err) {
      ApiClient.showToast(err.message || 'Failed to load meal preferences', 'error');
    }
  }

  static async setPreference(date, mealType, foodPreference, btnElement) {
    try {
      const parentGroup = btnElement.parentElement;
      const buttons = parentGroup.querySelectorAll('.pref-toggle');
      buttons.forEach(b => b.disabled = true);

      const result = await ApiClient.post('/student/food-preference', {
        date,
        mealType,
        foodPreference
      });

      buttons.forEach(b => {
        b.classList.remove('active-veg', 'active-nonveg');
      });

      if (foodPreference === 'VEG') {
        btnElement.classList.add('active-veg');
      } else {
        btnElement.classList.add('active-nonveg');
      }

      if (result.locked || result.editCount >= 1) {
        buttons.forEach(b => b.disabled = true);
        ApiClient.showToast(`${mealType} selection saved! Selection is now locked (Single Edit Rule applied).`, 'success');
      } else {
        buttons.forEach(b => b.disabled = false);
        ApiClient.showToast(`${mealType} selection saved as ${foodPreference}! (1 edit allowed)`, 'info');
      }

    } catch (err) {
      ApiClient.showToast(err.message || 'Could not save preference', 'error');
      const parentGroup = btnElement.parentElement;
      const buttons = parentGroup.querySelectorAll('.pref-toggle');
      buttons.forEach(b => b.disabled = false);
    }
  }

  static async loadComplaintsList() {
    const tableBody = document.getElementById('complaints-table-body');
    if (!tableBody) return;

    try {
      const complaints = await ApiClient.get('/student/complaints');

      if (complaints.length === 0) {
        tableBody.innerHTML = `
          <tr>
            <td colspan="8" style="text-align: center; padding: 30px; color: var(--text-muted);">
              No complaints filed yet. Click "Report a Problem" to submit one.
            </td>
          </tr>`;
        return;
      }

      let html = '';
      complaints.forEach(c => {
        let statusBadge = '';
        if (c.status === 'PENDING') statusBadge = '<span class="badge badge-pending">🔴 Pending</span>';
        if (c.status === 'IN_PROGRESS') statusBadge = '<span class="badge badge-progress">🟡 In Progress</span>';
        if (c.status === 'RESOLVED') statusBadge = '<span class="badge badge-resolved">🟢 Resolved</span>';
        if (c.status === 'REJECTED') statusBadge = '<span class="badge badge-rejected">⚪ Rejected</span>';

        let prioColor = '#94a3b8';
        if (c.priority === 'HIGH') prioColor = '#f97316';
        if (c.priority === 'URGENT') prioColor = '#f43f5e';

        const photoHtml = c.imageUrl 
          ? `<a href="${c.imageUrl}" target="_blank" style="color: var(--accent-primary); font-weight:700; text-decoration:none;">📷 View Photo</a>`
          : '<span style="color:var(--text-muted);">No photo</span>';

        html += `
          <tr>
            <td><strong>#${c.id}</strong></td>
            <td><span class="badge" style="background: rgba(255,255,255,0.06);">${c.category}</span></td>
            <td><strong>${c.title}</strong></td>
            <td style="max-width: 250px; font-size: 0.88rem;">${c.description}</td>
            <td>${photoHtml}</td>
            <td><span style="color: ${prioColor}; font-weight: 700; font-size: 0.85rem;">${c.priority}</span></td>
            <td>${statusBadge}</td>
            <td style="font-size: 0.85rem; color: var(--text-muted);">${c.adminRemark ? `💬 ${c.adminRemark}` : '<em>No remarks yet</em>'}</td>
          </tr>
        `;
      });

      tableBody.innerHTML = html;

    } catch (err) {
      ApiClient.showToast(err.message || 'Failed to load complaints', 'error');
    }
  }

  static initComplaintModal() {
    const openBtn = document.getElementById('btn-open-complaint-modal');
    const modal = document.getElementById('complaint-modal');
    const closeBtn = document.getElementById('btn-close-complaint-modal');
    const form = document.getElementById('complaint-form');

    if (openBtn && modal) {
      openBtn.addEventListener('click', () => modal.classList.add('active'));
    }
    if (closeBtn && modal) {
      closeBtn.addEventListener('click', () => modal.classList.remove('active'));
    }

    if (form) {
      form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const category = document.getElementById('complaint-category').value;
        const title = document.getElementById('complaint-title').value.trim();
        const description = document.getElementById('complaint-description').value.trim();
        const priority = document.getElementById('complaint-priority').value;
        const imageUrl = document.getElementById('complaint-image-url') ? document.getElementById('complaint-image-url').value.trim() : '';

        if (!title || !description) {
          ApiClient.showToast('Please fill out the problem title and description', 'warning');
          return;
        }

        try {
          await ApiClient.post('/student/complaints', { category, title, description, priority, imageUrl });
          ApiClient.showToast('Complaint submitted successfully!', 'success');
          modal.classList.remove('active');
          form.reset();

          StudentApp.loadComplaintsList();
          StudentApp.loadDashboardStats();
        } catch (err) {
          ApiClient.showToast(err.message || 'Failed to submit complaint', 'error');
        }
      });
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('/student/')) {
    if (!AuthManager.checkAuthGuard('ROLE_STUDENT')) return;

    StudentApp.loadDashboardStats();
    StudentApp.initWeeklyMealSelectionGrid();
    StudentApp.loadComplaintsList();
    StudentApp.initComplaintModal();
  }
});
