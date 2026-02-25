package com.tt1.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestsServicio {

    private Servicio servicio;
    // stubs 
    private static class RepoStub implements InterfazRepToDo {
        AtomicReference<ToDo> saved = new AtomicReference<>();
        List<ToDo> todos = new ArrayList<>();
        List<String> emails = new ArrayList<>();

        @Override
        public ToDo guardarToDo(ToDo todo) {
            saved.set(todo);
            todos.add(todo);
            return todo;
        }

        @Override
        public ToDo encontrarPorNombre(String nombre) {
            return todos.stream().filter(t -> t.getNombre().equals(nombre)).findFirst().orElse(null);
        }

        @Override
        public List<ToDo> encontrarToDos() {
            return new ArrayList<>(todos);
        }

        @Override
        public boolean marcarCompletado(String nombre) {
            ToDo t = encontrarPorNombre(nombre);
            if (t == null) return false;
            t.setCompletado(true);
            return true;
        }

        @Override
        public boolean eliminarNombre(String nombre) {
            return todos.removeIf(t -> t.getNombre().equals(nombre));
        }

        @Override
        public boolean guardarEmail(String email) {
            if (emails.contains(email)) return false;
            emails.add(email);
            return true;
        }

        @Override
        public List<String> listarEmails() {
            return new ArrayList<>(emails);
        }
    }

    private static class MailerStub implements InterfazMailerStub {
        int sendCount = 0;
        List<String> destinations = new ArrayList<>();
        List<String> messages = new ArrayList<>();

        @Override
        public boolean enviar(String email, String mensaje) {
            sendCount++;
            destinations.add(email);
            messages.add(mensaje);
            return true;
        }
    }

    private RepoStub repo;
    private MailerStub mailer;

    @BeforeEach
    void setUp() {
        repo = new RepoStub();
        mailer = new MailerStub();
        servicio = new Servicio(repo, mailer);
    }

    @Test
    void crearToDo_valido_llamaGuardarY_noEnvíaSiNoHayVencidos() {
        servicio.crearToDo("tareaFutura", "desc", LocalDate.now().plusDays(5));
        assertNotNull(repo.saved.get(), "El repo debe haber recibido guardarToDo");
        assertEquals("tareaFutura", repo.saved.get().getNombre());
        assertEquals(0, mailer.sendCount, "No debe enviar emails si no hay vencidos");
    }

    @Test
    void crearToDo_vencido_envíaAlertasAEmailsGuardados() {
        // añadimos un email en repo (simulando que ya existían)
        repo.guardarEmail("x@x.com");
        // crear un ToDo vencido
        servicio.crearToDo("tareaVencida", "desc", LocalDate.now().minusDays(1));
        // checkAndSendAlerts se ejecuta tras crearToDo: debe enviar 1 email
        assertEquals(1, mailer.sendCount, "Debe haber enviado 1 email de alerta");
        assertTrue(mailer.destinations.contains("x@x.com"));
        assertTrue(mailer.messages.get(0).contains("tareaVencida"));
    }

    @Test
    void crearToDo_nombreNulo_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.crearToDo(null, "desc", LocalDate.now());
        });
    }

    @Test
    void aniadirEmail_invalido_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.aniadirEmail("no-valido");
        });
    }

    @Test
    void marcarCompletado_inexistente_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.marcarCompletado("no-existe");
        });
    }
}