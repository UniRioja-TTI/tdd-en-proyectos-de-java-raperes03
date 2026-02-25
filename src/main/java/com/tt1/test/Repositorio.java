package com.tt1.test;

import java.util.ArrayList;
import java.util.List;

public class Repositorio implements InterfazRepToDo {
	
	private DBStub db;
	
	public Repositorio(DBStub db) {
		this.db = db;
	}

	@Override
	public ToDo guardarToDo(ToDo todo) {
		if (todo == null || todo.getNombre() == null) {
            throw new IllegalArgumentException("ToDo o nombre nulo");
        }
        db.getTodos().put(todo.getNombre(), todo);
        return todo;
	}

	@Override
	public ToDo encontrarPorNombre(String nombre) {
		if (nombre == null) return null;
        return db.getTodos().get(nombre);
	}

	@Override
	public List<ToDo> encontrarToDos() {
		return new ArrayList<>(db.getTodos().values());
	}

	@Override
	public boolean marcarCompletado(String nombre) {
		ToDo t = db.getTodos().get(nombre);
        if (t == null) return false;
        t.setCompletado(true);
        db.getTodos().put(nombre, t);
        return true;
	}

	@Override
	public boolean eliminarNombre(String nombre) {
		return db.getTodos().remove(nombre) != null;
	}

	@Override
	public boolean guardarEmail(String email) {
		if (email == null) return false;
        return db.getEmails().add(email);
	}

	@Override
	public List<String> listarEmails() {
		 return new ArrayList<>(db.getEmails());
	}

}