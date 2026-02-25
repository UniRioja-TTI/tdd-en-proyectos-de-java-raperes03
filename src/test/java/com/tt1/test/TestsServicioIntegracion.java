package com.tt1.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestsServicioIntegracion {

    private DBStub db;
    private Repositorio repo;
    private TestMailer mailer;
    private Servicio servicio;

    // mailer de prueba que registra envíos
    static class TestMailer implements InterfazMailerStub {
        int sendCount = 0;
        String lastDest = null;
        String lastMessage = null;

        @Override
        public boolean enviar(String email, String mensaje) {
            sendCount++;
            lastDest = email;
            lastMessage = mensaje;
            return true;
        }
    }

    @BeforeEach
    void setup() {
        db = new DBStub();
        repo = new Repositorio(db);
        mailer = new TestMailer();
        servicio = new Servicio(repo, mailer);
    }

    @Test
    void integración_crearToDo_y_comprobarAlertas() {
        // añadimos un email a través del servicio
        servicio.aniadirEmail("integ@test.com");
        // creamos un ToDo vencido -> debe enviarse alerta
        servicio.crearToDo("integVencida", "desc", LocalDate.now().minusDays(2));
        assertEquals(1, mailer.sendCount);
        assertEquals("integ@test.com", mailer.lastDest);
        assertTrue(mailer.lastMessage.contains("integVencida"));
    }

    @Test
    void integración_marcarCompletado_y_listarPendientes() {
        servicio.crearToDo("t1", "d", LocalDate.now().plusDays(1));
        servicio.crearToDo("t2", "d", LocalDate.now().plusDays(1));
        // marcar t1 completada
        servicio.marcarCompletado("t1");
        List<ToDo> pendientes = servicio.listarPendientes();
        assertTrue(pendientes.stream().noneMatch(t -> "t1".equals(t.getNombre())));
        assertTrue(pendientes.stream().anyMatch(t -> "t2".equals(t.getNombre())));
    }
}