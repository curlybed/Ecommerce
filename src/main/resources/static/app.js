// ============================================
//  Ecommerce — Frontend App
//  (No JWT token — session via localStorage)
// ============================================

const API = '';  // Same origin

// ---- State ----
let currentUser = null;   // { userId, name, email }
let cart = [];             // local cart array
let pendingEmail = '';     // email waiting for OTP verification

// ============================================
//  VIEW MANAGEMENT
// ============================================

function showView(viewName) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    const view = document.getElementById('view-' + viewName);
    if (view) view.classList.add('active');

    if (viewName === 'products') loadProducts();
    if (viewName === 'cart') renderCart();

    updateNav();
}

function updateNav() {
    const nav = document.getElementById('nav-links');

    if (currentUser) {
        nav.innerHTML = `
            <li><button class="nav-link active" onclick="showView('products')">Products</button></li>
            ${currentUser.role === 'ADMIN' ? `<li><button class="nav-link" onclick="showView('admin')">Admin</button></li>` : ''}
            <li class="nav-cart">
                <button class="nav-link" onclick="showView('cart')">🛒 Cart
                    ${cart.length > 0 ? `<span class="cart-badge">${cart.length}</span>` : ''}
                </button>
            </li>
            <li class="nav-user-info">
                <span class="nav-user-name">Hi, ${currentUser.name}</span>
                <button class="btn-logout" onclick="logout()">Logout</button>
            </li>
        `;
    } else {
        nav.innerHTML = `
            <li><button class="nav-link" onclick="showView('register')">Register</button></li>
            <li><button class="nav-link" onclick="showView('login')">Login</button></li>
        `;
    }
}

// ============================================
//  TOAST NOTIFICATIONS
// ============================================

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const icons = { success: '✓', error: '✕', info: 'ℹ' };
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type] || ''}</span> ${message}`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'toastOut 0.35s ease forwards';
        setTimeout(() => toast.remove(), 350);
    }, 3000);
}

// ============================================
//  API HELPER (No JWT)
// ============================================

async function apiCall(url, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(API + url, options);
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || data.error || 'Request failed');
    }
    return data;
}

// ============================================
//  BUTTON RIPPLE EFFECT
// ============================================

document.addEventListener('pointerdown', (e) => {
    const btn = e.target.closest('.btn, .btn-add-cart');
    if (btn) {
        const rect = btn.getBoundingClientRect();
        const x = ((e.clientX - rect.left) / rect.width) * 100;
        const y = ((e.clientY - rect.top) / rect.height) * 100;
        btn.style.setProperty('--ripple-x', x + '%');
        btn.style.setProperty('--ripple-y', y + '%');
    }
});

// ============================================
//  REGISTER
// ============================================

document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('btn-register');
    const name = document.getElementById('reg-name').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const role = document.getElementById('reg-role').value;

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Registering...';

    try {
        await apiCall('/api/auth/register', 'POST', { name, email, password, role });
        pendingEmail = email;
        document.getElementById('otp-email-display').textContent = email;
        showToast('OTP sent to your email!', 'success');
        showView('otp');
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Create Account';
    }
});

// ============================================
//  OTP VERIFICATION
// ============================================

document.querySelectorAll('.otp-input').forEach((input, idx, inputs) => {
    input.addEventListener('input', (e) => {
        const val = e.target.value;
        if (val && idx < inputs.length - 1) {
            inputs[idx + 1].focus();
        }
    });
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Backspace' && !e.target.value && idx > 0) {
            inputs[idx - 1].focus();
        }
    });
});

document.getElementById('btn-verify-otp').addEventListener('click', async () => {
    const btn = document.getElementById('btn-verify-otp');
    const inputs = document.querySelectorAll('.otp-input');
    const otp = Array.from(inputs).map(i => i.value).join('');

    if (otp.length !== 6) {
        showToast('Please enter all 6 digits', 'error');
        return;
    }

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Verifying...';

    try {
        await apiCall('/api/auth/verify-otp', 'POST', { email: pendingEmail, otp });
        showToast('Email verified! Please login.', 'success');
        showView('login');
        document.getElementById('login-email').value = pendingEmail;
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Verify OTP';
    }
});

async function resendOtp() {
    try {
        await apiCall('/api/auth/resend-otp', 'POST', { email: pendingEmail });
        showToast('OTP resent!', 'success');
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ============================================
//  LOGIN (No JWT)
// ============================================

document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('btn-login');
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Signing in...';

    try {
        const data = await apiCall('/api/auth/login', 'POST', { email, password });

        // Store user info (no token)
        currentUser = { userId: data.userId, name: data.name, email: data.email, role: data.role };
        localStorage.setItem('user', JSON.stringify(currentUser));

        // Load cart from localStorage
        const savedCart = localStorage.getItem('cart_' + currentUser.userId);
        if (savedCart) cart = JSON.parse(savedCart);

        showToast(`Welcome back, ${data.name}!`, 'success');
        showView('products');
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Sign In';
    }
});

function logout() {
    currentUser = null;
    cart = [];
    localStorage.removeItem('user');
    showToast('Logged out', 'info');
    showView('login');
}

// ============================================
//  SHIMMER SKELETON LOADER
// ============================================

function renderSkeletons(count = 6) {
    return Array.from({ length: count }, () => `
        <div class="skeleton-card">
            <div class="skeleton-image"></div>
            <div class="skeleton-body">
                <div class="skeleton-line medium"></div>
                <div class="skeleton-line"></div>
                <div class="skeleton-line short"></div>
                <div class="skeleton-line price"></div>
                <div class="skeleton-line button"></div>
            </div>
        </div>
    `).join('');
}

// ============================================
//  PRODUCTS
// ============================================

async function loadProducts() {
    const grid = document.getElementById('products-grid');
    grid.innerHTML = renderSkeletons(6);

    try {
        const products = await apiCall('/product/view');

        if (products.length === 0) {
            grid.innerHTML = '<p style="color: var(--text-muted); grid-column: 1/-1; text-align: center; padding: 60px;">No products available yet.</p>';
            return;
        }

        grid.innerHTML = products.map((p, i) => {
            const stockClass = p.productQuantity === 0 ? 'out' : p.productQuantity < 5 ? 'low' : '';
            const stockText = p.productQuantity === 0 ? 'Out of stock' : `${p.productQuantity} in stock`;
            const emoji = getProductEmoji(p.productName);

            return `
                <div class="product-card" style="animation-delay: ${i * 0.08}s">
                    <div class="product-image">${emoji}</div>
                    <div class="product-info">
                        <div class="product-name">${escapeHtml(p.productName)}</div>
                        <div class="product-desc">${escapeHtml(p.productDescription)}</div>
                        <div class="product-manufacturer">by ${escapeHtml(p.manufacturer)}</div>
                        <div class="product-footer">
                            <span class="product-price">₹${p.productPrice.toLocaleString()}</span>
                            <span class="product-stock ${stockClass}">${stockText}</span>
                        </div>
                        <button class="btn-add-cart"
                                onclick='addToCart(${JSON.stringify(p).replace(/'/g, "&#39;")})'  
                                ${p.productQuantity === 0 ? 'disabled style="opacity:0.4;cursor:not-allowed"' : ''}>
                            <span>${p.productQuantity === 0 ? 'Out of Stock' : '+ Add to Cart'}</span>
                        </button>
                    </div>
                </div>
            `;
        }).join('');

    } catch (err) {
        grid.innerHTML = `<p style="color: var(--danger); grid-column: 1/-1; text-align: center; padding: 60px;">Failed to load products: ${err.message}</p>`;
    }
}

function getProductEmoji(name) {
    const n = name.toLowerCase();
    if (n.includes('phone') || n.includes('iphone') || n.includes('mobile')) return '📱';
    if (n.includes('laptop') || n.includes('macbook') || n.includes('computer')) return '💻';
    if (n.includes('headphone') || n.includes('earphone') || n.includes('airpod')) return '🎧';
    if (n.includes('watch') || n.includes('smartwatch')) return '⌚';
    if (n.includes('camera')) return '📷';
    if (n.includes('tv') || n.includes('television') || n.includes('monitor')) return '📺';
    if (n.includes('shoe') || n.includes('sneaker')) return '👟';
    if (n.includes('shirt') || n.includes('tshirt')) return '👕';
    if (n.includes('book')) return '📚';
    if (n.includes('bag') || n.includes('backpack')) return '🎒';
    return '📦';
}

function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

// ============================================
//  CART (Local)
// ============================================

function addToCart(product) {
    if (!currentUser) {
        showToast('Please login first', 'error');
        showView('login');
        return;
    }

    cart.push(product);
    localStorage.setItem('cart_' + currentUser.userId, JSON.stringify(cart));
    updateNav();
    showToast(`${product.productName} added to cart!`, 'success');
}

function removeFromCart(index) {
    const removed = cart.splice(index, 1);
    if (currentUser) {
        localStorage.setItem('cart_' + currentUser.userId, JSON.stringify(cart));
    }
    renderCart();
    updateNav();
    if (removed.length) showToast(`${removed[0].productName} removed`, 'info');
}

function renderCart() {
    const container = document.getElementById('cart-items');
    const countLabel = document.getElementById('cart-count-label');
    countLabel.textContent = `${cart.length} item${cart.length !== 1 ? 's' : ''}`;

    if (cart.length === 0) {
        container.innerHTML = `
            <div class="cart-empty">
                <div class="cart-empty-icon">🛒</div>
                <p>Your cart is empty</p>
                <button class="btn btn-secondary btn-sm" style="margin-top: 16px" onclick="showView('products')">Browse Products</button>
            </div>
        `;
        return;
    }

    const total = cart.reduce((sum, p) => sum + (p.productPrice || 0), 0);

    const itemsHtml = cart.map((item, i) => `
        <div class="cart-item" style="animation-delay: ${i * 0.06}s">
            <div class="cart-item-icon">${getProductEmoji(item.productName)}</div>
            <div class="cart-item-details">
                <div class="cart-item-name">${escapeHtml(item.productName)}</div>
                <div class="cart-item-price">₹${item.productPrice.toLocaleString()}</div>
            </div>
            <button class="cart-item-remove" onclick="removeFromCart(${i})">✕ Remove</button>
        </div>
    `).join('');

    container.innerHTML = `
        ${itemsHtml}
        <div class="cart-summary">
            <div class="cart-total">
                <span>Total</span>
                <span class="cart-total-amount">₹${total.toLocaleString()}</span>
            </div>
            <button class="btn btn-success" style="width: 100%;" onclick="checkout()">
                Proceed to Payment →
            </button>
        </div>
    `;
}

async function checkout() {
    if (!currentUser) {
        showToast('Please login first', 'error');
        return;
    }
    if (cart.length === 0) {
        showToast('Cart is empty', 'error');
        return;
    }

    const total = cart.reduce((sum, p) => sum + (p.productPrice || 0), 0);

    try {
        showToast('Creating order...', 'info');
        const order = await apiCall('/order/create', 'POST', { orderStatus: 'PENDING' });

        const paymentResp = await apiCall('/payment/create', 'POST', {
            userId: currentUser.userId,
            orderId: order.orderId,
            amount: total
        });

        showToast('Redirecting to Razorpay...', 'info');

        const options = {
            key: paymentResp.razorpayKeyId || '',
            amount: total * 100,
            currency: 'INR',
            name: 'Ecommerce',
            description: `Order #${order.orderId}`,
            order_id: paymentResp.razorpayOrderId,
            handler: async function (response) {
                try {
                    await apiCall('/payment/verify', 'POST', {
                        razorpayOrderId: response.razorpay_order_id,
                        razorpayPaymentId: response.razorpay_payment_id,
                        razorpaySignature: response.razorpay_signature
                    });
                    showToast('Payment successful! 🎉', 'success');
                    cart = [];
                    if (currentUser) localStorage.setItem('cart_' + currentUser.userId, JSON.stringify(cart));
                    updateNav();
                    showView('products');
                } catch (err) {
                    showToast('Payment verification failed: ' + err.message, 'error');
                }
            },
            prefill: {
                name: currentUser.name,
                email: currentUser.email
            },
            theme: {
                color: '#6c5ce7'
            }
        };

        if (typeof Razorpay !== 'undefined') {
            const rzp = new Razorpay(options);
            rzp.open();
        } else {
            showToast('Payment order created! Order ID: ' + paymentResp.razorpayOrderId, 'success');
            showToast('Add Razorpay checkout.js for live payments', 'info');
        }

    } catch (err) {
        showToast('Checkout failed: ' + err.message, 'error');
    }
}

// ============================================
//  INIT
// ============================================

(function init() {
    // Restore session from localStorage (no JWT)
    const savedUser = localStorage.getItem('user');

    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        const savedCart = localStorage.getItem('cart_' + currentUser.userId);
        if (savedCart) cart = JSON.parse(savedCart);
        showView('products');
    } else {
        localStorage.removeItem('user');
        showView('register');
    }
    updateNav();
})();

// ============================================
//  ADMIN DASHBOARD
// ============================================

const adminForm = document.getElementById('admin-add-product-form');
if (adminForm) {
    adminForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!currentUser || currentUser.role !== 'ADMIN') return;

        const btn = document.getElementById('btn-admin-add');
        const product = {
            name: document.getElementById('admin-prod-name').value,
            description: document.getElementById('admin-prod-desc').value,
            price: parseFloat(document.getElementById('admin-prod-price').value),
            stockQuantity: parseInt(document.getElementById('admin-prod-qty').value),
            manufacturer: document.getElementById('admin-prod-mfg').value,
            categoryId: document.getElementById('admin-prod-cat').value
        };

        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Adding...';

        try {
            const response = await fetch('/product/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': currentUser.userId
                },
                body: JSON.stringify(product)
            });

            if (!response.ok) {
                const errData = await response.json().catch(() => ({}));
                throw new Error(errData.message || 'Failed to add product');
            }

            showToast('Product added successfully!', 'success');
            adminForm.reset();
        } catch (err) {
            showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Add Product';
        }
    });
}
