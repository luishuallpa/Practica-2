package com.practica2.impuesto.retencion.repository;

import java.util.List;
import org.springframework.data.repository.Repository;
import com.practica2.impuesto.retencion.model.Trabajador;

public interface TrabajadorRepository extends Repository<Trabajador, Integer> {
	void save(Trabajador model);

	List<Trabajador> findAll();
}
