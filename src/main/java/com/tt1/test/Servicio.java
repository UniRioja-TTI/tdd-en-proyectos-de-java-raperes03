package com.tt1.test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Servicio {

    private InterfazRepToDo repo;
    private InterfazMailerStub mailer;

    public Servicio(InterfazRepToDo repo, InterfazMailerStub mailer) {
        this.repo = Objects.requireNonNull(repo);
        this.mailer = Objects.requireNonNull(mailer);
    }

    public void crearToDo(String nombre, String descripcion, LocalDate fechaLimite) {
    	if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre vacío");
        }

        if (fechaLimite == null) {
            throw new IllegalArgumentException("Fecha límite nula");
        }

        ToDo todo = new ToDo(nombre.trim(), descripcion, fechaLimite);
        repo.guardarToDo(todo);

        comprobarYMandarAlertas();
    }

    public void marcarCompletado(String nombre) {
    	if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre inválido");
        }

        boolean ok = repo.marcarCompletado(nombre.trim());

        if (!ok) {
            throw new IllegalArgumentException("No existe la tarea");
        }

        comprobarYMandarAlertas();
    }

    public List<ToDo> listarPendientes() {
    	
    	comprobarYMandarAlertas();

        return repo.encontrarToDos().stream()
                .filter(t -> !t.isCompletado())
                .collect(Collectors.toList());
    }

    public void aniadirEmail(String email) {
    	
    	 if (email == null || email.trim().isEmpty()) {
             throw new IllegalArgumentException("Email vacío");
         }

         String e = email.trim();

         if (!e.contains("@") || !e.contains(".")) {
             throw new IllegalArgumentException("Email no válido");
         }

         repo.guardarEmail(e);

         comprobarYMandarAlertas();
    }
    
    public void comprobarYMandarAlertas() {
    	List<ToDo> vencidos = repo.encontrarToDos().stream()
                .filter(t -> !t.isCompletado()
                        && t.getFechaLimite() != null
                        && t.getFechaLimite().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        if (vencidos.isEmpty()) {
            return;
        }

        String mensaje = "Hay tareas vencidas: \n";

        for (ToDo t : vencidos) {
            mensaje += "- " + t.getNombre() + " (límite: " + t.getFechaLimite() + ")\n";
        }

        List<String> emails = repo.listarEmails();

        for (String email : emails) {
            mailer.enviar(email, mensaje);
    }
}
}