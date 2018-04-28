package com.practica2.impuesto.retencion.web;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.practica2.impuesto.retencion.model.Trabajador;
import com.practica2.impuesto.retencion.repository.TrabajadorRepository;

@Controller
public class TrabajadorController {

	private double[] tasas = { 0.08, 0.14, 0.17, 0.2, 0.3 };

	@Autowired
	private TrabajadorRepository trabajadorRepository;

	public Trabajador LogicaNegocio(Trabajador trabajador) {
		double impuestoTotal = 0;
		double[] monto = { 0, 0, 0, 0, 0 };
		double[] impuestoCalculado = { 0, 0, 0, 0, 0 };
		String tipoTrabajador = trabajador.getTipoTrabajador();
		double salarioMensual = trabajador.getSalario();
		double uit = trabajador.getUit();

		double salarioAnual = 0;

		if (tipoTrabajador.equalsIgnoreCase("Independiente")) {
			salarioAnual = salarioMensual * 12;
		} else {
			salarioAnual = salarioMensual * 14;
		}

		trabajador.setTotalBruto(Math.floor(salarioAnual*100)/100);

		double descuentouit = trabajador.getUit()*7;

		double renta_neta = salarioAnual - descuentouit;

		double[] montos_maximos = { 5 * uit, 20 * uit, 35 * uit, 45 * uit };

		if (renta_neta > 0) {
			if (renta_neta <= montos_maximos[0]) {
				monto[0] = renta_neta;
				impuestoCalculado[0] = monto[0] * tasas[0];
			} else {
				monto[0] = montos_maximos[0];
				impuestoCalculado[0] = monto[0] * tasas[0];
			}

			if (renta_neta <= montos_maximos[1]) {
				monto[1] = renta_neta - monto[0];
				impuestoCalculado[1] = monto[1] * tasas[1];
			} else {
				monto[1] = montos_maximos[1] - monto[0];
				impuestoCalculado[1] = monto[1] * tasas[1];
			}

			if (renta_neta <= montos_maximos[2]) {
				monto[2] = renta_neta - (monto[0] + monto[1]);
				impuestoCalculado[2] = monto[2] * tasas[2];
			} else {
				monto[2] = montos_maximos[2] - (monto[0] + monto[1]);
				impuestoCalculado[2] = monto[2] * tasas[2];
			}

			if (renta_neta <= montos_maximos[3]) {
				monto[3] = renta_neta - (monto[0] + monto[1] + monto[2]);
				impuestoCalculado[3] = monto[3] * tasas[3];
			} else {
				monto[3] = montos_maximos[3] - (monto[0] + monto[1] + monto[2]);
				impuestoCalculado[3] = monto[3] * tasas[3];
			}

			if (renta_neta > montos_maximos[3]) {
				monto[4] = renta_neta - montos_maximos[3];
				impuestoCalculado[4] = monto[4] * tasas[4];
			} else {
				monto[4] = 0;
				impuestoCalculado[4] = monto[4] * tasas[4];
			}
		}

		impuestoTotal = CalcularImpuestoTotal(impuestoCalculado);

		trabajador.setTotalImpuesto(Math.floor(impuestoTotal*100)/100);

		return trabajador;
	}

	public double CalcularImpuestoTotal(double[] impuesto) {
		double impuestoTotal = 0;
		for (int i = 0; i < impuesto.length; i++) {
			impuestoTotal += impuesto[i];
		}
		return impuestoTotal;
	}


	@GetMapping("/trabajador/Impuesto")
	private String initForm(Model model) {

		model.addAttribute(new Trabajador());
		return "formulario";
	}

	@PostMapping("/trabajador/Impuesto")
	private String submitForm(@Valid Trabajador trabajador, BindingResult bindingResult) {

		if (bindingResult.hasFieldErrors()) {
			return "formulario";
		}

		Trabajador _trabajador = LogicaNegocio(trabajador);

		trabajadorRepository.save(_trabajador);

		return "redirect:/trabajador/lista";
	}

	@GetMapping("/trabajador/lista")
	private String listado(Map<String, Object> model) {

		List<Trabajador> trabajadores = trabajadorRepository.findAll();
		model.put("trabajadores", trabajadores);
		return "listTrabajador";
	}
}
