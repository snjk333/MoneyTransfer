package com.oleksandr.moneytransfer.repository;

import com.oleksandr.moneytransfer.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
//    Optional<Wallet> findByIdWithLock(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.number = :number")
    Optional<Wallet> findByNumberWithLock(String number);

    boolean existsByNumber(String number);

}
