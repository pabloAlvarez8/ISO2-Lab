document.addEventListener("DOMContentLoaded", () => {
  
  // 1. Obtener el ID del alojamiento del HTML
  const alojamientoIdInput = document.getElementById("alojamientoIdHidden");
  
  if (!alojamientoIdInput) return; // Si no hay ID, no hacemos nada

  const alojamientoId = alojamientoIdInput.value;

  // 2. Llamar a la API para ver las fechas ocupadas (Calendario)
  fetch(`/reservas/api/ocupadas/${alojamientoId}`)
      .then(response => response.json())
      .then(data => {
          // Transformamos al formato de Flatpickr
          const disabledDates = data.map(rango => {
              const partes = rango.split(":");
              return { from: partes[0], to: partes[1] };
          });

          // 3. Iniciar el calendario con las fechas bloqueadas
          iniciarCalendario(disabledDates);
      })
      .catch(err => console.error("Error cargando disponibilidad:", err));

  // 4. Lógica para enviar o modificar valoración (ACTUALIZADO)
  const formValoracion = document.getElementById("formValoracion");
  
  if (formValoracion) {
    formValoracion.addEventListener("submit", function(e) {
        e.preventDefault();

        const usuarioIdInput = document.getElementById("usuarioLogueadoId");
        if (!usuarioIdInput) {
            alert("Error: No se ha podido identificar al usuario.");
            return;
        }

        const usuarioId = usuarioIdInput.value;
        const puntuacion = document.getElementById("puntuacionInput").value;
        const comentario = document.getElementById("comentarioInput").value;

        if(!comentario || !puntuacion) {
            alert("Por favor completa la puntuación y el comentario");
            return;
        }

        const data = {
            inmuebleId: parseInt(alojamientoId),
            usuarioId: parseInt(usuarioId),
            puntuacion: parseFloat(puntuacion),
            comentario: comentario
        };

        // CAMBIO PRINCIPAL: Usamos el endpoint /guardar que gestiona Creación y Edición
        fetch('/valoraciones/guardar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(response => {
            // MANEJO DE ERROR 403 (Regla de negocio: No ha visitado)
            if (response.status === 403) {
                return response.json().then(err => {
                    throw new Error(err.mensaje); // Lanzamos error con el mensaje del servidor
                });
            }
            if (!response.ok) {
                throw new Error("Error al procesar la reseña.");
            }
            return response.json();
        })
        .then(responseData => {
            // responseData contiene: { valoracion: {...}, nuevaMedia: 4.5 }
            
            // 1. Actualizamos la media visualmente al instante
            const spanMedia = document.getElementById("mediaValoracionTexto");
            if(spanMedia && responseData.nuevaMedia !== undefined) {
                spanMedia.innerText = responseData.nuevaMedia; // Actualizamos el número
            }

            alert("¡Tu reseña se ha guardado correctamente!");

            // 2. Recargamos para que:
            //    - Se actualice la lista de comentarios (evitando duplicados si era edición)
            //    - El formulario muestre los datos actualizados
            window.location.reload();
        })
        .catch(err => {
            console.error(err);
            alert(err.message); // Muestra: "Para poder escribir una reseña... tienes que visitarlo"
        });
    });
  }
});

// --- FUNCIONES AUXILIARES ---

function iniciarCalendario(fechasOcupadas) {
  flatpickr("#rangoFechas", {
      mode: "range",
      dateFormat: "Y-m-d",
      minDate: "today",
      disable: fechasOcupadas,
      locale: {
          firstDayOfWeek: 1
      },
      onChange: function(selectedDates, dateStr, instance) {
          if (selectedDates.length === 2) {
              const entrada = instance.formatDate(selectedDates[0], "Y-m-d");
              const salida = instance.formatDate(selectedDates[1], "Y-m-d");
              
              document.getElementById("inputEntrada").value = entrada;
              document.getElementById("inputSalida").value = salida;
          }
      }
  });
}


function actualizarMedia(id) {
    fetch(`/valoraciones/media/${id}`)
        .then(res => res.json())
        .then(media => {
            const spanMedia = document.getElementById("mediaValoracionTexto");
            if(spanMedia) {
                spanMedia.innerText = media ? media.toFixed(1) : "0.0";
            }
        })
        .catch(err => console.error("Error actualizando media:", err));
}