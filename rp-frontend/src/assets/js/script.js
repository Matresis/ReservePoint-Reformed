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
