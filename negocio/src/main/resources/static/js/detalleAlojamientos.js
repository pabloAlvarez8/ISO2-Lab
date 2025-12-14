document.addEventListener("DOMContentLoaded", () => {
  
  // 1. Obtener el ID del alojamiento del HTML
  const alojamientoIdInput = document.getElementById("alojamientoIdHidden");
  
  if (!alojamientoIdInput) return; // Si no hay ID, no hacemos nada (evita errores)

  const alojamientoId = alojamientoIdInput.value;

  // 2. Llamar a la API para ver las fechas ocupadas
  fetch(`/reservas/api/ocupadas/${alojamientoId}`)
      .then(response => response.json())
      .then(data => {
          // data viene como ["2023-12-01:2023-12-05", ...]
          // Lo transformamos al formato de Flatpickr: { from: "...", to: "..." }
          const disabledDates = data.map(rango => {
              const partes = rango.split(":");
              return { from: partes[0], to: partes[1] };
          });

          // 3. Iniciar el calendario con las fechas bloqueadas
          iniciarCalendario(disabledDates);
      })
      .catch(err => console.error("Error cargando disponibilidad:", err));
});

function iniciarCalendario(fechasOcupadas) {
  flatpickr("#rangoFechas", {
      mode: "range",
      dateFormat: "Y-m-d",
      minDate: "today",
      disable: fechasOcupadas, // Bloquea los días ocupados
      locale: {
          firstDayOfWeek: 1 // Lunes como primer día
      },
      onChange: function(selectedDates, dateStr, instance) {
          // Cuando el usuario elige fecha, actualizamos los inputs ocultos para el formulario
          if (selectedDates.length === 2) {
              const entrada = instance.formatDate(selectedDates[0], "Y-m-d");
              const salida = instance.formatDate(selectedDates[1], "Y-m-d");
              
              document.getElementById("inputEntrada").value = entrada;
              document.getElementById("inputSalida").value = salida;
          }
      }
  });
}