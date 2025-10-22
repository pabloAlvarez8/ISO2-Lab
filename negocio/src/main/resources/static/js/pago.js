document.addEventListener("DOMContentLoaded", () => {
  // Botones
  const btnTarjeta = document.getElementById("btnTarjeta");
  const btnPayPal = document.getElementById("btnPayPal");

  // Formularios
  const formTarjeta = document.getElementById("formTarjeta");
  const formPayPal = document.getElementById("formPayPal");

  // Mensaje
  const mensaje = document.getElementById("mensaje");

  // Mostrar info del alojamiento desde localStorage
  const reserva = JSON.parse(localStorage.getItem("reservaSeleccionada"));
  if (reserva) {
    document.getElementById("nombreAlojamiento").textContent = `🏠 ${reserva.title}`;
    document.getElementById("precioAlojamiento").textContent = `💰 ${reserva.price} € / noche`;
  } else {
    document.getElementById("resumen-reserva").innerHTML = "<p>No se encontró información de la reserva.</p>";
  }

  // Función para mostrar un formulario y ocultar el otro
  function mostrarFormulario(tipo) {
    if (tipo === "tarjeta") {
      formTarjeta.classList.add("active");
      formPayPal.classList.remove("active");
      btnTarjeta.classList.add("active");
      btnPayPal.classList.remove("active");
      mensaje.textContent = "";
    } else if (tipo === "paypal") {
      formPayPal.classList.add("active");
      formTarjeta.classList.remove("active");
      btnPayPal.classList.add("active");
      btnTarjeta.classList.remove("active");
      mensaje.textContent = "";
    }
  }

  // Eventos botones
  btnTarjeta.addEventListener("click", () => mostrarFormulario("tarjeta"));
  btnPayPal.addEventListener("click", () => mostrarFormulario("paypal"));

  // Aquí se pueden poner los eventos de envío si quieres simular pago
  document.getElementById("btnEnviarTarjeta").addEventListener("click", () => {
    mensaje.textContent = "✅ Formulario de tarjeta enviado (simulado)";
  });

  document.getElementById("btnEnviarPayPal").addEventListener("click", () => {
    mensaje.textContent = "✅ Formulario de PayPal enviado (simulado)";
  });

  // Recuperar la reserva seleccionada
const selected = JSON.parse(localStorage.getItem("reservaSeleccionada"));

if (selected) {
  // Mostrar datos
  document.getElementById("nombreAlojamiento").textContent = selected.title;
  document.getElementById("precioAlojamiento").textContent = selected.price + " € / noche";

  // Imagen (misma que detalleAlojamiento)
  document.getElementById("fotoAlojamiento").src = selected.images ? selected.images[0] : selected.img;
}

});
