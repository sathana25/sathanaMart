const BACKEND_URL = "http://localhost:8080";

function showLogin() {
    document.getElementById("loginBox").style.display = "block";
    document.getElementById("registerBox").style.display = "none";
    document.getElementById("backLoginButton").style.display = "none";
    document.getElementById("message").innerHTML = "";
}

function showRegister() {
    document.getElementById("loginBox").style.display = "none";
    document.getElementById("registerBox").style.display = "block";
    document.getElementById("backLoginButton").style.display = "none";
    document.getElementById("message").innerHTML = "";
}

async function createAccount() {
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const role = document.getElementById("role").value;

    if (
        name === "" ||
        email === "" ||
        password === "" ||
        confirmPassword === "" ||
        role === ""
    ) {
        showMessage("Please fill all the fields.", "error");
        return;
    }

    if (password !== confirmPassword) {
        showMessage("Password does not match.", "error");
        return;
    }

    try {
        const data = new URLSearchParams();

        data.append("name", name);
        data.append("email", email);
        data.append("password", password);
        data.append("role", role);

        const response = await fetch(
            BACKEND_URL + "/register",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );

        const result = await response.text();

        if (response.ok) {
            showMessage(result, "success");

            document.getElementById("loginBox").style.display = "none";
            document.getElementById("registerBox").style.display = "none";
            document.getElementById("backLoginButton").style.display = "block";
        } else {
            showMessage("Account creation failed.", "error");
        }

    } catch (error) {
        console.log(error);
        showMessage("Unable to connect to Java backend.", "error");
    }
}

async function login() {
    const email = document.getElementById("loginEmail").value.trim();
    const password = document.getElementById("loginPassword").value;
    const role = document.getElementById("loginRole").value;

    if (
        email === "" ||
        password === "" ||
        role === ""
    ) {
        showMessage("Please fill all login details.", "error");
        return;
    }

    try {
        const data = new URLSearchParams();

        data.append("email", email);
        data.append("password", password);
        data.append("role", role);

        const response = await fetch(
            BACKEND_URL + "/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );

        const result = await response.text();

        if (response.ok) {
            showMessage(result, "success");

            if (role === "Seller") {
                setTimeout(function () {
                    window.location.href = "seller.html";
                }, 1000);

                return;
            }

            if (role === "Buyer") {
                setTimeout(function () {
                    window.location.href = "buyer.html";
                }, 1000);

                return;
            }

            if (role === "Admin") {
                return;
            }

        } else {
            showMessage(result, "error");
        }

    } catch (error) {
        console.log(error);
        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );
    }
}

function showMessage(text, type) {
    const message = document.getElementById("message");
    message.innerHTML = text;
    message.className = type;
}