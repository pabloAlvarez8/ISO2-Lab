document.addEventListener("DOMContentLoaded", () => {
  
    // 1. Obtener ID del alojamiento
    const alojamientoIdInput = document.getElementById("alojamientoIdHidden");
    if (!alojamientoIdInput) return;
  
    const alojamientoId = alojamientoIdInput.value;
  
    // 2. Llamar API y configurar calendario
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
  
    // --- LÓGICA DE ESTRELLAS (CON SEGURIDAD) ---
    const starWidget = document.getElementById("starWidget");
    const starsGold = document.getElementById("starsGold");
    const hiddenInput = document.getElementById("puntuacionInput");
    const valorVisual = document.getElementById("valorVisual");
  
    if (starWidget && starsGold && hiddenInput) {
        
        function calcularPuntuacion(e) {
            const rect = starWidget.getBoundingClientRect();
            const paddingLeft = 15; 
            let x = e.clientX - rect.left - paddingLeft;
            const widthStars = rect.width - paddingLeft;
            if (x < 0) return 0;
            let percent = x / widthStars;
            if (percent > 1) percent = 1;
            let rawScore = percent * 5;
            let score = Math.ceil(rawScore * 2) / 2;
            if (score < 0) score = 0;
            if (score > 5) score = 5;
            return score;
        }
  
        function pintarEstrellas(score) {
            const porcentaje = (score / 5) * 100;
            starsGold.style.width = `${porcentaje}%`;
            if (valorVisual) valorVisual.innerText = score.toFixed(1);
        }
  
        starWidget.addEventListener("mousemove", (e) => {
            pintarEstrellas(calcularPuntuacion(e));
        });
        starWidget.addEventListener("mouseleave", () => {
            const valorGuardado = parseFloat(hiddenInput.value) || 0;
            pintarEstrellas(valorGuardado);
        });
        starWidget.addEventListener("click", (e) => {
            const score = calcularPuntuacion(e);
            hiddenInput.value = score;
            pintarEstrellas(score);
            starsGold.style.opacity = "0.6";
            setTimeout(() => starsGold.style.opacity = "1", 150);
        });
        
        pintarEstrellas(parseFloat(hiddenInput.value));
    }
  
    // --- FORMULARIO VALORACIÓN ---
    const formValoracion = document.getElementById("formValoracion");
    if (formValoracion) {
        formValoracion.addEventListener("submit", function(e) {
            e.preventDefault();
            const usuarioIdInput = document.getElementById("usuarioLogueadoId");
            if (!usuarioIdInput) { alert("Error usuario"); return; }
            
            const usuarioId = usuarioIdInput.value;
            const puntuacion = document.getElementById("puntuacionInput").value;
            const comentario = document.getElementById("comentarioInput").value;
    
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
                if (response.status === 403) return response.json().then(err => { throw new Error(err.mensaje); });
                if (!response.ok) throw new Error("Error al procesar.");
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
            .catch(err => { console.error(err); alert(err.message); });
        });
    }
});
  
// --- FUNCIÓN CALENDARIO ---
function iniciarCalendario(fechasOcupadas) {

    // NUEVA COMPROBACIÓN DE SEGURIDAD
    // Si no existe el input (porque no estamos logueados), salimos sin hacer nada.
    const inputLlegada = document.getElementById("fechasLlegada");
    if (!inputLlegada) return; 
    
    // 1. Obtener precio
    const precioElement = document.getElementById("precioPorNocheData");
    let precioPorNoche = 0;
    if (precioElement) {
        let rawPrecio = precioElement.getAttribute("data-precio");
        if (rawPrecio) {
            rawPrecio = rawPrecio.toString().replace(',', '.');
            precioPorNoche = parseFloat(rawPrecio);
        }
    }
    if (isNaN(precioPorNoche)) precioPorNoche = 0;

    // 2. Elementos
    const desgloseBox = document.getElementById("desglosePrecio");
    const textoCalculo = document.getElementById("textoCalculo");
    const precioTotalSpan = document.getElementById("precioTotal");
    const precioTotalFinalSpan = document.getElementById("precioTotalFinal");
    
    const inputEntradaHidden = document.getElementById("inputEntradaHidden");
    const inputSalidaHidden = document.getElementById("inputSalidaHidden");

    // 3. Inicializar Flatpickr
    flatpickr("#fechasLlegada", {
        mode: "range",
        dateFormat: "Y-m-d",
        minDate: "today",
        disable: fechasOcupadas,
        locale: { firstDayOfWeek: 1 },
        plugins: [new rangePlugin({ input: "#fechasSalida" })], 
        
        onChange: function(selectedDates, dateStr, instance) {
            
            if (selectedDates.length === 2) {
                const entrada = instance.formatDate(selectedDates[0], "Y-m-d");
                const salida = instance.formatDate(selectedDates[1], "Y-m-d");

                if (inputEntradaHidden) inputEntradaHidden.value = entrada;
                if (inputSalidaHidden) inputSalidaHidden.value = salida;

                const diffTime = Math.abs(selectedDates[1] - selectedDates[0]);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 

                if (diffDays > 0) {
                    const total = (diffDays * precioPorNoche).toFixed(2); 

                    if (desgloseBox) {
                        desgloseBox.style.display = "block";
                        textoCalculo.innerText = `${precioPorNoche} € x ${diffDays} noches`;
                        precioTotalSpan.innerText = `${total} €`;
                        precioTotalFinalSpan.innerText = `${total} €`;
                    }
                }
            } else {
                if (desgloseBox) desgloseBox.style.display = "none";
                if (inputEntradaHidden) inputEntradaHidden.value = "";
                if (inputSalidaHidden) inputSalidaHidden.value = "";
            }
        }
    });
}