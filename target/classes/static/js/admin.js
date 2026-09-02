/* ==========================================================================
   BRAHMOS BHAWAN - Admin Control Panel JS Logic
   ========================================================================== */

class AdminApp {
  static async loadDashboardStats() {
    try {
      const todayStr = new Date().toISOString().split('T')[0];
      
      const summary = await ApiClient.get(`/admin/food-summary?date=${todayStr}`);

      const lunchVegEl = document.getElementById('stat-today-lunch-veg');
      if (lunchVegEl) lunchVegEl.textContent = summary.lunchVegCount || 0;

      const lunchNonVegEl = document.getElementById('stat-today-lunch-nonveg');
      if (lunchNonVegEl) lunchNonVegEl.textContent = summary.lunchNonVegCount || 0;

      const dinnerVegEl = document.getElementById('stat-today-dinner-veg');
      if (dinnerVegEl) dinnerVegEl.textContent = summary.dinnerVegCount || 0;

      const dinnerNonVegEl = document.getElementById('stat-today-dinner-nonveg');
      if (dinnerNonVegEl) dinnerNonVegEl.textContent = summary.dinnerNonVegCount || 0;

    } catch (err) {
      console.error('Error loading admin dashboard stats:', err);
    }
  }

  static initNoticeManagement() {
    const noticesContainer = document.getElementById('admin-notices-container');
    if (!noticesContainer) return;

    const openBtn = document.getElementById('btn-open-notice-modal');
    const modal = document.getElementById('admin-notice-modal');
    const closeBtn = document.getElementById('btn-close-notice-modal');
    const form = document.getElementById('admin-notice-form');

    if (openBtn && modal) {
      openBtn.addEventListener('click', () => modal.classList.add('active'));
    }
    if (closeBtn && modal) {
      closeBtn.addEventListener('click', () => modal.classList.remove('active'));
    }

    const fetchAndRenderNotices = async () => {
      try {
        const notices = await ApiClient.get('/notices');

        if (notices.length === 0) {
          noticesContainer.innerHTML = `
            <div class="glass-panel" style="padding: 30px; text-align: center; color: var(--text-muted);">
              No announcements published yet. Click "Publish New Announcement" above.
            </div>`;
          return;
        }

        let html = '';
        notices.forEach(n => {
          let badgeClass = 'badge-pending';
          if (n.priority === 'IMPORTANT') badgeClass = 'badge-progress';
          if (n.priority === 'URGENT') badgeClass = 'badge-rejected';

          const imageHtml = n.imageUrl 
            ? `<div style="margin-top: 10px;"><a href="${n.imageUrl}" target="_blank" style="color:var(--accent-primary); font-size:0.85rem; font-weight:700;">🖼️ View Attachment</a></div>`
            : '';

          html += `
            <div class="glass-panel" style="padding: 20px; border-left: 4px solid ${n.priority === 'URGENT' ? '#f43f5e' : 'var(--accent-primary)'};">
              <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px; margin-bottom: 10px;">
                <div>
                  <h4 style="font-size: 1.1rem; color: var(--text-primary); margin-bottom: 4px;">${n.title}</h4>
                  <div style="font-size: 0.78rem; color: var(--text-muted);">Posted by ${n.postedBy} on ${new Date(n.createdAt).toLocaleString()}</div>
                </div>
                <div style="display: flex; gap: 10px; align-items: center;">
                  <span class="badge" style="background: rgba(255,255,255,0.06);">${n.category}</span>
                  <span class="badge ${badgeClass}">${n.priority}</span>
                  <button class="btn btn-danger btn-sm" onclick="AdminApp.deleteNotice(${n.id})" style="padding: 6px 12px; font-size: 0.8rem;">
                    🗑 Delete Notice
                  </button>
                </div>
              </div>
              <div style="font-size: 0.9rem; color: var(--text-secondary); line-height: 1.5; white-space: pre-line;">${n.content}</div>
              ${imageHtml}
            </div>
          `;
        });

        noticesContainer.innerHTML = html;

      } catch (err) {
        ApiClient.showToast(err.message || 'Failed to fetch notices', 'error');
      }
    };

    fetchAndRenderNotices();

    if (form) {
      form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const title = document.getElementById('notice-title').value.trim();
        const category = document.getElementById('notice-category').value;
        const priority = document.getElementById('notice-priority').value;
        const content = document.getElementById('notice-content').value.trim();
        const imageUrl = document.getElementById('notice-image-url') ? document.getElementById('notice-image-url').value.trim() : '';

        if (!title || !content) {
          ApiClient.showToast('Please fill out the title and content', 'warning');
          return;
        }

        try {
          await ApiClient.post('/admin/notices', { title, category, priority, content, imageUrl });
          ApiClient.showToast('Announcement published successfully to Notice Board!', 'success');
          modal.classList.remove('active');
          form.reset();
          fetchAndRenderNotices();
        } catch (err) {
          ApiClient.showToast(err.message || 'Failed to publish notice', 'error');
        }
      });
    }
  }

  static async deleteNotice(id) {
    if (!confirm('Are you sure you want to delete this notice? It will no longer be visible to students.')) return;

    try {
      await ApiClient.delete(`/admin/notices/${id}`);
      ApiClient.showToast('Notice deleted successfully!', 'info');
      this.initNoticeManagement();
    } catch (err) {
      ApiClient.showToast(err.message || 'Could not delete notice', 'error');
    }
  }

  static initWhitelistUpload() {
    const fileInput = document.getElementById('whitelist-file-input');
    const label = document.getElementById('upload-file-label');
    const form = document.getElementById('whitelist-upload-form');

    if (fileInput && label) {
      fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
          label.textContent = `📄 Selected: ${fileInput.files[0].name} (${(fileInput.files[0].size / 1024).toFixed(1)} KB)`;
        }
      });
    }

    if (form) {
      form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!fileInput.files || fileInput.files.length === 0) {
          ApiClient.showToast('Please select an Excel file (.xlsx)', 'warning');
          return;
        }

        const submitBtn = form.querySelector('button[type="submit"]');

        try {
          submitBtn.disabled = true;
          submitBtn.innerHTML = '<span>⏳ Uploading Roster...</span>';

          const formData = new FormData();
          formData.append('file', fileInput.files[0]);

          const token = ApiClient.getToken();
          const res = await fetch('/api/admin/approved-students/upload-excel', {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`
            },
            body: formData
          });

          const data = await res.json();
          if (!res.ok) throw new Error(data.message || 'Upload failed');

          ApiClient.showToast(`Success! Imported ${data.importedCount} pre-approved boarders.`, 'success');
          form.reset();
          if (label) label.textContent = 'Click to browse or drop Excel file (.xlsx / .xls)';

          AdminApp.initStudentsListPage();
        } catch (err) {
          ApiClient.showToast(err.message || 'Failed to upload Excel roster', 'error');
        } finally {
          submitBtn.disabled = false;
          submitBtn.innerHTML = '<span>📤 Upload & Activate Whitelist</span>';
        }
      });
    }
  }

  static async initFoodManagementPage() {
    const summaryContainer = document.getElementById('food-summary-cards');
    if (!summaryContainer) return;

    const datePicker = document.getElementById('filter-food-date');
    const mealFilter = document.getElementById('filter-food-meal');
    
    // Modal elements
    const openExcelModalBtn = document.getElementById('btn-open-excel-modal');
    const excelModal = document.getElementById('excel-export-modal');
    const closeExcelModalBtn = document.getElementById('btn-close-excel-modal');
    const excelForm = document.getElementById('excel-export-form');

    if (datePicker && !datePicker.value) {
      datePicker.value = new Date().toISOString().split('T')[0];
    }

    const fetchAndRenderFoodData = async () => {
      try {
        const dateVal = datePicker.value;
        const mealVal = mealFilter.value;

        let query = `/admin/food-summary?date=${dateVal}`;
        if (mealVal && mealVal !== 'ALL') {
          query += `&mealType=${mealVal}`;
        }

        const summary = await ApiClient.get(query);

        summaryContainer.innerHTML = `
          <div class="glass-panel stat-card" style="border-left: 4px solid var(--veg-color);">
            <div class="stat-icon emerald">☀️</div>
            <div>
              <div class="stat-value" style="color: var(--veg-color);">${summary.lunchVegCount}</div>
              <div class="stat-label">Lunch - Veg Count</div>
            </div>
          </div>
          <div class="glass-panel stat-card" style="border-left: 4px solid var(--nonveg-color);">
            <div class="stat-icon rose">☀️</div>
            <div>
              <div class="stat-value" style="color: var(--nonveg-color);">${summary.lunchNonVegCount}</div>
              <div class="stat-label">Lunch - Non-Veg Count</div>
            </div>
          </div>
          <div class="glass-panel stat-card" style="border-left: 4px solid var(--veg-color);">
            <div class="stat-icon emerald">🌙</div>
            <div>
              <div class="stat-value" style="color: var(--veg-color);">${summary.dinnerVegCount}</div>
              <div class="stat-label">Dinner - Veg Count</div>
            </div>
          </div>
          <div class="glass-panel stat-card" style="border-left: 4px solid var(--nonveg-color);">
            <div class="stat-icon rose">🌙</div>
            <div>
              <div class="stat-value" style="color: var(--nonveg-color);">${summary.dinnerNonVegCount}</div>
              <div class="stat-label">Dinner - Non-Veg Count</div>
            </div>
          </div>
        `;

        const rosterTable = document.getElementById('admin-food-roster-body');
        if (rosterTable) {
          if (!summary.studentPreferences || summary.studentPreferences.length === 0) {
            rosterTable.innerHTML = `
              <tr>
                <td colspan="8" style="text-align: center; padding: 30px; color: var(--text-muted);">
                  No meal preferences submitted for the selected date and meal type.
                </td>
              </tr>`;
            return;
          }

          let rosterHtml = '';
          summary.studentPreferences.forEach(sp => {
            const isVeg = sp.foodPreference === 'VEG';
            const blockStr = sp.block ? sp.block.replace('_', ' ') : 'A BLOCK';
            rosterHtml += `
              <tr>
                <td><strong>${sp.studentId}</strong></td>
                <td>${sp.studentName}</td>
                <td><span class="badge" style="background: rgba(99,102,241,0.15); color: #a5b4fc;">${blockStr}</span></td>
                <td><span class="badge" style="background: rgba(255,255,255,0.06);">${sp.roomNumber}</span></td>
                <td>${sp.date}</td>
                <td>${sp.day}</td>
                <td><strong>${sp.mealType}</strong></td>
                <td><span class="badge ${isVeg ? 'badge-veg' : 'badge-nonveg'}">${isVeg ? '🥗 VEG' : '🍗 NON-VEG'}</span></td>
              </tr>
            `;
          });
          rosterTable.innerHTML = rosterHtml;
        }

      } catch (err) {
        ApiClient.showToast(err.message || 'Failed to fetch food summary', 'error');
      }
    };

    if (datePicker) datePicker.addEventListener('change', fetchAndRenderFoodData);
    if (mealFilter) mealFilter.addEventListener('change', fetchAndRenderFoodData);

    fetchAndRenderFoodData();

    // Excel Modal Controls
    if (openExcelModalBtn && excelModal) {
      openExcelModalBtn.addEventListener('click', () => {
        document.getElementById('export-modal-date').value = datePicker.value || new Date().toISOString().split('T')[0];
        document.getElementById('export-modal-meal').value = mealFilter.value || 'ALL';
        excelModal.classList.add('active');
      });
    }

    if (closeExcelModalBtn && excelModal) {
      closeExcelModalBtn.addEventListener('click', () => excelModal.classList.remove('active'));
    }

    if (excelForm) {
      excelForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const targetDate = document.getElementById('export-modal-date').value;
        const targetMeal = document.getElementById('export-modal-meal').value;
        const confirmBtn = document.getElementById('btn-confirm-excel-download');

        try {
          confirmBtn.disabled = true;
          confirmBtn.innerHTML = '<span>⏳ Generating Excel...</span>';

          let query = `/admin/export/excel?startDate=${targetDate}`;
          if (targetMeal && targetMeal !== 'ALL') {
            query += `&mealType=${targetMeal}`;
          }

          const blob = await ApiClient.get(query);

          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `BRAHMOS_Mess_Report_${targetDate}_${targetMeal}.xlsx`;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(url);

          ApiClient.showToast('Excel report downloaded successfully!', 'success');
          excelModal.classList.remove('active');
        } catch (err) {
          ApiClient.showToast(err.message || 'Excel export failed', 'error');
        } finally {
          confirmBtn.disabled = false;
          confirmBtn.innerHTML = '<span>📥 Download Excel File</span>';
        }
      });
    }
  }

  static async initComplaintManagementPage() {
    const tableBody = document.getElementById('admin-complaints-table-body');
    if (!tableBody) return;

    const statusFilter = document.getElementById('filter-complaint-status');
    const categoryFilter = document.getElementById('filter-complaint-category');
    const searchInput = document.getElementById('search-complaint');
    const exportComplaintsBtn = document.getElementById('btn-export-complaints-excel');

    const fetchComplaints = async () => {
      try {
        let queryParams = [];
        if (statusFilter && statusFilter.value) queryParams.push(`status=${statusFilter.value}`);
        if (categoryFilter && categoryFilter.value) queryParams.push(`category=${categoryFilter.value}`);
        if (searchInput && searchInput.value.trim()) queryParams.push(`search=${encodeURIComponent(searchInput.value.trim())}`);

        const queryStr = queryParams.length ? `?${queryParams.join('&')}` : '';
        const complaints = await ApiClient.get(`/admin/complaints${queryStr}`);

        if (complaints.length === 0) {
          tableBody.innerHTML = `
            <tr>
              <td colspan="9" style="text-align: center; padding: 30px; color: var(--text-muted);">
                No complaints found matching criteria.
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

          const blockStr = c.block ? c.block.replace('_', ' ') : 'A BLOCK';
          const photoBtn = c.imageUrl 
            ? `<a href="${c.imageUrl}" target="_blank" class="btn btn-secondary btn-sm" style="font-size:0.78rem;">🖼️ View Photo</a>`
            : '<span style="color:var(--text-muted); font-size:0.8rem;">No Photo</span>';

          html += `
            <tr>
              <td><strong>#${c.id}</strong></td>
              <td><strong>${c.studentName}</strong><br><span style="font-size: 0.78rem; color: var(--text-muted);">${c.studentId} • ${blockStr} Room ${c.roomNumber}</span></td>
              <td><span class="badge" style="background: rgba(255,255,255,0.06);">${c.category}</span></td>
              <td><strong>${c.title}</strong><br><span style="font-size: 0.82rem; color: var(--text-muted);">${c.description}</span></td>
              <td>${photoBtn}</td>
              <td><span style="font-weight: 700; font-size: 0.85rem;">${c.priority}</span></td>
              <td>${statusBadge}</td>
              <td style="font-size: 0.82rem; color: var(--text-muted);">${c.adminRemark ? `💬 ${c.adminRemark}` : '—'}</td>
              <td>
                <button class="btn btn-secondary btn-sm" onclick="AdminApp.openUpdateModal(${c.id}, '${c.status}', '${c.adminRemark ? c.adminRemark.replace(/'/g, "\\'") : ''}')">
                  ⚙️ Action
                </button>
              </td>
            </tr>
          `;
        });
        tableBody.innerHTML = html;

      } catch (err) {
        ApiClient.showToast(err.message || 'Failed to load complaints', 'error');
      }
    };

    if (statusFilter) statusFilter.addEventListener('change', fetchComplaints);
    if (categoryFilter) categoryFilter.addEventListener('change', fetchComplaints);
    if (searchInput) searchInput.addEventListener('input', fetchComplaints);

    fetchComplaints();
    this.initStatusUpdateModal(fetchComplaints);

    if (exportComplaintsBtn) {
      exportComplaintsBtn.addEventListener('click', async () => {
        try {
          exportComplaintsBtn.disabled = true;
          exportComplaintsBtn.innerHTML = '<span>⏳ Exporting...</span>';

          let queryParams = [];
          if (statusFilter && statusFilter.value) queryParams.push(`status=${statusFilter.value}`);
          if (categoryFilter && categoryFilter.value) queryParams.push(`category=${categoryFilter.value}`);
          if (searchInput && searchInput.value.trim()) queryParams.push(`search=${encodeURIComponent(searchInput.value.trim())}`);

          const queryStr = queryParams.length ? `?${queryParams.join('&')}` : '';
          const blob = await ApiClient.get(`/admin/export/complaints/excel${queryStr}`);

          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `BRAHMOS_Complaints_Report_${new Date().toISOString().split('T')[0]}.xlsx`;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(url);

          ApiClient.showToast('Complaints Excel report downloaded successfully!', 'success');
        } catch (err) {
          ApiClient.showToast(err.message || 'Export failed', 'error');
        } finally {
          exportComplaintsBtn.disabled = false;
          exportComplaintsBtn.innerHTML = '<span>📥 Export Complaints Excel</span>';
        }
      });
    }
  }

  static openUpdateModal(id, currentStatus, currentRemark) {
    const modal = document.getElementById('admin-status-modal');
    if (!modal) return;

    document.getElementById('modal-complaint-id').value = id;
    document.getElementById('modal-status-select').value = currentStatus;
    document.getElementById('modal-admin-remark').value = currentRemark || '';

    modal.classList.add('active');
  }

  static initStatusUpdateModal(refreshCallback) {
    const modal = document.getElementById('admin-status-modal');
    const closeBtn = document.getElementById('btn-close-status-modal');
    const form = document.getElementById('admin-status-form');

    if (closeBtn && modal) {
      closeBtn.addEventListener('click', () => modal.classList.remove('active'));
    }

    if (form) {
      form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('modal-complaint-id').value;
        const status = document.getElementById('modal-status-select').value;
        const adminRemark = document.getElementById('modal-admin-remark').value.trim();

        try {
          await ApiClient.put(`/admin/complaints/${id}/status`, { status, adminRemark });
          ApiClient.showToast('Complaint status updated successfully!', 'success');
          modal.classList.remove('active');
          if (refreshCallback) refreshCallback();
        } catch (err) {
          ApiClient.showToast(err.message || 'Failed to update status', 'error');
        }
      });
    }
  }

  static async initStudentsListPage() {
    const tableBody = document.getElementById('admin-students-table-body');
    if (!tableBody) return;

    try {
      const approvedList = await ApiClient.get(`/admin/approved-students`);
      if (approvedList && approvedList.length > 0) {
        let rosterHtml = '';
        approvedList.forEach(s => {
          const blockStr = s.block ? s.block.replace('_', ' ') : 'A BLOCK';
          rosterHtml += `
            <tr>
              <td><strong>${s.studentId}</strong></td>
              <td>${s.name}</td>
              <td><span class="badge" style="background: rgba(99,102,241,0.15); color: #a5b4fc;">${blockStr}</span></td>
              <td><span class="badge badge-progress">Room ${s.roomNumber || '101'}</span></td>
              <td><code>${s.email}</code></td>
              <td><span class="badge badge-resolved">✅ Pre-Approved</span></td>
            </tr>
          `;
        });
        tableBody.innerHTML = rosterHtml;
      }
    } catch (err) {
      console.error(err);
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('/admin/')) {
    if (!AuthManager.checkAuthGuard('ROLE_ADMIN')) return;

    AdminApp.loadDashboardStats();
    AdminApp.initNoticeManagement();
    AdminApp.initWhitelistUpload();
    AdminApp.initFoodManagementPage();
    AdminApp.initComplaintManagementPage();
    AdminApp.initStudentsListPage();
  }
});
