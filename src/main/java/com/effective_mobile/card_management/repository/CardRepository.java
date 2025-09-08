package com.effective_mobile.card_management.repository;


import com.effective_mobile.card_management.entity.Card;
import com.effective_mobile.card_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByUser(User user, Pageable pageable);

    List<Card> findByUser(User user);
}
