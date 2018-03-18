package io.toro.pairprogramming.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import io.toro.pairprogramming.models.WorkShift;

public interface WorkShiftRepository extends JpaRepository<WorkShift,Long> {
}
