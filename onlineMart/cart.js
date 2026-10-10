let cart = JSON.parse(localStorage.getItem("sathanaCart")) || [];

const ADDRESS_KEY = "sathanaDeliveryDetails";
const ORDER_COUNT_KEY = "sathanaOrderCount";

let originalAddress = null;
let isEditingAddress = false;

// ==============================
// DISPLAY CART
// ==============================

function displayCart() {
    const cartList = document.getElementById("cartList");
    const cartTotal = document.getElementById("cartTotal");

    cartList.innerHTML = "";

    if (!cart.length) {
        cartList.innerHTML = "<p>Your cart is empty.</p>";
        cartTotal.innerText = "Total: ₹0";
        return;
    }

    let total = 0;

    cart.forEach(product => {
        const itemTotal = Number(product.price) * Number(product.quantity);
        total += itemTotal;

        const div = document.createElement("div");
        div.className = "cart-item";

        const heading = document.createElement("h3");
        heading.textContent = product.name || "Product";

        const price = document.createElement("p");
        price.textContent = "Price: ₹" + product.price;

        const subtotal = document.createElement("p");
        subtotal.textContent = "Subtotal: ₹" + itemTotal;

        const decrease = document.createElement("button");
        decrease.textContent = "−";
        decrease.type = "button";
        decrease.onclick = () => decreaseQuantity(product.id);

        const quantity = document.createElement("span");
        quantity.textContent = " " + product.quantity + " ";

        const increase = document.createElement("button");
        increase.textContent = "+";
        increase.type = "button";
        increase.onclick = () => increaseQuantity(product.id);

        const remove = document.createElement("button");
        remove.textContent = "Remove";
        remove.type = "button";
        remove.onclick = () => removeFromCart(product.id);

        if (product.image) {
            const img = document.createElement("img");
            img.src = product.image;
            img.alt = product.name || "Product";
            img.width = 90;
            div.appendChild(img);
        }

        div.append(heading, price, subtotal, decrease, quantity, increase, remove);
        cartList.appendChild(div);
    });

    cartTotal.innerText = "Total: ₹" + total;
}

// ==============================
// CART CONTROLS
// ==============================

function saveCart() {
    localStorage.setItem("sathanaCart", JSON.stringify(cart));
}

function increaseQuantity(id) {
    const product = cart.find(item => String(item.id) === String(id));
    if (product) {
        product.quantity++;
        saveCart();
        displayCart();
    }
}

function decreaseQuantity(id) {
    const product = cart.find(item => String(item.id) === String(id));
    if (!product) return;

    if (product.quantity > 1) {
        product.quantity--;
        saveCart();
        displayCart();
    } else {
        removeFromCart(id);
    }
}

function removeFromCart(id) {
    cart = cart.filter(item => String(item.id) !== String(id));
    saveCart();
    displayCart();
}

function goBack() {
    window.location.href = "/buyer.html";
}

// ==============================
// DELIVERY DETAILS
// ==============================

function getBuyerEmail() {
    return (localStorage.getItem("sathanaUserEmail") || "")
        .trim().toLowerCase();
}

function getAddressStorageKey() {
    return ADDRESS_KEY + "_" + getBuyerEmail();
}

function getOrderCountStorageKey() {
    return ORDER_COUNT_KEY + "_" + getBuyerEmail();
}

function readDeliveryDetails() {
    try {
        return JSON.parse(localStorage.getItem(getAddressStorageKey())) || null;
    } catch {
        return null;
    }
}

function fillDeliveryForm(details) {
    document.getElementById("customerName").value = details.name || "";
    document.getElementById("customerPhone").value = details.phone || "";
    document.getElementById("customerGender").value = details.gender || "";
    document.getElementById("customerAddress").value = details.address || "";
    document.getElementById("customerCity").value = details.city || "";
    document.getElementById("customerPincode").value = details.pincode || "";
}

function setAddressFieldsDisabled(disabled) {
    [
        "customerName",
        "customerPhone",
        "customerGender",
        "customerAddress",
        "customerCity",
        "customerPincode"
    ].forEach(id => {
        document.getElementById(id).disabled = disabled;
    });
}

function updateAddressButtons(hasSavedAddress) {
    document.getElementById("editAddressBtn").hidden =
        !hasSavedAddress || isEditingAddress;

    document.getElementById("cancelAddressEditBtn").hidden =
        !hasSavedAddress || !isEditingAddress;

    document.getElementById("saveAddressBtn").hidden =
        hasSavedAddress && !isEditingAddress;
}

function initializeDeliveryDetails() {
    const email = getBuyerEmail();
    const message = document.getElementById("savedAddressMessage");

    if (!email) {
        message.textContent = "Please log in to save your delivery details.";
        return;
    }

    const details = readDeliveryDetails();

    if (details) {
        originalAddress = { ...details };
        fillDeliveryForm(details);
        setAddressFieldsDisabled(true);
        message.textContent = "Saved delivery details loaded.";
        updateAddressButtons(true);
    } else {
        setAddressFieldsDisabled(false);
        message.textContent = "Enter your delivery details and save them.";
        updateAddressButtons(false);
    }
}

function enableAddressEdit() {
    const details = readDeliveryDetails();
    if (!details) return;

    originalAddress = { ...details };
    isEditingAddress = true;
    fillDeliveryForm(details);
    setAddressFieldsDisabled(false);
    updateAddressButtons(true);

    document.getElementById("savedAddressMessage").textContent =
        "Edit your details and save the changes.";
}

function cancelAddressEdit() {
    if (originalAddress) {
        fillDeliveryForm(originalAddress);
    }

    isEditingAddress = false;
    setAddressFieldsDisabled(true);
    updateAddressButtons(true);

    document.getElementById("savedAddressMessage").textContent =
        "Changes cancelled.";
}

function getFormDetails() {
    return {
        name: document.getElementById("customerName").value.trim(),
        phone: document.getElementById("customerPhone").value.trim(),
        gender: document.getElementById("customerGender").value,
        address: document.getElementById("customerAddress").value.trim(),
        city: document.getElementById("customerCity").value.trim(),
        pincode: document.getElementById("customerPincode").value.trim()
    };
}

function validateDeliveryDetails(details) {
    if (!details.name || !details.address || !details.city) {
        alert("Please fill in all required delivery details.");
        return false;
    }

    if (!/^[0-9]{10}$/.test(details.phone)) {
        alert("Enter a valid 10-digit phone number.");
        return false;
    }

    if (!/^[0-9]{6}$/.test(details.pincode)) {
        alert("Enter a valid 6-digit pincode.");
        return false;
    }

    return true;
}

function saveDeliveryAddress() {
    const email = getBuyerEmail();

    if (!email) {
        alert("Please log in first.");
        window.location.href = "/index.html";
        return;
    }

    const details = getFormDetails();

    if (!validateDeliveryDetails(details)) return;

    localStorage.setItem(
        getAddressStorageKey(),
        JSON.stringify(details)
    );

    originalAddress = { ...details };
    isEditingAddress = false;

    setAddressFieldsDisabled(true);
    updateAddressButtons(true);

    document.getElementById("savedAddressMessage").textContent =
        "Delivery details saved successfully!";

    alert("Delivery details saved!");
}

// ==============================
// PLACE ORDER - COD
// ==============================

async function placeOrder() {
    cart = JSON.parse(localStorage.getItem("sathanaCart")) || [];

    if (!Array.isArray(cart) || cart.length === 0) {
        alert("Cart is empty!");
        return;
    }

    const buyerEmail = getBuyerEmail();

    if (!buyerEmail) {
        alert("Please log in before placing an order.");
        window.location.href = "/index.html";
        return;
    }

    const details = readDeliveryDetails();

    if (!details || !validateDeliveryDetails(details)) {
        alert("Please save your delivery details before placing an order.");
        return;
    }

    const paymentMethod =
        document.querySelector('input[name="paymentMethod"]:checked')?.value
        || "Cash on Delivery";

    if (paymentMethod !== "Cash on Delivery") {
        alert("Currently, only Cash on Delivery is available.");
        return;
    }

    const button = document.getElementById("placeOrderBtn");
    button.disabled = true;

    try {
        for (const product of cart) {
            const quantity = Number(product.quantity);
            const price = Number(product.price);

            if (!Number.isFinite(price) || price < 0 ||
                !Number.isInteger(quantity) || quantity < 1) {
                throw new Error("Invalid product price or quantity.");
            }

            const formData = new URLSearchParams({
                product_name: product.name || "",
                price: String(price),
                quantity: String(quantity),
                total: String(price * quantity),
                buyer_email: buyerEmail,
                payment_method: "Cash on Delivery",

                customer_name: details.name,
                customer_phone: details.phone,
                customer_gender: details.gender,
                delivery_address: details.address,
                delivery_city: details.city,
                delivery_pincode: details.pincode
            });

            const response = await fetch("/orders", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: formData.toString()
            });

            const text = await response.text();

            if (!response.ok ||
                !text.includes("Order Placed Successfully!")) {
                throw new Error(text || "Order placement failed.");
            }
        }

        const countKey = getOrderCountStorageKey();
        const previousCount =
            Number(localStorage.getItem(countKey)) || 0;

        localStorage.setItem(countKey, String(previousCount + cart.length));

        localStorage.removeItem("sathanaCart");

        //alert("Order Placed Successfully! Payment: Cash on Delivery");
        showOrderConfirmation();
        localStorage.removeItem("sathanaCart");
        

    } catch (error) {
        console.error("Order Error:", error);
        alert("Order placement failed: " + error.message);
    } finally {
        button.disabled = false;
    }
}

// ==============================
// FORM SUBMIT
// ==============================

document.getElementById("checkoutForm").addEventListener("submit", function(event) {
    event.preventDefault();
    placeOrder();
});

// ==============================
// START
// ==============================

displayCart();
initializeDeliveryDetails();

function showOrderConfirmation() {
    const overlay = document.createElement("div");

    overlay.id = "orderSuccessOverlay";

    overlay.innerHTML = `
        <div class="success-content">
            <div class="success-icon">✓</div>
            <h2>Order Confirmed!</h2>
            <p>Your order has been placed successfully.</p>
            <p>Thank you for shopping with SathanaMart 💚</p>
            <p class="redirect-message">Redirecting to your shopping page...</p>
        </div>
    `;

    document.body.appendChild(overlay);

    setTimeout(() => {
        window.location.href = "/buyer.html";
    }, 2500);
}