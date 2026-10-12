document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", async function (event) {
      event.preventDefault();

      const email = document.getElementById("email").value.trim();
      const password = document.getElementById("password").value;
      const message = document.getElementById("message");
      const button = document.getElementById("loginButton");

      message.textContent = "";
      button.disabled = true;
      button.textContent = "Signing in...";

      try {
        const user = await loginUser({ email, password });

        sessionStorage.setItem("apiCredentials", btoa(email + ":" + password));

        // Store basic user information for the frontend.
        sessionStorage.setItem("loggedInUser", JSON.stringify(user));

        message.style.color = "green";
        message.textContent = "Login successful! Redirecting...";

        if (user.role === "ADMIN") {
          window.location.href = "pages/admin-dashboard.html";
        } else if (user.role === "STUDENT") {
          window.location.href = "pages/student-dashboard.html";
        } else {
          sessionStorage.removeItem("loggedInUser");
          message.style.color = "#b42318";
          message.textContent = "Unknown user role.";
        }
      } catch (error) {
        message.style.color = "#b42318";
        message.textContent = error.message;
      } finally {
        button.disabled = false;
        button.textContent = "Sign In";
      }
    });
  }
});
