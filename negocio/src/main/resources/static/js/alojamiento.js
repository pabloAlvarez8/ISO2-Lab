// ============================
// CARGA DE ALOJAMIENTOS
// ============================

document.addEventListener("DOMContentLoaded", async () => {
  try {
    const response = await fetch("/alojamientos/api");
    const alojamientos = await response.json();
    const container = document.getElementById("alojamientos-container");

    container.innerHTML = ""; // limpia el contenedor

    alojamientos.forEach((a) => {
      const card = document.createElement("div");
      card.classList.add("alojamiento-card");
      card.innerHTML = `
        <img src="${a.imagenUrl || '/img/default.jpg'}" alt="${a.nombre}" class="alojamiento-img">
        <h3>${a.nombre}</h3>
        <p>${a.ubicacion}</p>
        <p class="precio">${a.precio} €/noche</p>
      `;
      // Evento de click → guardar en localStorage y redirigir
      card.addEventListener("click", () => {
        localStorage.setItem("selectedAlojamiento", JSON.stringify(a));
        window.location.href = "/alojamientos/detalleAlojamientos";
      });
      container.appendChild(card);
    });
  } catch (error) {
    console.error("Error cargando alojamientos:", error);
  }
});
