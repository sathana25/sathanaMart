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
                    </div>
                    <hr>
                `;
            });

        })
        .catch(error => {
            console.log("Error loading products:", error);
        });
}

loadProducts();