document.addEventListener("DOMContentLoaded", () => {
    console.log("🔵 header.js: Cargado y listo.");

    // 1. Referencias
    const minusBtn = document.getElementById("minusPerson");
    const plusBtn = document.getElementById("plusPerson");
    const peopleCountEl = document.getElementById("peopleCount");
    const searchBtn = document.getElementById("searchBtn");

    // 2. Comprobación de existencia
    if (!minusBtn || !plusBtn || !peopleCountEl) {
        console.warn("⚠️ header.js: No se encontraron los elementos del contador. (Es normal si estás en el header simple)");
        // No hacemos return para que siga ejecutando el resto (dropdown, etc.)
    } else {
        console.log("✅ header.js: Contador encontrado.");

        let count = 2;

        // Función para pintar
        const updateVisual = () => {
            peopleCountEl.textContent = count;
            console.log("🔢 Contador actualizado a: " + count);
        };

        // Leer URL inicial
        const params = new URLSearchParams(window.location.search);
        if (params.has("people")) {
            count = parseInt(params.get("people")) || 2;
            updateVisual();
        }

        // LISTENERS (Con logs para ver si entran)
        minusBtn.addEventListener("click", (e) => {
            e.preventDefault(); // Evita recargas fantasmas
            e.stopPropagation();
            console.log("➖ Click en Menos");

            if (count > 1) {
                count--;
                updateVisual();
            }
        });

        plusBtn.addEventListener("click", (e) => {
            e.preventDefault(); // Evita recargas fantasmas
            e.stopPropagation();
            console.log("➕ Click en Más");

            count++;
            updateVisual();
        });
    }

    // 3. Botón Buscar (Redirección segura)
    const isBuscadorPage = document.getElementById("resultsList") !== null;

    if (searchBtn && !isBuscadorPage) {
        searchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            console.log("🔎 Redirigiendo al buscador...");

            const q = document.getElementById("q")?.value || "";
            const checkin = document.getElementById("checkin")?.value || "";
            const checkout = document.getElementById("checkout")?.value || "";
            // Leemos el valor visual actual (que acabamos de modificar con los botones)
            const people = peopleCountEl ? peopleCountEl.textContent : "2";

            const params = new URLSearchParams();
            if(q) params.set("q", q);
            if(checkin) params.set("checkin", checkin);
            if(checkout) params.set("checkout", checkout);
            params.set("people", people);

            window.location.href = `/alojamientos?${params.toString()}`;
        });
    }

    // 4. Dropdown (si existe)
    const btnDropdown = document.querySelector(".btn-dropdown");
    const dropdownContent = document.querySelector(".dropdown-content");
    if(btnDropdown && dropdownContent) {
        btnDropdown.addEventListener("click", (e) => {
            e.stopPropagation();
            dropdownContent.style.display = dropdownContent.style.display === "block" ? "none" : "block";
        });
        document.addEventListener("click", () => dropdownContent.style.display = "none");
    }
});