/* =======================================================
   CONFIGURACIÓN INICIAL Y VARIABLES
   ======================================================= */
let alojamientosCargados = []; // Guardamos los datos recibidos de la API
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

// Variables de estado
let count = 2;
let lastQuery = "";
let lastCheckin = "";
let lastCheckout = "";

/* =======================================================
   FUNCIONES DE UTILIDAD
   ======================================================= */

function formatDistance(km) {
    if (!km && km !== 0) return "";
    if (typeof km !== "number") return km;
    if (km === 0) return "en el centro";
    if (km < 0.1) return "a 100 m del centro";
    return `a ${km.toFixed(1)} km del centro`;
}

/* =======================================================
   RENDERIZADO (AQUÍ ES DONDE PINTAMOS EL HTML)
   ======================================================= */
// EN TU ARCHIVO JS DEL BUSCADOR:

function renderList(items) {
    resultsList.innerHTML = "";

    if (!items || !items.length) {
        // ... mensaje vacio ...
        return;
    }

    resultsCount.textContent = `Mostrando ${items.length} alojamientos`;

    for (const it of items) {
        // AHORA USAMOS LOS NOMBRES DEL DTO (Inglés)
        // Nota: images es una lista, cogemos la primera [0]
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
                <button class="btn-book" onclick="book(${it.id})">
                    Ver disponibilidad
                </button>
            </div>
        `;
        resultsList.appendChild(card);
    }
}

/* =======================================================
   LLAMADA A LA API (BACKEND SPRING BOOT)
   ======================================================= */
async function applyFilters() {
    // 1. Recoger filtros del HTML
    const checkedTypes = Array.from(document.querySelectorAll(".filter-type:checked")).map((n) => n.value);
    const max = +maxPrice.value;
    const minR = +minRating.value;
    const s = sortBy.value;
    const cap = count;
    const query = lastQuery;

    // 2. Construir la URL para llamar al Java
    const params = new URLSearchParams();
    params.set("q", query);
    params.set("maxPrice", max);
    params.set("minRating", minR);
    params.set("capacity", cap);
    params.set("sortBy", s);
    checkedTypes.forEach(type => params.append("types", type));

    // ESTA RUTA DEBE EXISTIR EN TU JAVA (AlojamientoRestController)
    const url = `/api/alojamientos?${params.toString()}`;

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`Error API: ${response.status}`);

        const items = await response.json();
        alojamientosCargados = items;
        renderList(alojamientosCargados);

    } catch (error) {
        console.error("Error buscando alojamientos:", error);
        resultsList.innerHTML = '<div class="no-results-card">Error al conectar con el servidor.</div>';
    }
}

/* =======================================================
   LA FUNCIÓN QUE PEDÍAS (REDIRECCIÓN)
   ======================================================= */
function book(id) {
    // ANTES: Guardabas en localStorage (MAL para backend real)
    // AHORA: Rediriges pasando el ID en la URL
    window.location.href = `/alojamientos/detalleAlojamientos?id=${id}`;
}

/* =======================================================
   EVENT LISTENERS (BOTONES Y FILTROS)
   ======================================================= */

// Contador de personas
function updatePeople() { peopleCountEl.textContent = count; }
minusBtn.addEventListener("click", () => { if (count > 1) { count--; updatePeople(); } });
plusBtn.addEventListener("click", () => { count++; updatePeople(); });
updatePeople();

// Botón Buscar
searchBtn.addEventListener("click", () => {
    lastQuery = (searchInput.value || "").trim().toLowerCase();
    lastCheckin = checkinEl.value || "";
    lastCheckout = checkoutEl.value || "";

    // Sincronizar URL del navegador (estético)
    const p = new URLSearchParams(location.search);
    if (lastQuery) p.set("q", lastQuery); else p.delete("q");
    p.set("people", String(count));
    history.replaceState(null, "", `${location.pathname}?${p.toString()}`);

    applyFilters();
});

// Filtros laterales en vivo
maxPrice.addEventListener("input", () => { maxPriceVal.textContent = maxPrice.value; applyFilters(); });
minRating.addEventListener("input", () => { minRatingVal.textContent = minRating.value; applyFilters(); });
sortBy.addEventListener("change", applyFilters);
document.querySelectorAll(".filter-type").forEach((cb) => cb.addEventListener("change", applyFilters));

// Carga inicial al abrir la página
(function hydrateFromParams() {
    const p = new URLSearchParams(location.search);
    const q = p.get("q") || "";
    const people = parseInt(p.get("people") || "", 10);

    if (q) { searchInput.value = q; lastQuery = q.toLowerCase(); }
    if (!isNaN(people) && people > 0) { count = people; updatePeople(); }

    maxPriceVal.textContent = maxPrice.value;
    minRatingVal.textContent = minRating.value;

    applyFilters(); // Primera búsqueda automática
})();