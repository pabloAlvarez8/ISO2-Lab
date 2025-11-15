// Archivo: /static/js/index.js

(function () {
  // --- Utilidades ---
  function buildParams(extra = {}) {
    const [qEl, inEl, outEl] = document.querySelectorAll(".search-bar input");
    const base = {
      q: qEl.value.trim() || "",
      checkin: inEl.value || "",
      checkout: outEl.value || "",
      people: String(currentPeople)
    };
    return new URLSearchParams({ ...base, ...extra });
  }
  function go(params) {
    // Importante: usamos la ruta del controlador, no el archivo .html
    location.href = `/alojamientos?${params.toString()}`; // Nota: He cambiado /buscador por /alojamientos para que coincida con tu controller
  }
  function onKeyActivate(el, handler) {
    el.addEventListener("keydown", (e) => {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        handler();
      }
    });
  }

  // --- Contador de personas ---
  const minus = document.querySelector(".people-counter button:first-child");
  const plus = document.querySelector(".people-counter button:last-child");
  const span = document.querySelector(".people-counter span");
  let currentPeople = 2;
  function updatePeople() { span.textContent = currentPeople; }
  minus.addEventListener("click", () => { if (currentPeople > 1) { currentPeople--; updatePeople(); } });
  plus.addEventListener("click", () => { currentPeople++; updatePeople(); });
  updatePeople();

  // --- Buscar libre con barra ---
  const btn = document.querySelector(".btn-search");
  btn.addEventListener("click", () => go(buildParams()));
  document.querySelector(".search-bar").addEventListener("keydown", (e) => {
    if (e.key === "Enter") btn.click();
  });

  // --- Explorar por tipo (Apartamentos / Hoteles) ---
  document.querySelectorAll(".type-card").forEach(card => {
    const type = card.getAttribute("data-type");
    const handler = () => go(buildParams({ type }));
    card.addEventListener("click", handler);
    onKeyActivate(card, handler);
  });

  // --- Destinos populares (Madrid, etc.) ---
  // Limpia SIEMPRE fechas y fuerza q=<ciudad>
  document.querySelectorAll(".destino").forEach(tile => {
    const city = tile.getAttribute("data-city");
    const handler = () => {
      const params = new URLSearchParams({
        q: city,
        checkin: "",
        checkout: "",
        people: String(currentPeople)
      });
      go(params);
    };
    tile.addEventListener("click", handler);
    onKeyActivate(tile, handler);
  });
})();