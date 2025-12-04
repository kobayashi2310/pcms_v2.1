document.addEventListener('DOMContentLoaded', () => {
    const reservationModalEl = document.getElementById('reservationModal');
    if (reservationModalEl && typeof hasErrors !== 'undefined' && hasErrors) {
        const reservationModal = new bootstrap.Modal(reservationModalEl);
        reservationModal.show();
    }

    const dateInput = document.getElementById('dateModal');
    if (dateInput) {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const todayString = `${yyyy}-${mm}-${dd}`;
        dateInput.setAttribute('min', todayString);
    }

    const pcSelect = document.getElementById('pcSelect');
    const periodCheckboxes = document.querySelectorAll('input[name="periodIds"]');
    const localBookedData = (typeof bookedData !== 'undefined') ? bookedData : {};

    if (pcSelect) {
        function updatePeriodCheckboxes() {
            const selectedPcId = pcSelect.value;
            const bookedPeriods = localBookedData[selectedPcId] || [];

            periodCheckboxes.forEach(checkbox => {
               const periodValue = parseInt(checkbox.value, 10);
               const formCheckDiv = checkbox.closest('.form-check');

               if (bookedPeriods.includes(periodValue)) {
                   checkbox.disabled = true;
                   checkbox.checked = false;
                   if (formCheckDiv) {
                       formCheckDiv.classList.add('text-muted');
                   } else {
                       checkbox.disabled = false;
                       if (formCheckDiv) {
                           formCheckDiv.classList.remove('text-muted');
                       }
                   }
               }
            });
        }
        pcSelect.addEventListener('change', updatePeriodCheckboxes);
        updatePeriodCheckboxes();
    }

    const selectAmBtn = document.getElementById('modal-select-am');
    const selectPmBtn = document.getElementById('modal-select-pm');
    const selectAllDayBtn = document.getElementById('modal-select-all-day');
    const selectClearBtn = document.getElementById('modal-select-clear');

    if (selectAmBtn) {
        selectAmBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                const periodValue = parseInt(checkbox.value, 10);
                if (!checkbox.disabled) {
                    checkbox.checked = periodValue <= 2;
                }
            });
        });
    }

    if (selectPmBtn) {
        selectPmBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                const periodValue = parseInt(checkbox.value, 10);
                if (!checkbox.disabled) {
                    checkbox.checked = periodValue > 2;
                }
            });
        });
    }

    if (selectAllDayBtn) {
        selectAllDayBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                if (!checkbox.disabled) {
                    checkbox.checked = true;
                }
            });
        });
    }

    if (selectClearBtn) {
        selectClearBtn.addEventListener('click', function() {
            periodCheckboxes.forEach(function(checkbox) {
                checkbox.checked = false;
            });
        });
    }
})
