let products = [

    {
        name: "Laptop",
        price: 45000,
        stock: 10,
        category: "Electronics",
        icon: "💻"
    },

    {
        name: "Earphones",
        price: 2000,
        stock: 20,
        category: "Accessories",
        icon: "🎧"
    },

    {
        name: "Mobile",
        price: 18000,
        stock: 15,
        category: "Mobile",
        icon: "📱"
    },

    {
        name: "Smart Watch",
        price: 3500,
        stock: 12,
        category: "Accessories",
        icon: "⌚"
    },

    {
        name: "Camera",
        price: 35000,
        stock: 8,
        category: "Electronics",
        icon: "📷"
    }

];


function displayProducts() {

    let productList =
        document.getElementById("productList");

    productList.innerHTML = "";

    products.forEach(function(product, index) {

        productList.innerHTML += `

            <div class="product">

                <div class="product-icon">
                    ${product.icon}
                </div>

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
                    class="edit-button"
                    onclick="editProduct(${index})">

                    Edit

                </button>

                <button
                    class="delete-button"
                    onclick="deleteProduct(${index})">

                    Delete

                </button>

            </div>

        `;

    });

}


async function addProduct() {

    let name =
        document.getElementById("productName").value.trim();

    let price =
        document.getElementById("productPrice").value;

    let stock =
        document.getElementById("productStock").value;

    let category =
        document.getElementById("productCategory").value;


    if (
        name === "" ||
        price === "" ||
        stock === "" ||
        category === ""
    ) {

        showMessage(
            "Please fill all product details.",
            "error"
        );

        return;
    }


    try {

        let data = new URLSearchParams();

        data.append("name", name);
        data.append("price", price);
        data.append("stock", stock);
        data.append("category", category);


        let response = await fetch(
            "http://localhost:8080/products",
            {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );


        let result = await response.text();


        if (response.ok) {

            let icon = "📦";


            if (category === "Mobile") {
                icon = "📱";
            }

            else if (category === "Accessories") {
                icon = "🎧";
            }

            else if (category === "Electronics") {
                icon = "💻";
            }


            products.push({

                name: name,

                price: Number(price),

                stock: Number(stock),

                category: category,

                icon: icon

            });


            displayProducts();


            document.getElementById("productName").value = "";

            document.getElementById("productPrice").value = "";

            document.getElementById("productStock").value = "";

            document.getElementById("productCategory").value = "";


            showMessage(
                result,
                "success"
            );

        } else {

            showMessage(
                "Product addition failed.",
                "error"
            );

        }

    } catch (error) {

        console.log(error);

        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );

    }

}


async function editProduct(index) {

    let product = products[index];


    let newName =
        prompt(
            "Enter Product Name:",
            product.name
        );


    if (newName === null || newName.trim() === "") {
        return;
    }


    let newPrice =
        prompt(
            "Enter Price:",
            product.price
        );


    if (newPrice === null || newPrice.trim() === "") {
        return;
    }


    let newStock =
        prompt(
            "Enter Stock:",
            product.stock
        );


    if (newStock === null || newStock.trim() === "") {
        return;
    }


    let data = new URLSearchParams();

    data.append("id", product.id);

    data.append("name", newName);

    data.append("price", newPrice);

    data.append("stock", newStock);

    data.append("category", product.category);


    try {

        let response = await fetch(
            "http://localhost:8080/products",
            {
                method: "PUT",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );


        let result =
            await response.text();


        if (response.ok) {

            showMessage(
                result,
                "success"
            );

            loadProducts();

        } else {

            showMessage(
                "Product update failed.",
                "error"
            );

        }

    } catch (error) {

        console.log(error);

        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );

    }

}


async function deleteProduct(index) {

    let product =
        products[index];


    let confirmDelete =
        confirm(
            "Delete " +
            product.name +
            "?"
        );


    if (!confirmDelete) {
        return;
    }


    let data = new URLSearchParams();

    data.append("id", product.id);


    try {

        let response = await fetch(
            "http://localhost:8080/products",
            {
                method: "DELETE",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );


        let result =
            await response.text();


        if (response.ok) {

            showMessage(
                result,
                "success"
            );

            loadProducts();

        } else {

            showMessage(
                "Product deletion failed.",
                "error"
            );

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

    let message =
        document.getElementById("message");

    message.innerHTML = text;

    message.className = type;

}


function goBack() {

    window.location.href =
        "index.html";

}


async function loadProducts() {

    try {

        let response = await fetch(
            "http://localhost:8080/products"
        );


        let data =
            await response.json();


        products =
            data.map(function(product) {

                let icon = "📦";


                if (product.category === "Mobile") {
                    icon = "📱";
                }

                else if (product.category === "Accessories") {
                    icon = "🎧";
                }

                else if (product.category === "Electronics") {
                    icon = "💻";
                }


                return {

                    id: product.id,

                    name: product.name,

                    price: product.price,

                    stock: product.stock,

                    category: product.category,

                    icon: icon

                };

            });


        displayProducts();

    } catch (error) {

        console.log(error);

        displayProducts();

    }

}


// ==============================
// LOAD ORDERS
// ==============================

async function loadOrders() {

    try {

        let response = await fetch(
            "http://localhost:8080/orders"
        );


        if (!response.ok) {

            throw new Error(
                "Unable to load orders"
            );

        }


        let orders =
            await response.json();


        let orderList =
            document.getElementById("orderList");


        orderList.innerHTML = "";


        if (orders.length === 0) {

            orderList.innerHTML =
                "<p>No orders received.</p>";

            return;
        }


        orders.forEach(function(order) {

            orderList.innerHTML += `

                <div class="product">

                    <h3>
                        Order ID: ${order.id}
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
                      <select onchange="updateOrderStatus(${order.id}, this.value)">
                        <option value="Placed" ${order.status === "Placed" ? "selected" : ""}>
                           Placed
                        </option>

                        <option value="Processing" ${order.status === "Processing" ? "selected" : ""}>
                          Processing
                        </option>

                        <option value="Shipped" ${order.status === "Shipped" ? "selected" : ""}>
                           Shipped
                        </option>

                        <option value="Delivered" ${order.status === "Delivered" ? "selected" : ""}>
                           Delivered
                        </option>

                        <option value="Cancelled" ${order.status === "Cancelled" ? "selected" : ""}>
                          Cancelled
                       </option>
                       </select>
                    </p>
                

                </div>

            `;

        });

    } catch (error) {

        console.log(
            "Order Error:",
            error
        );


        let orderList =
            document.getElementById("orderList");


        orderList.innerHTML =
            "<p>Unable to load orders.</p>";

    }

}
async function updateOrderStatus(orderId, status) {

    let data = new URLSearchParams();

    data.append("id", orderId);
    data.append("status", status);

    try {

        let response = await fetch(
            "http://localhost:8080/orders",
            {
                method: "PUT",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );

        let result = await response.text();

        if (response.ok) {

            showMessage(
                result,
                "success"
            );

            loadOrders();

        } else {

            showMessage(
                "Order status update failed.",
                "error"
            );

        }

    } catch (error) {

        console.log(
            "Order Status Error:",
            error
        );

        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );
    }
}



loadProducts();

loadOrders();