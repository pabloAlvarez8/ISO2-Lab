let alojamientosCargados = []; // Aquí guardaremos los resultados de la base de datos
// Elements
const resultsList = document.getElementById("resultsList");
const resultsCount = document.getElementById("resultsCount");
const maxPrice = document.getElementById("maxPrice");
const maxPriceVal = document.getElementById("maxPriceVal");
const sortBy = document.getElementById("sortBy");
const searchInput = document.getElementById("q");
const minRating = document.getElementById("minRating");
const minRatingVal = document.getElementById("minRatingVal");
const minusBtn = document.getElementById("minusPerson");
const plusBtn = document.getElementById("plusPerson");
const peopleCountEl = document.getElementById("peopleCount");
const checkinEl = document.getElementById("checkin");
const checkoutEl = document.getElementById("checkout");
const searchBtn = document.getElementById("searchBtn");

// Estado controlado SOLO al pulsar "Buscar"
let count = 2;
let lastQuery = "";
let lastCheckin = "";
let lastCheckout = "";

/** Convierte una distancia en km a un texto amigable para el usuario.
 * @param {number} km - La distancia en kilómetros.
 * @returns {string} El texto formateado.
 */
function formatDistance(km) {
    // Si la distancia es un número, la formateamos. Si ya es un texto, lo devolvemos tal cual.
    if (typeof km !== "number") {
        return km;
    }
    if (km === 0) return "en el centro";
    if (km < 0.1) return "a 100 m del centro";
    // .toFixed(1) limita a un decimal, ej: 0.8
    return `a ${km.toFixed(1)} km del centro`;
}

/**
 * Pinta la lista de resultados en el DOM.
 * @param {Array<Object>} items - El array de alojamientos a mostrar.
 */
function renderList(items) {
    resultsList.innerHTML = ""; // Limpia la lista anterior

    if (!items || !items.length) {
        // Muestra un mensaje si no hay resultados
        resultsList.innerHTML =
            '<div class="no-results-card">No hay alojamientos que coincidan con tu búsqueda.</div>';
        resultsCount.textContent = "Mostrando 0 alojamientos";
        return;
    }

    resultsCount.textContent = `Mostrando ${items.length} alojamientos`;

    for (const it of items) {
        const card = document.createElement("article");
        card.className = "card";
        card.innerHTML = `
            <img src="${it.images[0]}" alt="${it.title}" loading="lazy" />
            <div class="info">
                <div class="title">${it.title}</div>
                <div class="meta">⭐ ${it.rating} · ${formatDistance(
            it.distance
        )}</div>
                <div class="details">Tipo: ${it.type} · Ciudad: ${it.ciudad
            } · Capacidad: ${it.capacity} pers.</div>
            </div>
            <div class="actions">
                <div class="price">${it.price} €</div>
                <button class="btn-book" 
                        aria-label="Ver disponibilidad de ${it.title}" 
                        onclick="book(${it.id})">
                    Ver disponibilidad
                </button>
            </div>
        `;
        resultsList.appendChild(card);
    }
}

/**
 * Llama a la API del backend para obtener alojamientos filtrados y los renderiza.
 */
async function applyFilters() {
    // 1. Recopilamos TODOS los valores de los filtros (igual que en tu versión)
    const checkedTypes = Array.from(
        document.querySelectorAll(".filter-type:checked")
    ).map((n) => n.value);

    const max = +maxPrice.value;
    const minR = +minRating.value;
    const s = sortBy.value;

    // Usamos las variables de estado controlado para la barra de búsqueda
    const cap = count; // 'count' es tu variable global de personas
    const query = lastQuery; // 'lastQuery' es tu variable global de ciudad

    // 2. Construimos la URL con parámetros de búsqueda
    // Esto crea una URL como: /api/alojamientos?q=madrid&maxPrice=500&...
    const params = new URLSearchParams();
    params.set("q", query);
    params.set("maxPrice", max);
    params.set("minRating", minR);
    params.set("capacity", cap);
    params.set("sortBy", s);

    // Añadimos los 'types' manualmente (para que Spring los reciba como una lista)
    checkedTypes.forEach((type) => {
        params.append("types", type);
    });

    // Esta es la URL de tu API que creamos en Spring Boot
    const url = `/api/alojamientos?${params.toString()}`;

    // 3. Hacemos la llamada (fetch) a tu API de Spring Boot
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Error del servidor: ${response.status}`);
        }
        const items = await response.json(); // Convertimos la respuesta en JSON

        // 4. ¡Guardamos los datos y renderizamos!
        alojamientosCargados = items; // Guardamos los datos en nuestra variable global
        renderList(alojamientosCargados); // Usamos tu función renderList() existente
    } catch (error) {
        console.error("Error al buscar alojamientos:", error);
        resultsList.innerHTML =
            '<div class="no-results-card">Error al cargar los resultados. Inténtalo de nuevo.</div>';
        resultsCount.textContent = "Mostrando 0 alojamientos";
    }
}

// Personas: ACTUALIZA SOLO EL CONTADOR VISUAL; NO FILTRA EN VIVO
function updatePeople() {
    peopleCountEl.textContent = count;
}
minusBtn.addEventListener("click", () => {
    if (count > 1) {
        count--;
        updatePeople();
    }
});
plusBtn.addEventListener("click", () => {
    count++;
    updatePeople();
});
updatePeople();

// Click en Buscar: aquí se sincroniza todo y se filtra
searchBtn.addEventListener("click", () => {
    // Sincroniza estado controlado
    lastQuery = (searchInput.value || "").trim().toLowerCase();
    lastCheckin = checkinEl.value || "";
    lastCheckout = checkoutEl.value || "";

    // Sanea fechas (si el usuario puso mal el rango)
    if (lastCheckin && lastCheckout && lastCheckin > lastCheckout) {
        lastCheckout = "";
        checkoutEl.value = ""; // refleja la corrección en UI
    }

    applyFilters();
    // (Opcional) actualiza la URL con los parámetros de búsqueda
    const p = new URLSearchParams(location.search);
    if (lastQuery) p.set("q", lastQuery);
    else p.delete("q");
    p.set("people", String(count));
    if (lastCheckin) p.set("checkin", lastCheckin);
    else p.delete("checkin");
    if (lastCheckout) p.set("checkout", lastCheckout);
    else p.delete("checkout");
    history.replaceState(null, "", `${location.pathname}?${p.toString()}`);
});

// Enter dentro de la barra de búsqueda dispara "Buscar"
document.querySelector(".search-bar").addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
        e.preventDefault();
        searchBtn.click();
    }
});

// Filtros de la SIDEBAR siguen siendo en vivo (checkbox/range/select)
maxPrice.addEventListener("input", () => {
    maxPriceVal.textContent = maxPrice.value;
    applyFilters();
});
minRating.addEventListener("input", () => {
    minRatingVal.textContent = minRating.value;
    applyFilters();
});
sortBy.addEventListener("change", applyFilters);
document
    .querySelectorAll(".filter-type")
    .forEach((cb) => cb.addEventListener("change", applyFilters));

// Precarga desde la URL (solo una vez al inicio) y primer render
(function hydrateFromParams() {
    const p = new URLSearchParams(location.search);
    const q = p.get("q") || "";
    const type = p.get("type");
    const people = parseInt(p.get("people") || "", 10);
    const cIn = p.get("checkin") || "";
    const cOut = p.get("checkout") || "";

    if (q) {
        searchInput.value = q;
        lastQuery = q.toLowerCase();
    }
    if (!isNaN(people) && people > 0) {
        count = people;
        updatePeople();
    }
    if (cIn) {
        checkinEl.value = cIn;
        lastCheckin = cIn;
    }
    if (cOut) {
        checkoutEl.value = cOut;
        lastCheckout = cOut;
    }

    if (type) {
        document.querySelectorAll(".filter-type").forEach((cb) => {
            cb.checked = cb.value === type;
        });
    }

    maxPriceVal.textContent = maxPrice.value;
    minRatingVal.textContent = minRating.value;

    if (
        checkinEl.value &&
        checkoutEl.value &&
        checkinEl.value > checkoutEl.value
    ) {
        checkoutEl.value = "";
        lastCheckout = "";
    }

    applyFilters(); // pinta resultados iniciales con lo que haya en URL
})();

function book(id) {
    // CAMBIA 'data' POR 'alojamientosCargados'
    const selected = alojamientosCargados.find((item) => item.id === id);
    if (selected) {
        localStorage.setItem("selectedLodging", JSON.stringify(selected));
        window.location.href = "/alojamientos/detalleAlojamientos";
    }
}