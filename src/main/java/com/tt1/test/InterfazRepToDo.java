package com.tt1.test;

import java.util.List;


public interface InterfazRepToDo {
    ToDo guardarToDo(ToDo todo);                
    ToDo encontrarPorNombre(String nombre);
    List<ToDo> encontrarToDos();
    boolean marcarCompletado(String nombre);    
    boolean eliminarNombre(String nombre);        
    boolean guardarEmail(String email);
    List<String> listarEmails();
}