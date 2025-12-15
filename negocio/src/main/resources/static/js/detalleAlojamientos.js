document.addEventListener("DOMContentLoaded", () => {
  
  // 1. Obtener el ID del alojamiento del HTML
  const alojamientoIdInput = document.getElementById("alojamientoIdHidden");
  
  if (!alojamientoIdInput) return; // Si no hay ID, no hacemos nada

  const alojamientoId = alojamientoIdInput.value;

  // 2. Llamar a la API para ver las fechas ocupadas (Calendario)
  fetch(`/reservas/api/ocupadas/${alojamientoId}`)
      .then(response => response.json())
      .then(data => {
          const disabledDates = data.map(rango => {
              const partes = rango.split(":");
              return { from: partes[0], to: partes[1] };
          });
          iniciarCalendario(disabledDates);
      })
      .catch(err => console.error("Error cargando disponibilidad:", err));

  // --- LÓGICA AVANZADA DE ESTRELLAS (CORREGIDO) ---
  const starWidget = document.getElementById("starWidget");
  const starsGold = document.getElementById("starsGold");
  const hiddenInput = document.getElementById("puntuacionInput");
  const valorVisual = document.getElementById("valorVisual");

  if (starWidget && starsGold && hiddenInput) {
      
      // Función para calcular la puntuación
      function calcularPuntuacion(e) {
          const rect = starWidget.getBoundingClientRect();
          const paddingLeft = 15; // Debe coincidir con CSS .star-widget padding-left
          
          // Posición X relativa al inicio de las estrellas
          let x = e.clientX - rect.left - paddingLeft;
          
          // Ancho real de la zona de estrellas (ancho total - padding)
          const widthStars = rect.width - paddingLeft;

          // Si estamos en la zona de padding (izquierda), es 0
          if (x < 0) {
              return 0;
          }

          // Porcentaje sobre la zona de estrellas únicamente
          let percent = x / widthStars;
          if (percent > 1) percent = 1;

          let rawScore = percent * 5;
          let score = Math.ceil(rawScore * 2) / 2; // Redondear a 0.5

          if (score < 0) score = 0;
          if (score > 5) score = 5;

          return score;
      }

      function pintarEstrellas(score) {
          const porcentaje = (score / 5) * 100;
          starsGold.style.width = `${porcentaje}%`;
          if (valorVisual) valorVisual.innerText = score.toFixed(1);
      }

      // Hover
      starWidget.addEventListener("mousemove", (e) => {
          const score = calcularPuntuacion(e);
          pintarEstrellas(score);
      });

      // Salir del widget (restaurar)
      starWidget.addEventListener("mouseleave", () => {
          const valorGuardado = parseFloat(hiddenInput.value) || 0;
          pintarEstrellas(valorGuardado);
      });

      // Click (guardar)
      starWidget.addEventListener("click", (e) => {
          const score = calcularPuntuacion(e);
          hiddenInput.value = score;
          pintarEstrellas(score);
          // Feedback visual
          starsGold.style.opacity = "0.6";
          setTimeout(() => starsGold.style.opacity = "1", 150);
      });

      // Inicializar
      pintarEstrellas(parseFloat(hiddenInput.value));
  }
  // --- FIN LÓGICA ESTRELLAS ---


  // 4. Enviar valoración
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

        // Validamos que exista puntuación (0 es válido, vacío no)
        if(!comentario || puntuacion === "" || puntuacion === null) {
            alert("Por favor selecciona una puntuación y escribe un comentario.");
            return;
        }

        const data = {
            inmuebleId: parseInt(alojamientoId),
            usuarioId: parseInt(usuarioId),
            puntuacion: parseFloat(puntuacion),
            comentario: comentario
        };

        fetch('/valoraciones/guardar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (response.status === 403) {
                return response.json().then(err => { throw new Error(err.mensaje); });
            }
            if (!response.ok) {
                throw new Error("Error al procesar la reseña.");
            }
            return response.json();
        })
        .then(responseData => {
            const spanMedia = document.getElementById("mediaValoracionTexto");
            if(spanMedia && responseData.nuevaMedia !== undefined) {
                spanMedia.innerText = responseData.nuevaMedia;
            }

            alert("¡Tu reseña se ha guardado correctamente!");
            window.location.reload();
        })
        .catch(err => {
            console.error(err);
            alert(err.message);
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
      locale: { firstDayOfWeek: 1 },
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