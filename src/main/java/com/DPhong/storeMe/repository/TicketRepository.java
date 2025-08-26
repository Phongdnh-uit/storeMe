package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.support.Ticket;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends SimpleRepository<Ticket, Long> {}
