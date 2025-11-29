/* eslint-env browser */

// Función autoejecutable (IIFE) para encapsular el código y no contaminar el ámbito global (window)
(function () {

  // Funciones 

  // Construye la Query String (ej: ?q=madrid&people=2) leyendo el estado actual del DOM
  function buildParams(extra = {}) {
    // Usamos destructuring para capturar los inputs de la barra de búsqueda
    const [qEl, inEl, outEl] = document.querySelectorAll(".search-bar input");

    const base = {
      q: qEl.value.trim() || "",       // Evitamos espacios en blanco accidentales
      checkin: inEl.value || "",
      checkout: outEl.value || "",
      people: String(currentPeople)    // Convertimos a string para la URL
    };
    // Fusionamos los parámetros base con los extra (ej: filtros de tipo)
    return new URLSearchParams({ ...base, ...extra });
  }

  // Centraliza la redirección. Si cambia la ruta del controlador, solo tocamos aquí.
  function go(params) {
    location.href = `/alojamientos?${params.toString()}`;
  }

  // Helper de Accesibilidad (A11Y): permite activar divs o spans con teclado (Enter/Espacio)
  function onKeyActivate(el, handler) {
    el.addEventListener("keydown", (e) => {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault(); // Evita scroll al pulsar espacio
        handler();
      }
    });
  }

  // Lógica del contador de personas 

  const minus = document.querySelector(".people-counter button:first-child");
  const plus = document.querySelector(".people-counter button:last-child");
  const span = document.querySelector(".people-counter span");
  
  let currentPeople = 2; // Valor inicial por defecto

  function updatePeople() { span.textContent = currentPeople; }

  minus.addEventListener("click", () => {
    // Validación lógica: nunca permitir menos de 1 persona
    if (currentPeople > 1) {
      currentPeople--;
      updatePeople();
    }
  });

  plus.addEventListener("click", () => {
    currentPeople++;
    updatePeople();
  });
  
  updatePeople(); // Inicializar visualmente al cargar

  // Barra de búsqueda

  const btn = document.querySelector(".btn-search");

  // 1. Clic en el botón de lupa
  btn.addEventListener("click", () => go(buildParams()));
  
  // 2. Pulsar Enter estando en cualquiera de los inputs de la barra
  document.querySelector(".search-bar").addEventListener("keydown", (e) => {
    if (e.key === "Enter") btn.click();
  });

  // Filtros 

  document.querySelectorAll(".type-card").forEach(card => {
    const type = card.getAttribute("data-type");
    
    // Al hacer clic, buscamos manteniendo las fechas y personas, pero forzando el tipo
    const handler = () => go(buildParams({ type }));
    
    card.addEventListener("click", handler);
    onKeyActivate(card, handler); // Soporte para teclado
  });

  // Filtros por destinos

  document.querySelectorAll(".destino").forEach(tile => {
    const city = tile.getAttribute("data-city");
    
    const handler = () => {
      // IMPORTANTE: Al elegir destino, limpiamos las fechas (checkin/checkout)
      // para asegurar que el usuario vea disponibilidad general en esa ciudad.
      const params = new URLSearchParams({
        q: city,
        checkin: "",
        checkout: "",
        people: String(currentPeople) // Mantenemos el número de personas
      });
      go(params);
    };

    tile.addEventListener("click", handler);
    onKeyActivate(tile, handler);
  });

})();