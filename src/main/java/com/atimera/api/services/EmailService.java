package com.atimera.api.services;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface EmailService {

    /**
     * Envoie un mail contenant son mdp généré à l'utilisateur qui vient de s'inscrire
     *
     * @param firstName Le prénom de l'utilisateur
     * @param password  le mdp généré en clair
     * @param email     l'email de l'utiliseur où on envoie le mail.
     * @throws MessagingException possible exception TODO à gérer
     */
    void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException;

}
