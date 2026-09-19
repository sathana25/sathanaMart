// ==============================
// LOAD WISHLIST
// ==============================

let wishlist =
    JSON.parse(
        localStorage.getItem("sathanaWishlist")
    ) || [];


// ==============================
// DISPLAY WISHLIST
// ==============================

function displayWishlist() {

    let wishlistList =
        document.getElementById(
            "wishlistList"
        );


    wishlistList.innerHTML = "";


    // Check empty wishlist

    if (wishlist.length === 0) {

        wishlistList.innerHTML =
            "<p>Your wishlist is empty.</p>";

        return;
    }


    // Display products

    wishlist.forEach(
        function(product) {

            let imageHTML = "";


            // Product image

            if (product.image) {

                imageHTML = `

                    <img
                        src="${product.image}"
                        alt="${product.name}"
                    >

                `;

            }


            wishlistList.innerHTML += `

                <div class="wishlist-item">

                    ${imageHTML}


                    <h3>
                        ❤️ ${product.name}
                    </h3>


                    <p>
                        Price:
                        ₹${product.price}
                    </p>


                    <button
                        class="remove-wishlist"
                        onclick="
                            removeFromWishlist(
                                ${product.id}
                            )
                        "
                    >
                        Remove from Wishlist
                    </button>

                </div>

            `;

        }
    );
}


// ==============================
// REMOVE FROM WISHLIST
// ==============================

function removeFromWishlist(id) {

    wishlist =
        wishlist.filter(
            function(product) {

                return product.id !== id;

            }
        );


    saveWishlist();


    displayWishlist();
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
// GO BACK TO BUYER PAGE
// ==============================

function goBackToBuyer() {

    window.location.href =
        "/buyer.html";
}


// ==============================
// START
// ==============================

displayWishlist();