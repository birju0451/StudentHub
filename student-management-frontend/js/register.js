document.addEventListener("DOMContentLoaded", function () {
  const registerForm = document.getElementById("registerForm");

  if (!registerForm) {
    return;
  }

  const nameInput = document.getElementById("name");
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");
  const message = document.getElementById("message");
  const submitButton = registerForm.querySelector('button[type="submit"]');

  registerForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const name = nameInput.value.trim();
    const email = emailInput.value.trim();
    const password = passwordInput.value;

    message.textContent = "";
    message.style.color = "#b42318";

    if (!name || !email || !password) {
      message.textContent = "Please fill in all fields.";
      return;
    }

    if (password.length < 8) {
      message.textContent = "Password must contain at least 8 characters.";
      return;
    }

    submitButton.disabled = true;
    submitButton.textContent = "Creating account...";

    try {
      const result = await registerUser({
        name: name,
        email: email,
        password: password,
      });

      message.style.color = "green";
      message.textContent =
        result.message || "Registration successful! Redirecting to login...";

      registerForm.reset();

      setTimeout(function () {
        window.location.href = "index.html";
      }, 1500);
    } catch (error) {
      message.style.color = "#b42318";
      message.textContent =
        error.message || "Registration failed. Please try again.";
    } finally {
      submitButton.disabled = false;
      submitButton.textContent = "Create Account";
    }
  });
});
