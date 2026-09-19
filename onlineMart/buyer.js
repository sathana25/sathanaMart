let cart = [];

let allProducts = [];


// ==============================
// LOAD PRODUCTS
// ==============================

function loadProducts() {

    fetch("/products")

        .then(response => response.json())

        .then(products => {

            allProducts = products;

            displayProducts(products);

        })

        .catch(error => {

            console.log(
                "Error loading products:",
                error
            );

        });
}


// ==============================
// DISPLAY PRODUCTS
// ==============================

function displayProducts(products) {

    let productList =
        document.getElementById("productList");

    productList.innerHTML = "";


    if (products.length === 0) {

        productList.innerHTML =
            "<p>No products found.</p>";

        return;
    }


    products.forEach(function(product) {

        productList.innerHTML += `

            <div class="product">

                <h3>${product.name}</h3>

                <p>
                    Price: ₹${product.price}
                </p>

                <p>
                    Stock: ${product.stock}
                </p>

                <p>
                    Category: ${product.category}
                </p>


                <button
                    onclick="addToCart(
                        ${product.id},
                        '${product.name}',
                        ${product.price}
                    )"
                >
                    Add to Cart
                </button>

            </div>

            <hr>

        `;

    });
}


// ==============================
// SEARCH + CATEGORY FILTER
// ==============================

function filterProducts() {

    let searchText =
        document
            .getElementById("searchBox")
            .value
            .toLowerCase();


    let selectedCategory =
        document
            .getElementById("categoryFilter")
            .value;


    let filteredProducts =
        allProducts.filter(function(product) {


            let nameMatch =
                product.name
                    .toLowerCase()
                    .includes(searchText);


            let categoryMatch =
                selectedCategory === "All" ||
                product.category === selectedCategory;


            return nameMatch && categoryMatch;

        });


    displayProducts(filteredProducts);
}


// ==============================
// ADD TO CART
// ==============================

function addToCart(id, name, price) {

    let existingProduct =
        cart.find(function(product) {

            return product.id === id;

        });


    if (existingProduct) {

        existingProduct.quantity++;

    } else {

        cart.push({

            id: id,

            name: name,

            price: price,

            quantity: 1

        });

    }


    displayCart();
}


// ==============================
// DISPLAY CART
// ==============================

function displayCart() {

    let cartList =
        document.getElementById("cartList");

    let cartTotal =
        document.getElementById("cartTotal");

    cartList.innerHTML = "";

    let total = 0;

    cart.forEach(function(product) {

        let productTotal =
            product.price *
            product.quantity;

        total += productTotal;

        cartList.innerHTML += `

            <div class="cart-item">

                <h3>
                    ${product.name}
                </h3>

                <p>
                    Price: ₹${product.price}
                </p>

                <p>
                    Quantity:
                    <button onclick="decreaseQuantity(${product.id})">
                        -
                    </button>

                    ${product.quantity}

                    <button onclick="increaseQuantity(${product.id})">
                        +
                    </button>
                </p>

                <p>
                    Subtotal: ₹${productTotal}
                </p>

                <button
                    onclick="removeFromCart(${product.id})"
                >
                    Remove
                </button>

            </div>

            <hr>

        `;

    });

    cartTotal.innerHTML =
        "Total: ₹" + total;
}
function increaseQuantity(id) {

    let cartProduct = cart.find(function(product) {
        return product.id === id;
    });

    let originalProduct = allProducts.find(function(product) {
        return product.id === id;
    });

    if (cartProduct && originalProduct) {

        if (cartProduct.quantity < originalProduct.stock) {

            cartProduct.quantity++;

            displayCart();

        } else {

            alert("Stock limit reached!");

        }
    }
}

function decreaseQuantity(id) {

    let product = cart.find(function(product) {
        return product.id === id;
    });

    if (product) {

        if (product.quantity > 1) {
            product.quantity--;
        } else {
            cart = cart.filter(function(product) {
                return product.id !== id;
            });
        }

        displayCart();
    }
}

// ==============================
// REMOVE FROM CART
// ==============================

function removeFromCart(id) {

    cart = cart.filter(function(product) {

        return product.id !== id;

    });


    displayCart();
}


// ==============================
// PLACE ORDER
// ==============================

function placeOrder() {


    if (cart.length === 0) {

        alert("Cart is empty!");

        return;
    }


    let requests =
        cart.map(function(product) {


            let productTotal =
                product.price *
                product.quantity;


            let data =
                "product_name=" +
                encodeURIComponent(
                    product.name
                ) +

                "&price=" +
                product.price +

                "&quantity=" +
                product.quantity +

                "&total=" +
                productTotal;


            return fetch("/orders", {

                method: "POST",

                headers: {

                    "Content-Type":
                        "application/x-www-form-urlencoded"

                },

                body: data

            })

            .then(response =>
                response.text()
            );

        });


    Promise.all(requests)

        .then(function(results) {


            alert(
                "Order Placed Successfully!"
            );


            cart = [];


            displayCart();


            loadOrders();

        })

        .catch(function(error) {


            console.log(
                "Order Error:",
                error
            );


            alert(
                "Order Placement Failed!"
            );

        });
}


// ==============================
// LOAD ORDERS
// ==============================

function loadOrders() {

    fetch("/orders")

        .then(response =>
            response.json()
        )

        .then(orders => {


            let orderList =
                document.getElementById(
                    "orderList"
                );


            orderList.innerHTML = "";


            if (orders.length === 0) {

                orderList.innerHTML =
                    "<p>No orders found.</p>";

                return;
            }


            orders.forEach(function(order) {


                orderList.innerHTML += `

                    <div class="order">

                        <h3>
                            Order ID:
                            ${order.id}
                        </h3>


                        <p>
                            Product:
                            ${order.product_name}
                        </p>


                        <p>
                            Price:
                            ₹${order.price}
                        </p>


                        <p>
                            Quantity:
                            ${order.quantity}
                        </p>


                        <p>
                            Total:
                            ₹${order.total}
                        </p>


                        <p>
                            Status:
                            ${order.status}
                        </p>

                        <p>
                            Order Date:
                            ${order.order_date}
                       </p>

                    </div>

                    <hr>

                `;

            });

        })

        .catch(error => {

            console.log(
                "Error loading orders:",
                error
            );

        });
}


// ==============================
// START
// ==============================

loadProducts();

loadOrders();