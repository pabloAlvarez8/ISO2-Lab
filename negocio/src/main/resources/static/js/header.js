document.addEventListener("DOMContentLoaded", () => {
    console.log("🔵 header.js: Cargado y listo.");

    // --- 1. CONFIGURACIÓN DEL CALENDARIO (Flatpickr) ---
    // Buscamos los inputs. Usamos "checkin-index" que es el que tienes en el HTML.
    const checkinInput = document.getElementById("checkin-index");
    const checkoutInput = document.getElementById("checkout-index");

    // Solo activamos si existen en la página
    if (checkinInput && checkoutInput) {
        // Aseguramos que sean tipo texto para que Flatpickr funcione bien
        checkinInput.type = "text";
        checkoutInput.type = "text";

        flatpickr(checkinInput, {
            mode: "range",
            dateFormat: "Y-m-d", // Formato compatible con Java LocalDate
            minDate: "today",
            locale: "es",
            // Vinculamos el segundo input
            plugins: [new rangePlugin({ input: "#checkout-index" })],
            onChange: function(selectedDates) {
                console.log("📅 Fechas seleccionadas:", selectedDates);
            }
        });
        console.log("✅ Calendario Flatpickr activado.");
    }

    // --- 2. CONTADOR DE PERSONAS ---
    const minusBtn = document.getElementById("minusPerson");
    const plusBtn = document.getElementById("plusPerson");
    const peopleCountEl = document.getElementById("peopleCount");
    const searchBtn = document.getElementById("searchBtn");

    if (!minusBtn || !plusBtn || !peopleCountEl) {
        console.warn("⚠️ header.js: No se encontraron los elementos del contador.");
    } else {
        let count = 2;

        const updateVisual = () => {
            peopleCountEl.textContent = count;
        };

        // Leer URL inicial por si venimos de una búsqueda anterior
        const params = new URLSearchParams(window.location.search);
        if (params.has("people")) {
            count = parseInt(params.get("people")) || 2;
            updateVisual();
        }

        minusBtn.addEventListener("click", (e) => {
            e.preventDefault(); e.stopPropagation();
            if (count > 1) { count--; updateVisual(); }
        });

        plusBtn.addEventListener("click", (e) => {
            e.preventDefault(); e.stopPropagation();
            count++; updateVisual();
        });
    }

    // --- 3. BOTÓN BUSCAR (Redirección) ---
    // Nota: Quitamos el check de "!isBuscadorPage" para asegurar que funcione siempre en el index
    if (searchBtn) {
        searchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            console.log("🔎 Procesando búsqueda...");

            const q = document.getElementById("q")?.value || "";

            // AQUI ESTA LA CLAVE: Leemos los IDs correctos (-index)
            // Usamos ?.value || "" para evitar errores si el elemento no existe
            let checkin = document.getElementById("checkin-index")?.value || "";
            let checkout = document.getElementById("checkout-index")?.value || "";

            // (Opcional) Si en el futuro usas IDs sin "-index" en otra página, esto lo soporta:
            if (!checkin) checkin = document.getElementById("checkin")?.value || "";
            if (!checkout) checkout = document.getElementById("checkout")?.value || "";

            const people = peopleCountEl ? peopleCountEl.textContent : "2";

            // Validar (Opcional: puedes quitar esto si quieres permitir búsquedas vacías)
            /* if (!checkin || !checkout) {
                alert("Por favor selecciona las fechas de viaje.");
                return;
            }
            */

            const params = new URLSearchParams();
            if(q) params.set("q", q);
            if(checkin) params.set("checkin", checkin);
            if(checkout) params.set("checkout", checkout);
            params.set("people", people);

            console.log("🚀 Redirigiendo a:", `/alojamientos?${params.toString()}`);
            window.location.href = `/alojamientos?${params.toString()}`;
        });
    }

    // --- 4. DROPDOWN DE USUARIO ---
    // Ajustado para buscar dentro de .dropdown-container por seguridad
    const container = document.querySelector(".dropdown-container");
    if(container) {
        const btn = container.querySelector("button");
        const content = container.querySelector(".dropdown-content");

        if(btn && content) {
            btn.addEventListener("click", (e) => {
                e.stopPropagation();
                content.style.display = content.style.display === "block" ? "none" : "block";
            });
            document.addEventListener("click", () => content.style.display = "none");
        }
    }
});