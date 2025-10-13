document.addEventListener('DOMContentLoaded', function() {
    const dateInput = document.getElementById('date');

    if(dateInput) {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const todayString = `${yyyy}-${mm}-${dd}`;
        dateInput.setAttribute('min', todayString);
    }

    const pcSelect = document.getElementById('pcId');
    const periodCheckboxes = document.querySelectorAll('input[name="periodIds"]');

    const localBookedData = (typeof bookedData !== 'undefined') ? bookedData : {};


    function updatePeriodCheckboxes() {
        const selectedPcId = pcSelect.value;
        const bookedPeriods = localBookedData[selectedPcId] || [];

        periodCheckboxes.forEach(function(checkbox) {
            const periodValue = parseInt(checkbox.value, 10);

            if (bookedPeriods.includes(periodValue)) {
                checkbox.disabled = true;
                checkbox.checked = false;
                checkbox.parentElement.classList.add('text-muted');
            } else {
                checkbox.disabled = false;
                checkbox.parentElement.classList.remove('text-muted');
            }
        });
    }

    if(pcSelect) {
        pcSelect.addEventListener('change', updatePeriodCheckboxes);
    }

    updatePeriodCheckboxes();

});
