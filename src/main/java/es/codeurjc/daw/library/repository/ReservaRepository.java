package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Reserva;
import es.codeurjc.daw.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Busca la reserva ABIERTA (el carrito actual) de un usuario específico
    Optional<Reserva> findByUserAndCerradaFalse(User user);
}