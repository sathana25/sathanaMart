let cart = JSON.parse(localStorage.getItem("sathanaCart")) || [];

function displayCart() {

    let cartList = document.getElementById("cartList");
    let cartTotal = document.getElementById("cartTotal");

    cartList.innerHTML = "";

    if (cart.length === 0) {

        cartList.innerHTML = "<p>Your cart is empty.</p>";
        cartTotal.innerText = "Total: ₹0";
        return;
    }

    let total = 0;

    cart.forEach(function(product) {

        let itemTotal = product.price * product.quantity;

        total += itemTotal;

        let div = document.createElement("div");

        div.className = "cart-item";

        let imageHTML = "";

        if (product.image) {

            imageHTML =
                `<img src="${product.image}" alt="${product.name}">`;

        }

        div.innerHTML = `

            ${imageHTML}

            <h3>${product.name}</h3>

            <p>Price: ₹${product.price}</p>

            <p>Quantity: ${product.quantity}</p>

            <p>Subtotal: ₹${itemTotal}</p>

            <button onclick="decreaseQuantity(${product.id})">
                -
            </button>

            <button onclick="increaseQuantity(${product.id})">
                +
            </button>

            <button onclick="removeFromCart(${product.id})">
                Remove
            </button>

        `;

        cartList.appendChild(div);
    });

    cartTotal.innerText = "Total: ₹" + total;
}


function increaseQuantity(id) {

    let product = cart.find(function(item) {
        return item.id === id;
    });

    if (product) {

        product.quantity++;

        saveCart();

        displayCart();
    }
}


function decreaseQuantity(id) {

    let product = cart.find(function(item) {
        return item.id === id;
    });

    if (product) {

        if (product.quantity > 1) {

            product.quantity--;

        } else {

            removeFromCart(id);
            return;
        }

        saveCart();

        displayCart();
    }
}


function removeFromCart(id) {

    cart = cart.filter(function(item) {
        return item.id !== id;
    });

    saveCart();

    displayCart();
}


function saveCart() {

    localStorage.setItem(
        "sathanaCart",
        JSON.stringify(cart)
    );
}


function goBack() {

    window.location.href = "/buyer.html";
}


displayCart();