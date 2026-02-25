package com.tt1.test;

public class MailerStub implements InterfazMailerStub {

    @Override
    public boolean enviar(String email, String mensaje) {
    	System.out.println("Envío en proceso");
        System.out.println("A: " + email);
        System.out.println("Con el mensaje: ");
        System.out.println(mensaje);
        return true;
    }
}