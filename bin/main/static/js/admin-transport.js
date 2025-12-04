document.addEventListener('DOMContentLoaded', function () {
  const userSearchInput = document.getElementById('userSearch');
  const userSearchResults = document.getElementById('userSearchResults');
  const userIdInput = document.getElementById('userId'); // hidden input

  const transportModalEl = document.getElementById('transportModal');
  if (transportModalEl && typeof hasErrors !== 'undefined' && hasErrors) {
    const transportModal = new bootstrap.Modal(transportModalEl);
    transportModal.show();
  }

  if (userSearchInput && userSearchResults && userIdInput) {
    let searchTimeout;
    let activeIndex = -1;

    function updateActiveItem(items) {
      items.forEach((item, index) => {
        if (index === activeIndex) {
          item.classList.add('active');
          item.scrollIntoView({ block: 'nearest' });
        } else {
          item.classList.remove('active');
        }
      });
    }

    userSearchInput.addEventListener('keydown', function (e) {
      const items = userSearchResults.querySelectorAll('.list-group-item-action');
      if (items.length === 0) {
        activeIndex = -1;
        return;
      }

      switch (e.key) {
        case 'ArrowDown':
          e.preventDefault();
          activeIndex++;
          if (activeIndex >= items.length) {
            activeIndex = 0;
          }
          updateActiveItem(items);
          break;
        case 'ArrowUp':
          e.preventDefault();
          activeIndex--;
          if (activeIndex < 0) {
            activeIndex = items.length - 1;
          }
          updateActiveItem(items);
          break;
        case 'Enter':
          e.preventDefault();
          if (activeIndex > -1 && activeIndex < items.length) {
            items[activeIndex].click();
          }
          break;
        case 'Escape':
          userSearchResults.innerHTML = '';
          activeIndex = -1;
          break;
      }
    });

    userSearchInput.addEventListener('input', function () {
      const query = userSearchInput.value;
      clearTimeout(searchTimeout);

      if (query.length < 1) {
        userSearchResults.innerHTML = '';
        userIdInput.value = '';
        activeIndex = -1;
        return;
      }

      searchTimeout = setTimeout(() => {
        const tokenInput = document.querySelector('input[name="_csrf"]');
        const token = tokenInput ? tokenInput.value : null;

        fetch(`/api/users/search?query=${encodeURIComponent(query)}`, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'X-CSRF-TOKEN': token
          }
        })
            .then(response => {
              if (!response.ok) {
                throw new Error('Network response was not ok');
              }
              return response.json();
            })
            .then(users => {
              userSearchResults.innerHTML = '';
              activeIndex = -1;
              if (users.length > 0) {
                users.forEach(user => {
                  const a = document.createElement('a');
                  a.href = '#';
                  a.classList.add('list-group-item', 'list-group-item-action');
                  a.textContent = `${user.name} (${user.studentId}) / ${user.kana}`;
                  a.setAttribute('data-user-id', user.id);
                  a.setAttribute('data-user-name', `${user.name} (${user.studentId})`);

                  a.addEventListener('click', function (e) {
                    e.preventDefault();
                    userIdInput.value = user.id;
                    userSearchInput.value = a.getAttribute('data-user-name');
                    userSearchResults.innerHTML = '';
                    userSearchResults.style.display = 'none';
                    activeIndex = -1;
                  });
                  userSearchResults.appendChild(a);
                });
                userSearchResults.style.display = 'block';
              } else {
                userSearchResults.innerHTML = '<span class="list-group-item">該当する学生が見つかりません。</span>';
                userSearchResults.style.display = 'block';
              }
            })
            .catch(error => {
              console.error('Error fetching users:', error);
              userSearchResults.innerHTML = '<span class="list-group-item text-danger">検索エラーが発生しました。</span>';
              userSearchResults.style.display = 'block';
            });
      }, 300);
    });

    document.addEventListener('click', function (e) {
      if (!userSearchInput.contains(e.target) && !userSearchResults.contains(e.target)) {
        userSearchResults.innerHTML = '';
        userSearchResults.style.display = 'none';
        activeIndex = -1;
      }
    });
  }
});