/* eslint-env browser */
document.addEventListener("DOMContentLoaded", () => {

    // --- REFERENCIAS ---
    const resultsList = document.getElementById("resultsList");
    const resultsCount = document.getElementById("resultsCount");

    // Filtros laterales
    const maxPrice = document.getElementById("maxPrice");
    const maxPriceVal = document.getElementById("maxPriceVal");
    const sortBy = document.getElementById("sortBy");
    const minRating = document.getElementById("minRating");
    const minRatingVal = document.getElementById("minRatingVal");

    // --- CORRECCIÓN DE IDs DEL HEADER ---
    const searchInput = document.getElementById("q");
    // Usamos los IDs que definiste en el HTML del fragment
    const checkinEl = document.getElementById("checkin-index");
    const checkoutEl = document.getElementById("checkout-index");
    const searchBtn = document.getElementById("searchBtn");
    const peopleCountEl = document.getElementById("peopleCount");

    let lastQuery = "";

    // --- UTILIDADES ---
    function formatDistance(km) {
        if (!km && km !== 0) return "";
        if (typeof km !== "number") return km;
        if (km === 0) return "en el centro";
        if (km < 0.1) return "a 100 m del centro";
        return `a ${km.toFixed(1)} km del centro`;
    }

    window.book = function(id) {
        window.location.href = `/alojamientos/detalleAlojamientos?id=${id}`;
    };

    function renderList(items) {
        resultsList.innerHTML = "";
        if (!items || !items.length) {
            resultsList.innerHTML = '<div class="no-results-card">No se encontraron alojamientos.</div>';
            if(resultsCount) resultsCount.textContent = "Mostrando 0 alojamientos";
            return;
        }

        resultsCount.textContent = `Mostrando ${items.length} alojamientos`;

        items.forEach(it => {
            const imagen = (it.images && it.images.length > 0) ? it.images[0] : '/images/no-image.png';
            const puntuacion = it.rating ? `⭐ ${it.rating}` : 'Nuevo';

            const card = document.createElement("article");
            card.className = "card";
            card.innerHTML = `
                <img src="${imagen}" alt="${it.title}" loading="lazy" />
                <div class="info">
                    <div class="title">${it.title}</div>
                    <div class="meta">${puntuacion} · ${formatDistance(it.distance)}</div>
                    <div class="details">
                       Ciudad: ${it.ciudad} · Tipo: ${it.type} · Cap.: ${it.capacity} p.
                    </div>
                </div>
                <div class="actions">
                    <div class="price">${it.price} €</div>
                    <button class="btn btn-primary btn-sm" onclick="book(${it.id})">
                        Ver disponibilidad
                    </button>
                </div>
            `;
            resultsList.appendChild(card);
        });
    }

    // --- FILTRADO AJAX ---
    async function applyFilters() {
        const checkedTypes = Array.from(document.querySelectorAll(".filter-type:checked")).map((n) => n.value);
        const max = maxPrice ? +maxPrice.value : 1000;
        const minR = minRating ? +minRating.value : 0;
        const s = sortBy ? sortBy.value : "recommend";

        let cap = 1;
        if (peopleCountEl && peopleCountEl.textContent) {
            const num = parseInt(peopleCountEl.textContent);
            if(!isNaN(num)) cap = num;
        }

        const params = new URLSearchParams();
        if (lastQuery) params.set("q", lastQuery);
        params.set("maxPrice", max);
        params.set("minRating", minR);
        params.set("capacity", cap);
        params.set("sortBy", s);

        // --- NUEVO: ENVIAR FECHAS A LA API ---
        if (checkinEl && checkinEl.value) {
            params.set("checkin", checkinEl.value);
        }
        if (checkoutEl && checkoutEl.value) {
            params.set("checkout", checkoutEl.value);
        }
        // -------------------------------------

        checkedTypes.forEach(t => params.append("types", t));

        try {
            const response = await fetch(`/api/alojamientos?${params.toString()}`);
            if (!response.ok) throw new Error("Error API");
            const items = await response.json();
            renderList(items);
        } catch (error) {
            console.error("Error:", error);
            resultsList.innerHTML = '<div class="no-results-card">Error al cargar resultados.</div>';
        }
    }

    // --- EVENTOS ---

    if (searchBtn) {
        searchBtn.addEventListener("click", (e) => {
            e.preventDefault();

            lastQuery = (searchInput?.value || "").trim().toLowerCase();
            const countStr = peopleCountEl ? peopleCountEl.textContent : "1";

            const p = new URLSearchParams(location.search);
            if (lastQuery) p.set("q", lastQuery); else p.delete("q");

            p.set("people", countStr);
            if (checkinEl?.value) p.set("checkin", checkinEl.value);
            if (checkoutEl?.value) p.set("checkout", checkoutEl.value);

            history.replaceState(null, "", `${location.pathname}?${p.toString()}`);
            applyFilters();
        });
    }

    if(maxPrice) {
        maxPrice.addEventListener("input", () => {
            maxPriceVal.textContent = maxPrice.value;
            applyFilters();
        });
    }

    if(minRating) {
        minRating.addEventListener("input", () => {
            minRatingVal.textContent = minRating.value;
            applyFilters();
        });
    }

    if(sortBy) sortBy.addEventListener("change", applyFilters);

    document.querySelectorAll(".filter-type").forEach(cb => {
        cb.addEventListener("change", applyFilters);
    });

    // --- CARGA INICIAL (HYDRATE) ---
    (function hydrate() {
        const p = new URLSearchParams(location.search);

        if (p.has("q") && searchInput) {
            searchInput.value = p.get("q");
            lastQuery = p.get("q").toLowerCase();
        }

        // --- NUEVO: RECUPERAR FECHAS DE LA URL PARA EL INPUT ---
        if (p.has("checkin") && checkinEl) {
            checkinEl.value = p.get("checkin");
        }
        if (p.has("checkout") && checkoutEl) {
            checkoutEl.value = p.get("checkout");
        }
        // ------------------------------------------------------

        if (maxPrice && p.has("maxPrice")) {
            maxPrice.value = p.get("maxPrice");
        }
        if (maxPrice && maxPriceVal) {
            maxPriceVal.textContent = maxPrice.value;
        }

        if (minRating) {
            if (p.has("minRating")) minRating.value = p.get("minRating");
            if (minRatingVal) minRatingVal.textContent = minRating.value;
        }

        applyFilters();
    })();
});