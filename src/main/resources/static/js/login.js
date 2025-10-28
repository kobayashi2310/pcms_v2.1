document.addEventListener('DOMContentLoaded', () => {
   const togglePasswordButton = document.getElementById('togglePassword');
   const passwordInput = document.getElementById('password');
   const icon = togglePasswordButton.querySelector('i');

   if (togglePasswordButton && passwordInput && icon) {
       togglePasswordButton.addEventListener('click', () => {
          const type = passwordInput.getAttribute('type')=== 'password'? 'text': 'password';
          passwordInput.setAttribute('type', type);

          if (type === 'password') {
              icon.classList.remove('bi-eye');
              icon.classList.add('bi-eye-slash');
          } else {
              icon.classList.remove('bi-eye-slash');
              icon.classList.add('bi-eye');
          }
       });
   }
});
