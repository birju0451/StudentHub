const API_BASE_URL = "http://localhost:8080";

// Common API request handler
async function apiRequest(endpoint, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  // Add HTTP Basic authentication for protected APIs
  if (options.requiresAuth) {
    const credentials = sessionStorage.getItem("apiCredentials");

    if (!credentials) {
      throw new Error("Please log in again.");
    }

    headers["Authorization"] = "Basic " + credentials;
  }

  const { requiresAuth, ...fetchOptions } = options;

  let response;

  try {
    response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...fetchOptions,
      headers,
    });
  } catch (error) {
    throw new Error(
      "Unable to connect to the backend. Make sure Spring Boot is running.",
    );
  }

  const responseText = await response.text();
  let data = {};

  if (responseText) {
    try {
      data = JSON.parse(responseText);
    } catch {
      data = { message: responseText };
    }
  }

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("Authentication failed. Please log in again.");
    }

    if (response.status === 403) {
      throw new Error("You are not allowed to perform this action.");
    }

    throw new Error(data.message || `Request failed (${response.status}).`);
  }

  return data;
}

// ================================
// AUTHENTICATION
// ================================

// Register a new student account
async function registerUser(userData) {
  return apiRequest("/auth/register", {
    method: "POST",
    body: JSON.stringify(userData),
  });
}

// Log in
async function loginUser(credentials) {
  return apiRequest("/auth/login", {
    method: "POST",
    body: JSON.stringify(credentials),
  });
}

// ================================
// FORGOT AND RESET PASSWORD
// ================================

// Request a password-reset email
async function requestPasswordReset(email) {
  return apiRequest("/auth/forgot-password", {
    method: "POST",
    body: JSON.stringify({ email }),
  });
}

// Reset password using the token from the email link
async function resetPassword(token, newPassword) {
  return apiRequest("/auth/reset-password", {
    method: "POST",
    body: JSON.stringify({
      token,
      newPassword,
    }),
  });
}

// ================================
// STUDENT MANAGEMENT (ADMIN)
// ================================

// Get all students
async function getAllStudents() {
  return apiRequest("/students", {
    method: "GET",
    requiresAuth: true,
  });
}

// Get one student by ID
async function getStudentById(studentId) {
  return apiRequest(`/students/${encodeURIComponent(studentId)}`, {
    method: "GET",
    requiresAuth: true,
  });
}

// Add a student
async function addStudent(studentData) {
  return apiRequest("/students", {
    method: "POST",
    body: JSON.stringify(studentData),
    requiresAuth: true,
  });
}

// Update a student
async function updateStudent(studentId, studentData) {
  return apiRequest(`/students/${encodeURIComponent(studentId)}`, {
    method: "PUT",
    body: JSON.stringify(studentData),
    requiresAuth: true,
  });
}

// Delete a student
async function deleteStudent(studentId) {
  return apiRequest(`/students/${encodeURIComponent(studentId)}`, {
    method: "DELETE",
    requiresAuth: true,
  });
}

// ================================
// STUDENT PROFILE
// ================================

// Get the logged-in student's own profile
async function getMyProfile() {
  return apiRequest("/students/my-profile", {
    method: "GET",
    requiresAuth: true,
  });
}

// ================================
// CHANGE PASSWORD
// ================================

// Change the logged-in user's password
async function changePassword(currentPassword, newPassword) {
  return apiRequest("/auth/change-password", {
    method: "PUT",
    requiresAuth: true,
    body: JSON.stringify({
      currentPassword,
      newPassword,
    }),
  });
}
