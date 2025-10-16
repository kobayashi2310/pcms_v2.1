document.addEventListener('DOMContentLoaded', function () {
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeToggleIcon = document.getElementById('themeToggleIcon');

    const applyTheme = (theme) => {
        const root = document.documentElement;
        if (theme === 'light') {
            root.style.setProperty('--primary-bg', '#f0f2f5');
            root.style.setProperty('--secondary-bg', '#ffffff');
            root.style.setProperty('--card-bg', '#ffffff');
            root.style.setProperty('--text-primary', '#1c1e21');
            root.style.setProperty('--text-secondary', '#65676b');
            root.style.setProperty('--border-color', '#ced0d4');
            root.style.setProperty('--grid-color', 'rgba(0, 0, 0, 0.05)');

            themeToggleIcon.classList.remove('bi-moon-stars-fill');
            themeToggleIcon.classList.add('bi-sun-fill');
        } else { // dark
            root.style.setProperty('--primary-bg', '#1a1a1a');
            root.style.setProperty('--secondary-bg', '#2c2c2c');
            root.style.setProperty('--card-bg', '#383838');
            root.style.setProperty('--text-primary', '#f0f0f0');
            root.style.setProperty('--text-secondary', '#888');
            root.style.setProperty('--border-color', '#444');
            root.style.setProperty('--grid-color', 'rgba(255, 255, 255, 0.05)');

            themeToggleIcon.classList.remove('bi-sun-fill');
            themeToggleIcon.classList.add('bi-moon-stars-fill');
        }
    };

    const currentTheme = localStorage.getItem('theme');
    applyTheme(currentTheme || 'light');

    themeToggleBtn.addEventListener('click', () => {
        const isDarkMode = document.documentElement.style.getPropertyValue('--primary-bg') === '#1a1a1a';
        let newTheme = isDarkMode ? 'light' : 'dark';
        applyTheme(newTheme);
        localStorage.setItem('theme', newTheme);
    });
});
