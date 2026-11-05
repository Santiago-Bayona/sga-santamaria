package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.application.dto.request.RegistroHuespedRequest;
import co.edu.uniquindio.sga.application.usecase.RegistrarTitularUseCase;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Registro público (7.2). Crea el titular del negocio con el caso de uso existente y el usuario de acceso
 * con rol HUESPED; el documento es el puente entre ambos (código del titular = "TIT-" + documento).
 * Los roles internos solo los asigna un administrador: aquí el rol es siempre HUESPED.
 * Vive en infraestructura porque el usuario es un concepto de acceso, no del dominio (3.6).
 */
@Service
public class RegistroHuespedService {

    private final UsuarioJpaRepository usuarios;
    private final PasswordHasherPort hasher;
    private final RegistrarTitularUseCase registrarTitular;

    public RegistroHuespedService(UsuarioJpaRepository usuarios, PasswordHasherPort hasher,
                                  RegistrarTitularUseCase registrarTitular) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.registrarTitular = registrarTitular;
    }

    @Transactional
    public UsuarioJpaEntity registrar(RegistroHuespedRequest request) {
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsById(correo)) {
            throw new UsuarioYaRegistradoException(correo);
        }
        // Si el documento ya tiene titular, el caso de uso lanza TitularYaRegistradoException (409).
        CodigoTitular codigo = new CodigoTitular("TIT-" + request.documento().trim());
        registrarTitular.ejecutar(codigo, request.nombre().trim(), request.fechaNacimiento(),
                request.telefono().trim(), correo);

        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsername(correo);
        usuario.setPasswordHash(hasher.cifrar(request.contrasena()));
        usuario.setRol(RolUsuario.HUESPED);
        usuario.setActivo(true);
        usuario.setNombreCompleto(request.nombre().trim());
        usuario.setCodigoTitular(codigo.valor());
        return usuarios.save(usuario);
    }
}
