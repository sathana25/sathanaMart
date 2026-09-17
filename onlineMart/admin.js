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
                        <h3>${product.name}</h3>
                        <p>Price: ₹${product.price}</p>
                        <p>Stock: ${product.stock}</p>
                        <p>Category: ${product.category}</p>
                    </div>
                `;
            });

        })
        .catch(error => {

            console.log("Product Error:", error);

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
                            Product: ${order.product_name}
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
                            Status: ${order.status}
                        </p>

                    </div>
                `;
            });

        })
        .catch(error => {

            console.log("Order Error:", error);

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

                    </div>
                `;
            });

        })
        .catch(error => {

            console.log("User Error:", error);

        });
}


loadProducts();
loadOrders();
loadUsers();