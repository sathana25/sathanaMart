let cart =
    JSON.parse(
        localStorage.getItem("sathanaCart")
    ) || [];

let wishlist =
    JSON.parse(
        localStorage.getItem("sathanaWishlist")
    ) || [];

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

        let imageHTML = "";

        if (product.image) {

            imageHTML = `
                <img
                    src="${product.image}"
                    alt="${product.name}"
                    style="
                        width: 200px;
                        height: 200px;
                        object-fit: contain;
                        display: block;
                        margin-bottom: 10px;
                    "
                >
            `;

        }


        // Check wishlist
        let isWishlisted =
            wishlist.some(function(item) {

                return item.id === product.id;

            });


        let heart =
            isWishlisted ? "❤️" : "♡";


        productList.innerHTML += `

            <div class="product">

                ${imageHTML}

                <button
                    class="wishlist-heart"
                    onclick="
                        toggleWishlist(
                            ${product.id},
                            '${product.name}',
                            ${product.price},
                            '${product.image || ""}'
                        )
                    "
                >
                    ${heart}
                </button>

                <h3>
                    ${product.name}
                </h3>

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
                    onclick="
                        addToCart(
                            ${product.id},
                            '${product.name}',
                            ${product.price},
                            '${product.image || ""}'
                        )
                    "
                >
                    Add to Cart
                </button>

            </div>

            <hr>

        `;

    });
}


// ==============================
// WISHLIST
// ==============================

function toggleWishlist(
    id,
    name,
    price,
    image
) {

    let existingProduct =
        wishlist.find(function(product) {

            return product.id === id;

        });


    if (existingProduct) {

        // Remove from wishlist

        wishlist =
            wishlist.filter(function(product) {

                return product.id !== id;

            });

    } else {

        // Add to wishlist

        wishlist.push({

            id: id,

            name: name,

            price: price,

            image: image

        });

    }


    saveWishlist();

    displayProducts(
        getFilteredProducts()
    );
}


// ==============================
// SAVE WISHLIST
// ==============================

function saveWishlist() {

    localStorage.setItem(
        "sathanaWishlist",
        JSON.stringify(wishlist)
    );
}


// ==============================
// GET FILTERED PRODUCTS
// ==============================

function getFilteredProducts() {

    let searchBox =
        document.getElementById("searchBox");

    let categoryFilter =
        document.getElementById("categoryFilter");


    let searchText = "";

    let selectedCategory = "All";


    if (searchBox) {

        searchText =
            searchBox.value
                .toLowerCase();

    }


    if (categoryFilter) {

        selectedCategory =
            categoryFilter.value;

    }


    return allProducts.filter(
        function(product) {

            let nameMatch =
                product.name
                    .toLowerCase()
                    .includes(searchText);


            let categoryMatch =
                selectedCategory === "All" ||
                product.category === selectedCategory;


            return nameMatch &&
                   categoryMatch;

        }
    );
}


// ==============================
// SEARCH + CATEGORY FILTER
// ==============================

function filterProducts() {

    let filteredProducts =
        getFilteredProducts();


    displayProducts(
        filteredProducts
    );
}


// ==============================
// GO TO WISHLIST
// ==============================

function goToWishlist() {

    window.location.href =
        "/wishlist.html";
}


// ==============================
// ADD TO CART
// ==============================

function addToCart(
    id,
    name,
    price,
    image
) {

    let existingProduct =
        cart.find(function(product) {

            return product.id === id;

        });


    if (existingProduct) {

        let originalProduct =
            allProducts.find(function(product) {

                return product.id === id;

            });


        if (
            originalProduct &&
            existingProduct.quantity <
            originalProduct.stock
        ) {

            existingProduct.quantity++;

        } else {

            alert(
                "Stock limit reached!"
            );

            return;
        }

    } else {

        cart.push({

            id: id,

            name: name,

            price: price,

            image: image,

            quantity: 1

        });

    }


    saveCart();

    alert(
        "Product added to cart!"
    );

    displayCart();
}


// ==============================
// SAVE CART
// ==============================

function saveCart() {

    localStorage.setItem(
        "sathanaCart",
        JSON.stringify(cart)
    );
}


// ==============================
// DISPLAY CART
// ==============================

function displayCart() {

    let cartList =
        document.getElementById(
            "cartList"
        );

    let cartTotal =
        document.getElementById(
            "cartTotal"
        );


    if (!cartList || !cartTotal) {

        return;

    }


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
                    Price:
                    ₹${product.price}
                </p>

                <p>
                    Quantity:

                    <button
                        onclick="
                            decreaseQuantity(
                                ${product.id}
                            )
                        "
                    >
                        -
                    </button>

                    ${product.quantity}

                    <button
                        onclick="
                            increaseQuantity(
                                ${product.id}
                            )
                        "
                    >
                        +
                    </button>

                </p>

                <p>
                    Subtotal:
                    ₹${productTotal}
                </p>

                <button
                    onclick="
                        removeFromCart(
                            ${product.id}
                        )
                    "
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


// ==============================
// INCREASE QUANTITY
// ==============================

function increaseQuantity(id) {

    let cartProduct =
        cart.find(function(product) {

            return product.id === id;

        });


    let originalProduct =
        allProducts.find(function(product) {

            return product.id === id;

        });


    if (
        cartProduct &&
        originalProduct
    ) {

        if (
            cartProduct.quantity <
            originalProduct.stock
        ) {

            cartProduct.quantity++;

            saveCart();

            displayCart();

        } else {

            alert(
                "Stock limit reached!"
            );

        }

    }
}


// ==============================
// DECREASE QUANTITY
// ==============================

function decreaseQuantity(id) {

    let product =
        cart.find(function(product) {

            return product.id === id;

        });


    if (product) {

        if (product.quantity > 1) {

            product.quantity--;

        } else {

            cart =
                cart.filter(
                    function(product) {

                        return product.id !== id;

                    }
                );

        }


        saveCart();

        displayCart();

    }
}


// ==============================
// REMOVE FROM CART
// ==============================

function removeFromCart(id) {

    cart =
        cart.filter(
            function(product) {

                return product.id !== id;

            }
        );


    saveCart();

    displayCart();
}


// ==============================
// GO TO CART PAGE
// ==============================

function goToCart() {

    window.location.href =
        "/cart.html";
}


// ==============================
// PLACE ORDER
// ==============================

function placeOrder() {

    if (cart.length === 0) {

        alert(
            "Cart is empty!"
        );

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


            return fetch(
                "/orders",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/x-www-form-urlencoded"

                    },

                    body: data

                }
            )

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

            saveCart();

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

displayCart();