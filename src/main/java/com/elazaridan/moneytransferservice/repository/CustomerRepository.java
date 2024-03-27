package com.elazaridan.moneytransferservice.repository;

import com.elazaridan.moneytransferservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Transactional
    @Query("SELECT c.balance FROM Customer c WHERE c.id= :recipientId")
    Double findBalanceById(@Param("recipientId") Long recipientId);

}
