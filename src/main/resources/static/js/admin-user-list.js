document.addEventListener('DOMContentLoaded', function() {
    const yearInput = document.getElementById('admissionYear');
    loadUsers(yearInput.value);

    let timeout = null;
    yearInput.addEventListener('input', function() {
        clearTimeout(timeout);
        const val = this.value;
        timeout = setTimeout(function() {
            loadUsers(val);
        }, 500);
    });
});

function loadUsers(year) {
    let url = '/api/admin/users';
    if (year) {
        url += `?year=${year}`;
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                console.error("API Error:", response.status);
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(users => renderTable(users))
        .catch(error => {
            console.error('Error fetching users:', error);
            const tbody = document.querySelector('table tbody');
            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">データの取得に失敗しました (サーバー再起動が必要な場合があります)</td></tr>';
        });
}

function renderTable(users) {
    const tbody = document.querySelector('table tbody');
    tbody.innerHTML = '';

    if (!users || users.length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = '<td colspan="3" class="text-center">データがありません</td>';
        tbody.appendChild(tr);
        return;
    }

    users.forEach(user => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${escapeHtml(user.studentId)}</td>
            <td>${escapeHtml(user.name)}</td>
            <td>${escapeHtml(user.kana)}</td>
        `;
        tbody.appendChild(tr);
    });
}

function escapeHtml(unsafe) {
    return (unsafe || "").toString()
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}
