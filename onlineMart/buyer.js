let cart = [];

function loadProducts() {

    fetch("/products")
        .then(response => response.json())
        .then(products => {

            let productList = document.getElementById("productList");

            productList.innerHTML = "";

            products.forEach(function(product) {

                productList.innerHTML += `
                    <div>
                        <h3>${product.name}</h3>
                        <p>Price: ₹${product.price}</p>
                        <p>Stock: ${product.stock}</p>
                        <p>Category: ${product.category}</p>

                        <button onclick="addToCart(${product.id}, '${product.name}', ${product.price})">
                            Add to Cart
                        </button>
                    </div>
                    <hr>
                `;
            });

        })
        .catch(error => {
            console.log("Error loading products:", error);
        });
}


function addToCart(id, name, price) {

    let existingProduct = cart.find(function(product) {
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


function displayCart() {

    let cartList = document.getElementById("cartList");
    let cartTotal = document.getElementById("cartTotal");

    cartList.innerHTML = "";

    let total = 0;

    cart.forEach(function(product) {

        let productTotal = product.price * product.quantity;

        total += productTotal;

        cartList.innerHTML += `
            <div>
                <h3>${product.name}</h3>
                <p>Price: ₹${product.price}</p>
                <p>Quantity: ${product.quantity}</p>
                <p>Subtotal: ₹${productTotal}</p>

                <button onclick="removeFromCart(${product.id})">
                    Remove
                </button>
            </div>
            <hr>
        `;
    });

    cartTotal.innerHTML = "Total: ₹" + total;
}


function removeFromCart(id) {

    cart = cart.filter(function(product) {
        return product.id !== id;
    });

    displayCart();
}

function placeOrder() {

    if (cart.length === 0) {
        alert("Cart is empty!");
        return;
    }

    let requests = cart.map(function(product) {

        let productTotal = product.price * product.quantity;

        let data =
            "product_name=" + encodeURIComponent(product.name) +
            "&price=" + product.price +
            "&quantity=" + product.quantity +
            "&total=" + productTotal;

        return fetch("/orders", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: data
        })
        .then(response => response.text());
    });

    Promise.all(requests)
        .then(function(results) {

            alert("Order Placed Successfully!");

            cart = [];

            displayCart();
        })
        .catch(function(error) {

            console.log("Order Error:", error);

            alert("Order Placement Failed!");
        });
}

loadProducts();