function goBackToBuyer() {
    window.location.href = "/buyer.html";
}

function logout() {
    localStorage.removeItem("sathanaUserEmail");
    localStorage.removeItem("sathanaUserRole");
    window.location.href = "/index.html";
}

async function loadMyOrders() {
    const ordersList = document.getElementById("ordersList");
    const userInfo = document.getElementById("userInfo");

    const buyerEmail = localStorage.getItem("sathanaUserEmail");

    if (!buyerEmail || !buyerEmail.trim()) {
        if (userInfo) userInfo.textContent = "You are not logged in.";
        if (ordersList) {
            ordersList.innerHTML = `
                <div class="empty-orders">
                    <h3>Please log in to view your orders.</h3>
                    <p><a href="/index.html">Click here to Login</a></p>
                </div>`;
        }
        return;
    }

    const cleanEmail = buyerEmail.trim();

    if (userInfo) userInfo.textContent = "Logged in as: " + cleanEmail;
    if (ordersList) ordersList.textContent = "Loading your orders...";

    try {
        const response = await fetch(
            "/orders?buyer_email=" + encodeURIComponent(cleanEmail),
            { cache: "no-store" }
        );

        if (!response.ok) {
            throw new Error("Server error: " + response.status);
        }

        const orders = await response.json();
        ordersList.innerHTML = "";

        if (!Array.isArray(orders) || orders.length === 0) {
            ordersList.innerHTML = `
                <div class="empty-orders">
                    <h3>No orders found</h3>
                    <p>Your orders will appear here after you place an order.</p>
                </div>`;
            return;
        }

        orders.forEach(order => {
            const card = document.createElement("div");
            card.className = "order-card";

            const details = [
                ["Order ID", order.id],
                ["Product", order.product_name],
                ["Price", "₹" + order.price],
                ["Quantity", order.quantity],
                ["Total", "₹" + order.total],
                ["Status", order.status || "Pending"],
                ["Order Date", order.order_date || "Not available"]
            ];

            details.forEach(([label, value]) => {
                const p = document.createElement("p");
                const strong = document.createElement("strong");

                strong.textContent = label + ": ";
                p.append(strong, document.createTextNode(String(value ?? "")));
                card.appendChild(p);
            });

            const status = String(order.status || "").trim().toLowerCase();

            if (status === "delivered") {
                addReviewForm(card, order, cleanEmail);
            } else {
                const note = document.createElement("p");
                note.className = "review-waiting";
                note.textContent = "⭐ Rating will be available after delivery.";
                card.appendChild(note);
            }

            ordersList.appendChild(card);
        });
    } catch (error) {
        console.error("My Orders error:", error);
        if (ordersList) {
            ordersList.textContent =
                "Unable to load orders. Check the backend server.";
        }
    }
}

function addReviewForm(card, order, buyerEmail) {
    const section = document.createElement("section");
    section.className = "review-section";

    const title = document.createElement("h3");
    title.textContent = "Rate this product";

    const hint = document.createElement("p");
    hint.textContent = "How was your experience?";

    const stars = document.createElement("div");
    stars.className = "rating-stars";

    let selectedRating = 5;

    const starButtons = [];

    function updateStars() {
        starButtons.forEach((button, index) => {
            button.classList.toggle("selected", index < selectedRating);
            button.setAttribute("aria-pressed", String(index < selectedRating));
        });
    }

    for (let rating = 1; rating <= 5; rating++) {
        const star = document.createElement("button");
        star.type = "button";
        star.className = "rating-star";
        star.textContent = "★";
        star.setAttribute("aria-label", rating + " star rating");

        star.addEventListener("click", () => {
            selectedRating = rating;
            updateStars();
        });

        starButtons.push(star);
        stars.appendChild(star);
    }

    updateStars();

    const comment = document.createElement("textarea");
    comment.className = "review-comment";
    comment.maxLength = 1000;
    comment.placeholder = "Write a review (optional)";

    const submit = document.createElement("button");
    submit.type = "button";
    submit.className = "submit-review";
    submit.textContent = "Submit Review";

    const message = document.createElement("p");
    message.className = "review-message";
    message.setAttribute("role", "status");

    submit.addEventListener("click", async () => {
        if (submit.disabled) return;

        submit.disabled = true;
        message.textContent = "Submitting your review...";

        const data = new URLSearchParams({
            order_id: String(order.id),
            buyer_email: buyerEmail,
            rating: String(selectedRating),
            review_comment: comment.value.trim()
        });

        try {
            const response = await fetch("/reviews", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data.toString()
            });

            const result = await response.text();

            if (response.status === 409) {
                message.textContent =
                    "You have already reviewed this order.";
                section.querySelectorAll("button, textarea").forEach(el => {
                    el.disabled = true;
                });
                return;
            }

            if (!response.ok) {
                throw new Error(result || "Review submission failed.");
            }

            message.textContent = "✓ Thank you! Your review was submitted.";
            section.querySelectorAll("button, textarea").forEach(el => {
                el.disabled = true;
            });
        } catch (error) {
            console.error("Review error:", error);
            message.textContent =
                "Unable to submit review. Please try again.";
            submit.disabled = false;
        }
    });

    section.append(title, hint, stars, comment, submit, message);
    card.appendChild(section);
}

loadMyOrders();