// PRODUCT DATA

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


// DISPLAY PRODUCTS

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


// ADD PRODUCT

function addProduct() {

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


    // Clear fields

    document.getElementById("productName").value = "";

    document.getElementById("productPrice").value = "";

    document.getElementById("productStock").value = "";

    document.getElementById("productCategory").value = "";


    showMessage(
        "Product Added Successfully!",
        "success"
    );

}


// EDIT PRODUCT

function editProduct(index) {

    let product = products[index];


    let newName =
        prompt(
            "Enter Product Name:",
            product.name
        );


    if (newName === null) {
        return;
    }


    let newPrice =
        prompt(
            "Enter Price:",
            product.price
        );


    if (newPrice === null) {
        return;
    }


    let newStock =
        prompt(
            "Enter Stock:",
            product.stock
        );


    if (newStock === null) {
        return;
    }


    products[index].name =
        newName;

    products[index].price =
        Number(newPrice);

    products[index].stock =
        Number(newStock);


    displayProducts();


    showMessage(
        "Product Updated Successfully!",
        "success"
    );

}


// DELETE PRODUCT

function deleteProduct(index) {

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


    products.splice(index, 1);


    displayProducts();


    showMessage(
        "Product Deleted Successfully!",
        "success"
    );

}


// MESSAGE

function showMessage(text, type) {

    let message =
        document.getElementById("message");

    message.innerHTML = text;

    message.className = type;

}


// BACK TO LOGIN

function goBack() {

    window.location.href =
        "index.html";

}


// LOAD PRODUCTS

displayProducts();