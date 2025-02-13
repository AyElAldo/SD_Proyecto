package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> { }
