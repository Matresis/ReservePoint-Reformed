document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        height: 'auto',
        weekends: false,
        locale: 'en',
        headerToolbar: {
            left: 'prev,next',
            center: 'title',
            right: 'timeGridWeek,timeGridDay'
        },
        events: {
            url: '/admin/calendar/events',
            success: function(events) {
                const highlightId = sessionStorage.getItem("highlightReservationId");
                if (highlightId) {
                    events.forEach(event => {
                        if (event.id && event.id.toString() === highlightId) {
                            event.color = '#FF7F50';  // Coral highlight color
                            event.textColor = 'white';
                        }
                    });
                    sessionStorage.removeItem("highlightReservationId");
                }
                return events;
            }
        },
        slotMinTime: "08:00:00",
        slotMaxTime: "18:00:00",
        nowIndicator: true,
        eventClick: function (info) {
            console.log("Clicked event:", info.event);

            if (!info.event.id) {
                console.error("Event ID is missing:", info.event);
                return;
            }

            fetch(`/admin/calendar/${info.event.id}`)
                .then(response => response.json())
                .then(data => {
                    console.log("Fetched reservation:", data);
                    showReservationPopup(data);
                })
                .catch(error => console.error('Error loading reservation:', error));
        },
        datesSet: function () {
            setTimeout(() => {
                let timeGridSlots = document.querySelector(".fc-timegrid-slots");
                let timeGridCols = document.querySelector(".fc-timegrid-cols");

                if (timeGridSlots && timeGridCols) {
                    let height = timeGridSlots.offsetHeight;
                    timeGridCols.style.height = height + "px";
                }
            }, 500);
        }
    });

    calendar.render();

    if (window.highlightedReservationId) {
        fetch(`/admin/calendar/${window.highlightedReservationId}`)
            .then(response => response.json())
            .then(data => {
                if (data.formattedOrderTime && data.formattedOrderTime !== "N/A") {
                    // Convert your dd.MM.yyyy HH:mm format to a JS Date object
                    // Note: FullCalendar's gotoDate prefers ISO format or Date objects
                    const dateParts = data.formattedOrderTime.split(' ');
                    const dayParts = dateParts[0].split('.');
                    const isoDate = `${dayParts[2]}-${dayParts[1]}-${dayParts[0]}T${dateParts[1]}`;

                    // 1. Move the calendar to that week
                    calendar.gotoDate(isoDate);

                    // 2. (Optional) Open the popup automatically
                    showPopup(data);
                }
            })
            .catch(err => console.error("Error fetching highlighted reservation:", err));
    }

    // Back button logic
    const backButton = document.getElementById("return-button");

    if (sessionStorage.getItem("fromReservationPage") === "true") {
        backButton.href = "/admin/reservations";
        backButton.textContent = "Back to Reservations";
        sessionStorage.removeItem("fromReservationPage");
    } else {
        backButton.href = "/admin";
    }
});

// Toggle edit section
function toggleEditSection() {
    const editSection = document.getElementById("popup-editSection");
    const editButton = document.getElementById("editButton");

    if (editSection.style.display === "none") {
        editSection.style.display = "block";
        editButton.textContent = "Cancel Edit";
    } else {
        editSection.style.display = "none";
        editButton.textContent = "Edit Reservation";
    }
}

// Show reservation popup
function showReservationPopup(reservation) {
    if (!reservation || !reservation.id) {
        console.error("Reservation ID is missing:", reservation);
        return;
    }

    document.getElementById("popup-name").textContent = reservation.customer.user.name;
    document.getElementById("popup-surname").textContent = reservation.customer.user.surname;
    document.getElementById("popup-email").textContent = reservation.customer.user.email;
    document.getElementById("popup-phone").textContent = reservation.customer.phone;
    document.getElementById("popup-service").textContent = reservation.service.name;
    document.getElementById("popup-created").textContent = reservation.formattedCreatedAt;
    document.getElementById("popup-status").textContent = reservation.status;
    document.getElementById("popup-date").textContent = reservation.formattedOrderTime;
    document.getElementById("popup-notes").textContent = reservation.notes;

    const detailLink = document.getElementById("reservation-detail-link");
    if (reservation.id) {
        detailLink.href = `/admin/reservations/${reservation.id}`;
    } else {
        console.error("Reservation ID is missing in popup", reservation);
    }

    document.getElementById("popup").style.display = "flex";
}

// Close the pop-up
function closePopup() {
    document.getElementById("popup").style.display = "none";
}

document.addEventListener('DOMContentLoaded', () => {
    // Get the current URL path
    const currentPath = window.location.pathname;

    // Get all nav links
    const navLinks = document.querySelectorAll('.nav-options li a');

    // Loop through the nav links and set active-tab based on the current page
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active-tab');
        } else {
            link.classList.remove('active-tab');
        }
    });
});
