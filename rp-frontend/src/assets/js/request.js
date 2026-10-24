function toggleRejectSection(button) {
    const section = button.nextElementSibling;

    if (section.style.display === "none" || section.style.display === "") {
        section.style.display = "block";
        button.textContent = "Cancel Rejection";
    } else {
        section.style.display = "none";
        button.textContent = "Reject";
    }
}


function showSection(type) {
    const sections = ['confirmation', 'modification', 'cancellation'];

    // Hide all sections
    sections.forEach(s => {
        document.getElementById('section-' + s).classList.add('hidden-section');
    });

    // Show the selected section
    document.getElementById('section-' + type).classList.remove('hidden-section');

    // Handle nav link highlighting
    const navLinks = document.querySelectorAll('.requests-nav-options li a');
    navLinks.forEach(link => link.classList.remove('active-tab'));

    const activeLink = document.querySelector(`.requests-nav-options li a[onclick="showSection('${type}')"]`);
    if (activeLink) {
        activeLink.classList.add('active-tab');
    }
}

// Optional: show confirmation section by default on load
document.addEventListener('DOMContentLoaded', () => {
    showSection('confirmation');
});