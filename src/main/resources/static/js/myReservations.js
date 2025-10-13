document.addEventListener('DOMContentLoaded', function () {
    const returnReportModal = document.getElementById('returnReportModal');
    if (returnReportModal) {
        returnReportModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const reservationIds = button.getAttribute('data-reservation-ids');

            const reservationIdsInput = document.getElementById('returnReservationIds');
            if (reservationIdsInput) {
                reservationIdsInput.value = reservationIds;
            }
        });
    }

    const cancelModal = document.getElementById('cancelModal');
    if (cancelModal) {
        cancelModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const reservationIds = button.getAttribute('data-reservation-ids');

            const reservationIdsInput = document.getElementById('cancelReservationIds');
            if (reservationIdsInput) {
                reservationIdsInput.value = reservationIds;
            }
        });
    }

    const form = document.getElementById('returnReportForm');
    if (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    }
});

