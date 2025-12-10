/* eslint-env browser */

// Función del botón "Volver a inicio" y logo
// Se define globalmente para que el onclick del HTML la encuentre
function volverInicio() {
  window.location.href = "/"; // Asumiendo que /inicio es tu página principal
}

// Función del botón "Reservar"
let selectedData = null; // Guardar los datos para la función reservar

function reservar() {
  if (selectedData)
    alert(`✅ Has reservado: ${selectedData.title} por ${selectedData.price} € / noche`);
}

// Lógica del Carrusel 
let currentSlideIndex = 0;
let slideImages = [];

function showSlide(index) {
  const carouselImg = document.getElementById("detalleImg");
  if (carouselImg) {
    carouselImg.src = slideImages[index];
  }
}

// Hacemos las funciones del carrusel globales para los onclick
window.nextSlide = function () {
  currentSlideIndex = (currentSlideIndex + 1) % slideImages.length;
  showSlide(currentSlideIndex);
}

window.prevSlide = function () {
  currentSlideIndex = (currentSlideIndex - 1 + slideImages.length) % slideImages.length;
  showSlide(currentSlideIndex);
}


// Carga de datos al iniciar la página 
document.addEventListener("DOMContentLoaded", () => {
  // Recuperar alojamiento del localStorage
  const selectedJSON = localStorage.getItem("selectedLodging");

  if (selectedJSON) {
    const selected = JSON.parse(selectedJSON);
    selectedData = selected; // Guarda los datos para la función 'reservar'

    // 🏠 Mostrar los datos originales
    document.getElementById("detalleTitulo").textContent = selected.title;
    document.getElementById("detalleCiudad").textContent =
      "📍 Ciudad: " + selected.ciudad;
    document.getElementById("detalleTipo").textContent =
      "🏠 Tipo: " + selected.type;
    document.getElementById("detalleCapacidad").textContent =
      "👥 Capacidad: " + selected.capacity + " personas";
    document.getElementById("detalleDistancia").textContent =
      "📏 Distancia al centro: " + selected.distance;
    document.getElementById("detalleRating").textContent =
      "⭐ Valoración: " + selected.rating;
    document.getElementById("detallePrecio").textContent =
      selected.price + " € / noche";

    // Descripción más elaborada
    document.getElementById("lodgingDescription").textContent =
      `Este ${selected.type} se encuentra en ${selected.ciudad}, a ${selected.distance} del centro.
      Ideal para ${selected.capacity} personas. Ofrece todas las comodidades modernas, con una valoración media de ${selected.rating} estrellas.`;

    // Carrusel: varias imágenes
    slideImages = selected?.images || [selected?.img]; // Carga las imágenes
    showSlide(currentSlideIndex); // Muestra la primera imagen

    // Manejo de error: si se accede directamente sin seleccionar alojamiento
  } else {
    document.querySelector("main").innerHTML = "<p>No se ha encontrado información del alojamiento. <a href='/'>Volver al inicio</a>.</p>";
  }

  // 💬 Comentarios simulados
  const comentarios = [
    { nombre: "María G.", puntuacion: 5, texto: "Todo perfecto, muy limpio y buena ubicación." },
    { nombre: "Carlos P.", puntuacion: 4, texto: "Buen sitio, aunque el wifi podría mejorar." },
    { nombre: "Lucía D.", puntuacion: 5, texto: "La experiencia fue excelente. Repetiría sin dudarlo." }
  ];

  const commentsList = document.getElementById("commentsList");
  comentarios.forEach(c => {
    const div = document.createElement("div");
    div.className = "comment";
    div.innerHTML = `<strong>${c.nombre}</strong> <span>⭐ ${c.puntuacion}</span>
                     <p>${c.texto}</p>`;
    commentsList.appendChild(div);
  });
});