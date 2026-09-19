function loadProducts() {

    fetch("/products")
        .then(response => response.json())
        .then(products => {

            document.getElementById("productCount").innerText =
                products.length;

            let productList =
                document.getElementById("productList");

            productList.innerHTML = "";

            products.forEach(function(product) {

                productList.innerHTML += `
                    <div class="product">

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

                        <button onclick="editProduct(${product.id})">
                           Edit
                        </button>

                        <button onclick="deleteProduct(${product.id})">
                          Delete
                        </button>

                    </div>
                `;
            });

        })
        .catch(error => {

            console.log(
                "Product Error:",
                error
            );
        });
}
function deleteProduct(productId) {

    if (!confirm("Are you sure you want to delete this product?")) {
        return;
    }

    fetch("/products", {
        method: "DELETE",
        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },
        body: "id=" + productId
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadProducts();

    })
    .catch(error => {

        console.log(
            "Delete Product Error:",
            error
        );

        alert("Product delete failed!");

    });
}
function editProduct(productId) {

    let name = prompt("Enter new product name:");
    if (name === null) return;

    let price = prompt("Enter new price:");
    if (price === null) return;

    let stock = prompt("Enter new stock:");
    if (stock === null) return;

    let category = prompt("Enter new category:");
    if (category === null) return;

    let data =
        "id=" + productId +
        "&name=" + encodeURIComponent(name) +
        "&price=" + price +
        "&stock=" + stock +
        "&category=" + encodeURIComponent(category);

    fetch("/products", {
        method: "PUT",
        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },
        body: data
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadProducts();

    })
    .catch(error => {

        console.log(
            "Edit Product Error:",
            error
        );

        alert("Product update failed!");

    });
}


function loadOrders() {

    fetch("/orders")
        .then(response => response.json())
        .then(orders => {

            document.getElementById("orderCount").innerText =
                orders.length;

            let orderList =
                document.getElementById("orderList");

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
                            Order ID: ${order.id}
                        </h3>

                        <p>
                            Product:
                            ${order.product_name}
                        </p>

                        <p>
                            Price: ₹${order.price}
                        </p>

                        <p>
                            Quantity: ${order.quantity}
                        </p>

                        <p>
                            Total: ₹${order.total}
                        </p>

                        <p>
                            Status:
                            
                            <select
                                onchange="updateOrderStatus(
                                    ${order.id},
                                    this.value
                                )"
                            >

                                <option
                                    value="Placed"
                                    ${order.status === "Placed" ? "selected" : ""}
                                >
                                    Placed
                                </option>

                                <option
                                    value="Processing"
                                    ${order.status === "Processing" ? "selected" : ""}
                                >
                                    Processing
                                </option>

                                <option
                                    value="Shipped"
                                    ${order.status === "Shipped" ? "selected" : ""}
                                >
                                    Shipped
                                </option>

                                <option
                                    value="Delivered"
                                    ${order.status === "Delivered" ? "selected" : ""}
                                >
                                    Delivered
                                </option>

                                <option
                                    value="Cancelled"
                                    ${order.status === "Cancelled" ? "selected" : ""}
                                >
                                    Cancelled
                                </option>

                            </select>

                        </p>

                    </div>
                `;
            });

        })
        .catch(error => {

            console.log(
                "Order Error:",
                error
            );
        });
}


function updateOrderStatus(orderId, status) {

    fetch("/orders", {

        method: "PUT",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },

        body:
            "id=" +
            orderId +
            "&status=" +
            encodeURIComponent(status)

    })

    .then(response => response.text())

    .then(message => {

        alert(message);

        loadOrders();

    })

    .catch(error => {

        console.log(
            "Order Status Error:",
            error
        );
    });
}


function loadUsers() {

    fetch("/users")
        .then(response => response.json())
        .then(users => {

            document.getElementById("userCount").innerText =
                users.length;

            let userList =
                document.getElementById("userList");

            userList.innerHTML = "";

            if (users.length === 0) {

                userList.innerHTML =
                    "<p>No users found.</p>";

                return;
            }

            users.forEach(function(user) {

                userList.innerHTML += `
                    <div class="user">

                        <h3>
                            User ID: ${user.id}
                        </h3>

                        <p>
                            Name: ${user.name}
                        </p>

                        <p>
                            Email: ${user.email}
                        </p>

                        <p>
                            Role: ${user.role}
                        </p>

                        <button
                            onclick="deleteUser(${user.id})"
                        >
                            Delete User
                        </button>

                    </div>
                `;
            });

        })
        .catch(error => {

            console.log(
                "User Error:",
                error
            );
        });
}


function deleteUser(userId) {

    if (
        !confirm(
            "Are you sure you want to delete this user?"
        )
    ) {
        return;
    }

    fetch("/users", {

        method: "DELETE",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },

        body:
            "id=" + userId

    })

    .then(response => response.text())

    .then(message => {

        alert(message);

        loadUsers();

    })

    .catch(error => {

        console.log(
            "Delete User Error:",
            error
        );
    });
}


loadProducts();

loadOrders();

loadUsers();