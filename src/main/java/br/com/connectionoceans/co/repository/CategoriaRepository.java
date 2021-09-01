package br.com.connectionoceans.co.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.connectionoceans.co.model.Categoria;


@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	public List <Categoria> findAllByEmbalagensContainingIgnoreCase (String embalagens);

}
