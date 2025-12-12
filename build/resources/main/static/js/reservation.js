document.addEventListener("DOMContentLoaded", () => {
  const reservationModalEl = document.getElementById("reservationModal");
  if (reservationModalEl && typeof hasErrors !== "undefined" && hasErrors) {
    const reservationModal = new bootstrap.Modal(reservationModalEl);
    reservationModal.show();
  }

  const dateInput = document.getElementById("dateModal");
  if (dateInput) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    const todayString = `${yyyy}-${mm}-${dd}`;
    dateInput.setAttribute("min", todayString);
  }

  const pcSelect = document.getElementById("pcModal");
  const periodCheckboxes = document.querySelectorAll('input[name="periodIds"]');

  // Function to fetch booked data from API
  async function fetchBookedData(date) {
    try {
      const response = await fetch(`/api/reservations/booked?date=${date}`);
      if (!response.ok) {
        console.error("Failed to fetch booked data");
        return {};
      }
      return await response.json();
    } catch (error) {
      console.error("Error fetching booked data:", error);
      return {};
    }
  }

  if (pcSelect && dateInput) {
    async function updatePeriodCheckboxes() {
      const selectedPcId = pcSelect.value;
      const selectedDate = dateInput.value;

      if (!selectedPcId || !selectedDate) return;

      // Fetch latest data based on selected date
      const bookedData = await fetchBookedData(selectedDate);
      const bookedPeriods = bookedData[selectedPcId] || [];

      periodCheckboxes.forEach((checkbox) => {
        const periodValue = parseInt(checkbox.value, 10);
        const formCheckDiv = checkbox.closest(".form-check");

        if (bookedPeriods.includes(periodValue)) {
          checkbox.disabled = true;
          checkbox.checked = false;
          if (formCheckDiv) {
            formCheckDiv.classList.add("text-muted");
            // Optional: Add tooltip or title to indicate why it's disabled
            formCheckDiv.title = "既に予約されています";
          }
        } else {
          // Only enable if it's not disabled for other reasons (though logic here seems to assume re-enabling is fine)
          // We need to be careful not to enable if it was disabled by something else, but here we control it.
          checkbox.disabled = false;
          if (formCheckDiv) {
            formCheckDiv.classList.remove("text-muted");
            formCheckDiv.removeAttribute("title");
          }
        }
      });
    }

    pcSelect.addEventListener("change", updatePeriodCheckboxes);
    dateInput.addEventListener("change", updatePeriodCheckboxes);

    // Initial check triggers immediately (or we can wait for user interaction, but better to initialize state)
    // Note: The original code used `localBookedData` passed from Thymeleaf.
    // We can keep using it for initial load to save a request, or just use the async updating for consistency.
    // Let's rely on the async update to ensure we have the correct data for the date in the modal.
    updatePeriodCheckboxes();
  }

  const selectAmBtn = document.getElementById("modal-select-am");
  const selectPmBtn = document.getElementById("modal-select-pm");
  const selectAllDayBtn = document.getElementById("modal-select-all-day");
  const selectClearBtn = document.getElementById("modal-select-clear");

  if (selectAmBtn) {
    selectAmBtn.addEventListener("click", function () {
      periodCheckboxes.forEach(function (checkbox) {
        const periodValue = parseInt(checkbox.value, 10);
        if (!checkbox.disabled) {
          checkbox.checked = periodValue <= 2;
        }
      });
    });
  }

  if (selectPmBtn) {
    selectPmBtn.addEventListener("click", function () {
      periodCheckboxes.forEach(function (checkbox) {
        const periodValue = parseInt(checkbox.value, 10);
        if (!checkbox.disabled) {
          checkbox.checked = periodValue > 2;
        }
      });
    });
  }

  if (selectAllDayBtn) {
    selectAllDayBtn.addEventListener("click", function () {
      periodCheckboxes.forEach(function (checkbox) {
        if (!checkbox.disabled) {
          checkbox.checked = true;
        }
      });
    });
  }

  if (selectClearBtn) {
    selectClearBtn.addEventListener("click", function () {
      periodCheckboxes.forEach(function (checkbox) {
        checkbox.checked = false;
      });
    });
  }
});
