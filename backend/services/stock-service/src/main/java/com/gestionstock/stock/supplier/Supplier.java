package com.gestionstock.stock.supplier;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplier")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String name;

    /** Numero d'identification de l'entreprise (ex-champ cnpj du modele d'origine). */
    @Column(name = "registration_number")
    private String registrationNumber;

    private String phone;

    private String email;
}
