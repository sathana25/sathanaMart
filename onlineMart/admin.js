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

                        <h3>Order ID: ${order.id}</h3>

                        <p>Product: ${order.product_name}</p>

                        <p>Price: ₹${order.price}</p>

                        <p>Quantity: ${order.quantity}</p>

                        <p>Total: ₹${order.total}</p>

                        <p>Current Status: ${order.status}</p>

                        <select id="status-${order.id}">

                            <option value="Placed">
                                Placed
                            </option>

                            <option value="Processing">
                                Processing
                            </option>

                            <option value="Shipped">
                                Shipped
                            </option>

                            <option value="Delivered">
                                Delivered
                            </option>

                            <option value="Cancelled">
                                Cancelled
                            </option>

                        </select>

                        <button onclick="updateOrderStatus(${order.id})">
                            Update Status
                        </button>

                    </div>

                    <hr>
                `;

                document.getElementById(
                    "status-" + order.id
                ).value = order.status;

            });

        })
        .catch(error => {

            console.log("Order Error:", error);

        });
}


function updateOrderStatus(orderId) {

    let status =
        document.getElementById(
            "status-" + orderId
        ).value;

    let data =
        "id=" + orderId +
        "&status=" + encodeURIComponent(status);

    fetch("/orders", {
        method: "PUT",
        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },
        body: data
    })
    .then(response => response.text())
    .then(result => {

        alert(result);

        loadOrders();

    })
    .catch(error => {

        console.log(
            "Status Update Error:",
            error
        );

        alert("Status Update Failed!");

    });
}


loadProducts();
loadOrders();