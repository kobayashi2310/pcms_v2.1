document.addEventListener('DOMContentLoaded', () => {
    const returnReportModal = document.getElementById('returnReportModal');
    if (returnReportModal) {
        returnReportModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            const reservationIds = button.getAttribute('data-reservation-ids');
            const reservationIdsInput = document.getElementById('reservationIds');
            if (reservationIdsInput) {
                reservationIdsInput.value = reservationIds;
            }
        });
    }

    const form = document.getElementById('returnReportForm');
    if (form) {
        form.addEventListener('submit', event => {
           if (!form.checkVisibility()) {
               event.preventDefault();
               event.stopPropagation();
           }
           form.classList.add('was-validated');
        }, false);
    }
});
