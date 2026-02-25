package com.tt1.test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestsRepositorio {

	@Test
    void testCRUDyEmails() {
        DBStub db = new DBStub();
        Repositorio repo = new Repositorio(db);

        // guardarToDo y encontrarPorNombre
        ToDo t1 = new ToDo("t1", "descripcion", LocalDate.now().plusDays(1));
        repo.guardarToDo(t1);
        assertEquals(t1, repo.encontrarPorNombre("t1"));

        // encontrarToDos contiene el ToDo
        List<ToDo> todos = repo.encontrarToDos();
        assertTrue(todos.contains(t1), "La lista de ToDos debe contener t1");

        // marcarCompletado
        assertTrue(repo.marcarCompletado("t1"), "marcarCompletado debe devolver true si existe");
        assertTrue(repo.encontrarPorNombre("t1").isCompletado(), "t1 debe estar completado");

        // eliminarNombre
        assertTrue(repo.eliminarNombre("t1"), "eliminarNombre debe devolver true si existía");
        assertNull(repo.encontrarPorNombre("t1"), "t1 ya no debe encontrarse");

        // emails
        assertTrue(repo.guardarEmail("a@a.com"), "guardarEmail debe añadir el email");
        assertTrue(repo.listarEmails().contains("a@a.com"));
        // añadir de nuevo el mismo email debe devolver false (set)
        assertFalse(repo.guardarEmail("a@a.com"), "guardarEmail debe devolver false si ya existía");
    }

}
