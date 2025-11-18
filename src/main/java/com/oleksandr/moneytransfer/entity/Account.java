package com.oleksandr.moneytransfer.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "accounts") // lowercase в базе данных - хорошая практика
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

@EqualsAndHashCode(callSuper = true, exclude = "wallets")
@ToString(exclude = "wallets")

public class Account extends BaseEntity{
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Wallet> wallets = new ArrayList<>();
}
