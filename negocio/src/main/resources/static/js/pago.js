document.addEventListener("DOMContentLoaded", () => {

  // --- REFERENCIAS A ELEMENTOS DEL DOM ---
  // Botones de selección (Asegúrate que los IDs coinciden con el HTML)
  const btnTarjeta = document.getElementById("btnSelectTarjeta");
  const btnPayPal = document.getElementById("btnSelectPayPal");

  // Formularios
  const formTarjeta = document.getElementById("formTarjeta");
  const formPayPal = document.getElementById("formPayPal");

  // --- LÓGICA DE CAMBIO DE PESTAÑA ---
  function cambiarMetodo(tipo) {
    if (tipo === "tarjeta") {
      // Mostrar Tarjeta
      formTarjeta.classList.add("active");
      formPayPal.classList.remove("active");

      // Estilo Botones
      btnTarjeta.classList.add("active");
      btnPayPal.classList.remove("active");

    } else if (tipo === "paypal") {
      // Mostrar PayPal
      formPayPal.classList.add("active");
      formTarjeta.classList.remove("active");

      // Estilo Botones
      btnPayPal.classList.add("active");
      btnTarjeta.classList.remove("active");
    }
  }

  // --- EVENT LISTENERS ---
  // Si usas este JS, puedes quitar los 'onclick="..."' del HTML
  if (btnTarjeta && btnPayPal) {
    btnTarjeta.addEventListener("click", () => cambiarMetodo("tarjeta"));
    btnPayPal.addEventListener("click", () => cambiarMetodo("paypal"));
  }

  // NOTA:
  // Hemos borrado la parte de 'localStorage' porque Thymeleaf ya escribe
  // el nombre y el precio directamente en el HTML.
  // También hemos borrado los eventos de "Enviar simulado" porque ahora
  // los botones son type="submit" y envían el formulario real al servidor.
});