// =========================================
// SATHANAMART BUYER.JS
// =========================================

// CART AND WISHLIST
let cart =
    JSON.parse(localStorage.getItem("sathanaCart")) || [];

let wishlist =
    JSON.parse(localStorage.getItem("sathanaWishlist")) || [];

let allProducts = [];
let selectedCategory = "All";


// =========================================
// LOAD PRODUCTS
// =========================================

function loadProducts() {
    const productList = document.getElementById("productList");

    if (productList) {
        productList.innerHTML = "<p>Loading products...</p>";
    }

    fetch("/products", { cache: "no-store" })
        .then(async response => {
            const result = await response.json();

            if (!response.ok) {
                throw new Error(
                    result.error || "Products API failed"
                );
            }

            if (!Array.isArray(result)) {
                throw new Error(
                    "Products API did not return a product list."
                );
            }

            return result;
        })
        .then(products => {
            allProducts = products;

            // For You shows all products by default.
            selectedCategory = "All";

            const categoryTitle =
                document.getElementById("categoryTitle");

            if (categoryTitle) {
                categoryTitle.innerText = "For You";
            }

            displayProducts(getFilteredProducts());

            console.log("Products loaded:", allProducts.length);
        })
        .catch(error => {
            console.error("Error loading products:", error);

            if (productList) {
                productList.innerHTML =
                    `<p>Unable to load products: ${escapeHTML(error.message)}</p>`;
            }
        });
}


// =========================================
// DISPLAY PRODUCTS
// =========================================

function displayProducts(products) {
    const productList =
        document.getElementById("productList");

    if (!productList) return;

    productList.innerHTML = "";

    if (!products || products.length === 0) {
        productList.innerHTML =
            "<p>No products found.</p>";
        return;
    }

    products.forEach(product => {
        const card = document.createElement("div");
        card.className = "product";

        const image = document.createElement("img");
        image.alt = product.name || "Product";
        image.src = product.image || "";
        image.style.width = "200px";
        image.style.height = "200px";
        image.style.objectFit = "contain";
        image.style.display = product.image ? "block" : "none";

        image.onerror = function () {
            this.style.display = "none";
        };

        const heartButton = document.createElement("button");
        heartButton.className = "wishlist-heart";

        const isWishlisted = wishlist.some(
            item => Number(item.id) === Number(product.id)
        );

        heartButton.textContent = isWishlisted ? "❤️" : "♡";
        heartButton.setAttribute(
            "aria-label",
            isWishlisted ? "Remove from wishlist" : "Add to wishlist"
        );

        heartButton.addEventListener("click", function () {
            toggleWishlist(product.id);
        });

        const name = document.createElement("h3");
        name.textContent = product.name || "Unnamed product";

        const price = document.createElement("p");
        price.textContent = "Price: ₹" + product.price;

        const stock = document.createElement("p");
        stock.textContent = "Stock: " + product.stock;

        const category = document.createElement("p");
        category.textContent = "Category: " + (product.category || "Other");

        const addButton = document.createElement("button");
        addButton.textContent = "Add to Cart";
        addButton.disabled = Number(product.stock) <= 0;

        if (addButton.disabled) {
            addButton.textContent = "Out of Stock";
        }

        addButton.addEventListener("click", function () {
            addToCart(product.id);
        });

        card.appendChild(image);
        card.appendChild(heartButton);
        card.appendChild(name);
        card.appendChild(price);
        card.appendChild(stock);
        card.appendChild(category);
        card.appendChild(addButton);

        productList.appendChild(card);
    });
}


// =========================================
// CATEGORY FILTER
// =========================================

function filterCategory(category, button) {
    selectedCategory = category || "All";

    document.querySelectorAll(".category-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    if (button) {
        button.classList.add("active");
    }

    const title = document.getElementById("categoryTitle");

    if (title) {
        title.innerText =
            selectedCategory === "All"
                ? "For You"
                : selectedCategory === "Mobile"
                    ? "Mobiles"
                    : selectedCategory;
    }

    displayProducts(getFilteredProducts());
}


// =========================================
// GET FILTERED PRODUCTS
// =========================================

function getFilteredProducts() {
    const searchBox = document.getElementById("searchBox");

    const searchText = searchBox
        ? searchBox.value.toLowerCase().trim()
        : "";

    return allProducts.filter(product => {
        const name = (product.name || "").toLowerCase();
        const category = (product.category || "").toLowerCase();

        const nameMatch = name.includes(searchText);

        const categoryMatch =
            selectedCategory === "All" ||
            category === selectedCategory.toLowerCase() ||
            (selectedCategory === "Mobile" && category === "mobiles");

        return nameMatch && categoryMatch;
    });
}


// =========================================
// SEARCH PRODUCTS
// =========================================

function filterProducts() {
    displayProducts(getFilteredProducts());
}


// =========================================
// WISHLIST
// =========================================

function toggleWishlist(id) {
    const product = allProducts.find(
        item => Number(item.id) === Number(id)
    );

    if (!product) return;

    const exists = wishlist.some(
        item => Number(item.id) === Number(id)
    );

    if (exists) {
        wishlist = wishlist.filter(
            item => Number(item.id) !== Number(id)
        );
    } else {
        wishlist.push({
            id: product.id,
            name: product.name,
            price: product.price,
            image: product.image || ""
        });
    }

    saveWishlist();
    displayProducts(getFilteredProducts());
}


function saveWishlist() {
    localStorage.setItem(
        "sathanaWishlist",
        JSON.stringify(wishlist)
    );
}


function goToWishlist() {
    window.location.href = "/wishlist.html";
}


// =========================================
// CART
// =========================================

function addToCart(id) {
    const product = allProducts.find(
        item => Number(item.id) === Number(id)
    );

    if (!product) {
        alert("Product not found!");
        return;
    }

    const stock = Number(product.stock) || 0;

    if (stock <= 0) {
        alert("Product is out of stock!");
        return;
    }

    const existing = cart.find(
        item => Number(item.id) === Number(id)
    );

    if (existing) {
        if (Number(existing.quantity) >= stock) {
            alert("Stock limit reached!");
            return;
        }

        existing.quantity = Number(existing.quantity) + 1;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: Number(product.price),
            image: product.image || "",
            quantity: 1
        });
    }

    saveCart();
    displayCart();

    alert("Product added to cart!");
}


function saveCart() {
    localStorage.setItem(
        "sathanaCart",
        JSON.stringify(cart)
    );
}


function displayCart() {
    const cartList = document.getElementById("cartList");
    const cartTotal = document.getElementById("cartTotal");

    // Buyer page does not necessarily contain cart elements.
    if (!cartList || !cartTotal) return;

    cartList.innerHTML = "";

    if (cart.length === 0) {
        cartList.innerHTML = "<p>Your cart is empty.</p>";
        cartTotal.textContent = "Total: ₹0";
        return;
    }

    let total = 0;

    cart.forEach(product => {
        const quantity = Number(product.quantity) || 1;
        const price = Number(product.price) || 0;
        const subtotal = price * quantity;

        total += subtotal;

        const item = document.createElement("div");
        item.className = "cart-item";

        const name = document.createElement("h3");
        name.textContent = product.name;

        const priceText = document.createElement("p");
        priceText.textContent = "Price: ₹" + price;

        const quantityText = document.createElement("p");
        quantityText.textContent = "Quantity: " + quantity;

        const subtotalText = document.createElement("p");
        subtotalText.textContent = "Subtotal: ₹" + subtotal;

        const minus = document.createElement("button");
        minus.textContent = "-";
        minus.addEventListener("click", () => decreaseQuantity(product.id));

        const plus = document.createElement("button");
        plus.textContent = "+";
        plus.addEventListener("click", () => increaseQuantity(product.id));

        const remove = document.createElement("button");
        remove.textContent = "Remove";
        remove.addEventListener("click", () => removeFromCart(product.id));

        item.append(
            name,
            priceText,
            quantityText,
            minus,
            plus,
            subtotalText,
            remove
        );

        cartList.appendChild(item);
    });

    cartTotal.textContent = "Total: ₹" + total;
}


function increaseQuantity(id) {
    const item = cart.find(
        product => Number(product.id) === Number(id)
    );

    const original = allProducts.find(
        product => Number(product.id) === Number(id)
    );

    if (!item) return;

    if (original && Number(item.quantity) >= Number(original.stock)) {
        alert("Stock limit reached!");
        return;
    }

    item.quantity = Number(item.quantity) + 1;
    saveCart();
    displayCart();
}


function decreaseQuantity(id) {
    const item = cart.find(
        product => Number(product.id) === Number(id)
    );

    if (!item) return;

    if (Number(item.quantity) > 1) {
        item.quantity = Number(item.quantity) - 1;
    } else {
        cart = cart.filter(
            product => Number(product.id) !== Number(id)
        );
    }

    saveCart();
    displayCart();
}


function removeFromCart(id) {
    cart = cart.filter(
        product => Number(product.id) !== Number(id)
    );

    saveCart();
    displayCart();
}


function goToCart() {
    window.location.href = "/cart.html";
}


// =========================================
// PLACE ORDER
// =========================================

async function placeOrder() {
    if (cart.length === 0) {
        alert("Cart is empty!");
        return;
    }

    const buyerEmail =
        localStorage.getItem("sathanaUserEmail");

    if (!buyerEmail || buyerEmail.trim() === "") {
        alert("Please login again before placing an order.");
        window.location.href = "/index.html";
        return;
    }

    try {
        // Submit one request per product.
        const results = await Promise.all(
            cart.map(async product => {
                const data = new URLSearchParams({
                    product_name: product.name,
                    price: String(product.price),
                    quantity: String(product.quantity),
                    total: String(
                        Number(product.price) * Number(product.quantity)
                    ),
                    buyer_email: buyerEmail.trim()
                });

                const response = await fetch("/orders", {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },
                    body: data.toString()
                });

                const message = await response.text();

                if (
                    !response.ok ||
                    !message.includes("Order Placed Successfully!")
                ) {
                    throw new Error(message || "Order placement failed.");
                }

                return message;
            })
        );

        if (results.length > 0) {
            alert("Order Placed Successfully!");

            cart = [];
            saveCart();
            displayCart();
            window.location.href = "/orders.html";
        }
    } catch (error) {
        console.error("Order error:", error);
        alert("Order placement failed: " + error.message);
    }
}


// =========================================
// LOAD BUYER'S ORDER HISTORY
// =========================================

async function loadOrders() {
    const orderList = document.getElementById("orderList");

    // No order history section on this page.
    if (!orderList) return;

    const buyerEmail =
        localStorage.getItem("sathanaUserEmail");

    if (!buyerEmail || buyerEmail.trim() === "") {
        orderList.innerHTML =
            "<p>Please login to view your orders.</p>";
        return;
    }

    orderList.innerHTML = "<p>Loading your orders...</p>";

    try {
        const response = await fetch(
            "/orders?buyer_email=" + encodeURIComponent(buyerEmail.trim()),
            { cache: "no-store" }
        );

        const orders = await response.json();

        if (!response.ok || !Array.isArray(orders)) {
            throw new Error(
                orders.error || "Unable to load orders."
            );
        }

        orderList.innerHTML = "";

        if (orders.length === 0) {
            orderList.innerHTML = "<p>No orders found.</p>";
            return;
        }

        orders.forEach(order => {
            const card = document.createElement("div");
            card.className = "order";

            const title = document.createElement("h3");
            title.textContent = "Order ID: " + order.id;

            const product = document.createElement("p");
            product.textContent = "Product: " + order.product_name;

            const price = document.createElement("p");
            price.textContent = "Price: ₹" + order.price;

            const quantity = document.createElement("p");
            quantity.textContent = "Quantity: " + order.quantity;

            const total = document.createElement("p");
            total.textContent = "Total: ₹" + order.total;

            const status = document.createElement("p");
            status.textContent = "Status: " + (order.status || "Pending");

            const date = document.createElement("p");
            date.textContent = "Order Date: " + (order.order_date || "");

            card.append(
                title,
                product,
                price,
                quantity,
                total,
                status,
                date
            );

            orderList.appendChild(card);
        });
    } catch (error) {
        console.error("Error loading orders:", error);

        orderList.innerHTML =
            "<p>Unable to load orders. Please try again.</p>";
    }
}


// =========================================
// MY ORDERS PAGE
// =========================================

function goToOrders() {
    window.location.href = "/orders.html";
}

function logout() {
    localStorage.removeItem("sathanaUserEmail");
    localStorage.removeItem("sathanaUserRole");
    window.location.href = "/index.html";
}


// =========================================
// HTML TEXT HELPER
// =========================================

function escapeHTML(text) {
    return String(text ?? "").replace(/[&<>"']/g, character => {
        const entities = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return entities[character];
    });
}


// =========================================
// STARTUP
// =========================================

loadProducts();
loadOrders();
displayCart();