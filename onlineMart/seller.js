let products = [];


// ==============================
// DISPLAY PRODUCTS
// ==============================

function displayProducts() {

    const productList = document.getElementById("productList");

    productList.innerHTML = "";

    if (products.length === 0) {
        productList.innerHTML = "<p>No products found.</p>";
        return;
    }

    products.forEach(function(product, index) {

        let imageHTML = "";

        if (product.image && product.image.trim() !== "") {

            imageHTML = `
                <img
                    src="${product.image}"
                    alt="${product.name}"
                    style="
                        width:150px;
                        height:150px;
                        object-fit:cover;
                        display:block;
                        margin-bottom:10px;
                        border-radius:8px;
                    "
                >
            `;

        } else {

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

            imageHTML = `
                <div class="product-icon">
                    ${icon}
                </div>
            `;
        }

        productList.innerHTML += `

            <div class="product">

                ${imageHTML}

                <h3>${product.name}</h3>

                <p>Price: ₹${product.price}</p>

                <p>Stock: ${product.stock}</p>

                <p>Category: ${product.category}</p>

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


// ==============================
// ADD PRODUCT
// ==============================

async function addProduct() {

    const name =
        document.getElementById("productName").value.trim();

    const price =
        document.getElementById("productPrice").value;

    const stock =
        document.getElementById("productStock").value;

    const category =
        document.getElementById("productCategory").value;

    const imageInput =
        document.getElementById("productImage");

    const imageFile =
        imageInput ? imageInput.files[0] : null;


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

        let imageData = "";


        // Image is optional

        if (imageFile) {

            imageData =
                await convertImageToBase64(imageFile);
        }


        const data =
            new URLSearchParams();


        data.append("name", name);
        data.append("price", price);
        data.append("stock", stock);
        data.append("category", category);
        data.append("image", imageData);


        const response =
            await fetch(
                "/products",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body: data.toString()
                }
            );


        const result =
            await response.text();


        // DEBUG

        console.log(
            "Add Product Response:",
            response.status,
            result
        );


        if (response.ok) {

            showMessage(
                "Product added successfully!",
                "success"
            );


            document.getElementById(
                "productName"
            ).value = "";


            document.getElementById(
                "productPrice"
            ).value = "";


            document.getElementById(
                "productStock"
            ).value = "";


            document.getElementById(
                "productCategory"
            ).value = "";


            if (imageInput) {

                imageInput.value = "";
            }


            await loadProducts();

        }
        else {

            console.log(
                "Backend Error:",
                result
            );


            showMessage(
                "Product addition failed: " + result,
                "error"
            );
        }


    }
    catch (error) {

        console.log(
            "Product Add Error:",
            error
        );


        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );
    }
}


// ==============================
// CONVERT IMAGE TO BASE64
// ==============================

function convertImageToBase64(file) {

    return new Promise(
        function(resolve, reject) {

            const reader =
                new FileReader();


            reader.onload =
                function() {

                    resolve(
                        reader.result
                    );
                };


            reader.onerror =
                function(error) {

                    reject(error);
                };


            reader.readAsDataURL(file);
        }
    );
}


// ==============================
// EDIT PRODUCT
// ==============================

async function editProduct(index) {

    const product =
        products[index];


    const newName =
        prompt(
            "Enter Product Name:",
            product.name
        );


    if (
        newName === null ||
        newName.trim() === ""
    ) {

        return;
    }


    const newPrice =
        prompt(
            "Enter Price:",
            product.price
        );


    if (
        newPrice === null ||
        newPrice.trim() === ""
    ) {

        return;
    }


    const newStock =
        prompt(
            "Enter Stock:",
            product.stock
        );


    if (
        newStock === null ||
        newStock.trim() === ""
    ) {

        return;
    }


    const data =
        new URLSearchParams();


    data.append(
        "id",
        product.id
    );


    data.append(
        "name",
        newName.trim()
    );


    data.append(
        "price",
        newPrice
    );


    data.append(
        "stock",
        newStock
    );


    data.append(
        "category",
        product.category
    );


    data.append(
        "image",
        product.image || ""
    );


    try {

        const response =
            await fetch(
                "/products",
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body: data.toString()
                }
            );


        const result =
            await response.text();


        console.log(
            "Edit Product Response:",
            response.status,
            result
        );


        if (response.ok) {

            showMessage(
                "Product updated successfully!",
                "success"
            );


            await loadProducts();

        }
        else {

            showMessage(
                "Product update failed: " + result,
                "error"
            );
        }


    }
    catch (error) {

        console.log(
            "Edit Product Error:",
            error
        );


        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );
    }
}


// ==============================
// DELETE PRODUCT
// ==============================

async function deleteProduct(index) {

    const product =
        products[index];


    const confirmDelete =
        confirm(
            "Delete " + product.name + "?"
        );


    if (!confirmDelete) {

        return;
    }


    const data =
        new URLSearchParams();


    data.append(
        "id",
        product.id
    );


    try {

        const response =
            await fetch(
                "/products",
                {
                    method: "DELETE",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body: data.toString()
                }
            );


        const result =
            await response.text();


        console.log(
            "Delete Product Response:",
            response.status,
            result
        );


        if (response.ok) {

            showMessage(
                "Product deleted successfully!",
                "success"
            );


            await loadProducts();

        }
        else {

            showMessage(
                "Product deletion failed: " + result,
                "error"
            );
        }


    }
    catch (error) {

        console.log(
            "Delete Product Error:",
            error
        );


        showMessage(
            "Unable to connect to Java backend.",
            "error"
        );
    }
}


// ==============================
// SHOW MESSAGE
// ==============================

function showMessage(text, type) {

    const message =
        document.getElementById("message");


    if (!message) {

        return;
    }


    message.innerHTML = text;

    message.className = type;
}


// ==============================
// BACK TO LOGIN
// ==============================

function goBack() {

    window.location.href = "/";
}


// ==============================
// LOAD PRODUCTS
// ==============================

async function loadProducts() {

    try {

        const response =
            await fetch(
                "/products"
            );


        console.log(
            "Products Response Status:",
            response.status
        );


        if (!response.ok) {

            throw new Error(
                "Product request failed"
            );
        }


        const data =
            await response.json();


        console.log(
            "Products From Backend:",
            data
        );


        products =
            data.map(
                function(product) {

                    return {

                        id: product.id,

                        name: product.name,

                        price: product.price,

                        stock: product.stock,

                        category: product.category,

                        image:
                            product.image || ""
                    };
                }
            );


        displayProducts();


    }
    catch (error) {

        console.log(
            "Product Load Error:",
            error
        );


        products = [];

        displayProducts();
    }
}


// ==============================
// LOAD ORDERS
// ==============================

async function loadOrders() {

    try {

        const response =
            await fetch(
                "/orders"
            );


        console.log(
            "Orders Response Status:",
            response.status
        );


        if (!response.ok) {

            throw new Error(
                "Orders request failed"
            );
        }


        const orders =
            await response.json();


        console.log(
            "Orders From Backend:",
            orders
        );


        const orderList =
            document.getElementById(
                "orderList"
            );


        orderList.innerHTML = "";


        if (orders.length === 0) {

            orderList.innerHTML =
                "<p>No orders received.</p>";

            return;
        }


        orders.forEach(
            function(order) {

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

                            <select
                                onchange="
                                    updateOrderStatus(
                                        ${order.id},
                                        this.value
                                    )
                                "
                            >

                                <option
                                    value="Placed"
                                    ${
                                        order.status === "Placed"
                                        ? "selected"
                                        : ""
                                    }
                                >
                                    Placed
                                </option>


                                <option
                                    value="Processing"
                                    ${
                                        order.status === "Processing"
                                        ? "selected"
                                        : ""
                                    }
                                >
                                    Processing
                                </option>


                                <option
                                    value="Shipped"
                                    ${
                                        order.status === "Shipped"
                                        ? "selected"
                                        : ""
                                    }
                                >
                                    Shipped
                                </option>


                                <option
                                    value="Delivered"
                                    ${
                                        order.status === "Delivered"
                                        ? "selected"
                                        : ""
                                    }
                                >
                                    Delivered
                                </option>


                                <option
                                    value="Cancelled"
                                    ${
                                        order.status === "Cancelled"
                                        ? "selected"
                                        : ""
                                    }
                                >
                                    Cancelled
                                </option>

                            </select>

                        </p>

                    </div>

                `;
            }
        );


    }
    catch (error) {

        console.log(
            "Order Error:",
            error
        );


        document.getElementById(
            "orderList"
        ).innerHTML =
            "<p>Unable to load orders.</p>";
    }
}


// ==============================
// UPDATE ORDER STATUS
// ==============================

async function updateOrderStatus(
    orderId,
    status
) {

    const data =
        new URLSearchParams();


    data.append(
        "id",
        orderId
    );


    data.append(
        "status",
        status
    );


    try {

        const response =
            await fetch(
                "/orders",
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body: data.toString()
                }
            );


        const result =
            await response.text();


        console.log(
            "Order Status Response:",
            response.status,
            result
        );


        if (response.ok) {

            showMessage(
                "Order status updated successfully!",
                "success"
            );


            await loadOrders();

        }
        else {

            showMessage(
                "Order status update failed: " + result,
                "error"
            );
        }


    }
    catch (error) {

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


// ==============================
// START
// ==============================

loadProducts();

loadOrders();