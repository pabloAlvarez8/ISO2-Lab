/* eslint-env browser */

// Variables globales
let selectedData = null;

// Navegación
function volverInicio() {
  window.location.href = "/index"; 
}

// Reserva (Simulada por ahora)
function reservar() {
  if (selectedData)
    alert(`✅ Has reservado: ${selectedData.title} por ${selectedData.price} € / noche`);
}

// --- CARGA INICIAL ---
document.addEventListener("DOMContentLoaded", () => {
  // 1. Recuperar info del localStorage
  const selectedJSON = localStorage.getItem("selectedLodging");

  if (selectedJSON) {
    const selected = JSON.parse(selectedJSON);
    selectedData = selected;

    // 2. Rellenar textos
    document.getElementById("detalleTitulo").textContent = selected.title || "Alojamiento";
    document.getElementById("detalleCiudad").textContent = "📍 Ciudad: " + (selected.city || "No especificada");
    document.getElementById("detalleTipo").textContent = "🏠 Tipo: " + (selected.type || "-");
    document.getElementById("detalleCapacidad").textContent = "👥 Capacidad: " + (selected.capacity || 0) + " personas";
    document.getElementById("detalleDistancia").textContent = "📏 Distancia al centro: " + (selected.distance || 0) + " km";
    document.getElementById("detallePrecio").textContent = (selected.price || 0) + " € / noche";
    
    if(selected.rating) {
        document.getElementById("detalleRating").textContent = "⭐ Valoración Media: " + selected.rating;
    }

    document.getElementById("lodgingDescription").textContent =
      `Disfruta de este fantástico ${selected.type} situado en ${selected.city}. ` +
      `Cuenta con espacio para ${selected.capacity} personas y todas las comodidades.`;

    // 3. PONER LA IMAGEN ÚNICA (Sin carrusel)
    const imgElement = document.getElementById("detalleImg");
    if (imgElement) {
        // Lógica para detectar si viene un array o un string simple
        let imagenSrc = "";
        if (selected.images && selected.images.length > 0) {
            imagenSrc = selected.images[0];
        } else {
            imagenSrc = selected.img || 'https://via.placeholder.com/900x500';
        }
        imgElement.src = imagenSrc;
    }

    // 4. CARGAR VALORACIONES DE LA BD (Backend Java)
    if (selected.id) {
        cargarValoracionesBD(selected.id);
    } else {
        document.getElementById("commentsList").innerHTML = "<p>Error: ID desconocido.</p>";
    }

  } else {
    document.querySelector("main").innerHTML = "<p>No se ha seleccionado ningún alojamiento. <a href='/'>Volver al inicio</a>.</p>";
  }
});

// --- FUNCIÓN QUE CONECTA CON LA BASE DE DATOS ---
async function cargarValoracionesBD(idInmueble) {
    const contenedor = document.getElementById("commentsList");
    contenedor.innerHTML = '<p class="text-muted">Cargando comentarios...</p>';
    
    try {
        // Llamada a tu Controlador Java
        const response = await fetch(`/valoraciones/inmueble/${idInmueble}`);
        
        if (!response.ok) throw new Error("Error de conexión con el servidor");

        const valoraciones = await response.json();
        contenedor.innerHTML = "";

        // Si no hay reseñas
        if (valoraciones.length === 0) {
            contenedor.innerHTML = "<p><em>No hay valoraciones todavía para este alojamiento.</em></p>";
            return;
        }

        // Si hay reseñas, las pintamos una a una
        valoraciones.forEach(val => {
            const divResena = document.createElement("div");
            divResena.className = "comment-card"; 

            const estrellasHTML = generarEstrellas(val.puntuacion);
            
            divResena.innerHTML = `
                <div class="comment-header">
                    <strong>${val.usuario.nombre} ${val.usuario.apellido}</strong>
                    <span class="stars">${estrellasHTML}</span>
                </div>
                <p class="comment-text">"${val.comentario}"</p>
                <small style="color:#aaa; font-size:0.8em">Publicado el ${new Date(val.createdAt).toLocaleDateString()}</small>
            `;
            contenedor.appendChild(divResena);
        });

    } catch (error) {
        console.error("Error:", error);
        contenedor.innerHTML = "<p style='color:red'>No se pudieron cargar las opiniones. Revisa que la base de datos esté encendida.</p>";
    }
}

// Función auxiliar para pintar ★
function generarEstrellas(puntos) {
    let html = "";
    for (let i = 1; i <= 5; i++) {
        if (i <= puntos) html += "★"; 
        else html += "☆"; 
    }
    return html;
}